package com.go.fish.view;

import java.util.ArrayList;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.CommentData;
import com.go.fish.data.FNewsData;
import com.go.fish.data.FPlaceZixunData;
import com.go.fish.data.PersonData;
import com.go.fish.ui.IHasComment;
import com.go.fish.ui.UIMgr;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;

public class AdapterExt extends BaseAdapter {

	Object extra;
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
		
		arr = makeArray(array);
//		if(useScroolListener){
			mListView.setOnScrollListener(new ScrollListenerImpl());
//		}
		listDatas = arr;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listView.setAdapter(this);
	}
	private ArrayList<IBaseData> makeArray(JSONArray array){
		ArrayList<IBaseData> arr = null;
		switch (layout_id){
		case R.layout.listitem_fnews:
			useScroolListener = true;
			arr = makeFNewsDataArray(array);
			break;
		case R.layout.listitem_friend_for_ui_zan:
		case R.layout.listitem_friend:
			arr = makePersonDataArray(array);
			break;
		case R.layout.listitem_comment:
			arr = makeCommentDataArray(array);
			break;
		case R.layout.listitem_zixun:
			arr = makeZixunDataArray(array);
			break;
		}
		return arr;
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
	public static ArrayList<IBaseData> makeCommentDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			CommentData newsData = CommentData.newInstance(jsonObject);
			arr.add(newsData);
		}
		return arr;
	}
	public static ArrayList<IBaseData> makeZixunDataArray(JSONArray array){
		ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.optJSONObject(i);
			FPlaceZixunData newsData = FPlaceZixunData.newInstance(jsonObject);
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
		case R.layout.listitem_fnews:
			item = onGetMyFNews(position, convertView, parent);
			break;
		case R.layout.listitem_friend:
			item = onGetFriend(position, convertView, parent);
			break;
		case R.layout.listitem_friend_for_ui_zan:
			item = onGetZanFriend(position, convertView, parent);
			break;
		case R.layout.listitem_comment:
			item = onGetComment(position, convertView, parent);
			break;
		case R.layout.listitem_zixun:
			item = onGetZixun(position, convertView, parent);
			break;
		default:
			break;
		}
        return item;
	}

	class ZixunViewHolder{
		TextView listitem_zixun_title,listitem_zixun_desc,listitem_zixun_zan,listitem_zixun_time,listitem_zixun_comment;
		ImageView listitem_fplace_icon;
	}
	private View onGetZixun(int position, View convertView, ViewGroup parent){
		ViewGroup item = null;
		IBaseData data = listDatas.get(position);
		FPlaceZixunData fPlaceZixunData = (FPlaceZixunData)data;
		ZixunViewHolder holder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new ZixunViewHolder();
			item.setTag(holder);
			holder.listitem_zixun_title = ((TextView)item.findViewById(R.id.listitem_zixun_title));
			holder.listitem_zixun_desc = ((TextView)item.findViewById(R.id.listitem_zixun_desc));
			holder.listitem_zixun_zan = ((TextView)item.findViewById(R.id.listitem_zixun_zan));
			holder.listitem_zixun_time = (TextView)item.findViewById(R.id.listitem_zixun_time);
			holder.listitem_zixun_comment = (TextView)item.findViewById(R.id.listitem_zixun_comment);
			holder.listitem_fplace_icon = (ImageView)item.findViewById(R.id.listitem_fplace_icon);
		} else {
			item = (ViewGroup)convertView;
			holder = (ZixunViewHolder)convertView.getTag();
		}
		ViewHelper.load(holder.listitem_fplace_icon, fPlaceZixunData.imgUrl, true, false);
		holder.listitem_zixun_title.setText(fPlaceZixunData.title);
		holder.listitem_zixun_desc.setTag(fPlaceZixunData.content);
		holder.listitem_zixun_time.setText(BaseUtils.getTimeShow(fPlaceZixunData.createdAt));//设置日期
		holder.listitem_zixun_comment.setText(fPlaceZixunData.comNum);//设置评论内容
		holder.listitem_zixun_zan.setText(fPlaceZixunData.attNum);//设置评论内容
		return item;
	}
	class CommentViewHolder{
		TextView comment_listitem_name,comment_listitem_time,comment_listitem_time_right_of,comment_listitem_text,lineView;
		ImageView comment_listitem_icon,comment_listitem_reply;
		ViewGroup comment_listitem_lower_comments;
	}
	private View onGetComment(int position, View convertView, ViewGroup parent){
		ViewGroup item = null;
		IBaseData data = listDatas.get(position);
		CommentData commentData = (CommentData)data;
		CommentViewHolder holder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new CommentViewHolder();
			item.setTag(holder);
			holder.comment_listitem_name = ((TextView)item.findViewById(R.id.comment_listitem_name));
			holder.comment_listitem_text = ((TextView)item.findViewById(R.id.comment_listitem_text));
			holder.comment_listitem_time = ((TextView)item.findViewById(R.id.comment_listitem_time));
			holder.comment_listitem_time_right_of = ((TextView)item.findViewById(R.id.comment_listitem_time_right_of));
			holder.lineView = (TextView)item.findViewById(R.id.line);
			holder.comment_listitem_icon = (ImageView)item.findViewById(R.id.comment_listitem_icon);
			holder.comment_listitem_reply = (ImageView)item.findViewById(R.id.comment_listitem_reply);
			holder.comment_listitem_lower_comments = (ViewGroup)item.findViewById(R.id.comment_listitem_lower_comments);
		} else {
			item = (ViewGroup)convertView;
			holder = (CommentViewHolder)convertView.getTag();
		}
		ViewHelper.load(holder.comment_listitem_icon, commentData.imgUrl, true, false);
		holder.comment_listitem_name.setText(commentData.uname);
		holder.comment_listitem_name.setTag(commentData.uid);
		holder.comment_listitem_time_right_of.setText(BaseUtils.getTimeShow(commentData.commentTime));//设置日期
		holder.comment_listitem_time_right_of.setVisibility(View.VISIBLE);//设置显示
		holder.comment_listitem_time.setVisibility(View.GONE);//隐藏右边的时间
		holder.comment_listitem_text.setText(commentData.text);//设置评论内容
		holder.comment_listitem_reply.setVisibility(View.VISIBLE);//设置评论图标可见
		holder.comment_listitem_reply.setTag(BaseUtils.joinString(commentData.uname,commentData.uid,getExtra(),-1));
		holder.comment_listitem_lower_comments.setTag(new IHasComment(){//回复成功之后，二级评论有所改变

			@Override
			public void onCommentIconClick(View view) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCommentReplyClick(View view) {
				// TODO Auto-generated method stub
				
			}});
		if(commentData.lowerComments != null && commentData.lowerComments.length() > 0){
			holder.comment_listitem_lower_comments.setVisibility(View.VISIBLE);
			holder.comment_listitem_lower_comments.removeAllViews();
			for(int i = 0; i < commentData.lowerComments.length();i++){
				JSONObject json = commentData.lowerComments.optJSONObject(i);
				ViewGroup replyContainer = (ViewGroup)mInflater.inflate(R.layout.listitem_comment_reply, null, false);
				ViewGroup replyClickView = (ViewGroup)replyContainer.getChildAt(0);
				TextView fromNameTv = ((TextView)replyClickView.getChildAt(0));
				TextView fromTextTv = ((TextView)replyClickView.getChildAt(1));

				String fromId = json.optString(Const.STA_FROM_ID);
				String fromName = json.optString(Const.STA_FROM_NAME);
				fromNameTv.setTag(fromId);
				String toId = "-1";
				String str = Const.DEFT_REPLY_ + json.optString(Const.STA_COMMENT_STR);//设置评论
				if(json.has(Const.STA_TO_ID)){//回复某人评论
					fromNameTv.setText(fromName);//设置联系人名字
					fromTextTv.setText(Const.DEFT_REPLY);//设置"回复"
					{//设置被回复人
						ViewGroup replyToContainer = (ViewGroup) mInflater.inflate(R.layout.listitem_comment_reply, null, false);
						ViewGroup replyTO = (ViewGroup)replyToContainer.getChildAt(0);
						replyTO.setClickable(false);
						replyTO.setBackgroundColor(0x00000000);
						TextView toNameTv = ((TextView)replyTO.getChildAt(0));
						TextView toTextTv = ((TextView)replyTO.getChildAt(1));
						replyToContainer.removeView(replyTO);
						{
							toId = json.optString(Const.STA_TO_ID);
							toNameTv.setTag(toId);
						}
						{
							String toName = json.optString(Const.STA_TO_NAME);
							toNameTv.setText(toName);
						}
						toTextTv.setText(str);
						replyClickView.addView(replyTO);//磊回复梦：下次带我
					}
				}else{//不是回复某人
					fromNameTv.setText(fromName);
					fromTextTv.setText(str);
				}
				replyClickView.setTag(BaseUtils.joinString(fromName, fromId, getExtra(),toId));
				holder.comment_listitem_lower_comments.addView(replyContainer);
			}

//			holder.comment_listitem_lower_comments
		}
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
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
	public void updateAdapter(ArrayList<IBaseData> arr){
		listDatas.addAll(arr);
		setheight(mListView);
		notifyDataSetChanged();
	}
	public void updateAdapter(JSONArray array){
		ArrayList<IBaseData> arr = makeArray(array);
		updateAdapter(arr);
	}
	class ZanFriendViewHolder {
		TextView nameView,fYearView,fTimesView,lineView;
		ImageView userIcon;
	}
	private View onGetZanFriend(int position, View convertView, ViewGroup parent){
		ViewGroup item = null;
		IBaseData data = listDatas.get(position);
		PersonData personData = (PersonData)data;
		ZanFriendViewHolder holder = null;
		if (convertView == null) {
			item = (ViewGroup)mInflater.inflate(layout_id, parent, false);
			holder = new ZanFriendViewHolder();
			item.setTag(holder);
			holder.nameView = ((TextView)item.findViewById(R.id.listitem_friend_name));
			holder.fYearView = ((TextView)item.findViewById(R.id.listitem_friend_fyear));
			holder.fTimesView = ((TextView)item.findViewById(R.id.listitem_friend_ftimes));
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
			holder.lineView = (TextView)item.findViewById(R.id.line1);
		} else {
			item = (ViewGroup)convertView;
			holder = (ZanFriendViewHolder)convertView.getTag();
		}
		ViewHelper.load(holder.userIcon, personData.photoUrl, true, false);
		holder.nameView.setText(personData.userName);
		holder.fYearView.setText(""+personData.fYears);
		holder.fTimesView.setText(personData.fTimes + "次/月");
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
		}
		return item;
	}

	class FriendViewHolder {
		TextView nameView,farView,fYearView,fTimesView,listitem_friend_last_info,lineView;
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
			holder.listitem_friend_last_info = ((TextView)item.findViewById(R.id.listitem_friend_last_info));
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
			holder.lineView = (TextView)item.findViewById(R.id.line2);
		} else {
			item = (ViewGroup)convertView;
			holder = (FriendViewHolder)convertView.getTag();
		}
		ViewHelper.load(holder.userIcon, personData.photoUrl, true, false);
		holder.listitem_friend_last_info.setVisibility(View.GONE);
		holder.nameView.setText(personData.userName);
		holder.farView.setText(personData.far);
		holder.fYearView.setText(""+personData.fYears);
		holder.fTimesView.setText(personData.fTimes + "次/月");
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
		}
		return item;
	}
	class FNewsViewHolder {
		TextView nameView,fYearView,fTimesView,textView,listitem_fnews_comment_count;
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
			item = mInflater.inflate(layout_id, parent, false);
			holder = new FNewsViewHolder();
			item.setTag(holder);
			View fLayout = item.findViewById(R.id.listitem_friend_layout);
			holder.listitem_friend_layout = fLayout;
			holder.nameView = (TextView)fLayout.findViewById(R.id.listitem_friend_name);
			holder.fYearView = (TextView)fLayout.findViewById(R.id.listitem_friend_fyear);
			holder.fTimesView = (TextView)fLayout.findViewById(R.id.listitem_friend_ftimes);
			
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
//					holder.childViews[i].setBackgroundColor((int)new Random().nextLong());
//					holder.childViews[i].setPadding(p,p,p,p);
					holder.childViews[i].setTag(newsData.netPicUrl[i]);
					holder.childViews[i].setOnClickListener(listener);
//					ViewHelper.load((ImageView)((ViewGroup)holder.childViews[i]).getChildAt(0), (String)holder.childViews[i].getTag(), true,false);
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
//			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//				Toast.makeText(view.getContext(), "onScrollStateChanged", Toast.LENGTH_LONG);
//				update(mFirstVisibleItem, mVisibleItemCount);
//			} else if(scrollState == OnScrollListener.){
//				cancelAllTasks();
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
