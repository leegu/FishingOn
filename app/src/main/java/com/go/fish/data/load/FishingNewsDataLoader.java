package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.go.fish.data.FishingNewsData;
import com.go.fish.view.IBaseData;

public class FishingNewsDataLoader {

	public static ArrayList<IBaseData> makeFishingNewsDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			FishingNewsData newsData = FishingNewsData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
	}
}
