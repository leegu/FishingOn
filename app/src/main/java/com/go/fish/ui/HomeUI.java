package com.go.fish.ui;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.go.fish.R;
import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;

public class HomeUI extends FragmentActivity implements IHasHeadBar{

	FragmentManager fragmentMgr = null;
	MapView mMapView;
	RelativeLayout floatView = null;
	ViewGroup mHomeMainView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
		setContentView(layout_id);
		mHomeMainView = (ViewGroup)findViewById(R.id.home_main_view);
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
			mMapView = MapUtil.newMap(this);
			vg.addView(mMapView);
			
			LayoutInflater.from(this).inflate(R.layout.float_view_fplace, vg);
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
	
	public void onFishingPlaceClick(View view){
		int id = view.getId();
		switch (id) {
		case R.id.hfs_fishing_tools_store_btn:
			showFishingToolsStore();
			break;
		case R.id.hfs_hfs_splace_type_btn:
			if(splaceTypePopWinView == null){
				ListView list = (ListView)LayoutInflater.from(this).inflate(R.layout.list_pop_win, null);
				splaceTypePopWinView = list;
				PopWinListItemAdapter adapter = new PopWinListItemAdapter(R.array.hfs_splace_type);
				list.setAdapter(adapter);
				list.setDivider(new ColorDrawable(Color.GRAY));
				list.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.list_item_divider));
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						TextView tv = (TextView)((ViewGroup)view).getChildAt(1);//.findViewById(R.id.pop_list_item_status_text);
						if(tv.getTag() != null && tv.getTag().equals("1")){
							tv.setTag("0");
							tv.setTextColor(Color.GRAY);
						}else{
							tv.setTag("1");
							tv.setTextColor(Color.GREEN);
						}
//						popupWindow.dismiss();
					}
				});
			}
			showPopWin(view,splaceTypePopWinView);
			break;
		}
	}
	
	private void showSearchView(){
		UIMgr.showActivity(this, R.layout.search,SearchUI.class.getName());
	}
	private void showFishingToolsStore(){
		Toast.makeText(this,"显示渔具店",0).show();
	}
	View splaceTypePopWinView = null;
	PopupWindow popupWindow = null;
	private void showPopWin(View anchor,View view){
		int width = getResources().getDimensionPixelSize(R.dimen.pop_win_list_width);
		if(popupWindow == null){
			popupWindow = new PopupWindow();
			popupWindow.setWidth(width);
			popupWindow.setHeight(-2);
			// 使其聚集
	        popupWindow.setFocusable(true);
	        // 设置允许在外点击消失
	        popupWindow.setOutsideTouchable(true);
	        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(anchor, -width, -anchor.getHeight());
	}
	
//	private void changeStatus(ListView list,int position){
//		list.getAdapter().
//	}
	
	
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
			PopWinListItemData data = itemsData.get(position);
			if (convertView == null) {
				view = LayoutInflater.from(getBaseContext()).inflate(R.layout.listitem_pop_win, null);
//				 view = new TextView(HomeUI.this);
				((TextView)view.findViewById(R.id.pop_list_item_text)).setText(data.text);
				TextView status =(TextView)view.findViewById(R.id.pop_list_item_status_text);
				status.setText("√");
				status.setTextColor(Color.GRAY);
			}else{
				view = convertView;
			}
			view.setTag(data);
			return view;
		}

		
		
	}

	@Override
	public void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.fishing_place_head_search_btn:
			showSearchView();
			break;
		}
	}
	
	public void onMyClick(View view){
		int id = view.getId();
		switch (id) {
		case R.id.ui_f_my_exit:
			finish();
			break;
		case R.id.ui_f_my_go_next:{
			UIMgr.showActivity(this, R.layout.ui_my_sec, BaseUI.class.getName());
			break;
			}
		}
	}
}