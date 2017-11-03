
package com.jingye.coffeemac.util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.util.CommonUtil;
import com.jingye.coffeemac.util.StorageUtil;

import android.content.Context;

public class AppErrorLogHandler implements UncaughtExceptionHandler {

    private static final String LOG_FILE_NAME = "AppErrorLog.log";
    
    private static final int LOG_MAX_SIZE = 1024 * 1024;//1M

    private UncaughtExceptionHandler mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    private Context mContext;
    
    private static AppErrorLogHandler instance;

    private AppErrorLogHandler(Context mContext) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mContext = mContext;
    }

    public static AppErrorLogHandler getInstance(Context mContext) {
        if (instance == null) {
            instance = new AppErrorLogHandler(mContext);
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        saveCrashInfoToFile(ex);
        mUncaughtExceptionHandler.uncaughtException(thread, ex);
	}

    private void saveCrashInfoToFile(final Throwable ex) {
    	new Thread(){
    		public void run() {
    			 if(!CommonUtil.isSDcardExist()){//如果没有sdcard，则不存储
    		            return;
    		        }
    		        Writer writer = null;
    		        PrintWriter printWriter = null;
    		        String stackTrace = "";
    		        try {
    		            writer = new StringWriter();
    		            printWriter = new PrintWriter(writer);
    		            ex.printStackTrace(printWriter);
    		            Throwable cause = ex.getCause();
    		            while (cause != null) {
    		                cause.printStackTrace(printWriter);
    		                cause = cause.getCause();
    		            }
    		            stackTrace = writer.toString();
    		        } catch (Exception e) {
    		        }finally{
    		            if(writer!=null){
    		                try {
    		                    writer.close();
    		                } catch (IOException e) {
    		                    e.printStackTrace();
    		                }
    		            }
    		            if(printWriter!=null){
    		                printWriter.close();
    		            }
    		        }
    		        StringBuilder sb = new StringBuilder();
    		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    		        Date date = new Date();
    		        String timestamp = sdf.format(date);
    		        sb.append(">>>>>>>>>>>>>>>>>>>>>>");
    		        sb.append(timestamp);//记录每个error的发生时间
    		        sb.append(System.getProperty("line.separator"));
    		        sb.append(stackTrace);
    		        sb.append(System.getProperty("line.separator"));//每个error间隔2行
    		        sb.append(System.getProperty("line.separator"));
    		        BufferedWriter mBufferedWriter = null;
    		        try {
    		            File mFile = new File(StorageUtil.getWritePathIgnoreError(MyApplication.Instance().getApplicationContext(), LOG_FILE_NAME));
    		            File pFile = mFile.getParentFile();
    		            if(!pFile.exists()){//如果文件夹不存在，则先创建文件夹
    		                pFile.mkdirs();
    		            }
    		            if (mFile.length() > LOG_MAX_SIZE) {//如果超过最大文件大小，则重新创建一个文件
    		                mFile.delete();
    		                mFile.createNewFile();
    		            }
    		            mBufferedWriter = new BufferedWriter(new FileWriter(mFile,true));//追加模式写文件
    		            mBufferedWriter.append(sb.toString());
    		            mBufferedWriter.flush();
    		        } catch (IOException e) {
    		        } finally {
    		            if (mBufferedWriter != null) {
    		                try {
    		                    mBufferedWriter.close();
    		                } catch (IOException e) {
    		                }
    		            }
    		        }
    		};
    	}.start();
    }
}
