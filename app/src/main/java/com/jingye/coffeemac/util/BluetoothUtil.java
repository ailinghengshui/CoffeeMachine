package com.jingye.coffeemac.util;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Hades on 2017/5/9.
 */
public class BluetoothUtil {
    private static final String TAG = BluetoothUtil.class.getSimpleName();


    public static BluetoothAdapter getBluetoothAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static String getMacAddressByBluetooth() {
        if (isBluetoothAvailable()) {
            if (isEnable()) {
                if(isDiscovery()){
                    cancelDiscovery();
                }
                return getBluetoothAdapter().getAddress();
            } else {
                enable();
                if(isDiscovery()){
                    cancelDiscovery();
                }
                return "00:11:22:AA:BB:CC";
            }

        } else {
            return "00:11:22:AA:BB:CC";
        }
    }

    public static boolean isDiscovery() {
        return getBluetoothAdapter().isDiscovering();
    }

    public static boolean cancelDiscovery() {
        return getBluetoothAdapter().cancelDiscovery();
    }

    public static boolean isEnable() {
        return getBluetoothAdapter().isEnabled();
    }

    public static boolean enable() {
        return getBluetoothAdapter().enable();
    }



    public static boolean isBluetoothAvailable() {

        try {
            if (getBluetoothAdapter() == null || getBluetoothAdapter().getAddress().equals(null)) {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }
}
