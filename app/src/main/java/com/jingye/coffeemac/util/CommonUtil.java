package com.jingye.coffeemac.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jingye.coffeemac.util.multicard.MultiCard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class CommonUtil {


	/**
	 * int CommonUtil in main thread so Handler can be created successfully
	 */
	public static void init() {

	}

	public static String getSecretNumber(String src, int length) {
		if (src == null || src.length() == 0) {
			return "***";
		}
		
		if (src.length() <= length) {
			String dst = "";
			for (int i = 0; i < src.length(); i++) {
				dst+="*";
			}
			return dst;
		}
		else if (src.length() <= (length + 4)) {
			String dst = "****" + src.substring(4, src.length());
			return dst;
		}
		else {
			String dst =  src.substring(0, src.length() - 4 - 4) + "****" + src.substring(src.length() - 4, src.length());			
			return dst;
		}
	}


	/**
	 * 根据日期获得星期
	 *
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate(Date date) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
				"星期六" };
		// String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
	public static String getWeekOfDateS(Date date) {
		String[] weekDaysName = { "周日", "周一", "周二", "周三", "周四", "周五",
				"周六" };
		// String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}

	/**
	 * 判断两个日期是否在同一周
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	public static int calculateLength(String c) {
		if(Util.isEmpty(c))
			return 0;
		int len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 1;
			} else {
				len += 2;
			}
		}
		return len;
	}

	// 获取文件扩展名
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	// 获取不带扩展名的文件名
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static boolean isSDcardExist() {
		return MultiCard.getInstance().isSDCardExist();
	}

		/**
	 * InputStream转String
	 *
	 * @param is
	 * @return
	 */
	public static String inputStream2String(InputStream is) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 过滤字符串非数字，一般用于电话号码过滤
	 *
	 * @param str
	 *            :需要过滤的字符串
	 * @return:返回过滤后的字符串
	 */
	public static String filterUnNumber(String str) {
		if (str == null || str.length() <= 0) {
			return str;
		}
		// 只允数字
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		// 替换与模式匹配的所有字符（即非数字的字符将被""替换）
		return m.replaceAll("").trim();
	}

	/**
	 * 电话号码过滤
	 *
	 * @param str
	 *            :需要过滤的字符串
	 * @return:返回过滤后的字符串
	 */
	public static String filterUnPhoneNumber(String str) {
		if (str == null || str.length() <= 0) {
			return str;
		}
		String NUM = "+86";
		if (str.indexOf(NUM) > -1) {
			str = str.substring(NUM.length(), str.length());
		}
		// 只允数字
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		// 替换与模式匹配的所有字符（即非数字的字符将被""替换）
		return m.replaceAll("").trim();
	}

	/**
	 * 验证是否为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 验证手机号码位数是否正确
	 *
	 * @param number
	 * @return
	 */
	public static boolean isRightNumbers(String number) {
		if (null == number)
			return false;
		if (number.trim().length() != 7 && number.trim().length() != 8
				&& number.trim().length() != 11 && number.trim().length() != 12
				&& number.trim().length() != 14)
			return false;
		return true;
	}

	/**
	 * 验证是否是手机号码
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		String NUM = "+86";
		boolean flag = false;
		if (TextUtils.isEmpty(str)) {
			return flag;
		} else {
			if (str.indexOf(NUM) > -1) {
				str = str.substring(NUM.length(), str.length());
			}
			if (str.charAt(0) == '0') {
				str = str.substring(1, str.length());
			}
//			String rex = "^1[3,5,8]\\d{9}$";
			String rex = "^(1(([35][0-9])|[4][57]|[8][01236789]))\\d{8}$";//最新手机号正则
			// String rex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
			str = removeBlanks(str);
			if (str.matches(rex)) {
				flag = true;
			}
			return flag;
		}
	}
	/**
	 * 简单判断一个固话有没有区号
	 * @param fixNumber
	 * @return
	 */
	public static boolean isFixNumberHasCityCode(String fixNumber){
		if(isMobile(fixNumber))
			return false;
		String number = filterUnNumber(fixNumber);
		if(number.length()==7 || number.length() == 8)
			return false;
		return true;
	}

	/**
	 * 删除字符串中的空白符
	 *
	 * @param content
	 * @return String
	 */
	public static String removeBlanks(String content) {
		if (content == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer();
		buff.append(content);
		for (int i = buff.length() - 1; i >= 0; i--) {
			if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i))
					|| ('\t' == buff.charAt(i)) || ('\r' == buff.charAt(i))) {
				buff.deleteCharAt(i);
			}
		}
		return buff.toString();
	}

	/**
	 * 获取长宽都不超过160dip的图片，基本思想是设置Options.inSampleSize按比例取得缩略图
	 */
	public static Options getOptionsWithInSampleSize(String filePath,
			int maxWidth) {
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;// 只取得outHeight(图片原始高度)和
		// outWidth(图片的原始宽度)而不加载图片
		BitmapFactory.decodeFile(filePath, bitmapOptions);
		bitmapOptions.inJustDecodeBounds = false;
		int inSampleSize = bitmapOptions.outWidth / (maxWidth / 10);// 应该直接除160的，但这里出16是为了增加一位数的精度
		if (inSampleSize % 10 != 0) {
			inSampleSize += 10;// 尽量取大点图片，否则会模糊
		}
		inSampleSize = inSampleSize / 10;
		if (inSampleSize <= 0) {// 判断200是否超过原始图片高度
			inSampleSize = 1;// 如果超过，则不进行缩放
		}
		bitmapOptions.inSampleSize = inSampleSize;
		return bitmapOptions;
	}

	/**
	 * 从文件获取Bitmap
	 *
	 * @param path
	 *            文件路径
	 * @param dstMaxWH
	 *            大小
	 */
	public static Bitmap getBitmapWithPath(String path, int dstMaxWH) {
		File srcFile = new File(path);

		// 路径文件不存在
		if (!srcFile.exists()) {
			return null;
		}

		try {
			// 打开源文件
			Bitmap srcBitmap;
			{
				java.io.InputStream is;
				is = new FileInputStream(srcFile);

				BitmapFactory.Options opts = getOptionsWithInSampleSize(
						srcFile.getPath(), dstMaxWH);
				srcBitmap = BitmapFactory.decodeStream(is, null, opts);
				is.close();
				if (srcBitmap == null)
					return null;
				else {
					return srcBitmap;
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置对比度矩阵
	 */
	private static void setContrast(ColorMatrix cm, float contrast) {
		float scale = contrast + 1.f;
		float translate = (-.5f * scale + .5f) * 255.f;
		cm.set(new float[] { scale, 0, 0, 0, translate, 0, scale, 0, 0,
				translate, 0, 0, scale, 0, translate, 0, 0, 0, 1, 0 });
	}
	
	public static String getImsi(Context mContext) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getSubscriberId();
	}

	public static String getImei(Context mContext) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId();
	}

	public static State getMobileNetState(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();

		return mobile;
	}

	/**
	 * 是否联通号码
	 * @param num
	 * @return
	 */
	public static boolean isUnicomMobile(String num) {
		if(Util.isEmpty(num)) {
			return false;
		}
		if(!isMobile(num)) {
			return false;
		}
		String phone = num.substring(num.length() - 11, num.length());
		String regex = "^(130|131|132|155|156|186|185)[0-9]{8}$";
		if(phone.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否移动号码
	 * @param num
	 * @return
	 */
	public static boolean isCmccMobile(String num) {
		if(Util.isEmpty(num)) {
			return false;
		}
		if(!isMobile(num)) {
			return false;
		}
		String phone = num.substring(num.length() - 11, num.length());
		String regex = "^(134|135|136|137|138|139|147|150|151|152|157|158|159|182|183|187|188)[0-9]{8}$";
		if(phone.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}
}
