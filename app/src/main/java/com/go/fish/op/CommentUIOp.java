package com.go.fish.op;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.CommentData;
import com.go.fish.data.load.CommentDataLoader;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.IHasComment;
import com.go.fish.ui.pics.GalleryUtils;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.AutoLayoutViewGroup;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;

public class CommentUIOp extends Op{
	
	public static void onCreateCommentList(final Activity activity) {
		Intent intent = activity.getIntent();
		String fieldId = null;
		try {
			fieldId = intent.getStringExtra(Const.STA_ID);
		} catch (Exception e) {
			e.printStackTrace();
			fieldId = String.valueOf(intent.getIntExtra(Const.STA_ID,-1));
		}
		if (fieldId == null)
			activity.finish();
		ListView listView = (ListView) activity.findViewById(R.id.comment_list);
		listView.setDividerHeight(0);
		listView.setTag(fieldId);
//		View textView = findViewById(activity,R.id.comment_list_reply_text);//设置评论输入框默认回复钓场，并设置回复id数据等
//		CommentData commentData = new CommentData();
//		commentData.id = fieldId;
//		textView.setTag(commentData);//屏蔽为了解决
		OnBaseDataClickListener listener = null;
//		listener = new OnBaseDataClickListener() {
//			@Override
//			public void onItemClick(View view, IBaseData data) {
//				data.OnClick(activity, null, view);
//			}
//		};
		AdapterExt adapter = AdapterExt.newInstance(listView, listener, new JSONArray(), R.layout.listitem_comment);
		CommentDataLoader.loadList(activity, adapter, fieldId,true);
	}

	static class CommentViewHolder{
		TextView comment_listitem_name,comment_listitem_time,comment_listitem_time_right_of,comment_listitem_text,lineView;
		ImageView comment_listitem_icon,comment_listitem_reply;
		ViewGroup comment_listitem_lower_comments;
	}
	public static View onGetComment(final Activity activity,LayoutInflater mInflater,int layout_id,final int position, ArrayList<IBaseData> listDatas, View convertView, ViewGroup parent){//评论数据
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
		ViewHelper.load(holder.comment_listitem_icon, commentData.imgUrl);
		holder.comment_listitem_name.setText(commentData.memberName);
		holder.comment_listitem_name.setTag(commentData.memberId);
		holder.comment_listitem_time_right_of.setText(BaseUtils.getTimeShow(commentData.commentTime));//设置日期
		holder.comment_listitem_time_right_of.setVisibility(View.VISIBLE);//设置显示
		holder.comment_listitem_time.setVisibility(View.GONE);//隐藏右边的时间
		holder.comment_listitem_text.setText(commentData.commentStr);//设置评论内容
		holder.comment_listitem_text.setTag(commentData);//设置评论图标tag数据，以供点击事情数据处理
		holder.comment_listitem_reply.setVisibility(View.VISIBLE);//设置评论图标可见
		holder.comment_listitem_reply.setTag(commentData);//设置评论图标tag数据，以供点击事情数据处理
		holder.comment_listitem_lower_comments.setTag(new IHasComment(){//回复成功之后，二级评论有所改变
			@Override
			public void onCommentReplyClick(View view) {
				// TODO Auto-generated method stub
				
			}});
		if(holder.comment_listitem_lower_comments != null){//清除原来item保存的数据
			holder.comment_listitem_lower_comments.removeAllViews();
			holder.comment_listitem_lower_comments.setVisibility(View.GONE);
		}
		if(commentData.lowerComments != null && commentData.lowerComments.size() > 0){
			holder.comment_listitem_lower_comments.setVisibility(View.VISIBLE);
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
		}else{//
			
		}
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
		}
		return item;
	}
}
