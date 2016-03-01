package com.go.fish.data.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;

import com.go.fish.data.DataMgr;
import com.go.fish.data.FieldData;
import com.go.fish.data.PersonData;
import com.go.fish.data.load.PersonDataLoader.SortByDistance;
import com.go.fish.op.OpBack;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.LogUtils;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.FPlaceListAdapter;
import com.go.fish.view.FPlaceListFragment;
import com.go.fish.view.IBaseData;

public class FieldDataLoader {

	public static void loadFieldInfo(final String fPlaceId,final OpBack backListener,final Activity activity){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, fPlaceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				onStart(activity);
			}
			@Override
			public void onEnd(byte[] data) {
				try {
					if (isOver()) {// 返回键取消联网之后不应该继续处理
					} else {
						if (data != null) {
							String jsonStr = new String(data, "UTF-8");
							JSONObject response = toJSONObject(jsonStr);
							if(backListener != null){
								boolean suc = isRight(response);
								if(!suc){
									onDataError(activity,response.optString(Const.STA_MESSAGE) + "(" + fPlaceId + ")");
								}
								backListener.onBack(suc, response, activity);
							}
						} else {
							onDataError(activity);
						}
					}
					onEnd();
				} catch (Exception e) {
				}
			}
		}, jsonObject, UrlUtils.self().getFieldInfo());
	}
	public static void loadNetData(final Context context,final FPlaceListAdapter  listAdapter,
			final int listitemLayoutid, final String searchTitle,
			int startIndex,int resultCount,String defaultTag,final boolean pullRefresh, final LoaderListener loaderListener) {
    	JSONObject jsonObject = new JSONObject();
    	String url = null;
    	startIndex = listAdapter != null ? listAdapter.getCount() : 0;
		startIndex = pullRefresh ? 0 : startIndex;
    	try {
    		if(listAdapter != null && listAdapter.isAttentionList){
    			url = UrlUtils.self().getAttListForM();
    		}else{
    			url = UrlUtils.self().getQueryForMap();
				jsonObject.put(Const.STA_LAT, String.valueOf(User.self().userInfo.lat));
				jsonObject.put(Const.STA_LNG, String.valueOf(User.self().userInfo.lng));
				jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_100);
				jsonObject.put(Const.STA_START_INDEX, startIndex);
				jsonObject.put(Const.STA_TAG, defaultTag);
				jsonObject.put(Const.STA_TYPE, Const.DEFT_YC);
				jsonObject.put(Const.STA_TITLE, searchTitle);
    		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LogUtils.d("Search", "search " + searchTitle + ";tag:" + defaultTag + ";startIndex=" + startIndex);
		final String f_defaultTag = defaultTag;
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onStart() {
				 onStart(context);
			}
			@Override
			public void onEnd(byte[] data) {
				JSONObject resultData = toJSONObject(data);
				if(loaderListener == null){
					if (isRight(context,resultData,true)) {
						JSONArray jsonArray = resultData.optJSONArray(Const.STA_DATA);
						int count = jsonArray.length();
						LogUtils.d("searchUI", "initAdapterByNetData tag=" + f_defaultTag +  "count=" + count);
				        //不同tag的钓场 ；一个钓场可能有多个tag
//						if(count > 0){
							listAdapter.updateAdapter(DataMgr.makeFPlaceDatas(listitemLayoutid, jsonArray), pullRefresh);
//						}
						onEnd();
					}
				}else{
					loaderListener.onCompleted(this, resultData);
					onEnd();
				}
			}
		}, jsonObject, url);
    }
    public static void loadNetData(final ViewPager viewPager,
			final int listitemLayoutid, final String searchTitle,
			final int defaultIndex) {
    	BaseFragmentPagerAdapter adapter = (BaseFragmentPagerAdapter) viewPager.getAdapter();
    	final FPlaceListFragment listFragment = (FPlaceListFragment) adapter.getItem(defaultIndex);
    	if(listFragment.mListAdapter == null){
    		listFragment.mListAdapter = FPlaceListAdapter.setAdapter(viewPager.getContext(), null, new ArrayList<FieldData>());
    	}
    	int startIndex = listFragment.mListAdapter == null ? 0 : listFragment.mListAdapter.listDatas.size();
		String defaultTag = null;
    	defaultTag = LocalMgr.getFPlaceType()[defaultIndex];
    	loadNetData(viewPager.getContext(), listFragment.mListAdapter, listitemLayoutid, searchTitle, startIndex, Const.DEFT_REQ_COUNT_10, defaultTag, true, null);
	}
    
    public static void sortArrayList(ArrayList<FieldData> arr){
		Collections.sort(arr,new SortByDistance());
	}
	@SuppressWarnings("rawtypes")
	static class SortByDistance implements Comparator {

		@Override
		public int compare(Object lhs, Object rhs) {
			FieldData p1 = (FieldData)lhs;
			FieldData p2 = (FieldData)rhs;
			if(p1.farLong > p2.farLong){
				return 1;
			}
			return -1;
		}
		
	}
}
