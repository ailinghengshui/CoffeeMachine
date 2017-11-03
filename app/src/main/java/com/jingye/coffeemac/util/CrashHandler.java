package com.jingye.coffeemac.util;
 
import java.lang.Thread.UncaughtExceptionHandler;


import com.jingye.coffeemac.service.VendorService;

import android.content.Context;
 
import android.os.Looper;
 
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {


	public static final boolean DEBUG = false;
	private static CrashHandler INSTANCE;
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext; 
 
	private CrashHandler() {}
 
	public static CrashHandler getInstance() { 
		if (INSTANCE == null) { 
			INSTANCE = new CrashHandler(); 
		} 
		return INSTANCE; 
	} 
	
 
	public void init(Context ctx) { 
		mContext = ctx; 
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler(); 
		Thread.setDefaultUncaughtExceptionHandler(this); 
	} 
	
 
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) { 
			mDefaultHandler.uncaughtException(thread, ex); 
		} else { 
		 
			try { 
				Thread.sleep(3000); 
			} catch (InterruptedException e) { 
				Log.e("严重错误", "Error : ", e); 
			} 
			
//			 final ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);    
//		     am.restartPackage(mContext.getPackageName());  
//
//			android.os.Process.killProcess(android.os.Process.myPid()); 
//			System.exit(10); 
			//这里必须要注意，万一出现启动过程中报错，会出现死循环，切记
			VendorService.stopService(mContext);
			VendorService.restartService(mContext);
		} 
	} 
 
	private boolean handleException(Throwable ex) { 
		if (ex == null) { 
			return true; 
		} 
		final String msg = ex.getLocalizedMessage(); 
		if(msg == null) {
			return false;
		}
		new Thread() { 
		 
			public void run() { 
				Looper.prepare(); 
				Toast toast = Toast.makeText(mContext, "翼信系统错误，即将重新启动服务",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				Looper.loop(); 
			} 
		}.start(); 
		 
		return true; 
	} 
	 
}

