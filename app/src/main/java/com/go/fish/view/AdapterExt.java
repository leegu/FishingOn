package com.go.fish.view;

import java.util.ArrayList;

import com.go.fish.R;
import com.go.fish.data.FNewsData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;

public class AdapterExt extends BaseAdapter {

	public int layout_id ;
	private LayoutInflater mInflater;
	Context context = null;
	ArrayList<IBaseData> listDatas = null;
	private AdapterExt(Context context,JSONArray array,int layoutId){
		this.context = context;
		layout_id = layoutId;
		//TODO 解析json获取数据
		switch (layout_id){
			case R.layout.listitem_fnews:
				break;
			case R.layout.listitem_friend:
				break;
		}
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		arr.add(FNewsData.newInstance(""));
		arr.add(FNewsData.newInstance(""));
		arr.add(FNewsData.newInstance(""));
		listDatas = arr;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public static AdapterExt newInstance(Context context,JSONArray array,int layoutId){
		AdapterExt ada = new AdapterExt(context, array,layoutId);

		return ada;
	}
	@Override
	public int getCount() {
		return listDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = null;
		IBaseData data = listDatas.get(position);
		switch (layout_id) {
		case R.layout.listitem_fnews:
			item = onGetMyFNews(data, convertView, parent);
			break;
		case R.layout.listitem_friend:
			item = onGetFriend(data, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	public void updateAdapter(ArrayList<IBaseData> arr){
		listDatas.addAll(arr);
	}
	private View onGetFriend(IBaseData data, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
		} else {
			item = (ViewGroup)convertView;
		}
		return item;
	}
	private View onGetMyFNews(IBaseData data, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
		} else {
			item = (ViewGroup)convertView;
		}
		return item;
	}
}
