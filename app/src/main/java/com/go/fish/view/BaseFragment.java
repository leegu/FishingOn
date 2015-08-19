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
import com.go.fish.data.FishingPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.ImageLoader;
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
		if (layoutId == R.layout.search_list) {
			onCreateSearchViewPager(view);
		} else if (layoutId == R.layout.search_list_in_map) {
			onCreateSearchMap(view);
		} else if (layoutId == R.layout.search_item_detail) {
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
				R.layout.search_list_in_map_float_view, vg);
		TextView detail_text = (TextView) floatView
				.findViewById(R.id.float_view_title);
		Bundle data = getArguments();
		int orderId = data.getInt(Const.ORDER_ID);
		detail_text.setText(orderId + "." + data.getString(Const.TEXT));
	}

	private void onCreateSearchViewPager(View view) {
		// TODO Auto-generated method stub
		LinearLayout tabContent = (LinearLayout) view
				.findViewById(R.id.search_tab_content);
		String[] tabItemsTitle = getResources().getStringArray(
				R.array.hfs_splace_type);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		Resources res = getActivity().getResources();
		int sw = res.getDisplayMetrics().widthPixels;
		int leftOrRightWidth = res
				.getDimensionPixelSize(R.dimen.tab_arrow_left)
				+ res.getDimensionPixelSize(R.dimen.tab_arrow_right);
		int tabItemWidth = (sw - leftOrRightWidth) / 4;
		ViewPager viewPager = (ViewPager) view
				.findViewById(R.id.search_viewpager);
		ArrayList<Fragment> fragmentArr = new ArrayList<Fragment>();
		// ArrayList<SearchViewPagerAdapter> svpa = new
		// ArrayList<SearchViewPagerAdapter>();
		FragmentManager fm = getChildFragmentManager();
		for (String t : tabItemsTitle) {
			{// tab初始化
				ViewGroup tabitemLinear = (ViewGroup) inflater.inflate(
						R.layout.search_tabitem, null);
				{
					{
						TextView tabitem = (TextView) tabitemLinear
								.getChildAt(0);// .findViewById(R.id.search_tabitem_text);
						tabitem.setText(t);
					}
					{
						TextView status = (TextView) tabitemLinear
								.getChildAt(1);// .findViewById(R.id.search_tabitem_status);
						status.setBackgroundColor(Color.GRAY);
					}
				}
				tabContent.addView(tabitemLinear, new LayoutParams(
						tabItemWidth, LayoutParams.MATCH_PARENT));
			}
			{// listFragment初始化
				ListFragment listFragment = new ListFragment(mCallback);
				Bundle b = new Bundle();
				b.putInt(Const.LAYOUT_ID, R.layout.search_list_fragment);
				b.putString(Const.FRAGMENT_TAG, t);
				listFragment.setArguments(b);
				fm.beginTransaction().add(listFragment, null);
				fragmentArr.add(listFragment);
			}
		}

		BaseFragmentPagerAdapter bfpa = new BaseFragmentPagerAdapter(
				getChildFragmentManager(), fragmentArr);
		viewPager.setAdapter(bfpa);
	}

	private void onCreateSearchDetail(final View view) {
		Bundle b = getArguments();
		((TextView) view.findViewById(R.id.search_item_detail_title)).setText(b
				.getString(Const.TEXT));

		String spId = b.getString(Const.FISHING_PLACE_ID);
		RequestData rData = new RequestData(Const.SEARCH_URL,
				new RequestListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onEnd(final byte[] data) {
						// TODO Auto-generated method stub
						MessageHandler.sendMessage(new MessageListener() {

							@Override
							public void onExecute() {
								// TODO Auto-generated method stub

								try {
									String jsonStr = new String(data, "utf8");
									jsonStr = "{urls:['http://a.hiphotos.baidu.com/image/pic/item/5366d0160924ab18b7da6d3533fae6cd7a890be7.jpg','http://g.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72aee889e1fd22a2834359bbbc0.jpg','http://cdnq.duitang.com/uploads/item/201407/26/20140726212046_2AWny.jpeg'],"
											+ "menu_tabs:[{title:'年8月7日',content:'o1.'},{title:'年8月7日',content:'o1.'},{title:'年8月7日',content:'o1.'},{title:'年8月7日',content:'o1.'}]"
											+ "}";
									JSONObject json = new JSONObject(jsonStr);
									{
										if (json.has(Const.URLS)) {
											JSONArray urlArr = json
													.getJSONArray(Const.URLS);
											int len = urlArr.length();
											if (len > 0) {
												LayoutInflater inflator = LayoutInflater
														.from(getActivity());
												ImageLoader iLoader = ImageLoader
														.initEnv();
												ViewPager banner = (ViewPager) view
														.findViewById(R.id.search_item_detail_banner);
												ArrayList<ImageView> pviews = new ArrayList<ImageView>(
														len);
												final MyPagerAdapter pageAdapter = new MyPagerAdapter(
														pviews);
												for (int i = 0; i < len; i++) {
													String url = urlArr
															.getString(i);
													final ImageView iv = (ImageView) inflator
															.inflate(
																	R.layout.viewpager_imageview,
																	null);
													iv.setPadding(0, 0, 0, 0);
													iLoader.executeBitmapLoad(new ImageLoader.DownloadTask(
															url,
															new ImageLoader.DownloadTaskListener() {
																@Override
																public void onStart() {
																}

																@Override
																public void onEnd(
																		String downUrl,
																		Bitmap bitmap) {
																	iv.setBackgroundDrawable(new BitmapDrawable(
																			bitmap));
																	// iv.refreshDrawableState();
																	// iv.invalidate();
																}
															}));
													pviews.add(iv);
												}
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
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
					}
				});
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
		void onItemClick(View view, FishingPlaceData data);
	}
}
