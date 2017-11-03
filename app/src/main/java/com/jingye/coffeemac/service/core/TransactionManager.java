package com.jingye.coffeemac.service.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;



import com.jingye.coffeemac.service.protocol.request.Request;

import android.util.SparseArray;

public class TransactionManager {
	/** requests */
	private SparseArray<Request> mRequests = new SparseArray<Request>();

	/** timeouts */
	private SparseArray<Timeout> mTimeouts = new SparseArray<Timeout>();
	
	/** timer */
	private HashedWheelTimer mTimer;
	
	private final int STATE_STOP = 0;
	private final int STATE_RUNNING = 1;	
	
	/** state */
	private AtomicInteger mState = new AtomicInteger(STATE_STOP);
	
	public TransactionManager() {
		
	}
	
	/**
	 * startup
	 */
	public void startup() {
		// has startup
		if (!mState.compareAndSet(STATE_STOP, STATE_RUNNING)) {
			return;
		}
		
		// create timer
		mTimer = new HashedWheelTimer(500, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * shutdown
	 */
	public void shutdown() {
		// has startup
		if (!mState.compareAndSet(STATE_RUNNING, STATE_STOP)) {
			return;
		}

		// clear
		mRequests.clear();
		mTimeouts.clear();
		
		// stop
		mTimer.stop();

		// recycled
		mTimer = null;
	}
	
	private boolean isRunning() {
		return mState.get() == STATE_RUNNING;
	}
	
	/**
	 * pend a request 
	 * 
	 * @param request request
	 */
	public void pend(Request request) {
		// check
		if (!isRunning()) {
			return;
		}
		
		// check
		if (request == null) {
			return;
		}
		
		// pend
		pendRequest(request);
	}

	/**
	 * pend a request and setup a timer with timeout delay and retry action and count
	 * @param request request
	 * @param action timeout action
	 * @param delay timeout delay
	 * @param retry retry count
	 */
	public void pend(Request request, ResendRequestTask action, int delay, int retry) {
		// check
		if (!isRunning()) {
			return;
		}
		
		// check
		if (request == null) {
			return;
		}
		
		// request
		requestTimeout(newTimerTaskWithInfo(request, action, delay, retry));
	}

	/**
	 * revoke with a id
	 * @param id 
	 * @return action
	 */
	public ResendRequestTask revoke2(int id) {
		// check
		if (!isRunning()) {
			return null;
		}
		
		// revoke
		TimerTaskWithInfo taskWithInfo = revokeTimeout(id, false);
		
		return taskWithInfo == null ? null : taskWithInfo.action;
	}
	
	/**
	 * revoke with a id
	 * @param id 
	 * @return action
	 */
	public Request revoke(int id) {
		// check
		if (!isRunning()) {
			return null;
		}
		
		// revoke
		TimerTaskWithInfo taskWithInfo = revokeTimeout(id, false);
		
		return taskWithInfo == null ? null : taskWithInfo.request;
	}
	
	private void pendRequest(Request request) {
		// as request serial id
		int key = request.getLinkFrame().serialId;
		
		synchronized (mRequests) {
			mRequests.put(key, request);			
		}
	}
		
	private void requestTimeout(TimerTaskWithInfo taskWithInfo) {
		synchronized (mTimeouts) {
			// new timeout
			mTimeouts.put(taskWithInfo.getKey(), mTimer.newTimeout(taskWithInfo, taskWithInfo.delay, TimeUnit.SECONDS));
		}	
	}

	private TimerTaskWithInfo revokeTimeout(int key, boolean isTimeout) {
		/**
		 * get and remove timeout
		 */
		Timeout timeout = null;
		synchronized (mTimeouts) {
			timeout = mTimeouts.get(key);
			
			if (timeout != null) {
				mTimeouts.remove(key);
			}
		}	
		
		/**
		 * has and is not from timeout, cancel
		 */
		if (timeout != null && !isTimeout) {
			timeout.cancel();
		}
		
		// return task
		return timeout == null ? null : (TimerTaskWithInfo) timeout.getTask();
	}
	
	private void onTimeout(Timeout timeout) {
		// check
		if (!isRunning()) {
			return;
		}
		
		// task from timeout
		TimerTaskWithInfo taskWithInfo = (TimerTaskWithInfo) timeout.getTask();
		
		// revoke and get task
		taskWithInfo = revokeTimeout(taskWithInfo.getKey(), true);
		
		/**
		 * when timeout, maybe has been revoked
		 */
		if (taskWithInfo == null) {
			return;
		}
		
		/**
		 * handle time out and request again if needed
		 */
		if (handleTimeout(taskWithInfo)) {
			requestTimeout(taskWithInfo);
		}
	}
	
	private class TimerTaskWithInfo implements TimerTask {
		/** request */
		Request request;

		/** action */
		ResendRequestTask action;

		/** count */
		int retry;
		
		/** delay */
		int delay;
		
		@Override
		public void run(Timeout timeout) throws Exception {
			// on timeout
			onTimeout(timeout);
		}
		
		public int getKey() {
			// as request serial id
			return request.getLinkFrame().serialId;
		}
	}
	
	private boolean handleTimeout(TimerTaskWithInfo taskWithInfo) {
		// action
		ResendRequestTask action = taskWithInfo.action;
	
		if (taskWithInfo.retry-- == 0) {
			// on timeout
			if (action != null) {
				action.onTimeout();
			}
			
			// reach max
			return false;
		} else {
			// send again default
			if (action == null) {
				ResendRequestTask.resend(taskWithInfo.request);
			}
			
			// whether retry
			return action == null ? true : action.retry();
		}
	}
	
	private TimerTaskWithInfo newTimerTaskWithInfo(Request request, ResendRequestTask action, int delay, int retry) {
		TimerTaskWithInfo task = new TimerTaskWithInfo();
		
		task.request = request;
		task.action = action;
		task.delay = delay;
		task.retry = retry;
		
		return task;		
	}
}
