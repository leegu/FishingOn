package com.go.fish.op;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.data.load.PersonDataLoader;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.IBaseData;
import com.go.fish.view.ViewHelper;

public class PersonUIOp extends Op{

	public static void onCreatePrasizeList(final Activity activity,String pariseListType,final TextView callbackView) {
		final ListView list = (ListView) findViewById(activity,R.id.zan_listview);
		list.setDividerHeight(0);
		final AdapterExt ada = AdapterExt.newInstance(list, getDefault(activity),new JSONArray(), R.layout.listitem_person_2_rows);
		int fieldId = activity.getIntent().getIntExtra(Const.STA_ID, -1);
		PersonDataLoader.loadPraiseList(activity, callbackView, ada, fieldId);
	}
	
	static class FriendViewHolder {
		TextView nameView,farView,fYearView,fTimesView,listitem_friend_last_info,lineView;
		ImageView userIcon;
		View user_detail;
		ViewGroup listitem_friend_tags;
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
			holder.listitem_friend_tags = (ViewGroup)item.findViewById(R.id.listitem_friend_tags);
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
		holder.fTimesView.setText(personData.fTimes);
		if(holder.listitem_friend_tags != null){
			holder.listitem_friend_tags.setVisibility(View.VISIBLE);
			String[] tags = personData.tagArray;
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
//		if(position == listDatas.size() - 1){
//			holder.lineView.setVisibility(View.INVISIBLE);
//		}
		return item;
	}
	
}
