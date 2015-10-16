package com.go.fish.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.go.fish.R;
import com.go.fish.data.FNewsData;
import com.go.fish.data.PersonData;
import com.go.fish.ui.UIMgr;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.user.User;
import com.go.fish.util.Const;

public class AdapterExt extends BaseAdapter {

	public int layout_id ;
	private LayoutInflater mInflater;
	Context context = null;
	ListView mListView = null;
	ArrayList<IBaseData> listDatas = null;
	
	boolean useScroolListener = false;
	private AdapterExt(ListView listView,JSONArray array,int layoutId){
		mListView = listView;
		this.context = listView.getContext();
		layout_id = layoutId;
		ArrayList<IBaseData> arr = null;
		//TODO 解析json获取数据
		switch (layout_id){
			case R.layout.listitem_fnews:
				useScroolListener = true;
				arr = makeFNewsDataArray(array);
				break;
			case R.layout.listitem_friend:
				arr = makePersonDataArray(array);
				break;
		}
//		if(useScroolListener){
			mListView.setOnScrollListener(new ScrollListenerImpl());
//		}
		listDatas = arr;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public static AdapterExt newInstance(ListView listView,JSONArray array,int layoutId){
		AdapterExt ada = new AdapterExt(listView, array,layoutId);
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
		switch (layout_id) {
		case R.layout.listitem_fnews:
			item = onGetMyFNews(position, convertView, parent);
			break;
		case R.layout.listitem_friend:
			item = onGetFriend(position, convertView, parent);
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
	class FriendViewHolder {
		TextView nameView,farView,fYearView,fTimesView,lineView;
		ImageView userIcon;
	}
	private View onGetFriend(int position, View convertView, ViewGroup parent){
		ViewGroup item = null;
		IBaseData data = listDatas.get(position);
		PersonData personData = (PersonData)data;
		FriendViewHolder holder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new FriendViewHolder();
			item.setTag(holder);
			holder.nameView = ((TextView)item.findViewById(R.id.listitem_friend_name));
			holder.farView = ((TextView)item.findViewById(R.id.listitem_friend_far));
			holder.fYearView = ((TextView)item.findViewById(R.id.listitem_friend_fyear));
			holder.fTimesView = ((TextView)item.findViewById(R.id.listitem_friend_ftimes));
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
			holder.lineView = (TextView)item.findViewById(R.id.line2);
		} else {
			item = (ViewGroup)convertView;
			holder = (FriendViewHolder)convertView.getTag();
		}
		ViewHelper.load(holder.userIcon, personData.photoUrl,true,false);
		holder.nameView.setText(personData.userName);
		holder.farView.setText(personData.far);
		holder.fYearView.setText(""+personData.fYears);
		holder.fTimesView.setText("" + personData.fTimes);
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
		}
		return item;
	}
	class FNewsViewHolder {
		TextView nameView,farView,fYearView,fTimesView,textView,listitem_fnews_comment_count;
		ImageView userIcon;
		View listitem_friend_layout;
		HAutoAlign mHAutoAlign;
		View[] childViews = null;
	}
	private View onGetMyFNews(final int position, View convertView, ViewGroup parent){
		IBaseData data = listDatas.get(position);
		FNewsViewHolder holder = null;
		View item = null;
		final FNewsData newsData = (FNewsData)data;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new FNewsViewHolder();
			item.setTag(holder);
			holder.listitem_friend_layout = item.findViewById(R.id.listitem_friend_layout);
			holder.nameView = (TextView)item.findViewById(R.id.listitem_friend_name);
			holder.farView = (TextView)item.findViewById(R.id.listitem_friend_far);
			holder.fYearView = (TextView)item.findViewById(R.id.listitem_friend_fyear);
			holder.fTimesView = (TextView)item.findViewById(R.id.listitem_friend_ftimes);
			holder.textView = (TextView)item.findViewById(R.id.textView);
			holder.listitem_fnews_comment_count = (TextView)item.findViewById(R.id.listitem_fnews_comment_count);
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
			if(newsData.netPicUrl != null){
				int size = newsData.netPicUrl.length;
				ViewStub vs = (ViewStub)item.findViewById(R.id.fnews_image_contianer_view_stub);
				if(vs != null){
					holder.mHAutoAlign = (HAutoAlign)((ViewGroup)vs.inflate()).getChildAt(0);
				}else{
					holder.mHAutoAlign = (HAutoAlign)item.findViewById(R.id.h_image_view_container);
				}
				holder.childViews = new LinearLayout[size];
				OnClickListener listener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra(Const.PRI_EXTRA_IMAGE_INDEX, position);
						intent.putExtra(Const.PRI_EXTRA_IMAGE_URLS, newsData.netPicUrl);
						UIMgr.showActivity((Activity)context,intent,ImageViewUI.class.getName());
					}
				};
				int p = item.getContext().getResources().getDimensionPixelSize(R.dimen.base_space_2);
				for(int i = 0; i < size; i++){
					holder.childViews[i] = mInflater.inflate(R.layout.h_image_view_item, null);
					holder.childViews[i].setBackgroundColor((int)new Random().nextLong());
//					holder.childViews[i].setPadding(p,p,p,p);
					holder.childViews[i].setTag(newsData.netPicUrl[i]);
					holder.childViews[i].setOnClickListener(listener);
					ViewHelper.load((ImageView)((ViewGroup)holder.childViews[i]).getChildAt(0), (String)holder.childViews[i].getTag(), true,true);
				}
				holder.mHAutoAlign.fillChilds(holder.childViews);
			}
		} else {
			holder = (FNewsViewHolder)convertView.getTag();
			item = convertView;
		}
		
		if(newsData.authorData.id == User.self().userInfo.id){
			holder.listitem_friend_layout.setVisibility(View.GONE);
		}else{
			ViewHelper.load(holder.userIcon, newsData.authorData.photoUrl,true,false);
			holder.nameView.setText(newsData.authorData.userName);
			holder.farView.setText(newsData.authorData.far);
			holder.fYearView.setText(""+newsData.authorData.fYears);
			holder.fTimesView.setText(""+newsData.authorData.fTimes);
		}
		holder.textView.setText(newsData.content);
		holder.listitem_fnews_comment_count.setText(""+newsData.commentCount);
		final FNewsViewHolder f_holder = holder;
		if(newsData.netPicUrl != null && f_holder.childViews != null){
			item.postDelayed(new Runnable() {
				@Override
				public void run() {
//					if(position >= mFirstVisibleItem && position <= mFirstVisibleItem + mVisibleItemCount){
//						for(int i = 0; i < f_holder.childViews.length; i++){
//							ViewHelper.load(f_holder.childViews[i], (String)f_holder.childViews[i].getTag(), true);
//						}
//					}
				}
			}, 1000);
		}
		return item;
	}
	
	
	private int mFirstVisibleItem;
	// GridView中可见的图片的数量
	private int mVisibleItemCount;
	// 记录是否是第一次进入该界面
	private boolean isFirstEnterThisActivity = true;
	
	private class ScrollListenerImpl implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
		}
		
		/**
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			if (scrollState == SCROLL_STATE_IDLE) {
//				Toast.makeText(view.getContext(), "onScrollStateChanged", Toast.LENGTH_LONG);
//				update(mFirstVisibleItem, mVisibleItemCount);
//			} else {
////				cancelAllTasks();
//			}
		}
		
//		private void update(int firstVisibleItem,int visibleItemCount){
//			if(layout_id == R.layout.listitem_fnews){
//				for (int i = firstVisibleItem; i < firstVisibleItem
//						+ visibleItemCount; i++) {
//					FNewsData newsData = (FNewsData)listDatas.get(i);
//					ImageViewAdapter adapter = adapterHashMap.get(newsData);
//					GridView gridView = ((GridView)mListView.findViewWithTag(String.valueOf(i)));
//					if(gridView != null){//为null表示没有图片
//						if(adapter != null){
//							gridView.setAdapter(adapter);
//							adapter.onScrollStateChanged();
//						}else{
//							adapter = new ImageViewAdapter(gridView);
//							adapter.mContext = mListView.getContext();
//							adapter.mUrls = new String[newsData.netPicUrl.length];
//							for(int j = 0; j < newsData.netPicUrl.length; j++){
//								adapter.mUrls[j] = newsData.netPicUrl[j];
//							}
//							gridView.setAdapter(adapter);
//							adapterHashMap.put(newsData, adapter);
//						}
//					}
//				}
//			}
//		}
	}
}
