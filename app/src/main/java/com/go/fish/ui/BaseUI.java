package com.go.fish.ui;

import com.go.fish.R;
import com.go.fish.util.Location;
import com.go.fish.util.Location.LocationData;
import com.go.fish.util.Location.OnGetLocationListener;
import com.go.fish.view.Switcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BaseUI extends Activity {

//	BaseUIProxy proxy = null;
	public static final String LAYOUT_ID = "layout_id";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(LAYOUT_ID, 0); 
		setContentView(layout_id);
		switch (layout_id) {
		case R.layout.rule:
			WebView wb = (WebView)findViewById(R.id.base_ui_webview);
			initWebview(wb);
			wb.loadUrl("http://www.baidu.com");
			break;
		default:
			break;
		}
	}
	
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.base_ui_help:{
			Intent i = new Intent();
			i.putExtra("layout_id", R.layout.rule);
			i.putExtra("url", getResources().getString(R.string.help_link));
			showActivity(i);
			break;
		}
		case R.id.base_ui_close:{
			finish();
			break;
		}
		case R.id.login_pswd_forget:{
			showActivity(R.layout.forget_pswd);
			break;
		}
		case R.id.login_login_btn:{
			Intent i = new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("layout_id", R.layout.main);
			showActivity(i,HomeUI.class.getName());
			finish();
			break;
		}
		case R.id.login_reg_new_account_btn:{
			showActivity(R.layout.reg);
			break;
		}
		}
	
	}
	private void initWebview(WebView view){
		WebSettings setting = view.getSettings();
		view.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
		setting.setJavaScriptEnabled(true);
	}
	
	public void showActivity(int layoutId) {
		Intent i = new Intent();
		i.putExtra("layout_id", layoutId);
		showActivity(i);
	}

	public void showActivity(Intent intent){
		showActivity(intent,BaseUI.class.getName());
	}
	public void showActivity(Intent intent,String className){
		intent.setClassName(this, className);
		startActivity(intent);
	}
}
