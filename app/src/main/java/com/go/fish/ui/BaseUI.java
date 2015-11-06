package com.go.fish.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
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

import com.go.fish.MainActivity;
import com.go.fish.R;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FPlaceData;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.ui.pics.GalleryUtils;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.NetTool;
import com.go.fish.view.ActionSheet;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AutoLayoutViewGroup;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.FPlaceListAdapter;
import com.go.fish.view.IBaseData;
import com.go.fish.view.IMETools;
import com.go.fish.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class BaseUI extends FragmentActivity implements IHasHeadBar, IHasTag,
		IHasComment {

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
		case R.layout.ui_webview_left_close:
			onCreateWebview();
			break;
		case R.layout.ui_my_f_news:
			onCreateMyFNewsView();
			break;
		case R.layout.ui_near_fplace:
			onCreateNearFPlace();
			break;
		case R.layout.ui_near_friends:
			onCreateNearFriend();
			break;
		case R.layout.ui_my_care:
			onCreateMyCare();
			break;
		case R.layout.ui_comment_publish:
			onCreateCommentPublish();
			break;
		case R.layout.ui_comment_list:
			onCreateCommentList();
			break;
		case R.layout.ui_zan:
			onCreateZanList();
			break;
		case R.layout.ui_my_sec:
			break;
		default:
			break;
		}
	}

	int PADDING = 20;

	private void addImageView(final ViewGroup parent, final String filePath,
			int resId) {
		ImageView t = new ImageView(this);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// ViewHelper.showToast(BaseUI.this, "show a");
				GalleryUtils.self().pick(BaseUI.this,
						new GalleryUtils.GalleryCallback() {
							@Override
							public void onCompleted(String[] filePaths) {
								if (!(v.getTag() instanceof Integer)) {
									int w = (getResources().getDisplayMetrics().widthPixels - PADDING * 4) / 3;
									Bitmap bitmap = LocalMgr.self()
											.getSuitBimap(filePaths[0], w, w);
									((ImageView) v)
											.setImageDrawable(new BitmapDrawable(
													bitmap));
									// bitmap.recycle();
									((ImageView) v).setTag(filePath);
								} else if (filePaths != null) {
									addImageView(parent, filePaths[0], -1);
								}
							}
						}, null, false);
			}
		});
		t.setPadding(PADDING, PADDING, PADDING, PADDING);
		t.setBackgroundResource(R.drawable.base_background);
		int w = (getResources().getDisplayMetrics().widthPixels - PADDING * 6) / 3;
		if (filePath == null) {
			t.setImageResource(resId);
			t.setTag(resId);
		} else {
			Bitmap bitmap = LocalMgr.self().getSuitBimap(filePath, w, w);
			t.setImageDrawable(new BitmapDrawable(bitmap));
			t.setTag(filePath);
		}
		parent.addView(t, 0, new ViewGroup.LayoutParams(w, w));
	}

	private boolean makeSureExitPushlish() {
		if (mRootViewLayoutId == R.layout.ui_comment_publish
				&& (((ViewGroup) (((ViewGroup) findViewById(R.id.comment_pics)).getChildAt(0))).getChildCount() > 1 
						)) {
			if(!TextUtils.isEmpty(((TextView) findViewById(R.id.comment_text)).getText())){
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
			}else{
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

	private void onCreateCommentPublish() {// 创建钓播发布页面
		ViewGroup root = (ViewGroup) findViewById(R.id.comment_pics);
		AutoLayoutViewGroup autoLayout = new AutoLayoutViewGroup(this);
		addImageView(autoLayout, null, R.drawable.p);
		root.addView(autoLayout, -1, -2);
		// final EditText et = (EditText) findViewById(R.id.comment_text);
		// TextView t = (TextView)findViewById(R.id.comment_has_len);
		// et.setSelection(et.getText().length());
		// final int maxCount =
		// getResources().getInteger(R.integer.comment_max_count);
		// t.setText(String.format(getString(R.string.input_text_count),maxCount
		// - et.getText().length() ));
		// IMETools.showIME(et);
		// et.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// TextView t = (TextView) findViewById(R.id.comment_has_len);
		// int lessCount = 0;
		// if (et.getText().length() >= maxCount) {
		// et.removeTextChangedListener(this);
		// et.setText(et.getText().subSequence(0, maxCount));
		// et.setSelection(et.getText().length());
		// et.addTextChangedListener(this);
		// }
		// t.setText(String.format(getString(R.string.input_text_count),
		// maxCount - et.getText().length()));
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// }) ;
	}

	private void onCreateMyCare() {// 创建我的关注 钓场 钓友
		ViewGroup vg = (ViewGroup) findViewById(R.id.ui_my_care_list_root);
		{// 钓场、渔具
			ListView fPlaceList = new ListView(this);
			vg.addView(fPlaceList);
			// 本地先获取显示
			String careFPlace = LocalMgr.self().getString(
					Const.SIN_DB_MY_CARE_FPLACE);
			JSONArray jsonArr = null;
			try {
				if (!TextUtils.isEmpty(careFPlace)) {
					jsonArr = new JSONArray(careFPlace);
				} else {
					jsonArr = new JSONArray();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(
					R.layout.listitem_fpalce, jsonArr);
			final FPlaceListAdapter mListAdapter = new FPlaceListAdapter(this,
					fPlaceArr);
			fPlaceList.setAdapter(mListAdapter);
			// 网络数据抓取,进行更新
			NetTool.RequestData rd = NetTool.RequestData.newInstance(
					new NetTool.RequestListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onEnd(byte[] data) {
							try {
								String str = new String(data, "utf-8");
								mListAdapter.updateAdapter(DataMgr
										.makeFPlaceDatas(
												R.layout.listitem_fpalce,
												new JSONArray(str)
														.getJSONArray(0)));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, "fishing_place_near");
			NetTool.data().http(rd.syncCallback());
		}
		
		{// 钓播
			final ListView fNewsList = new ListView(this);
			fNewsList.setVisibility(View.GONE);
			vg.addView(fNewsList);
			// 本地先获取显示
			String careFPlace = LocalMgr.self().getString(
					Const.SIN_DB_MY_CARE_FNEWS);
			ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
			JSONArray jsonArr = null;
			try {
				if (!TextUtils.isEmpty(careFPlace)) {
					jsonArr = new JSONArray(careFPlace);
				} else {
					jsonArr = new JSONArray();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// friend界面此时最接近
			final AdapterExt mListAdapter = AdapterExt.newInstance(fNewsList,
					jsonArr, R.layout.listitem_friend);
			fNewsList.setAdapter(mListAdapter);
			// 网络数据抓取,进行更新
			NetTool.RequestData rd = NetTool.RequestData.newInstance(
					new NetTool.RequestListener() {
						@Override
						public void onStart() {
							ViewHelper.showGlobalWaiting(BaseUI.this, null);
						}

						@Override
						public void onEnd(byte[] data) {
							mListAdapter.updateAdapter(AdapterExt
									.makePersonDataArray(toJSONArray(data)));
							ViewHelper.closeGlobalWaiting();
						}
					}, "my_friend");
			NetTool.data().http(rd.syncCallback());
		}
	}

	private void onCreateNearFPlace() {// 创建附近钓场
		String[] tabItemsTitle = getResources().getStringArray(
				R.array.hfs_splace_type);
		ViewGroup vg = (ViewGroup) findViewById(R.id.ui_near_fplace_root);
		final ViewGroup mainVG = ViewHelper.newMainView(this,
				getSupportFragmentManager(),
				new BaseFragment.ResultForActivityCallback() {
					@Override
					public void onItemClick(View view, FPlaceData data) {

					}
				}, tabItemsTitle);
		vg.addView(mainVG);
		BaseFragmentPagerAdapter.initAdapterByNetData(
				(ViewPager) mainVG.findViewById(R.id.search_viewpager),
				R.layout.listitem_fpalce);
	}

	private void onCreateNearFriend() {// 创建附近钓友
		NetTool.RequestData rd = NetTool.RequestData.newInstance(
				new NetTool.RequestListener() {
					@Override
					public void onStart() {
						ViewHelper.showGlobalWaiting(BaseUI.this, null);
					}

					@Override
					public void onEnd(byte[] data) {
						ListView list = (ListView) findViewById(R.id.ui_near_f_friends_listview);
						list.setDividerHeight(0);
						JSONArray arr = null;
						try {
							arr = new JSONArray(new String(data, "utf-8"));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						list.setAdapter(AdapterExt.newInstance(list, arr,
								R.layout.listitem_friend));
						ViewHelper.closeGlobalWaiting();
					}
				}, "near_friend");
		NetTool.data().http(rd.syncCallback());
	}

	private void onCreateMyFNewsView() {// 创建我的钓播
		// TODO 先加载本地数据

		NetTool.RequestData rd = NetTool.RequestData.newInstance(
				new NetTool.RequestListener() {
					@Override
					public void onEnd(byte[] data) {
						ListView list = (ListView) findViewById(R.id.my_f_news_list);
						JSONArray arr = toJSONArray(data);
						list.setAdapter(AdapterExt.newInstance(list, arr,
								R.layout.listitem_fnews));
					}
				}, "my_fnews");
		NetTool.data().http(rd.syncCallback());
	}

	public void onIconClick(final View view) {
		int id = view.getId();
		switch (id) {
			case R.id.reg_next_photo: {
				GalleryUtils.self().crop(this, new GalleryUtils.GalleryCallback() {
					@Override
					public void onCompleted(String[] filePath) {
						((ImageView)view).setImageDrawable(Drawable.createFromPath(filePath[0]));
					}
				},view.getWidth(),view.getHeight());
				return;
			}
			case R.id.right_icon:{
				return;
			}
		}
		String tag = String.valueOf(view.getTag());
		if(!TextUtils.isEmpty(tag)){
			if(tag.startsWith("more")){
				Intent intent = new Intent();
				intent.putExtra(Const.STA_ID, Integer.parseInt((tag.split("\\|")[1])));
				intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_zan);
				UIMgr.showActivity(this,intent);
			}
		}
		// UIMgr.showActivity(this,R.layout.ui_pic_selecte,PicUI.class.getName());
//		UIMgr.showActivity(this, 0, ImageViewUI.class.getName());
	}
	
	private void onCreateZanList(){
		int fieldId = getIntent().getIntExtra(Const.STA_ID, -1);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fieldId);
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final ListView list = (ListView)findViewById(R.id.zan_listview);
		list.setDividerHeight(0);
		AdapterExt.newInstance(list, new JSONArray(), R.layout.listitem_friend_for_ui_zan);
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart();
				ViewHelper.showGlobalWaiting(BaseUI.this, null);
			}
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				AdapterExt ada = (AdapterExt)list.getAdapter();
				if(isRight(json)){
					JSONArray dataJson = json.optJSONArray(Const.STA_DATA);
					ada.updateAdapter(dataJson);
				}
				TextView title = (TextView)findViewById(R.id.base_head_bar_title);
				title.setText(String.format(Const.DEFT_ZAN_TITLE, ada.getCount()));
				ViewHelper.closeGlobalWaiting();
			}
		}, jsonObject, UrlUtils.self().getAttListForField());
	}

	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.comment_publish_back: {
			if(!makeSureExitPushlish()){
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
		case R.id.base_ui_publish: {
			finish();
			break;
		}
		case R.id.ui_my_care_head_fplace: {
            boolean f = "true".equals(view.getTag());
            if (!f) {
                ViewGroup p = (ViewGroup) view;
                p.setTag("true");
                {//处理标题已经底线
                    TextView titleView = (TextView) p.getChildAt(0);
                    titleView.setTextColor(getResources().getColor(R.color.tabitem_foucs_color));
                    p.getChildAt(1).setVisibility(View.VISIBLE);

                }
                {//设置另一个反色
                    ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent()).getChildAt(1);
                    otherP.setTag("false");
                    TextView titleView = (TextView) otherP.getChildAt(0);
                    titleView.setTextColor(getResources().getColor(R.color.text_btn_color));
                    otherP.getChildAt(1).setVisibility(View.GONE);
                }
                {//设置主内容区显示，隐藏
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
                {//处理标题已经底线
                    TextView titleView = (TextView) p.getChildAt(0);
                    titleView.setTextColor(getResources().getColor(R.color.tabitem_foucs_color));
                    p.getChildAt(1).setVisibility(View.VISIBLE);
                }
                {//设置另一个反色
                    ViewGroup otherP = (ViewGroup) ((ViewGroup) p.getParent()).getChildAt(0);
                    otherP.setTag("false");
                    TextView titleView = (TextView) otherP.getChildAt(0);
                    titleView.setTextColor(getResources().getColor(R.color.text_btn_color));
                    otherP.getChildAt(1).setVisibility(View.GONE);
                }
                {//设置主内容区显示，隐藏
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

	@Override
	public void onCommentIconClick(View view) {

	}

	private void onCreateCommentList() {
		int fieldId = getIntent().getIntExtra(Const.STA_ID, -1);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_OBJECTID, fieldId);
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT);
			jsonObject.put(Const.STA_TYPE, Const.DEFT_COMMENTLIST_TYPE_FIELD);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(fieldId < 1) finish();
		ListView listView = (ListView)findViewById(R.id.comment_list);
		listView.setDividerHeight(0);
		listView.setTag(fieldId);
		onBaseCreateListView(listView, R.layout.listitem_comment, jsonObject, UrlUtils.self().getCommentList(), 0);
	}
	
	private void onBaseCreateListView(ListView listView,int itemLayoutId,JSONObject jsonObject,String url, int extraData){
		final ListView list = listView;
		AdapterExt.newInstance(list, new JSONArray(), itemLayoutId);
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart();
				ViewHelper.showGlobalWaiting(BaseUI.this, null);
			}

			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if (isRight(json)) {
					JSONArray dataJson = json.optJSONArray(Const.STA_DATA);
					((AdapterExt) list.getAdapter()).updateAdapter(dataJson);
				}
				ViewHelper.closeGlobalWaiting();
			}
		}, jsonObject, url);
	}
	public void onShowUser(View view) {

	}
	@Override
	public void onCommentReplyClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.comment_list_reply_text:
				((TextView) view).setCompoundDrawables(null, null, null, null);
//			view.setBackgroundResource(R.drawable.base_border_bg);
				break;
			case R.id.comment_list_publish: {
				final TextView replyText = ((TextView) findViewById(R.id.comment_list_reply_text));
				IMETools.hideIME(findViewById(R.id.comment_list_reply_text));
				JSONObject jsonObject = new JSONObject();
				String[] args = BaseUtils.splitString(replyText.getTag());
				try {
					jsonObject.put(Const.STA_TO_MEMBER_ID,args[1]);
					jsonObject.put(Const.STA_OBJECTID,args[2]);
					jsonObject.put(Const.STA_MEMBER_ID,args[3]);
					jsonObject.put(Const.STA_COMMENT_STR,replyText.getText());
					jsonObject.put(Const.STA_TYPE,"field");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				NetTool.data().http(new RequestListener() {
					@Override
					public void onStart() {
						super.onStart();
						ViewHelper.showGlobalWaiting(BaseUI.this,null,Const.DEFT_SUBMITING);
					}

					@Override
					public void onEnd(byte[] data) {
						replyText.setHint(Const.DEFT_REPLY_TEXT);
						replyText.setText("");
						replyText.setCompoundDrawables(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.comment)), null, null, null);
						ViewHelper.closeGlobalWaiting();
					}
				},jsonObject,UrlUtils.self().getCommentOn());

				break;
			}
			case R.id.comment_listitem_reply:
			case -1: {
				TextView replyText = ((TextView) findViewById(R.id.comment_list_reply_text));
				replyText.setCompoundDrawables(null, null, null, null);
				IMETools.showIME(replyText);
				String tv = String.valueOf(view.getTag());
				String[] args = BaseUtils.splitString(tv);
				replyText.setHint(Const.DEFT_REPLY + Const.DEFT_REPLY_ + args[0]);
				replyText.setTag(view.getTag());
				replyText.requestFocus();
				break;
			}
			case R.id.detail_bottom_bar_comment_icon:
				Intent intent = new Intent();
				intent.putExtra(Const.STA_ID, (Integer) view.getTag());
				intent.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_comment_list);
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
					ViewHelper.closeGlobalWaiting();
					((TextView) view).setText(data.address);
				}
			});
			break;
		}
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.get_check_code: {
			String num = ((TextView) findViewById(R.id.text_phone_num_input))
					.getText().toString();
			if (TextUtils.isEmpty(num) || num.length() != 11) {
				ViewHelper.showToast(this, Const.DEFT_PLEASE_INPUT_RIGHT_PHONE_NUMBER);
				return;
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_MOBILE, num);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(BaseUI.this, null, Const.DEFT_GETTING);
				}
				@Override
				public void onEnd(byte[] data) {
					ViewHelper.closeGlobalWaiting();
					JSONObject response = toJSONObject(data);
//									if(response == null){
//										try {
//											response = new JSONObject("{\"code\":1,\"isError\":false,\"message\":\"success\",\"remark\":\"\",\"data\":{\"validateCode\":\"151230\"},\"unlogin\":\"\",\"exception\":\"\",\"error\":\"\"}");
//										} catch (Exception e) {
//										}
//									}
					if (response != null && response.has(Const.STA_CODE) && response.optString(Const.STA_CODE).equals(Const.DEFT_1)) {
						((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
					} else {
						ViewHelper .showToast(BaseUI.this, Const.DEFT_GET_CHECK_CODE_FAILED);
					}
				}
			},jsonObject,UrlUtils.self().getSendCheckNode());
			// SmsManager smsManager = SmsManager.getDefault();
			// smsManager.sendTextMessage("10086", null, "cxyl", null, null);
			break;
		}
		
		case R.id.ok: {//忘记密码确定按钮
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
					ViewHelper.showGlobalWaiting(BaseUI.this, null, Const.DEFT_GETTING);
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
						} else {
							ViewHelper.showToast(BaseUI.this, response.optString(Const.STA_MESSAGE));
						}
					}else{
						ViewHelper.showToast(BaseUI.this, Const.DEFT_NET_ERROR);
					}
				}
			},jsonObject,UrlUtils.self().getRegisterMember());
//			 showRegNext = true;
//			 replace(regNextFragment);
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
		case R.id.login_login_btn: {//登陆
			String num = ((TextView) findViewById(R.id.ui_login_input_phone))
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
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Const.STA_MOBILE, num);
				jsonObject.put(Const.STA_PASSWORD, login_pswd_input);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			NetTool.data().http(new NetTool.RequestListener() {
				@Override
				public void onStart() {
					super.onStart();
					ViewHelper.showGlobalWaiting(BaseUI.this, null, Const.DEFT_GETTING);
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
							Intent i = new Intent();
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_main);
							UIMgr.showActivity(BaseUI.this, i, HomeUI.class.getName());
							finish();
						} else {
							ViewHelper.showToast(BaseUI.this, response.optString(Const.STA_MESSAGE));
						}
					}else{
						ViewHelper.showToast(BaseUI.this, Const.DEFT_NET_ERROR);
					}
				}
			},jsonObject,UrlUtils.self().getMemberLogin());
			
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
}
