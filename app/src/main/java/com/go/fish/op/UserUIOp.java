package com.go.fish.op;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.barcode.BarcodeUI;
import com.go.fish.data.PersonData;
import com.go.fish.data.load.PersonDataLoader;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.SearchUI;
import com.go.fish.ui.UICode;
import com.go.fish.ui.UIMgr;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.MapUtil;
import com.go.fish.util.MapUtil.LocationData;
import com.go.fish.util.MapUtil.OnGetLocationListener;
import com.go.fish.util.MessageHandler;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.AdapterExt.OnBaseDataClickListener;
import com.go.fish.view.IBaseData;
import com.go.fish.view.MyListitemAdapter;
import com.go.fish.view.ViewHelper;

public class UserUIOp extends Op{

	public static void onCreateNearFriend(final Activity activity) {// 创建附近钓友

		MapUtil.getLocation(activity, new OnGetLocationListener() {
			@Override
			public void onGetLocation(LocationData data) {
				User.self().userInfo.lng = data.lng;
				User.self().userInfo.lat = data.lat;
				final ListView list = (ListView) findViewById(activity,R.id.ui_near_f_friends_listview);
				list.setDividerHeight(0);
				OnBaseDataClickListener listener = new OnBaseDataClickListener(){
					@Override
					public void onItemClick(View view, IBaseData data) {
						data.OnClick(activity, null, view);
					}};
				final AdapterExt adapter = AdapterExt.newInstance(list, listener, new JSONArray(), R.layout.listitem_person_3_rows);
				PersonDataLoader.getAroundMember(activity, adapter);
			}
		});
	}
	
	public static void onCreateUserCare(final Activity activity) {// 创建我的关注 钓场 钓友
		ViewGroup vg = (ViewGroup) findViewById(activity,R.id.ui_my_care_list_root);
		{// 钓场、渔具
			ListView fPlaceList = new ListView(activity);
			vg.addView(fPlaceList);
			fPlaceList.setId(R.id.ui_f_care_list);
//			ArrayList<FieldData> fPlaceArr = DataMgr.makeFPlaceDatas(R.layout.listitem_field, new JSONArray());
//			FPlaceListAdapter.setAdapter(BaseUI.this.getApplicationContext(),fPlaceList,fPlaceArr, FieldUIOp.FLAG_CARE_RESULT);
			// 网络数据抓取,进行更新
			FieldUIOp.onCreateCareFieldView(activity, vg,R.layout.listitem_field, null);
			FieldUIOp.onShowCareFieldList(vg,R.layout.listitem_field);
		}

		{// 钓播
			final ListView fNewsList = new ListView(activity);
			fNewsList.setVisibility(View.GONE);
			vg.addView(fNewsList);
			ListView list = fNewsList;
			list.setDividerHeight(0);
			OnBaseDataClickListener listener = new OnBaseDataClickListener(){
				@Override
				public void onItemClick(View view, IBaseData data) {
					data.OnClick(activity, null, view);
				}};
			final AdapterExt adapter = AdapterExt.newInstance(list, listener, new JSONArray(), R.layout.listitem_person_3_rows);
			
			PersonDataLoader.getAroundMember(activity, adapter);
		}
	}
	
	public static void onShowMyView(final ViewGroup view){
//		updateMyView(getView());
		//更新关注数量，头像信息，昵称
		JSONObject jsonObject = new JSONObject();
		NetTool.data().http(new NetTool.RequestListener() {
			@Override
			public void onEnd(byte[] bytes) {
				JSONObject response = toJSONObject(bytes);
				if (response != null ){
					if(isRight(response)) {
						JSONObject data = response.optJSONObject(Const.STA_DATA);
						User.self().userInfo = PersonData.updatePerson(User.self().userInfo,data.optJSONObject(Const.STA_MEMBER));
						LocalMgr.self().saveUserInfo(Const.K_photo_url,User.self().userInfo.photoUrl);
						updateMyView(view);
					}
				}
			}
		},jsonObject,UrlUtils.self().getSettingData());
		updateMyView(view);
	}
	
