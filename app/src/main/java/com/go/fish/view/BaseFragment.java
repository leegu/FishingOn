package com.go.fish.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.go.fish.R;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.ImageLoader;
import com.go.fish.util.ImageLoader.DownloadTask;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.MessageHandler.MessageListener;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;

public class BaseFragment extends Fragment {
	private static final String TAG = "MainView";
	ResultForActivityCallback mCallback = null;
	public boolean isFront = false;

	public static BaseFragment newInstance(ResultForActivityCallback callback,
			int layoutId) {
		BaseFragment f = new BaseFragment(callback);
		Bundle b = new Bundle();
		b.putInt(Const.LAYOUT_ID, layoutId);
		f.setArguments(b);
		return f;
	}

	private BaseFragment(ResultForActivityCallback callback) {
		mCallback = callback;
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
		int layoutId = b.getInt(Const.LAYOUT_ID);
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
		int orderId = data.getInt(Const.ORDER_ID);
		detail_text.setText(orderId + "." + data.getString(Const.TEXT));
	}

	private void onCreateSearchViewPager(View view) {
		// TODO Auto-generated method stub
		String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
		((ViewGroup)view).addView(ViewHelper.newMainView(getActivity(), getChildFragmentManager(),mCallback, tabItemsTitle, R.layout.list_fragment));
	}

	private void onCreateSearchDetail(final View view) {
		Bundle b = getArguments();
		((TextView) view.findViewById(R.id.search_item_detail_title)).setText(b
				.getString(Const.TEXT));

		String spId = b.getString(Const.FISHING_PLACE_ID);
		RequestData rData = RequestData.newInstance(
				new RequestListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onEnd(byte[] data) {try{
						// TODO Auto-generated method stub
						MessageHandler.sendMessage(new MessageListener<byte[]>() {
							String jsonStr = null;
							@Override
							public MessageListener init(byte[] args) {
								try {
									jsonStr =new String(args, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
								return this;
							}
							@Override
							public void onExecute() {
								try {
									JSONObject json = new JSONObject(jsonStr);
									{
										if (json.has(Const.URLS)) {
											JSONArray urlArr = json.getJSONArray(Const.URLS);
											int len = urlArr.length();
											if (len > 0) {
												LayoutInflater inflator = LayoutInflater.from(getActivity());
												ImageLoader iLoader = ImageLoader.initEnv();
												final ViewPager banner = (ViewPager) view.findViewById(R.id.search_item_detail_banner);
												{
													View bannerParent = view.findViewById(R.id.search_item_detail_banner_parent);
													int viewPagerHeight = getResources().getDisplayMetrics().widthPixels * 9 /16;//保持16:9的比例
													bannerParent.getLayoutParams().height = viewPagerHeight;
												}
												ArrayList<ImageView> pviews = new ArrayList<ImageView>(len);
												final ViewGroup focusItems = (ViewGroup)view.findViewById(R.id.search_item_detail_banner_focus_items);
												int focusItemSize = getActivity().getResources().getDimensionPixelSize(R.dimen.focus_item_size);
												final MyPagerAdapter pageAdapter = new MyPagerAdapter(pviews);
												for (int i = 0; i < len; i++) {
													String url = urlArr.getString(i);
													ImageView iv = (ImageView) inflator.inflate(R.layout.viewpager_imageview,null);
													iLoader.executeBitmapLoad(new ImageLoader.DownloadTask<ImageView>(url,iv));
													pviews.add(iv);
													TextView item = new TextView(getActivity());//.inflate(R.layout.viewpager_focus_item,null);
													if(i == 0){
														item.setBackgroundResource(R.drawable.circle_gray_solid);
													}else{
														item.setBackgroundResource(R.drawable.circle_gray);
													}
													focusItems.addView(item,new LayoutParams(focusItemSize, focusItemSize));
													
												}
												banner.setOnPageChangeListener(new OnPageChangeListener() {
													@Override
													public void onPageSelected(int arg0) {
														ViewHelper.updateChildrenBackground(focusItems, arg0, R.drawable.circle_gray_solid, R.drawable.circle_gray);
													}
													@Override
													public void onPageScrolled(int arg0, float arg1, int arg2) {}
													@Override
													public void onPageScrollStateChanged(int arg0) {}
												});
												banner.setAdapter(pageAdapter);
											}
										}
									}
									if (json.has("menu_tabs")) {
										ViewGroup menus = (ViewGroup) view
												.findViewById(R.id.search_item_detail_menu_tabs);
										ViewGroup tabitem_contents = (ViewGroup) view
												.findViewById(R.id.search_item_detail_menu_tabitem_contents);
										JSONArray arr = json
												.getJSONArray("menu_tabs");
										int len = arr.length();
										for (int i = 0; i < len; i++) {
											JSONObject ti = arr.getJSONObject(i);
											{
												TextView t = new TextView(getActivity());
												t.setText(ti.getString("title"));
												menus.addView(t);
											}
											{
												TextView t = new TextView(getActivity());
												t.setText(ti.getString("content"));
												tabitem_contents.addView(t);
											}
										}
									}
									{
										ListView commentListView = (ListView) view
												.findViewById(R.id.search_item_detail_comment_list);

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
							
							
						}.init(data));
					
					}catch(Exception e){}}
				},"fishing_place_" + spId);
		rData.putValue(Const.FISHING_PLACE_ID, spId);
		NetTool.httpGet(rData);
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
