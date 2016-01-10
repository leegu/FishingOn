package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.go.fish.data.PersonData;
import com.go.fish.view.IBaseData;

public class PersonDataLoader {

	public static ArrayList<IBaseData> makePersonDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		if(array != null){
			int count = array.length();
//			count = count > 0 ? 50 : 0;
			for(int i = 0; i < count; i++) {
				JSONObject jsonObject = array.optJSONObject(i);
//				JSONObject jsonObject = array.optJSONObject(0);
				PersonData newsData = PersonData.newInstance(jsonObject);
				arr.add(newsData);
			}
		}
		return arr;
	}

}
