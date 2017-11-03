package com.jingye.coffeemac.util;

import android.os.Environment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ToolUtil {

    //防止连续点击
    private static long lastClickTime;

    /**
     * 生成唯一号
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;

    }

    public static String getDateTime(String format) {
        Date date = new Date();

        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public static final String formatDate(String pdate, String fpat, String tpat) {
        if (pdate == null)
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(fpat, Locale.getDefault());
        Date tmp;
        try {
            tmp = formatter.parse(pdate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        formatter.applyPattern(tpat);
        return formatter.format(tmp);
    }

    /**
     * 格式化
     *
     * @param date
     * @param time
     * @return
     */
    public static String getSmartDateTime(String date, String time) {

        //return TimeUtil.getTimeShowString(date,time);

        if (date.equals(getDateTime("yyyyMMdd"))) {
            return formatDate(time, "HHmmss", "HH:mm");
        } else if (Integer.valueOf(date) == Integer.valueOf(getDateTime("yyyyMMdd")) - 1) {
            return "昨天";
        } else if (Integer.valueOf(date) == Integer.valueOf(getDateTime("yyyyMMdd")) - 2) {
            return "前天";
        } else {
            Date nowDate = new Date();

            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

            Date _date = null;
            try {
                _date = dateformatter.parse(date);
            } catch (ParseException e) {

                e.printStackTrace();
                return "";
            }
            if (CommonUtil.isSameWeekDates(nowDate, _date)) {
                return CommonUtil.getWeekOfDateS(_date);
            } else if (getDateTime("yyyy").equals(formatDate(date, "yyyyMMdd", "yyyy")))
                return formatDate(date, "yyyyMMdd", "MM-dd");
            else
                return formatDate(date, "yyyyMMdd", "yyyy-MM-dd");
        }
    }

    /**
     * 获取sdcard的根目录
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.getPath();
        }
        return null;
    }

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public synchronized static boolean above5Mi() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime > 5500) {
            lastClickTime = time;
            return true;
        }
        return false;
    }
}
