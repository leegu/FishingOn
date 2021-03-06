package com.go.fish;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.go.fish.data.PersonData;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.LogUtils;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.SplashView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//0ByhEGDFPEuV7VOvfm8ih0HS
		boolean welcome_ed = Boolean.parseBoolean(LocalMgr.self().getUserInfo(Const.K_Welcomed));
		if(testView()){
			return;
		}
		LogUtils.d("yl", "MainActivity welcome_ed=" + welcome_ed);
		if(welcome_ed){
			if(isLogined()){
				showHomeUI();
				if(true){
					return;
				}
				//TODO刷新登陆
				//成功进入首页面
				final String num = LocalMgr.self().getUserInfo(Const.K_num);
				String pswd = LocalMgr.self().getUserInfo(Const.K_pswd);
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put(Const.STA_MOBILE, num);
					jsonObject.put(Const.STA_PASSWORD, pswd);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				NetTool.data().http(new NetTool.RequestListener() {
					@Override
					public void onEnd(byte[] bytes) {
						JSONObject response = toJSONObject(bytes);
						if (response != null ){
							if(isRight(response)) {
								JSONObject data = response.optJSONObject(Const.STA_DATA);
								LocalMgr.self().saveUserInfo(Const.K_LoginData, data.toString());
								User.self().userInfo = PersonData.updatePerson(User.self().userInfo,data.optJSONObject(Const.STA_MEMBER));
								User.self().userInfo.mobileNum = num;
								showHomeUI();
							} else {
								showLoginUI();
							}
						}else{
							showLoginUI();
						}
					}
				},jsonObject,UrlUtils.self().getMemberLogin());
			}else{
				showLoginUI();
			}
		}else{
			setContentView(R.layout.ui_activity_splash);
			ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
			new SplashView(this,new IOnWelcomedListener(){
				@Override
				public void onClick() {
					LogUtils.d("yl", "MainActivity saveUserInfo welcome_ed");
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
//		Intent intent = new Intent();
//		intent.putExtra(Const.PRI_EXTRA_IMAGE_INDEX, 0);
//		String[] urls = new String[]{"http://i0.letvimg.com/lc05_isvrs/201601/16/11/52/12206bf7-1f49-488c-92f2-fb843b625bbe.jpg","http://i2.letvimg.com/lc03_iscms/201601/13/22/20/6aeb875f751e4f5f93dcb6b2d720f38f.jpg"};
//		intent.putExtra(Const.PRI_EXTRA_IMAGE_URLS, urls);
//		UIMgr.showActivity(this,intent,ImageViewUI.class.getName());
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
					UrlUtils.self().setToken(data.optString(Const.STA_TOKEN));
					User.self().userInfo.photoUrl = LocalMgr.self().getUserInfo(Const.K_photo_url);
					User.self().userInfo.mobileNum = LocalMgr.self().getUserInfo(Const.K_num);
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
