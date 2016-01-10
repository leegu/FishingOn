package com.go.fish.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.go.fish.R;
import com.go.fish.data.FieldData;
import com.go.fish.op.FieldUIOp;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil;

public class BaseFragment extends Fragment {
	private static final String TAG = "MainView";
	ResultForActivityCallback mCallback = null;
	public int mFlag = FieldUIOp.FLAG_SEARCH_RESULT;
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
		if (layoutId == R.layout.ui_search_list) {//搜索界面、附近钓场
			onCreateFieldViewPager(view);
//		} else if (layoutId == R.layout.ui_f_search_list_in_map) {
//			onCreateSearchMap(view);
		} else if (layoutId == R.layout.ui_detail_field || layoutId == R.layout.ui_detail_podcast) {
			String jsonStr = b.getString(Const.PRI_JSON_DATA);
			try {
				JSONObject resultData = new JSONObject(jsonStr);
				FieldUIOp.onCreateFieldDetail(getActivity(),view,resultData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return view;
	}

//	private void onCreateSearchMap(View view) {
//		ViewGroup vg = (ViewGroup) view
//				.findViewById(R.id.search_list_in_map_bmap_view);
//		MapView mapView = MapUtil.newMap(getActivity());
//		vg.addView(mapView);
//		View floatView = LayoutInflater.from(getActivity()).inflate(
//				R.layout.float_field_in_map, vg);
//		TextView detail_text = (TextView) floatView
//				.findViewById(R.id.float_view_title);
//		Bundle data = getArguments();
//		int orderId = data.getInt(Const.STA_ORDER_ID);
//		detail_text.setText(orderId + "." + data.getString(Const.STA_TEXT));
//	}

	private void onCreateFieldViewPager(View view) {
		// TODO Auto-generated method stub
//		String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
		String[] tabItemsTitle = LocalMgr.getFPlaceType();
		ViewGroup vg = ViewHelper.newMainView(getActivity(), getChildFragmentManager(),mCallback, tabItemsTitle);
		((ViewGroup)view).addView(vg);
		ViewPager viewPager = (ViewPager) vg.findViewById(R.id.search_viewpager);
		String searchTitle = ((TextView)view.findViewById(R.id.search_list_edit)).getText().toString();
		if(mFlag == FieldUIOp.FLAG_NEAR_RESULT){
			BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_field, searchTitle, viewPager.getCurrentItem());
		}
	}

	String[] strMenuLables = null;
	ViewGroup menuContent = null;
	PopupWindow menuPopupWindow = null;
	public void onHeadClick(View view){
//		if(menuContent == null){
//			ListView list = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.list_pop_win, null);
//			list.setDividerHeight(0);
//			menuContent = list;
//			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//										int position, long id) {
//					ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.menu_contents);
//					for (int i = 0; i < vg.getChildCount(); i++) {
//						ViewGroup menuItem = (ViewGroup) vg.getChildAt(i);
//						TextView contentTitle = ((TextView) menuItem.getChildAt(0));
//						TextView item = ((TextView) ((ViewGroup)view).getChildAt(0));
//
//						if (contentTitle.getText().equals(item.getText())) {
//							int[] loc = new int[2];
//							menuItem.getLocationOnScreen(loc);
//							ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.ui_f_search_item_detail_root_scrollview);
//							int offset = loc[1] /*- scrollView.getMeasuredHeight()*/;
//							if (offset < 0) {
//								offset = 0;
//							}
//							scrollView.smoothScrollTo(0, offset);
//							break;
//						}
//					}
//					menuPopupWindow.dismiss();
//				}
//			});
//			PopWinListItemAdapter.newInstance(getActivity(), list, strMenuLables);
//		}
//		menuPopupWindow = ViewHelper.showPopupWindow(getActivity(),menuPopupWindow,view,menuContent);
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
		void onItemClick(View view, FieldData data);
	}
}
