package com.jingye.coffeemac.application;

import com.jingye.coffeemac.BuildConfig;

public class AppConfig {

    //
    // BUILDS
    // DEFAULT Build.ONLINE
    //
//    public static final Build BUILD_SERVER = Build.TEST;
    public static final Build BUILD_SERVER = BuildConfig.BUILD_SERVER;
    //
    // SERIALPORT
    // DEFAULT true
    //
    private static final boolean SERIALPORT_SYSNC = BuildConfig.SERIALPORT_SYSNC;
    //
    // DEBUG MODE
    // DEFAULT false
    //
    private static final boolean DEBUG_MODE = true;
    //
    // MAC FOR ALI
    // DEFAULT false
    //
    private static  boolean MAC_FOR_ALI = BuildConfig.MAC_FOR_ALI;

    /**
     * MAC FOR ABC(nonghang)
     */
    private static  boolean MAC_FOR_ABC=BuildConfig.MAC_FOR_ABC;
    private static  boolean MAC_FOR_WECHAT=BuildConfig.MAC_FOR_WECHAT;


    public static boolean isSerialportSysnc() {
        return SERIALPORT_SYSNC;
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static boolean isMacForAli() {
        return MAC_FOR_ALI;
    }

    public static void setMacForAli(boolean MacForAli){
        MAC_FOR_ALI=MacForAli;
    }

    public static boolean isMacForWechat(){return MAC_FOR_WECHAT;}

    public static void setMacForWechat(boolean MacForWeChat){
        MAC_FOR_WECHAT=MacForWeChat;
    }

    public static boolean isMacForAbc(){return MAC_FOR_ABC;}

    public static void setMacForAbc(boolean MacForAbc){
        MAC_FOR_ABC=MacForAbc;
    }

    public enum Build {
        LOCAL,
        TEST,
        ONLINE,
    }

}
