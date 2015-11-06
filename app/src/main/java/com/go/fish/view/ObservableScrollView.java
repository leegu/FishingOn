package com.go.fish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

	ScrollViewListener mScrollViewListener = null;
	public ScrollViewListener getScrollViewListener() {
		return mScrollViewListener;
	}

	public void setScrollViewListener(ScrollViewListener mScrollViewListener) {
		this.mScrollViewListener = mScrollViewListener;
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(this.mScrollViewListener != null && getScrollY() > this.mScrollViewListener.ovn){
			this.mScrollViewListener.onScroll(l, t, oldl, oldt);
		}
	}
	
	public static abstract class ScrollViewListener {
		
		int ovn = 0;
		public ScrollViewListener(int overValueNotice){
			ovn = overValueNotice;
		}
		public ScrollViewListener(){
			this(0);
		}
		
		abstract public void onScroll(int l, int t, int oldl, int oldt);
	}
}
