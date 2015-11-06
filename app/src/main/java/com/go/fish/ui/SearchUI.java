package com.go.fish.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.go.fish.R;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.IME;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.ViewHelper;

public class SearchUI extends BaseUI implements ResultForActivityCallback,IHasHeadBar,IHasComment{
	FragmentManager fragmentMgr = null;
	BaseFragment searchFragment = null,detailFragment = null,searchInMapFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentMgr = getSupportFragmentManager();
		int layout = getIntent().getIntExtra(Const.PRI_EXTRA_LAYOUT_ID, 0);
		if(layout == R.layout.ui_f_search_item_detail){
			initDetailFragment();
			detailFragment.isFront = true;
			Bundle dataBundle = getIntent().getExtras();
			Bundle b = detailFragment.getArguments();
			b.putString(Const.PRI_JSON_DATA, dataBundle.getString(Const.PRI_JSON_DATA));
			detailFragment.setArguments(b);
			fragmentMgr.beginTransaction().add(R.id.search_content, detailFragment).commit();
		}else{
			{
				searchFragment = BaseFragment.newInstance(this, R.layout.ui_search_list);
				searchFragment.isFront = true;
				fragmentMgr.beginTransaction().add(R.id.search_content, searchFragment).commit();
			}
		}
	}
	public  void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.search_item_detail_head_back:
			case R.id.search_list_in_map_head_back:
				if(fragmentMgr.getFragments().size() == 1){
					finish();
				}else{
					fragmentMgr.popBackStack();
				}
				break;
			case R.id.search_head_back:
				finish();
				break;
			case R.id.search_item_detail_share:
				if(detailFragment != null) {
					Toast.makeText(this,"分享",Toast.LENGTH_LONG).show();
//					detailFragment.onHeadClick(view);
				}
				break;
			case R.id.search_head_btn:
				IME.hideIME(view);
				ViewPager viewPager = (ViewPager) searchFragment.getView().findViewById(R.id.search_viewpager);
				BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce);
				break;
			default:
				super.onClick(view);
				break;
		}
	}
	private void initSearchMap(){
		if(searchInMapFragment == null){
			searchInMapFragment = BaseFragment.newInstance(this, R.layout.ui_f_search_list_in_map);
		}
	}
	private void initDetailFragment() {
		if(detailFragment == null){
			detailFragment = BaseFragment.newInstance(this,R.layout.ui_f_search_item_detail);
		}
	}
	

	@Override
	public void onItemClick(View view, FPlaceData data) {
		initSearchMap();
		searchInMapFragment.setArguments(data.toBundle(searchInMapFragment.getArguments()));
		showFragment(searchInMapFragment);
	}
	
	private void showFragment(Fragment f){
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		ft.replace(R.id.search_content, f);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}
	public void onShare(View view) {
		ViewHelper.share(this, Const.DEFT_SHARE_CONTENT);
	}
	public void onMapFloatViewClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.float_view_detail_btn://详情
            String fPlaceId = searchInMapFragment.getArguments().getString(Const.STA_ID);
			RequestData rData = RequestData.newInstance(new RequestListener() {
				@Override
				public void onStart() {
					ViewHelper.showGlobalWaiting(SearchUI.this, null);
				}

				@Override
				public void onEnd(byte[] data) {
					try {
						String jsonStr = new String(data, "UTF-8");
						initDetailFragment();
						Bundle src = searchInMapFragment.getArguments();
						Bundle b = detailFragment.getArguments();
						String fPlaceId = searchInMapFragment.getArguments()
								.getString(Const.STA_ID);
						b.putString(Const.STA_ID, fPlaceId);
						b.putString(Const.STA_TEXT, src.getString(Const.STA_TEXT));
						b.putString(Const.PRI_JSON_DATA, jsonStr);
						detailFragment.setArguments(b);
						showFragment(detailFragment);
						ViewHelper.closeGlobalWaiting();

					} catch (Exception e) {
					}
				}
			}, "fishing_place_" + fPlaceId);
            rData.putData(Const.STA_ID, fPlaceId);
            NetTool.data().http(rData.syncCallback());
			break;
		case R.id.float_view_care_text:
			
			break;
		case R.id.float_view_nav_btn:
			
			break;

		default:
			break;
		}
	}
}
