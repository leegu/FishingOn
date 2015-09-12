package com.go.fish.data;

import android.os.Bundle;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class FPlaceData implements IBaseData{
	public int layout_id ;
	public int orderId;
	public String title = null,desp = null,phoneNum = null,sid = null;
	public long dataId,lng,alt;
	FPlaceData(){}
	public Bundle toBundle(Bundle bundle){
		bundle.putString(Const.STA_TEXT, title);
		bundle.putInt(Const.STA_ORDER_ID, orderId);
		bundle.putString(Const.STA_FISHING_PLACE_ID, sid);
		return bundle;
	}
}