package com.go.fish.ui;

import android.app.Activity;
import android.content.Intent;

public class UIMgr {

	public static void showActivity(Activity act, int layoutId) {
		Intent i = new Intent();
		i.putExtra("layout_id", layoutId);
		showActivity(act, i);
	}
	public static void showActivity(Activity act, int layoutId,String className) {
		Intent i = new Intent();
		i.putExtra("layout_id", layoutId);
		showActivity(act, i,className);
	}
	public static void showActivity(Activity act, Intent intent){
		showActivity(act,intent, BaseUI.class.getName());
	}
	public static void showActivity(Activity act,Intent intent, String className){
		intent.setClassName(act, className);
		act.startActivity(intent);
	}
}
