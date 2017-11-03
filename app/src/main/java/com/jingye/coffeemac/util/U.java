package com.jingye.coffeemac.util;

import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.common.dbhelper.SettingDbHelper;
import com.jingye.coffeemac.service.ITranCode;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class U {

	public static final String KEY_USER_IS_LOGINED = "login_status";
	public static final String KEY_USER_VENDOR = "vendor";
    public static final String KEY_USER_VENDOR_NAME = "vendor_name";
	public static final String KEY_USER_STATUS = "user_status";
	
	public static String getMyVendorNum() {
		String vendorNum = GlobalCached.activeVendor;
		if (TextUtils.isEmpty(vendorNum)) {
			vendorNum = queryAppSet(KEY_USER_VENDOR);
		}
		return vendorNum;
	}
	
	public static void saveMyVendorNum(String phone) {
		if (TextUtils.isEmpty(phone)) {
			String phoneNum = GlobalCached.activeVendor;
			saveAppSet(KEY_USER_VENDOR, phoneNum);
		}
		saveAppSet(KEY_USER_VENDOR, phone);
	}

    public static String getMyVendorName() {
        String vendorName = queryAppSet(KEY_USER_VENDOR_NAME);
        return vendorName;
    }

    public static void saveMyVendorName(String vendorName) {
        saveAppSet(KEY_USER_VENDOR_NAME, vendorName);
    }
	
	public static void saveAppSet(String key, String value, String remark){
		SettingDbHelper.saveAppSet(key, value, remark);
	}
	
	public static void saveAppSet(String key, String value) {
		SettingDbHelper.saveAppSet(key, value, "");
	}

	public static String queryAppSet(String key) {
		return SettingDbHelper.queryAppSet(key);
	}
	
	public static String queryAppSet(String key, String remark){
		return SettingDbHelper.queryAppSet(key, remark);
	}
	
	public static void setUserStatus(Context context, int status) {
		saveAppSet(KEY_USER_STATUS, String.valueOf(status));
	}

	public static int getVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pi == null) {
			return 0;
		} else {
			return pi.versionCode;
		}
	}

	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pi == null) {
			return "";
		} else {
			return pi.versionName;
		}
	}

	public static int getUserStatus(Context context) {
		String status = queryAppSet(KEY_USER_STATUS);
		if (TextUtils.isEmpty(status)) {
			return ITranCode.STATUS_UNLOGIN;
		}
		return Integer.valueOf(status);
	}

	public static String getNetworkInfo(Context context) {
		String info = "";
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
			if (activeNetInfo != null) {
				if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					info = activeNetInfo.getTypeName();
				} else {
					StringBuilder sb = new StringBuilder();
					TelephonyManager tm = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					sb.append(activeNetInfo.getTypeName());
					sb.append(" [");
					if (tm != null) {
						// Result may be unreliable on CDMA networks
						sb.append(tm.getNetworkOperatorName());
						sb.append("#");
					}
					sb.append(activeNetInfo.getSubtypeName());
					sb.append("]");
					info = sb.toString();
				}
			}
		}
		return info;
	}	
}
