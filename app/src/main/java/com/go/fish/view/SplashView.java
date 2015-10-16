package com.go.fish.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.go.fish.IOnWelcomedListener;
import com.go.fish.R;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.RegisterUI;
import com.go.fish.util.Const;

public class SplashView {
	Activity mSplashActivity = null;
	IOnWelcomedListener clickListener = null;
	public SplashView(Activity selfActivity,IOnWelcomedListener newActivityClass,final ViewPager viewPager){
		mSplashActivity = selfActivity;
		clickListener = newActivityClass;
		ArrayList<View> viewPagerList = new ArrayList<View>();
		final int size = 4;
		int baseInt = R.drawable.welcome1;
		View lastView = null;
		for(int i = 0; i < size; i++){
			if (i == size - 1){
				ViewGroup rl = (ViewGroup)LayoutInflater.from(selfActivity).inflate(R.layout.wel, null);
				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						switch (id) {
						case R.id.wel_login:
							showLoginUI();
							break;
						case R.id.wel_reg:
							showRegisterUI();
							break;
						}
					}
				};
				rl.findViewById(R.id.wel_login).setOnClickListener(listener);
				rl.findViewById(R.id.wel_reg).setOnClickListener(listener);
				lastView = rl.findViewById(R.id.reg_and_login_btns);
				viewPagerList.add(rl);
			}else{
				FrameLayout fl = new FrameLayout(selfActivity);
				fl.setBackgroundResource(baseInt + i);
				viewPagerList.add(fl);
			}
		}
		final View f_lastView = lastView;
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			boolean showed = false;
			@Override
			public void onPageSelected(int arg0) {
				if(arg0 == size - 1 && !showed){
					viewPager.postDelayed(new Runnable() {
						@Override
						public void run() {
							{
								Animation a = AnimationUtils.loadAnimation(mSplashActivity, R.anim.slide_in_from_bottom);
								f_lastView.setVisibility(View.VISIBLE);
								a.setFillAfter(true);
								f_lastView.setAnimation(a);
								a.start();
								showed = true;
							}
						}
					}, 20);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		viewPager.setAdapter(new MyPagerAdapter(viewPagerList));
		viewPager.setBackgroundColor(0xffff0000);
	}
	
	private void showRegisterUI(){
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_reg);
		i.setClassName(mSplashActivity, RegisterUI.class.getName());
		mSplashActivity.startActivity(i);
		clickListener.onClick();
		mSplashActivity.finish();
	}
	
	private void showLoginUI(){
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_login);
		i.setClassName(mSplashActivity, BaseUI.class.getName());
		mSplashActivity.startActivity(i);
		clickListener.onClick();
		mSplashActivity.finish();
	}
}

