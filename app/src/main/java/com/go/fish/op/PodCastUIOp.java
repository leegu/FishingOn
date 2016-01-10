package com.go.fish.op;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.data.PodCastData;
import com.go.fish.data.load.PodCastDataLoader;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UIMgr;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.HAutoAlign;
import com.go.fish.view.IBaseData;
import com.go.fish.view.MyPagerAdapter;
import com.go.fish.view.TextDrawable;
import com.go.fish.view.ViewHelper;

public class PodCastUIOp {

	public static void onCommentPodCastClick(Activity activity,final View stateView){
		ViewHelper.showToast(activity, Const.DEFT_NO_SUPPORT_COMMENT_PODCAST);
	}
	
	public static void onCarePodCastClick(final ImageView stateView, final TextView textView,String infoId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_INFO_ID, infoId);
			jsonObject.put(Const.STA_TYPE, (stateView.isSelected() ? "qxgz":"gz"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				JSONObject response = toJSONObject(data);
				if (isRight(response)) {
					stateView.setSelected(!stateView.isSelected());
					JSONObject dataResult = response.optJSONObject(Const.STA_DATA);
					int careCount = dataResult.optInt(Const.STA_CARE_COUNT);
					stateView.setSelected(!stateView.isSelected());
					if(textView != null){
						textView.setText(String.valueOf(careCount));
					}
				} else {
				}
			}
		},jsonObject, UrlUtils.self().getAttentionPod());
	}
	
	public static void onPraisePodCastClick(final ImageView stateView, final TextView textView,String infoId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_INFO_ID, infoId);
			jsonObject.put(Const.STA_TYPE, (stateView.isSelected() ? "qxgz":"gz"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				JSONObject response = toJSONObject(data);
				if (isRight(response)) {
					stateView.setSelected(!stateView.isSelected());
					JSONObject dataResult = response.optJSONObject(Const.STA_DATA);
					int careCount = dataResult.optInt(Const.STA_ZANCOUNT);
					stateView.setSelected(!stateView.isSelected());
					if(textView != null){
						textView.setText(String.valueOf(careCount));
					}
				} else {
				}
			}
		},jsonObject, UrlUtils.self().getpPraisePod());
	}
	
	public static void onCreatePodCastListView(final Activity activity,ViewGroup view) {//创建钓播列表 页面
		ViewGroup vg = (ViewGroup)view.findViewById(R.id.ui_fnews_list_root);
		OnBaseDataClickListener mOnBaseDataClickListener = new OnBaseDataClickListener(){
			@Override
			public void onItemClick(View view, IBaseData data) {
				data.OnClick(activity, null, view);
			}
		};
		{//最新钓播
//			final ListView fNewsList = new ReFreshListView(getActivity());
			final ListView fNewsList = (ListView)vg.getChildAt(0);
			fNewsList.setTag("0");
			//本地先获取显示
			final AdapterExt mListAdapter = AdapterExt.newInstance(fNewsList, mOnBaseDataClickListener, new JSONArray(), R.layout.listitem_podcast);
			fNewsList.setAdapter(mListAdapter);
		}
		{//my钓播
//			ListView fPlaceList = new ReFreshListView(getActivity());
			final ListView fPlaceList = (ListView)vg.getChildAt(1);
			AdapterExt ae = AdapterExt.newInstance(fPlaceList,mOnBaseDataClickListener,new JSONArray(), R.layout.listitem_podcast );
			ae.mFlag = AdapterExt.FLAG_MY_NEWS;
			fPlaceList.setTag(User.self().userInfo.mobileNum);
			fPlaceList.setAdapter(ae);
		}
	}
	
	public static void onShowPodCastListView(ViewGroup contentView){//显示钓播列表
		ViewGroup vg = (ViewGroup)contentView.findViewById(R.id.ui_fnews_list_root);
		for(int i = 0;i < vg.getChildCount() ; i++){
			ListView fNListView = (ListView)vg.getChildAt(i);
			if(fNListView.getVisibility() == View.VISIBLE){
				PodCastDataLoader.getNetPodList(fNListView,String.valueOf(fNListView.getTag()), true);
			}
		}
	}
	
	private static class PodCastViewHolder {
		TextView nameView,fYearView,fTimesView,textView,listitem_fnews_comment_count,publish_time;
		ImageView userIcon;
		View listitem_friend_layout,user_detail,listitem_fnews_care,listitem_fnews_good;
		ViewGroup listitem_friend_tags;
		HAutoAlign mHAutoAlign;
		View[] childViews = null;
	}
	public static View onGetPodCastView(final Activity activity,LayoutInflater mInflater,int layout_id,final int position, int mFlag,ArrayList<IBaseData> listDatas, View convertView, ViewGroup parent){
		PodCastViewHolder holder = null;
		final PodCastData newsData = (PodCastData)listDatas.get(position);
		View item = null;
		if (convertView == null) {
			item = mInflater.inflate(layout_id, parent, false);
			holder = new PodCastViewHolder();
			item.setTag(holder);
			View fLayout = item.findViewById(R.id.listitem_friend_person_layout);
			holder.user_detail = item.findViewById(R.id.user_detail);
			holder.listitem_fnews_care = item.findViewById(R.id.listitem_fnews_care);
			holder.listitem_fnews_good = item.findViewById(R.id.listitem_fnews_good);
			holder.listitem_friend_layout = fLayout;
			holder.nameView = (TextView)fLayout.findViewById(R.id.listitem_friend_name);
			holder.fYearView = (TextView)fLayout.findViewById(R.id.listitem_friend_fyear);
			holder.fTimesView = (TextView)fLayout.findViewById(R.id.listitem_friend_ftimes);
			holder.listitem_friend_tags = (ViewGroup)fLayout.findViewById(R.id.listitem_friend_tags);
			
			holder.textView = (TextView)item.findViewById(R.id.textView);
			holder.listitem_fnews_comment_count = (TextView)item.findViewById(R.id.listitem_fnews_comment_count);
			holder.publish_time = (TextView)item.findViewById(R.id.publish_time);
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
		} else {
			holder = (PodCastViewHolder)convertView.getTag();
			item = convertView;
		}
		holder.listitem_fnews_care.setSelected(newsData.isAttentaion);
		holder.listitem_fnews_care.setTag(newsData.id);
		holder.listitem_fnews_good.setTag(newsData.id);
		holder.listitem_fnews_good.setSelected(newsData.isZan);
		
		if(newsData.netPicUrl != null){//当有钓播图片时
			int size = newsData.netPicUrl.length;
			ViewStub vs = (ViewStub)item.findViewById(R.id.fnews_image_contianer_view_stub);
			if(vs != null){//还没有初始化需要inflate
				holder.mHAutoAlign = (HAutoAlign)((ViewGroup)vs.inflate()).getChildAt(0);
			}else{
				holder.mHAutoAlign = (HAutoAlign)item.findViewById(R.id.h_image_view_container);
			}
			holder.childViews = new LinearLayout[size];
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(Const.PRI_EXTRA_IMAGE_INDEX, 0);
					intent.putExtra(Const.PRI_EXTRA_IMAGE_URLS, newsData.netPicUrl);
					UIMgr.showActivity(activity,intent,ImageViewUI.class.getName());
				}
			};
			for(int i = 0; i < size; i++){
				holder.childViews[i] = mInflater.inflate(R.layout.h_image_view_item, null);
//					holder.childViews[i].setBackgroundColor((int)new Random().nextLong());
//					holder.childViews[i].setPadding(p,p,p,p);
				String url = newsData.netPicUrl[i];
//					url = "http://f.hiphotos.baidu.com/image/h%3D200/sign=129e451332fae6cd13b4ac613fb20f9e/1e30e924b899a901c9bdff121a950a7b0208f50e.jpg";
				holder.childViews[i].setTag(url);
				holder.childViews[i].setOnClickListener(listener);
				ViewHelper.load((ImageView)((ViewGroup)holder.childViews[i]).getChildAt(0), (String)holder.childViews[i].getTag(), true,false);
			}
			holder.mHAutoAlign.fillChilds(holder.childViews);
		}else{
			holder.mHAutoAlign.removeAllViews();
			holder.mHAutoAlign.setVisibility(View.GONE);
		}

		if( position != 0 &&  mFlag == AdapterExt.FLAG_MY_NEWS){//不需要当前用户信息
			holder.listitem_friend_layout.setVisibility(View.GONE);
			holder.publish_time.setVisibility(View.VISIBLE);
			String showTime = BaseUtils.getTimeShow(newsData.publishTime);
			if(!TextUtils.isEmpty(showTime)){
				holder.publish_time.setText(showTime);
			}else{
				
			}
		}else{//需要用户信息
			holder.user_detail.setTag(newsData.authorData);
			if(holder.listitem_friend_tags != null){
				holder.listitem_friend_tags.setVisibility(View.VISIBLE);
				String[] tags = newsData.authorData.tagArray;
				for(int i = 0; i < holder.listitem_friend_tags.getChildCount(); i++){//只对前三个进行显示
					TextView tv = (TextView)holder.listitem_friend_tags.getChildAt(i);
					if(i < tags.length){
						tv.setVisibility(View.VISIBLE);
						tv.setBackgroundColor(BaseUtils.getTagBg(tags[i]));
						tv.setText(tags[i]);
					}else{
						tv.setVisibility(View.GONE);
					}
				}
			}
			
			if(!TextUtils.isEmpty(newsData.authorData.photoUrl)){
				String url = newsData.authorData.photoUrl;
//				url = "http://img2.baobao88.com/bbfile/allimg/101021/10930462436-14.gif";
				ViewHelper.load(holder.userIcon,  UrlUtils.self().getNetUrl(url),true,false);
			}
			holder.publish_time.setVisibility(View.GONE);
			holder.nameView.setText(newsData.authorData.userName /*+ "--" + position*/);
			holder.fYearView.setText(""+newsData.authorData.fYears);
			holder.fTimesView.setText(""+newsData.authorData.fTimes);
		}
		if(holder.mHAutoAlign != null) holder.mHAutoAlign.scrollTo(0, 0);
		holder.textView.setText(newsData.content);
		holder.textView.setTag(new Object[]{newsData.authorData,newsData.id});
		if(newsData.commentCount > 0){holder.listitem_fnews_comment_count.setText(""+newsData.commentCount);}else{holder.listitem_fnews_comment_count.setVisibility(View.GONE);}
