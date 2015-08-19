package com.go.fish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.go.fish.ui.HomeUI;
import com.go.fish.util.Const;
import com.go.fish.view.SplashView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//0ByhEGDFPEuV7VOvfm8ih0HS
		final SharedPreferences sp = getSharedPreferences("welcome", MODE_PRIVATE);
		boolean welcome_ed = sp.getBoolean("welcome_ed", false);
		if(welcome_ed && isLogined()){
			showHomeUI();
		}else{
			setContentView(R.layout.ui_activity_splash);
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
		return true;
	}
	private void showHomeUI(){
		Intent intent = new Intent();
		intent.putExtra(Const.LAYOUT_ID, R.layout.main);
		intent.setClass(MainActivity.this, HomeUI.class);
		startActivity(intent);
		finish();
	}
}
