package com.go.fish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.view.ReplaceFragment;
import com.go.fish.view.Switcher;

public class RegisterUI extends BaseUI {

	FragmentManager fragmentMgr = null;
	boolean showRegNext = false;
	Fragment regNextFragment = null,reg1Fragment = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
//		setContentView(layout_id);
		fragmentMgr = getSupportFragmentManager();
		{
			reg1Fragment = new ReplaceFragment();
			Bundle b = new Bundle();
			b.putInt(Const.LAYOUT_ID, R.layout.ui_reg_first);
			reg1Fragment.setArguments(b);
			replace(reg1Fragment);
		}
		{
			regNextFragment = new ReplaceFragment();
			Bundle b = new Bundle();
			b.putInt(Const.LAYOUT_ID, R.layout.ui_my_sec);
			regNextFragment.setArguments(b);
		}
	}

	@Override
	public void onBackPressed() {
		if(showRegNext){
			replace(reg1Fragment);
			showRegNext = false;
		}else{
			super.onBackPressed();
		}
	}
	
	private void replace(Fragment f){
		FragmentTransaction ft = fragmentMgr.beginTransaction();
//		ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom,R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
//		ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_left_exit,
//                R.animator.fragment_slide_right_enter,
//                R.animator.fragment_slide_right_exit);
		
		ft.replace(R.id.reg_fragment,f);
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		ft.commit();
	}
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.reg_get_check_code:
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("10086", null, "cxyl", null, null);
			break;
		case R.id.reg_agree_rule: {
			TextView tv = ((TextView) view);
			Button nextBtn = (Button) findViewById(R.id.reg_next);
			if (tv.getText().equals("√")) {
				tv.setText("");
				nextBtn.setClickable(false);
				nextBtn.setBackgroundResource(R.drawable.gray_shape);
			} else {
				tv.setText("√");
				nextBtn.setClickable(true);
				nextBtn.setBackgroundResource(R.drawable.green_shape);
			}
			break;
		}
		case R.id.reg_next: {
			showRegNext = true;
			replace(regNextFragment);
//			showActivity(R.layout.reg_next);
			break;
		}
		case R.id.reg_agree_rule_text: {
			Intent i = new Intent();
			i.putExtra("layout_id", R.layout.ui_rule);
			i.putExtra("url", getResources().getString(R.string.rule_link));
			showActivity(i);
			break;
		}
		case R.id.reg_save_pswd: {
			Switcher switcher = (Switcher) findViewById(R.id.reg_save_pswd);
			switcher.change();
			break;
		}
		case R.id.reg_next_skip_btn: {
			Intent i = new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("layout_id", R.layout.ui_main);
			showActivity(i, HomeUI.class.getName());
			 finish();
			break;
		}
		case R.id.reg_next_get_location_btn: {
			MapUtil.getLocation(this, new OnGetLocationListener() {
				@Override
				public void onGetLocation(LocationData data) {
					TextView tv = (TextView) findViewById(R.id.reg_next_location_input);
					tv.setText(data.address);
				}
			}, 0);
			break;
		}
		}
	}

	public void showActivity(int layoutId) {
		Intent i = new Intent();
		i.putExtra("layout_id", layoutId);
		showActivity(i);
	}

	public void showActivity(Intent intent) {
		showActivity(intent, BaseUI.class.getName());
	}

	public void showActivity(Intent intent, String className) {
		intent.setClassName(this, className);
		startActivity(intent);
	}
}
