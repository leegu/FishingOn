package com.go.fish.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.go.fish.R;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.IME;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.MessageHandler.MessageListener;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;
import com.go.fish.view.FPlaceListFragment;

public class SearchUI extends BaseUI implements ResultForActivityCallback,IHasHeadBar{
	FragmentManager fragmentMgr = null;
	BaseFragment searchFragment = null,detailFragment = null,searchInMapFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
//		setContentView(layout_id);
		fragmentMgr = getSupportFragmentManager();
		{
			searchFragment = BaseFragment.newInstance(this, R.layout.ui_search_list);
			searchFragment.isFront = true;
			fragmentMgr.beginTransaction().add(R.id.search_content, searchFragment).commit();
		}
		initDetailFragment();
	}
	
	public  void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.search_item_detail_head_back:
		case R.id.search_list_in_map_head_back:
			fragmentMgr.popBackStack();
			break;
		case R.id.search_head_back:
			finish();
			break;
		case R.id.search_head_btn:
			IME.hideIME(view);
			NetTool.httpGet(RequestData.newInstance(new RequestListener() {
				@Override
				public void onStart() {}
				@Override
				public void onEnd(byte[] datas) {
					if(datas != null){
						MessageHandler.sendMessage(new MessageListener<ArrayList<FPlaceData>>() {
							ArrayList<FPlaceData> arr = null;
							@Override
							public MessageListener<ArrayList<FPlaceData>> init(ArrayList<FPlaceData> args) {
								this.arr = args;
								return this;
							}
							@Override
							public void onExecute() {
								ViewPager viewPager = (ViewPager)searchFragment.getView().findViewById(R.id.search_viewpager);
								FPlaceListFragment listFragment = (FPlaceListFragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
								listFragment.updateData(this.arr);
							}
						}.init(DataMgr.makeFPlaceDatas(R.layout.listitem_search, datas)));
					}
				}
			},"search_fishing_place"));
			break;
		default:
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
	public void onMapFloatViewClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.float_view_detail_btn:
			initDetailFragment();
			Bundle src = searchInMapFragment.getArguments();
			Bundle b = detailFragment.getArguments();
			b.putString(Const.FISHING_PLACE_ID, src.getString(Const.FISHING_PLACE_ID));
			b.putString(Const.TEXT, src.getString(Const.TEXT));
			detailFragment.setArguments(b);
			showFragment(detailFragment);
			break;
		case R.id.float_view_care_btn:
			
			break;
		case R.id.float_view_nav_btn:
			
			break;

		default:
			break;
		}
	}
}
