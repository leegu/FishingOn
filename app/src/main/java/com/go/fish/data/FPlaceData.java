package com.go.fish.data;

import android.os.Bundle;

import com.go.fish.util.Const;

public class FPlaceData{
	public int layout_id ;
	public int orderId;
	public String text = null,desp = null,phoneNum = null,sid = null;
	public long dataId,lng,alt;
	FPlaceData(){}
	public Bundle toBundle(Bundle bundle){
		bundle.putString(Const.TEXT, text);
		bundle.putInt(Const.ORDER_ID, orderId);
		bundle.putString(Const.FISHING_PLACE_ID, sid);
		return bundle;
	}
}