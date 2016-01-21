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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FishingNewsData;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;

public class FishingNewsUIOp extends Op{

	public static void onCreateFishingNewsList(final Activity activity,ViewGroup view){
		{//最新播况 撒鱼信息
			final ListView lastNews = (ListView)view.findViewById(R.id.last_news);
			//本地先获取显示
			String careFPlace = LocalMgr.self().getString(Const.SIN_DB_MY_CARE_FNEWS);
			ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
			JSONArray jsonArr = null;
			try {
				if(!TextUtils.isEmpty(careFPlace)){
					jsonArr = new JSONArray(careFPlace);
				}else{
					jsonArr = new JSONArray();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			final AdapterExt mListAdapter = AdapterExt.newInstance(lastNews, getDefault(activity), jsonArr, R.layout.listitem_fishing_news_3_row);
			lastNews.setAdapter(mListAdapter);
		}
		{//最新活动
//			final ListView lastActivity = (ListView)view.findViewById(R.id.last_activity);
//			lastActivity.setVisibility(View.GONE);
//			//本地先获取显示
//			String careFPlace = LocalMgr.self().getString(Const.SIN_DB_MY_CARE_FPLACE);
//			JSONArray jsonArr = null;
//			try {
//				if(!TextUtils.isEmpty(careFPlace)){
//					jsonArr = new JSONArray(careFPlace);
//				}else{
//					jsonArr = new JSONArray();
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			final AdapterExt mListAdapter = AdapterExt.newInstance(lastActivity, jsonArr, R.layout.listitem_zixun);
//			
//			lastActivity.setAdapter(mListAdapter);
		}
    }
	
	public static void onShowFishingNewsList(final ViewGroup view){
		final ListView lastNews = (ListView)view.findViewById(R.id.last_news);
		final AdapterExt mListAdapter = (AdapterExt)lastNews.getAdapter(); 
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if(isRight(view.getContext(),json,true)){
					JSONArray arr = json.optJSONArray(Const.STA_DATA);
					if(arr != null && arr.length() > 0) {
						mListAdapter.updateAdapter(arr);
					}
				}
			}
		}, jsonObject, UrlUtils.self().getPriceList());
	}
	
	public static void onCreateFishingNewsDetail(final Activity activity) {
		Intent intent = activity.getIntent();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_PRICE_ID, intent.getIntExtra(Const.STA_PRICE_ID,0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart(activity);
			}

			@Override
			public void onEnd(byte[] data) {
				JSONObject json = toJSONObject(data);
				if (isRight(activity,json,true)) {
					JSONObject jsonData = json.optJSONObject(Const.STA_DATA);
					String titleText = jsonData.optString(Const.STA_TITLE);
					TextView title = (TextView)findViewById(activity,R.id.title);
					title.setText(titleText);
					
					String contentText = jsonData.optString(Const.STA_CONTENT);
					TextView content = (TextView)findViewById(activity,R.id.content);
					content.setText(contentText);
					
					LinearLayout imgs = (LinearLayout)findViewById(activity, R.id.imgs);
					
					JSONArray imgUrls = jsonData.optJSONArray(Const.STA_IMGURL);
					LayoutInflater inflater = LayoutInflater.from(activity);
					for(int i = 0; i < imgUrls.length(); i++){
						String url = imgUrls.optString(i);
						ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.fishing_news_image, null);
						imgs.addView(vg,new LayoutParams(-1, -2));
						ImageView iv = (ImageView)vg.getChildAt(0);
						ViewHelper.load(iv, url, true,false);
					}
					onEnd();
				}
			}
		}, jsonObject, UrlUtils.self().getPriceInfo());
	}
	
	static class ZixunViewHolder{
		TextView listitem_zixun_title,listitem_zixun_field_name,listitem_zixun_desc,/*listitem_zixun_zan,*/listitem_zixun_time/*,listitem_zixun_comment*/;
		ImageView listitem_fplace_icon;
	}
	public static View onGetFishingNewsView(final Activity activity,LayoutInflater mInflater,int layout_id,final int position,ArrayList<IBaseData> listDatas, View convertView, ViewGroup parent){
		ViewGroup item = null;
		IBaseData data = listDatas.get(position);
		FishingNewsData fPlaceZixunData = (FishingNewsData)data;
		ZixunViewHolder holder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new ZixunViewHolder();
			item.setTag(holder);
			holder.listitem_zixun_title = ((TextView)item.findViewById(R.id.listitem_zixun_title));
			holder.listitem_zixun_desc = ((TextView)item.findViewById(R.id.listitem_zixun_desc));
			holder.listitem_zixun_field_name = ((TextView)item.findViewById(R.id.listitem_zixun_field_name));
//			holder.listitem_zixun_zan = ((TextView)item.findViewById(R.id.listitem_zixun_zan));
			holder.listitem_zixun_time = (TextView)item.findViewById(R.id.listitem_zixun_time);
//			holder.listitem_zixun_comment = (TextView)item.findViewById(R.id.listitem_zixun_comment);
			holder.listitem_fplace_icon = (ImageView)item.findViewById(R.id.listitem_fplace_icon);
		} else {
			item = (ViewGroup)convertView;
			holder = (ZixunViewHolder)convertView.getTag();
		}
		if(!TextUtils.isEmpty(fPlaceZixunData.imgUrl)){
			ViewHelper.load(holder.listitem_fplace_icon, fPlaceZixunData.imgUrl, true, false);
		}
		holder.listitem_zixun_title.setText(fPlaceZixunData.title);
		holder.listitem_zixun_desc.setText(fPlaceZixunData.content);
		holder.listitem_zixun_field_name.setText(fPlaceZixunData.fieldName);
		holder.listitem_zixun_time.setText(BaseUtils.getTimeShow(fPlaceZixunData.createdAt));//设置日期
//		holder.listitem_zixun_comment.setText(fPlaceZixunData.comNum);//设置评论内容
//		holder.listitem_zixun_zan.setText(fPlaceZixunData.attNum);//设置评论内容
		return item;
	}
	
	
}
