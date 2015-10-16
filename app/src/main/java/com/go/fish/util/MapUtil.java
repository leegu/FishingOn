package com.go.fish.util;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.go.fish.MainApplication;

public class MapUtil {

	private static String DISTANCE_KM = "距您%s公里";
	private static String DISTANCE_M = "距您%s米";
	public static String getDistance(double d_value){
		if(d_value >= 10000){
			int s = (int)(d_value / 1000);
			int e = (int)(d_value % 1000) % 100;
			return String.format(DISTANCE_KM,  + s + (e > 0 ? "." + e : ""));
		}else{
			return String.format(DISTANCE_M, (int)d_value + "");
		}
	}
	public static MapView newMap(Context context){
		BaiduMapOptions bmo = new BaiduMapOptions();
		bmo.scaleControlEnabled(false); 
		bmo.zoomControlsEnabled(false);
		MapView mapView = new MapView(context, bmo);
		mapView.showZoomControls(false);
//		mapView.getMap().getUiSettings().setCompassEnabled(false);
		return mapView;
	}
	public static void getLocation(Activity context,OnGetLocationListener listener){
		getLocation(context,listener,0);
	}
	public static void getLocation(Activity context,OnGetLocationListener listener,int span){
		MainApplication app = (MainApplication)context.getApplication();
		app.mOnGetLocationListener = listener;
		LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
//        int span=1000;
//        try {
//            span = Integer.valueOf(frequence.getText().toString());
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        app.mLocationClient.setLocOption(option);
		app.mLocationClient.start();
	}
	
	public static interface OnGetLocationListener{
		void onGetLocation(LocationData data);
	}
	
	public static class LocationData{
		public String address;
		public double lng,lat;
	}
}
