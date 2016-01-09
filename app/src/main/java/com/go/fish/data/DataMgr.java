package com.go.fish.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.mapapi.utils.DistanceUtil;
import com.go.fish.util.Const;

public class DataMgr {

	
	public static ArrayList<FieldData> makeFPlaceDatas(int layoutId, JSONArray jsonArr){
		
		ArrayList<FieldData> arr = new ArrayList<FieldData>();
		try {
			for(int i = 0;i < jsonArr.length(); i++){
				JSONObject json = jsonArr.getJSONObject(i);
				FieldData ld = FieldData.newInstance(json);
				ld.layout_id = layoutId;
				arr.add(ld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}
}
