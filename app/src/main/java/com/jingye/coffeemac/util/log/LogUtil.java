package com.jingye.coffeemac.util.log;

import com.jingye.coffeemac.application.AppConfig;

public class LogUtil {
	
	protected static final boolean isDebug = AppConfig.isDebugMode();
	
	public static void v(String tag,String msg) {
		if(isDebug){
			LectekLog.v(tag, buildMessage(msg));
		}
	}
	
    public static void systemOut(String msg){
    	v("vendor", msg);
    }
    
	public static void v(String tag,String msg, Throwable thr) {
		if(isDebug){
			LectekLog.v(tag, buildMessage(msg),thr);
		}
	}

	public static void d(String tag,String msg) {
		if(isDebug){
			LectekLog.d(tag, buildMessage(msg));
		}
	}

	public static void d(String tag,String msg, Throwable thr) {
		if(isDebug){
			LectekLog.d(tag, buildMessage(msg),thr);
		}
	}

	public static void i(String tag,String msg) {
		if(isDebug){
			LectekLog.i(tag, buildMessage(msg));
		}
	}

	public static void i(String tag,String msg, Throwable thr) {
		if(isDebug){
			LectekLog.i(tag, buildMessage(msg),thr);
		}
	}

	public static void w(String tag,String msg) {
		if(isDebug){
			LectekLog.w(tag, buildMessage(msg));
		}
	}

	public static void w(String tag,String msg, Throwable thr) {
		if(isDebug){
			LectekLog.w(tag, buildMessage(msg), thr);
		}
	}

	public static void w(String tag,Throwable thr) {
		if(isDebug){
			LectekLog.w(tag, buildMessage(""), thr);
		}
	}
	
	public static void e(String tag,String msg) {
		if(isDebug){
			LectekLog.e(tag, buildMessage(msg));
		}
	}

	public static void e(String tag,String msg, Throwable thr) {
		if(isDebug){
			LectekLog.e(tag, buildMessage(msg), thr);
		}
	}
	
	public static void vendor(String msg){
		if(isDebug){
			LectekLog.d("vendor", msg);
		}
	}
	
	public static void core(String msg){
		if(isDebug){
			LectekLog.d("core", msg);
		}
	}
	
	public static void report(String msg){
		if(isDebug){
			LectekLog.d("report", msg);
		}
	}
	
	private static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];
		String className = caller.getClassName();
		String simpleClassName = className.substring(className.lastIndexOf(".")+1);
		return new StringBuilder().append(simpleClassName).append(".")
				.append(caller.getMethodName()).append("(").append(caller.getLineNumber()).append("):").append(msg)
				.toString();
	}
}
