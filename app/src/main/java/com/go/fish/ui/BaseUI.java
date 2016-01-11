package com.go.fish.ui;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.go.fish.R;
import com.go.fish.data.CommentData;
import com.go.fish.data.FieldData;
import com.go.fish.data.FishingNewsData;
import com.go.fish.data.PersonData;
import com.go.fish.op.CommentUIOp;
import com.go.fish.op.FieldUIOp;
import com.go.fish.op.FishingNewsUIOp;
import com.go.fish.op.PodCastUIOp;
import com.go.fish.op.UserUIOp;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.ui.pics.GalleryUtils;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.IME;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.ActionSheet;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

public class BaseUI extends FragmentActivity implements IHasHeadBar, IHasTag,
		IHasComment,OnBaseDataClickListener {

	int mRootViewLayoutId = -1;
	ViewGroup mRootView = null;

	public void onPreCreate(Bundle savedInstanceState) {
	}

	public void onCreate_Super(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate_Super(savedInstanceState);
		onPreCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(Const.PRI_LAYOUT_ID, 0);
		if (layout_id != 0) {
			mRootViewLayoutId = layout_id;
			setContentView(layout_id);
		}
		switch (layout_id) {
		case R.layout.ui_detail_podcast:
			PodCastUIOp.onCreatePodCastDetail(this, getIntent().getExtras());
			break;
		case R.layout.ui_detail_fishing_news:
			FishingNewsUIOp.onCreateFishingNewsDetail(this);
			break;
		case R.layout.ui_webview_left_close:
			onCreateWebview();
			break;
		case R.layout.ui_podcast_person:
			PodCastUIOp.onCreatePodCastListView(this);
			break;
		case R.layout.ui_near_fplace:
			onCreateNearFPlace();
			break;
		case R.layout.ui_near_friends:
			UserUIOp.onCreateNearFriend(this);
			break;
		case R.layout.ui_my_care:
			UserUIOp.onCreateUserCare(this);
			break;
		case R.layout.ui_comment_publish:
			CommentUIOp.onCreateCommentPublish(this);
			break;
		case R.layout.ui_comment_list:
			CommentUIOp.onCreateCommentList(this);
			break;
		case R.layout.ui_zan:
			onCreateZanList();
			break;
		case R.layout.ui_about:
			try {
				TextView ver = (TextView) findViewById(R.id.ui_about_version);
				PackageManager pm = getPackageManager();
				PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
				String s_ver = pi.versionName;
				if (!s_ver.startsWith("v")) {
					s_ver = "v" + s_ver;
				}
				ver.setText(s_ver);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case R.layout.ui_my_sec:
			TextView reg_next_phone_num = (TextView) findViewById(R.id.reg_next_phone_num);
			reg_next_phone_num
					.setText(BaseUtils.formatPhoneNum(User.self().userInfo.mobileNum));
			TextView reg_next_account = (TextView) findViewById(R.id.reg_next_account);
			reg_next_account.setText(User.self().userInfo.id);
			if (getIntent().hasExtra(Const.PRI_REG_OP)
					&& getIntent().getBooleanExtra(Const.PRI_REG_OP, false)) {
				// 注册进入

			} else {
				if (!TextUtils.isEmpty(User.self().userInfo.fYears)) {
					TextView reg_next_fishing_years_spinner = (TextView) findViewById(R.id.reg_next_fishing_years_spinner);
					reg_next_fishing_years_spinner
							.setText(User.self().userInfo.fYears);
				}
				if (!TextUtils.isEmpty(User.self().userInfo.fTimes)) {
					TextView reg_next_fishing_times_spinner = (TextView) findViewById(R.id.reg_next_fishing_times_spinner);
					reg_next_fishing_times_spinner
							.setText(User.self().userInfo.fTimes);
				}

				TextView userName = (TextView) findViewById(R.id.reg_next_nick_input);
				if (!TextUtils.isEmpty(User.self().userInfo.userName)) {
					userName.setText(User.self().userInfo.userName);
				}

				if (!TextUtils.isEmpty(User.self().userInfo.photoUrl)) {
					ImageView userIcon = (ImageView) findViewById(R.id.userIcon);
					ViewHelper.load(
							userIcon,
							UrlUtils.self().getNetUrl(
									User.self().userInfo.photoUrl), true,false);
				}

				TextView reg_next_location_input = (TextView) findViewById(R.id.reg_next_location_input);
				reg_next_location_input.setText(User.self().userInfo.address);
				ViewGroup alvg = (ViewGroup) findViewById(R.id.tags);
				ViewGroup tags_div = (ViewGroup) findViewById(R.id.tags_div);
				alvg.setVisibility(View.VISIBLE);
				String[] userTags = null;
				if (!TextUtils.isEmpty(User.self().userInfo.tag)) {
					userTags = User.self().userInfo.tag.split(",");
				}
				String[] totalTags = LocalMgr.getFPlaceType();
				LayoutInflater mInflater = LayoutInflater.from(this);
				for (int i = 0; i < totalTags.length;) {
					ViewGroup line_tags = (ViewGroup) mInflater.inflate(
							R.layout.line_tags, null);
					for (int j = 0; j < line_tags.getChildCount(); j++) {// 设置tag显示标题
						TextView tv = (TextView) line_tags.getChildAt(j);
						int ii = i + j;
						if (ii >= totalTags.length) {
							tv.setVisibility(View.INVISIBLE);
						} else {// 设置标题
							tv.setText(totalTags[ii]);
							if (userTags != null) {
								for (int k = 0; k < userTags.length; k++) {
									if (totalTags[ii].equals(userTags[k])) {// 设置用户已选择标签状态
										tv.setSelected(true);
										break;
									}
								}
							}
						}
					}
					tags_div.addView(line_tags);
					i = i + 3;
				}
			}
			break;
		case R.layout.ui_forget_pswd:
		case R.layout.ui_login:
			String num = LocalMgr.self().getUserInfo(Const.K_num);
			if (!TextUtils.isEmpty(num)) {
				num = num.trim();
				if (num.length() > 0) {
					TextView tv = (TextView) findViewById(R.id.text_phone_num_input);
					tv.setText(num);
					TextView login_pswd_input = (TextView) findViewById(R.id.login_pswd_input);
					login_pswd_input.requestFocus();
					IME.showIME(login_pswd_input);
				}
			}
			break;
		default:
			break;
		}
	}
	

	public void onUserTagClick(View view) {
		view.setSelected(!view.isSelected());
	}


	private boolean makeSureExitPushlish() {
		if (mRootViewLayoutId == R.layout.ui_comment_publish
				&& (((ViewGroup) (((ViewGroup) findViewById(R.id.comment_pics))
						.getChildAt(0))).getChildCount() > 1)) {
			if (!TextUtils.isEmpty(((TextView) findViewById(R.id.comment_text))
					.getText())) {
				ViewHelper.showConfrim(this, R.layout.confirm_dialog,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (v.getId() == R.id.confirm_dialog_btn_left) {
									finish();
								}
							}
						});
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (makeSureExitPushlish()) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		GalleryUtils.self().onActivityResult(requestCode, resultCode, data);
	}

	private void onCreateNearFPlace() {// 创建附近钓场
		String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
		ViewGroup vg = (ViewGroup) findViewById(R.id.ui_near_fplace_root);
		final ViewGroup mainVG = ViewHelper.newMainView(this,
				getSupportFragmentManager(),
				new BaseFragment.ResultForActivityCallback() {
					@Override
					public void onItemClick(View view, FieldData data) {

					}
				}, tabItemsTitle);
		vg.addView(mainVG);
		ViewPager vp = (ViewPager) mainVG.findViewById(R.id.search_viewpager);
		BaseFragmentPagerAdapter.initAdapterByNetData(vp, R.layout.listitem_field, null, vp.getCurrentItem());
	}


	public void onCareClick(final View view) {

	}
	public void onCareFieldClick(final View view) {
		FieldUIOp.onCareFieldClick((ImageView)view, null, String.valueOf(view.getTag()));
	}
	public void onIconClick(final View view) {
		int id = view.getId();
		switch (id) {
		case R.id.userIcon: {
			GalleryUtils.self().crop(this, new GalleryUtils.GalleryCallback() {
				@Override
				public void onCompleted(String[] filePath, Bitmap bitmap0) {
					String f = filePath[0];
					if(!TextUtils.isEmpty(f)){//更新了头像，设置seletected为true
						((ImageView) view).setImageDrawable(new BitmapDrawable(
								bitmap0));
						view.setTag(f);
						view.setSelected(true);
						User.self().userInfo.photoUrl = f;
					}
				}
			}, view.getWidth(), view.getHeight());
			// GalleryUtils.self().crop(this, new GalleryUtils.GalleryCallback()
			// {
			// @Override
			// public void onCompleted(String[] filePath, Bitmap bitmap0) {
			// if(filePath != null && filePath.length > 0){
			// String f = filePath[0];
			// ((ImageView)view).setImageDrawable(Drawable.createFromPath(f));
			// view.setTag(f);
			// }
			// }
			// },"",false);

			return;
		}
		case R.id.care_btn: {
			return;
		}
		}
		String tag = String.valueOf(view.getTag());
		if (!TextUtils.isEmpty(tag)) {
			if (tag.startsWith("more")) {
				Intent intent = new Intent();
				intent.putExtra(Const.STA_ID,
						Integer.parseInt((tag.split("\\|")[1])));
				intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_zan);
				UIMgr.showActivity(this, intent);
			}
		}
		// UIMgr.showActivity(this,R.layout.ui_pic_selecte,PicUI.class.getName());
		// UIMgr.showActivity(this, 0, ImageViewUI.class.getName());
	}

	private void onCreateZanList() {
		int fieldId = getIntent().getIntExtra(Const.STA_ID, -1);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fieldId);
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final ListView list = (ListView) findViewById(R.id.zan_listview);
		list.setDividerHeight(0);
		AdapterExt.newInstance(list, BaseUI.this,new JSONArray(), R.layout.listitem_friend_2_rows);
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart(BaseUI.this);
			}

			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				AdapterExt ada = (AdapterExt) list.getAdapter();
				if (isRight(json)) {
					JSONArray dataJson = json.optJSONArray(Const.STA_DATA);
					ada.updateAdapter(dataJson);
				}
				TextView title = (TextView) findViewById(R.id.base_head_bar_title);
				title.setText(String.format(Const.DEFT_ZAN_TITLE,
						ada.getCount()));
				onEnd();
			}
		}, jsonObject, UrlUtils.self().getAttListForField());
	}

	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		// case R.id.login:{
		// Intent intent = new Intent();
		// intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_login);
		// intent.setClass(BaseUI.this, BaseUI.class);
		// startActivity(intent);
		// finish();
		// break;
		// }
		case R.id.base_head_bar_ok: {//完善资料页面，提交按钮
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_NAME,
						((TextView) findViewById(R.id.reg_next_nick_input))
								.getText().toString());
				jsonObject
						.put(Const.STA_FISH_YEAR,
								((TextView) findViewById(R.id.reg_next_fishing_years_spinner))
										.getText().toString());
				jsonObject
						.put(Const.STA_FREQUENCY,
								((TextView) findViewById(R.id.reg_next_fishing_times_spinner))
										.getText().toString());
				jsonObject.put(Const.STA_ADDRESS,
						((TextView) findViewById(R.id.reg_next_location_input))
								.getText().toString());
				StringBuffer tags = new StringBuffer();
				{
					ViewGroup tags_div = (ViewGroup) findViewById(R.id.tags_div);
					for (int i = 0; i < tags_div.getChildCount(); i++) {
						ViewGroup line_tags = (ViewGroup) tags_div
								.getChildAt(i);
						for (int j = 0; j < line_tags.getChildCount(); j++) {
							TextView v = (TextView) line_tags.getChildAt(j);
							if (v.isSelected()) {
								tags.append(v.getText()).append(",");
							}
						}
					}
				}
				if (tags.length() > 0 && tags.charAt(tags.length() - 1) == ',') {
					tags.deleteCharAt(tags.length() - 1);
				}
				jsonObject.put(Const.STA_TAG, tags);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new RequestListener() {
				@Override
				public void onStart() {
					super.onStart(BaseUI.this, Const.DEFT_COMMITTING);
				}

				@Override
				public void onEnd(byte[] data) {
					JSONObject response = toJSONObject(data);
					if (isRight(BaseUI.this,response,true)) {// 文字数据提交成功
						View view = findViewById(R.id.userIcon);
						if (TextUtils.isEmpty((String) view.getTag()) || !view.isSelected()) {
							onEnd();
							showHomeUI();
							return;
						}
						JSONObject jsonObject = new JSONObject();
						RequestData rData = RequestData.newInstance(
								new RequestListener() {
									@Override
									public void onEnd(byte[] data) {
										// onEnd();
										JSONObject response = toJSONObject(data);
										if (isRight(BaseUI.this,response,true)) {// 头像数据提交成功
											onEnd();
											showHomeUI();
										}
									}
								}, jsonObject, UrlUtils.self().getUploadUserImg());
						rData.putData("UserImg", new File((String) view.getTag()));
						NetTool.submit().http(rData.syncCallback());
					}
				}
			}, jsonObject, UrlUtils.self().getCompleteData());
			break;
		}
		case R.id.comment_publish_back: {// 发布 返回
			if (!makeSureExitPushlish()) {
				finish();
			}
			break;
		}
		case R.id.base_head_bar_weather: {
			Toast.makeText(this, "此功能静等期待", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.base_head_bar_last:
		case R.id.search_head_back:
		case R.id.base_ui_close: {
			finish();
			break;
		}
		case R.id.base_ui_help: {
			Intent i = new Intent();
			i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_webview_left_close);
			i.putExtra(Const.STA_URL,
					getResources().getString(R.string.help_link));
			i.putExtra(Const.STA_TITLE, getResources().getString(R.string.help));
			UIMgr.showActivity(BaseUI.this, i);
			break;
		}
		case R.id.base_ui_publish: {// 发布钓播
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_INFO_STR,
						((TextView) findViewById(R.id.comment_text)).getText()
								.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			RequestData rData = RequestData.newInstance(new RequestListener() {
				@Override
				public void onStart() {
					super.onStart(BaseUI.this, Const.DEFT_PUBLISHING);
				}

				@Override
				public void onEnd(byte[] data) {
					JSONObject response = toJSONObject(data);
					if (isRight(BaseUI.this, response, true)) {
						//更新前界面界面数据
						finish();
						ViewHelper.showToast(BaseUI.this, Const.DEFT_PUBLISH_COMPLETED);
					}else{
						ViewHelper.showToast(BaseUI.this, response.optString(Const.STA_MESSAGE));
					}
				}
			}, (JSONObject) null, UrlUtils.self().getCreatePodCast());
			rData.putData("json", jsonObject.toString());
			ViewGroup root = (ViewGroup) findViewById(R.id.comment_pics);
			ViewGroup pics = (ViewGroup) root.getChildAt(0);
			for (int i = 0; i < pics.getChildCount(); i++) {
				View im = pics.getChildAt(i);
				Object tag = im.getTag();
				if (tag instanceof String && !TextUtils.isEmpty((String) tag)) {
					rData.putData("UserImg" + i, new File((String) im.getTag()));
				}
			}
			NetTool.submit().http(rData.syncCallback());
			break;
		}
		case R.id.ui_my_care_head_fplace: {
			boolean f = "true".equals(view.getTag());
			if (!f) {
				ViewGroup p = (ViewGroup) view;
				p.setTag("true");
				{// 处理标题已经底线
					TextView titleView = (TextView) p.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.tabitem_foucs_color));
					p.getChildAt(1).setVisibility(View.VISIBLE);

				}
				{// 设置另一个反色
					ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent())
							.getChildAt(1);
					otherP.setTag("false");
					TextView titleView = (TextView) otherP.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.text_btn_color));
					otherP.getChildAt(1).setVisibility(View.GONE);
				}
				{// 设置主内容区显示，隐藏
					int rootViewId = R.id.ui_my_care_list_root;
					ViewGroup vg = (ViewGroup) findViewById(rootViewId);
					vg.getChildAt(0).setVisibility(View.VISIBLE);
					vg.getChildAt(1).setVisibility(View.GONE);
				}
			}
			break;
		}
		case R.id.ui_my_care_head_fnews: {
			boolean f = "true".equals(view.getTag());
			if (!f) {
				ViewGroup p = (ViewGroup) view;
				p.setTag("true");
				{// 处理标题已经底线
					TextView titleView = (TextView) p.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.tabitem_foucs_color));
					p.getChildAt(1).setVisibility(View.VISIBLE);
				}
				{// 设置另一个反色
					ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent())
							.getChildAt(0);
					otherP.setTag("false");
					TextView titleView = (TextView) otherP.getChildAt(0);
					titleView.setTextColor(getResources().getColor(
							R.color.text_btn_color));
					otherP.getChildAt(1).setVisibility(View.GONE);
				}
				{// 设置主内容区显示，隐藏
					int rootViewId = R.id.ui_my_care_list_root;
					ViewGroup vg = (ViewGroup) findViewById(rootViewId);
					vg.getChildAt(1).setVisibility(View.VISIBLE);
					vg.getChildAt(0).setVisibility(View.GONE);
				}
			}
			break;
		}
		}
	}
	
	public void onFishingNews(View view) {
		FishingNewsData.StaticOnClick(BaseUI.this, (Integer)view.getTag());
	}


	public void onShowUser(View view) {

	}

	@Override
	public void onCommentReplyClick(View view) {// 回复，发布评论
		int id = view.getId();
		switch (id) {
		case R.id.comment_listitem_text:
			
			break;
		case R.id.comment_list_reply_text:
			((TextView) view).setCompoundDrawables(null, null, null, null);
			// view.setBackgroundResource(R.drawable.base_border_bg);
			break;
		case R.id.comment_list_publish: {// 回复时
			String objectId = String.valueOf(getIntent().getIntExtra(Const.STA_ID, 0));
			final TextView replyText = ((TextView) findViewById(R.id.comment_list_reply_text));
			IME.hideIME(findViewById(R.id.comment_list_reply_text));
			JSONObject jsonObject = new JSONObject();
			CommentData commentData = (CommentData) replyText.getTag();
			String toId = "0";
			if (commentData != null) {
				toId = commentData.memberId;
				objectId = commentData.id;// 评论的id
			}
			try {
				jsonObject.put(Const.STA_TO_ID, toId);
				jsonObject.put(Const.STA_OBJECTID, objectId);// 可能是钓场，可能是评论
				jsonObject.put(Const.STA_COMMENT_STR, replyText.getText());
				jsonObject.put(Const.STA_TYPE, "field");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			final String f_object = objectId;
			NetTool.data().http(new RequestListener() {
				@Override
				public void onStart() {
					onStart(BaseUI.this, Const.DEFT_SUBMITING);
				}

				@Override
				public void onEnd(byte[] data) {
					JSONObject response = toJSONObject(data);
					if(isRight(BaseUI.this, response, true)){
						ListView listView = (ListView) findViewById(R.id.comment_list);
						JSONObject comment = new JSONObject();
						try {
							CommentData replayComment = (CommentData) replyText.getTag();// 要回复的评论数据对象
							comment.put("commentStr", replyText.getText().toString());
							comment.put("id", f_object);
							comment.put("imgUrl", User.self().userInfo.photoUrl);
							comment.put("name", User.self().userInfo.userName);
							comment.put("createdAt", BaseUtils.getCurrentTime());
							CommentData newCommentData = CommentData.newInstance(comment);// 即将发布的新评论
							if (replayComment == null) {// 对渔场的评论
								((AdapterExt) listView.getAdapter()).updateAdapter(newCommentData);
							} else {// 对评论的回复，添加新评论到结构中时，需要使用根rootComment
								newCommentData.commentStr = replyText.getText().toString();
								newCommentData.commentTime = BaseUtils.getCurrentTime();
								newCommentData.fromId = User.self().userInfo.id;
								newCommentData.toId = replayComment.memberId;
								newCommentData.fromName = User.self().userInfo.userName;
								newCommentData.toName = replayComment.memberName;
								CommentData rootComment = replayComment.getRootCommentData();// 使用
								if (rootComment == null) {
									rootComment = replayComment;
								}
								if (rootComment.lowerComments == null) {
									rootComment.lowerComments = new ArrayList<CommentData>();
								}
								newCommentData.setRootCommentData(rootComment);
								rootComment.lowerComments.add(newCommentData);
								((AdapterExt) listView.getAdapter()).updateAdapter();
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						replyText.setTag(null);
						replyText.setHint(Const.DEFT_REPLY_TEXT);
						replyText.setText("");
						replyText.setCompoundDrawables(
								new BitmapDrawable(BitmapFactory.decodeResource(
										getResources(), R.drawable.comment)), null,
										null, null);
						onEnd();
					}
				}
			}, jsonObject, UrlUtils.self().getCommentOn());

			break;
		}
		case R.id.comment_listitem_reply:// 点击回复图标
		case -1: {// 点击评论信息 回复图标
			TextView replyText = ((TextView) findViewById(R.id.comment_list_reply_text));// 向输入框view设置数据
			replyText.setCompoundDrawables(null, null, null, null);
			IME.showIME(replyText);
			CommentData tv = (CommentData) (view.getTag());// 名字|memberId
			replyText.setHint(Const.DEFT_REPLY + Const.DEFT_REPLY_
					+ tv.memberName);
			replyText.setTag(tv);// 设置toMemberId
			replyText.requestFocus();
			break;
		}
		case R.id.detail_bottom_bar_comment_icon:
			Intent intent = new Intent();
			intent.putExtra(Const.STA_ID, (Integer) view.getTag());
			intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_comment_list);// 跳转到评论列表页面
			UIMgr.showActivity(this, intent);
			break;
		}
	}

	public void onChange(View view) {
		if (view.getId() == R.id.hide_show_pwsd) {
			View switcher = findViewById(R.id.hide_show_pwsd);
			EditText input = (EditText) findViewById(R.id.login_pswd_input);
			if (switcher.getTag().equals("true")) {// 明文-->密文
				switcher.setBackgroundResource(R.drawable.icon_switch_off);
				switcher.setTag("false");
				input.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {// 密文-->明文
				switcher.setBackgroundResource(R.drawable.icon_switch_on);
				input.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_NORMAL);
				switcher.setTag("true");
			}
			input.setSelection(input.getText().toString().length());
		}
	}

	public void onSelect(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.reg_next_fishing_times_spinner: {
			String[] tabItemsTitle = getResources().getStringArray(
					R.array.reg_next_fishing_times_spinner);
			new ActionSheet(this, tabItemsTitle, "钓龄选择",
					new ActionSheet.ActionSheetListener() {
						@Override
						public void onSelected(int position, String item) {
							((TextView) findViewById(R.id.reg_next_fishing_times_spinner))
									.setText(item);
						}
					}).showActionSheet();
			break;
		}
		case R.id.reg_next_fishing_years_spinner: {
			String[] tabItemsTitle = getResources().getStringArray(
					R.array.reg_next_fishing_years_spinner);
			new ActionSheet(this, tabItemsTitle, "钓频选择",
					new ActionSheet.ActionSheetListener() {
						@Override
						public void onSelected(int position, String item) {
							((TextView) findViewById(R.id.reg_next_fishing_years_spinner))
									.setText(item);
						}
					}).showActionSheet();
			break;
		}
		}
	}

	public void onLocation(final View view) {
		int id = view.getId();
		switch (id) {
		case R.id.comment_publish_add_location:
		case R.id.reg_next_location_input:
			ViewHelper.showGlobalWaiting(this, null, "位置请求中...", 6000);
			MapUtil.getLocation(this, new OnGetLocationListener() {
				@Override
				public void onGetLocation(LocationData data) {
					User.self().userInfo.lng = data.lng;
					User.self().userInfo.lat = data.lat;
					ViewHelper.closeGlobalWaiting();
					((TextView) view).setText(data.address);
				}
			});
			break;
		}
	}

	public void showHomeUI() {
		Intent i = new Intent();
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_main);
		UIMgr.showActivity(BaseUI.this, i, HomeUI.class.getName());
		finish();
	}

	public void onShare(final View view) {
		int id = view.getId();
		switch (id) {
		case R.id.listitem_fnews_share: {// 搜索页面 share
		}
		}
	}

	public void onClick(final View view) {
		int id = view.getId();
		switch (id) {
		case R.id.listitem_fplace_care: {// 搜索页面 care
			FieldData fpp = (FieldData) view.getTag();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_FIELDID, fpp.sid);
				jsonObject.put(Const.STA_TYPE, (view.isSelected() ? "qxgz"
						: "gz"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onEnd(byte[] data) {
					JSONObject response = toJSONObject(data);
					if (isRight(response)) {
						view.setSelected(!view.isSelected());
						// int careCount =
						// response.optInt(Const.STA_CARE_COUNT);
						// ((TextView)
						// findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
					} else {
						// ViewHelper .showToast(getActivity(),
						// Const.DEFT_GET_CHECK_CODE_FAILED);
					}
				}
			}, jsonObject, UrlUtils.self().getAttention());
			break;
		}
		case R.id.ui_forget_get_check_code:
		case R.id.get_check_code: {
			String num = ((TextView) findViewById(R.id.text_phone_num_input))
					.getText().toString();
			if (TextUtils.isEmpty(num) || num.length() != 11) {
				ViewHelper.showToast(this,
						Const.DEFT_PLEASE_INPUT_RIGHT_PHONE_NUMBER);
				return;
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_MOBILE, num);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			String url = UrlUtils.self().getSendCheckNode();
			if (id == R.id.ui_forget_get_check_code) {
				url = UrlUtils.self().getCheckMobile();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(BaseUI.this, null,
							Const.DEFT_GETTING);
				}

				@Override
				public void onError(int type, String msg) {
					super.onError(type, msg);
				}

				@Override
				public void onEnd(byte[] data) {
					onEnd();
					JSONObject response = toJSONObject(data);
					// if(response == null){
					// try {
					// response = new
					// JSONObject("{\"code\":1,\"isError\":false,\"message\":\"success\",\"remark\":\"\",\"data\":{\"validateCode\":\"151230\"},\"unlogin\":\"\",\"exception\":\"\",\"error\":\"\"}");
					// } catch (Exception e) {
					// }
					// }
					if (isRight(response)
					// && response.has(Const.STA_DATA)
					// &&
					// response.optJSONObject(Const.STA_DATA).has(Const.STA_VALIDATECODE)
					) {
						ViewHelper.showToast(BaseUI.this,
								Const.DEFT_GET_CHECK_CODE_SENDING);
						// 浏览器查看接口 http://115.29.51.39:8080/code/listCode
						// ((TextView)
						// findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
					} else {
						ViewHelper.showToast(BaseUI.this,
								response.optString(Const.STA_MESSAGE));
					}
				}
			}, jsonObject, url);
			// SmsManager smsManager = SmsManager.getDefault();
			// smsManager.sendTextMessage("10086", null, "cxyl", null, null);
			break;
		}

		case R.id.ok: {// 忘记密码确定按钮
			final String num = ((TextView) findViewById(R.id.text_phone_num_input))
					.getText().toString();
			if (TextUtils.isEmpty(num) || num.length() != 11) {
				ViewHelper.showToast(this,
						Const.DEFT_PLEASE_INPUT_RIGHT_PHONE_NUMBER);
				return;
			}
			final String login_pswd_input = ((TextView) findViewById(R.id.login_pswd_input))
					.getText().toString();
			if (TextUtils.isEmpty(login_pswd_input)) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_PSWD);
				return;
			}
			String checkCode = ((TextView) findViewById(R.id.checkCode))
					.getText().toString();
			if (TextUtils.isEmpty(checkCode)) {
				ViewHelper.showToast(this,
						Const.DEFT_PLEASE_INPUT_RIGHT_CHECK_CODE);
				return;
			}
			JSONObject checkCodeJsonObject = new JSONObject();
			try {
				checkCodeJsonObject.put(Const.STA_MOBILE, num);
				// jsonObject.put(Const.STA_PASSWORD, login_pswd_input);
				checkCodeJsonObject.put(Const.STA_VALIDATECODE, checkCode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(BaseUI.this, null,
							Const.DEFT_REQUESTING);
				}

				@Override
				public void onEnd(byte[] bytes) {
					JSONObject response = toJSONObject(bytes);
					if (response != null) {
						if (isRight(response)) {// 校验验证码成功
							// 启动重置密码请求
							JSONObject jsonObject = new JSONObject();
							try {
								jsonObject.put(Const.STA_MOBILE, num);
								jsonObject.put(Const.STA_NEW_PASSWORD,
										login_pswd_input);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							NetTool.data().http(
									new NetTool.RequestListener() {
										@Override
										public void onEnd(byte[] bytes) {
											onEnd();
											JSONObject response = toJSONObject(bytes);
											if (response != null) {
												if (isRight(response)) {
													// JSONObject data =
													// response.optJSONObject(Const.STA_DATA);
													// LocalMgr.self().saveUserInfo(Const.K_LoginData,
													// data.toString());
													ViewHelper
															.showToast(
																	BaseUI.this,
																	response.optString(Const.STA_MESSAGE));
													finish();
												} else {
													ViewHelper
															.showToast(
																	BaseUI.this,
																	response.optString(Const.STA_MESSAGE));
												}
											} else {
												onEnd();
												ViewHelper.showToast(
														BaseUI.this,
														Const.DEFT_NET_ERROR);
											}
										}
									}, jsonObject,
									UrlUtils.self().getResetNewPassword());

						} else {
							ViewHelper.showToast(BaseUI.this,
									response.optString(Const.STA_MESSAGE));
						}
					} else {
						onEnd();
						ViewHelper.showToast(BaseUI.this, Const.DEFT_NET_ERROR);
					}
				}
			}, checkCodeJsonObject, UrlUtils.self().getCheckValidateCode());
			// showRegNext = true;
			// replace(regNextFragment);
			break;
		}
		case R.id.my_fishing_item_pics_gridview: {
			UIMgr.showActivity(this, 0, ImageViewUI.class.getName());
			break;
		}
		case R.id.login_pswd_forget: {
			UIMgr.showActivity(BaseUI.this, R.layout.ui_forget_pswd);
			break;
		}
		case R.id.login_login_btn: {// 登陆按钮login
			final String num = ((TextView) findViewById(R.id.text_phone_num_input))
					.getText().toString();
			if (TextUtils.isEmpty(num) || num.length() != 11) {
				ViewHelper.showToast(this,
						Const.DEFT_PLEASE_INPUT_RIGHT_PHONE_NUMBER);
				return;
			}
			final String login_pswd_input = ((TextView) findViewById(R.id.login_pswd_input))
					.getText().toString();
			if (TextUtils.isEmpty(login_pswd_input)) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_PSWD);
				return;
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_USER_NAME, num);
				jsonObject.put(Const.STA_PASSWORD, login_pswd_input);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(BaseUI.this, null,
							Const.DEFT_GETTING);
				}

				@Override
				public void onEnd(byte[] bytes) {
					ViewHelper.closeGlobalWaiting();
					JSONObject response = toJSONObject(bytes);
					// if(response == null){
					// try {
					// response = new
					// JSONObject("{\"code\":1,\"isError\":false,\"message\":\"success\",\"remark\":\"\",\"data\":{\"token\":\"ASDFCGUDHJFJHSGKH151230\"},\"unlogin\":\"\",\"exception\":\"\",\"error\":\"\"}");
					// } catch (Exception e) {
					// }
					// }
					if (response != null) {
						if (isRight(response)) {
							JSONObject data = response.optJSONObject(Const.STA_DATA);
							LocalMgr.self().saveUserInfo(Const.K_LoginData,
									data.toString());
							LocalMgr.self().saveUserInfo(Const.K_num, num);
							LocalMgr.self().saveUserInfo(Const.K_pswd,
									login_pswd_input);
							User.self().userInfo = PersonData.newInstance(data
									.optJSONObject(Const.STA_MEMBER));
							User.self().userInfo.mobileNum = num;
							showHomeUI();
						} else {
							ViewHelper.showToast(BaseUI.this,
									response.optString(Const.STA_MESSAGE));
						}
					} else {
						ViewHelper.showToast(BaseUI.this, Const.DEFT_NET_ERROR);
					}
				}

			}, jsonObject, UrlUtils.self().getMemberLogin());

			break;
		}

		case R.id.login_reg_new_account_btn: {
			Intent i = new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_reg);
			UIMgr.showActivity(this, i, RegisterUI.class.getName());
			break;
		}
		case R.id.reg_next_skip: {
			finish();
			// 应该还要触发显示附近钓场
			break;
		}
		case R.id.listitem_fnews_share: {
			ViewHelper.share(this, Const.DEFT_SHARE_CONTENT);
			break;
		}
		// case R.id.reg_next_get_location_btn: {
		// MapUtil.getLocation(this, new OnGetLocationListener() {
		// @Override
		// public void onGetLocation(LocationData data) {
		// TextView tv = (TextView) findViewById(R.id.reg_next_location_input);
		// tv.setText(data.address);
		// }
		// }, 0);
		// break;
		// }
		}

	}

	private void onCreateWebview() {
		WebView wb = (WebView) findViewById(R.id.base_ui_webview);
		WebSettings setting = wb.getSettings();
		wb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				ViewHelper.closeGlobalWaiting();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				ViewHelper.showGlobalWaiting(BaseUI.this, null);
			}
		});
		setting.setJavaScriptEnabled(true);
		String url = getIntent().getStringExtra(Const.STA_URL);
		wb.loadUrl(url);
		String title = getIntent().getStringExtra(Const.STA_TITLE);
		TextView titleView = (TextView) findViewById(R.id.base_ui_webview_title);
		titleView.setText(title);
	}
	public void onPersonClick(View view) {
		PersonData personData = (PersonData)view.getTag();
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_podcast_person);
		i.putExtra(Const.STA_TITLE, personData.userName + "钓播");
		i.putExtra(Const.PRI_HIDE_PUBLISH, true);
		i.putExtra(Const.STA_MOBILE, personData.mobileNum);
		UIMgr.showActivity(this,i,BaseUI.class.getName());
	}
	@Override
	public void onTagClick(View view) {
		// view self seting background child[1] setVisible
		View child = ((ViewGroup) view).getChildAt(1);
		if (child.getVisibility() == View.GONE) {
			view.setBackgroundResource(R.drawable.tag_selected_background);
			child.setVisibility(View.VISIBLE);
		} else {
			view.setBackgroundResource(R.drawable.tag_background);
			child.setVisibility(View.GONE);
		}
	}
	
	public void onPodCastDetailClick(View view) {
		
		UIMgr.showActivity(this,R.layout.ui_detail_podcast,SearchUI.class.getName());
	}
	public void onFieldClick(View view) {
		
	}

	@Override
	public void onItemClick(View view, IBaseData data) {
		data.OnClick(BaseUI.this, null, view);
	}
}
