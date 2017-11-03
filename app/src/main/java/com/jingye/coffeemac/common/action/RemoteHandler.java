package com.jingye.coffeemac.common.action;

import java.lang.ref.WeakReference;

import com.jingye.coffeemac.service.Remote;

import android.os.Handler;
import android.os.Message;

public final class RemoteHandler extends Handler {
	private WeakReference<RemoteProxy> proxy;

	public RemoteHandler(RemoteProxy proxy) {
		this.proxy = new WeakReference<RemoteProxy>(proxy);
	}

	@Override
	public void handleMessage(Message message) {
		RemoteProxy proxy = this.proxy.get();
		
		if (proxy == null) {
			return;
		}

		proxy.receive(this, (Remote) message.obj);
	}
}
