package com.jingye.coffeemac.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
	public static String getNowString() {
		return Long.toString((new java.util.Date()).getTime() / 1000);
	}

	public static int getNow() {
		return (int) ((new java.util.Date()).getTime() / 1000);
	}

	public static long getNow_millisecond() {
		return (new java.util.Date()).getTime();
	}

	public static String getNowDatetime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return (formatter.format(new java.util.Date()));
	}
	
	public static String getDateString(long milliseconds) {
		return getDateTimeString(milliseconds, "yyyyMMdd");
	}
	
	public static String getTimeString(long milliseconds) {
		return getDateTimeString(milliseconds, "HHmmss");
	}
	
	public static String getDateTimeString(long milliseconds, String format) {
		Date date = new Date(milliseconds);
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
		return formatter.format(date);
	}
	
	public static String getTimeShowString(String dataString, String timeString) {
		String ret;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			Date nowDate = new Date();
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());	
			
			Date date = dateformatter.parse(dataString);					
			String showDataString;

			String today = dateFormat.format(new Date(nowDate.getTime()));
			String yesterday = dateFormat.format(new Date(nowDate.getTime() - 3600*24*1000));
			
			if (dataString.equals(today)) {
				showDataString = "今天";
			} else if (dataString.equals(yesterday)) {
				showDataString = "昨天";
			}
			else if (CommonUtil.isSameWeekDates(nowDate, date)) {
				showDataString = CommonUtil.getWeekOfDate(date);
			}
			else {
				dateformatter.applyPattern("yyyy-MM-dd");
				showDataString = dateformatter.format(date);
			}

			SimpleDateFormat timeformatter = new SimpleDateFormat("HHmmss", Locale.getDefault());
			Date time = timeformatter.parse(timeString);
			dateformatter.applyPattern("HH:mm");
			String showTimeString = dateformatter.format(time);
			ret = showDataString + " " + showTimeString;
									
		} catch (Exception e) {
			ret = dataString + " " + timeString;
		}
		
		return ret;
	}
	
	public static String getTimeDateShow(String dataString) {
		String ret = null;
		try {
			SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());	
			Date date = dateformatter.parse(dataString);	
			dateformatter.applyPattern("yyyy-MM-dd");
			String showDataString = dateformatter.format(date);
			String weekString = CommonUtil.getWeekOfDate(date);

			ret = showDataString + " " + weekString;						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static String getTimeMinutesShowString(String timeString) {
		String ret;
		try {
			SimpleDateFormat timeformatter = new SimpleDateFormat("HHmmss", Locale.getDefault());
			Date time = timeformatter.parse(timeString);
			timeformatter.applyPattern("HH:mm");
			String showTimeString = timeformatter.format(time);
			
			ret = showTimeString;
		} catch (Exception e) {
			ret = timeString;
		}
		
		return ret;
	}

}
