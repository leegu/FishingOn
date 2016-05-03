package com.go.fish;

import java.util.ArrayList;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.go.fish.util.ImageLoaderUtil;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.UrlUtils;

public class MainApplication extends Application {
	private static MainApplication instance = null;
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public ArrayList<OnGetLocationListener> mOnGetLocationListeners = new ArrayList<OnGetLocationListener>();
    public TextView mLocationResult,logMsg;
    public TextView trigger,exit;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
     // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
 		SDKInitializer.initialize(this);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        LocalMgr.initEnv(this);
        UrlUtils.initEnv(this);
        ImageLoaderUtil.initImageLoader(this);
//        String umkey = "5627058667e58e29b9002f81";
//		AnalyticsConfig.setAppkey(this, umkey);
//		AnalyticsConfig.setChannel(getPackageName().replace(".", "_"));
//		
//        PushManager.getInstance().initialize(this);
//        LocalServer server = new LocalServer(this, null);
//		server.start();
    }
    
    


    public static MainApplication getInstance() {
		return instance;
	}


	public static void setInstance(MainApplication instance) {
		MainApplication.instance = instance;
	}
	
	public boolean isStarted(){
		return mLocationClient != null ? mLocationClient.isStarted() : false;
	}
	
	public void start(){
		mHandler.sendEmptyMessage(START_OP);
	}
	
	public void stop(){
		mHandler.sendEmptyMessage(STOP_OP);
	}

	public synchronized void addLocListener(OnGetLocationListener listener){
		mOnGetLocationListeners.add(listener);
	}
	final int START_OP = 0;
	final int STOP_OP = 1;
	final int UPDATE_OP = 2;
	Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START_OP:
				initLocation();
				if(!mLocationClient.isStarted()){
					mLocationClient.registerLocationListener(mMyLocationListener);
					mLocationClient.start();
				}
				break;
			case STOP_OP:
				if(mLocationClient.isStarted()){
					mLocationClient.stop();
				}
				break;
			case UPDATE_OP:
				BDLocation location = (BDLocation)msg.obj;
				for(OnGetLocationListener mOnGetLocationListener : mOnGetLocationListeners){
					LocationData ld = new LocationData();
					ld.address = location.getAddrStr();
					ld.lng = location.getLongitude();
					ld.lat = location.getLatitude();
					mOnGetLocationListener.onGetLocation(ld);
				}
				break;
			default:
				break;
			}
		}
	};

	private void initLocation(){
		if(mLocationClient == null){
			mLocationClient = new LocationClient(this.getApplicationContext());
	        mMyLocationListener = new MyLocationListener();
		}
		LocationClientOption option = new LocationClientOption();
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		}else{
			
		}
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        mLocationClient.setLocOption(option);
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
	}
	/**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
        	Log.d("application", "MyLocationListener onReceiveLocation " + location.getLocType());
        	Message m = Message.obtain();
        	m.what = UPDATE_OP;
        	m.obj = location;
        	mHandler.sendMessage(m);
            //Receive Location
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append("\ndescribe : ");
//                sb.append("gps定位成功");
//
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//                sb.append("\ndescribe : ");
//                sb.append("网络定位成功");
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                sb.append("\ndescribe : ");
//                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                sb.append("\ndescribe : ");
//                sb.append("网络不同导致定位失败，请检查网络是否通畅");
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                sb.append("\ndescribe : ");
//                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//            }
//            sb.append("\nlocationdescribe : ");// 位置语义化信息
//            logMsg(sb.toString());
//            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }


    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
