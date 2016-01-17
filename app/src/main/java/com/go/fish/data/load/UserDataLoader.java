package com.go.fish.data.load;

import org.json.JSONException;
import org.json.JSONObject;

import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LogUtils;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;

public class UserDataLoader {

	public static void updateUserLocation() {
		LogUtils.d("UserDataLoader", "updateUserLocation lat=" + User.self().userInfo.lat +";lng=" + User.self().userInfo.lng);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_LAT,
					String.valueOf(User.self().userInfo.lat));
			jsonObject.put(Const.STA_LNG,
					String.valueOf(User.self().userInfo.lng));
			jsonObject.put(Const.STA_LOCATION, User.self().userInfo.address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(null, jsonObject, UrlUtils.self().getSetLocation());
	}
}
