package com.go.fish.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.go.fish.R;
import com.go.fish.barcode.BarcodeUI;
import com.go.fish.data.FPlaceData;
import com.go.fish.data.MyListitemData;
import com.go.fish.ui.HomeUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UICode;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainView";
    /**fragment中的根view*/
    ViewGroup contentView;
    private int layoutId = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int id = getId();
        Bundle b = getArguments();
        if(b != null && b.getInt(Const.PRI_LAYOUT_ID) == R.layout.ui_f_fplace){
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
        contentView = (ViewGroup)view;
        return view;
    }

	private void onCreateFPlaceView(ViewGroup view) {

	}

	private void onCreateFNewsView(ViewGroup view) {
		ViewGroup vg = (ViewGroup)view.findViewById(R.id.ui_fnews_list_root);
		{//最新钓播
//			final ListView fNewsList = new ReFreshListView(getActivity());
			final ListView fNewsList = new ListView(getActivity());
			vg.addView(fNewsList);
			//本地先获取显示
			String careFPlace = LocalMgr.self().getString(Const.SIN_DB_MY_CARE_FNEWS);
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
			final AdapterExt mListAdapter = AdapterExt.newInstance(fNewsList, jsonArr, R.layout.listitem_fnews);
			fNewsList.setAdapter(mListAdapter);
		}
		{//my钓播
//			ListView fPlaceList = new ReFreshListView(getActivity());
			ListView fPlaceList = new ListView(getActivity());
			fPlaceList.setVisibility(View.GONE);
			vg.addView(fPlaceList);
			//本地先获取显示
			String careFPlace = LocalMgr.self().getString(Const.SIN_DB_MY_CARE_FPLACE);
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
			fPlaceList.setAdapter(AdapterExt.newInstance(fPlaceList,jsonArr,R.layout.listitem_fnews ));
		}
	}
	
	private void onShowFNews(){
		ViewGroup vg = (ViewGroup)contentView.findViewById(R.id.ui_fnews_list_root);
		{
			final ListView fNListView = (ListView)vg.getChildAt(0);
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
						ListAdapter adapter = fNListView.getAdapter();
						if(adapter instanceof AdapterExt){
							((AdapterExt)adapter).updateAdapter(AdapterExt.makeFNewsDataArray(arr));
						}else if(adapter instanceof HeaderViewListAdapter){
							((AdapterExt)((HeaderViewListAdapter)adapter).getWrappedAdapter()).updateAdapter(AdapterExt.makeFNewsDataArray(arr));
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						ViewHelper.closeGlobalWaiting();
					}
				}
			}, "last_fnews");
			NetTool.data().http(rd.syncCallback());
		}
		{
			final ListView fNListView = (ListView)vg.getChildAt(1);
			//网络数据抓取,进行更新
			NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
				@Override
				public void onStart() {
				}

				@Override
				public void onEnd(byte[] data) {
					try {
						try {
							String str = new String(data,"utf-8");
							JSONArray arr= new JSONArray(str);
//							((AdapterExt)fNListView.getAdapter()).updateAdapter(AdapterExt.makeFNewsDataArray(arr));
							ListAdapter adapter = fNListView.getAdapter();
							if(adapter instanceof AdapterExt){
								((AdapterExt)adapter).updateAdapter(AdapterExt.makeFNewsDataArray(arr));
							}else if(adapter instanceof HeaderViewListAdapter){
								((AdapterExt)((HeaderViewListAdapter)adapter).getWrappedAdapter()).updateAdapter(AdapterExt.makeFNewsDataArray(arr));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}


					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} 
				}
			}, "my_fnews");
			NetTool.data().http(rd.syncCallback());
		}
	}

	boolean showStatus = false;
	public boolean isShowing(){
		return showStatus;
	}
	public void onShow(){
		showStatus = true;
		switch (layoutId) {
		case R.layout.ui_f_fplace:
			break;
		case R.layout.ui_f_care:{
	    	ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.search_viewpager);
			BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce);
			break;
		}
		case R.layout.ui_f_appear:{
			ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.search_viewpager);
			BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce);
			break;
		}
		case R.layout.ui_fnews:
			onShowFNews();
			break;
		case R.layout.ui_my:
			break;
		default:
			break;
		}
	}
	public void onHide(){
		showStatus = false;
	}
	private void onCreateMyView(ViewGroup view) {
		{
			final ListView list1 = (ListView)view.findViewById(R.id.ui_f_my_listview1);
			list1.setDividerHeight(0);
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
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
//							i.putExtra(Const.PRI_TO_QR_CONTENT,"我是一个兵");
							i.setClassName(getActivity(), BarcodeUI.class.getName());
							UIMgr.showActivity(getActivity(),i,UICode.RequestCode.REQUEST_BARCODE);
							break;
					}
				}
			});
		}
		{
			ListView list2 = (ListView)view.findViewById(R.id.ui_f_my_listview2);
			list2.setDividerHeight(0);
			MyListitemAdapter.fillToListView(list2, R.array.hmy_listview2, R.array.hmy_listview2_icons);
			list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					问题反馈  清空缓存  关于我们
					switch (position) {
						case 0://问题反馈
							UIMgr.showActivity(getActivity(),R.layout.ui_comment_publish);
							break;
						case 1://清除缓存
							LocalMgr.self().clearCache();
							break;
						case 2:
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
							i.putExtra(Const.PRI_TO_QR_CONTENT,"13245698756");
							UIMgr.showActivity(getActivity(),i,BarcodeUI.class.getName());
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
			public void onItemClick(View view, final FPlaceData fPlaceData) {
				RequestData rData = RequestData.newInstance(new RequestListener() {
					@Override
					public void onStart() {
						ViewHelper.showGlobalWaiting(getActivity(), null);
					}

					@Override
					public void onEnd(byte[] data) {
						try {
							String jsonStr = new String(data, "UTF-8");
							Bundle newBundle = new Bundle();
							String fPlaceId = fPlaceData.sid;;
							newBundle.putString(Const.STA_FISHING_PLACE_ID, fPlaceId);
							newBundle.putString(Const.STA_TEXT, fPlaceData.title);
							newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.search);
							newBundle.putInt(Const.PRI_EXTRA_LAYOUT_ID, R.layout.ui_f_search_item_detail);
							Intent i = new Intent();
							i.putExtras(newBundle);
							UIMgr.showActivity(getActivity(),i, SearchUI.class.getName());
							ViewHelper.closeGlobalWaiting();
						} catch (Exception e) {
						}
					}
				}, "fishing_place_" + fPlaceData.sid);
	            rData.putData(Const.STA_FISHING_PLACE_ID, fPlaceData.sid);
	            NetTool.data().http(rData.syncCallback());
			}
		}, tabItemsTitle);
    	vg.addView(mainView);
    }
    
    private void onCreateCareView(ViewGroup vg){
    	String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
    	vg.addView(ViewHelper.newMainView(getActivity(), getChildFragmentManager(), new ResultForActivityCallback() {
			@Override
			public void onItemClick(View view,final FPlaceData fPlaceData) {
				RequestData rData = RequestData.newInstance(new RequestListener() {
					@Override
					public void onStart() {
						ViewHelper.showGlobalWaiting(getActivity(), null);
					}

					@Override
					public void onEnd(byte[] data) {
						try {
							String jsonStr = new String(data, "UTF-8");
							Bundle newBundle = new Bundle();
							String fPlaceId = fPlaceData.sid;;
							newBundle.putString(Const.STA_FISHING_PLACE_ID, fPlaceId);
							newBundle.putString(Const.STA_TEXT, fPlaceData.title);
							newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.search);
							newBundle.putInt(Const.PRI_EXTRA_LAYOUT_ID, R.layout.ui_f_search_item_detail);
							Intent i = new Intent();
							i.putExtras(newBundle);
							UIMgr.showActivity(getActivity(),i, SearchUI.class.getName());
							ViewHelper.closeGlobalWaiting();
						} catch (Exception e) {
						}
					}
				}, "fishing_place_" + fPlaceData.sid);
	            rData.putData(Const.STA_FISHING_PLACE_ID, fPlaceData.sid);
	            NetTool.data().http(rData.syncCallback());
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