//		final FNewsViewHolder f_holder = holder;
//		if(newsData.netPicUrl != null && f_holder.childViews != null){
//			item.postDelayed(new Runnable() {
//				@Override
//				public void run() {
////					if(position >= mFirstVisibleItem && position <= mFirstVisibleItem + mVisibleItemCount){
////						for(int i = 0; i < f_holder.childViews.length; i++){
////							ViewHelper.load(f_holder.childViews[i], (String)f_holder.childViews[i].getTag(), true);
////						}
////					}
//				}
//			}, 1000);
//		}
		return item;
	}

	public static void onPodCastTextClick(Activity activity,final PersonData data, String podCastId) {
		PodCastDataLoader.getPodCastInfo(podCastId, new OpBack() {
			@Override
			public void onBack(boolean suc, JSONObject json, Activity activity) {
				// TODO Auto-generated method stub
				if(suc){
					Bundle newBundle = new Bundle();
					newBundle.putString(Const.PRI_JSON_DATA, json.toString());
					newBundle.putString(Const.STA_NAME, data.userName);
					newBundle.putStringArray(Const.STA_TAG, data.tagArray);
					newBundle.putString(Const.STA_FISH_YEAR, data.fYears);
					newBundle.putString(Const.STA_FISH_TIMES, data.fTimes);
					newBundle.putString(Const.STA_IMGURL, data.photoUrl);
					newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_detail_podcast);
					Intent i = new Intent();
					i.putExtras(newBundle);
					UIMgr.showActivity(activity, i, BaseUI.class.getName());
				}else{
					
				}
			}
		}, activity);
	}
	
	/**
	 * 创建钓场详情页
	 * @param activity
	 * @param view
	 * @param resultData
	 */
	public static void onCreatePodCastDetail(Activity activity,Bundle data) {
		try{
			JSONObject resultData = new JSONObject(data.getString(Const.PRI_JSON_DATA));
			String userName = data.getString(Const.STA_NAME);
			String[] tagArray = data.getStringArray(Const.STA_TAG);
			String fYears = data.getString(Const.STA_FISH_YEAR);
			String fTimes = data.getString(Const.STA_FISH_TIMES);
			String photoUrl = data.getString(Const.STA_IMGURL);
			
			if(resultData != null && Const.DEFT_1.equals(resultData.optString(Const.STA_CODE))){
				
				if(!TextUtils.isEmpty(photoUrl)){
					String url = photoUrl;
					ImageView userIcon = (ImageView)activity.findViewById(R.id.user_icon);
//					url = "http://img2.baobao88.com/bbfile/allimg/101021/10930462436-14.gif";
					ViewHelper.load(userIcon,  UrlUtils.self().getNetUrl(url),true,false);
				}
				((TextView)activity.findViewById(R.id.listitem_friend_name)).setText(userName);
				((TextView)activity.findViewById(R.id.listitem_friend_fyear)).setText(fYears);
				((TextView)activity.findViewById(R.id.listitem_friend_ftimes)).setText(fTimes);
				ViewGroup listitem_friend_tags = ((ViewGroup)activity.findViewById(R.id.listitem_friend_tags));
				
				if(listitem_friend_tags != null){
					listitem_friend_tags.setVisibility(View.VISIBLE);
					String[] tags = tagArray;
					for(int i = 0; i < listitem_friend_tags.getChildCount(); i++){//只对前三个进行显示
						TextView tv = (TextView)listitem_friend_tags.getChildAt(i);
						if(i < tags.length){
							tv.setVisibility(View.VISIBLE);
							tv.setBackgroundColor(BaseUtils.getTagBg(tags[i]));
							tv.setText(tags[i]);
						}else{
							tv.setVisibility(View.GONE);
						}
					}
				}
				
				JSONObject json = resultData.optJSONObject(Const.STA_DATA);
				final int podcast_id = json.optInt(Const.STA_ID);
				((TextView) activity.findViewById(R.id.textView)).setText(json.optString(Const.STA_TITLE));

				LinearLayout imgs = (LinearLayout)activity.findViewById(R.id.imgs);
				
				JSONArray imgUrls = json.optJSONArray(Const.STA_IMGURL);
				if(imgUrls != null){
					LayoutInflater inflater = LayoutInflater.from(activity);
					for(int i = 0; i < imgUrls.length(); i++){
						String url = imgUrls.optString(i);
						ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.fishing_news_image, null);
						imgs.addView(vg);
						ImageView iv = (ImageView)vg.getChildAt(0);
						ViewHelper.load(iv, url, true,false);
					}
				}
//				View bannerParent = activity.findViewById(R.id.search_item_detail_banner_parent);
//				makeDetailBanner(activity,json, bannerParent);

				//关注
				int careCount = json.optInt(Const.STA_CARE_COUNT);//总关注数
				View membersViewGroup = activity.findViewById(R.id.care_container);
				if(json.has(Const.STA_MEMBERS)){
					JSONArray members = json.optJSONArray(Const.STA_MEMBERS);
					makeCareView(activity,membersViewGroup,podcast_id, members);
				}else{
					membersViewGroup.setVisibility(View.GONE);
				}
				//评论
				int commentSize = json.optInt(Const.STA_COMCOUNT);//总评论数
				ViewGroup commentListView = (ViewGroup) activity.findViewById(R.id.last_comment);
				if(json.has(Const.STA_COMMENTS)){
					JSONArray comments = json.optJSONArray(Const.STA_COMMENTS);
					makeCommentView(comments, commentListView);
				}else{
					commentListView.setVisibility(View.GONE);
				}
				//构造详情底部栏
				int zanCount = json.optInt(Const.STA_ZANCOUNT);
				{
					makeBottomView(activity, json, podcast_id, careCount, commentSize, zanCount);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	//构造渔场详情页，底部栏
		private static void  makeBottomView(final Activity activity, JSONObject json,
				final int fieldId, int careCount, int commentSize, int zanCount) {
			OnClickListener clickListener = new OnClickListener() {
				@Override
				public void onClick(final View v) {
					int id = v.getId();
					if(id == R.id.detail_bottom_bar_care_icon){
						ImageView stateView = ((ImageView)((ViewGroup)v).getChildAt(0));
						TextView textView = ((TextView) activity.findViewById(R.id.detail_bottom_bar_care_number));
						onCarePodCastClick(stateView, textView, String.valueOf(fieldId));
					}else if(id == R.id.detail_bottom_bar_comment_icon){
						v.setTag(fieldId);
//						int oldCount = Integer.parseInt(((TextView) activity.findViewById(R.id.detail_bottom_bar_comment_number)).getText().toString());
//						((TextView) activity.findViewById(R.id.detail_bottom_bar_comment_number)).setText(String.valueOf(oldCount + 1));
						((BaseUI)activity).onCommentReplyClick(v);
					}else if(id == R.id.detail_bottom_bar_zan_icon){//点赞
						ImageView stateView = (ImageView)((ViewGroup)v).getChildAt(0);
						TextView textView = ((TextView) activity.findViewById(R.id.detail_bottom_bar_zan_number));
						onPraisePodCastClick(stateView, textView, String.valueOf(fieldId));
					}
				}
			};
			
			boolean isAttention = json.optBoolean(Const.STA_IS_ATTENTION) ;
			if(isAttention){
				((ViewGroup)activity.findViewById(R.id.detail_bottom_bar_care_icon)).getChildAt(0).setSelected(true);
			}
			boolean isZan = json.optBoolean(Const.STA_IS_ZAN) ;
			if(isZan){
				((ViewGroup)( activity.findViewById(R.id.detail_bottom_bar_zan_icon))).getChildAt(0).setSelected(true);
			}
			( activity.findViewById(R.id.detail_bottom_bar_care_icon)).setOnClickListener(clickListener);
			( activity.findViewById(R.id.detail_bottom_bar_comment_icon)).setOnClickListener(clickListener);
			( activity.findViewById(R.id.detail_bottom_bar_zan_icon)).setOnClickListener(clickListener);

			((TextView) activity.findViewById(R.id.detail_bottom_bar_care_number)).setText(String.valueOf(careCount));
			((TextView)activity.findViewById(R.id.detail_bottom_bar_comment_number)).setText(String.valueOf(commentSize));
			((TextView)activity.findViewById(R.id.detail_bottom_bar_zan_number)).setText(String.valueOf(zanCount));
		}

		private static void  makeCareView(final Activity activity, View view,int fieldId, JSONArray members) {//构造关注栏
			int careCountThumb = members.length();//详情页待会关注数，永远小总关注数
			OnClickListener careIcon = new OnClickListener(){
				@Override
				public void onClick(View v) {
					((BaseUI)activity).onIconClick(v);
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
						JSONObject member = members.optJSONObject(0);
						if(!TextUtils.isEmpty(member.optString(Const.STA_IMGURL))){
//							String imgUrl = "http://images.banma.com/v0/app-feed/soft/icons/_bigIcon76e029e26dce486c9c969c23b99bf31f.png";
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
//							String imgUrl = "http://images.banma.com/v0/app-feed/soft/icons/_bigIcon76e029e26dce486c9c969c23b99bf31f.png";
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
		
		private static void makeDetailBanner(Activity activity,JSONObject json,
				View bannerParent) throws JSONException {
			if (json.has(Const.STA_IMGS)) {//根据img是准备banner
				final ViewPager banner = (ViewPager) activity.findViewById(R.id.search_item_detail_banner);
				JSONArray urlArr = json.getJSONArray(Const.STA_IMGS);
				int len = urlArr.length();
				if (len > 0) {
					bannerParent.setVisibility(View.VISIBLE);
					LayoutInflater inflator = LayoutInflater.from(activity);
//							ImageLoader iLoader = ImageLoader.self();
//							{
//								int viewPagerHeight = getResources().getDisplayMetrics().widthPixels * 9 /16;//保持16:9的比例
//								bannerParent.getLayoutParams().height = viewactivityrHeight;
//							}
					ArrayList<ImageView> pviews = new ArrayList<ImageView>(len);
					final ViewGroup focusItems = (ViewGroup)activity.findViewById(R.id.search_item_detail_banner_focus_items);
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
}
