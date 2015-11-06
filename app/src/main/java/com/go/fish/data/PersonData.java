package com.go.fish.data;


import org.json.JSONObject;

import com.go.fish.util.Const;
import com.go.fish.view.IBaseData;

public class PersonData implements IBaseData {

    public int id;
    public  String photoUrl;
    public String userName;
    public String[] aiHaos = null;
    public String far = null;
    public int fYears = 0;
    public int fTimes = 0;

    public static PersonData newInstance(JSONObject json){
        PersonData personData = new PersonData();
        personData.id = json.optInt(Const.STA_MEMBER_ID);
        personData.photoUrl = json.optString(Const.STA_IMGURL);
        personData.userName = json.optString(Const.STA_NAME);
        personData.fYears = json.optInt(Const.STA_FISH_YEAR);
        personData.fTimes = json.optInt(Const.STA_FREQUENCY);
        personData.far = json.optString(Const.STA_FAR);
        return personData;
    }
}
