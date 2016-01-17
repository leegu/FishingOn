package com.go.fish.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.go.fish.R;
import com.go.fish.op.FieldUIOp;
import com.go.fish.op.FishingNewsUIOp;
import com.go.fish.op.PodCastUIOp;
import com.go.fish.op.UserUIOp;
import com.go.fish.util.Const;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainView";
    /**fragment中的根view*/
    ViewGroup contentView;
    OnBaseDataClickListener mOnBaseDataClickListener = null;
    private int layoutId = 0;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if(getActivity() instanceof OnBaseDataClickListener){
//    		mOnBaseDataClickListener = (OnBaseDataClickListener)mOnBaseDataClickListener;
//    	}
//    }
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnBaseDataClickListener){
    		mOnBaseDataClickListener = (OnBaseDataClickListener)activity;
    	}
	}
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//    	super.onActivityCreated(savedInstanceState);
//    	
//    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int id = getId();
        Bundle b = getArguments();
        if(b != null && b.getInt(Const.PRI_LAYOUT_ID) == R.layout.ui_f_fplace){
        	id = R.id.home_fishing_place;
        }
        View view = null;
        switch (id) {
		case R.id.home_fishing_place:
			layoutId = R.layout.ui_f_fplace;
			view = inflater.inflate(layoutId,container,false);
			break;
		case R.id.home_care://关注
			layoutId = R.layout.ui_f_care;
			view = inflater.inflate(layoutId,container,false);
			FieldUIOp.onCreateCareFieldList(getActivity(),(ViewGroup)view, null);
			break;
		case R.id.home_zixun:
			layoutId = R.layout.ui_f_fishing_news;
			view = inflater.inflate(layoutId,container,false);
			FishingNewsUIOp.onCreateFishingNewsList(getActivity(),(ViewGroup) view);
			break;
		case R.id.home_fishing_news://钓播
//			layoutId = R.layout.ui_f_fishing_news;
			layoutId = R.layout.ui_podcast_all;
			view = inflater.inflate(layoutId,container,false);
			PodCastUIOp.onCreatePodCastList(getActivity(),(ViewGroup) view);
			break;
		case R.id.home_my:
			layoutId = R.layout.ui_my;
			view = inflater.inflate(layoutId,container,false);
			UserUIOp.onCreateMyView(getActivity(),(ViewGroup) view);
			break;
		default:
			break;
		}
        contentView = (ViewGroup)view;
        return view;
    }


	boolean showStatus = false;
	public boolean isShowing(){
		return showStatus;
	}
	public void onShow(){
		showStatus = true;
		switch (layoutId) {
		case R.layout.ui_f_fplace:
			break;
		case R.layout.ui_f_care:{//关注页面
			FieldUIOp.onShowCareFieldList(contentView);
			break;
		}
		case R.layout.ui_f_fishing_news:{
			FishingNewsUIOp.onShowFishingNewsList(contentView);
			break;
		}
		case R.layout.ui_podcast_all:
			PodCastUIOp.onShowPodCastList(contentView);
			break;
		case R.layout.ui_my:
			UserUIOp.onShowMyView(contentView);
			break;
		default:
			break;
		}
	}
	public void onHide(){
		showStatus = false;
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
