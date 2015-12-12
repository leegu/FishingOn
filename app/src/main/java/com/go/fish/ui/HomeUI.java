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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.go.fish.R;
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
import com.go.fish.view.HomeFragment;
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
import java.util.Random;
import java.util.Set;

public class HomeUI extends FragmentActivity implements IHasHeadBar {

    FragmentManager fragmentMgr = null;
    MapView mMapView;
    BaiduMap mBaiduMap;
    ArrayList<Marker> mSearchResult = null;
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

    void initFirstTabItem() {
        {// 设置foot item 获取焦点状态
            int i = 0;
//			 showFragment(fragmentIds[i], footItemIds[i]);
            View view = findViewById(footItemIds[i]);
            ImageView icon = (ImageView) ((ViewGroup) view).getChildAt(0);
            icon.setImageResource(footItemFocusIconIds[i]);
            TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
            text.setTextColor(getResources().getColor(R.color.foot_bar_focus_text_color));
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
//			LayoutInflater.from(this).inflate(R.layout.float_view_mylocation_fplace, vg);
            mFloatViewInfo = (ViewGroup) LayoutInflater.from(HomeUI.this).inflate(R.layout.listitem_float_view_in_map, null);
            mFloatViewInfo.setVisibility(View.GONE);
            vg.addView(mFloatViewInfo, new LayoutParams(-1, -2));

            mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
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
                    if(data.getInt(Const.STA_CARE_COUNT) > 0){
                        TextView care_text = (TextView) mFloatViewInfo.findViewById(R.id.float_view_care_text);
                        care_text.setText(String.valueOf(data.getInt(Const.STA_CARE_COUNT)));
                    }
                    {
                        ViewStub vs = (ViewStub) mFloatViewInfo.findViewById(R.id.calendar_container);
                        if (data.containsKey(Const.STA_PIRCES)) {
                            ViewGroup calendar_container = null;
                            if (vs != null) {//viewstub不为空时，表示还没有初始化
                                calendar_container = (ViewGroup) vs.inflate();
                            } else {
                                calendar_container = (ViewGroup) mFloatViewInfo.findViewById(R.id.calendar);
                            }
                            if (calendar_container.getVisibility() == View.GONE) {
                                calendar_container.setVisibility(View.VISIBLE);
//								calendar_container.startAnimation(AnimationUtils.loadAnimation(HomeUI.this,R.anim.slide_in_from_bottom));
                            }
                            LinearLayout container = (LinearLayout) calendar_container.findViewById(R.id.calendar);
                            ((View) container.getParent()).scrollTo(0, 0);
                            try {
                                JSONArray datas = new JSONArray(data.getString(Const.STA_PIRCES));
                                int size = datas.length();
                                int ciw = getResources().getDimensionPixelSize(R.dimen.calender_item_width);
                                int cih = getResources().getDimensionPixelSize(R.dimen.calender_item_height);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date curDate = new Date();
                                for (int i = 0; i < size; i++) {
                                    View view = null;
                                    boolean needAdd = false;
                                    if (container.getChildCount() > i) {
                                        view = container.getChildAt(i);
                                    } else {
                                        view = mLayoutInflater.inflate(R.layout.calender_item, null);
                                        needAdd = true;
                                    }
                                    JSONObject item = datas.getJSONObject(i);
                                    String priceTitle = item.getString(Const.STA_PRICE_TITLE);
                                    String date = item.getString(Const.STA_PRICE_DATE);
                                    int type = -1;
                                    try {
                                        Date pd = sdf.parse(date);
                                        if("正".equals(priceTitle)){
                                            type = 1;
                                            if(!pd.after(curDate)){
                                                type = -1;
                                            }
                                        }else{
                                            type = 2;
                                            if(!pd.after(curDate)){
                                                type = -2;
                                            }
                                        }
                                        date = (pd.getMonth() + 1) + "月" + pd.getDate() + "日";
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    int money = item.getInt(Const.STA_PRICE);
                                    View statusView = view.findViewById(R.id.calendar_pic_status);
                                    TextView moneyView = (TextView) view.findViewById(R.id.calendar_money);
                                    TextView dateView = (TextView) view.findViewById(R.id.calendar_date);
                                    moneyView.setText(money + "￥");

                                    dateView.setText(date);
                                    if (needAdd) {
                                        container.addView(view/*, new LinearLayout.LayoutParams(ciw, cih)*/);
                                    }
                                    switch (type) {
                                        case -1:
                                            moneyView.setTextColor(getResources().getColor(R.color.calender_disable_money_text_color));
                                            dateView.setTextColor(getResources().getColor(R.color.calender_disable_text_color));
                                            dateView.setBackgroundColor(getResources().getColor(R.color.calender_disable_bg_color));
                                            statusView.setBackgroundResource(R.drawable.z_);
                                            break;
                                        case 1:
                                            moneyView.setTextColor(getResources().getColor(R.color.calender_money_text_color));
                                            dateView.setTextColor(getResources().getColor(R.color.calender_z_text_color));
                                            dateView.setBackgroundColor(getResources().getColor(R.color.calender_z_bg_color));
                                            statusView.setBackgroundResource(R.drawable.z);
                                            break;
                                        case -2:
                                            moneyView.setTextColor(getResources().getColor(R.color.calender_disable_money_text_color));
                                            dateView.setTextColor(getResources().getColor(R.color.calender_disable_text_color));
                                            dateView.setBackgroundColor(getResources().getColor(R.color.calender_disable_bg_color));
                                            statusView.setBackgroundResource(R.drawable.t_);
                                            break;
                                        case 2:
                                            moneyView.setTextColor(getResources().getColor(R.color.calender_money_text_color));
                                            dateView.setTextColor(getResources().getColor(R.color.calender_t_text_color));
                                            dateView.setBackgroundColor(getResources().getColor(R.color.calender_t_bg_color));
                                            statusView.setBackgroundResource(R.drawable.t);
                                            break;
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (vs == null) {//viewstub已经初始化，才有必要执行隐藏
                            if (mFloatViewInfo.findViewById(R.id.calendar).getVisibility() == View.VISIBLE) {
//								Animation anim = AnimationUtils.loadAnimation(HomeUI.this,R.anim.slide_out_to_bottom);
//								anim.setAnimationListener(new Animation.AnimationListener() {
//									@Override
//									public void onAnimationStart(Animation animation) {}
//									@Override
//									public void onAnimationEnd(Animation animation) {
                                mFloatViewInfo.findViewById(R.id.calendar).setVisibility(View.GONE);
//									}
//									@Override
//									public void onAnimationRepeat(Animation animation) {}
//								});
//								mFloatViewInfo.startAnimation(anim);
                            }
                        }
                    }
                    //设置跟随点击marker位置
//					Point p = mBaiduMap.getProjection().toScreenLocation(marker.getPosition());
//					mFloatViewInfo.setPadding(mFloatViewInfo.getPaddingLeft(), p.y, mFloatViewInfo.getPaddingRight(), mFloatViewInfo.getPaddingBottom());
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
//					mFloatViewInfo.setVisibility(View.GONE);
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

    /**
     * 离开当前页面10秒则刷新位置
     */
    final int spaceTime = 600000;
    /**
     * 上一次定位时间
     */
    long locationUpdateTime = System.currentTimeMillis();

    private void updateLocation() {
        if (mMapView == null && System.currentTimeMillis() - locationUpdateTime < spaceTime) return;
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                onGetNearFPlace();
            }
        }, 500);
    }
    
    boolean justUplocation = false;
    @Override
    protected void onResume() {
        super.onResume();
        justUplocation = false;
		updateLocation();
    }

    public void onMapFloatViewClick(View view) {//漂浮渔场信息
        int id = view.getId();
        switch (id) {
            case R.id.float_view_detail_btn://详情
                View titleView = mFloatViewInfo.findViewById(R.id.float_view_title);
                final Bundle b = (Bundle) titleView.getTag();
                int fPlaceId = b.getInt(Const.STA_ID);
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
                        	if(isOver()){//返回键取消联网之后不应该继续处理
                        		
                        	}else{
                        		JSONObject response = toJSONObject(data);
                        		if(response != null){
	                        		if(isRight(response)){
			                            String jsonStr = new String(data, "UTF-8");
			                            Bundle newBundle = new Bundle();
			                            newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
			                            newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.search);
			                            newBundle.putInt(Const.PRI_EXTRA_LAYOUT_ID, R.layout.ui_f_search_item_detail);
			                            Intent i = new Intent();
			                            i.putExtras(newBundle);
			                            UIMgr.showActivity(HomeUI.this, i, SearchUI.class.getName());
			                            mFloatViewInfo.setVisibility(View.GONE);
	                        		}else{
	                        			onDataError(HomeUI.this, response);
	                        		}
                        		}else{
                        			onDataError(HomeUI.this);
                        		}
                        	}
                            onEnd();
                        } catch (Exception e) {
                        }
                    }
                }, jsonObject, UrlUtils.self().getFieldInfo());
                break;
            case R.id.float_view_nav_btn://
                break;
            case R.id.float_view_care_text://
                break;
        }
    }
    
    private void updateMarkerStatus(ViewGroup parent){
    	if(mSearchResult == null) return;//不存在搜索结果的时候
    	//获取正在显示的类型
    	int count = parent.getChildCount();
    	ArrayList<Integer> showTags = new ArrayList<Integer>();
    	for(int i = 0; i < count; i++){
    		TextView tv = (TextView)((ViewGroup)parent.getChildAt(i)).getChildAt(1);
    		if(tv.getTag() != null && tv.getTag().equals("1")){//tag为时为显示状态
    			showTags.add(convertInt(tv.getText().toString()));
    		}
    	}
    	int mCount = mSearchResult.size();
    	int s = mCount;
    	for(int i = 0;i < mCount; i++){
    		Marker m = mSearchResult.get(i);
    		Bundle b = m.getExtraInfo();
    		int[] ts = b.getIntArray(Const.STA_TAG);//获取marker的tag类型
    		boolean setHide = true;
    		for(int j = 0;j < ts.length; j++){
    			if(showTags.contains(ts[j])){//含有标签在显示列表，此marker需要继续显示
    				setHide = false;
    				break;
    			}
    		}
    		if(setHide){
    			s--;
    			m.setVisible(false);
    		}else{
    			m.setVisible(true);
    		}
    	}
    	ViewHelper.showToast(HomeUI.this, "剩余" + s + "显示");
    }

    OnGetLocationListener mOnGetLocationListener = null;

    private OnGetLocationListener initOnGetLocationListener() {
        if (mOnGetLocationListener == null) {
            mOnGetLocationListener = new OnGetLocationListener() {
                @Override
                public void onGetLocation(LocationData data) {
                	User.self().userInfo.lng = data.lng;
    				User.self().userInfo.lat = data.lat;
    				User.self().userInfo.address = data.address;
    				if(justUplocation){//更新实时位置
    					JSONObject jsonObject = new JSONObject();
            			try {
            				jsonObject.put(Const.STA_LAT, String.valueOf(data.lat));
            				jsonObject.put(Const.STA_LNG, String.valueOf(data.lng));
            				jsonObject.put(Const.STA_LOCATION, User.self().userInfo.address);
            			} catch (JSONException e) {
            				e.printStackTrace();
            			}
                        NetTool.data().http(null, jsonObject,UrlUtils.self().getSetLocation());
    					return;
    				}
                    JSONObject jsonObject = new JSONObject();
        			try {
        				jsonObject.put(Const.STA_LAT, String.valueOf(data.lat));
        				jsonObject.put(Const.STA_LNG, String.valueOf(data.lng));
        				jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT);
        				jsonObject.put(Const.STA_TAG, Const.DEFT_REQUEST_QUERY_LIST);
        				jsonObject.put(Const.STA_TYPE, Const.DEFT_YC);
        				jsonObject.put(Const.STA_TITLE, "");
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    NetTool.data().http(
                            new NetTool.RequestListener() {
                                @Override
                                public void onStart() {
//                                	onStart(HomeUI.this);
                                }

                                @Override
                                public void onEnd(byte[] data) {
                                    if (data != null) {
                                        locationUpdateTime = System.currentTimeMillis();
                                        JSONObject resultData = toJSONObject(data);
                                        if(resultData != null ){
                                            if( isRight(resultData)){
                                            	justUplocation = true;
                                            	makeQueryMapResultMarkers(resultData);
                                            }else{
                                                ViewHelper.showToast(HomeUI.this,resultData.optString(Const.STA_MESSAGE));
                                            }
                                        }
//                                        onEnd();
                                    }
                                }
                            }, jsonObject,UrlUtils.self().getQueryForMap());
                }
            };
        }
        return mOnGetLocationListener;
    }

    private static final int TAG_YD = 1;
    private static final int TAG_BD = 2;
    private static final int TAG_HK = 3;
    private static final int TAG_GD = 4;
    private static final int TAG_SSJRX = 5;
