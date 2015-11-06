package com.go.fish.data;

import org.json.JSONObject;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class FPlaceZixunData implements IBaseData{
	public int layout_id ;
	public int orderId;
	public String title = null,content = null,attNum = null,comNum = null,imgUrl = null,createdAt = null;
	int id = 0;

	public long dataId,lng,alt;
	FPlaceZixunData(){}

	public static FPlaceZixunData newInstance(JSONObject jsonObject){
		FPlaceZixunData o = new FPlaceZixunData();
		o.id = jsonObject.optInt(Const.STA_ID);
		o.imgUrl = jsonObject.optString(Const.STA_IMGURL);
		o.title = jsonObject.optString(Const.STA_TITLE);
		o.content = jsonObject.optString(Const.STA_CONTENT);
		o.imgUrl = jsonObject.optString(Const.STA_IMGURL);
		o.createdAt = jsonObject.optString(Const.STA_CREATED_AT_TIME);
		o.comNum = jsonObject.optString(Const.STA_COMNUM);
		o.attNum = jsonObject.optString(Const.STA_ATTNUM);
		return o;
	}
}