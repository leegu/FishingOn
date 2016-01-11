package com.go.fish.barcode;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.go.fish.R;
import com.go.fish.barcode.decoding.CaptureActivityHandler;
import com.go.fish.barcode.view.DetectorViewConfig;
import com.go.fish.ui.BaseUI;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class BarcodeUI extends BaseUI {
	
	public static boolean save =false;
	public static Context context = null;
	BarcodeView mBarcodeView = null;
	boolean mIsRegisetedSysEvent = false;
	
	FrameLayout rootView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.ui_barcode);
		rootView = (FrameLayout)findViewById(R.id.barcode_rootview);
		Intent i = getIntent();
		if (i.hasExtra(Const.PRI_TO_QR_CONTENT)) {
			String content = i.getStringExtra(Const.PRI_TO_QR_CONTENT);
			try {
				Bitmap bitmap = createImage(content);
				ImageView iv = new ImageView(this);
				if(bitmap != null){
					iv.setImageBitmap(bitmap);
				}
				iv.setBackgroundColor(0xffffFFff);
				rootView.addView(iv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			rootView.setBackgroundColor(Color.BLACK);
			rootView.postDelayed(new Runnable() {
				@Override
				public void run() {
					initBarcodeView();
					start(false);
				}
			}, 300);
		}
	}
	int QR_WIDTH = 200;
	int QR_HEIGHT = 200;
	// 生成QR图
	private Bitmap createImage(String text) {
		String filePath = LocalMgr.sRootPath + System.currentTimeMillis();
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		QREncoder.createQRImage(text, QR_WIDTH, QR_HEIGHT, icon, filePath);
		
		return LocalMgr.self().getLowBitmap(filePath);
//		return QREncoder.encoder(text, QR_WIDTH, QR_HEIGHT);
	}
	void start(boolean _conserve){
		mBarcodeView.mConserve = _conserve;
		mBarcodeView.start();
	}
	
	void cancel(){
		mBarcodeView.cancel();
	}
	
	
	void setFlash(boolean flash){
		mBarcodeView.setFlash(flash);
	}
	
	void initBarcodeView(){
		Rect dvc = DetectorViewConfig.getInstance().gatherRect;
		dvc.left = 0; 
		dvc.top = 0;
		dvc.right = dvc.left + getResources().getDisplayMetrics().widthPixels;
		dvc.bottom = dvc.top + getResources().getDisplayMetrics().heightPixels;
		JSONArray filters = new JSONArray();
		try {
			filters.put(0, BarcodeFormat.QR_CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//创建barcode系统控件
		mBarcodeView = new BarcodeView(this,dvc,filters);
		rootView.addView(mBarcodeView);
	}
	
	void scanImage(String path){
		Bitmap map = BitmapFactory.decodeFile(path);
		Result result = CaptureActivityHandler.decode(map);
		if(result != null){
			String message = "{type:'%s',message:'%s',file:'%s'}";
			 message = String.format(message, result.getBarcodeFormat().toString(),result.getText(),path); 
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBarcodeView != null){
			mBarcodeView.onDestroy();
			mBarcodeView = null;
		}
		mIsRegisetedSysEvent = false;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mBarcodeView != null)
			mBarcodeView.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
		if(mBarcodeView != null)
			mBarcodeView.onResume(true);
	}
}