package com.go.fish.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.go.fish.R;
import com.go.fish.ui.BaseUI;

public class MainView extends Fragment {
    private static final String TAG = "MainView";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = 0;
        int id = getId();
        switch (id) {
		case R.id.home_fishing_place:
			layoutId = R.layout.fishing_place;
			break;
		case R.id.home_care:
			layoutId = R.layout.care;
			break;
		case R.id.home_appear:
			layoutId = R.layout.appear;
			break;
		case R.id.home_fishing_news:
			layoutId = R.layout.fishing_news;
			break;
		case R.id.home_my:
			layoutId = R.layout.my;
			break;
		default:
			break;
		}
        View view = inflater.inflate(layoutId,null);
        return view;
    }
    
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	Log.d(TAG, "TestFragment-----onPause");
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Log.d(TAG, "TestFragment-----onResume");
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
    	super.onHiddenChanged(hidden);
    	Log.d(TAG, "TestFragment-----" + hidden);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TestFragment-----onDestroy");
    }
 
}
