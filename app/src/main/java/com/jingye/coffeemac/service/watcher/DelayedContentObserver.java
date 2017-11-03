package com.jingye.coffeemac.service.watcher;

import com.jingye.coffeemac.util.log.LogUtil;

import android.os.Handler;

public class DelayedContentObserver extends BufferedContentObserver {
	/** delayed time */
	private long delayedTime;
	
	/** handler */
	private Handler handler;
	
    private final class DelayedRunnable implements Runnable {

        private boolean mSelf;

        public DelayedRunnable(boolean self) {
            mSelf = self;
        }

        public void run() {
        	handleChangeDelayed(mSelf);
        }
    }
	
	public DelayedContentObserver(Handler handler, int delayedTime) {
		super(handler, delayedTime);
	
		this.handler = handler != null ? handler : new Handler();
		this.delayedTime = delayedTime;
	}
	
	/**
	 * on changed delayed
	 * 
	 * @param selfChange
	 */
	public void onChangeDelayed(boolean selfChange) {
		// Do nothing.  Subclass should override.
	}
	
	@Override
	public void onChangeBuffered(boolean selfChange) {
		LogUtil.d("DelayedContentObserver", "on change: " + " delay " + delayedTime);
		
		handler.postDelayed(new DelayedRunnable(selfChange), delayedTime);
	}
	
	private void handleChangeDelayed(boolean selfChange) {
		LogUtil.d("DelayedContentObserver", "handle change");
		
		onChangeDelayed(selfChange);
	}
}
