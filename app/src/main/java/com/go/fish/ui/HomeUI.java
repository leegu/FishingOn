package com.go.fish.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.go.fish.R;

public class HomeUI extends FragmentActivity {

	FragmentManager fragmentMgr = null;
	MapView mMapView;
	RelativeLayout floatView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(BaseUI.LAYOUT_ID, 0);
		setContentView(layout_id);
		fragmentMgr = getSupportFragmentManager();
		initFirstTabItem();
	}

	void initFirstTabItem() {
		{// 设置foot item 获取焦点状态
			// showFragment(R.id.home_fishing_place,
			// R.id.foot_bar_fishing_place);
			View view = findViewById(R.id.foot_bar_fishing_place);
			TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
			text.setTextColor(getResources().getColor(
					R.color.foot_bar_focus_text_color));
		}
		{// 初始化地图
			ViewGroup vg = (ViewGroup) findViewById(R.id.fishing_place_bmap_view);
			mMapView = new MapView(this, new BaiduMapOptions());
			vg.addView(mMapView);
			
			LayoutInflater.from(this).inflate(R.layout.hfs_float_view, vg);
//			vg.addView(floatView);
			// {
			// MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15).build();
			// BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
			// .compassEnabled(false).zoomControlsEnabled(false);
			// SupportMapFragment map = SupportMapFragment.newInstance(bo);
			// fragmentMgr.beginTransaction()
			// .add(R.id.fishing_place_bmap_view, map, "bmap")
			// .commit();
			// }

		}
	}

	int[] ids = new int[] { R.id.home_fishing_place, R.id.home_care,
			R.id.home_appear, R.id.home_fishing_news, R.id.home_my };

	int[] footBarIds = new int[] { R.id.foot_bar_fishing_place,
			R.id.foot_bar_care, R.id.foot_bar_appear,
			R.id.foot_bar_fishing_news, R.id.foot_bar_my };

	public void showFragment(int fid, int footBarId) {
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		Fragment f = fragmentMgr.findFragmentById(fid);
		ft.show(f);
		for (int id : ids) {
			if (id != fid) {
				f = fragmentMgr.findFragmentById(id);
				ft.hide(f);
			}
		}
		ft.commit();

		for (int fbid : footBarIds) {
			View view = findViewById(fbid);
			TextView text = (TextView) ((ViewGroup) view).getChildAt(1);
			if (fbid == footBarId) {
				text.setTextColor(getResources().getColor(
						R.color.foot_bar_focus_text_color));
			} else {
				text.setTextColor(getResources().getColor(
						R.color.foot_bar_text_color));
			}
		}
	}

	public void onFootItemClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.foot_bar_fishing_place:
			showFragment(R.id.home_fishing_place, id);
			break;
		case R.id.foot_bar_care:
			showFragment(R.id.home_care, id);
			break;
		case R.id.foot_bar_appear:
			showFragment(R.id.home_appear, id);
			break;
		case R.id.foot_bar_fishing_news:
			showFragment(R.id.home_fishing_news, id);
			break;
		case R.id.foot_bar_my:
			showFragment(R.id.home_my, id);
			break;
		}
	}
	
	public void onFishingSplachClick(View view){
		int id = view.getId();
		switch (id) {
		case R.id.hfs_hfs_splace_type_btn:
			PopWinListItemAdapter adapter = new PopWinListItemAdapter(R.array.hfs_splace_type);
			showPopWin(view, adapter);
			break;
		}
	}
	
	private void showPopWin(View view,ListAdapter adapter){
		ListView list = (ListView)LayoutInflater.from(this).inflate(R.layout.pop_win_list, null);
		list.setAdapter(adapter);
		PopupWindow pw = new PopupWindow(list,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		pw.setOutsideTouchable(false);
//		FrameLayout list = new FrameLayout(this);
//		list.setBackgroundColor(Color.RED);
		pw.setContentView(list);
//		pw.showAtLocation(view, Gravity.LEFT, 0,0);
		pw.showAtLocation(view, Gravity.LEFT,(int)getResources().getDimension(R.dimen.pop_win_list_width), 0);  
//		pw.showAsDropDown(view);
	}
	
	
	
	class PopWinListItemData{
		String text = null;
		int destId = 0;
	}
	
	class PopWinListItemAdapter extends BaseAdapter{

		ArrayList<PopWinListItemData> itemsData = null;
		PopWinListItemAdapter(ArrayList<PopWinListItemData> datas){
			itemsData = datas;
		}
		PopWinListItemAdapter(int stringArrayId){
			this(getResources().getStringArray(R.array.hfs_splace_type));
		}
		PopWinListItemAdapter(String[] sArrays){
			ArrayList<PopWinListItemData> datas = new ArrayList<HomeUI.PopWinListItemData>();
			String[] sDatas = getResources().getStringArray(R.array.hfs_splace_type);
			for(String s : sDatas){
				PopWinListItemData pwid = new PopWinListItemData();
				pwid.text = s;
				datas.add(pwid);
			}
			itemsData = datas;
		}
		@Override
		public int getCount() {
			return itemsData.size();
		}

		@Override
		public Object getItem(int position) {
			return itemsData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
//				TextView textview = (TextView)LayoutInflater.from(getBaseContext()).inflate(R.layout.pop_win_list_item, null);
				TextView textview = new TextView(HomeUI.this);
				PopWinListItemData data = itemsData.get(position);
				textview.setTag(data);
				textview.setTextColor(0xff0000);
				textview.setText(data.text);
				view = textview;
			}else{
				view = convertView;
			}
			return view;
		}

		
		
	}
}