package com.jingye.coffeemac.service.watcher;

import com.jingye.coffeemac.activity.SplashActivity;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoRunWatcher extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			LogUtil.vendor("ON ACTION BOOT_COMPLETED");
			Intent auto = new Intent();
			auto.setClass(context, SplashActivity.class);
			auto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(auto);
		}
	}
}