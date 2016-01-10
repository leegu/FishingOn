package com.go.fish.op;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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

import com.go.fish.R;
import com.go.fish.data.PodCastData;
import com.go.fish.data.load.PodCastDataLoader;
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
import com.go.fish.view.ViewHelper;

public class PodCastUIOp {

	public static void onCommentPodCastClick(Activity activity,final View stateView){
		ViewHelper.showToast(activity, Const.DEFT_NO_SUPPORT_COMMENT_PODCAST);
	}
	
	public static void onCarePodCastClick(final ImageView stateView){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_NEWS_ID, (String)stateView.getTag());
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
				} else {
				}
			}
		},jsonObject, UrlUtils.self().getAttentionPod());
	}
	
	public static void onPraisePodCastClick(final ImageView stateView){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_INFO_ID, (String)stateView.getTag());
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
		View listitem_friend_layout,user_detail;
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
			holder.listitem_friend_layout = fLayout;
			holder.nameView = (TextView)fLayout.findViewById(R.id.listitem_friend_name);
			holder.fYearView = (TextView)fLayout.findViewById(R.id.listitem_friend_fyear);
			holder.fTimesView = (TextView)fLayout.findViewById(R.id.listitem_friend_ftimes);
			holder.listitem_friend_tags = (ViewGroup)fLayout.findViewById(R.id.listitem_friend_tags);
			
			holder.textView = (TextView)item.findViewById(R.id.textView);
			holder.listitem_fnews_comment_count = (TextView)item.findViewById(R.id.listitem_fnews_comment_count);
			holder.publish_time = (TextView)item.findViewById(R.id.publish_time);
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
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
						intent.putExtra(Const.PRI_EXTRA_IMAGE_INDEX, position);
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
//					ViewHelper.load((ImageView)((ViewGroup)holder.childViews[i]).getChildAt(0), (String)holder.childViews[i].getTag(), true,false);
				}
				holder.mHAutoAlign.fillChilds(holder.childViews);
			}
		} else {
			holder = (PodCastViewHolder)convertView.getTag();
			item = convertView;
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
				String[] tags = newsData.authorData.aiHaos;
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
			holder.nameView.setText(newsData.authorData.userName + "--" + position);
			holder.fYearView.setText(""+newsData.authorData.fYears);
			holder.fTimesView.setText(""+newsData.authorData.fTimes);
		}
		if(holder.mHAutoAlign != null) holder.mHAutoAlign.scrollTo(0, 0);
		holder.textView.setText(newsData.content);
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
}
