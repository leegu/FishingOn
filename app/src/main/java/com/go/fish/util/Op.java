package com.go.fish.util;

import java.util.HashMap;

public class Op {

	private static Op instance = null;
	private Op(){};
	public static Op self(){
		if(instance == null){
			instance = new Op();
		}
		return instance;
	}
	
}
