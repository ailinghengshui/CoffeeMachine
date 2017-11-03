package com.jingye.coffeemac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SharePrefConfig {

	public static final int DEFAULT_TEMP=98;
	public static final String PrefsFileName = "VendorPref";
	public static final int LANGUAGECH = 0;
	public static final int LANGUAGEEN = 1;
	private static final String IS_NEED_LOCK = "is_need_lock";
	private static final String WORK_TEMP = "work_temp";
	private static final String KEEP_TEMP = "keep_temp";
	private static final String DOSING_NUM = "dosing_num";
	private static final String DOSING_SETTING = "setting_dosing_";
    private static final String APP_DOWNLOAD_URL_TIMESTAMP = "app_download_time";
    private static final String APP_DOWNLOAD_URL_ANDROID = "app_download_android";
    private static final String APP_DOWNLOAD_URL_IOS = "app_download_ios";
	private static final String MACHINE_WASH_TIME = "wash_time";
	private static final String WELCOME_ADV_IMGS = "adv_imgs";
	private static final String WELCOME_ADV_UPDATE_TIME = "adv_update_time";
	private static final String LANGUAGETYPE = "languagetype";
	private static final String COMP_WATER = "comp_water";
	private static final String IS_LAST_COFFEE ="is_last_coffee" ;
	private static SharePrefConfig instance;
	private SharedPreferences settings;
	private Editor editor;

	private SharePrefConfig(Context context) {
		int __sdkLevel = Build.VERSION.SDK_INT;
		settings = context.getSharedPreferences(PrefsFileName, (__sdkLevel > 8) 
				? 4 : Context.MODE_WORLD_READABLE);
		editor = settings.edit();
	}

	public static SharePrefConfig getInstance() {
		if (instance == null) {
			instance = new SharePrefConfig(MyApplication.Instance());
		}
		return instance;
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
	}

	public Editor getEditor() {
		return editor;
	}

	public void clearAllData() {
		editor.clear();
		editor.commit();
	}

	public void setTemp(String workTemp,String keepTemp){
		setTemp(Integer.getInteger(workTemp,DEFAULT_TEMP),Integer.getInteger(keepTemp,DEFAULT_TEMP));
	}

	public void setTemp(int workTemp, int keepTemp) {
		editor.putInt(WORK_TEMP,workTemp);
		editor.putInt(KEEP_TEMP,keepTemp);
		editor.commit();
	}

	public int getWorkTemp(){
		return settings.getInt(WORK_TEMP,DEFAULT_TEMP);
	}

	public int getKeepTemp(){
		return settings.getInt(KEEP_TEMP,DEFAULT_TEMP);
	}

	public boolean isDosingInit(String vendorNum) {

		if(settings.getString(DOSING_NUM,"NO_NUM").equals(vendorNum)){
			return true;
		}else{
			return false;
		}
//		return ((settings.getString(DOSING_NUM,"NO_NUM")).equals(vendorNum));
//		return settings.getBoolean(DOSING_NUM, false);
	}

	public void setDosingInit(String vendorNum) {
		editor.putString(DOSING_NUM,vendorNum);
		editor.commit();
	}

	public void setIsNeedLock(boolean isNeedLock){
		editor.putBoolean(IS_NEED_LOCK,isNeedLock);
		editor.commit();
	}

	public boolean isNeedLock(){
		return settings.getBoolean(IS_NEED_LOCK,false);
	}
	
	public void setDosingValue(String value, int dosingID) {
		editor.putString(DOSING_SETTING + dosingID, value);
		editor.commit();
	}

	public double getDosingValue(int dosingID) {
		String dosingValue = settings.getString(DOSING_SETTING + dosingID, "0");
		double ret = 0;
		try{
			ret = Double.parseDouble(dosingValue);
		}catch(Exception e){
			e.printStackTrace();
		}

		return ret;
	}

    public String getAppDownloadUrlAndroid() {
        String downloadUrl = settings.getString(APP_DOWNLOAD_URL_ANDROID , "");
        return downloadUrl;
    }

    public void setAppDownloadUrlAndroid(String urlAndroid) {
        editor.putString(APP_DOWNLOAD_URL_ANDROID, urlAndroid);
        editor.commit();
    }

    public String getAppDownloadUrlIos() {
        String downloadUrl = settings.getString(APP_DOWNLOAD_URL_IOS , "");
        return downloadUrl;
    }

    public void setAppDownloadUrlIos(String urlIos) {
        editor.putString(APP_DOWNLOAD_URL_IOS, urlIos);
        editor.commit();
    }

    public void setAppDownloadTime(long timestamp){
        editor.putLong(APP_DOWNLOAD_URL_TIMESTAMP, timestamp);
        editor.commit();
    }

    public long getAppDownloadUrlTime(){
        long timestamp = settings.getLong(APP_DOWNLOAD_URL_TIMESTAMP, 0);
        return timestamp;
    }

	public Set<String> getWashTime(){
		Set<String> timeSet = settings.getStringSet(MACHINE_WASH_TIME, null);
		return timeSet;
	}

	public void setWashTime(Set<String> set){
		editor.putStringSet(MACHINE_WASH_TIME, set);
		editor.commit();
	}

	public List<String> getAdvImgs(){
		List<String> advPicUrlsList = new ArrayList<String>();
		try{
			String advImgUrls = settings.getString(WELCOME_ADV_IMGS , "");

			Log.d("wecolme get",advImgUrls);

			if(TextUtils.isEmpty(advImgUrls)){
				return null;
			}

			JSONArray array = JSONArray.parseArray(advImgUrls);
			if(array != null){
				int size = array.size();
				for(int i = 0; i < size; i++){
					String picURL = array.getString(i);
					advPicUrlsList.add(picURL);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.e("DEBUG", "get local history error");
		}


		return advPicUrlsList;
	}

	public void setAdvImgs(String advImgUrls){
		editor.remove(WELCOME_ADV_IMGS);
		editor.putString(WELCOME_ADV_IMGS, advImgUrls);
		editor.commit();

		Log.d("wecolme set",advImgUrls);

		setAdvUpdateTime(TimeUtil.getNow_millisecond());
	}

	public long getAdvUpdateTime(){
		long updateTime = settings.getLong(WELCOME_ADV_UPDATE_TIME, 0);
		return updateTime;
	}

	public void setAdvUpdateTime(long time){
		editor.putLong(WELCOME_ADV_UPDATE_TIME, time);
		editor.commit();
	}

	public int getLanguageType(){
		int languageType = settings.getInt(LANGUAGETYPE, LANGUAGECH);
		return languageType;
	}

	public void setLanguageType(int type){
		editor.putInt(LANGUAGETYPE, type);
		editor.commit();
	}

	public int getConpWater() {

		return settings.getInt(COMP_WATER,35);
	}

	public void setConpWater(int conpWater){
		editor.putInt(COMP_WATER,conpWater);
		editor.commit();
	}

	public boolean getLastCoffee(){

		return settings.getBoolean(IS_LAST_COFFEE,false);
	}

	public void setLastCoffee(boolean isLastCoffee){
		editor.putBoolean(IS_LAST_COFFEE,isLastCoffee);
		editor.commit();
	}


}
