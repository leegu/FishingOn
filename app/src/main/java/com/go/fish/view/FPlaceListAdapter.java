package com.go.fish.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.go.fish.R;
import com.go.fish.data.FieldData;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

/**
 * 钓场列表适配器
 * @author TianGuo
 *
 */
public class FPlaceListAdapter extends BaseAdapter implements OnItemClickListener,OnLongClickListener{

	public static final int FLAG_SEARCH_RESULT = 0;
	public static final int FLAG_NEAR_RESULT = 1;
	public static final int FLAG_CARE_RESULT = 2;
	public int flag = FLAG_SEARCH_RESULT;
	boolean isAttentionList = false;
	private LayoutInflater mInflater;
	private ResultForActivityCallback mCallback;
	View mFooterTextView = null;
	ListView mListView = null;
	ArrayList<FieldData> listDatas = null;
	/**
	 * @param context TODO
	 * @param listView
	 * @param array
	 * @param resultFlag
	 */
	FPlaceListAdapter(Context context,ListView listView,ArrayList<FieldData> array, int resultFlag){
		mListView = listView;
		this.flag = resultFlag;
		listDatas = array;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(listView != null){
			attachToListView(mListView);
		}
	}
	static public FPlaceListAdapter setAdapter(Context context,ListView listView, ArrayList<FieldData> array) {
		return setAdapter(context, listView, array, FLAG_SEARCH_RESULT);
	}
	static public FPlaceListAdapter setAdapter(Context context,ListView listView,ArrayList<FieldData> array, int resultFlag) {
		if(listView == null || listView.getAdapter() == null){
			return new FPlaceListAdapter(context, listView, array, resultFlag);
		}else{
			return (FPlaceListAdapter)listView.getAdapter();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		if(mCallback != null){
			mCallback.onItemClick(view, listDatas.get(position));
		}
	}
	@Override
	public boolean onLongClick(View v) {
		ViewHelper.showToast(v.getContext(), "长按" + v);
		return false;
	}
	public void setmResultForActivityCallback(ResultForActivityCallback callback){
		mCallback = callback;
	}
	public void attachToListView(ListView list){
		mListView = list;
		list.setAdapter(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnLongClickListener(this);
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
		FieldData ld = listDatas.get(position);
		switch (ld.layout_id) {
//		case R.layout.listitem_search:
//			item = onGetSearchListitem(ld, convertView, parent);
//			break;
		case R.layout.listitem_field:
			item = onGetNearFPlace(ld, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	public void updateAdapter(ArrayList<FieldData> array){
		boolean hasListView = mListView != null;
		if(hasListView){
			if(mListView.getFooterViewsCount() > 0) {
				mListView.removeFooterView(mFooterTextView);
			}
		}
		listDatas.addAll(0,array);
		if(hasListView){
			notifyDataSetChanged();
		}
	}

	class FPlaceViewHolder{
		TextView listitem_fplace_title,fplace_desc,float_view_distance;
		ImageView listitem_fplace_icon;
		ViewGroup fplace_state;
		View float_view_detail_btn;
	}
	private View onGetNearFPlace(FieldData fPlace, View convertView, ViewGroup parent){//搜索结果展示，附件钓场
		ViewGroup item = null;
		FPlaceViewHolder mViewHolder = null;
		//周边调用列表
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(fPlace.layout_id, parent, false);
			mViewHolder = new FPlaceViewHolder();
			item.setTag(mViewHolder);
			mViewHolder.float_view_detail_btn = item.findViewById(R.id.float_view_detail_btn);
			mViewHolder.listitem_fplace_title = ((TextView)item.findViewById(R.id.listitem_fplace_title));
			mViewHolder.fplace_desc = ((TextView)item.findViewById(R.id.fplace_desc));
			mViewHolder.float_view_distance = ((TextView)item.findViewById(R.id.float_view_distance));
			mViewHolder.fplace_state = ((ViewGroup)item.findViewById(R.id.fplace_state));
			mViewHolder.listitem_fplace_icon = ((ImageView)item.findViewById(R.id.listitem_fplace_icon));
		} else {
			item = (ViewGroup)convertView;
			mViewHolder = (FPlaceViewHolder)item.getTag();
		}
		mViewHolder.float_view_detail_btn.setTag(fPlace);
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
