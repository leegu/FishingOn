package com.go.fish.view;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
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
import com.go.fish.util.UrlUtils;

public class AdapterExt extends BaseAdapter {

	Object extra;
	public int layout_id ;
	private LayoutInflater mInflater;
	Context context = null;
	ListView mListView = null;
	ArrayList<IBaseData> listDatas = null;
	
	public static final int FLAG_MY_NEWS = 1;
	public static final int FLAG_NEWS = 2;
	public int mFlag = FLAG_NEWS;
	public boolean mHideCare = false;
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
		case R.layout.listitem_friend_2_rows:
		case R.layout.listitem_friend_3_rows:
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
		if(array != null){
			int count = array.length();
//			count = count > 0 ? 50 : 0;
			for(int i = 0; i < count; i++) {
				JSONObject jsonObject = array.optJSONObject(i);
//				JSONObject jsonObject = array.optJSONObject(0);
				PersonData newsData = PersonData.newInstance(jsonObject);
				arr.add(newsData);
			}
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
		case R.layout.listitem_fnews://钓播
			item = onGetMyFNews(position, convertView, parent);
			break;
		case R.layout.listitem_friend_3_rows:
			item = onGetFriend(position, convertView, parent);
			break;
		case R.layout.listitem_friend_2_rows:
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
	private View onGetComment(int position, View convertView, ViewGroup parent){//评论数据
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
		if(!TextUtils.isEmpty(commentData.imgUrl)){
			ViewHelper.load(holder.comment_listitem_icon, commentData.imgUrl, true, false);
		}
		holder.comment_listitem_name.setText(commentData.memberName);
		holder.comment_listitem_name.setTag(commentData.memberId);
		holder.comment_listitem_time_right_of.setText(BaseUtils.getTimeShow(commentData.commentTime));//设置日期
		holder.comment_listitem_time_right_of.setVisibility(View.VISIBLE);//设置显示
		holder.comment_listitem_time.setVisibility(View.GONE);//隐藏右边的时间
		holder.comment_listitem_text.setText(commentData.commentStr);//设置评论内容
		holder.comment_listitem_reply.setVisibility(View.VISIBLE);//设置评论图标可见
		holder.comment_listitem_reply.setTag(commentData);//设置评论图标tag数据，以供点击事情数据处理
		holder.comment_listitem_lower_comments.setTag(new IHasComment(){//回复成功之后，二级评论有所改变

			@Override
			public void onCommentIconClick(View view) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCommentReplyClick(View view) {
				// TODO Auto-generated method stub
				
			}});
		if(commentData.lowerComments != null && commentData.lowerComments.size() > 0){
			holder.comment_listitem_lower_comments.setVisibility(View.VISIBLE);
			holder.comment_listitem_lower_comments.removeAllViews();
			for(int i = 0; i < commentData.lowerComments.size();i++){
				CommentData cData = commentData.lowerComments.get(i);
				ViewGroup replyContainer = (ViewGroup)mInflater.inflate(R.layout.listitem_comment_reply, null, false);
				ViewGroup replyClickView = (ViewGroup)replyContainer.getChildAt(0);
				TextView fromNameTv = ((TextView)replyClickView.getChildAt(0));
				TextView fromTextTv = ((TextView)replyClickView.getChildAt(1));

				String fromId = cData.fromId;
				String fromName = cData.fromName;
				fromNameTv.setTag(fromId);
				String toId = "-1";
				String str = Const.DEFT_REPLY_ + cData.commentStr;//设置评论
				if(!TextUtils.isEmpty(cData.toId)){//回复某人评论
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
							toId = cData.toId;
							toNameTv.setTag(toId);
						}
						{
							String toName = cData.toName;
							toNameTv.setText(toName);
						}
						toTextTv.setText(str);
						replyClickView.addView(replyTO);//磊回复梦：下次带我
					}
				}else{//不是回复某人
					fromNameTv.setText(fromName);
					fromTextTv.setText(str);
				}
				replyClickView.setTag(cData);//使用当前条评论作为tag
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
		listDatas.addAll(0,arr);
		setheight(mListView);
		notifyDataSetChanged();
	}
	public void updateAdapter(){
		setheight(mListView);
		notifyDataSetChanged();
	}
	public void updateAdapter(IBaseData data){
		listDatas.add(0, data);
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
		View user_detail;
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
			holder.user_detail = item.findViewById(R.id.user_detail);
			
			holder.nameView = ((TextView)item.findViewById(R.id.listitem_friend_name));
			holder.fYearView = ((TextView)item.findViewById(R.id.listitem_friend_fyear));
			holder.fTimesView = ((TextView)item.findViewById(R.id.listitem_friend_ftimes));
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
			holder.lineView = (TextView)item.findViewById(R.id.line1);
			if(mHideCare){
				item.findViewById(R.id.care_btn).setVisibility(View.GONE);
			}
		} else {
			item = (ViewGroup)convertView;
			holder = (ZanFriendViewHolder)convertView.getTag();
		}
		if(!TextUtils.isEmpty(personData.photoUrl)){
			ViewHelper.load(holder.userIcon, UrlUtils.self().getNetUrl(personData.photoUrl), true, false);
		}
		holder.user_detail.setTag(personData);
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
		View user_detail;
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
			holder.user_detail = item.findViewById(R.id.user_detail);
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
		if(!TextUtils.isEmpty(personData.photoUrl)){
			ViewHelper.load(holder.userIcon, UrlUtils.self().getNetUrl(personData.photoUrl), true, false);
		}
		holder.user_detail.setTag(personData);
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
		TextView nameView,fYearView,fTimesView,textView,listitem_fnews_comment_count,publish_time;
		ImageView userIcon;
		View listitem_friend_layout,user_detail;
		ViewGroup listitem_friend_tags;
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
			View fLayout = item.findViewById(R.id.listitem_friend_person_layout);
			holder.user_detail = item.findViewById(R.id.user_detail);
			holder.listitem_friend_layout = fLayout;
			holder.nameView = (TextView)fLayout.findViewById(R.id.listitem_friend_name);
			holder.fYearView = (TextView)fLayout.findViewById(R.id.listitem_friend_fyear);
			holder.fTimesView = (TextView)fLayout.findViewById(R.id.listitem_friend_ftimes);
			holder.listitem_friend_tags = (ViewGroup)fLayout.findViewById(R.id.listitem_friend_tags);
			
			holder.textView = (TextView)item.findViewById(R.id.textView);
			holder.listitem_fnews_comment_count = (TextView)item.findViewById(R.id.listitem_fnews_comment_count);
			holder.publish_time = (TextView)item.findViewById(R.id.publish_time);
			holder.userIcon = (ImageView)item.findViewById(R.id.user_icon);
//			if(newsData.netPicUrl != null){//当有钓播图片时
//				int size = newsData.netPicUrl.length;
//				ViewStub vs = (ViewStub)item.findViewById(R.id.fnews_image_contianer_view_stub);
//				if(vs != null){//还没有初始化需要inflate
//					holder.mHAutoAlign = (HAutoAlign)((ViewGroup)vs.inflate()).getChildAt(0);
//				}else{
//					holder.mHAutoAlign = (HAutoAlign)item.findViewById(R.id.h_image_view_container);
//				}
//				holder.childViews = new LinearLayout[size];
//				OnClickListener listener = new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent();
//						intent.putExtra(Const.PRI_EXTRA_IMAGE_INDEX, position);
//						intent.putExtra(Const.PRI_EXTRA_IMAGE_URLS, newsData.netPicUrl);
//						UIMgr.showActivity((Activity)context,intent,ImageViewUI.class.getName());
//					}
//				};
//				for(int i = 0; i < size; i++){
//					holder.childViews[i] = mInflater.inflate(R.layout.h_image_view_item, null);
////					holder.childViews[i].setBackgroundColor((int)new Random().nextLong());
////					holder.childViews[i].setPadding(p,p,p,p);
//					String url = newsData.netPicUrl[i];
////					url = "http://f.hiphotos.baidu.com/image/h%3D200/sign=129e451332fae6cd13b4ac613fb20f9e/1e30e924b899a901c9bdff121a950a7b0208f50e.jpg";
//					holder.childViews[i].setTag(url);
//					holder.childViews[i].setOnClickListener(listener);
////					ViewHelper.load((ImageView)((ViewGroup)holder.childViews[i]).getChildAt(0), (String)holder.childViews[i].getTag(), true,false);
//				}
//				holder.mHAutoAlign.fillChilds(holder.childViews);
//			}
		} else {
			holder = (FNewsViewHolder)convertView.getTag();
			item = convertView;
		}

