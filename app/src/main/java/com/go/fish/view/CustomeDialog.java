package com.go.fish.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CustomeDialog extends Dialog {

	View rootView = null;
	public CustomeDialog(Context context, int layoutId) {
		super(context/*, R.style.Dialog*/);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		lp.dimAmount=0.5f;
		getWindow().setAttributes(lp);
		rootView = LayoutInflater.from(context).inflate(layoutId, null);
//		view.setBackgroundColor(Color.RED);
		setContentView(rootView);
	}

	public View getRootView(){
		return rootView;
	}
}
