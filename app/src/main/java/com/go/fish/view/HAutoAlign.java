package com.go.fish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.go.fish.R;

/**
 * Created by DCloud on 2015/10/8.
 */
public class HAutoAlign extends HorizontalScrollView {

	public HAutoAlign(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	LinearLayout contentView = null;
	GestureDetector mGestureDetector = null;

	public HAutoAlign(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		contentView = new LinearLayout(context);
		contentView.setOrientation(LinearLayout.HORIZONTAL);
		addView(contentView, new LayoutParams(-1, -1));
		screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		childWidth =  screenWidth * 8 / 10;
		childHeight = childWidth * 9 / 16;
		space = screenWidth - childWidth; 
	}

	int space = 0;
	int childWidth = 0,childHeight = 0;
	int screenWidth = 0;
	
	public void addChild(View child,int maybeCount) {
		childWidth = (maybeCount < 0 ? getChildCount() : maybeCount) > 1 ? childWidth : screenWidth;
		contentView.addView(child, childWidth, childHeight);
	}

	public void updateChild(View child,boolean useScreenWidth){
		ViewGroup.LayoutParams lp = child.getLayoutParams();
		int destWidth = !useScreenWidth ? childWidth : screenWidth;
		if(lp.width != destWidth){
			lp.width = destWidth;
			child.setLayoutParams(lp);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_UP){
			postDelayed(new Runnable() {
				@Override
				public void run() {
					int s = getScrollX() / childWidth;
					int need_scrollX = 0;
					if(s == 0){
						if(getScrollX()  + space > childWidth / 2){
							need_scrollX = childWidth - space / 2;
						}else{
							need_scrollX = 0;
						}
					}else if(s == getChildCount() - 1){//
						need_scrollX = getChildCount() * childWidth - screenWidth;
					}else{
						int ss = getScrollX() % childWidth;
						if(ss >= childWidth / 2){
							s++;
						}
						need_scrollX = s * childWidth - (space / 2);
					}
//					Log.d("yl", "s=" + s + ";need_scrollX=" + need_scrollX + ";scrollX=" + getScrollX());
					smoothScrollTo(need_scrollX, 0);
				}
			}, 100);
		}
//		if(ev.getAction() == MotionEvent.ACTION_MOVE){
//			Log.d("yl", ";scrollX=" + getScrollX() + ";childWidth=" + childWidth);
//		}
		return super.onTouchEvent(ev);
	}
}
