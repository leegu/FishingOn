package com.go.fish.data;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentData {

	public long commentTime = 0;
	public Bitmap bitmap;
	public String uid ;
	public String uname ;
	public String text ;

	private CommentData(){};
	public static CommentData newInstance(JSONObject json){
		CommentData cd = new CommentData();
		try {
			cd.uid = json.getString("uid");
			cd.uname = json.getString("uname");
			cd.text = json.getString("text");
			cd.commentTime = json.getInt("time");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cd;
	}
}
