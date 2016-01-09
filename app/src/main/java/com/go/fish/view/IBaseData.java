package com.go.fish.view;

import android.app.Activity;
import android.view.View;


public interface IBaseData {
	void OnClick(Activity activity, IBaseDataHandledCallback handledCallback, View attachedView);
	
	public static interface IBaseDataHandledCallback{
		void onHandledCallback();
	}
}
