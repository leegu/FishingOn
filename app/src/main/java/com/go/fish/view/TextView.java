package com.go.fish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TextView extends android.widget.TextView{


	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int a = event.getAction();
		if(a == MotionEvent.ACTION_DOWN){
			setBackgroundColor(0xFFCCCCCC);
		}else if(a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_OUTSIDE){
			setBackgroundColor(0xFFEEEEEE);
		}
//		if(a == MotionEvent.ACTION_DOWN){
//			setBackgroundResource(RInformation.DRAWABLE_DCLOUD_DIALOG_BUTTON_PRESS_SHAPE);
//		}else if(a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_OUTSIDE){
//			setBackgroundResource(RInformation.DRAWABLE_DCLOUD_DIALOG_BUTTON_SHAPE);
//		}
		return super.onTouchEvent(event);
	}
}