		if( position != 0 &&  mFlag == AdapterExt.FLAG_MY_NEWS){//不需要当前用户信息
			holder.listitem_friend_layout.setVisibility(View.GONE);
			holder.publish_time.setVisibility(View.VISIBLE);
			String showTime = BaseUtils.getTimeShow(newsData.publishTime);
			if(!TextUtils.isEmpty(showTime)){
				holder.publish_time.setText(showTime);
			}else{
				
			}
		}else{//需要用户信息
			holder.user_detail.setTag(newsData.authorData);
			if(holder.listitem_friend_tags != null){
				holder.listitem_friend_tags.setVisibility(View.VISIBLE);
				String[] tags = newsData.authorData.aiHaos;
				for(int i = 0; i < holder.listitem_friend_tags.getChildCount(); i++){//只对前三个进行显示
					TextView tv = (TextView)holder.listitem_friend_tags.getChildAt(i);
					if(i < tags.length){
						tv.setVisibility(View.VISIBLE);
						tv.setBackgroundColor(BaseUtils.getTagBg(tags[i]));
						tv.setText(tags[i]);
					}else{
						tv.setVisibility(View.GONE);
					}
				}
			}
			
			if(!TextUtils.isEmpty(newsData.authorData.photoUrl)){
				String url = newsData.authorData.photoUrl;
//				url = "http://img2.baobao88.com/bbfile/allimg/101021/10930462436-14.gif";
				ViewHelper.load(holder.userIcon,  UrlUtils.self().getNetUrl(url),true,false);
			}
			holder.publish_time.setVisibility(View.GONE);
			holder.nameView.setText(newsData.authorData.userName + "--" + position);
			holder.fYearView.setText(""+newsData.authorData.fYears);
			holder.fTimesView.setText(""+newsData.authorData.fTimes);
		}
		if(holder.mHAutoAlign != null) holder.mHAutoAlign.scrollTo(0, 0);
		holder.textView.setText(newsData.content);
		if(newsData.commentCount > 0){holder.listitem_fnews_comment_count.setText(""+newsData.commentCount);}else{holder.listitem_fnews_comment_count.setVisibility(View.GONE);}
//		final FNewsViewHolder f_holder = holder;
//		if(newsData.netPicUrl != null && f_holder.childViews != null){
//			item.postDelayed(new Runnable() {
//				@Override
//				public void run() {
////					if(position >= mFirstVisibleItem && position <= mFirstVisibleItem + mVisibleItemCount){
////						for(int i = 0; i < f_holder.childViews.length; i++){
////							ViewHelper.load(f_holder.childViews[i], (String)f_holder.childViews[i].getTag(), true);
////						}
////					}
//				}
//			}, 1000);
//		}
		return item;
	}
	
	
	private class ScrollListenerImpl implements OnScrollListener {
		boolean isLastRow = false;    
		int firstVisibleItem, visibleItemCount;
        @Override    
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {    
            //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。    
            //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）    
            //visibleItemCount：当前能看见的列表项个数（小半个也算）    
            //totalItemCount：列表项共数    
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
            if (/*isLastRow && */scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {    
                //加载元素    
            	update(firstVisibleItem, visibleItemCount);
                isLastRow = false;    
            }    
        }    
		
		private void update(int firstVisibleItem,int visibleItemCount){
			if(layout_id == R.layout.listitem_fnews){
				for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
					FNewsData newsData = (FNewsData)listDatas.get(i);
					
				}
			}
			System.out.print(firstVisibleItem  +" ------ " + visibleItemCount);
		}
		
	}
}
