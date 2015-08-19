package com.go.fish.data;

import com.go.fish.util.Const;

import android.os.Bundle;

public class FishingPlaceData{
	public int orderId;
	public String text = null,desp = null,phoneNum = null,sid = null;
	public long dataId,lng,alt;
	FishingPlaceData(){}
	public Bundle toBundle(Bundle bundle){
		bundle.putString(Const.TEXT, text);
		bundle.putInt(Const.ORDER_ID, orderId);
		bundle.putString(Const.FISHING_PLACE_ID, sid);
		return bundle;
	}
}