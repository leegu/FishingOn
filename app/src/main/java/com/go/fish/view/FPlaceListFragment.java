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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.data.DataMgr;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class FPlaceListFragment extends Fragment implements OnItemClickListener{
	public String name = null;
	FPlaceListAdapter mListAdapter = null;
	ListView mListView = null;
	TextView mFooterTextView = null;
	ResultForActivityCallback mCallback = null;
	private boolean created = false;
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
		created = true;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(mListView == null){
			Bundle b = getArguments();
			int layoutId = b.getInt(Const.PRI_LAYOUT_ID);
			mListView = (ListView)inflater.inflate(layoutId, container, false);
			updateAdapter();
			if(mListAdapter.isEmpty()) {
				mFooterTextView = new TextView(getActivity());
				mFooterTextView.setText("无数据");
				mFooterTextView.setClickable(false);
				mFooterTextView.setGravity(Gravity.CENTER);
				mListView.addFooterView(mFooterTextView, null, false);
			}
			mListView.setOnItemClickListener(this);
			mListView.setAdapter(mListAdapter);
			mListView.setBackgroundColor((int)new Random().nextLong());
		}
		return mListView;
	}
	
	public void updateData(ArrayList<FPlaceData> arr){
		if(mListView.getFooterViewsCount() > 0) {
			mListView.removeFooterView(mFooterTextView);
		}
		mListAdapter.listDatas.addAll(arr);
		if(created) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		mCallback.onItemClick(view, mListAdapter.listDatas.get(position));
	}

	public void updateAdapter(){
		Bundle bundle = getArguments();
		if(bundle.containsKey(Const.PRI_EXTRA_DATA)){
			try {
				int listitemLayoutid = bundle.getInt(Const.PRI_EXTRA_LAYOUT_ID);
				JSONArray jsonArr = new JSONArray(bundle.getString(Const.PRI_EXTRA_DATA));
				ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(listitemLayoutid, jsonArr);
				if(mListAdapter == null) {
					mListAdapter = new FPlaceListAdapter(getActivity(), fPlaceArr);
				} else {
					updateData(fPlaceArr);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bundle.remove(Const.PRI_EXTRA_DATA);
		}else{
			if(mListAdapter == null) {
				mListAdapter = new FPlaceListAdapter(mListView.getContext(), new ArrayList<FPlaceData>());
			}
		}
	}
}
