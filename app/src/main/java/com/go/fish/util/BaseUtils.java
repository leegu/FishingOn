package com.go.fish.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by DCloud on 2015/10/18.
 */
public class BaseUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date curDate = new Date();
    public static HashMap<String, Integer> tagColors = new HashMap<>();
    public static int getTagBg(String tag){
    	return tagColors.get(tag);
    }
    public static String getCurrentTime(){
    	return sdf.format(new Date());
    }
    public static String getTimeShow(String str){
        String ret = null;
        try {
            str = str.trim();
            Date d2 = sdf.parse(str);
            Date d1 = new Date();
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            StringBuffer sb = new StringBuffer();
            if(days >= 1) {
                if (days == 2) {
                    sb.append("前天");
                } else if (days == 1) {
                    sb.append("昨天");
                }else{
                    return str.split(" ")[0];
                }
                return sb.toString();
            }

            if(hours > 0){
                sb.append(hours).append("小时前");
                return sb.toString();
            }
            if(minutes == 0){
                minutes = 1;
            }
            sb.append(minutes).append("分前");
            ret = sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String joinString(Object... args){
        StringBuffer sb = new StringBuffer();
        for(Object o : args){
            sb.append("|").append(o);
        }
        return sb.toString().substring(1);
    }

    public static String[] splitString(Object str){
    	if(str != null){
    		return String.valueOf(str).split("\\,");
    	}else{
    		return null;
    	}
    }
    
    public static String formatPhoneNum(String num){
    	num = num.trim();
		if(num.length() > 11){
			num = num.substring(num.length() - 11);
		}
		num = new StringBuffer(num.substring(0,3)).append(" ").append(num.substring(3,7)).append(" ").append(num.substring(7)).toString();
		return num;
    }
}
