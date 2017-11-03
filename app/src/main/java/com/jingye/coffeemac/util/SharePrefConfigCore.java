package com.jingye.coffeemac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;

public class SharePrefConfigCore {

	private static SharePrefConfigCore instance;
	
	private SharedPreferences settings;

	private Editor editor;
	
	public static final String PrefsFileName = "VendorPrefCore";

	private static final String LAST_USER_ID = "last_user_id";
	private static final String LAST_USER_PWD = "last_user_pwd";
	private static final String LAST_UPDATE_TIMESTAMP = "last_update_timestamp";
	private static final String LAST_SESSION_ID = "last_session_id";
	
	private SharePrefConfigCore(Context context) {
		int __sdkLevel = Build.VERSION.SDK_INT;
		settings = context.getSharedPreferences(PrefsFileName, (__sdkLevel > 8) 
				? 4 : Context.MODE_WORLD_READABLE);
		editor = settings.edit();
	}

	public static SharePrefConfigCore getInstance(Context context) {
		if (instance == null) {
			instance = new SharePrefConfigCore(context);
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
	
	public void setLastUpdateTimeSamp(long timesamp){
		editor.putLong(LAST_UPDATE_TIMESTAMP, timesamp);
		editor.commit();
	}
	
	public long getLastUpdateTimeSamp() {
		return settings.getLong(LAST_UPDATE_TIMESTAMP, -1);
	}

	public void setLastUserId(String uid) {
		editor.putString(LAST_USER_ID, uid);
		editor.commit();
		Log.d("SharePrefrence", "Save Last User Id : " + uid);
	}

	public String getLastUserId() {
		String uid = settings.getString(LAST_USER_ID, null);
		Log.d("SharePrefrence", "Read Last User Id : " + uid);
		return uid;
	}
	
	public void setLastUserPwd(String pwd) {
		editor.putString(LAST_USER_PWD, pwd);
		editor.commit();
	}

	public String getLastUserPwd() {
		String uid = settings.getString(LAST_USER_PWD, null);
		return uid;
	}
	
	public void setLastSessionId(String SessionId) {
		editor.putString(LAST_SESSION_ID, SessionId);
		editor.commit();
	}

	public String getLastSessionId() {
		String uid = settings.getString(LAST_SESSION_ID, null);
		return uid;
	}
}
