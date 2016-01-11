package com.go.fish.data.load;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.go.fish.data.PodCastData;
import com.go.fish.op.OpBack;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;

public class PodCastDataLoader {
	
	public static void getPodCastInfo(String id,final OpBack backListener,final Activity activity){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_ID, id);
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
								backListener.onBack(isRight(response), response, activity);
							}
						} else {
							onDataError(activity);
						}
					}
					onEnd();
				} catch (Exception e) {
				}
			}
		}, jsonObject, UrlUtils.self().getInfo());
	}
	
	public static void getNetPodList(final ListView fNListView,final String mobileNum,final boolean pullRefresh) {
		JSONObject jsonObject = new JSONObject();
		try {
			int count = fNListView.getAdapter() != null ? fNListView.getAdapter().getCount() : 0;
//			count = 0;
			jsonObject.put(Const.STA_START_INDEX, count);
//			jsonObject.put(Const.STA_SIZE, 4);
			jsonObject.put(Const.STA_SIZE, Const.DEFT_REQ_COUNT_10);
			jsonObject.put(Const.STA_MOBILE, mobileNum);//默认所有钓播
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		//网络数据抓取,进行更新
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onStart() {
				onStart(fNListView.getContext());
			}
			@Override
			public void onEnd(byte[] data) {
				JSONObject response = toJSONObject(data);
				if(isRight(fNListView.getContext(),response,true)){
					final JSONArray arr= response.optJSONArray(Const.STA_DATA);
					if(arr != null && arr.length() > 0){
						final ListAdapter adapter = fNListView.getAdapter();
						if(adapter instanceof AdapterExt){
							new Thread(){
								public void run() {
									final ArrayList<IBaseData> ai = makePodCastDataArray(arr);
									fNListView.postDelayed(new Runnable() {
										@Override
										public void run() {
											((AdapterExt)adapter).updateAdapter(ai, pullRefresh);
										}
									}, 10);
								};
							}.start();
						}else if(adapter instanceof HeaderViewListAdapter){
							((AdapterExt)((HeaderViewListAdapter)adapter).getWrappedAdapter()).updateAdapter(makePodCastDataArray(arr), pullRefresh);
						}
//						else if(adapter instanceof FPlaceListAdapter){
//							ArrayList<FPlaceData> fDataArr = DataMgr.makeFPlaceDatas(R.layout.listitem_fpalce,arr);
//							((FPlaceListAdapter)adapter).updateAdapter(fDataArr);
//						}
					}else{
						ViewHelper.showToast(fNListView.getContext(), Const.DEFT_NO_DATA);
					}
					onEnd();
				}
			}
		}, jsonObject, UrlUtils.self().getPodCastList());
	}
	public static ArrayList<IBaseData> makePodCastDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			PodCastData newsData = PodCastData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
	}
}
