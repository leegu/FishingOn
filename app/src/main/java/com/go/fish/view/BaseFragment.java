package com.go.fish.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.go.fish.R;
import com.go.fish.data.FPlaceData;
import com.go.fish.ui.BaseUI;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.ObservableScrollView.ScrollViewListener;

public class BaseFragment extends Fragment {
	private static final String TAG = "MainView";
	ResultForActivityCallback mCallback = null;
	public boolean isFront = false;

	public static BaseFragment newInstance(ResultForActivityCallback callback,
			int layoutId) {
		BaseFragment f = new BaseFragment();
		f.mCallback = callback;
		Bundle b = new Bundle();
		b.putInt(Const.PRI_LAYOUT_ID, layoutId);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle b = getArguments();
		int layoutId = b.getInt(Const.PRI_LAYOUT_ID);
		View view = inflater.inflate(layoutId, container, false);
		if (layoutId == R.layout.ui_search_list) {
			onCreateSearchViewPager(view);
		} else if (layoutId == R.layout.ui_f_search_list_in_map) {
			onCreateSearchMap(view);
		} else if (layoutId == R.layout.ui_f_search_item_detail) {
			onCreateSearchDetail(view);
		}
		return view;
	}

	private void onCreateSearchMap(View view) {
		ViewGroup vg = (ViewGroup) view
				.findViewById(R.id.search_list_in_map_bmap_view);
		MapView mapView = MapUtil.newMap(getActivity());
		vg.addView(mapView);
		View floatView = LayoutInflater.from(getActivity()).inflate(
				R.layout.float_view_search_list_in_map, vg);
		TextView detail_text = (TextView) floatView
				.findViewById(R.id.float_view_title);
		Bundle data = getArguments();
		int orderId = data.getInt(Const.STA_ORDER_ID);
		detail_text.setText(orderId + "." + data.getString(Const.STA_TEXT));
	}

	private void onCreateSearchViewPager(View view) {
		// TODO Auto-generated method stub
//		String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
		String[] tabItemsTitle = LocalMgr.getFPlaceType();
		((ViewGroup)view).addView(ViewHelper.newMainView(getActivity(), getChildFragmentManager(),mCallback, tabItemsTitle));
	}

