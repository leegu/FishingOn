package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.go.fish.data.CommentData;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.IBaseData;

public class CommentDataLoader {

	public static void loadList(final Context context, final AdapterExt adapter, String fieldId,final boolean pullRefresh) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_OBJECTID, fieldId);
			jsonObject.put(Const.STA_START_INDEX, 0);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
			jsonObject.put(Const.STA_TYPE, Const.DEFT_COMMENTLIST_TYPE_FIELD);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				super.onStart(context);
			}
			
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if (isRight(json)) {
					JSONArray dataJson = json.optJSONArray(Const.STA_DATA);
					adapter.updateAdapter(dataJson,pullRefresh);
				}
				onEnd();
			}
		}, jsonObject, UrlUtils.self().getCommentList());
	}

	public static ArrayList<IBaseData> makeCommentDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			CommentData newsData = CommentData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
	}
}
