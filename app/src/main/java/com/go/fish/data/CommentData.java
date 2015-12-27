package com.go.fish.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class CommentData implements IBaseData{

	private CommentData rootCommentData;
	public String commentTime;
	public String imgUrl;
	public String id ;//评论的id
	public String memberId ;//理论上等同于fromId
	public String memberName ;//理论上等同于fromName
	
	public String fromId ;
	public String fromName ;
	public String toId ;
	public String toName ;
	public String commentStr ;
	public ArrayList<CommentData> lowerComments;
	public JSONObject jsonData;
	public CommentData(){};
	
	public CommentData getRootCommentData(){
		return rootCommentData;
	}
	public void setRootCommentData(CommentData rootComment){
		rootCommentData = rootComment;
	}
	public static CommentData newInstance(JSONObject json){
		CommentData cd = new CommentData();
//		try {
			cd.jsonData = json;
			cd.id = json.optString(Const.STA_ID);
			cd.memberId = json.optString(Const.STA_MEMBER_ID);
			cd.fromId = json.optString(Const.STA_FROM_ID);
			if(TextUtils.isEmpty(cd.memberId) && !TextUtils.isEmpty(cd.fromId)){
				cd.memberId = cd.fromId ;
			}else{
				cd.fromId = cd.memberId;
			}
			cd.memberName = json.optString(Const.STA_NAME);
			cd.fromName = json.optString(Const.STA_FROM_NAME);
			if(TextUtils.isEmpty(cd.memberName) && !TextUtils.isEmpty(cd.fromName)){
				cd.memberName = cd.fromName ;
			}else{
				cd.fromName = cd.memberName;
			}
			
			cd.toId = json.optString(Const.STA_TO_ID);
			cd.toName = json.optString(Const.STA_TO_NAME);
			
			cd.imgUrl = json.optString(Const.STA_IMGURL);
			cd.commentStr = json.optString(Const.STA_COMMENT_STR);
			cd.commentTime = json.optString(Const.STA_CREATED_AT_TIME);
			JSONArray arr = json.optJSONArray(Const.STA_LOWER_COMMENTS);
			if(arr != null && arr.length() > 0){
				cd.lowerComments = new ArrayList<CommentData>();
				for(int i = 0; i < arr.length(); i++){
					CommentData cData = CommentData.newInstance(arr.optJSONObject(i));
					cData.rootCommentData = cd;
					cd.lowerComments.add(cData);
				}
			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		return cd;
	}
}
