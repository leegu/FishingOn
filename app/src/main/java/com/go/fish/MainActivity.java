package com.go.fish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.view.SplashView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//0ByhEGDFPEuV7VOvfm8ih0HS
		boolean welcome_ed = Boolean.parseBoolean(LocalMgr.self().getUserInfo(Const.K_Welcomed));
		if(testView()){
			return;
		}
		if(welcome_ed){
			if(isLogined()){
				showHomeUI();
			}else{
				showLoginUI();
			}
		}else{
			setContentView(R.layout.ui_activity_splash);
			ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
			new SplashView(this,new IOnWelcomedListener(){
				@Override
				public void onClick() {
					LocalMgr.self().saveUserInfo(Const.K_Welcomed,"true");
				}
			},vp);
		}
	}

	private boolean testView(){
//		Intent intent = new Intent();
//		intent.setClass(MainActivity.this, TestActivity.class);
//		startActivity(intent);
//		finish();
//		return true;
		return false;
	}
	private boolean isLogined(){
		String userInfo = LocalMgr.self().getUserInfo(Const.K_LoginData);
		if(!TextUtils.isEmpty(userInfo)){
			try {
				JSONObject data = new JSONObject(userInfo);
				if (data != null && data.has(Const.STA_TOKEN)){
                    return true;
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	private void showHomeUI(){
		Intent intent = new Intent();
		intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_main);
		intent.setClass(MainActivity.this, HomeUI.class);
//		intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.tab_custome);
//		intent.setClass(MainActivity.this, BaseUI.class);
		startActivity(intent);
		finish();
	}
	private void showLoginUI(){
		Intent intent = new Intent();
		intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_login);
		intent.setClass(MainActivity.this, BaseUI.class);
		startActivity(intent);
		finish();
	}
}
