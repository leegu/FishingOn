package com.go.fish.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.go.fish.R;

public class DataMgr {

	
	public static ArrayList<FPlaceData> makeFPlaceDatas(int layoutId, JSONArray jsonArr){
		
		ArrayList<FPlaceData> arr = new ArrayList<FPlaceData>();
		try {
			for(int i = 0;i < jsonArr.length(); i++){
				JSONObject json = jsonArr.getJSONObject(i);
				FPlaceData ld = new FPlaceData();
				ld.layout_id = layoutId;
				ld.orderId = i + 1;
				ld.text = json.getString("title");
				ld.sid = json.getString("id");
				arr.add(ld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}
}
