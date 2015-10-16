package com.go.fish.barcode;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

import com.go.fish.barcode.camera.CameraManager;
import com.go.fish.barcode.decoding.CaptureActivityHandler;
import com.go.fish.barcode.decoding.ICallback;
import com.go.fish.barcode.decoding.InactivityTimer;
import com.go.fish.barcode.view.DetectorViewConfig;
import com.go.fish.barcode.view.ViewfinderView;
import com.go.fish.ui.UICode;
import com.go.fish.util.Const;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

class BarcodeView extends AbsoluteLayout implements Callback,ICallback{
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	SurfaceView surfaceView;
	String mCallbackId = null;
	/**是否处于待扫描状态*/
	private boolean mRunning = false;
	public String errorMsg = null;
	boolean mConserve = false;
	String mFilename = null;
	BarcodeUI mContext;
	static BarcodeView sBarcodeFrameItem = null;
	protected BarcodeView(BarcodeUI pProxy,Rect lp,JSONArray filters) {
		super(pProxy);
		sBarcodeFrameItem = this;
		mContext = pProxy;
//		AbsoluteLayout mainView = new AbsoluteLayout(mAct);//会使得相机预览成像超出div区域,不会变形
//		RelativeLayout mainView = new RelativeLayout(mAct);//相对布局不会超出div区域
//		FrameLayout mainView = new FrameLayout(mAct);//相对布局不会超出div区域
		surfaceView = new SurfaceView(pProxy);
		viewfinderView = new ViewfinderView(pProxy,this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(pProxy);
		CameraManager.init(pProxy.getApplication());
		CameraManager.sScreenWidth = pProxy.getResources().getDisplayMetrics().widthPixels;
		CameraManager.sScreenAllHeight = pProxy.getResources().getDisplayMetrics().heightPixels;
		
		Rect gatherRect = DetectorViewConfig.getInstance().gatherRect;
		
		//获取相机预览分辨率，不应超过采集区域(div)大小，避免超出采集区域，影响到webview其他内容展示
		Point camearResolution = CameraManager.getCR(gatherRect.width(),gatherRect.height());
		
		//计算surfaceView宽高，前提不能超过采集区域(div)大小
		int surfaceViewWidth,surfaceViewHeight;
//		公式 w/h = y/x
		surfaceViewWidth = lp.width();
		surfaceViewHeight = (int)(surfaceViewWidth * camearResolution.x / camearResolution.y);//获得分辨率的宽高比，根据此宽高比计算出适合的surfaceview布局
		int left = 0, top = 0;
		if(lp.top == 0 && surfaceViewHeight > lp.height()){//如果div top为零，则继续使用宽基准
			top = lp.height() - surfaceViewHeight ;
			DetectorViewConfig.detectorRectOffestTop = top;
		}else{//div top值不为0时
			if(surfaceViewHeight > lp.height()){//超出div高，需要使用高基准，调整宽
				surfaceViewHeight = lp.height();
				surfaceViewWidth = surfaceViewHeight * camearResolution.y / camearResolution.x;
			}else{
				top = (lp.height() - surfaceViewHeight)/2;
				DetectorViewConfig.detectorRectOffestTop = top;
			}
			
			if(lp.width() - surfaceViewWidth > 0){
				left = (lp.width() - surfaceViewWidth)/2;
				DetectorViewConfig.detectorRectOffestLeft = left;
			}
		}
		surfaceView.setClickable(false);
		addView(surfaceView,new AbsoluteLayout.LayoutParams(surfaceViewWidth,surfaceViewHeight,left,top));
		DetectorViewConfig.getInstance().initSurfaceViewRect(left,top,surfaceViewWidth,surfaceViewHeight);
		addView(viewfinderView);
		initDecodeFormats(filters);
		onResume(false);//启动预览，绘制探测区域
	}
	
	@Override
	public void autoFocus() {
		handler.autoFocus();
	}
	public void dispose() {
		onPause();
		DetectorViewConfig.clearData();
		mContext.mBarcodeView = null;
		surfaceView = null;
	}
	protected void onResume(boolean isSysEvent){
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
//		decodeFormats = null;
//		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) mContext.getSystemService(Activity.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		if(isSysEvent){//系统事件过来的通知
			if(mRunning){//系统时间过来的时候处于扫描状态
				mRunning = false;//认为设置处于非扫描状态，因为onpause事件可能引起扫描状态改变
				start();
			}
		}
	}
	
	protected void onPause(){
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
		boolean t = mRunning;//保存取消前的扫描状态
		cancel();
		mRunning = t;//恢复扫描状态
	}
	
	protected void start() {
		if(!mRunning){
			getViewfinderView().startUpdateScreenTimer();
			if (handler != null) {
				handler.restartPreviewAndDecode();
			}
			mRunning = true;
		}
	}

	public void setFlash(boolean enable){
		CameraManager.get().setFlashlight(enable);
	}
	protected void cancel() {
		if(mRunning){
			if (handler != null) {
				handler.stopDecode();
			}
			getViewfinderView().stopUpdateScreenTimer();
			mRunning = false;
		}
	}

