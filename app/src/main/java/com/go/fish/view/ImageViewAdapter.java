package com.go.fish.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.go.fish.R;

public class ImageViewAdapter extends BaseAdapter {

	// GridView中可见的第一张图片的下标
	private int mFirstVisibleItem;
	// GridView中可见的图片的数量
	private int mVisibleItemCount;
	// 记录是否是第一次进入该界面
	private boolean isFirstEnterThisActivity = true;
	public String[] mUrls = null;
	public Context mContext = null;
	
	ScrollListenerImpl mScrollListenerImpl = null;
	private GridView mGridView;
	public ImageViewAdapter(GridView gridView) {
		mGridView = gridView;
		// TODO Auto-generated constructor stub
		mGridView.setOnScrollListener((mScrollListenerImpl = new ScrollListenerImpl()));
	}
	@Override
	public int getCount() {
		return mUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return mUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup ivP = null;
		if (convertView == null) {
			if (mUrls.length == 1) {
				ivP = (ViewGroup) LayoutInflater.from(mContext).inflate(
						R.layout.griditem_image, null);
			} else {
				ivP = (ViewGroup) LayoutInflater.from(mContext).inflate(
						R.layout.griditem_image_3col, null);
			}
			ViewHolder holder = new ViewHolder();
			holder.rootView = ivP;
			ivP.setTag(holder);
		} else {
			ivP = (ViewGroup) ((ViewHolder) convertView.getTag()).rootView;
		}
		ImageView iv = (ImageView) ivP.getChildAt(0);
		iv.setTag(mUrls[position]);
		ViewHelper.load(iv, mUrls[position],false);
		return ivP;
	}

	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				String imageUrl = mUrls[i];
					ImageView imageView = (ImageView) mGridView
							.findViewWithTag(imageUrl);
					if (imageView != null) {
						ViewHelper.load(imageView, imageUrl);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ScrollListenerImpl implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
			if (isFirstEnterThisActivity && visibleItemCount > 0) {
				loadBitmaps(firstVisibleItem, visibleItemCount);
				isFirstEnterThisActivity = false;
			}
		}

		/**
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == SCROLL_STATE_IDLE) {
				Toast.makeText(view.getContext(), "onScrollStateChanged", Toast.LENGTH_LONG);
				loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
			} else {
//				cancelAllTasks();
			}
		}
	}

	public void onScrollStateChanged() {
		loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
	}
	
}
