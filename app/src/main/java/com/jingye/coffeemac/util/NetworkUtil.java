package com.jingye.coffeemac.util;

import com.jingye.coffeemac.util.log.LogUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class NetworkUtil {
    public static final byte CURRENT_NETWORK_TYPE_NONE = 0;

    /**
     * 根据APN区分网络类型
     */
    public static final byte CURRENT_NETWORK_TYPE_WIFI = 1;//wifi
    public static final byte CURRENT_NETWORK_TYPE_CTNET = 2;//ctnet
    public static final byte CURRENT_NETWORK_TYPE_CTWAP = 3;//ctwap
    public static final byte CURRENT_NETWORK_TYPE_CMWAP = 4;//cmwap
    public static final byte CURRENT_NETWORK_TYPE_UNIWAP = 5;//uniwap,3gwap
    public static final byte CURRENT_NETWORK_TYPE_CMNET = 6;//cmnet
    public static final byte CURRENT_NETWORK_TYPE_UNIET = 7;//uninet,3gnet

    /**
     * 根据运营商区分网络类型
     */
    public static final byte CURRENT_NETWORK_TYPE_CTC = 10;//ctwap,ctnet
    public static final byte CURRENT_NETWORK_TYPE_CUC = 11;//uniwap,3gwap,uninet,3gnet
    public static final byte CURRENT_NETWORK_TYPE_CM = 12;//cmwap,cmnet

    /**
     * apn值
     */
    private static final String CONNECT_TYPE_WIFI = "wifi";
    private static final String CONNECT_TYPE_CTNET = "ctnet";
    private static final String CONNECT_TYPE_CTWAP = "ctwap";
    private static final String CONNECT_TYPE_CMNET = "cmnet";
    private static final String CONNECT_TYPE_CMWAP = "cmwap";
    private static final String CONNECT_TYPE_UNIWAP = "uniwap";
    private static final String CONNECT_TYPE_UNINET = "uninet";
    private static final String CONNECT_TYPE_UNI3GWAP = "3gwap";
    private static final String CONNECT_TYPE_UNI3GNET = "3gnet";

    private static final Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    public static byte curNetworkType = CURRENT_NETWORK_TYPE_NONE;

    /**
     * 判断当前网络类型。WIFI,NET,WAP
     *
     * @param context
     * @return
     */
    public static byte getCurrentNetType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        byte type = CURRENT_NETWORK_TYPE_NONE;
        if (networkInfo != null) {
            // String typeName = networkInfo.getTypeName();
            // XT800
            String typeName = networkInfo.getExtraInfo();
            if (TextUtils.isEmpty(typeName)) {
                typeName = networkInfo.getTypeName();
            }
            if (!TextUtils.isEmpty(typeName)) {
                String temp = typeName.toLowerCase();
                if (temp.indexOf(CONNECT_TYPE_WIFI) > -1) {// wifi
                    type = CURRENT_NETWORK_TYPE_WIFI;
                } else if (temp.indexOf(CONNECT_TYPE_CTNET) > -1) {// ctnet
                    type = CURRENT_NETWORK_TYPE_CTNET;
                } else if (temp.indexOf(CONNECT_TYPE_CTWAP) > -1) {// ctwap
                    type = CURRENT_NETWORK_TYPE_CTWAP;
                } else if (temp.indexOf(CONNECT_TYPE_CMNET) > -1) {// cmnet
                    type = CURRENT_NETWORK_TYPE_CMNET;
                } else if (temp.indexOf(CONNECT_TYPE_CMWAP) > -1) {// cmwap
                    type = CURRENT_NETWORK_TYPE_CMWAP;
                } else if (temp.indexOf(CONNECT_TYPE_UNIWAP) > -1) {// uniwap
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                } else if (temp.indexOf(CONNECT_TYPE_UNI3GWAP) > -1) {// 3gwap
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                } else if (temp.indexOf(CONNECT_TYPE_UNINET) > -1) {// uninet
                    type = CURRENT_NETWORK_TYPE_UNIET;
                } else if (temp.indexOf(CONNECT_TYPE_UNI3GNET) > -1) {// 3gnet
                    type = CURRENT_NETWORK_TYPE_UNIET;
                }
            }
        }
        if (type == CURRENT_NETWORK_TYPE_NONE) {
            String apnType = getApnType(context);
            if (apnType != null && apnType.equals(CONNECT_TYPE_CTNET)) {// ctnet
                type = CURRENT_NETWORK_TYPE_CTNET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CTWAP)) {// ctwap
                type = CURRENT_NETWORK_TYPE_CTWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CMWAP)) {// cmwap
                type = CURRENT_NETWORK_TYPE_CMWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CMNET)) {// cmnet
                type = CURRENT_NETWORK_TYPE_CMNET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNIWAP)) {// uniwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GWAP)) {// 3gwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNINET)) {// uninet
                type = CURRENT_NETWORK_TYPE_UNIET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GNET)) {// 3gnet
                type = CURRENT_NETWORK_TYPE_UNIET;
            }
        }

        curNetworkType = type;

        return type;
    }

    /**
     * 判断APNTYPE
     *
     * @param context
     * @return
     */
    public static String getApnType(Context context) {

        String apntype = "nomatch";
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null,
                null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String user = c.getString(c.getColumnIndex("user"));
                if (user != null && user.startsWith(CONNECT_TYPE_CTNET)) {
                    apntype = CONNECT_TYPE_CTNET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CTWAP)) {
                    apntype = CONNECT_TYPE_CTWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CMWAP)) {
                    apntype = CONNECT_TYPE_CMWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CMNET)) {
                    apntype = CONNECT_TYPE_CMNET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNIWAP)) {
                    apntype = CONNECT_TYPE_UNIWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNINET)) {
                    apntype = CONNECT_TYPE_UNINET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GWAP)) {
                    apntype = CONNECT_TYPE_UNI3GWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GNET)) {
                    apntype = CONNECT_TYPE_UNI3GNET;
                }
            }
            c.close();
            c = null;
        }

        return apntype;
    }

    /**
     * 判断是否有网络可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        } else {
            return false;
        }
    }

    /**
     * 此判断不可靠
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo != null) {
            boolean a = networkInfo.isConnected();
            return a;
        } else {
            return false;
        }
    }

    /**
     * 判断网络是否连接上
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo != null) {
            if (networkInfo.getState().compareTo(NetworkInfo.State.CONNECTED) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取可用的网络信息
     *
     * @param context
     * @return
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 当前网络是否是wifi网络
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getNetworkConnectionStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }

        if ((tm.getDataState() == TelephonyManager.DATA_CONNECTED || tm
                .getDataState() == TelephonyManager.DATA_ACTIVITY_NONE)
                && info.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getNetworkProxyInfo(Context context) {
        String proxyHost = android.net.Proxy.getDefaultHost();
        int proxyPort = android.net.Proxy.getDefaultPort();
        String szport = String.valueOf(proxyPort);
        String proxyInfo = null;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return null;
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                String typeName = info.getTypeName().toLowerCase();
                if (typeName != null && typeName.equals("wifi")) {
                    return null;
                }
            } else {
                return null;
            }
        }

        if (proxyHost != null && (0 < proxyPort && proxyPort < 65535)) {
            proxyInfo = proxyHost + ":" + szport;
            return proxyInfo;
        } else {
            return null;
        }
    }

    public static String getNetworkProxyUrl(Context context) {
        if (isWifi(context)) {
            return null;
        }

        String proxyHost = android.net.Proxy.getDefaultHost();
        LogUtil.e("vendor", "proxyHost:" + proxyHost);
        return proxyHost;
    }

    public static String getNetworkProxyUrl() {
        /**
         * 当网络为wifi时,直接返回空代理:
         * 当ctwap,cmwap,uniwap,3gwap开启时同时开启wifi网络
         * ,通过下面的getDefaultHost接口将得到对应wap网络代理ip
         * ,这是错误的,所以在此判断当前网络是否为wifi
         */
        if (curNetworkType == CURRENT_NETWORK_TYPE_WIFI) {
            return null;
        }

        String proxyHost = android.net.Proxy.getDefaultHost();
        LogUtil.e("vendor", "proxyHost:" + proxyHost);
        return proxyHost;
    }

    public static int getNetworkProxyPort() {
        int proxyPort = android.net.Proxy.getDefaultPort();
        return proxyPort;
    }

    public static boolean isCtwap(Context context) {
        if (getApnType(context).equals(CONNECT_TYPE_CTWAP)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isUniwap(Context context) {
        if (getApnType(context).equals(CONNECT_TYPE_UNIWAP)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCmwap(Context context) {
        if (getApnType(context).equals(CONNECT_TYPE_CMWAP)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是电信网络(ctwap,ctnet)
     *
     * @return
     */
    public static boolean isCtcNetwork(Context context) {
        byte type = getCurrentNetType(context);

        return isCtcNetwork(type);
    }

    public static boolean isCtcNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }

        if (apnName.equals(CONNECT_TYPE_CTWAP)
                || apnName.equals(CONNECT_TYPE_CTNET)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCtcNetwork(byte type) {
        if (type == CURRENT_NETWORK_TYPE_CTWAP
                || type == CURRENT_NETWORK_TYPE_CTNET) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是联通网络(uniwap,uninet,3gwap,3gnet)
     *
     * @return
     */
    public static boolean isCucNetwork(Context context) {
        byte type = getCurrentNetType(context);

        return isCucNetwork(type);
    }

    public static boolean isCucNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }

        if (apnName.equals(CONNECT_TYPE_UNIWAP)
                || apnName.equals(CONNECT_TYPE_UNINET)
                || apnName.equals(CONNECT_TYPE_UNI3GWAP)
                || apnName.equals(CONNECT_TYPE_UNI3GNET)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCucNetwork(byte type) {
        if (type == CURRENT_NETWORK_TYPE_UNIWAP
                || type == CURRENT_NETWORK_TYPE_UNIET) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是移动网络(cmwap,cmnet)
     *
     * @return
     */
    public static boolean isCmbNetwork(Context context) {
        byte type = getCurrentNetType(context);

        return isCmbNetwork(type);
    }

    public static boolean isCmbNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }

        if (apnName.equals(CONNECT_TYPE_CMWAP)
                || apnName.equals(CONNECT_TYPE_CMNET)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCmbNetwork(byte type) {
        if (type == CURRENT_NETWORK_TYPE_CMWAP
                || type == CURRENT_NETWORK_TYPE_CMNET) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取网络运营商类型(中国移动,中国联通,中国电信,wifi)
     *
     * @param context
     * @return
     */
    public static byte getNetworkOperators(Context context) {
        if (isWifi(context)) {
            return CURRENT_NETWORK_TYPE_WIFI;
        } else if (isCtcNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CTC;
        } else if (isCmbNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CM;
        } else if (isCucNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CUC;
        } else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    public static byte getNetworkOperators(byte type) {
        if (type == CURRENT_NETWORK_TYPE_NONE) {
            return CURRENT_NETWORK_TYPE_NONE;
        } else if (type == CURRENT_NETWORK_TYPE_WIFI) {
            return CURRENT_NETWORK_TYPE_WIFI;
        } else if (type == CURRENT_NETWORK_TYPE_CTNET
                || type == CURRENT_NETWORK_TYPE_CTWAP) {
            return CURRENT_NETWORK_TYPE_CTC;
        } else if (type == CURRENT_NETWORK_TYPE_CMWAP
                || type == CURRENT_NETWORK_TYPE_CMNET) {
            return CURRENT_NETWORK_TYPE_CM;
        } else if (type == CURRENT_NETWORK_TYPE_UNIWAP
                || type == CURRENT_NETWORK_TYPE_UNIET) {
            return CURRENT_NETWORK_TYPE_CUC;
        } else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    /**
     * 是否需要设置代理(网络请求,一般用于wap网络,但有些机型设置代理会导致系统异常)
     *
     * @return
     */
    public static boolean isNeedSetProxyForNetRequest() {    //#00044 +
        if (Build.MODEL.equals("SCH-N719") || Build.MODEL.equals("SCH-I939D")) {
            return false;
        } else {
            return true;
        }
    }


    public static String getActiveMacAddress(Context context) {
//		String macAddr = "000000000000";
//
//		if (context.getPackageManager().checkPermission(Manifest.permission.ACCESS_WIFI_STATE,
//				context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
//			final WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//			final WifiInfo info = manager.getConnectionInfo();
//			if (null != info) {
//				macAddr = info.getMacAddress();
//			}
//		}
//		else {
//			Log.d("NetworkUtil",
//					"Failed to get mac address due to Application does not have permission ACCESS_WIFI_STATE.");
//		}
//
//		return macAddr;
        LogUtil.vendor("NetworkUtil"+"-----" + getMacAddress(context));
        return getMacAddress(context);
    }

    public static String getMacAddress(Context context) {
//        String macAddress = "0000000000000";
//        if ((isWifi(context))) {
//            macAddress = getMacAddressByWifiInfo(context);
//        } else {
//            macAddress = getMacAddressByNetworkInterface();
//        }

        return getMacAddressByNetworkInterface(context);
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return MAC地址
     */
    private static String getMacAddressByWifiInfo(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    private static String getMacAddressByNetworkInterface(Context context) {
        String macAddress = "02:00:00:00:00:00";
        WifiManager wifiManager =
                (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());

        if (!wifiManager.isWifiEnabled())
        {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;

    }

}
