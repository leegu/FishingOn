package com.go.fish.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.FieldData;
import com.go.fish.data.load.FieldDataLoader;
import com.go.fish.op.FieldUIOp;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

/**
 * 钓场列表适配器
 * @author TianGuo
 *
 */
public class FPlaceListAdapter extends BaseAdapter implements OnItemClickListener,OnLongClickListener{

	public int flag = FieldUIOp.FLAG_SEARCH_RESULT;
	public boolean isAttentionList = false;
	private LayoutInflater mInflater;
	private ResultForActivityCallback mCallback;
	TextView mFooterTextView = null;
	ListView mListView = null;
	public ArrayList<FieldData> listDatas = null;
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
		if(listDatas != null && listDatas.size() > 1){
			FieldDataLoader.sortArrayList(listDatas);
		}
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(listView != null){
			attachToListView(mListView);
		}
	}
	static public FPlaceListAdapter setAdapter(Context context,ListView listView, ArrayList<FieldData> array) {
		return setAdapter(context, listView, array, FieldUIOp.FLAG_SEARCH_RESULT);
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
		case R.layout.listitem_field_3_rows:
			item = FieldUIOp.onGetFieldView((Activity)mListView.getContext(), mInflater, ld.layout_id, flag, ld, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	public void updateAdapter(ArrayList<FieldData> array, boolean pullRefresh){
		boolean hasListView = mListView != null;
		if(pullRefresh){
			listDatas.clear();
			listDatas.addAll(0,array);
		}else{
			listDatas.addAll(listDatas.size(), array);
		}
		if(hasListView){
			if(listDatas.size() > 0) {
				FieldDataLoader.sortArrayList(listDatas);
				if(mFooterTextView != null && mListView.getFooterViewsCount() > 0){
					try {
						mListView.removeFooterView(mFooterTextView);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				if(mFooterTextView == null){
					mFooterTextView = new TextView((Activity)mListView.getContext());
					mFooterTextView.setClickable(false);
					mFooterTextView.setGravity(Gravity.CENTER);
					mListView.addFooterView(mFooterTextView, null, false);
				}
				mFooterTextView.setText("无数据");
			}
			notifyDataSetChanged();
		}
	}

	
}
