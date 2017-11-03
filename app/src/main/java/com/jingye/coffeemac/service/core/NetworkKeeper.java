package com.jingye.coffeemac.service.core;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.jingye.coffeemac.net.client.NetworkEnums;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.Context;

public class NetworkKeeper {
    private final int RECONNECT_TIMEOUT_THRESTHOD = 120;
	ConnectivityWatcher mConnectivityWatcher;
	VendorCore vendorCore = VendorCore.sharedInstance();
	private Timer keepaliveTimer; // 检查服务器心跳的定时器, 同时也是后台保活的定时器
	private Timer reconnectTimer; // 自动重连定时器
	private AtomicInteger reconnectCounter = new AtomicInteger(); // 重连计数

	public NetworkKeeper(Context context) {
		mConnectivityWatcher = new ConnectivityWatcher(context,
				new ConnectivityWatcher.Callback() {

					@Override
					public void onNetworkEvent(NetworkEnums.Event event) {
						notifyEvent(event);
					}
				});
	}

	public void startWork() {
		mConnectivityWatcher.startup();
	}

	public void stopWork() {
		mConnectivityWatcher.shutdown();

		stopKeepaliveTimer();

		stopReconnect();
	}

	public boolean isReachable() {
		return mConnectivityWatcher.isAvailable();
	}

	public void startReconnect() {
		if (reconnectTimer != null)
			return;

//		int random = new Random().nextInt(10);
		int random=0;
		final int RECONNECT_PERIOD = (10 + random) * 1000;
		reconnectCounter.set(0);
		reconnectTimer = new Timer();
		TimerTask reconnectTask = new TimerTask() {

			@Override
			public void run() {
				if (shouldReconnect(reconnectCounter.incrementAndGet()) ) {
                    LogUtil.vendor("[DEBUG]shouldReconnect is true" );
                    if(reconnectCounter.get() >= RECONNECT_TIMEOUT_THRESTHOD){
						vendorCore.requestReboot();
                        return;
                    }
					// 如果网络没有恢复或者已经连上，就不重连了
					if (!mConnectivityWatcher.isAvailable()) {
						LogUtil.vendor("network is not available");
						return;
					}
					if (vendorCore.isConnected()) {
						LogUtil.vendor("we has connected to the server, reconnect not needed");
						return;
					}
					vendorCore.logout();
					vendorCore.login(true);
					LogUtil.vendor("relogin because of reconnect timer");
				}
			}
		};
		reconnectTimer.schedule(reconnectTask, (5 + random / 2) * 1000, RECONNECT_PERIOD);
	}
	
	private boolean shouldReconnect(int count) {
        LogUtil.vendor("[DEBUG] shouldReconnect count = " + count);
		if(count % 2 == 1){
			return true;
		}else{
			return false;
		}
	}

	public void stopReconnect() {
		if (reconnectTimer != null) {
			reconnectTimer.cancel();
			reconnectTimer = null;
		}
	}

	private void stopKeepaliveTimer() {
		if (keepaliveTimer != null) {
			keepaliveTimer.cancel();
			keepaliveTimer = null;
		}
	}

	private void notifyEvent(NetworkEnums.Event event) {
		vendorCore.onNetworkEvent(event);
	}
}
