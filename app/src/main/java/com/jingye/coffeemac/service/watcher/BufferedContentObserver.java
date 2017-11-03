package com.jingye.coffeemac.service.watcher;

import com.jingye.coffeemac.util.log.LogUtil;

import android.database.ContentObserver;
import android.os.Handler;

public abstract class BufferedContentObserver extends ContentObserver {
	/** buffered time */
	private long bufferedTime;
	
	/** last time observe change */
	private long last;
	
	public BufferedContentObserver(Handler handler, int bufferedTime) {
		super(handler);
		
		this.bufferedTime = bufferedTime;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		// current
		long current = System.nanoTime() / 1000000;
		
		// elapse
		long elapse = current - last;
		boolean discard = elapse < bufferedTime;
		LogUtil.d("BufferedContentObserver", "receive change: " + " elapse " + elapse + " buffered " + bufferedTime + " discard " + discard);

		if (!discard) {
			// update
			last = current;
			
			// fire
			onChangeBuffered(selfChange);
		}
	}
	
	/**
	 * on changed buffered
	 * 
	 * @param selfChange
	 */
	public void onChangeBuffered(boolean selfChange) {
		// Do nothing.  Subclass should override.
	}
}
