package com.go.fish.barcode;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.go.fish.R;
import com.go.fish.barcode.decoding.CaptureActivityHandler;
import com.go.fish.barcode.view.DetectorViewConfig;
import com.go.fish.ui.BaseUI;
import com.go.fish.util.Const;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

public class BarcodeUI extends BaseUI {
	
	public static boolean save =false;
	public static Context context = null;
	BarcodeView mBarcodeView = null;
	boolean mIsRegisetedSysEvent = false;
	
	FrameLayout rootView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_barcode);
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
		try {

			if (TextUtils.isEmpty(text)) {
				return null;
			}
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
//						pixels[y * QR_WIDTH + x] = 0xff000000;
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffFFff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
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
	protected void onPause() {
		super.onPause();
		if(mBarcodeView != null)
			mBarcodeView.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(mBarcodeView != null)
			mBarcodeView.onResume(true);
	}
}