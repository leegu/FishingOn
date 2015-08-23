package com.go.fish.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MessageHandler {

	static Handler myHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			MessageListener ml = (MessageListener)msg.obj;
			ml.onExecute();
		}
	};
	
	public static void sendMessage(MessageListener listener){
		Message m = Message.obtain();
		m.obj = listener;
		m.what = 0;
		myHandler.sendMessage(m);
	}
	public static void sendMessage(MessageListener listener,int delayTime){
		Message m = Message.obtain();
		m.obj = listener;
		m.what = 0;
		myHandler.sendMessageDelayed(m,delayTime);
	}
	
	public static interface MessageListener<T>{
		MessageListener init(T args);
		void onExecute();
	}
}
