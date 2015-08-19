package com.go.fish.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataMgr {

	
	public static ArrayList<FishingPlaceData> makeFishPlaceDatas(byte[] bytes){
		
		ArrayList<FishingPlaceData> arr = new ArrayList<FishingPlaceData>();
		try {
			String strJson = "[{title:'海淀区驾驭钓鱼休闲区'},{title:'海淀区驾驭钓鱼休闲区海淀区驾驭钓鱼休海淀区驾驭钓鱼休'}]";
//			String strJson = new String(bytes,"UTF-8");
			JSONArray jsonArr = new JSONArray(strJson);
			for(int i = 0;i < jsonArr.length(); i++){
				JSONObject json = jsonArr.getJSONObject(i);
				FishingPlaceData ld = new FishingPlaceData();
				ld.orderId = i + 1;
				ld.text = json.getString("title");
				arr.add(ld);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arr;
	}
}
