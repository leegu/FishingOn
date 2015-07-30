package com.go.fish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.view.SplashView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences sp = getSharedPreferences("welcome", MODE_PRIVATE);
		boolean welcome_ed = sp.getBoolean("welcome_ed", false);
		if(welcome_ed && isLogined()){
			showHomeUI();
		}else{
			setContentView(R.layout.activity_splash);
			ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
			new SplashView(this,new IOnWelcomedListener(){
				@Override
				public void onClick() {
					Editor edit = sp.edit();
					edit.putBoolean("welcome_ed", true);
					edit.commit();
				}
				
			},vp);
		}
	}
	
	private boolean isLogined(){
		return false;
	}
	private void showHomeUI(){
		Intent intent = new Intent();
		intent.putExtra(BaseUI.LAYOUT_ID, R.layout.main);
		intent.setClass(MainActivity.this, HomeUI.class);
		startActivity(intent);
		finish();
	}
}