	protected void onDestroy() {
		inactivityTimer.shutdown();
		hasSurface = false;
		decodeFormats = null;
		characterSet = null;
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			errorMsg = ioe.getMessage();
			return;
		} catch (RuntimeException e) {
			errorMsg = e.getMessage();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
			if (mRunning && handler != null) {//可能start的调用早于此处运行
				handler.restartPreviewAndDecode();
			}
		}else{
			handler.resume(mRunning);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}
	@Override
	public boolean isRunning() {
		return mRunning;
	}
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		 playBeepSoundAndVibrate();
		 boolean saveSuc = false;
		 if(mConserve){
//			 saveSuc = PdrUtil.saveBitmapToFile(barcode, mFilename);
//			 PdrUtil.showNativeAlert(mWebViewImpl.getContext(), "获取的扫描信息", barcode);
		 }
		 int num = convertTypestrToNum(obj.getBarcodeFormat());
//		 String json = null;
//		 if(saveSuc){
//			 String message = "{type:%d,message:%s,file:'%s'}";
//			 json = String.format(message, num,JSONObject.quote(obj.getText()),""); 
//		 }
		 cancel();//start一次只能有一次结果，所以成功之后需要停止
		 Intent data = new Intent();
		 data.putExtra(Const.PRI_QR_RESULT, obj.getText());
		 mContext.setResult(UICode.ResultCode.RESULT_BARCODE_QR, data);
		 mContext.finish();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			
			try {
				AssetFileDescriptor file = mContext.getResources().getAssets().openFd("res/beep.ogg");
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	private int convertTypestrToNum(BarcodeFormat format){
		if(format == BarcodeFormat.QR_CODE){
			return QR;
		}else if(format == BarcodeFormat.EAN_13){
			return EAN13;
		}else if(format == BarcodeFormat.EAN_8){
			return EAN8;
		}else if(format == BarcodeFormat.AZTEC){
			return AZTEC;
		}else if(format == BarcodeFormat.DATA_MATRIX){
			return DATAMATRIX;
		}else if(format == BarcodeFormat.UPC_A){
			return UPCA;
		}else if(format == BarcodeFormat.UPC_E){
			return UPCE;
		}else if(format == BarcodeFormat.CODABAR){
			return CODABAR;
		}else if(format == BarcodeFormat.CODE_39){
			return CODE39;
		}else if(format == BarcodeFormat.CODE_93){
			return CODE93;
		}else if(format == BarcodeFormat.CODE_128){
			return CODE128;
		}else if(format == BarcodeFormat.ITF){
			return ITF;
		}else if(format == BarcodeFormat.MAXICODE){
			return MAXICODE;
		}else if(format == BarcodeFormat.PDF_417){
			return PDF417;
		}else if(format == BarcodeFormat.RSS_14){
			return RSS14;
		}else if(format == BarcodeFormat.RSS_EXPANDED){
			return RSSEXPANDED;
		}
		return UNKOWN;
	}
	
	
	
	private void initDecodeFormats(JSONArray filters){
		decodeFormats = new Vector<BarcodeFormat>();
		if(filters == null || filters.length() == 0){//默认支持
			decodeFormats.add(BarcodeFormat.EAN_13);
			decodeFormats.add(BarcodeFormat.EAN_8);
			decodeFormats.add(BarcodeFormat.QR_CODE);
		}else{
			int size = filters.length();
			for(int i = 0; i < size; i++){
				int filter = -1;
				try {
					filter = filters.getInt(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(filter != -1){
					decodeFormats.add(convertNumToBarcodeFormat(filter));
				}
			}
		}
	}

	private BarcodeFormat convertNumToBarcodeFormat(int num){
		BarcodeFormat _ret = null;
		switch (num) {
		case QR :{
			_ret = BarcodeFormat.QR_CODE;
			break;
		}
	    case EAN13 :{
			_ret = BarcodeFormat.EAN_13;
			break;
		}
	    case EAN8 :{
			_ret = BarcodeFormat.EAN_8;
			break;
		}
	    case AZTEC :{
			_ret = BarcodeFormat.AZTEC;
			break;
		}
	    case DATAMATRIX :{
			_ret = BarcodeFormat.DATA_MATRIX;
			break;
		}
	    case UPCA :{
			_ret = BarcodeFormat.UPC_A;
			break;
		}
	    case UPCE :{
			_ret = BarcodeFormat.UPC_E;
			break;
		}
	    case CODABAR :{
			_ret = BarcodeFormat.CODABAR;
			break;
		}
	    case CODE39 :{
			_ret = BarcodeFormat.CODE_39;
			break;
		}
	    case CODE93 :{
			_ret = BarcodeFormat.CODE_93;
			break;
		}
	    case CODE128 :{
			_ret = BarcodeFormat.CODE_128;
			break;
		}
	    case ITF :{
			_ret = BarcodeFormat.ITF;
			break;
		}
	    case MAXICODE :{
			_ret = BarcodeFormat.MAXICODE;
			break;
		}
	    case PDF417 :{
			_ret = BarcodeFormat.PDF_417;
			break;
		}
	    case RSS14 :{
			_ret = BarcodeFormat.RSS_14;
			break;
		}
	    case RSSEXPANDED :{
			_ret = BarcodeFormat.RSS_EXPANDED;
			break;
		}
		}
		return _ret;
	}
	
	static final int UNKOWN      = -1000;
	static final int QR = 0;
    static final int EAN13 = 1;
    static final int EAN8 = 2;
    static final int AZTEC = 3;
    static final int DATAMATRIX = 4;
    static final int UPCA = 5;
    static final int UPCE = 6;
    static final int CODABAR = 7;
    static final int CODE39 = 8;
    static final int CODE93 = 9;
    static final int CODE128 = 10;
    static final int ITF = 11;
    static final int MAXICODE = 12;
    static final int PDF417 = 13;
    static final int RSS14 = 14;
    static final int RSSEXPANDED = 15;

}