//    private static final int TAG_YD = 1;
    private int convertInt(String tag){
    	if(tag.equals("夜钓")){
    		return TAG_YD;
    	}else if(tag.equals("冰钓")){
    		return TAG_BD;
    	}else if(tag.equals("高钓")){
    		return TAG_GD;
    	}else if(tag.equals("黑坑")){
    		return TAG_HK;
    	}else if(tag.equals("舒适家人行") || tag.equals("舒钓")){
    		return TAG_SSJRX;
    	}else{
    		return 0;
    	}
    }
    
    final int interval = 2000; 
    private void onGetNearFPlace() {
        //获取自己位置
        MapUtil.getLocation(this, initOnGetLocationListener(), interval);
    }
    

    int[] fragmentIds = new int[]{R.id.home_fishing_place, R.id.home_care,
            R.id.home_zixun, R.id.home_fishing_news, R.id.home_my};

    int[] footItemIds = new int[]{R.id.foot_bar_fishing_place,
            R.id.foot_bar_care, R.id.foot_bar_appear,
            R.id.foot_bar_fishing_news, R.id.foot_bar_my};
    int[] footItemIconIds = new int[]{R.drawable.map,
            R.drawable.hart, R.drawable.news,
            R.drawable.play, R.drawable.mine};
    int[] footItemFocusIconIds = new int[]{R.drawable.map_,
            R.drawable.hart_, R.drawable.news_,
            R.drawable.play_, R.drawable.mine_};


    public void showFragment(int fid, int footBarId) {
        FragmentTransaction ft = fragmentMgr.beginTransaction();
        HomeFragment f = (HomeFragment) fragmentMgr.findFragmentById(fid);
        if (!f.isShowing()) {//不显示状态才需要进行展示
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

            //修改footbar状态
            for (int i = 0; i < footItemIds.length; i++) {
                int fbid = footItemIds[i];
                View view = findViewById(fbid);
                ImageView icon = (ImageView) ((ViewGroup) view).getChildAt(0);
                TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
                if (fbid == footBarId) {//获取焦点
                    text.setTextColor(getResources().getColor(
                            R.color.foot_bar_focus_text_color));
                    icon.setImageResource(footItemFocusIconIds[i]);
                } else {
                    text.setTextColor(getResources().getColor(
                            R.color.foot_bar_text_color));
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
            case R.id.myloc://我的位置
            	justUplocation = true;
                onGetNearFPlace();
                break;
            case R.id.hfs_fishing_tools_store_btn:
                showFishingToolsStore();
                break;
            case R.id.hfs_hfs_splace_type_btn:
                if (splaceTypePopWinView == null) {
                    splaceTypePopWinView = LayoutInflater.from(this).inflate(R.layout.list_pop_win, null);
                    ListView list = (ListView) ((ViewGroup) splaceTypePopWinView).getChildAt(0);
                    list.setDividerHeight(0);
                    PopWinListItemAdapter.newInstance(this, list, R.array.hfs_splace_type, R.layout.listitem_pop_win);
                    list.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // TODO Auto-generated method stub
                            TextView tv = (TextView) ((ViewGroup) view).getChildAt(1);//.findViewById(R.id.pop_list_item_status_text);
                            if (tv.getTag() != null && tv.getTag().equals("1")) {
                                tv.setTag("0");
                                tv.setTextColor(Color.GRAY);
                            } else {
                                tv.setTag("1");
//							tv.setTextColor(Color.GREEN);
                                tv.setTextColor(HomeUI.this.getResources().getColor(R.color.base_btn_color));
                            }
                            updateMarkerStatus(parent);
//						popupWindow.dismiss();
                        }
                    });
                }
                showPopWin(view, splaceTypePopWinView);
                break;
        }
    }

    private void showSearchView() {
        UIMgr.showActivity(this, R.layout.search, SearchUI.class.getName());
    }

    private void showFishingToolsStore() {
        Toast.makeText(this, "显示渔具店", Toast.LENGTH_LONG).show();
    }

    View splaceTypePopWinView = null;
    PopupWindow popupWindow = null;

    private void showPopWin(View anchor, View view) {
        popupWindow = ViewHelper.showPopupWindow(this, popupWindow, anchor, view);
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
                    {//处理标题已经底线
                        TextView titleView = (TextView) p.getChildAt(0);
                        titleView.setTextColor(getResources().getColor(R.color.tabitem_foucs_color));
                        p.getChildAt(1).setVisibility(View.VISIBLE);

                    }
                    {//设置另一个反色
                        ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent()).getChildAt(1);
                        otherP.setTag("false");
                        TextView titleView = (TextView) otherP.getChildAt(0);
                        titleView.setTextColor(getResources().getColor(R.color.text_btn_color));
                        otherP.getChildAt(1).setVisibility(View.GONE);
                    }
                    {//设置主内容区显示，隐藏
                    	int rootViewId = 0;
                    	if(R.id.ui_fnews_head_last_news == id){
                    		rootViewId = R.id.ui_fnews_list_root;
                    	}else if(R.id.ui_zixun_head_last_news == id){
                    		rootViewId = R.id.ui_f_appear_list_root;
                    	}
                        ViewGroup vg = (ViewGroup) findViewById(rootViewId);
                        vg.getChildAt(0).setVisibility(View.VISIBLE);
                        vg.getChildAt(1).setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.ui_fnews_head_mynews: 
            case R.id.ui_zixun_head_last_activity: {
                boolean f = "true".equals(view.getTag());
                if (!f) {
                    ViewGroup p = (ViewGroup) view;
                    p.setTag("true");
                    {//处理标题已经底线
                        TextView titleView = (TextView) p.getChildAt(0);
                        titleView.setTextColor(getResources().getColor(R.color.tabitem_foucs_color));
                        p.getChildAt(1).setVisibility(View.VISIBLE);
                    }
                    {//设置另一个反色
                        ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent()).getChildAt(0);
                        otherP.setTag("false");
                        TextView titleView = (TextView) otherP.getChildAt(0);
                        titleView.setTextColor(getResources().getColor(R.color.text_btn_color));
                        otherP.getChildAt(1).setVisibility(View.GONE);
                    }
                    {//设置主内容区显示，隐藏
                    	int rootViewId = 0;
                    	if(R.id.ui_fnews_head_mynews == id){
                    		rootViewId = R.id.ui_fnews_list_root;
                    	}else if(R.id.ui_zixun_head_last_activity == id){
                    		rootViewId = R.id.ui_f_appear_list_root;
                    	}
                        ViewGroup vg = (ViewGroup) findViewById(rootViewId);
                        vg.getChildAt(1).setVisibility(View.VISIBLE);
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

    public void onExit(View view){
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
	    	case R.id.fplace_temp_title: {
	    		ImageView iv = (ImageView)((ViewGroup)view).getChildAt(1);
	    		String gzOrqxgz = "gz";
	    		if(iv.isSelected()){//取消关注
	    			iv.setSelected(false);
	    			gzOrqxgz = "qxgz";
	    		}else{//设置关注
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
						if (response != null && response.has(Const.STA_CODE) && response.optString(Const.STA_CODE).equals(Const.DEFT_1)) {//点赞成功
//							((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
						} else {
//							ViewHelper .showToast(getActivity(), Const.DEFT_GET_CHECK_CODE_FAILED);
						}
					}
				},jsonObject, UrlUtils.self().getAttention());
	    		break;
	    	}
    	}
    }
    public void onMyClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_f_my_go_next: {
            	//进入到完善页面
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
                    if(URLUtil.isNetworkUrl(ret)){
                    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(ret));
						startActivity(i);
                    }else{
                    	Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
                    }
                }
                break;

            default:
                break;
        }
    }

	private void makeQueryMapResultMarkers(JSONObject resultData) {
		LatLng userP = new LatLng(User.self().userInfo.lat, User.self().userInfo.lng);
		 {//移动到自己位置
		     MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(userP);
		     mBaiduMap.animateMapStatus(u);
		 }
		JSONArray jsonArray = resultData.optJSONArray(Const.STA_DATA);
		mSearchResult = new ArrayList<Marker>();
		BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.point);
		int count = jsonArray.length();
