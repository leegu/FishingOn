package com.go.fish.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroupOverlay;
import android.view.ViewStub;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.DistanceUtil;
import com.go.fish.R;
import com.go.fish.data.FieldData;
import com.go.fish.data.PersonData;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.LogUtils;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.HomeFragment;
import com.go.fish.view.IBaseData;
import com.go.fish.view.PodCastHelper;
import com.go.fish.view.PopWinListItemAdapter;
import com.go.fish.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomeUI extends FragmentActivity implements IHasHeadBar,OnBaseDataClickListener {

	FragmentManager fragmentMgr = null;
	HomeFragment currentFragment = null;
	int mFragmentIndex = 0;
	MapView mMapView;
	BaiduMap mBaiduMap;
//	ArrayList<Marker> mSearchResult = null;
	Marker mLastMarker = null;
	LocationData mUserLocationData = null;
	OverlayOptions mUserLocationOverlayOptions = null;
	/**标点集合*/
	ArrayList<OverlayOptions> mOverlayOptions = null;
	/**标点集合管理*/
	OverlayManager mOverlayManager= null;
	BitmapDescriptor mDefaultMarkerBD = BitmapDescriptorFactory.fromResource(R.drawable.point);
	BitmapDescriptor mDefaultMarkerBD_focus = BitmapDescriptorFactory.fromResource(R.drawable.point_);
	BitmapDescriptor mDefaultMarkerBD_care = BitmapDescriptorFactory.fromResource(R.drawable.point_care);
	BitmapDescriptor mDefaultMarkerBD_care_focus = BitmapDescriptorFactory.fromResource(R.drawable.point_care_);
	ViewGroup mFloatViewInfo = null;
	ViewGroup mHomeMainView = null;
	LayoutInflater mLayoutInflater = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(Const.PRI_LAYOUT_ID, 0);
		setContentView(layout_id);
		mLayoutInflater = LayoutInflater.from(this);
		mHomeMainView = (ViewGroup) findViewById(R.id.home_main_view);
		fragmentMgr = getSupportFragmentManager();
		initFirstTabItem();
	}

	final int TIME = 1500;
	long lastBackTime = -1;
	@Override
	public void onBackPressed() {
		if(lastBackTime == -1 || System.currentTimeMillis() - lastBackTime > TIME){
			lastBackTime = System.currentTimeMillis();
			ViewHelper.showToast(this, Const.DEFT_AGAIN_EXIT);
		}else{
			super.onBackPressed();
		}
	}
	void initFirstTabItem() {
		{// 设置foot item 获取焦点状态
			int i = 0;
			// showFragment(fragmentIds[i], footItemIds[i]);
			View view = findViewById(footItemIds[i]);
			ImageView icon = (ImageView) ((ViewGroup) view).getChildAt(0);
			icon.setImageResource(footItemFocusIconIds[i]);
			TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
			text.setTextColor(getResources().getColor(
					R.color.foot_bar_focus_text_color));
		}
		{// 初始化地图
			ViewGroup vg = (ViewGroup) findViewById(R.id.fishing_place_bmap_view);
			mMapView = MapUtil.newMap(this);
			mBaiduMap = mMapView.getMap();
			mBaiduMap.setMyLocationEnabled(true);
//			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//					LocationMode.NORMAL, true, null));
			try {
				// mMapView.getChildAt(2).setVisibility(View.GONE);
				mMapView.getChildAt(1).setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			vg.addView(mMapView);

			LayoutInflater.from(this).inflate(R.layout.float_view_fplace, vg);
			// LayoutInflater.from(this).inflate(R.layout.float_view_mylocation_fplace,
			// vg);
			mFloatViewInfo = (ViewGroup) LayoutInflater.from(HomeUI.this)
					.inflate(R.layout.listitem_float_view_in_map, null);
			mFloatViewInfo.setVisibility(View.GONE);
			vg.addView(mFloatViewInfo, new LayoutParams(-1, -2));

			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					return showFloatView(marker);
				}
			});
			mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public boolean onMapPoiClick(MapPoi arg0) {
					return false;
				}
				@Override
				public void onMapClick(LatLng arg0) {
					mFloatViewInfo.setVisibility(View.GONE);
				}
			});
			mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
				@Override
				public void onTouch(MotionEvent arg0) {
				}
			});

			// {
			// MapStatus ms = new
			// MapStatus.Builder().overlook(-20).zoom(15).build();
			// BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
			// .compassEnabled(false).zoomControlsEnabled(false);
			// SupportMapFragment map = SupportMapFragment.newInstance(bo);
			// fragmentMgr.beginTransaction()
			// .add(R.id.fishing_place_bmap_view, map, "bmap")
			// .commit();
			// }
		}
		updateLocation();
	}

	/**
	 * 离开当前页面10秒则刷新位置
	 */
	final int spaceTime = 600000;
	/**
	 * 上一次定位时间
	 */
	long locationUpdateTime = System.currentTimeMillis();

	private void updateLocation() {
		if (mMapView == null
				&& System.currentTimeMillis() - locationUpdateTime < spaceTime)
			return;
		mMapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				onGetNearFPlace();
			}
		}, 500);
	}

	boolean justUplocation = false;
	long lastQueryMapTime = -1;
	final int TIME_QUERYMAP_INTERVAL = 10000;
	@Override
	protected void onResume() {
		super.onResume();
		if(lastQueryMapTime == -1 || System.currentTimeMillis() - lastQueryMapTime > TIME_QUERYMAP_INTERVAL){
			justUplocation = false;
		}
		if(mFragmentIndex == 0){//地图界面
			updateLocation();
		}/*else if(mFragmentIndex == 3){//钓播
			
		}*/
		if(currentFragment != null){
			currentFragment.onShow();
		}
		updateUserData();
	}

	private void updateUserData(){
		NetTool.data().http(new NetTool.RequestListener() {//刷新用户信息
			@Override
			public void onEnd(byte[] bytes) {
				JSONObject response = toJSONObject(bytes);
				if (response != null ){
					if(isRight(response)) {
						JSONObject data = response.optJSONObject(Const.STA_DATA);
						User.self().userInfo = PersonData.newInstance(data.optJSONObject(Const.STA_MEMBER));
					}
				}
			}
		},new JSONObject(),UrlUtils.self().getSettingData());
	}
	public void onMapFloatViewClick(View view) {// 漂浮渔场信息
		int id = view.getId();
		switch (id) {
		case R.id.float_view_detail_btn:// 钓场详情
			View titleView = mFloatViewInfo.findViewById(R.id.float_view_title);
			final Bundle b = (Bundle) titleView.getTag();
			int fPlaceId = b.getInt(Const.STA_ID);
			showFieldDetail("" + fPlaceId, true);
			break;
		case R.id.float_view_nav_btn://
			break;
		case R.id.float_view_care_text://
			break;
		}
	}

	private void updateMarkerStatus(ViewGroup parent) {
		if (mOverlayOptions == null)
			return;// 不存在搜索结果的时候
		// 获取正在显示的类型
		int count = parent.getChildCount();
		ArrayList<Integer> showTags = new ArrayList<Integer>();
		for (int i = 0; i < count; i++) {
			TextView tv = (TextView) ((ViewGroup) parent.getChildAt(i)).getChildAt(1);
			if (tv.getTag() != null && tv.getTag().equals("1")) {// tag为时为显示状态
				showTags.add(convertInt(tv.getText().toString()));
			}
		}
		int mCount = mOverlayOptions.size();
		int s = mCount;
		for (int i = 0; i < mCount; i++) {
			MarkerOptions m = (MarkerOptions)mOverlayOptions.get(i);
			Bundle b = m.getExtraInfo();
			int[] ts = b.getIntArray(Const.STA_TAG);// 获取marker的tag类型
			boolean setHide = true;
			for (int j = 0; j < ts.length; j++) {
				if (showTags.contains(ts[j])) {// 含有标签在显示列表，此marker需要继续显示
					setHide = false;
					break;
				}
			}
			if (setHide) {
				s--;
				m.visible(false);
			} else {
				m.visible(true);
			}
		}
		mOverlayManager.addToMap();
		mOverlayManager.zoomToSpan();
		ViewHelper.showToast(HomeUI.this, "剩余" + s + "显示");
	}

	OnGetLocationListener mOnGetLocationListener = null;

	
	private OnGetLocationListener initOnGetLocationListener() {
		if(mOverlayManager == null){
			LogUtils.d("homeui", "initOnGetLocationListener");
			mOverlayManager = new OverlayManager(mBaiduMap) {
				@Override
				public boolean onPolylineClick(Polyline arg0) {
					return false;
				}
				
				@Override
				public boolean onMarkerClick(Marker arg0) {
					return false;
				}
				
				@Override
				public List<OverlayOptions> getOverlayOptions() {
					return mOverlayOptions;
				}
			};
		}
		if (mOnGetLocationListener == null) {
			mOnGetLocationListener = new OnGetLocationListener() {
				@Override
				public void onGetLocation(LocationData data) {
					User.self().userInfo.lng = data.lng;
					User.self().userInfo.lat = data.lat;
					User.self().userInfo.address = data.address;
					LogUtils.d("homeui", "onGetLocation " + User.self().userInfo.lng + " " + User.self().userInfo.lat);
					if (justUplocation) {// 更新实时位置
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put(Const.STA_LAT, String.valueOf(data.lat));
							jsonObject.put(Const.STA_LNG, String.valueOf(data.lng));
							jsonObject.put(Const.STA_LOCATION, User.self().userInfo.address);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						NetTool.data().http(null, jsonObject,
								UrlUtils.self().getSetLocation());
						return;
					}
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put(Const.STA_LAT, String.valueOf(data.lat));
						jsonObject.put(Const.STA_LNG, String.valueOf(data.lng));
						jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_100);
						String tags = LocalMgr.getFPlaceTypes();
						jsonObject.put(Const.STA_TAG, tags);
						jsonObject.put(Const.STA_TYPE, Const.DEFT_YC);
						jsonObject.put(Const.STA_TITLE, "");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					LogUtils.d("homeui", "onGetLocation request queryMap");
					NetTool.data().http(new NetTool.RequestListener() {
						@Override
						public void onStart() {
							// onStart(HomeUI.this);
						}

						@Override
						public void onEnd(byte[] data) {
							if (data != null) {
								locationUpdateTime = System.currentTimeMillis();
								JSONObject resultData = toJSONObject(data);
								LogUtils.d("homeui", "onGetLocation request queryMap onEnd");
								if (resultData != null) {
									if (isRight(resultData)) {
										justUplocation = true;
										makeQueryMapResultMarkers(resultData);
									} else {
										ViewHelper.showToast(HomeUI.this,resultData.optString(Const.STA_MESSAGE));
									}
								}
								// onEnd();
							}
						}
					}, jsonObject, UrlUtils.self().getQueryForMap());
				}
			};
		}
		return mOnGetLocationListener;
	}

	// private static final int TAG_YD = 1;
	// private static final int TAG_BD = 2;
	// private static final int TAG_HK = 3;
	// private static final int TAG_GD = 4;
	// private static final int TAG_SSJRX = 5;
	// private static final int TAG_YD = 1;
	private int convertInt(String tag) {
		String totalTags[] = LocalMgr.getFPlaceType();
		for (int i = 0; i < totalTags.length; i++) {
			if (TextUtils.equals(tag, totalTags[i])) {
				return i;
			}
		}
		return 0;
	}

	final int interval = 2000;

	private void onGetNearFPlace() {
		// 获取自己位置
		MapUtil.getLocation(this, initOnGetLocationListener(), interval);
	}

	int[] fragmentIds = new int[] { R.id.home_fishing_place, R.id.home_care,
			R.id.home_zixun, R.id.home_fishing_news, R.id.home_my };

	int[] footItemIds = new int[] { R.id.foot_bar_fishing_place,
			R.id.foot_bar_care, R.id.foot_bar_appear,
			R.id.foot_bar_fishing_news, R.id.foot_bar_my };
	int[] footItemIconIds = new int[] { R.drawable.map, R.drawable.hart,
			R.drawable.news, R.drawable.play, R.drawable.mine };
	int[] footItemFocusIconIds = new int[] { R.drawable.map_, R.drawable.hart_,
			R.drawable.news_, R.drawable.play_, R.drawable.mine_ };

	public void showFragment(int fid, int footBarId) {
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		HomeFragment f = (HomeFragment) fragmentMgr.findFragmentById(fid);
		currentFragment = f;
		if (!f.isShowing()) {// 不显示状态才需要进行展示
			f.onShow();
			ft.show(f);
			for (int id : fragmentIds) {
				if (id != fid) {
					f = (HomeFragment) fragmentMgr.findFragmentById(id);
					f.onHide();
					ft.hide(f);
				}
			}
			ft.commit();

			// 修改footbar状态
			for (int i = 0; i < footItemIds.length; i++) {
				int fbid = footItemIds[i];
				View view = findViewById(fbid);
				ImageView icon = (ImageView) ((ViewGroup) view).getChildAt(0);
				TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
				if (fbid == footBarId) {// 获取焦点
					mFragmentIndex = i;
					text.setTextColor(getResources().getColor( R.color.foot_bar_focus_text_color));
					icon.setImageResource(footItemFocusIconIds[i]);
				} else {
					text.setTextColor(getResources().getColor( R.color.foot_bar_text_color));
					icon.setImageResource(footItemIconIds[i]);
				}
			}
		}
	}

	public void onFootItemClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.foot_bar_fishing_place:
			showFragment(R.id.home_fishing_place, id);
			break;
		case R.id.foot_bar_care:
			showFragment(R.id.home_care, id);
			break;
		case R.id.foot_bar_appear:
			showFragment(R.id.home_zixun, id);
			break;
		case R.id.foot_bar_fishing_news:
			showFragment(R.id.home_fishing_news, id);
			break;
		case R.id.foot_bar_my:
			showFragment(R.id.home_my, id);
			break;
		}
	}

	public void onLocation(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.myloc:// 我的位置
			justUplocation = false;
			onGetNearFPlace();
			break;
		case R.id.hfs_fishing_tools_store_btn:
			showFishingToolsStore();
			break;
		case R.id.hfs_hfs_splace_type_btn:
			if (splaceTypePopWinView == null) {
				splaceTypePopWinView = LayoutInflater.from(this).inflate(
						R.layout.list_pop_win, null);
				ListView list = (ListView) ((ViewGroup) splaceTypePopWinView)
						.getChildAt(0);
				list.setDividerHeight(0);
				PopWinListItemAdapter.newInstance(this, list,
						LocalMgr.getFPlaceType(), R.layout.listitem_pop_win);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						TextView tv = (TextView) ((ViewGroup) view)
								.getChildAt(1);// .findViewById(R.id.pop_list_item_status_text);
						if (tv.getTag() != null && tv.getTag().equals("1")) {
							tv.setTag("0");
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTag("1");
							// tv.setTextColor(Color.GREEN);
							tv.setTextColor(HomeUI.this.getResources()
									.getColor(R.color.base_btn_color));
						}
						updateMarkerStatus(parent);
						// popupWindow.dismiss();
					}
				});
			}
			showPopWin(view, splaceTypePopWinView);
			break;
		}
	}

	private void showSearchView() {
		UIMgr.showActivity(this, R.layout.ui_search_list, SearchUI.class.getName());
	}

	private void showFishingToolsStore() {
		Toast.makeText(this, "显示渔具店", Toast.LENGTH_LONG).show();
	}

	View splaceTypePopWinView = null;
	PopupWindow popupWindow = null;

	private void showPopWin(View anchor, View view) {
		popupWindow = ViewHelper.showPopupWindow(this, popupWindow, anchor,
				view);
	}

	@Override
	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.fishing_place_head_search_btn:
			showSearchView();
			break;
		case R.id.ui_fnews_head_last_news:
		case R.id.ui_zixun_head_last_news: {
			boolean f = "true".equals(view.getTag());
			if (!f) {
				ViewGroup p = (ViewGroup) view;
				p.setTag("true");
				{// 处理标题已经底线
					TextView titleView = (TextView) p.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.tabitem_foucs_color));
					p.getChildAt(1).setVisibility(View.VISIBLE);

				}
				{// 设置另一个反色
					ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent())
							.getChildAt(1);
					otherP.setTag("false");
					TextView titleView = (TextView) otherP.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.text_btn_color));
					otherP.getChildAt(1).setVisibility(View.GONE);
				}
				{// 设置主内容区显示，隐藏
					int rootViewId = 0;
					if (R.id.ui_fnews_head_last_news == id) {//Home钓播-【钓播|My钓播】
						rootViewId = R.id.ui_fnews_list_root;
					} else if (R.id.ui_zixun_head_last_news == id) {
						rootViewId = R.id.ui_f_appear_list_root;
					}
					ViewGroup vg = (ViewGroup) findViewById(rootViewId);
					vg.getChildAt(0).setVisibility(View.VISIBLE);
					ListView listView  = (ListView)vg.getChildAt(0);
					listView.setTag("0");
					PodCastHelper.getNetPodList(listView, "0", true);
					vg.getChildAt(1).setVisibility(View.GONE);
				}
			}
			break;
		}
		case R.id.ui_fnews_head_mynews:
		case R.id.ui_zixun_head_last_activity: {
			if(R.id.ui_zixun_head_last_activity == id){
				ViewHelper.showToast(this, Const.DEFT_DEVING);
				return ;
			}
			boolean f = "true".equals(view.getTag());
			if (!f) {
				ViewGroup p = (ViewGroup) view;
				p.setTag("true");
				{// 处理标题已经底线
					TextView titleView = (TextView) p.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.tabitem_foucs_color));
					p.getChildAt(1).setVisibility(View.VISIBLE);
				}
				{// 设置另一个反色
					ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent())
							.getChildAt(0);
					otherP.setTag("false");
					TextView titleView = (TextView) otherP.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.text_btn_color));
					otherP.getChildAt(1).setVisibility(View.GONE);
				}
				{// 设置主内容区显示，隐藏
					int rootViewId = 0;
					if (R.id.ui_fnews_head_mynews == id) {
						rootViewId = R.id.ui_fnews_list_root;
					} else if (R.id.ui_zixun_head_last_activity == id) {
						rootViewId = R.id.ui_f_appear_list_root;
					}
					ViewGroup vg = (ViewGroup) findViewById(rootViewId);
					vg.getChildAt(1).setVisibility(View.VISIBLE);
					ListView listView  = (ListView)vg.getChildAt(1);
					listView.setTag(User.self().userInfo.mobileNum);
					PodCastHelper.getNetPodList(listView, User.self().userInfo.mobileNum, true);
					vg.getChildAt(0).setVisibility(View.GONE);
				}
			}
			break;
		}
		case R.id.base_head_bar_publish: {
			UIMgr.showActivity(HomeUI.this, R.layout.ui_comment_publish);
			break;
		}
		}
	}

	public void onExit(View view) {
		LocalMgr.self().saveUserInfo(Const.K_LoginData, null);
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_login);
		intent.setClass(HomeUI.this, BaseUI.class);
		startActivity(intent);
		finish();
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.fplace_state: {
			ImageView iv = (ImageView) ((ViewGroup) view).getChildAt(1);
			String gzOrqxgz = "gz";
			if (iv.isSelected()) {// 取消关注
				iv.setSelected(false);
				gzOrqxgz = "qxgz";
			} else {// 设置关注
				iv.setSelected(true);
				gzOrqxgz = "gz";
			}
			JSONObject jsonObject = new JSONObject();
			int fieldId = (Integer.parseInt(String.valueOf(view.getTag())));
			try {
				jsonObject.put(Const.STA_FIELDID, fieldId);
				jsonObject.put(Const.STA_TYPE, gzOrqxgz);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onEnd(byte[] data) {
					JSONObject response = toJSONObject(data);
					if (response != null
							&& response.has(Const.STA_CODE)
							&& response.optString(Const.STA_CODE).equals(
									Const.DEFT_1)) {// 点赞成功
					// ((TextView)
					// findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
					} else {
						// ViewHelper .showToast(getActivity(),
						// Const.DEFT_GET_CHECK_CODE_FAILED);
					}
				}
			}, jsonObject, UrlUtils.self().getAttention());
			break;
		}
		}
	}

	public void onMyClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.ui_f_my_go_next: {
			// 进入到完善页面
			UIMgr.showActivity(this, R.layout.ui_my_sec, BaseUI.class.getName());
			break;
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case UICode.RequestCode.REQUEST_BARCODE:
			if (resultCode == UICode.ResultCode.RESULT_BARCODE_QR) {
				String ret = data.getStringExtra(Const.PRI_QR_RESULT);
				Log.d("onActivityResult", "RESULT_BARCODE_QR " + ret);
				if (URLUtil.isNetworkUrl(ret)) {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(ret));
					startActivity(i);
				} else {
					Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
				}
			}
			break;

		default:
			break;
		}
	}

	private void makeQueryMapResultMarkers(JSONObject resultData) {
		LogUtils.d("homeui", "makeQueryMapResultMarkers ");
		LatLng userP = new LatLng(User.self().userInfo.lat,
				User.self().userInfo.lng);
		{// 移动到自己位置
//			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(userP);
//			mBaiduMap.animateMapStatus(u);
			//实例化定位数据，并设置在我的位置图层  
			if(mUserLocationOverlayOptions == null){
//				MarkerOptions mo = new MarkerOptions();
//				mUserLocationOverlayOptions = mo;
//				BitmapDescriptor loc = BitmapDescriptorFactory.fromResource(R.drawable.loc);
//				BitmapDescriptor loc_ = BitmapDescriptorFactory.fromResource(R.drawable.loc_);
//				ArrayList<BitmapDescriptor> locs = new ArrayList<BitmapDescriptor>();
//				locs.add(loc);
//				locs.add(loc_);
//				mo.anchor(0.5f, 0.5f);
//				mo.position(userP);
//				mo.icons(locs); 
//				mBaiduMap.addOverlay(mo);
				
				
				 MyLocationData locData = new MyLocationData.Builder()
//                 .accuracy(location.getRadius())
                         // 此处设置开发者获取到的方向信息，顺时针0-360
                 .direction(100).latitude(User.self().userInfo.lat)
                 .longitude(User.self().userInfo.lng).build();
				 mBaiduMap.setMyLocationData(locData);
         
			}else{//仅是更新
				((MarkerOptions)mUserLocationOverlayOptions).position(userP);
			}
	       	
		}
		JSONArray jsonArray = resultData.optJSONArray(Const.STA_DATA);
//		mSearchResult = new ArrayList<OverlayOptions>();
		mOverlayOptions = new ArrayList<OverlayOptions>();
		int count = jsonArray.length();
		LogUtils.d("homeui", "makeQueryMapResultMarkers count=" + count);
		for (int i = 0; i < count; i++) {
			try {
				JSONObject json = jsonArray.optJSONObject(i);// test
				LatLng p = new LatLng(json.optDouble(Const.STA_LAT),json.optDouble(Const.STA_LNG));
				boolean isAttention = json.optBoolean(Const.STA_IS_ATTENTION);
				MarkerOptions ooD = new MarkerOptions().position(p);
				if(isAttention){
					ooD.icon(mDefaultMarkerBD_care);
				}else{
					ooD.icon(mDefaultMarkerBD);
				}
				Bundle b = new Bundle();
//				Marker mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));
//				mSearchResult.add(mMarkerD);
//				mMarkerD.setExtraInfo(b);
				mOverlayOptions.add(ooD);
				ooD.extraInfo(b);
				int fPlaceId = json.optInt(Const.STA_ID);
				b.putString(Const.STA_NAME, json.optString(Const.STA_NAME));
				b.putDouble(Const.PRI_DISTANCE, DistanceUtil.getDistance(userP, p));
				b.putBoolean(Const.STA_IS_ATTENTION,isAttention);
				b.putInt(Const.STA_ID, fPlaceId);
				b.putInt(Const.STA_CARE_COUNT, json.optInt(Const.STA_CARE_COUNT, 0));
				if (json.has(Const.STA_PIRCES)) {
					b.putString(Const.STA_PIRCES, json.optJSONArray(Const.STA_PIRCES).toString());
				}
				String tags = json.optString(Const.STA_TAG);
				// tags = "高钓，冰钓，舒适，黑坑";
				if (!TextUtils.isEmpty(tags)) {
					String[] ts = tags.split(",");
					int[] i_tags = new int[ts.length];
					for (int tag_i = 0; tag_i < ts.length; tag_i++) {
						String s_tag = ts[tag_i];
						i_tags[tag_i] = convertInt(s_tag);
					}

					b.putIntArray(Const.STA_TAG, i_tags);
				}
				// typeMarkers.add(mMarkerD);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
		}
		mOverlayManager.addToMap();
		mOverlayManager.zoomToSpan();
	}

	private void updateMarkerToNormal(Marker marker,Marker newMarker){
		if(marker != null){
			if(marker.getExtraInfo().getBoolean(Const.STA_IS_ATTENTION, false)){
				marker.setIcon(mDefaultMarkerBD_care);
			}else{
				marker.setIcon(mDefaultMarkerBD);
			}
		}
		if(newMarker.getExtraInfo().getBoolean(Const.STA_IS_ATTENTION, false)){
			newMarker.setIcon(mDefaultMarkerBD_care_focus);
		}else{
			newMarker.setIcon(mDefaultMarkerBD_focus);
		}
	}
	private boolean showFloatView(Marker marker) {
		updateMarkerToNormal(mLastMarker,marker);
		mLastMarker = marker;
		mFloatViewInfo.setVisibility(View.VISIBLE);
		Bundle data = marker.getExtraInfo();
		{
			TextView detail_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_title);
			detail_text.setTag(data);
			detail_text.setText(data.getString(Const.STA_NAME));
		}
		{
			TextView distance_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_distance);
			distance_text.setText(MapUtil.getDistance(data.getDouble(Const.PRI_DISTANCE)));
		}
		TextView care_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_care_text);
		care_text.setText(String.valueOf(data.getInt(Const.STA_CARE_COUNT)));
		care_text.setSelected(data.getBoolean(Const.STA_IS_ATTENTION,false));
		{
//			showOrHidePrice(data);
		}
		// 设置跟随点击marker位置
		// Point p =
		// mBaiduMap.getProjection().toScreenLocation(marker.getPosition());
		// mFloatViewInfo.setPadding(mFloatViewInfo.getPaddingLeft(),
		// p.y, mFloatViewInfo.getPaddingRight(),
		// mFloatViewInfo.getPaddingBottom());
		return false;
	}
	private void showOrHidePrice(Bundle data) {
		ViewStub vs = (ViewStub) mFloatViewInfo.findViewById(R.id.calendar_container);
		if (data.containsKey(Const.STA_PIRCES)) {
			ViewGroup calendar_container = null;
			if (vs != null) {// viewstub不为空时，表示还没有初始化
				calendar_container = (ViewGroup) vs.inflate();
			} else {
				calendar_container = (ViewGroup) mFloatViewInfo
						.findViewById(R.id.calendar);
			}
			if (calendar_container.getVisibility() == View.GONE) {
				calendar_container.setVisibility(View.VISIBLE);
				// calendar_container.startAnimation(AnimationUtils.loadAnimation(HomeUI.this,R.anim.slide_in_from_bottom));
			}
			LinearLayout container = (LinearLayout) calendar_container
					.findViewById(R.id.calendar);
			((View) container.getParent()).scrollTo(0, 0);
			try {
				JSONArray datas = new JSONArray(data
						.getString(Const.STA_PIRCES));
				int size = datas.length();
				int ciw = getResources().getDimensionPixelSize(
						R.dimen.calender_item_width);
				int cih = getResources().getDimensionPixelSize(
						R.dimen.calender_item_height);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd");
				Date curDate = new Date();
				for (int i = 0; i < size; i++) {
					View view = null;
					boolean needAdd = false;
					if (container.getChildCount() > i) {
						view = container.getChildAt(i);
					} else {
						view = mLayoutInflater.inflate(
								R.layout.calender_item, null);
						needAdd = true;
					}
					JSONObject item = datas.getJSONObject(i);
					String priceTitle = item
							.getString(Const.STA_PRICE_TITLE);
					String date = item
							.getString(Const.STA_PRICE_DATE);
					int type = -1;
					try {
						Date pd = sdf.parse(date);
						if ("正".equals(priceTitle)) {
							type = 1;
							if (!pd.after(curDate)) {
								type = -1;
							}
						} else {
							type = 2;
							if (!pd.after(curDate)) {
								type = -2;
							}
						}
						date = (pd.getMonth() + 1) + "月"
								+ pd.getDate() + "日";
					} catch (ParseException e) {
						e.printStackTrace();
					}
					int money = item.getInt(Const.STA_PRICE);
					View statusView = view
							.findViewById(R.id.calendar_pic_status);
					TextView moneyView = (TextView) view
							.findViewById(R.id.calendar_money);
					TextView dateView = (TextView) view
							.findViewById(R.id.calendar_date);
					moneyView.setText(money + "￥");

					dateView.setText(date);
					if (needAdd) {
						container.addView(view/*
											 * , new
											 * LinearLayout
											 * .LayoutParams
											 * (ciw, cih)
											 */);
					}
					switch (type) {
					case -1:
						moneyView
								.setTextColor(getResources()
										.getColor(
												R.color.calender_disable_money_text_color));
						dateView.setTextColor(getResources()
								.getColor(
										R.color.calender_disable_text_color));
						dateView.setBackgroundColor(getResources()
								.getColor(
										R.color.calender_disable_bg_color));
						statusView
								.setBackgroundResource(R.drawable.z_);
						break;
					case 1:
						moneyView
								.setTextColor(getResources()
										.getColor(
												R.color.calender_money_text_color));
						dateView.setTextColor(getResources()
								.getColor(
										R.color.calender_z_text_color));
						dateView.setBackgroundColor(getResources()
								.getColor(
										R.color.calender_z_bg_color));
						statusView
								.setBackgroundResource(R.drawable.z);
						break;
					case -2:
						moneyView
								.setTextColor(getResources()
										.getColor(
												R.color.calender_disable_money_text_color));
						dateView.setTextColor(getResources()
								.getColor(
										R.color.calender_disable_text_color));
						dateView.setBackgroundColor(getResources()
								.getColor(
										R.color.calender_disable_bg_color));
						statusView
								.setBackgroundResource(R.drawable.t_);
						break;
					case 2:
						moneyView
								.setTextColor(getResources()
										.getColor(
												R.color.calender_money_text_color));
						dateView.setTextColor(getResources()
								.getColor(
										R.color.calender_t_text_color));
						dateView.setBackgroundColor(getResources()
								.getColor(
										R.color.calender_t_bg_color));
						statusView
								.setBackgroundResource(R.drawable.t);
						break;
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (vs == null) {// viewstub已经初始化，才有必要执行隐藏
			if (mFloatViewInfo.findViewById(R.id.calendar)
					.getVisibility() == View.VISIBLE) {
				mFloatViewInfo.findViewById(R.id.calendar)
						.setVisibility(View.GONE);
			}
		}
	}
	
	public void showFieldDetail(String fPlaceId,final boolean hideMapInfo){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fPlaceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				onStart(HomeUI.this);
			}
			@Override
			public void onEnd(byte[] data) {
				try {
					if (isOver()) {// 返回键取消联网之后不应该继续处理
					} else {
						if (data != null) {
							String jsonStr = new String(data, "UTF-8");
							JSONObject response = toJSONObject(jsonStr);
							if (isRight(HomeUI.this,response,true)) {
								Bundle newBundle = new Bundle();
								newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
								newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_detail_field);
								Intent i = new Intent();
								i.putExtras(newBundle);
								UIMgr.showActivity(HomeUI.this, i, SearchUI.class.getName());
								if(hideMapInfo){
									mFloatViewInfo.setVisibility(View.GONE);
								}
							}
						} else {
							onDataError(HomeUI.this);
						}
					}
					onEnd();
				} catch (Exception e) {
				}
			}
		}, jsonObject, UrlUtils.self().getFieldInfo());
	}
	public void onPodCastDetailClick(View view) {
		UIMgr.showActivity(this,R.layout.ui_detail_podcast,SearchUI.class.getName());
	}
	public void onPersonClick(View view) {
		PersonData personData = (PersonData)view.getTag();
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_podcast_person);
		i.putExtra(Const.STA_TITLE, personData.userName + "钓播");
		i.putExtra(Const.PRI_HIDE_PUBLISH, true);
		i.putExtra(Const.STA_MOBILE, personData.mobileNum);
		UIMgr.showActivity(this,i,BaseUI.class.getName());
	}
	@Override
	public void onItemClick(View view, IBaseData data) {
		data.OnClick(HomeUI.this, null, view);
	}
}