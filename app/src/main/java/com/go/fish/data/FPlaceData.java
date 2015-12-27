package com.go.fish.data;

import org.json.JSONObject;

import android.os.Bundle;

import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;
import com.go.fish.view.IBaseData;

public class FPlaceData implements IBaseData{
	public int layout_id ;
	public String title = null,desp = null,phoneNum = null,sid = null,distance = null;
	public double lng,alt;
	public boolean isAttentionByUser = false;
	private JSONObject mData = null;
	FPlaceData(){}
	public static FPlaceData newInstance(JSONObject json){
		FPlaceData ret = new FPlaceData();
		ret.mData = json;
		ret.sid = json.optString(Const.STA_ID);
		ret.isAttentionByUser = json.optBoolean(Const.STA_IS_ATTENTION);
		ret.alt = json.optDouble(Const.STA_LAT);
		ret.lng = json.optDouble(Const.STA_LNG);
		ret.distance = MapUtil.getDistance(ret.alt, ret.lng);
		ret.title = json.optString(Const.STA_NAME);
		ret.desp = json.optString(Const.STA_INTRODUCTION);
//		ret.desp = json.optString(Const.STA_NAME);
		ret.sid = json.optString(Const.STA_ID);
		return ret;
	}
	public String toJSONString(){
		return mData.toString();
	}
	
}