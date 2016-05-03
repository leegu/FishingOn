package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.go.fish.data.FishingNewsData;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.AdapterExt;
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
	
	public static void loadList(final Context context, final AdapterExt adapter,final boolean pullRefresh){
		JSONObject jsonObject = new JSONObject();
		int startIndex = adapter != null ? adapter.getCount() : 0;
		startIndex = pullRefresh ? 0 : startIndex;
		try {
			jsonObject.put(Const.STA_START_INDEX, startIndex);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				JSONObject json = toJSONObject(data);
				if(isRight(context,json,true)){
					JSONArray arr = json.optJSONArray(Const.STA_DATA);
					if(arr != null && arr.length() > 0) {
						adapter.updateAdapter(arr,pullRefresh);
					}
				}
			}
		}, jsonObject, UrlUtils.self().getPriceList());
	}
}
