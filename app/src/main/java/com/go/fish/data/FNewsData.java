package com.go.fish.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class FNewsData implements IBaseData{

	public String content;
	public long publishTime = 0;
	public int goodCount,careCount,commentCount,shareCount;
	public String[] netPicUrl = null;
	public String[] localPicUrl = null;
	
	private FNewsData(){};
	public static FNewsData newInstance(String str){
		return new FNewsData();
	}
	public static FNewsData newInstance(JSONObject jsonObject){
		FNewsData newsData = new FNewsData();
		JSONArray urlArr = jsonObject.optJSONArray(Const.STA_URLS);
		newsData.netPicUrl = new String[urlArr.length()];
		for(int j = 0; j < urlArr.length(); j++) {
			newsData.netPicUrl[j] = urlArr.optString(j);
		}
		newsData.content =jsonObject.optString(Const.STA_TEXT);
		newsData.goodCount =jsonObject.optInt(Const.STA_GOOD_COUNT);
		newsData.commentCount =jsonObject.optInt(Const.STA_COMMENT_COUNT);
		newsData.careCount =jsonObject.optInt(Const.STA_CARE_COUNT);
		return newsData;
	}
}
