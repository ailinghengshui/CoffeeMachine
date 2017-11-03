package com.jingye.coffeemac.common.action;

import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.log.LogUtil;

import android.os.Handler;

public abstract class RemoteProxy {
	public abstract void onReceive(Remote remote);
			
	private Handler handler;
	
	public void bind(boolean bind) {
		if (handler != null) {
			trace("unbind" + " handler#" + handler);

			TViewWatcher.newInstance().unBindView(handler);

			handler = null;
		}	
		
		if (bind) {
			handler = new RemoteHandler(this);
			
			trace("bind" + " handler#" + handler);
			
			TViewWatcher.newInstance().bindView(handler);
		}
	}
	
	/* package */void receive(Handler handler, Remote remote) {
		boolean accept = handler == this.handler;
		
		if (accept) {
			onReceive(remote);
		} else {
			trace("reject" + " bound#" + this.handler + " handler#" + handler);	
		}
	}
	
	private static final void trace(String message) {
		LogUtil.d("RemoteProxy", message);
	}
}
