package com.go.fish.data;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.go.fish.R;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.UIMgr;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.MapUtil;
import com.go.fish.util.UrlUtils;
import com.go.fish.view.IBaseData;

public class PersonData implements IBaseData {

    public String id;
    public  String photoUrl;
    public String userName;
    public String mobileNum;
    public String[] tagArray = null;
    public String far = null;
    public double farLong = 0;
    public String address = null;
    public String tag = null;
    public String fYears = null;
    public String fTimes = null;
    public double lng = 0;
    public double lat = 0;
    
    public PersonData updateData(JSONObject member){
    	PersonData personData = this;
    	 if(member != null){
 	        personData.id = member.optString(Const.STA_MEMBER_ID,personData.id);
 	        personData.photoUrl = UrlUtils.self().getNetUrl(member.optString(Const.STA_IMGURL,personData.photoUrl));
 	        personData.userName = member.optString(Const.STA_NAME,personData.userName);
 	//        personData.userName = "丰";
 	        personData.fYears = member.optString(Const.STA_FISH_YEAR,personData.fYears);
 	        personData.mobileNum = member.optString(Const.STA_MOBILE,personData.mobileNum);
 	        personData.fTimes = member.optString(Const.STA_FREQUENCY,personData.fTimes);
 	        personData.address = member.optString(Const.STA_ADDRESS,personData.address);
 	        personData.tag = member.optString(Const.STA_TAG,personData.tag);
 	        personData.tagArray = BaseUtils.splitString(personData.tag);
 	        personData.lng = member.optDouble(Const.STA_LNG,personData.lng);
 	        personData.lat = member.optDouble(Const.STA_LAT,personData.lat);
         }
         if(personData.lat > 0){
        	 personData.farLong = MapUtil.getDistanceDoubleValue(personData.lat,personData.lng);
         	personData.far = MapUtil.getDistance(personData.farLong);
         }
         return personData;
    }
    public static PersonData updatePerson(PersonData personData,JSONObject member){
    	if(personData == null){
    		personData = new PersonData();
    	}
    	personData.updateData(member);
        return personData;
    }
    public static PersonData newInstance(JSONObject member){
       
        return updatePerson(new PersonData(), member);
    }

	@Override
	public void OnClick(Activity activity, IBaseDataHandledCallback handledCallback, View attachedView) {
		StaticOnClick(activity, userName, mobileNum);
	}
	
	public static void StaticOnClick(Activity activity,String name,String mobileNum){
		Intent i = new Intent();
		i.putExtra(Const.PRI_LAYOUT_ID, R.layout.ui_podcast_person);
		i.putExtra(Const.STA_TITLE, name + "钓播");
		i.putExtra(Const.PRI_HIDE_PUBLISH, true);
		i.putExtra(Const.STA_MOBILE, mobileNum);
		UIMgr.showActivity(activity,i,BaseUI.class.getName());
	}
}
