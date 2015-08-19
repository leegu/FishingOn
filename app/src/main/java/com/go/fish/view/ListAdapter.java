package com.go.fish.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FishingPlaceData;

public class ListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	Context context = null;
	ArrayList<FishingPlaceData> listDatas = null;
	public ListAdapter(Context context,ArrayList<FishingPlaceData> array){
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
		TextView text;
        if (convertView == null) {
            text = (TextView)mInflater.inflate(R.layout.search_listitem, parent, false);
        } else {
            text = (TextView)convertView;
        }
        FishingPlaceData ld = listDatas.get(position);
        text.setText(ld.text);
        return text;
	}

}
