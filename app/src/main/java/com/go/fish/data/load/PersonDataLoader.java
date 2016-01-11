package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.go.fish.data.PersonData;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.IBaseData;

public class PersonDataLoader {

	public static void getAroundMember(final Activity activity,
			final AdapterExt adapter) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_LNG, User.self().userInfo.lng);
			jsonObject.put(Const.STA_LAT, User.self().userInfo.lat);
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart(activity);
			}

			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if (isRight(json)) {
					JSONArray dataJson = json.optJSONArray(Const.STA_DATA);
					adapter.updateAdapter(dataJson);
				}
				onEnd();
			}
		}, jsonObject, UrlUtils.self().getAroundMember());
	}
	
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
