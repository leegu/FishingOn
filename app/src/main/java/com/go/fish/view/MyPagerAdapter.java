package com.go.fish.view;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter<E> extends PagerAdapter {

	private ArrayList<?> views;
	MyPagerAdapter(ArrayList<? extends E> pviews){
		views = pviews;
	}

	@Override
	public int getCount() {
		return this.views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView((View)views.get(position));
	}
	
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView((View)views.get(position));
		return views.get(position);
	}
}
