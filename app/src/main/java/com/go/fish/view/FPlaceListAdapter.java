package com.go.fish.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FPlaceData;

import java.util.ArrayList;

public class FPlaceListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	Context context = null;
	ArrayList<FPlaceData> listDatas = null;
	public FPlaceListAdapter(Context context,ArrayList<FPlaceData> array){
		this.context = context;
		listDatas = array;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item = null;
		FPlaceData ld = listDatas.get(position);
		switch (ld.layout_id) {
		case R.layout.listitem_search:
			item = onGetSearchListitem(ld, convertView, parent);
			break;
		case R.layout.listitem_fpalce:
			item = onGetNearFPlace(ld, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	public void updateAdapter(ArrayList<FPlaceData> array){
		listDatas.addAll(array);
		notifyDataSetChanged();
	}

	private View onGetNearFPlace(FPlaceData fPlace, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
		} else {
			item = (ViewGroup)convertView;
		}
		return item;
	}
	private View onGetSearchListitem(FPlaceData fPlace, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
		} else {
			item = (ViewGroup)convertView;
		}
		((TextView)item.getChildAt(1)).setText(fPlace.text);
		return item;
	}
}
