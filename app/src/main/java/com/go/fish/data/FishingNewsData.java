package com.go.fish.data;

public class FishingNewsData {

	long publishTime = 0;
	int goodCount,careCount,commentCount,shareCount;
	String[] netPicUrl = null;
	String[] localPicUrl = null;
	
	private FishingNewsData(){};
	public static FishingNewsData newInstance(String str){
		return new FishingNewsData();
	}
}
