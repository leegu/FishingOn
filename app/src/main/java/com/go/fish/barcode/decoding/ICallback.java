package com.go.fish.barcode.decoding;

import android.graphics.Bitmap;
import android.os.Handler;

import com.go.fish.barcode.view.ViewfinderView;
import com.google.zxing.Result;

public interface ICallback {
	Handler getHandler();
	ViewfinderView getViewfinderView();
	void autoFocus();
	void handleDecode(Result obj, Bitmap barcode);
	void drawViewfinder();
	boolean isRunning();
}
