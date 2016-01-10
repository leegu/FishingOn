package com.go.fish.op;

import org.json.JSONObject;

import android.app.Activity;

public interface OpBack {
	void onBack(boolean suc,JSONObject json,Activity activity);
}
