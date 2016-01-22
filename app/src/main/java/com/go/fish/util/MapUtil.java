package com.go.fish.util;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.go.fish.MainApplication;
import com.go.fish.user.User;

public class MapUtil {

	private static String DISTANCE_KM = "距您%s公里";
	private static String DISTANCE_M = "距您%s米";
	/**
	 * 获取距离
	 * @param d_value 单位米
	 * @return
	 */
	public static String getDistance(double d_value){
		if(d_value >= 10000){
			int s = (int)(d_value / 1000);
			int e = (int)(d_value % 1000) % 100;
			return String.format(DISTANCE_KM,  + s + (e > 0 ? "." + e : ""));
		}else{
			return String.format(DISTANCE_M, (int)d_value + "");
		}
	}
	public static double getDistanceDoubleValue(double d_lat,double d_lng){
		LatLng p = new LatLng(d_lat, d_lng);
		LatLng p1 = new LatLng(User.self().userInfo.lat,User.self().userInfo.lng);
		return DistanceUtil.getDistance(p, p1);
	}
	public static String getDistance(double d_lat,double d_lng){
		LatLng p = new LatLng(d_lat, d_lng);
		LatLng p1 = new LatLng(User.self().userInfo.lat,User.self().userInfo.lng);
		return getDistance(DistanceUtil.getDistance(p, p1));
	}
	public static MapView newMap(Context context){
		BaiduMapOptions bmo = new BaiduMapOptions();
		bmo.scaleControlEnabled(false); 
		bmo.zoomControlsEnabled(false);
		MapView mapView = new MapView(context, bmo);
		mapView.showZoomControls(false);
		mapView.getMap().getUiSettings().setCompassEnabled(true);
		return mapView;
	}
	/**
	 * 
	 * @param context
	 * @param listener
	 * @param span 单位毫米
	 */
	public static void getLocation(Activity context,OnGetLocationListener listener,int span){
		MainApplication app = (MainApplication)context.getApplication();
		app.addLocListener(listener);
		if(!app.isStarted()){
			app.start();
		}
	}
	
	public static void stopLocation(Activity context){
		MainApplication app = (MainApplication)context.getApplication();
		app.stop();
	}
	public static interface OnGetLocationListener{
		void onGetLocation(LocationData data);
	}
	
	public static class LocationData{
		public String address;
		public double lng,lat;
	}
}