	String[] strMenuLables = null;
	ViewGroup menuContent = null;
	PopupWindow menuPopupWindow = null;
	public void onHeadClick(View view){
//		if(menuContent == null){
//			ListView list = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.list_pop_win, null);
//			list.setDividerHeight(0);
//			menuContent = list;
//			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//										int position, long id) {
//					ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.menu_contents);
//					for (int i = 0; i < vg.getChildCount(); i++) {
//						ViewGroup menuItem = (ViewGroup) vg.getChildAt(i);
//						TextView contentTitle = ((TextView) menuItem.getChildAt(0));
//						TextView item = ((TextView) ((ViewGroup)view).getChildAt(0));
//
//						if (contentTitle.getText().equals(item.getText())) {
//							int[] loc = new int[2];
//							menuItem.getLocationOnScreen(loc);
//							ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.ui_f_search_item_detail_root_scrollview);
//							int offset = loc[1] /*- scrollView.getMeasuredHeight()*/;
//							if (offset < 0) {
//								offset = 0;
//							}
//							scrollView.smoothScrollTo(0, offset);
//							break;
//						}
//					}
//					menuPopupWindow.dismiss();
//				}
//			});
//			PopWinListItemAdapter.newInstance(getActivity(), list, strMenuLables);
//		}
//		menuPopupWindow = ViewHelper.showPopupWindow(getActivity(),menuPopupWindow,view,menuContent);
	}
	private void onCreateSearchDetail(final View view) {
		Bundle b = getArguments();
		try{
			String jsonStr = b.getString(Const.PRI_JSON_DATA);
			JSONObject resultData = new JSONObject(jsonStr);
			if(resultData != null && Const.DEFT_1.equals(resultData.optString(Const.STA_CODE))){
				JSONObject json = resultData.optJSONObject(Const.STA_DATA);
				final int fieldId = json.optInt(Const.STA_ID);
				((TextView) view.findViewById(R.id.search_item_detail_title)).setText(json.optString(Const.STA_NAME));

				View bannerParent = view.findViewById(R.id.search_item_detail_banner_parent);
				makeDetailBanner(view, json, bannerParent);
				makeMenuView(view, json);

				//关注
				int careCount = json.optInt(Const.STA_CARE_COUNT);//总关注数
				if(json.has(Const.STA_MEMBERS)){
					JSONArray members = json.optJSONArray(Const.STA_MEMBERS);
					makeCareView(view.findViewById(R.id.care_container), fieldId, members);
				}
				//评论
				int commentSize = json.optInt(Const.STA_COMCOUNT);//总评论数
				if(json.has(Const.STA_COMMENTS)){
					JSONArray comments = json.optJSONArray(Const.STA_COMMENTS);
					ViewGroup commentListView = (ViewGroup) view.findViewById(R.id.last_comment);
					makeCommentView(comments, commentListView);
				}
				//构造详情底部栏
				int zanCount = json.optInt(Const.STA_ZANCOUNT);
				{
					makeBottomView(view, json, fieldId, careCount, commentSize, zanCount);
				}
//				view.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						android.view.ViewGroup.LayoutParams lp = view.findViewById(R.id.detail_bottom_hr).getLayoutParams();
//						lp.height = view.findViewById(R.id.detail_bottom_bar).getMeasuredHeight();
//						view.findViewById(R.id.detail_bottom_hr).setLayoutParams(lp);
//					}
//				}, 500);
//				final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.ui_f_search_item_detail_root_scrollview);
//				scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
//					int barHeight = view.findViewById(R.id.detail_bottom_bar).getHeight();
//					{
//						view.findViewById(R.id.detail_bottom_hr).getLayoutParams().height = barHeight;
//						view.findViewById(R.id.detail_bottom_hr).setVisibility(View.INVISIBLE);
//					}
//					@Override
//					public void onScroll(int l, int t, int oldl, int oldt) {
//						ViewGroup commentListView = (ViewGroup) view.findViewById(R.id.last_comment);
//						Log.d("yl",t + ";" + scrollView.getHeight() + ";" + commentListView.getHeight() + ";" + view.findViewById(R.id.detail_bottom_bar).getHeight());
//						if(t > oldt){//向下滚动
//							if(t <= scrollView.getHeight() - barHeight){
//								view.findViewById(R.id.detail_bottom_bar).setVisibility(View.VISIBLE);
//							}else {
//								view.findViewById(R.id.detail_bottom_bar).setVisibility(View.GONE);
//							}
//						}else if(t < oldt){
//							view.findViewById(R.id.detail_bottom_bar).setVisibility(View.VISIBLE);
//						}
//					}
//				});
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void makeMenuView(final View view, JSONObject json)
			throws JSONException {
		String introduction = json.optString(Const.STA_INTRODUCTION);
		((TextView)view.findViewById(R.id.value)).setText(introduction);
		final ViewGroup menu_contents = (ViewGroup)view.findViewById(R.id.menu_contents);
		final ViewGroup menu_item_contents = (ViewGroup)view.findViewById(R.id.menu_item_contents);
		{
			//撒鱼动态
			if (json.has(Const.STA_PIRCES)) {
				AutoLayoutViewGroup container = new AutoLayoutViewGroup(getActivity());
				menu_item_contents.addView(container,-2,-2);
				JSONArray datas = new JSONArray(json.getString(Const.STA_PIRCES));
				makePriceView(container, datas);
			}
			//配套环境
			TextView surroundingView = new TextView(getActivity());
			surroundingView.setTextColor(0xFF888888);
			surroundingView.setText(json.optString(Const.STA_SURROUNDING));
			menu_item_contents.addView(surroundingView,-2,-2);
			//场馆图片
			TextView imgsView = new TextView(getActivity());
			imgsView.setText("无");
			imgsView.setTextColor(0xFF888888);
			menu_item_contents.addView(imgsView, -2, -2);
			//联系我们
			TextView contactUsView = new TextView(getActivity());
			contactUsView.setTextColor(0xFF888888);
			contactUsView.setText(json.optString(Const.STA_CONTACS_US));
			menu_item_contents.addView(contactUsView, -2, -2);
		}
		OnClickListener listener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				updateState(menu_contents,menu_item_contents,(Integer)v.getTag(),null);
			}
		};
		updateState(menu_contents,menu_item_contents,0,listener);
	}

	private void makeDetailBanner(final View view, JSONObject json,
			View bannerParent) throws JSONException {
		if (json.has(Const.STA_IMGS)) {//根据img是准备banner
			final ViewPager banner = (ViewPager) view.findViewById(R.id.search_item_detail_banner);
			JSONArray urlArr = json.getJSONArray(Const.STA_IMGS);
			int len = urlArr.length();
			if (len > 0) {
				bannerParent.setVisibility(View.VISIBLE);
				LayoutInflater inflator = LayoutInflater.from(getActivity());
//						ImageLoader iLoader = ImageLoader.self();
//						{
//							int viewPagerHeight = getResources().getDisplayMetrics().widthPixels * 9 /16;//保持16:9的比例
//							bannerParent.getLayoutParams().height = viewPagerHeight;
//						}
				ArrayList<ImageView> pviews = new ArrayList<ImageView>(len);
				final ViewGroup focusItems = (ViewGroup)view.findViewById(R.id.search_item_detail_banner_focus_items);
				int focusItemSize = getActivity().getResources().getDimensionPixelSize(R.dimen.focus_item_size);
				final MyPagerAdapter pageAdapter = new MyPagerAdapter(pviews);
				for (int i = 0; i < len; i++) {
					JSONObject urlJson = urlArr.optJSONObject(i);
					String url = urlJson.getString(Const.STA_IMGURL);
					ImageView iv = (ImageView) inflator.inflate(R.layout.w_imageview,null);
					ViewHelper.load(iv, url);
					pviews.add(iv);
					TextView item = new TextView(getActivity());
					item.setLayoutParams(new LayoutParams(focusItemSize,focusItemSize));
					if(i == 0){
						item.setBackgroundResource(R.drawable.circle_gray_solid_focus);
					}else{
						item.setBackgroundResource(R.drawable.circle_gray_solid);
					}
					focusItems.addView(item);
				}
				banner.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						ViewHelper.updateChildrenBackground(focusItems, arg0, R.drawable.circle_gray_solid_focus, R.drawable.circle_gray_solid);
					}
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {}
					@Override
					public void onPageScrollStateChanged(int arg0) {}
				});
				banner.setAdapter(pageAdapter);
			}
		}else{
			bannerParent.setVisibility(View.GONE);
		}
	}
	//构造渔场详情页，底部栏
	private void makeBottomView(final View view, JSONObject json,
			final int fieldId, int careCount, int commentSize, int zanCount) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(final View v) {
				int id = v.getId();
				if(id == R.id.detail_bottom_bar_care_icon){
					final View destView = ((ImageView)((ViewGroup)v).getChildAt(0));
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put(Const.STA_FIELDID, fieldId);
						jsonObject.put(Const.STA_TYPE, (destView.isSelected() ? "qxgz":"gz"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					NetTool.data().http(new NetTool.RequestListener() {
						@Override
						public void onEnd(byte[] data) {
							JSONObject response = toJSONObject(data);
							if (isRight(response)) {
								destView.setSelected(!destView.isSelected());
								int careCount = response.optInt(Const.STA_CARE_COUNT);
								((TextView) view.findViewById(R.id.detail_bottom_bar_care_number)).setText(String.valueOf(careCount));
//											((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
							} else {
//											ViewHelper .showToast(getActivity(), Const.DEFT_GET_CHECK_CODE_FAILED);
							}
						}
					},jsonObject, UrlUtils.self().getAttention());
					
				}else if(id == R.id.detail_bottom_bar_comment_icon){
					v.setTag(fieldId);
					int oldCount = Integer.parseInt(((TextView) view.findViewById(R.id.detail_bottom_bar_comment_number)).getText().toString());
					((TextView) view.findViewById(R.id.detail_bottom_bar_comment_number)).setText(String.valueOf(oldCount + 1));
					((BaseUI)getActivity()).onCommentReplyClick(v);
				}else if(id == R.id.detail_bottom_bar_zan_icon){//点赞
					final View destView = ((ViewGroup)v).getChildAt(0);
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put(Const.STA_FIELDID, fieldId);
						jsonObject.put(Const.STA_TYPE, (destView.isSelected() ? "qxzan":"zan")); 
					} catch (JSONException e) {
						e.printStackTrace();
					}
					NetTool.data().http(new NetTool.RequestListener() {
						@Override
						public void onEnd(byte[] data) {
							JSONObject response = toJSONObject(data);
							if (isRight(response)) {
								int zanCount = response.optInt(Const.STA_ZANCOUNT);
								((TextView) view.findViewById(R.id.detail_bottom_bar_zan_number)).setText(String.valueOf(zanCount));
								destView.setSelected(!destView.isSelected());
//											((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
							} else {
//											ViewHelper .showToast(getActivity(), Const.DEFT_GET_CHECK_CODE_FAILED);
							}
						}
					},jsonObject, UrlUtils.self().getPraise());
				}
			}
		};
		
		boolean isAttention = json.optBoolean(Const.STA_IS_ATTENTION) ;
		if(isAttention){
			((ViewGroup)view.findViewById(R.id.detail_bottom_bar_care_icon)).getChildAt(0).setSelected(true);
		}
		boolean isZan = json.optBoolean(Const.STA_IS_ZAN) ;
		if(isZan){
			((ViewGroup)( view.findViewById(R.id.detail_bottom_bar_zan_icon))).getChildAt(0).setSelected(true);
		}
		( view.findViewById(R.id.detail_bottom_bar_care_icon)).setOnClickListener(clickListener);
		( view.findViewById(R.id.detail_bottom_bar_comment_icon)).setOnClickListener(clickListener);
		( view.findViewById(R.id.detail_bottom_bar_zan_icon)).setOnClickListener(clickListener);

		((TextView) view.findViewById(R.id.detail_bottom_bar_care_number)).setText(String.valueOf(careCount));
		((TextView)view.findViewById(R.id.detail_bottom_bar_comment_number)).setText(String.valueOf(commentSize));
		((TextView)view.findViewById(R.id.detail_bottom_bar_zan_number)).setText(String.valueOf(zanCount));
	}

	private void makeCommentView(JSONArray comments, ViewGroup commentListView) {
		int commentSizeThumb = comments.length();//详情页返回评论数
		for(int i = 0; i < 3;  i++){
			boolean able = true;
			View item = commentListView.getChildAt(i);
			JSONObject commentJson = comments.optJSONObject(0);
			if(commentSizeThumb <= 3){
				if(i < commentSizeThumb){
					item.setVisibility(View.VISIBLE);
				}else{
					able = false;
					item.setVisibility(View.GONE);
				}
			}else{
				item.setVisibility(View.VISIBLE);
			}
			if(able){
				item.findViewById(R.id.comment_listitem_reply).setVisibility(View.GONE);
				ImageView iv = (ImageView)item.findViewById(R.id.comment_listitem_icon);
				String url = commentJson.optString(Const.STA_IMGURL);
				ViewHelper.load(iv, url, true, false);
				TextView comment_listitem_name = (TextView)item.findViewById(R.id.comment_listitem_name);
				comment_listitem_name.setText(commentJson.optString(Const.STA_NAME));
				TextView comment_listitem_time = (TextView)item.findViewById(R.id.comment_listitem_time);
				String str_time = commentJson.optString(Const.STA_CREATED_AT_TIME);
				comment_listitem_time.setText(BaseUtils.getTimeShow(str_time));
				TextView comment_listitem_text = (TextView)item.findViewById(R.id.comment_listitem_text);
				comment_listitem_text.setText(commentJson.optString(Const.STA_COMMENT_STR));
			}
		}
	}

	private void makeCareView(final View view, final int fieldId,
			JSONArray members) {//构造关注栏
		int careCountThumb = members.length();//详情页待会关注数，永远小总关注数
		OnClickListener careIcon = new OnClickListener(){
			@Override
			public void onClick(View v) {
				((BaseUI)getActivity()).onIconClick(v);
			}
		};
		ViewGroup care_container = (ViewGroup)view;
		for (int i = 0 ; i < 7; i++){
			ImageView careItem = (ImageView) ((ViewGroup)care_container.getChildAt(i)).getChildAt(0);
			careItem.setOnClickListener(careIcon);
			if(careCountThumb > 7){
				//显示剩余20+
				if(i == 6){
					careItem.setTag("more|" + fieldId);
					TextDrawable td = new TextDrawable(getActivity());
					td.setTextSize(getResources().getDimensionPixelSize(R.dimen.base_font_size_h6));
					td.setTextColor(0xFF888888);
					td.setSize(200, 200);
					String text = null;
					if(careCountThumb - 6 > 99){
						text = "99+";
					}else{
						text = String.valueOf(careCountThumb - 6);
					}
					td.setText(text);
					td.setTextAlign(Layout.Alignment.ALIGN_CENTER);
					careItem.setImageDrawable(td);
				}else if(i > careCountThumb){
					careItem.setVisibility(View.GONE);
				}else {
					JSONObject member = members.optJSONObject(0);
					if(!TextUtils.isEmpty(member.optString(Const.STA_IMGURL))){
//						String imgUrl = "http://images.banma.com/v0/app-feed/soft/icons/_bigIcon76e029e26dce486c9c969c23b99bf31f.png";
						String imgUrl = member.optString(Const.STA_IMGURL, member.optString(Const.STA_IMGURL));
						ViewHelper.load(careItem, imgUrl, true, false);
					}
					int id = member.optInt(Const.STA_ID);
					careItem.setTag(id);
				}
			}else{
				if(careCountThumb < 7 && i >= careCountThumb) {
					careItem.setVisibility(View.GONE);
				}else{
					JSONObject member = members.optJSONObject(0);
					if(!TextUtils.isEmpty(member.optString(Const.STA_IMGURL))){
//						String imgUrl = "http://images.banma.com/v0/app-feed/soft/icons/_bigIcon76e029e26dce486c9c969c23b99bf31f.png";
						String imgUrl = member.optString(Const.STA_IMGURL, member.optString(Const.STA_IMGURL));
						ViewHelper.load(careItem, imgUrl, true, false);
					}
					int id = member.optInt(Const.STA_ID);
					careItem.setTag(id);
				}
			}
		}
	}

	private void makePriceView(AutoLayoutViewGroup container, JSONArray datas) {
		try {
			int size = datas.length();
			int PADDING = 5;
			int w = (getResources().getDisplayMetrics().widthPixels - PADDING * 10) / 4;
			int h = w * getResources().getDimensionPixelSize(R.dimen.calender_item_height) / getResources().getDimensionPixelSize(R.dimen.calender_item_width);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = new Date();
			ArrayList<View> arr = new ArrayList<View>();
			for (int i = 0; i < size; i++) {
				ViewGroup calendarItem = (ViewGroup)LayoutInflater.from(getActivity()).inflate(R.layout.calender_item, null);
				calendarItem.setPadding(PADDING, PADDING, PADDING, PADDING);
				calendarItem.setBackgroundResource(R.drawable.base_background);
				arr.add(calendarItem);
				JSONObject item = datas.getJSONObject(i);
				String priceTitle = item.getString(Const.STA_PRICE_TITLE);
				String date = item.getString(Const.STA_PRICE_DATE);
				int type = -1;
				try {
					Date pd = sdf.parse(date);
					if("正".equals(priceTitle)){
						type = 1;
						if(!pd.after(curDate)){
							type = -1;
						}
					}else{
						type = 2;
						if(!pd.after(curDate)){
							type = -2;
						}
					}
					date = (pd.getMonth() + 1) + "月" + pd.getDate() + "日";
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int money = item.getInt(Const.STA_PRICE);
				View statusView = calendarItem.findViewById(R.id.calendar_pic_status);
				TextView moneyView = (TextView) calendarItem.findViewById(R.id.calendar_money);
				TextView dateView = (TextView) calendarItem.findViewById(R.id.calendar_date);
				moneyView.setText(money + "￥");

				dateView.setText(date);
				switch (type) {
					case -1:
						moneyView.setTextColor(getResources().getColor(R.color.calender_disable_money_text_color));
						dateView.setTextColor(getResources().getColor(R.color.calender_disable_text_color));
						dateView.setBackgroundColor(getResources().getColor(R.color.calender_disable_bg_color));
						statusView.setBackgroundResource(R.drawable.z_);
						break;
					case 1:
						moneyView.setTextColor(getResources().getColor(R.color.calender_money_text_color));
						dateView.setTextColor(getResources().getColor(R.color.calender_z_text_color));
						dateView.setBackgroundColor(getResources().getColor(R.color.calender_z_bg_color));
						statusView.setBackgroundResource(R.drawable.z);
						break;
					case -2:
						moneyView.setTextColor(getResources().getColor(R.color.calender_disable_money_text_color));
						dateView.setTextColor(getResources().getColor(R.color.calender_disable_text_color));
						dateView.setBackgroundColor(getResources().getColor(R.color.calender_disable_bg_color));
						statusView.setBackgroundResource(R.drawable.t_);
						break;
					case 2:
						moneyView.setTextColor(getResources().getColor(R.color.calender_money_text_color));
						dateView.setTextColor(getResources().getColor(R.color.calender_t_text_color));
						dateView.setBackgroundColor(getResources().getColor(R.color.calender_t_bg_color));
						statusView.setBackgroundResource(R.drawable.t);
						break;
				}

			}
			for(int i = 0; i < arr.size() ; i++){
				container.addView(arr.get(i),w,h);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	int[] s_titile = new int[]{R.string.sayu_news,R.string.surrounding,R.string.imgs,R.string.contact_us};
	int[] icons = new int[]{R.drawable.incon_114,R.drawable.incon_115,R.drawable.incon_116,R.drawable.incon_117};
	int[] icons_ = new int[]{R.drawable.incon_114_,R.drawable.incon_115_,R.drawable.incon_116_,R.drawable.incon_117_};
	private void updateState(ViewGroup menu_contents,ViewGroup menu_item_contents,int postion,OnClickListener listener){
		for(int i = 0; i < menu_contents.getChildCount() ; i++){
			ViewGroup mr = (ViewGroup)((ViewGroup)menu_contents.getChildAt(i)).getChildAt(0);
			mr.setTag(i);
			if(listener != null) {
				mr.setOnClickListener(listener);
			}
			ImageView icon = (ImageView)mr.getChildAt(2);
			TextView btn = (TextView)mr.getChildAt(3);
			TextView line = (TextView)mr.getChildAt(4);
			btn.setText(s_titile[i]);
			if(i == postion) {
				icon.setImageResource(icons[i]);
				btn.setTextColor(getResources().getColor(R.color.base_btn_color));
				line.setVisibility(View.VISIBLE);
				if(menu_item_contents.getChildCount() > i){
					menu_item_contents.getChildAt(i).setVisibility(View.VISIBLE);
				}
			}else{
				icon.setImageResource(icons_[i]);
				btn.setTextColor(getResources().getColor(R.color.menu_item_text_color));
				line.setVisibility(View.INVISIBLE);
				if(menu_item_contents.getChildCount() > i){
					menu_item_contents.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "TestFragment-----onPause");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "TestFragment-----onResume");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		isFront = !hidden;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "TestFragment-----onDestroy");
	}

	public interface ResultForActivityCallback {
		void onItemClick(View view, FPlaceData data);
	}
}
