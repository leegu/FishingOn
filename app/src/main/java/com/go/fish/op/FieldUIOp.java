package com.go.fish.op;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FieldData;
import com.go.fish.data.load.FieldDataLoader;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AutoLayoutViewGroup;
import com.go.fish.view.FPlaceListAdapter;
import com.go.fish.view.IBaseData;
import com.go.fish.view.MyPagerAdapter;
import com.go.fish.view.TextDrawable;
import com.go.fish.view.ViewHelper;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class FieldUIOp {

	static class FPlaceViewHolder{
		TextView listitem_fplace_title,fplace_desc,float_view_distance;
		ImageView listitem_fplace_icon;
		ViewGroup fplace_state,listitem_field_tags;
		View float_view_detail_btn;
	}
	
	public static final int FLAG_SEARCH_RESULT = 0;
	public static final int FLAG_NEAR_RESULT = 1;
	public static final int FLAG_CARE_RESULT = 2;
	static int[] s_titile = new int[]{R.string.sayu_news,R.string.surrounding,R.string.imgs,R.string.contact_us};
	
	static int[] icons = new int[]{R.drawable.incon_114,R.drawable.incon_115,R.drawable.incon_116,R.drawable.incon_117};
	static int[] icons_ = new int[]{R.drawable.incon_114_,R.drawable.incon_115_,R.drawable.incon_116_,R.drawable.incon_117_};
	
	//构造渔场详情页，底部栏
	private static void  makeBottomView(final Activity activity,final View view, JSONObject json,
			final int fieldId, int careCount, int commentSize, int zanCount) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(final View v) {
				int id = v.getId();
				if(id == R.id.detail_bottom_bar_care_icon){
					ImageView stateView = ((ImageView)((ViewGroup)v).getChildAt(0));
					TextView textView = ((TextView) view.findViewById(R.id.detail_bottom_bar_care_number));
					onCareFieldClick(stateView, textView, String.valueOf(fieldId));
				}else if(id == R.id.detail_bottom_bar_comment_icon){
					v.setTag(fieldId);
//					int oldCount = Integer.parseInt(((TextView) view.findViewById(R.id.detail_bottom_bar_comment_number)).getText().toString());
//					((TextView) view.findViewById(R.id.detail_bottom_bar_comment_number)).setText(String.valueOf(oldCount + 1));
					((BaseUI)activity).onCommentReplyClick(v);
				}else if(id == R.id.detail_bottom_bar_zan_icon){//点赞
					ImageView stateView = (ImageView)((ViewGroup)v).getChildAt(0);
					TextView textView = ((TextView) view.findViewById(R.id.detail_bottom_bar_zan_number));
					onPriseFieldClick(stateView, textView, String.valueOf(fieldId));
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

	private static void  makeCareView(final Activity activity,final View view, final int fieldId,
			JSONArray members) {//构造关注栏
		int careCountThumb = members.length();//详情页待会关注数，永远小于关注数
		ViewGroup care_container = (ViewGroup)view;
		care_container.setTag(fieldId);
		for (int i = 0 ; i < 7; i++){
			ImageView careItem = (ImageView) ((ViewGroup)care_container.getChildAt(i)).getChildAt(0);
//			careItem.setOnClickListener(careIcon);
			if(careCountThumb > 7){
				//显示剩余20+
				if(i == 6){
//					careItem.setTag("more|" + fieldId);
					TextDrawable td = new TextDrawable(activity);
					td.setTextSize(activity.getResources().getDimensionPixelSize(R.dimen.base_font_size_h6));
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
					JSONObject member = members.optJSONObject(i);
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
					JSONObject member = members.optJSONObject(i);
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

	private static void  makeCommentView(JSONArray comments, ViewGroup commentListView) {
		int commentSizeThumb = comments.length();//详情页返回评论数
		for(int i = 0; i < 3;  i++){
			boolean able = true;
			View item = commentListView.getChildAt(i);
			JSONObject commentJson = comments.optJSONObject(i);
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
				if(!TextUtils.isEmpty(url)){
					ViewHelper.load(iv, url, true, false);
				}
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
	
	private static void makeDetailBanner(Activity activity,final View view, JSONObject json,
			View bannerParent) throws JSONException {
		if (json.has(Const.STA_IMGS)) {//根据img是准备banner
			final ViewPager banner = (ViewPager) view.findViewById(R.id.search_item_detail_banner);
			JSONArray urlArr = json.getJSONArray(Const.STA_IMGS);
			int len = urlArr.length();
			if (len > 0) {
				bannerParent.setVisibility(View.VISIBLE);
				LayoutInflater inflator = LayoutInflater.from(activity);
//						ImageLoader iLoader = ImageLoader.self();
//						{
//							int viewPagerHeight = getResources().getDisplayMetrics().widthPixels * 9 /16;//保持16:9的比例
//							bannerParent.getLayoutParams().height = viewPagerHeight;
//						}
				ArrayList<ImageView> pviews = new ArrayList<ImageView>(len);
				final ViewGroup focusItems = (ViewGroup)view.findViewById(R.id.search_item_detail_banner_focus_items);
				int focusItemSize = activity.getResources().getDimensionPixelSize(R.dimen.focus_item_size);
				final MyPagerAdapter pageAdapter = new MyPagerAdapter(pviews);
				for (int i = 0; i < len; i++) {
					JSONObject urlJson = urlArr.optJSONObject(i);
					String url = urlJson.getString(Const.STA_IMGURL);
					ImageView iv = (ImageView) inflator.inflate(R.layout.w_imageview,null);
					ViewHelper.load(iv, url);
					pviews.add(iv);
					TextView item = new TextView(activity);
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
					public void onPageScrolled(int arg0, float arg1, int arg2) {}
					@Override
					public void onPageScrollStateChanged(int arg0) {}
					@Override
					public void onPageSelected(int arg0) {
						ViewHelper.updateChildrenBackground(focusItems, arg0, R.drawable.circle_gray_solid_focus, R.drawable.circle_gray_solid);
					}
				});
				banner.setAdapter(pageAdapter);
			}
		}else{
			bannerParent.setVisibility(View.GONE);
		}
	}
	private static void makeMenuView(final Activity activity,final View view, JSONObject json)
			throws JSONException {
		String introduction = json.optString(Const.STA_INTRODUCTION);
		((TextView)view.findViewById(R.id.value)).setText(introduction);
		final ViewGroup menu_contents = (ViewGroup)view.findViewById(R.id.menu_contents);
		final ViewGroup menu_item_contents = (ViewGroup)view.findViewById(R.id.menu_item_contents);
		{
			//撒鱼动态
			if (json.has(Const.STA_PIRCES)) {
//				AutoLayoutViewGroup container = new AutoLayoutViewGroup(getActivity());
//				menu_item_contents.addView(container,-2,-2);
//				JSONArray datas = new JSONArray(json.getString(Const.STA_PIRCES));
//				makePriceView(container, datas);
				LinearLayout container = new LinearLayout(activity);
				container.setOrientation(LinearLayout.VERTICAL);
				menu_item_contents.addView(container,-2,-2);
				JSONArray pricesArr = json.optJSONArray(Const.STA_PIRCES);
				for(int i = 0;i < pricesArr.length(); i++){
					JSONObject price = pricesArr.optJSONObject(i);
					ViewGroup vg = (ViewGroup)LayoutInflater.from(activity).inflate(R.layout.listitem_fishing_news_1_row, null);
					TextView tv = (TextView)vg.getChildAt(0);
					tv.setTag(price.opt(Const.STA_ID));
					tv.setText(price.optString(Const.STA_TITLE));
					container.addView(vg);
				}
			}
			//配套环境
			TextView surroundingView = new TextView(activity);
			surroundingView.setTextColor(0xFF888888);
			surroundingView.setText(json.optString(Const.STA_SURROUNDING));
			menu_item_contents.addView(surroundingView,-2,-2);
			//场馆图片
			TextView imgsView = new TextView(activity);
			imgsView.setText("无");
			imgsView.setTextColor(0xFF888888);
			menu_item_contents.addView(imgsView, -2, -2);
			//联系我们
			TextView contactUsView = new TextView(activity);
			contactUsView.setTextColor(0xFF888888);
			contactUsView.setText(json.optString(Const.STA_CONTACS_US));
			menu_item_contents.addView(contactUsView, -2, -2);
		}
		OnClickListener listener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				updateState(activity,menu_contents,menu_item_contents,(Integer)v.getTag(),null);
			}
		};
		updateState(activity,menu_contents,menu_item_contents,0,listener);
	}
	private static void  makePriceView(Activity activity,AutoLayoutViewGroup container, JSONArray datas) {
		try {
			int size = datas.length();
			int PADDING = 5;
			Resources res = activity.getResources();
			int w = (res.getDisplayMetrics().widthPixels - PADDING * 10) / 4;
			int h = w * res.getDimensionPixelSize(R.dimen.calender_item_height) / res.getDimensionPixelSize(R.dimen.calender_item_width);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = new Date();
			ArrayList<View> arr = new ArrayList<View>();
			for (int i = 0; i < size; i++) {
				ViewGroup calendarItem = (ViewGroup)LayoutInflater.from(activity).inflate(R.layout.calender_item, null);
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
						moneyView.setTextColor(res.getColor(R.color.calender_disable_money_text_color));
						dateView.setTextColor(res.getColor(R.color.calender_disable_text_color));
						dateView.setBackgroundColor(res.getColor(R.color.calender_disable_bg_color));
						statusView.setBackgroundResource(R.drawable.z_);
						break;
					case 1:
						moneyView.setTextColor(res.getColor(R.color.calender_money_text_color));
						dateView.setTextColor(res.getColor(R.color.calender_z_text_color));
						dateView.setBackgroundColor(res.getColor(R.color.calender_z_bg_color));
						statusView.setBackgroundResource(R.drawable.z);
						break;
					case -2:
						moneyView.setTextColor(res.getColor(R.color.calender_disable_money_text_color));
						dateView.setTextColor(res.getColor(R.color.calender_disable_text_color));
						dateView.setBackgroundColor(res.getColor(R.color.calender_disable_bg_color));
						statusView.setBackgroundResource(R.drawable.t_);
						break;
					case 2:
						moneyView.setTextColor(res.getColor(R.color.calender_money_text_color));
						dateView.setTextColor(res.getColor(R.color.calender_t_text_color));
						dateView.setBackgroundColor(res.getColor(R.color.calender_t_bg_color));
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

	public static void onCareFieldClick(final ImageView stateView,final TextView textView, String fieldId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fieldId);
			jsonObject.put(Const.STA_TYPE, (stateView.isSelected() ? "qxgz":"gz"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				JSONObject response = toJSONObject(data);
				if (isRight(response)) {
					JSONObject dataResult = response.optJSONObject(Const.STA_DATA);
					int careCount = dataResult.optInt(Const.STA_CARE_COUNT);
					stateView.setSelected(!stateView.isSelected());
					if(textView != null){
						textView.setText(String.valueOf(careCount));
					}
//								((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
				} else {
//								ViewHelper .showToast(getActivity(), Const.DEFT_GET_CHECK_CODE_FAILED);
				}
			}
		},jsonObject, UrlUtils.self().getAttention());
	}

	public static void onCreateCareFieldView(final Activity activity,ViewGroup vg, OpBack backListener){
		onCreateCareFieldView(activity, vg, R.layout.listitem_field_3_rows, backListener);
	}
	/**
	 * 创建关注钓场页面
	 * @param activity
	 * @param vg
	 * @param backListener TODO
	 */
	public static void onCreateCareFieldView(final Activity activity,ViewGroup vg,int layoutId, OpBack backListener){
    	//创建 关注 页面
    	ListView fPlaceList = (ListView)vg.findViewById(R.id.ui_f_care_list);
    	ArrayList<FieldData> fPlaceArr = DataMgr.makeFPlaceDatas(layoutId, new JSONArray());
    	FPlaceListAdapter adapter = FPlaceListAdapter.setAdapter(activity,fPlaceList,fPlaceArr, FieldUIOp.FLAG_CARE_RESULT);
    	adapter.setmResultForActivityCallback(new ResultForActivityCallback() {
			@Override
			public void onItemClick(View view, FieldData data) {
				String fPlaceId = data.sid;
				FieldDataLoader.loadFieldInfo(fPlaceId, new OpBack() {
					@Override
					public void onBack(boolean suc, JSONObject json, Activity activity) {
						// TODO Auto-generated method stub
						Bundle newBundle = new Bundle();
						newBundle.putString(Const.PRI_JSON_DATA, json.toString());
						newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_detail_field);
						Intent i = new Intent();
						i.putExtras(newBundle);
						UIMgr.showActivity(activity, i, SearchUI.class.getName());
					}
				}, activity);
			}
		});
    	adapter.isAttentionList = true;
		// 网络数据抓取,进行更新
//		HomeFragment.getNetPodList(fPlaceList, "0");
    }

	/**
	 * 创建钓场详情页
	 * @param activity
	 * @param view
	 * @param resultData
	 */
	public static void onCreateFieldDetail(Activity activity,final View view,JSONObject resultData) {
		try{
			if(resultData != null && Const.DEFT_1.equals(resultData.optString(Const.STA_CODE))){
				JSONObject json = resultData.optJSONObject(Const.STA_DATA);
				final int fieldId = json.optInt(Const.STA_ID);
				((TextView) view.findViewById(R.id.search_item_detail_title)).setText(json.optString(Const.STA_NAME));

				View bannerParent = view.findViewById(R.id.search_item_detail_banner_parent);
				makeDetailBanner(activity,view, json, bannerParent);
				makeMenuView(activity,view, json);

				//关注
				int careCount = json.optInt(Const.STA_CARE_COUNT);//总关注数
				if(json.has(Const.STA_MEMBERS)){
					JSONArray members = json.optJSONArray(Const.STA_MEMBERS);
					makeCareView(activity,view.findViewById(R.id.care_container), fieldId, members);
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
					makeBottomView(activity,view, json, fieldId, careCount, commentSize, zanCount);
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
	public static View onGetFieldView(final Activity activity,LayoutInflater mInflater,int layout_id,int flag,final FieldData fPlace, View convertView, ViewGroup parent){//搜索结果展示，附件钓场
		ViewGroup item = null;
		FPlaceViewHolder mViewHolder = null;
		//周边调用列表
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
			mViewHolder = new FPlaceViewHolder();
			item.setTag(mViewHolder);
			mViewHolder.float_view_detail_btn = item.findViewById(R.id.float_view_detail_btn);
			mViewHolder.listitem_fplace_title = ((TextView)item.findViewById(R.id.listitem_fplace_title));
			mViewHolder.fplace_desc = ((TextView)item.findViewById(R.id.fplace_desc));
			mViewHolder.float_view_distance = ((TextView)item.findViewById(R.id.float_view_distance));
			mViewHolder.fplace_state = ((ViewGroup)item.findViewById(R.id.fplace_state));
			mViewHolder.listitem_fplace_icon = ((ImageView)item.findViewById(R.id.listitem_fplace_icon));
			if(layout_id == R.layout.listitem_field_3_rows){
				mViewHolder.listitem_field_tags = (ViewGroup)item.findViewById(R.id.listitem_friend_tags);
			}
		} else {
			item = (ViewGroup)convertView;
			mViewHolder = (FPlaceViewHolder)item.getTag();
		}
		if(!TextUtils.isEmpty(fPlace.imgUrl)){
			ViewHelper.load(mViewHolder.listitem_fplace_icon,fPlace.imgUrl,true,false);
		}
		mViewHolder.float_view_detail_btn.setTag(fPlace);
		mViewHolder.listitem_fplace_title.setText(fPlace.title);
		mViewHolder.fplace_desc.setText(fPlace.desp);
		mViewHolder.float_view_distance.setText(fPlace.distance);
		mViewHolder.fplace_state.getChildAt(1).setTag(String.valueOf(fPlace.sid));
		
		if(layout_id == R.layout.listitem_field_3_rows){
			if(mViewHolder.listitem_field_tags != null){
				mViewHolder.listitem_field_tags.setVisibility(View.VISIBLE);
				String[] tags = fPlace.tagArray;
				for(int i = 0; i < mViewHolder.listitem_field_tags.getChildCount(); i++){//只对前三个进行显示
					TextView tv = (TextView)mViewHolder.listitem_field_tags.getChildAt(i);
					if(i < tags.length){
						tv.setVisibility(View.VISIBLE);
						tv.setBackgroundColor(BaseUtils.getTagBg(tags[i]));
						tv.setText(tags[i]);
					}else{
						tv.setVisibility(View.GONE);
					}
				}
			}
		}
		if(flag == FLAG_SEARCH_RESULT){
			mViewHolder.fplace_state.getChildAt(0).setVisibility(View.GONE);
			mViewHolder.fplace_state.getChildAt(1).setVisibility(View.VISIBLE);
			mViewHolder.fplace_state.getChildAt(1).setTag(fPlace);
		}else if(flag == FLAG_CARE_RESULT){
			mViewHolder.fplace_state.getChildAt(1).setSelected(true);
		//
		}
		return item;
	}
	public static void onPriseFieldClick(final ImageView stateView,final TextView textView, String fieldId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fieldId);
			jsonObject.put(Const.STA_TYPE, (stateView.isSelected() ? "qxzan":"zan")); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				JSONObject response = toJSONObject(data);
				if (isRight(response)) {
					JSONObject dataResult = response.optJSONObject(Const.STA_DATA);
					int zanCount = dataResult.optInt(Const.STA_ZANCOUNT);
					textView.setText(String.valueOf(zanCount));
					stateView.setSelected(!stateView.isSelected());
//								((TextView) findViewById(R.id.checkCode)).setText(response.optJSONObject(Const.STA_DATA).optString(Const.STA_VALIDATECODE));
				} else {
//								ViewHelper .showToast(getActivity(), Const.DEFT_GET_CHECK_CODE_FAILED);
				}
			}
		},jsonObject, UrlUtils.self().getPraise());
	}
	public static void onShowCareFieldView(ViewGroup view){
		onShowCareFieldView(view, R.layout.listitem_field_3_rows);
	}
	public static void onShowCareFieldView(ViewGroup view,int layoutId){
		ListView fPlaceList = (ListView)view.findViewById(R.id.ui_f_care_list);
		// 网络数据抓取,进行更新
		FPlaceListAdapter adapter = (FPlaceListAdapter)fPlaceList.getAdapter();
		if(adapter.listDatas.size() == 0){
			FieldDataLoader.loadNetData(fPlaceList.getContext(),adapter , layoutId, "", fPlaceList.getAdapter().getCount(), LocalMgr.getFPlaceTypes());
		}
	}
	private static void updateState(Activity activity,ViewGroup menu_contents,ViewGroup menu_item_contents,int postion,OnClickListener listener){
		Resources res = activity.getResources();
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
				btn.setTextColor(res.getColor(R.color.base_btn_color));
				line.setVisibility(View.VISIBLE);
				if(menu_item_contents.getChildCount() > i){
					menu_item_contents.getChildAt(i).setVisibility(View.VISIBLE);
				}
			}else{
				icon.setImageResource(icons_[i]);
				btn.setTextColor(res.getColor(R.color.menu_item_text_color));
				line.setVisibility(View.INVISIBLE);
				if(menu_item_contents.getChildCount() > i){
					menu_item_contents.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}
	}
}
