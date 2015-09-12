package com.go.fish.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FNewsData;
import com.go.fish.data.PersonData;

public class AdapterExt extends BaseAdapter {

	public int layout_id ;
	private LayoutInflater mInflater;
	Context context = null;
	ArrayList<IBaseData> listDatas = null;
	private AdapterExt(Context context,JSONArray array,int layoutId){
		this.context = context;
		layout_id = layoutId;
		ArrayList<IBaseData> arr = null;
		//TODO 解析json获取数据
		switch (layout_id){
			case R.layout.listitem_fnews:
				arr = makeFNewsDataArray(array);
				break;
			case R.layout.listitem_friend:
				arr = makePersonDataArray(array);
				break;
		}
		listDatas = arr;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public static AdapterExt newInstance(Context context,JSONArray array,int layoutId){
		AdapterExt ada = new AdapterExt(context, array,layoutId);
		return ada;
	}
	public static ArrayList<IBaseData> makeFNewsDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			FNewsData newsData = FNewsData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
	}
	public static ArrayList<IBaseData> makePersonDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			PersonData newsData = PersonData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
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
		notifyDataSetChanged();
	}
	private View onGetFriend(IBaseData data, View convertView, ViewGroup parent){
		ViewGroup item = null;
		PersonData personData = (PersonData)data;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			((TextView)item.findViewById(R.id.listitem_friend_name)).setText(personData.userName);
			((TextView)item.findViewById(R.id.listitem_friend_time)).setText(personData.far);
//			((TextView)item.findViewById(R.id.listitem_friend_fyear)).setText(personData.fYears);
//			((TextView)item.findViewById(R.id.listitem_friend_ftimes)).setText(personData.fTimes);
		} else {
			item = (ViewGroup)convertView;
		}
		return item;
	}
	private View onGetMyFNews(IBaseData data, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			FNewsData newsData = (FNewsData)data;
			((TextView)item.findViewById(R.id.textView)).setText(newsData.content);
//			((TextView)item.findViewById(R.id.listitem_fnews_good_count)).setText(newsData.goodCount);
//			((TextView)item.findViewById(R.id.listitem_fnews_comment_count)).setText(newsData.commentCount);
//			((TextView)item.findViewById(R.id.listitem_fnews_care_count)).setText(newsData.careCount);
//			GridView gridView = (GridView)item.findViewById(R.id.my_fishing_item_pics_gridview);
//			gridView.setAdapter();
		} else {
			item = (ViewGroup)convertView;
		}
		return item;
	}
}
