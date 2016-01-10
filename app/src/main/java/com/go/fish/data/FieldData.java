package com.go.fish.data;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;
import com.go.fish.view.IBaseData;

public class FieldData implements IBaseData{
	public int layout_id ;
	public String title = null,desp = null,phoneNum = null,sid = null,distance = null,imgUrl = null,tag = null;
	public double lng,alt;
	public boolean isAttentionByUser = false;
	private JSONObject mData = null;
	public String[] tagArray;
	FieldData(){}
	public static FieldData newInstance(JSONObject json){
		FieldData ret = new FieldData();
		ret.mData = json;
		ret.sid = json.optString(Const.STA_ID);
		ret.isAttentionByUser = json.optBoolean(Const.STA_IS_ATTENTION);
		ret.alt = json.optDouble(Const.STA_LAT);
		ret.lng = json.optDouble(Const.STA_LNG);
		ret.distance = MapUtil.getDistance(ret.alt, ret.lng);
		ret.tag = json.optString(Const.STA_TAG);
		ret.tagArray = BaseUtils.splitString(ret.tag);
		ret.title = json.optString(Const.STA_NAME);
		ret.imgUrl = json.optString(Const.STA_IMGURL);
		ret.desp = json.optString(Const.STA_INTRODUCTION);
//		ret.desp = json.optString(Const.STA_NAME);
//		ret.sid = json.optString(Const.STA_ID);
		return ret;
	}
	public String toJSONString(){
		return mData.toString();
	}
	@Override
	public void OnClick(Activity activity, IBaseDataHandledCallback handledCallback, View attachedView) {
		// TODO Auto-generated method stub
		
	}
	
}