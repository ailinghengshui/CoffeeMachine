package com.jingye.coffeemac.ui;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDlgHelper {
	
	private static ProgressDialog mProgress;
	
	public static void showProgress(Context context, CharSequence message){
		closeProgress();
		mProgress = new ProgressDialog(context);
		mProgress.setMessage(message);
		mProgress.setIndeterminate(false);
		mProgress.setCancelable(false);
//		mProgress.setButton("取消", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.cancel();
//			}
//		});

		mProgress.show();
	}
	
	public static void setMessage(CharSequence message){
		mProgress.setMessage(message);
	}
	
	public static void closeProgress(){
    	if( mProgress != null && mProgress.isShowing()){
    		mProgress.dismiss();
    		mProgress = null;
    	}
    }
	
	public static boolean isShowing(){
		if(mProgress != null && mProgress.isShowing())
			return true;
		else
			return false;
	}
}
