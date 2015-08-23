package com.go.fish.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.go.fish.R;
import com.go.fish.util.Const;

public class BaseUI extends FragmentActivity implements IHasHeadBar,IHasTag {

	// BaseUIProxy proxy = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
		if(layout_id != 0) setContentView(layout_id);
		switch (layout_id) {
		case R.layout.ui_rule:
			WebView wb = (WebView) findViewById(R.id.base_ui_webview);
			initWebview(wb);
			wb.loadUrl("http://www.baidu.com");
			break;
		default:
			break;
		}
	}

	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.base_ui_close: {
			finish();
			break;
		}
		case R.id.base_ui_help: {
			Intent i = new Intent();
			i.putExtra(Const.LAYOUT_ID, R.layout.ui_rule);
			i.putExtra(Const.URL, getResources().getString(R.string.help_link));
			UIMgr.showActivity(BaseUI.this, i);
			break;
		}
		}
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		

		case R.id.login_pswd_forget: {
			UIMgr.showActivity(BaseUI.this, R.layout.ui_forget_pswd);
			break;
		}
		case R.id.login_login_btn: {
			Intent i = new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra(Const.LAYOUT_ID, R.layout.ui_main);
			UIMgr.showActivity(BaseUI.this, i, HomeUI.class.getName());
			finish();
			break;
		}
		case R.id.login_reg_new_account_btn: {
			UIMgr.showActivity(BaseUI.this, R.layout.ui_reg);
			break;
		}
		}

	}

	private void initWebview(WebView view) {
		WebSettings setting = view.getSettings();
		view.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
		setting.setJavaScriptEnabled(true);
	}

	@Override
	public void onTagClick(View view) {

	}
}
