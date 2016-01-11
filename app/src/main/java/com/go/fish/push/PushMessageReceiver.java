package com.go.fish.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PushMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {try{/*
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			byte[] dataBase = bundle.getByteArray("payload");

			boolean isOnClick = false;
			if (dataBase != null) {
				String data = new String(dataBase);
				if (TextUtils.isEmpty(data)) {// payload数据为空时，认为是消息中心点击进入的，需要触发click事件
					isOnClick = true;
				} else {// 获得的payload不为空时触发receive逻辑
					isOnClick = false;
					boolean createNotifcation = true;// title、content、payload三者都存在的时候才创建消息通知
					JSONObject jsobj = new JSONObject(data);
					String title = jsobj.optString("title");
					if (TextUtils.isEmpty(title)) {
						createNotifcation = false;
						title = getApplicationName(context);
					}
					String content = jsobj.optString("content");
					if (TextUtils.isEmpty(content)) {
						content = data;
						createNotifcation = false;
					}
					// appid要考虑开机自启动的时候，程序没有初始化的情况，为空的时候现在用默认的PDR作为
					String appid = jsobj.optString("appid");
					String payload = jsobj.optString( "payload");
					if (TextUtils.isEmpty(payload)) {
						payload = data;
						createNotifcation = false;
					}
					
//					PushMessage _pushMessage = new PushMessage(title, content,appid);
//					_pushMessage.mPayload = payload;
//					Log.d("PushMessageReceiver", "Got Payload:" + data);
//					boolean needPush = AbsPushService.getAutoNotification(context, appid, GTPushService.ID);
//					if (needPush && createNotifcation) {
//						_pushMessage.setNotificationID();
//						APSFeatureImpl.sendCreateNotificationBroadcast(context, appid, _pushMessage);
//					}else if (!APSFeatureImpl.execScript(context,"receive", _pushMessage.toJSON())) {// 添加receive执行队列
//						APSFeatureImpl.addNeedExecReceiveMessage(context,_pushMessage);
//					}
//					APSFeatureImpl.addPushMessage(context,appid, _pushMessage);
					
				}
			} else {
				isOnClick = true;
			}
			if (isOnClick) {// 触发click事件

			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
//			String clientid = bundle.getString("clientid");
//			SharedPreferences _sp = context.getSharedPreferences(AbsPushService.CLIENTID + GTPushService.ID, Context.MODE_PRIVATE);
//			Editor ed = _sp.edit();
//			ed.putString(AbsPushService.PUSH_CLIENT_ID_NAME, clientid);
//			ed.commit();
//			Logger.e("PushMessageReceiver", "CLIENTID=" + clientid);
			break;
		default:
			break;
		}
	*/}catch(Exception e){}}

	public String getApplicationName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}
}
