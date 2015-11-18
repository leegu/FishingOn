package com.go.fish.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.ReplaceFragment;
import com.go.fish.view.ViewHelper;

public class RegisterUI extends BaseUI {

	FragmentManager fragmentMgr = null;
	boolean showRegNext = false;
	Fragment regNextFragment = null, reg1Fragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
		// setContentView(layout_id);
		fragmentMgr = getSupportFragmentManager();
		{
			reg1Fragment = new ReplaceFragment();
			Bundle b = new Bundle();
			b.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_reg_first);
			reg1Fragment.setArguments(b);
			replace(reg1Fragment);
		}
		{
			regNextFragment = new ReplaceFragment();
			Bundle b = new Bundle();
			b.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_my_sec);
			regNextFragment.setArguments(b);
		}
	}

	@Override
	public void onBackPressed() {
		if (showRegNext) {
			replace(reg1Fragment);
			showRegNext = false;
		} else {
			super.onBackPressed();
		}
	}

	private void replace(Fragment f) {
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_from_bottom,
		// R.anim.slide_out_to_bottom,R.anim.slide_in_from_bottom,
		// R.anim.slide_out_to_bottom);
		// ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
		// R.animator.fragment_slide_left_exit,
		// R.animator.fragment_slide_right_enter,
		// R.animator.fragment_slide_right_exit);

		ft.replace(R.id.reg_fragment, f);
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		ft.commit();
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.reg_next: {
			String num = ((TextView) findViewById(R.id.text_phone_num_input))
					.getText().toString();
			if (TextUtils.isEmpty(num) || num.length() != 11) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_PHONE_NUMBER);
				return;
			}
			String login_pswd_input = ((TextView) findViewById(R.id.login_pswd_input))
					.getText().toString();
			if (TextUtils.isEmpty(login_pswd_input)) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_PSWD);
				return;
			}
			String checkCode = ((TextView) findViewById(R.id.checkCode))
					.getText().toString();
			if (TextUtils.isEmpty(checkCode)) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_CHECK_CODE);
				return;
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_MOBILE, num);
				jsonObject.put(Const.STA_PASSWORD, login_pswd_input);
				jsonObject.put(Const.STA_VALIDATECODE, checkCode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(RegisterUI.this, null, Const.DEFT_GETTING);
				}
				@Override
				public void onEnd(byte[] bytes) {
					ViewHelper.closeGlobalWaiting();
					JSONObject response = toJSONObject(bytes);
					if(response == null){
						try {
							response = new JSONObject("{\"code\":1,\"isError\":false,\"message\":\"success\",\"remark\":\"\",\"data\":{\"token\":\"ASDFCGUDHJFJHSGKH151230\"},\"unlogin\":\"\",\"exception\":\"\",\"error\":\"\"}");
						} catch (Exception e) {
						}
					}
					if (response != null ){
						if( response.has(Const.STA_CODE)
							&& response.optString(Const.STA_CODE).equals(Const.DEFT_1)) {
							JSONObject data = response.optJSONObject(Const.STA_DATA);
							LocalMgr.self().saveUserInfo(Const.K_LoginData, data.toString());
							User.self().userInfo = PersonData.newInstance(data);
							showRegNext = true;
							replace(regNextFragment);
						} else {
							ViewHelper.showToast(RegisterUI.this, response.optString(Const.STA_MESSAGE));
						}
					}else{
						ViewHelper.showToast(RegisterUI.this, Const.DEFT_NET_ERROR);
					}
				}
			},jsonObject,UrlUtils.self().getRegisterMember());
//			 showRegNext = true;
//			 replace(regNextFragment);
			break;
		}
		case R.id.reg_agree_rule: {
			TextView tv = ((TextView) view);
			Button nextBtn = (Button) findViewById(R.id.reg_next);
			if (tv.getText().equals("√")) {
				tv.setText("");
				nextBtn.setClickable(false);
			} else {
				tv.setText("√");
				nextBtn.setClickable(true);
			}
			break;
		}
		case R.id.reg_agree_rule_text: {
			Intent i = new Intent();
			i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_webview_left_close);
			i.putExtra(Const.STA_URL,
					getResources().getString(R.string.rule_link));
			i.putExtra(Const.STA_TITLE,
					getResources().getString(R.string.reg_reg_and_rule));
			UIMgr.showActivity(this, i);
			break;
		}

		case R.id.reg_next_skip: {
			Intent i = new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_main);
			UIMgr.showActivity(this, i, HomeUI.class.getName());
			finish();
			break;
		}
		default:
			super.onClick(view);
		}
	}

	private boolean canDoNext() {
		return true;
	}
}
