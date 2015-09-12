package com.go.fish.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.go.fish.R;
import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.HomeFragment;
import com.go.fish.view.PopWinListItemAdapter;
import com.go.fish.view.ViewHelper;

public class HomeUI extends FragmentActivity implements IHasHeadBar{

	FragmentManager fragmentMgr = null;
	MapView mMapView;
	BaiduMap mBaiduMap;
	HashMap<String, ArrayList<Marker>> mSearchResult = null;
	ViewGroup mFloatViewInfo = null;
	RelativeLayout floatView = null;
	ViewGroup mHomeMainView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(Const.PRI_LAYOUT_ID, 0);
		setContentView(layout_id);
		mHomeMainView = (ViewGroup)findViewById(R.id.home_main_view);
		fragmentMgr = getSupportFragmentManager();
		initFirstTabItem();
	}

	void initFirstTabItem() {
		{// 设置foot item 获取焦点状态
			// showFragment(R.id.home_fishing_place,
			// R.id.foot_bar_fishing_place);
			View view = findViewById(R.id.foot_bar_fishing_place);
			TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
			text.setTextColor(getResources().getColor(
					R.color.foot_bar_focus_text_color));
		}
		{// 初始化地图
			ViewGroup vg = (ViewGroup) findViewById(R.id.fishing_place_bmap_view);
			mMapView = MapUtil.newMap(this);
			mBaiduMap = mMapView.getMap();
			try {
//				mMapView.getChildAt(2).setVisibility(View.GONE);
				mMapView.getChildAt(1).setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			vg.addView(mMapView);
			
			LayoutInflater.from(this).inflate(R.layout.float_view_fplace, vg);
			LayoutInflater.from(this).inflate(R.layout.float_view_mylocation_fplace, vg);
			ViewGroup floatView = (ViewGroup)LayoutInflater.from(HomeUI.this).inflate(R.layout.listitem_float_view_in_map, null);
			mFloatViewInfo = new FrameLayout(this);
			mFloatViewInfo.setVisibility(View.GONE);
			mFloatViewInfo.addView(floatView);
			int space = (int)getResources().getDimension(R.dimen.base_space);
			mFloatViewInfo.setPadding(space, 0, space, 0);
			vg.addView(mFloatViewInfo,new LayoutParams(-1, -2));
			
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					mFloatViewInfo.setVisibility(View.VISIBLE);
					Bundle data = marker.getExtraInfo();
					{
						TextView detail_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_title);
						detail_text.setTag(data);
						detail_text.setText(data.getString(Const.STA_TITLE));
					}
					{
						TextView distance_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_distance);
						distance_text.setText(MapUtil.getDistance(data.getDouble(Const.PRI_DISTANCE)));
					}
					Point p = mBaiduMap.getProjection().toScreenLocation(marker.getPosition());
					mFloatViewInfo.setPadding(mFloatViewInfo.getPaddingLeft(), p.y, mFloatViewInfo.getPaddingRight(), mFloatViewInfo.getPaddingBottom());
					return false;
				}
			});
			mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public boolean onMapPoiClick(MapPoi arg0) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					mFloatViewInfo.setVisibility(View.GONE);
				}
			});
			mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
				
				@Override
				public void onTouch(MotionEvent arg0) {
					mFloatViewInfo.setVisibility(View.GONE);
				}
			});
			
			// {
			// MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15).build();
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
	/**离开当前页面10秒则刷新位置*/
	final int spaceTime = 600000;
	/**上一次定位时间*/
	long locationUpdateTime = System.currentTimeMillis();
	private void updateLocation(){
		if(mMapView == null && System.currentTimeMillis() - locationUpdateTime < spaceTime) return;
		mMapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				onGetNearFPlace();
			}
		}, 500);
	}
	@Override
	protected void onResume() {
		super.onResume();
//		updateLocation();
	}

	public void onMapFloatViewClick(View view){
		int id = view.getId();
		switch (id) {
			case R.id.float_view_detail_btn://详情
				View titleView = mFloatViewInfo.findViewById(R.id.float_view_title);
				final Bundle b = (Bundle)titleView.getTag();
				String fPlaceId = b.getString(Const.STA_FISHING_PLACE_ID);
				RequestData rData = RequestData.newInstance(new RequestListener() {
					@Override
					public void onStart() {
						ViewHelper.showGlobalWaiting(HomeUI.this, null);
					}

					@Override
					public void onEnd(byte[] data) {
						try {
							String jsonStr = new String(data, "UTF-8");
							Bundle newBundle = new Bundle();
							String fPlaceId = b.getString(Const.STA_FISHING_PLACE_ID);
							newBundle.putString(Const.STA_FISHING_PLACE_ID, fPlaceId);
							newBundle.putString(Const.STA_TEXT, b.getString(Const.STA_TITLE));
							newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.search);
							newBundle.putInt(Const.PRI_EXTRA_LAYOUT_ID, R.layout.ui_f_search_item_detail);
							Intent i = new Intent();
							i.putExtras(newBundle);
							UIMgr.showActivity(HomeUI.this,i, SearchUI.class.getName());
							ViewHelper.closeGlobalWaiting();
							mFloatViewInfo.setVisibility(View.GONE);
						} catch (Exception e) {
						}
					}
				}, "fishing_place_" + fPlaceId);
	            rData.putData(Const.STA_FISHING_PLACE_ID, fPlaceId);
	            NetTool.data().http(rData.syncCallback());
				break;
			case R.id.float_view_nav_btn://
				break;
			case R.id.float_view_care_btn://
				break;
		}
	}
	
	OnGetLocationListener mOnGetLocationListener = null;
	private OnGetLocationListener initOnGetLocationListener(){
		if(mOnGetLocationListener == null){
			mOnGetLocationListener = new OnGetLocationListener(){
				@Override
				public void onGetLocation(LocationData data) {
					final LatLng userP = new LatLng(data.lat, data.lng);
					{//移动到自己位置
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(userP);
						mBaiduMap.animateMapStatus(u);
					}
					
					NetTool.RequestData rData = NetTool.RequestData.newInstance(
			                new NetTool.RequestListener() {
			                    @Override
			                    public void onStart() {
			                      ViewHelper.showGlobalWaiting(HomeUI.this,null);
			                    }
			                    @Override
			                    public void onEnd(byte[] data) {
			                        if (data != null) {
			                        	locationUpdateTime = System.currentTimeMillis();
										JSONArray jsonArray = toJSONArray(data);
										mSearchResult = new HashMap<String, ArrayList<Marker>>();
										BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.point);
										for (int i = 0; i < jsonArray.length(); i++) {
											JSONArray typeFPlaceArr = jsonArray.optJSONArray(i);
											ArrayList<Marker> typeMarkers = new ArrayList<Marker>();
											mSearchResult.put(i+"", typeMarkers);
											for(int j = 0; j < typeFPlaceArr.length(); j++){
												try {
													JSONObject json = typeFPlaceArr.optJSONObject(j);
													LatLng p = new LatLng(json.optDouble(Const.STA_LAT),json.optDouble(Const.STA_LNG));
													OverlayOptions ooD = new MarkerOptions().position(p).icon(bdA);
													Marker mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));
													Bundle b = new Bundle();
													String fPlaceId = json.optString(Const.STA_FISHING_PLACE_ID);
													b.putString(Const.STA_TITLE, json.optString(Const.STA_TITLE));
													b.putDouble(Const.PRI_DISTANCE, DistanceUtil.getDistance(userP, p));
													b.putString(Const.STA_FISHING_PLACE_ID, fPlaceId);
													mMarkerD.setExtraInfo(b);
													typeMarkers.add(mMarkerD);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
			                            ViewHelper.closeGlobalWaiting();
			                        }
			                    }
			                }, "fishing_place_near"
			        );
			        NetTool.data().http(rData.syncCallback());
//					JSONObject json = new JSONObject();
//					json.put("lng", data.lng);
//					json.put("lat", data.lat);
//					RequestData rData = RequestData.newInstance(new RequestListener() {
//						@Override
//						public void onEnd(byte[] data) {
//							JSONObject json = toJSONObject(data);
//						}
//					}, json);
//					NetTool.http(rData);
				}
			};
		}
		return mOnGetLocationListener;
	}
	private void onGetNearFPlace(){
		//获取自己位置
		MapUtil.getLocation(this, initOnGetLocationListener(), 0);
		
	}
	
	int[] ids = new int[] { R.id.home_fishing_place, R.id.home_care,
			R.id.home_appear, R.id.home_fishing_news, R.id.home_my };

	int[] footBarIds = new int[] { R.id.foot_bar_fishing_place,
			R.id.foot_bar_care, R.id.foot_bar_appear,
			R.id.foot_bar_fishing_news, R.id.foot_bar_my };

	public void showFragment(int fid, int footBarId) {
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		HomeFragment f = (HomeFragment)fragmentMgr.findFragmentById(fid);
		if(!f.isShowing()){//不显示状态才需要进行展示
			
			f.onShow();
			ft.show(f);
		
			for (int id : ids) {
				if (id != fid) {
					f = (HomeFragment)fragmentMgr.findFragmentById(id);
					f.onHide();
					ft.hide(f);
				}
			}
			ft.commit();
	
			for (int fbid : footBarIds) {
				View view = findViewById(fbid);
				TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
				if (fbid == footBarId) {
					text.setTextColor(getResources().getColor(
							R.color.foot_bar_focus_text_color));
				} else {
					text.setTextColor(getResources().getColor(
							R.color.foot_bar_text_color));
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
			showFragment(R.id.home_appear, id);
			break;
		case R.id.foot_bar_fishing_news:
			showFragment(R.id.home_fishing_news, id);
			break;
		case R.id.foot_bar_my:
			showFragment(R.id.home_my, id);
			break;
		}
	}
	
	public void onFishingPlaceClick(View view){
		int id = view.getId();
		switch (id) {
		case R.id.myloc:
			onGetNearFPlace();
			break;
		case R.id.hfs_fishing_tools_store_btn:
			showFishingToolsStore();
			break;
		case R.id.hfs_hfs_splace_type_btn:
			if(splaceTypePopWinView == null){
				ListView list = (ListView)LayoutInflater.from(this).inflate(R.layout.list_pop_win, null);
				splaceTypePopWinView = list;
				PopWinListItemAdapter adapter = PopWinListItemAdapter.newInstance(this,list,R.array.hfs_splace_type,R.layout.listitem_pop_win);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						TextView tv = (TextView)((ViewGroup)view).getChildAt(1);//.findViewById(R.id.pop_list_item_status_text);
						if(tv.getTag() != null && tv.getTag().equals("1")){
							tv.setTag("0");
							tv.setTextColor(Color.GRAY);
						}else{
							tv.setTag("1");
							tv.setTextColor(Color.GREEN);
						}
//						popupWindow.dismiss();
					}
				});
			}
			showPopWin(view,splaceTypePopWinView);
			break;
		}
	}
	
	private void showSearchView(){
		UIMgr.showActivity(this, R.layout.search,SearchUI.class.getName());
	}
	private void showFishingToolsStore(){
		Toast.makeText(this,"显示渔具店",0).show();
	}
	View splaceTypePopWinView = null;
	PopupWindow popupWindow = null;
	private void showPopWin(View anchor,View view){
		popupWindow = ViewHelper.showPopupWindow(this,popupWindow,anchor,view);
	}
	

	@Override
	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.fishing_place_head_search_btn:
			showSearchView();
			break;
		case R.id.ui_fnews_head_last_news:{
            boolean f = "true".equals(view.getTag());
            if(!f){
                view.setBackgroundResource(R.drawable.title_btn_background_selected);
                view.setTag("true");
                {
                    View fPlace = findViewById(R.id.ui_fnews_head_mynews);
                    fPlace.setBackgroundResource(R.drawable.title_btn_background);
                    fPlace.setTag(false);
                }
                {
                    ViewGroup vg = (ViewGroup)findViewById(R.id.ui_fnews_list_root);
                    vg.getChildAt(0).setVisibility(View.VISIBLE);
                    vg.getChildAt(1).setVisibility(View.GONE);
                }
            }
            break;
        }
        case R.id.ui_fnews_head_mynews:{
            boolean f = "true".equals(view.getTag());
            if(!f){
                view.setBackgroundResource(R.drawable.title_btn_background_selected);
                view.setTag("true");
                {
                    View fNews = findViewById(R.id.ui_fnews_head_last_news);
                    fNews.setBackgroundResource(R.drawable.title_btn_background);
                    fNews.setTag(false);
                }
                {
                    ViewGroup vg = (ViewGroup)findViewById(R.id.ui_fnews_list_root);
                    vg.getChildAt(1).setVisibility(View.VISIBLE);
                    vg.getChildAt(0).setVisibility(View.GONE);
                }
            }
            break;
        }
		}
	}
	
	public void onMyClick(View view){
		int id = view.getId();
		switch (id) {
		case R.id.ui_f_my_exit:
			finish();
			break;
		case R.id.ui_f_my_go_next:{
			UIMgr.showActivity(this, R.layout.ui_my_sec, BaseUI.class.getName());
			break;
			}
		}
	}
}