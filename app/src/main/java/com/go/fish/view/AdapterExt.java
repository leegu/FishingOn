package com.go.fish.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.data.PodCastData;
import com.go.fish.data.load.CommentDataLoader;
import com.go.fish.data.load.FishingNewsDataLoader;
import com.go.fish.data.load.PersonDataLoader;
import com.go.fish.data.load.PodCastDataLoader;
import com.go.fish.op.CommentUIOp;
import com.go.fish.op.FishingNewsUIOp;
import com.go.fish.op.PersonUIOp;
import com.go.fish.op.PodCastUIOp;
import com.go.fish.util.LogUtils;

public class AdapterExt extends BaseAdapter {

	static final String TAG = AdapterExt.class.getSimpleName();
	Object extra;
	public int layout_id ;
	private LayoutInflater mInflater;
	Context context = null;
	ListView mListView = null;
	ArrayList<IBaseData> listDatas = null;
	OnBaseDataClickListener mOnBaseDataClickListener = null;
	public static final int FLAG_MY_NEWS = 1;
	public static final int FLAG_NEWS = 2;
	public int mFlag = FLAG_NEWS;
	public boolean mHideCare = false;
	boolean useScroolListener = false;
	private AdapterExt(ListView listView,OnBaseDataClickListener listener,JSONArray array, int layoutId){
		mListView = listView;
		this.context = listView.getContext();
		layout_id = layoutId;
		ArrayList<IBaseData> arr = null;
		//TODO 解析json获取数据
		mOnBaseDataClickListener = listener;
		arr = makeArray(array);
//		if(useScroolListener){
			mListView.setOnScrollListener(new ScrollListenerImpl());
//		}
		listDatas = arr;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(mOnBaseDataClickListener != null){
					mOnBaseDataClickListener.onItemClick(view, listDatas.get(position));
				}
			}
		});
		listView.setAdapter(this);
	}
	private ArrayList<IBaseData> makeArray(JSONArray array){
		ArrayList<IBaseData> arr = null;
		switch (layout_id){
		case R.layout.listitem_podcast:
			useScroolListener = true;
			arr = PodCastDataLoader.makePodCastDataArray(array);
			break;
		case R.layout.listitem_person_2_rows:
		case R.layout.listitem_person_3_rows:
			arr = PersonDataLoader.makePersonDataArray(array);
			Collections.sort(arr, new SortByDistance());
			break;
		case R.layout.listitem_comment:
			arr = CommentDataLoader.makeCommentDataArray(array);
			break;
		case R.layout.listitem_fishing_news_3_row:
			arr = FishingNewsDataLoader.makeFishingNewsDataArray(array);
			break;
		}
		return arr;
	}
	@SuppressWarnings("rawtypes")
	class SortByDistance implements Comparator {

		@Override
		public int compare(Object lhs, Object rhs) {
			PersonData p1 = (PersonData)lhs;
			PersonData p2 = (PersonData)rhs;
			if(p1.farLong > p2.farLong){
				return 1;
			}
			return 0;
		}
		
	}
	public static AdapterExt newInstance(ListView listView,OnBaseDataClickListener listener,JSONArray array, int layoutId){
		AdapterExt ada = new AdapterExt(listView, listener,array, layoutId);
		return ada;
	}
	
	
	public void setExtra(Object extra){
		this.extra = extra;
	}
	public Object getExtra(){
		return this.extra;
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
		case R.layout.listitem_podcast://钓播
			item = PodCastUIOp.onGetPodCastView((Activity)context,mInflater, layout_id, position, mFlag, listDatas, convertView, parent);
			break;
		case R.layout.listitem_person_3_rows:
		case R.layout.listitem_person_2_rows:
			item = PersonUIOp.onGetFriend((Activity)context, mInflater, layout_id, position, mHideCare, listDatas, convertView, parent);
			break;
		case R.layout.listitem_comment:
			item = CommentUIOp.onGetComment((Activity)context, mInflater, layout_id,position, listDatas, convertView, parent);
			break;
		case R.layout.listitem_fishing_news_3_row:
			item = FishingNewsUIOp.onGetFishingNewsView((Activity)context, mInflater, layout_id,position, listDatas, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	
	public static void setheight(ListView listView)
    {
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter==null){return;}
        int maxHeight=0;
        int itemNum=listAdapter.getCount();
        for(int i=0;i<itemNum;i++)
        {
            View listItem=listAdapter.getView(i,null,listView);
            listItem.measure(0,0);
            int thisHeight=listItem.getMeasuredHeight();//计算子项View的宽高
            maxHeight=(maxHeight>thisHeight)?(maxHeight):(thisHeight);
        }
        for(int j=0;j<itemNum;j++)
        {
            View listItem=listAdapter.getView(j,null,listView);
            ViewGroup.LayoutParams params=listItem.getLayoutParams();
            params.height=maxHeight;
            listItem.setLayoutParams(params);
        }
    }
	public void updateAdapter(ArrayList<IBaseData> arr, boolean pullRefresh){
		if(pullRefresh){
			listDatas.clear();
			listDatas.addAll(arr);
//			listDatas.addAll(0,arr);//20161014 下拉刷新删除之前的重新加载列表内的数据
		}else{
			listDatas.addAll(arr);
		}
		updateAdapter();
	}
	public void updateAdapter(){
//		setheight(mListView);
		notifyDataSetChanged();
	}
	public void updateAdapter(IBaseData data){
		updateAdapter(data,true);
	}
	public void updateAdapter(IBaseData data, boolean pullRefresh){
		listDatas.add(0, data);
		updateAdapter();
	}
	
	public void updateAdapter(JSONArray array){
		updateAdapter(array,true);
	}
	public void updateAdapter(JSONArray array, boolean pullRefresh){
		ArrayList<IBaseData> arr = makeArray(array);
		updateAdapter(arr, pullRefresh);
	}
	
	private class ScrollListenerImpl implements OnScrollListener {
		boolean isLastRow = false;
		boolean isFirstRow = false;
		int firstVisibleItem, visibleItemCount;
        @Override    
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {    
            //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。    
            //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）    
            //visibleItemCount：当前能看见的列表项个数（小半个也算）    
            //totalItemCount：列表项共数    
        	if(this.firstVisibleItem != firstVisibleItem && firstVisibleItem == 0){
        		isFirstRow = true;
        	}
        	this.firstVisibleItem = firstVisibleItem;
        	this.visibleItemCount = visibleItemCount;
            //判断是否滚到最后一行    
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {    
                isLastRow = true;    
            }    
        }    
        @Override    
        public void onScrollStateChanged(AbsListView view, int scrollState) {    
            //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调    
            //回调顺序如下    
            //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动    
            //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）    
            //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动             
            //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；  
            //由于用户的操作，屏幕产生惯性滑动时为2  
        
            //当滚到最后一行且停止滚动时，执行加载    
        	if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {    
	            if (isLastRow){
	            	//加载元素    
	            	loadMore();
	            	isLastRow = false;
	            }else if(isFirstRow){
	            	loadRecent();
	            	isFirstRow = false;
	            }else{
            		update(firstVisibleItem, visibleItemCount);
	            }
        	}
        }    
		
        private void loadRecent(){
        	LogUtils.d(TAG, "loadRecent start =" + listDatas.size());
        	if(layout_id == R.layout.listitem_podcast){
        		PodCastDataLoader.getNetPodList(mListView, String.valueOf(mListView.getTag()), true);
        	}else if(layout_id == R.layout.listitem_comment){
        		CommentDataLoader.loadList(mListView.getContext(), AdapterExt.this, String.valueOf(mListView.getTag()),true);
        	}
        }
        private void loadMore(){
        	LogUtils.d(TAG, "loadMore start =" + listDatas.size());
        	if(layout_id == R.layout.listitem_podcast){
        		PodCastDataLoader.getNetPodList(mListView,String.valueOf(mListView.getTag()), false);
        	}else if(layout_id == R.layout.listitem_comment){
        		CommentDataLoader.loadList(mListView.getContext(), AdapterExt.this,String.valueOf(mListView.getTag()),false);
        	}
        }
		private void update(int firstVisibleItem,int visibleItemCount){
			if(layout_id == R.layout.listitem_podcast){
				ArrayList<IBaseData> listData = AdapterExt.this.listDatas;
				PodCastUIOp.updateList(AdapterExt.this.mInflater,listData, firstVisibleItem, visibleItemCount);
			}
			LogUtils.d(TAG,"update    " + firstVisibleItem  +" ------ " + visibleItemCount);
		}
		
	}
	
	
	public interface OnBaseDataClickListener {
		void onItemClick(View view, IBaseData data);
	}
}
