package com.jingye.coffeemac.action;

import com.jingye.coffeemac.activity.LoginActivity;
import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TAction;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.Intent;

import java.io.InputStream;
import java.io.OutputStream;

public class SysAction extends TAction {

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
			onStatusChange(remote);
		}else if(remote.getAction() == ITranCode.ACT_SYS_REBOOT){
            LogUtil.vendor("DO REBOOT COFFEE MACHINE");

            if(!MyApplication.Instance().isMakingCoffee()){
				LogUtil.vendor("Reboot coffee machine");
                String cmd = "su -c reboot";
                try {


//					Process process=Runtime.getRuntime().exec("/system/bin/su");
//					OutputStream out=process.getOutputStream();
//					out.write(("reboot ").getBytes());
//					InputStream in=process.getInputStream();
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

		notifyAll(remote);
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_SYS;
	}

	private boolean onStatusChange(Remote remote) {
		StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
		if (notify.getStatus() == ITranCode.STATUS_KICKOUT) {

			Intent intent = new Intent();
			intent.setClass(MyApplication.Instance().getApplicationContext(), LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("status", notify.getStatus());
			intent.putExtra("vendor", GlobalCached.activeVendor);
			MyApplication.Instance().getApplicationContext().startActivity(intent);

			// 清空全局缓存
			GlobalCached.clear();

			return false;
		}

		return true;
	}
}
