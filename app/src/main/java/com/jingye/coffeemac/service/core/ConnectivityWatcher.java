package com.jingye.coffeemac.service.core;

import com.jingye.coffeemac.net.client.NetworkEnums;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityWatcher {

	public interface Callback {
		public void onNetworkEvent(NetworkEnums.Event event);
	}
	
	Callback mCallback;
	Context mContext;	
	
	private boolean mAvailable;
	private String mTypeName;

	ConnectivityWatcher(Context context, Callback callback) {
		mContext = context;
		mCallback = callback;
	}
	
	public boolean isAvailable() {
		return mAvailable;
	}
	
	public void startup() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();
		mAvailable = (info != null && info.isAvailable());
		mTypeName = mAvailable ? info.getTypeName() : null;
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
			
		mContext.registerReceiver(mReceiver, filter);
	}
	
	public void shutdown() {
		mContext.unregisterReceiver(mReceiver);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			String action = intent.getAction();	
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				boolean available = info != null && info.isAvailable();
				String typeName = available ? info.getTypeName() : null;
				if (mAvailable != available) {			
					// update
					mAvailable = available;
					mTypeName = typeName;

					// notify
					onAvailable(available);
				} else if (mAvailable) {
					if (!typeName.equals(mTypeName)) {
						// update
						mTypeName = typeName;

						// notify
						notifyEvent(NetworkEnums.Event.NETWORK_CHANGE);
					}
				}
			} else if (action.equals(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED)) {
				onBackgroundData(cm.getBackgroundDataSetting());
			}
		}
	};
	
	private void onBackgroundData(boolean on) {
		notifyEvent(on ? NetworkEnums.Event.BACKGROUND_DATA_ON : NetworkEnums.Event.BACKGROUND_DATA_OFF);
	}
	
	private void onAvailable(boolean available) {
		if (available) {
			notifyEvent(NetworkEnums.Event.NETWORK_AVAILABLE);
		} else {
			notifyEvent(NetworkEnums.Event.NETWORK_UNAVAILABLE);
		}
	}

	private void notifyEvent(NetworkEnums.Event event) {
		if (mCallback != null) {
			mCallback.onNetworkEvent(event);
		}
	}
}
