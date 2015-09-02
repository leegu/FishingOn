package com.go.fish.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.go.fish.R;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FNewsData;
import com.go.fish.data.MyListitemData;
import com.go.fish.data.FPlaceData;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.NetTool;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainView";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = 0;
        int id = getId();
        Bundle b = getArguments();
        if(b != null && b.getInt(Const.LAYOUT_ID) == R.layout.ui_f_fplace){
        	id = R.id.home_fishing_place;
        }
        View view = null;
        switch (id) {
		case R.id.home_fishing_place:
			layoutId = R.layout.ui_f_fplace;
			view = inflater.inflate(layoutId,container,false);
			onCreateFPlaceView((ViewGroup) view);
			break;
		case R.id.home_care:
			layoutId = R.layout.ui_f_care;
			view = inflater.inflate(layoutId,container,false);
			onCreateCareView((ViewGroup)view);
			break;
		case R.id.home_appear:
			layoutId = R.layout.ui_f_appear;
			view = inflater.inflate(layoutId,container,false);
			onCreateAppearView((ViewGroup)view);
			break;
		case R.id.home_fishing_news:
//			layoutId = R.layout.ui_f_fishing_news;
			layoutId = R.layout.ui_fnews;
			view = inflater.inflate(layoutId,container,false);
			onCreateFNewsView((ViewGroup) view);
			break;
		case R.id.home_my:
			layoutId = R.layout.ui_my;
			view = inflater.inflate(layoutId,container,false);
			onCreateMyView((ViewGroup) view);
			break;
		default:
			break;
		}
        return view;
    }

	private void onCreateFPlaceView(ViewGroup view) {

	}

	private void onCreateFNewsView(ViewGroup view) {
		ViewGroup vg = (ViewGroup)view.findViewById(R.id.ui_fnews_list_root);
		{//最新钓播
			final ListView fNewsList = new ListView(getActivity());
			fNewsList.setVisibility(View.GONE);
			vg.addView(fNewsList);
			//本地先获取显示
			String careFPlace = LocalMgr.loadData(Const.DB_MY_CARE_FNEWS);
			ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
			JSONArray jsonArr = null;
			try {
				if(!TextUtils.isEmpty(careFPlace)){
					jsonArr = new JSONArray(careFPlace);
				}else{
					jsonArr = new JSONArray();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//friend界面此时最接近
			final AdapterExt mListAdapter = AdapterExt.newInstance(getActivity(), jsonArr, R.layout.listitem_fnews);
			fNewsList.setAdapter(mListAdapter);
			//网络数据抓取,进行更新
			NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					ViewHelper.showGlobalWaiting(getActivity(), null);
				}

				@Override
				public void onEnd(byte[] data) {
					try {
						String str = new String(data,"utf-8");
						JSONArray arr= new JSONArray(str);
						ArrayList<IBaseData> arrBaseData = new ArrayList<IBaseData>();
						arrBaseData.add(FNewsData.newInstance(""));
						arrBaseData.add(FNewsData.newInstance(""));
						mListAdapter.updateAdapter(arrBaseData);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						ViewHelper.closeGlobalWaiting();
					}
				}
			}, "my_friend");
			NetTool.httpGet(rd.syncCallback());
		}
		{//my钓播
			ListView fPlaceList = new ListView(getActivity());
			vg.addView(fPlaceList);
			//本地先获取显示
			String careFPlace = LocalMgr.loadData(Const.DB_MY_CARE_FPLACE);
			JSONArray jsonArr = null;
			try {
				if(!TextUtils.isEmpty(careFPlace)){
					jsonArr = new JSONArray(careFPlace);
				}else{
					jsonArr = new JSONArray();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(R.layout.listitem_fnews, jsonArr);
			final FPlaceListAdapter mListAdapter = new FPlaceListAdapter(getActivity(), fPlaceArr);
			fPlaceList.setAdapter(mListAdapter);
			//网络数据抓取,进行更新
			NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
				@Override
				public void onStart() {
				}

				@Override
				public void onEnd(byte[] data) {
					try {
						String str = new String(data,"utf-8");
						mListAdapter.updateAdapter(DataMgr.makeFPlaceDatas(R.layout.listitem_fnews, new JSONArray(str).getJSONArray(0)));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, "fishing_place_near");
			NetTool.httpGet(rd.syncCallback());
		}
	}

	private void onCreateMyView(ViewGroup view) {
		{
			final ListView list1 = (ListView)view.findViewById(R.id.ui_f_my_listview1);
			MyListitemAdapter.fillToListView(list1,R.array.hmy_listview1, R.array.hmy_listview1_icons);
			MessageHandler.sendMessage(new MessageHandler.MessageListener() {
				@Override
				public MessageHandler.MessageListener init(Object args) {
					return this;
				}

				@Override
				public void onExecute() {
					MyListitemAdapter listitemAdapter = (MyListitemAdapter) list1.getAdapter();
					((MyListitemData) listitemAdapter.getItem(1)).bedgerNumber = 45;
					((MyListitemData) listitemAdapter.getItem(1)).subLabel = "钓场~播况~GO";
				}
			});
			list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// 我钓播况 我的关注 附近钓场 附近钓友 扫一扫
					switch (position) {
						case 0:
							UIMgr.showActivity(getActivity(),R.layout.ui_my_f_news);
							break;
						case 1:
							UIMgr.showActivity(getActivity(),R.layout.ui_my_care);
							break;
						case 2:
							UIMgr.showActivity(getActivity(),R.layout.ui_near_fplace);
							break;
						case 3:
							UIMgr.showActivity(getActivity(),R.layout.ui_near_friends);
							break;
						case 4:
							UIMgr.showActivity(getActivity(),R.layout.ui_my_f_news);
							break;
					}
				}
			});
		}
		{
			ListView list2 = (ListView)view.findViewById(R.id.ui_f_my_listview2);
			MyListitemAdapter.fillToListView(list2, R.array.hmy_listview2, R.array.hmy_listview2_icons);
			list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					问题反馈  清空缓存  关于我们
					switch (position) {
						case 0:
							UIMgr.showActivity(getActivity(),R.layout.ui_comment_publish);
							break;
					}
				}
			});
		}
	}

	private void onCreateAppearView(ViewGroup vg){
    	String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
    	ViewGroup mainView = ViewHelper.newMainView(getActivity(), getChildFragmentManager(), new ResultForActivityCallback() {
			@Override
			public void onItemClick(View view, FPlaceData data) {
				
			}
		}, tabItemsTitle);
    	vg.addView(mainView);
    	ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.search_viewpager);
		BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_search);
    }
    
    private void onCreateCareView(ViewGroup vg){
    	String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
    	vg.addView(ViewHelper.newMainView(getActivity(), getChildFragmentManager(), new ResultForActivityCallback() {
			@Override
			public void onItemClick(View view, FPlaceData data) {
				
			}
		}, tabItemsTitle));
    }
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	Log.d(TAG, "TestFragment-----onPause");
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Log.d(TAG, "TestFragment-----onResume");
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
    	super.onHiddenChanged(hidden);
    	Log.d(TAG, "TestFragment-----" + hidden);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TestFragment-----onDestroy");
    }
 
}
