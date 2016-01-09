package com.go.fish.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.barcode.BarcodeUI;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FieldData;
import com.go.fish.data.PersonData;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UICode;
import com.go.fish.ui.UIMgr;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainView";
    /**fragment中的根view*/
    ViewGroup contentView;
    OnBaseDataClickListener mOnBaseDataClickListener = null;
    private int layoutId = 0;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if(getActivity() instanceof OnBaseDataClickListener){
//    		mOnBaseDataClickListener = (OnBaseDataClickListener)mOnBaseDataClickListener;
//    	}
//    }
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnBaseDataClickListener){
    		mOnBaseDataClickListener = (OnBaseDataClickListener)activity;
    	}
	}
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//    	super.onActivityCreated(savedInstanceState);
//    	
//    }
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
		case R.id.home_care://关注
			layoutId = R.layout.ui_f_care;
			view = inflater.inflate(layoutId,container,false);
			onCreateCareView((ViewGroup)view);
			break;
		case R.id.home_zixun:
			layoutId = R.layout.ui_f_zixun;
			view = inflater.inflate(layoutId,container,false);
			onCreateZixunView((ViewGroup) view);
			break;
		case R.id.home_fishing_news://钓播
//			layoutId = R.layout.ui_f_fishing_news;
			layoutId = R.layout.ui_podcast;
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

	private void onCreateFNewsView(ViewGroup view) {//创建钓播 页面
		ViewGroup vg = (ViewGroup)view.findViewById(R.id.ui_fnews_list_root);
		{//最新钓播
//			final ListView fNewsList = new ReFreshListView(getActivity());
			final ListView fNewsList = (ListView)vg.getChildAt(0);
			fNewsList.setTag("0");
			//本地先获取显示
			final AdapterExt mListAdapter = AdapterExt.newInstance(fNewsList, mOnBaseDataClickListener, new JSONArray(), R.layout.listitem_podcast);
			fNewsList.setAdapter(mListAdapter);
		}
		{//my钓播
//			ListView fPlaceList = new ReFreshListView(getActivity());
			final ListView fPlaceList = (ListView)vg.getChildAt(1);
			AdapterExt ae = AdapterExt.newInstance(fPlaceList,mOnBaseDataClickListener,new JSONArray(), R.layout.listitem_podcast );
			ae.mFlag = AdapterExt.FLAG_MY_NEWS;
			fPlaceList.setTag(User.self().userInfo.mobileNum);
			fPlaceList.setAdapter(ae);
		}
	}
	
	private void onShowFNews(){//显示钓播 
		ViewGroup vg = (ViewGroup)contentView.findViewById(R.id.ui_fnews_list_root);
		for(int i = 0;i < vg.getChildCount() ; i++){
			ListView fNListView = (ListView)vg.getChildAt(i);
			if(fNListView.getVisibility() == View.VISIBLE){
				PodCastHelper.getNetPodList(fNListView,String.valueOf(fNListView.getTag()), true);
			}
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
		case R.layout.ui_f_care:{//关注页面
			onShowCare();
//	    	ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.search_viewpager);
//			BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce, null, viewPager.getCurrentItem());
			break;
		}
		case R.layout.ui_f_zixun:{
			onShowZixun();
//			onCreateZixunView((ViewGroup)contentView.findViewById(R.id.ui_f_appear_list_root));
//			ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.ui_f_appear_list_root);
//			BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce);
			break;
		}
		case R.layout.ui_podcast:
			onShowFNews();
			break;
		case R.layout.ui_my:
			onShowMyView();
			break;
		default:
			break;
		}
	}
	public void onHide(){
		showStatus = false;
	}
	
	void onShowCare(){
		ListView fPlaceList = (ListView)getView().findViewById(R.id.ui_f_care_list);
//    	ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(R.layout.listitem_fpalce, new JSONArray());
//		final FPlaceListAdapter mListAdapter = new FPlaceListAdapter(getActivity(),fPlaceArr, FPlaceListAdapter.FLAG_CARE_RESULT);
//		mListAdapter.flag = FPlaceListAdapter.FLAG_CARE_RESULT;
//		fPlaceList.setAdapter(mListAdapter);
		// 网络数据抓取,进行更新
		FPlaceListAdapter adapter = (FPlaceListAdapter)fPlaceList.getAdapter();
		if(adapter.listDatas.size() == 0){
			BaseFragmentPagerAdapter.loadNetData(fPlaceList.getContext(),adapter , R.layout.listitem_field, "", fPlaceList.getAdapter().getCount(), LocalMgr.getFPlaceTypes());
		}
	}
	void onShowZixun(){
		final ListView lastNews = (ListView)getView().findViewById(R.id.last_news);
		final AdapterExt mListAdapter = (AdapterExt)lastNews.getAdapter(); 
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if(isRight(json)){
					JSONArray arr = json.optJSONArray(Const.STA_DATA);
					if(arr != null && arr.length() > 0) {
						mListAdapter.updateAdapter(arr);
					}
				}
			}
		}, jsonObject, UrlUtils.self().getPriceList());
	}
	void onShowMyView(){
//		updateMyView(getView());
		//更新关注数量，头像信息，昵称
		JSONObject jsonObject = new JSONObject();
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] bytes) {
				JSONObject response = toJSONObject(bytes);
				if (response != null ){
					if(isRight(response)) {
						JSONObject data = response.optJSONObject(Const.STA_DATA);
						User.self().userInfo = PersonData.newInstance(data.optJSONObject(Const.STA_MEMBER));
						updateMyView(getView());
					}
				}
			}
		},jsonObject,UrlUtils.self().getSettingData());
	}
	
	private void updateMyView(View view){
		{//更新用户名,头像，手机号
			TextView userPhoneNumber = (TextView)view.findViewById(R.id.userPhoneNumber);
			if(!TextUtils.isEmpty(User.self().userInfo.mobileNum)){
				userPhoneNumber.setText(BaseUtils.formatPhoneNum(User.self().userInfo.mobileNum));
			}
			
			TextView userName = (TextView)view.findViewById(R.id.userName);
			if(!TextUtils.isEmpty(User.self().userInfo.userName)){
				userName.setText(User.self().userInfo.userName);
			}else{
				userName.setText(User.self().userInfo.id);
			}
			
			if(!TextUtils.isEmpty(User.self().userInfo.photoUrl)){
				ImageView userIcon = (ImageView)view.findViewById(R.id.userIcon);
				ViewHelper.load(userIcon, UrlUtils.self().getNetUrl(User.self().userInfo.photoUrl), true,false);
			}
		}
	}
	private void onCreateMyView(ViewGroup view) {
		{
			updateMyView(view);
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
//					MyListitemAdapter listitemAdapter = (MyListitemAdapter) list1.getAdapter();
//					((MyListitemData) listitemAdapter.getItem(1)).bedgerNumber = 45;
//					((MyListitemData) listitemAdapter.getItem(1)).subLabel = "钓场~播况~GO";
//					((MyListitemData) listitemAdapter.getItem(0)).bedgerNumber = 15;
//					((MyListitemData) listitemAdapter.getItem(0)).subLabel = "gaga";
				}
			});
			list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// 我钓播况 我的关注 附近钓场 附近钓友 扫一扫
					switch (position) {
						case 0:{//我的播况
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_podcast_person);
							i.putExtra(Const.STA_MOBILE, User.self().userInfo.mobileNum);
							i.putExtra(Const.PRI_HIDE_CARE, true);
							UIMgr.showActivity(getActivity(),i,BaseUI.class.getName());
							break;
						}
						case 1://我的关注
							UIMgr.showActivity(getActivity(),R.layout.ui_my_care);
							break;
						case 2:{//附近钓场
							Bundle newBundle = new Bundle();
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_search_list);
							newBundle.putInt(Const.PRI_FPLACE_RESULT_TYPE, FPlaceListAdapter.FLAG_NEAR_RESULT);
							Intent intent = new Intent();
							intent.putExtras(newBundle);
							UIMgr.showActivity(getActivity(), intent, SearchUI.class.getName());