	private static void updateMyView(View view){
		{//更新用户名,头像，手机号
			TextView userPhoneNumber = (TextView)view.findViewById(R.id.userPhoneNumber);
			if(!TextUtils.isEmpty(User.self().userInfo.mobileNum)){
				userPhoneNumber.setText(BaseUtils.formatPhoneNum(User.self().userInfo.mobileNum));
			}
			
			TextView userName = (TextView)view.findViewById(R.id.userName);
			if(!TextUtils.isEmpty(User.self().userInfo.userName)){
				userName.setText(User.self().userInfo.userName);
			}else{
				userName.setText(User.self().userInfo.id);
			}
			
			if(!TextUtils.isEmpty(User.self().userInfo.photoUrl)){
				ImageView userIcon = (ImageView)view.findViewById(R.id.userIcon);
				ViewHelper.load(userIcon, UrlUtils.self().getNetUrl(User.self().userInfo.photoUrl), true,false);
			}
		}
	}
	
	public static void onCreateMyView(final Activity activity,ViewGroup view) {
		{
			updateMyView(view);
			final ListView list1 = (ListView)view.findViewById(R.id.ui_f_my_listview1);
			list1.setDividerHeight(0);
			MyListitemAdapter.fillToListView(list1,R.array.hmy_listview1, R.array.hmy_listview1_icons);
			MessageHandler.sendMessage(new MessageHandler.MessageListener() {
				@Override
				public MessageHandler.MessageListener init(Object args) {
					return this;
				}

				@Override
				public void onExecute() {
//					MyListitemAdapter listitemAdapter = (MyListitemAdapter) list1.getAdapter();
//					((MyListitemData) listitemAdapter.getItem(1)).bedgerNumber = 45;
//					((MyListitemData) listitemAdapter.getItem(1)).subLabel = "钓场~播况~GO";
//					((MyListitemData) listitemAdapter.getItem(0)).bedgerNumber = 15;
//					((MyListitemData) listitemAdapter.getItem(0)).subLabel = "gaga";
				}
			});
			list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// 我钓播况 我的关注 附近钓场 附近钓友 扫一扫
					switch (position) {
						case 0:{//我的播况
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_podcast_person);
							i.putExtra(Const.STA_MOBILE, User.self().userInfo.mobileNum);
							i.putExtra(Const.PRI_HIDE_CARE, true);
							UIMgr.showActivity(activity,i,BaseUI.class.getName());
							break;
						}
						case 1://我的关注
							UIMgr.showActivity(activity,R.layout.ui_my_care);
							break;
						case 2:{//附近钓场
							Bundle newBundle = new Bundle();
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_search_list);
							newBundle.putInt(Const.PRI_FPLACE_RESULT_TYPE, FieldUIOp.FLAG_NEAR_RESULT);
							Intent intent = new Intent();
							intent.putExtras(newBundle);
							UIMgr.showActivity(activity, intent, SearchUI.class.getName());
//							UIMgr.showActivity(activity,R.layout.ui_near_fplace);
							break;
						}
						case 3://附近钓友
							UIMgr.showActivity(activity,R.layout.ui_near_friends);
							break;
						case 4:{
							Intent i = new Intent();
							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
//							i.putExtra(Const.PRI_TO_QR_CONTENT,"我是一个兵");
							i.setClassName(activity, BarcodeUI.class.getName());
							UIMgr.showActivity(activity,i,UICode.RequestCode.REQUEST_BARCODE);
							break;
						}
					}
				}
			});
		}
		{
			ListView list2 = (ListView)view.findViewById(R.id.ui_f_my_listview2);
			list2.setDividerHeight(0);
			MyListitemAdapter.fillToListView(list2, R.array.hmy_listview2, R.array.hmy_listview2_icons);
			list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					问题反馈  清空缓存  关于我们
					switch (position) {
						case 0://问题反馈
							UIMgr.showActivity(activity,R.layout.ui_advice);
							break;
						case 1://清除缓存
							ViewHelper.showGlobalWaiting(activity, null,"清理中");
							new Thread(){
								public void run() {
									LocalMgr.self().clearCache();
									activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ViewHelper.closeGlobalWaiting();
										}
									});
								};
							}.start();
							break;
						case 2:
							UIMgr.showActivity(activity,R.layout.ui_about);
//							Intent i = new Intent();
//							i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_barcode);
//							i.putExtra(Const.PRI_TO_QR_CONTENT,"13245698756");
//							UIMgr.showActivity(activity,i,BarcodeUI.class.getName());
							break;
					}
				}
			});
		}
	}
}
