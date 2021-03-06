package com.go.fish.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.URLUtil;

/**
 * Created by DCloud on 2015/10/14.
 */
public class UrlUtils {

    private static UrlUtils instance = null;

    public static void initEnv(Context context) {
        if (instance == null) {
            instance = new UrlUtils();
            instance.initUserAgent(context);
        }
    }

    static public UrlUtils self() {
        return instance;
    }

    public HashMap<String, String> getRequestHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", getUserAgent());
        headers.put("cc_token", getToken());
        return headers;
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();

    }

    String deviceId = null;
    String ua = null;
    String deviceType = "android";

    public String getUserAgent() {
        return ua;
    }

    private void initUserAgent(Context context) {
        TelephonyManager _telephonyMgr = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        deviceId = _telephonyMgr.getDeviceId();//
        if (deviceId == null) {
            deviceId = LocalMgr.self().getUserInfo(Const.K_DeviceId);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = md5("" + System.currentTimeMillis() + new Random().nextLong());
                LocalMgr.self().saveUserInfo(Const.K_DeviceId, deviceId);
            }
        }
        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = pinfo.versionCode;
        } catch (NameNotFoundException e) {
        }
        ua = deviceType + ";" + deviceId + ";" + versionCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String token = "";

    public String getToken() {
        return token;
    }

    String HOST = "http://115.29.51.39:8080";
    String TEST_HOST = "http://192.168.1.125:9027/qndiao";
    boolean isTest = false;

    public String getHost() {
        return isTest ? TEST_HOST : HOST;
    }

    public String getNetUrl(String url){
    	if(TextUtils.isEmpty(url)) return url;
    	if(URLUtil.isNetworkUrl(url)){
    		return url;
    	}else{
    		while(url.startsWith("/")){
    			url = url.substring(1);
    		}
    		return getHost() + "//" + url;
    	}
    }
    String memberLogin = "/member/memberLogin";
    public String getMemberLogin() {
        return getHost() + memberLogin;
    }

    String registerMember = "/member/registerMember";
    public String getRegisterMember() {
        return getHost() + registerMember;
    }

    String sendCheckNode = "/member/sendCheckNode";
    public String getSendCheckNode() {//查看验证码接口 http://115.29.51.39:8080/code/listCode
        return getHost() + sendCheckNode;
    }
    
    String checkNode = "/member/checkNode";
    public String getCheckNode() {
    	return getHost() + checkNode;
    }

    String checkMobile = "/member/checkMobile";
    public String getCheckMobile() {//查看验证码接口 http://115.29.51.39:8080/code/listCode
        return getHost() + checkMobile;
    }

    String checkValidateCode = "/member/checkValidateCode";
    public String getCheckValidateCode() {
        return getHost() + checkValidateCode;
    }

    String resetPassword = "/member/resetPassword";
    public String getResetPassword() {
        return getHost() + resetPassword;
    }

    String resetNewPassword = "/member/resetNewPassword";
    public String getResetNewPassword() {
        return getHost() + resetNewPassword;
    }

    String completeData = "/member/completeData";
    public String getCompleteData() {
        return getHost() + completeData;
    }

    String resetUserName = "/member/resetUserName";
    public String getResetUserName() {
        return getHost() + resetUserName;
    }

    String queryForMap = "/merchant/queryForMap";
    public String getQueryForMap() {
        return getHost() + queryForMap;
    }

    String fieldInfo = "/merchant/fieldInfo";
    public String getFieldInfo() {
        return getHost() + fieldInfo;
    }

    String attention = "/merchant/attention";
    public String getAttention() {//关注
        return getHost() + attention;
    }
    String praise = "/merchant/praise";
    public String getPraise() {//点赞
    	return getHost() + praise;
    }
    
    String commentList = "/merchant/commentList";//?objectId=1&type=field
    public String getCommentList() {
    	return getHost() + commentList;
    }
    
    String attListForField = "/merchant/attListForField";
    public String getAttListForField() {
    	return getHost() + attListForField;
    }
    
    String commentOn = "/merchant/commentOn";
    public String getCommentOn() {
    	return getHost() + commentOn;
    }
    
    String infoList = "/relevant/infoList";
    public String getInfoList() {
    	return getHost() + infoList;
    }

    String aroundMember = "/member/aroundMember";
    public String getAroundMember() {
    	return getHost() + aroundMember;
    }

    String podCastList = "/relevant/podCastList";
    public String getPodCastList() {
    	return getHost() + podCastList;
    }
    String uploadUserImg = "/member/uploadUserImg";
    public String getUploadUserImg() {
    	return getHost() + uploadUserImg;
    }
    
    String setLocation = "/member/setLocation";
    public String getSetLocation() {
    	return getHost() + setLocation;
    }
    String getSettingData = "/member/getSettingData";
    public String getSettingData() {
    	return getHost() + getSettingData;
    }
    String createPodCast = "/relevant/createPodCast";
    public String getCreatePodCast() {
    	return getHost() + createPodCast;
    }
    String attentionPod = "/relevant/attentionPod";
    public String getAttentionPod() {//关注/取消 钓播 infoId type
    	return getHost() + attentionPod;
    }
    String praisePod = "/relevant/praisePod";
    public String getpPraisePod() {//点赞/取消赞 钓播 infoId type
    	return getHost() + praisePod;
    }
    String info = "/relevant/info";
    public String getInfo() {//钓播详情
    	return getHost() + info;
    }
    String priceList = "/relevant/priceList";
    public String getPriceList() {//列表参数  startIndex  size
    	return getHost() + priceList;
    }
    
    String attentionPrice = "/relevant/attentionPrice";
    public String getAttentionPrice() {//pirceId   type
    	return getHost() + attentionPrice;
    }
    
    String praisePrice = "/relevant/praisePrice";
    public String getPraisePrice() {
    	return getHost() + priceList;
    }
    String attListForM = "/merchant/attListForM";
    public String getAttListForM() {//  startIndex size
    	return getHost() + attListForM;
    }
    String priceInfo = "/relevant/priceInfo";
    public String getPriceInfo() {//  startIndex size
    	return getHost() + priceInfo;
    }
}
