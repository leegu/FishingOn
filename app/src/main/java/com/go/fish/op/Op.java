package com.go.fish.op;

import android.app.Activity;
import android.view.View;

import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.IBaseData;

abstract class Op {

	public static View findViewById(Activity activity,int id){
		return activity.findViewById(id);
	}
	public static OnBaseDataClickListener getDefault(final Activity activity){
		return new OnBaseDataClickListener() {
			
			@Override
			public void onItemClick(View view, IBaseData data) {
				data.OnClick(activity, null, view);
			}
		};
	}
}
