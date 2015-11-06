package com.go.fish.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.go.fish.util.Const;

public class DataMgr {

	
	public static ArrayList<FPlaceData> makeFPlaceDatas(int layoutId, JSONArray jsonArr){
		
		ArrayList<FPlaceData> arr = new ArrayList<FPlaceData>();
		try {
			for(int i = 0;i < jsonArr.length(); i++){
				JSONObject json = jsonArr.getJSONObject(i);
				FPlaceData ld = new FPlaceData();
				ld.layout_id = layoutId;
				ld.orderId = i + 1;
				ld.title = json.getString(Const.STA_TITLE);
				ld.sid = json.getString(Const.STA_ID);
				arr.add(ld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}
}
