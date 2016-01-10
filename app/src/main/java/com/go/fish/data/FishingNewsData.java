package com.go.fish.data;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.go.fish.R;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class FishingNewsData implements IBaseData{
	public int layout_id ;
	public int orderId;
	public String title = null,content = null,attNum = null,comNum = null,imgUrl = null,createdAt = null,fieldName = null;
	public JSONArray imgs = null;
	int id = 0;

	public long dataId,lng,alt;
	FishingNewsData(){}

	public static FishingNewsData newInstance(JSONObject jsonObject){
		FishingNewsData o = new FishingNewsData();
		o.id = jsonObject.optInt(Const.STA_ID);
		o.imgs = jsonObject.optJSONArray(Const.STA_IMGURL);
		o.title = jsonObject.optString(Const.STA_TITLE);
		o.fieldName = jsonObject.optString(Const.STA_FIELD_NAME);
		o.content = jsonObject.optString(Const.STA_CONTENT);
		o.imgUrl = jsonObject.optString(Const.STA_IMGURL);
		o.createdAt = jsonObject.optString(Const.STA_CREATED_AT_TIME);
		o.comNum = jsonObject.optString(Const.STA_COMNUM);
		o.attNum = jsonObject.optString(Const.STA_ATTNUM);
		return o;
	}

	@Override
	public void OnClick(Activity activity, IBaseDataHandledCallback handledCallback, View attachedView) {
		// TODO Auto-generated method stub
		StaticOnClick(activity, id);
	}
	public static void StaticOnClick(Activity activity,int id){
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_detail_fishing_news);
		i.putExtra(Const.STA_PRICE_ID, id);
		UIMgr.showActivity(activity,i, BaseUI.class.getName());
	}
}