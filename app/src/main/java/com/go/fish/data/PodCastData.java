package com.go.fish.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.go.fish.R;
import com.go.fish.ui.HomeUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.IBaseData;

public class PodCastData implements IBaseData{

	public PersonData authorData = null;
	public int newsId = 0;
	public String content;
	public String publishTime;
	public int goodCount,careCount,commentCount,shareCount;
	public String[] netPicUrl = null;
//	public String[] localPicUrl = null;
	
	private PodCastData(){};
	public static PodCastData newInstance(String str){
		return new PodCastData();
	}
	public static PodCastData newInstance(JSONObject jsonObject){
		PodCastData newsData = new PodCastData();
		JSONArray urlArr = jsonObject.optJSONArray(Const.STA_IMGURL);//钓播 发布的图片
		if(urlArr != null && urlArr.length() > 0){
			newsData.netPicUrl = new String[urlArr.length()];
			for(int j = 0; j < urlArr.length(); j++) {
				newsData.netPicUrl[j] = urlArr.optString(j);
			}
		}
		newsData.content =jsonObject.optString(Const.STA_CONTENT);
		newsData.publishTime =jsonObject.optString(Const.STA_CREATED_AT_TIME);
		newsData.goodCount =jsonObject.optInt(Const.STA_GOOD_COUNT);
		newsData.commentCount =jsonObject.optInt(Const.STA_COMMENT_COUNT);
		newsData.careCount =jsonObject.optInt(Const.STA_CARE_COUNT);
		newsData.newsId =jsonObject.optInt(Const.STA_NEWS_ID);
		newsData.authorData = PersonData.newInstance(jsonObject.optJSONObject(Const.STA_MEMBER));
		return newsData;
	}
	@Override
	public void OnClick(final Activity activity, IBaseDataHandledCallback handledCallback, View attachedView) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_ID, newsId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				onStart(activity);
			}
			@Override
			public void onEnd(byte[] data) {
				try {
					if (isOver()) {// 返回键取消联网之后不应该继续处理
					} else {
						if (data != null) {
							String jsonStr = new String(data, "UTF-8");
							JSONObject response = toJSONObject(jsonStr);
							if (isRight(activity,response,true)) {
								Bundle newBundle = new Bundle();
								newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
								newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_detail_podcast);
								Intent i = new Intent();
								i.putExtras(newBundle);
								UIMgr.showActivity(activity, i, SearchUI.class.getName());
							}
						} else {
							onDataError(activity);
						}
					}
					onEnd();
				} catch (Exception e) {
				}
			}
		}, jsonObject, UrlUtils.self().getInfo());
	}
}
