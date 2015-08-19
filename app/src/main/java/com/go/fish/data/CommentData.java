package com.go.fish.data;

import java.util.ArrayList;

public class CommentData {

	long commentTime = 0;
	ArrayList<Integer> personIds ;
	ArrayList<String> personNames ;
	
	private CommentData(){};
	public static CommentData newInstance(String str){
		return new CommentData();
	}
}
