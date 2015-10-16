package com.go.fish.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.go.fish.R;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.ImageLoader;
import com.go.fish.util.MapUtil;

public class BaseFragment extends Fragment {
	private static final String TAG = "MainView";
	ResultForActivityCallback mCallback = null;
	public boolean isFront = false;

	public static BaseFragment newInstance(ResultForActivityCallback callback,
			int layoutId) {
		BaseFragment f = new BaseFragment();
		f.mCallback = callback;
		Bundle b = new Bundle();
		b.putInt(Const.PRI_LAYOUT_ID, layoutId);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle b = getArguments();
		int layoutId = b.getInt(Const.PRI_LAYOUT_ID);
		View view = inflater.inflate(layoutId, container, false);
		if (layoutId == R.layout.ui_search_list) {
			onCreateSearchViewPager(view);
		} else if (layoutId == R.layout.ui_f_search_list_in_map) {
			onCreateSearchMap(view);
		} else if (layoutId == R.layout.ui_f_search_item_detail) {
			onCreateSearchDetail(view);
		}
		return view;
	}

	private void onCreateSearchMap(View view) {
		ViewGroup vg = (ViewGroup) view
				.findViewById(R.id.search_list_in_map_bmap_view);
		MapView mapView = MapUtil.newMap(getActivity());
		vg.addView(mapView);
		View floatView = LayoutInflater.from(getActivity()).inflate(
				R.layout.float_view_search_list_in_map, vg);
		TextView detail_text = (TextView) floatView
				.findViewById(R.id.float_view_title);
		Bundle data = getArguments();
		int orderId = data.getInt(Const.STA_ORDER_ID);
		detail_text.setText(orderId + "." + data.getString(Const.STA_TEXT));
	}

	private void onCreateSearchViewPager(View view) {
		// TODO Auto-generated method stub
		String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
		((ViewGroup)view).addView(ViewHelper.newMainView(getActivity(), getChildFragmentManager(),mCallback, tabItemsTitle));
	}

	String[] strMenuLables = null;
	ViewGroup menuContent = null;
	PopupWindow menuPopupWindow = null;
	public void onHeadClick(View view){
		if(menuContent == null){
			ListView list = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.list_pop_win, null);
			list.setDividerHeight(0);
			menuContent = list;
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.menu_contents);
					for (int i = 0; i < vg.getChildCount(); i++) {
						ViewGroup menuItem = (ViewGroup) vg.getChildAt(i);
						TextView contentTitle = ((TextView) menuItem.getChildAt(0));
						TextView item = ((TextView) ((ViewGroup)view).getChildAt(0));

						if (contentTitle.getText().equals(item.getText())) {
							int[] loc = new int[2];
							menuItem.getLocationOnScreen(loc);
							ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.ui_f_search_item_detail_root_scrollview);
							int offset = loc[1] /*- scrollView.getMeasuredHeight()*/;
							if (offset < 0) {
								offset = 0;
							}
							scrollView.smoothScrollTo(0, offset);
							break;
						}
					}
					menuPopupWindow.dismiss();
				}
			});
			PopWinListItemAdapter.newInstance(getActivity(), list, strMenuLables);
		}
		menuPopupWindow = ViewHelper.showPopupWindow(getActivity(),menuPopupWindow,view,menuContent);
	}
	private void onCreateSearchDetail(final View view) {
		Bundle b = getArguments();
		((TextView) view.findViewById(R.id.search_item_detail_title)).setText(b.getString(Const.STA_TITLE));
		try{
			String jsonStr = b.getString(Const.PRI_JSON_DATA);
			JSONObject json = new JSONObject(jsonStr);
			{
				if (json.has(Const.STA_URLS)) {
					JSONArray urlArr = json.getJSONArray(Const.STA_URLS);
					int len = urlArr.length();
					if (len > 0) {
						LayoutInflater inflator = LayoutInflater.from(getActivity());
						ImageLoader iLoader = ImageLoader.self();
						final ViewPager banner = (ViewPager) view.findViewById(R.id.search_item_detail_banner);
//						{
//							View bannerParent = view.findViewById(R.id.search_item_detail_banner_parent);
//							int viewPagerHeight = getResources().getDisplayMetrics().widthPixels * 9 /16;//保持16:9的比例
//							bannerParent.getLayoutParams().height = viewPagerHeight;
//						}
						ArrayList<ImageView> pviews = new ArrayList<ImageView>(len);
						final ViewGroup focusItems = (ViewGroup)view.findViewById(R.id.search_item_detail_banner_focus_items);
						int focusItemSize = getActivity().getResources().getDimensionPixelSize(R.dimen.focus_item_size);
						final MyPagerAdapter pageAdapter = new MyPagerAdapter(pviews);
						for (int i = 0; i < len; i++) {
							String url = urlArr.getString(i);
							ImageView iv = (ImageView) inflator.inflate(R.layout.w_imageview,null);
							ViewHelper.load(iv, url);
							pviews.add(iv);
							TextView item = new TextView(getActivity());
							item.setLayoutParams(new LayoutParams(focusItemSize,focusItemSize));
							if(i == 0){
								item.setBackgroundResource(R.drawable.circle_gray_solid_focus);
							}else{
								item.setBackgroundResource(R.drawable.circle_gray_solid);
							}
							focusItems.addView(item);
						}
						banner.setOnPageChangeListener(new OnPageChangeListener() {
							@Override
							public void onPageSelected(int arg0) {
								ViewHelper.updateChildrenBackground(focusItems, arg0, R.drawable.circle_gray_solid_focus, R.drawable.circle_gray_solid);
							}
							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2) {}
							@Override
							public void onPageScrollStateChanged(int arg0) {}
						});
						banner.setAdapter(pageAdapter);
//						banner.postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								banner.setCurrentItem(0);
//							}
//						},100);
					}
				}
			}
//			if (json.has("menu_tabs")) {
//				view.findViewById(R.id.search_item_detail_head_menu).setVisibility(View.VISIBLE);
//				ViewGroup menus = (ViewGroup) view.findViewById(R.id.menu_contents);
//				JSONArray arr = json.getJSONArray("menu_tabs");
//				int len = arr.length();
//				strMenuLables = new String[len];
//				LayoutInflater inflater = LayoutInflater.from(getActivity());
//				for (int i = 0; i < len; i++) {
//					JSONObject ti = arr.getJSONObject(i);
//					View menuItem = inflater.inflate(R.layout.w_fplace_detail_menu_item, null);
//					{
//						TextView t = (TextView)menuItem.findViewById(R.id.title);
//						String title = ti.getString("title");
//						t.setText(title);
//						strMenuLables[i] = title;
//					}
//					{
//						TextView t = (TextView)menuItem.findViewById(R.id.value);
//						t.setText(ti.getString("content"));
//					}
//					menus.addView(menuItem);
//				}
//			}
			if(json.has("comments")){
//				ListView commentListView = (ListView) view.findViewById(R.id.search_item_detail_comment_list);
//				CommentAdapter.fillToListView(commentListView,json.getJSONArray("comments"));
			}
		}catch (Exception e){

		}
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
		isFront = !hidden;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "TestFragment-----onDestroy");
	}

	public interface ResultForActivityCallback {
		void onItemClick(View view, FPlaceData data);
	}
}
