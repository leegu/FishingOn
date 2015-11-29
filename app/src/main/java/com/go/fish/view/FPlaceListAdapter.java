package com.go.fish.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FPlaceData;

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

	class FPlaceViewHolder{
		TextView listitem_fplace_title,fplace_desc,float_view_distance;
		ImageView listitem_fplace_icon;
		ViewGroup fplace_temp_title;
	}
	private View onGetNearFPlace(FPlaceData fPlace, View convertView, ViewGroup parent){
		ViewGroup item = null;
		FPlaceViewHolder mViewHolder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
			mViewHolder = new FPlaceViewHolder();
			item.setTag(mViewHolder);
			mViewHolder.listitem_fplace_title = ((TextView)item.findViewById(R.id.listitem_fplace_title));
			mViewHolder.fplace_desc = ((TextView)item.findViewById(R.id.fplace_desc));
			mViewHolder.float_view_distance = ((TextView)item.findViewById(R.id.float_view_distance));
			mViewHolder.fplace_temp_title = ((ViewGroup)item.findViewById(R.id.fplace_temp_title));
			mViewHolder.listitem_fplace_icon = ((ImageView)item.findViewById(R.id.listitem_fplace_icon));
		} else {
			item = (ViewGroup)convertView;
			mViewHolder = (FPlaceViewHolder)item.getTag();
		}
		mViewHolder.fplace_temp_title.setTag(fPlace.sid);
		mViewHolder.fplace_temp_title.getChildAt(0).setVisibility(View.GONE);//隐藏最近状态
		mViewHolder.fplace_temp_title.getChildAt(1).setVisibility(View.VISIBLE);//显示关注图标
		return item;
	}
	private View onGetSearchListitem(FPlaceData fPlace, View convertView, ViewGroup parent){
		ViewGroup item = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
		} else {
			item = (ViewGroup)convertView;
		}
		((TextView)item.getChildAt(1)).setText(fPlace.title);
		return item;
	}
}
