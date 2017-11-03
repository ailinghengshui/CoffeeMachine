package com.jingye.coffeemac.service.core;


import com.jingye.coffeemac.util.SharePrefConfigCore;

import android.content.Context;

public class PasswordGenerator {

	private static final String USER_TOKEN = "user_cache";

	public static String getPassword(Context context) {
		SharePrefConfigCore util = SharePrefConfigCore.getInstance(context);
		String token = util.getSharedPreferences().getString(USER_TOKEN, "");
		
		return token;
	}
	
	public static void storeToken(Context context, String token) {
		SharePrefConfigCore util = SharePrefConfigCore.getInstance(context);
		util.getEditor().putString(USER_TOKEN, token);
		util.getEditor().commit();
	}
}
