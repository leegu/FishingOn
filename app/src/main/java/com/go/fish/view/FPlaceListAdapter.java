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

/**
 * 钓场列表适配器
 * @author TianGuo
 *
 */
public class FPlaceListAdapter extends BaseAdapter{

	public static final int FLAG_SEARCH_RESULT = 0;
	public static final int FLAG_NEAR_RESULT = 1;
	public static final int FLAG_CARE_RESULT = 2;
	public int flag = 0;
	private LayoutInflater mInflater;
	Context context = null;
	ArrayList<FPlaceData> listDatas = null;
	public FPlaceListAdapter(Context context,ArrayList<FPlaceData> array, int fPlaceResultType){
		this.context = context;
		listDatas = array;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		// TODO Auto-generated method stub
		View item = null;
		FPlaceData ld = listDatas.get(position);
		switch (ld.layout_id) {
//		case R.layout.listitem_search:
//			item = onGetSearchListitem(ld, convertView, parent);
//			break;
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
		ViewGroup fplace_state;
	}
	private View onGetNearFPlace(FPlaceData fPlace, View convertView, ViewGroup parent){//搜索结果展示，附件钓场
		ViewGroup item = null;
		FPlaceViewHolder mViewHolder = null;
		//周边调用列表
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
			mViewHolder = new FPlaceViewHolder();
			item.setTag(mViewHolder);
			mViewHolder.listitem_fplace_title = ((TextView)item.findViewById(R.id.listitem_fplace_title));
			mViewHolder.fplace_desc = ((TextView)item.findViewById(R.id.fplace_desc));
			mViewHolder.float_view_distance = ((TextView)item.findViewById(R.id.float_view_distance));
			mViewHolder.fplace_state = ((ViewGroup)item.findViewById(R.id.fplace_state));
			mViewHolder.listitem_fplace_icon = ((ImageView)item.findViewById(R.id.listitem_fplace_icon));
		} else {
			item = (ViewGroup)convertView;
			mViewHolder = (FPlaceViewHolder)item.getTag();
		}
		mViewHolder.listitem_fplace_title.setText(fPlace.title);
		mViewHolder.fplace_desc.setText(fPlace.desp);
		mViewHolder.float_view_distance.setText(fPlace.distance);
		if(flag == FLAG_SEARCH_RESULT){
			mViewHolder.fplace_state.getChildAt(0).setVisibility(View.GONE);
			mViewHolder.fplace_state.getChildAt(1).setVisibility(View.VISIBLE);
			mViewHolder.fplace_state.getChildAt(1).setTag(fPlace);
		}else if(flag == FLAG_CARE_RESULT){
		//
		}
		return item;
	}
}
