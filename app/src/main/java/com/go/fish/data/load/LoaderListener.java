package com.go.fish.data.load;

import org.json.JSONObject;

import com.go.fish.util.NetTool.RequestListener;

public interface LoaderListener {

	void onCompleted(RequestListener requestListener, JSONObject jsonData);
}