//                                                count = 20;//test
		for (int i = 0; i < count; i++) {
//                                                    JSONArray typeFPlaceArr = jsonArray.optJSONArray(i);
//                                                    ArrayList<Marker> typeMarkers = new ArrayList<Marker>();
//                                                    for (int j = 0; j < typeFPlaceArr.length(); j++) {
		        try {
//                                                            JSONObject json = jsonArray.optJSONObject(0);
		            JSONObject json = jsonArray.optJSONObject(i);//test
//                                                            double d1 = new Random().nextDouble();
//                                                            d1 = d1 - (int)d1;
//                                                            double d2 = new Random().nextDouble();
//                                                            d2 = d2 - (int)d2;
//                                                            LatLng p = new LatLng(json.optDouble(Const.STA_LAT) + d1, json.optDouble(Const.STA_LNG) + d2);
		            LatLng p = new LatLng(json.optDouble(Const.STA_LAT), json.optDouble(Const.STA_LNG));
		            OverlayOptions ooD = new MarkerOptions().position(p).icon(bdA);
		            Marker mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));
		            mSearchResult.add(mMarkerD);
		            Bundle b = new Bundle();
		            int fPlaceId = json.optInt(Const.STA_ID);
		            b.putString(Const.STA_NAME, json.optString(Const.STA_NAME));
		            b.putDouble(Const.PRI_DISTANCE, DistanceUtil.getDistance(userP, p));
		            b.putInt(Const.STA_ID, fPlaceId);
		            b.putInt(Const.STA_CARE_COUNT, json.optInt(Const.STA_CARE_COUNT, 0));
		            if (json.has(Const.STA_PIRCES)) {
		                b.putString(Const.STA_PIRCES, json.optJSONArray(Const.STA_PIRCES).toString());
		            }
		            String tags = json.optString(Const.STA_TAG);
//                                                            tags = "高钓，冰钓，舒适，黑坑";
		            if(!TextUtils.isEmpty(tags)){
		            	String[] ts = tags.split(",");
		            	int[] i_tags = new int[ts.length];//test
//                                                            	int[] i_tags = new int[1];//test
//                                                            	int random = Math.abs(new Random().nextInt(10));
//                                                            	int tag_i = random % ts.length;
//                                                            	LogUtils.d("random", "rand=" + random + ";length=" + ts.length + ";tag_i=" + tag_i);
		            	for(int tag_i = 0; tag_i < ts.length; tag_i++){//test
		            		String s_tag = ts[tag_i];
		            		i_tags[tag_i] = convertInt(s_tag);//test
//                                                            		i_tags[0] = convertInt(s_tag);//test
		            	}
		            	
		            	b.putIntArray(Const.STA_TAG, i_tags);
		            }
		            mMarkerD.setExtraInfo(b);
//                                                            typeMarkers.add(mMarkerD);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
//                                                    }
		}
	}
}