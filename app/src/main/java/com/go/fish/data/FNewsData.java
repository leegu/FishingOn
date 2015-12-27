package com.go.fish.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class FNewsData implements IBaseData{

	public PersonData authorData = null;
	public int newsId = 0;
	public String content;
	public String publishTime;
	public int goodCount,careCount,commentCount,shareCount;
	public String[] netPicUrl = null;
//	public String[] localPicUrl = null;
	
	private FNewsData(){};
	public static FNewsData newInstance(String str){
		return new FNewsData();
	}
	public static FNewsData newInstance(JSONObject jsonObject){
		FNewsData newsData = new FNewsData();
		JSONArray urlArr = jsonObject.optJSONArray(Const.STA_IMGS);
		if(urlArr != null && urlArr.length() > 0){
			newsData.netPicUrl = new String[urlArr.length()];
			for(int j = 0; j < urlArr.length(); j++) {
				newsData.netPicUrl[j] = urlArr.optString(j);
			}
		}
		newsData.content =jsonObject.optString(Const.STA_CONTENT);
		newsData.publishTime =jsonObject.optString(Const.STA_CREATED_AT_TIME);
		newsData.goodCount =jsonObject.optInt(Const.STA_GOOD_COUNT);
		newsData.commentCount =jsonObject.optInt(Const.STA_COMMENT_COUNT);
		newsData.careCount =jsonObject.optInt(Const.STA_CARE_COUNT);
		newsData.newsId =jsonObject.optInt(Const.STA_NEWS_ID);
		newsData.authorData = PersonData.newInstance(jsonObject.optJSONObject(Const.STA_MEMBER));
		return newsData;
	}
}
