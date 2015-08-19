package com.go.fish.view;

import java.util.ArrayList;
import java.util.Random;

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

import com.go.fish.data.FishingPlaceData;
import com.go.fish.util.Const;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class ListFragment extends Fragment implements OnItemClickListener{

	ListAdapter mListAdapter = null;
	ListView mListView = null;
	TextView mFooterTextView = null;
	ResultForActivityCallback mCallback = null;
	ListFragment(ResultForActivityCallback callback){
		mCallback = callback;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(mListView == null){
			Bundle b = getArguments();
			int layoutId = b.getInt(Const.LAYOUT_ID);
			mListView = (ListView)inflater.inflate(layoutId, container, false);
//			View empty = mListView.findViewById(R.id.list_empty);
//			empty.setVisibility(View.VISIBLE);
//			mListView.setEmptyView(empty);
			ArrayList<FishingPlaceData> array = new ArrayList<FishingPlaceData>();
			mListAdapter = new ListAdapter(getActivity(), array);
			mFooterTextView = new TextView(getActivity());
			mFooterTextView.setText("无数据");
			mFooterTextView.setGravity(Gravity.CENTER);
			mListView.addFooterView(mFooterTextView);
			mListView.setOnItemClickListener(this);
			mListView.setAdapter(mListAdapter);
			mListView.setBackgroundColor((int)new Random().nextLong());
		}
		return mListView;
	}
	
	public void updateData(ArrayList<FishingPlaceData> arr){
		mListView.removeFooterView(mFooterTextView);
		mListAdapter.listDatas.addAll(arr);
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		mCallback.onItemClick(view, mListAdapter.listDatas.get(position));
	}
}
