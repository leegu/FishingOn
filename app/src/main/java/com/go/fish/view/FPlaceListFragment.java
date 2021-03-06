package com.go.fish.view;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.data.DataMgr;
import com.go.fish.data.FieldData;
import com.go.fish.util.Const;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class FPlaceListFragment extends Fragment {
	public String name = null;
	public FPlaceListAdapter mListAdapter = null;
	ListView mListView = null;
//	TextView mFooterTextView = null;
	private ResultForActivityCallback mCallback = null;
//	private boolean created = false;
	public static FPlaceListFragment newInstance(ResultForActivityCallback callback,int layoutId){
		FPlaceListFragment listFragment = new FPlaceListFragment();
		listFragment.mCallback = callback;
		Bundle b = new Bundle();
		b.putInt(Const.PRI_LAYOUT_ID, layoutId);
		listFragment.setArguments(b);
		return listFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		created = true;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(mListView == null){
			Bundle b = getArguments();
			int layoutId = b.getInt(Const.PRI_LAYOUT_ID);//list_fragment.xml 钓场列表
			mListView = (ListView)inflater.inflate(layoutId, container, false);
			updateAdapter();
			mListAdapter.setmResultForActivityCallback(mCallback);
			mListAdapter.attachToListView(mListView);
			if(mListAdapter.isEmpty()) {
				TextView mFooterTextView = new TextView(getActivity());
				mListAdapter.mFooterTextView = mFooterTextView;
				mFooterTextView.setText("无数据");
				mFooterTextView.setClickable(false);
				mFooterTextView.setGravity(Gravity.CENTER);
				mListView.addFooterView(mFooterTextView, null, false);
			}else{
				if(mListView.getFooterViewsCount() > 0) {
					mListView.removeFooterView(mListAdapter.mFooterTextView);
				}
			}
//			mListView.setBackgroundColor((int)new Random().nextLong());//设置随机背景色
		}
		return mListView;
	}
	
	public void updateData(ArrayList<FieldData> arr,boolean pullRefresh){
		mListAdapter.updateAdapter(arr, pullRefresh);
	}

	

	//更新适配器内容
	public void updateAdapter(){
		Bundle bundle = getArguments();
		if(bundle.containsKey(Const.PRI_EXTRA_DATA)){
			try {
				int listitemLayoutid = bundle.getInt(Const.PRI_LAYOUT_ID);
				JSONArray jsonArr = new JSONArray(bundle.getString(Const.PRI_EXTRA_DATA));
				ArrayList<FieldData> fPlaceArr = DataMgr.makeFPlaceDatas(listitemLayoutid, jsonArr);
				if(mListAdapter == null) {
					//构造钓场列表所需list 适配器
					mListAdapter = FPlaceListAdapter.setAdapter(getActivity(), mListView, fPlaceArr);
				} else {
					updateData(fPlaceArr,true);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bundle.remove(Const.PRI_EXTRA_DATA);
		}else{
			if(mListAdapter == null && mListView != null) {
				mListAdapter = FPlaceListAdapter.setAdapter(getActivity(), mListView, new ArrayList<FieldData>());
			}
		}
	}
	
	public int getSize(){
		if(mListAdapter != null && mListAdapter.listDatas != null){
			return mListAdapter.listDatas.size();
		}else{
			return 0;
		}
	}
	
}
