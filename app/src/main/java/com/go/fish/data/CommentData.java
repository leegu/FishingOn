package com.go.fish.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class CommentData implements IBaseData{

	public String commentTime;
	public String imgUrl;
	public String uid ;
	public String uname ;
	public String text ;
	public JSONArray lowerComments;

	private CommentData(){};
	public static CommentData newInstance(JSONObject json){
		CommentData cd = new CommentData();
		try {
			cd.uid = json.getString(Const.STA_ID);
			cd.imgUrl = json.getString(Const.STA_IMGURL);
			cd.uname = json.getString(Const.STA_NAME);
			cd.text = json.getString(Const.STA_COMMENT_STR);
			cd.commentTime = json.getString(Const.STA_CREATED_AT_TIME);
			cd.lowerComments = json.optJSONArray(Const.STA_LOWER_COMMENTS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cd;
	}
}
