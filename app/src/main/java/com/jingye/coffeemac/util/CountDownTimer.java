package com.jingye.coffeemac.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class CountDownTimer {

	private Handler countDownHander;

	private CountDownCallback callback;

	private static final int UI_MSG_COUNT_DOWN_TIMER = 1;

	private int totalTimes;

	private Timer countDownTimer;

	public CountDownTimer(CountDownCallback callback) {
		this.callback = callback;
		initHander();
	}

	private void initHander() {
		countDownHander = new Handler(Looper.getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case UI_MSG_COUNT_DOWN_TIMER: {
					--totalTimes;
					cancelTimerWhenIntervelFinshed();
					currentIntervel(totalTimes);
					break;
				}
				}
			}

			private void cancelTimerWhenIntervelFinshed() {
				if (totalTimes <= 0) {
					cancelCountDownTimer();
				}
			}
		};
	}

	/**
	 * 开始倒数计时
	 */
	public void startCountDownTimer(int totalTimes, int delay, int period) {
		cancelCountDownTimer();
		if (countDownTimer == null) {
			countDownTimer = new Timer();
		}
		this.totalTimes = totalTimes;
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				if (countDownHander != null) {
					Message message = Message.obtain();
					message.what = UI_MSG_COUNT_DOWN_TIMER;
					countDownHander.sendMessage(message);
				}
			}
		};
		currentIntervel(totalTimes);
		countDownTimer.schedule(timerTask, delay, period);
	}

	/**
	 * 取消倒数计时
	 */
	public void cancelCountDownTimer() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
			countDownTimer = null;
		}
	}

	protected void currentIntervel(int currentTimes) {
		if (callback != null) {
			callback.currentInterval(currentTimes);
		}
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public interface CountDownCallback {

		public void currentInterval(int value);
	}
}
