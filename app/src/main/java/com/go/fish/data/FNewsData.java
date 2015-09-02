package com.go.fish.data;

import com.go.fish.view.IBaseData;

public class FNewsData implements IBaseData{

	long publishTime = 0;
	int goodCount,careCount,commentCount,shareCount;
	String[] netPicUrl = null;
	String[] localPicUrl = null;
	
	private FNewsData(){};
	public static FNewsData newInstance(String str){
		return new FNewsData();
	}
}