//							UIMgr.showActivity(getActivity(),R.layout.ui_near_fplace);
							break;
						}
						case 3://附近钓友
							UIMgr.showActivity(getActivity(),R.layout.ui_near_friends);
							break;
						case 4:{
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
//							i.putExtra(Const.PRI_TO_QR_CONTENT,"我是一个兵");
							i.setClassName(getActivity(), BarcodeUI.class.getName());
							UIMgr.showActivity(getActivity(),i,UICode.RequestCode.REQUEST_BARCODE);
							break;
						}
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
							UIMgr.showActivity(getActivity(),R.layout.ui_advice);
							break;
						case 1://清除缓存
							LocalMgr.self().clearCache();
							break;
						case 2:
							UIMgr.showActivity(getActivity(),R.layout.ui_about);
//							Intent i = new Intent();
//							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
//							i.putExtra(Const.PRI_TO_QR_CONTENT,"13245698756");
//							UIMgr.showActivity(getActivity(),i,BarcodeUI.class.getName());
							break;
					}
				}
			});
		}
	}

	private void onCreateZixunView(ViewGroup view){
		{//最新播况 撒鱼信息
			final ListView lastNews = (ListView)view.findViewById(R.id.last_news);
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
			final AdapterExt mListAdapter = AdapterExt.newInstance(lastNews, mOnBaseDataClickListener, jsonArr, R.layout.listitem_zixun);
			lastNews.setAdapter(mListAdapter);
		}
		{//最新活动
//			final ListView lastActivity = (ListView)view.findViewById(R.id.last_activity);
//			lastActivity.setVisibility(View.GONE);
//			//本地先获取显示
//			String careFPlace = LocalMgr.self().getString(Const.SIN_DB_MY_CARE_FPLACE);
//			JSONArray jsonArr = null;
//			try {
//				if(!TextUtils.isEmpty(careFPlace)){
//					jsonArr = new JSONArray(careFPlace);
//				}else{
//					jsonArr = new JSONArray();
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			final AdapterExt mListAdapter = AdapterExt.newInstance(lastActivity, jsonArr, R.layout.listitem_zixun);
//			
//			lastActivity.setAdapter(mListAdapter);
		}
    }
    
    private void onCreateCareView(ViewGroup vg){
    	//创建 关注 页面
    	ListView fPlaceList = (ListView)vg.findViewById(R.id.ui_f_care_list);
    	ArrayList<FieldData> fPlaceArr = DataMgr.makeFPlaceDatas(R.layout.listitem_field, new JSONArray());
    	FPlaceListAdapter adapter = FPlaceListAdapter.setAdapter(getActivity(),fPlaceList,fPlaceArr, FPlaceListAdapter.FLAG_CARE_RESULT);
    	adapter.setmResultForActivityCallback(new ResultForActivityCallback() {
			
			@Override
			public void onItemClick(View view, FieldData data) {
				String fPlaceId = data.sid;
				((HomeUI)getActivity()).showFieldDetail(fPlaceId, false);
			}
		});
    	adapter.isAttentionList = true;
		// 网络数据抓取,进行更新
//		HomeFragment.getNetPodList(fPlaceList, "0");
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
