package com.go.fish.data;


import org.json.JSONObject;

import com.go.fish.view.IBaseData;

public class PersonData implements IBaseData {

    public  String id;
    public  String photoUrl;
    public String userName;
    public String[] aiHaos = null;
    public String far = null;
    public int fYears = 0;
    public int fTimes = 0;

    public static PersonData newInstance(JSONObject json){
        PersonData personData = new PersonData();
        personData.userName = json.optString("name");
        personData.fYears = json.optInt("year");
        personData.fTimes = json.optInt("times");
        personData.far = json.optString("far");
        return personData;
    }
}
