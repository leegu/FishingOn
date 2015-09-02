package com.go.fish.util;

import java.io.File;

import android.content.Context;

public class LocalMgr {

	private static LocalMgr instance = null;
	private Context context = null;
	private LocalMgr(Context context){
		this.context = context;
	}
	
	public static LocalMgr initEnv(Context context){
		if(instance == null) instance = new LocalMgr(context);
		return instance;
	}
	
	public String save(String url,byte[] data){
		String file = ""+System.nanoTime();
//		File f = new File(path);
		return file;
	}

	public static String loadData(String  colName) {
		return "";
	}
}
