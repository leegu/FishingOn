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
	
	public static void onCreateCommentPublish(Activity activity) {// 创建钓播发布页面
		ViewGroup root = (ViewGroup) findViewById(activity,R.id.comment_pics);
		AutoLayoutViewGroup autoLayout = new AutoLayoutViewGroup(activity);
		addImageView(activity,autoLayout, null, R.drawable.p);
		root.addView(autoLayout, -1, -2);
		// final EditText et = (EditText) findViewById(R.id.comment_text);
		// TextView t = (TextView)findViewById(R.id.comment_has_len);
		// et.setSelection(et.getText().length());
		// final int maxCount =
		// getResources().getInteger(R.integer.comment_max_count);
		// t.setText(String.format(getString(R.string.input_text_count),maxCount
		// - et.getText().length() ));
		// IMETools.showIME(et);
		// et.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// TextView t = (TextView) findViewById(R.id.comment_has_len);
		// int lessCount = 0;
		// if (et.getText().length() >= maxCount) {
		// et.removeTextChangedListener(this);
		// et.setText(et.getText().subSequence(0, maxCount));
		// et.setSelection(et.getText().length());
		// et.addTextChangedListener(this);
		// }
		// t.setText(String.format(getString(R.string.input_text_count),
		// maxCount - et.getText().length()));
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// }) ;
	}
	
	static int PADDING = 20;
	private static void addImageView(final Activity activity,final ViewGroup parent, final String filePath,
			int resId) {
		ImageView t = new ImageView(activity);
		t.setScaleType(ImageView.ScaleType.CENTER_CROP);
		t.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// ViewHelper.showToast(BaseUI.this, "show a");
				GalleryUtils.self().pick(activity,
						new GalleryUtils.GalleryCallback() {
							@Override
							public void onCompleted(String[] filePaths,
									Bitmap bitmap0) {
								if (!(v.getTag() instanceof Integer)) {
									int w = (activity.getResources().getDisplayMetrics().widthPixels - PADDING * 4) / 3;
									Bitmap bitmap = LocalMgr.self()
											.getSuitBimap(filePaths[0], w, w);
									((ImageView) v)
											.setImageDrawable(new BitmapDrawable(
													bitmap));
									// bitmap.recycle();
									((ImageView) v).setTag(filePath);
								} else if (filePaths != null) {
									addImageView(activity,parent, filePaths[0], -1);
								}
							}
						}, null, false);
			}
		});
		t.setPadding(PADDING, PADDING, PADDING, PADDING);
		t.setBackgroundResource(R.drawable.base_background);
		int w = (activity.getResources().getDisplayMetrics().widthPixels - PADDING * 6) / 3;
		if (filePath == null) {
			t.setImageResource(resId);
			t.setTag(resId);
		} else {
			Bitmap bitmap = LocalMgr.self().getSuitBimap(filePath, w, w);
			t.setImageDrawable(new BitmapDrawable(bitmap));
			t.setTag(filePath);
		}
		parent.addView(t, 0, new ViewGroup.LayoutParams(w, w));
	}
	
	public static void onCreateCommentList(final Activity activity) {
		Intent intent = activity.getIntent();
		int fieldId = intent.getIntExtra(Const.STA_ID, -1);
		if (fieldId < 1)
			activity.finish();
		ListView listView = (ListView) activity.findViewById(R.id.comment_list);
		listView.setDividerHeight(0);
		listView.setTag(fieldId);
		OnBaseDataClickListener listener = null;
//		listener = new OnBaseDataClickListener() {
//			@Override
//			public void onItemClick(View view, IBaseData data) {
//				data.OnClick(activity, null, view);
//			}
//		};
		AdapterExt adapter = AdapterExt.newInstance(listView, listener, new JSONArray(), R.layout.listitem_comment);
		CommentDataLoader.loadList(activity, adapter, String.valueOf(fieldId),true);
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
			public void onCommentReplyClick(View view) {
				// TODO Auto-generated method stub
				
			}});
		if(commentData.lowerComments != null){
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
		}else{
		}
		if(position == listDatas.size() - 1){
			holder.lineView.setVisibility(View.INVISIBLE);
		}
		return item;
	}
}
