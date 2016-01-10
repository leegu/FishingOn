package com.go.fish.op;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;

public class PersonUIOp {
	
	static class ZanFriendViewHolder {
		TextView nameView,fYearView,fTimesView,lineView;
		ImageView userIcon;
		View user_detail;
	}
	public static View onGetZanFriend(final Activity activity,LayoutInflater mInflater,int layout_id,final int position, boolean mHideCare,final ArrayList<IBaseData> listDatas, View convertView, ViewGroup parent){
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

	static class FriendViewHolder {
		TextView nameView,farView,fYearView,fTimesView,listitem_friend_last_info,lineView;
		ImageView userIcon;
		View user_detail;
	}
	public static View onGetFriend(final Activity activity,LayoutInflater mInflater,int layout_id,final int position, boolean mHideCare,final ArrayList<IBaseData> listDatas, View convertView, ViewGroup parent){
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
	
}
