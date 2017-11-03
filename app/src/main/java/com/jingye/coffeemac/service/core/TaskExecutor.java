package com.jingye.coffeemac.service.core;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskExecutor {
	private final static int QUEUE_INIT_CAPACITY = 11;
	
	private String mName;
	private int mCore;
	private int mMax;
	private int mTimeout;
	private ExecutorService mExecutor;
	
	public TaskExecutor(String name, int core, int max, int timeout) {
		mName = name;
		mCore = core;
		mMax = max;
		mTimeout = timeout;
	}	

	public void startup() {
		synchronized (this) {
			// has startup
			if (mExecutor != null && !mExecutor.isShutdown()) {
				return;
			}
			
			// create
			mExecutor = createExecutor();
		}
	}
	
	public void shutdown() {
		ExecutorService executor = null;
		
		synchronized (this) {
			// swap
			if (mExecutor != null) {
				executor = mExecutor;
				mExecutor = null;
			}
		}
		
		if (executor != null) {
			// shutdown
			if (!executor.isShutdown()) {
				executor.shutdown();
			}
		
			// recycle
			executor = null;
		}
	}
	
	public void execute(Runnable runnable) {
		synchronized (this) {		
			// has shutdown, reject
			if (mExecutor == null || mExecutor.isShutdown()) {
				return;
			}
	
			// execute
			mExecutor.execute(runnable);
		}
	}
	
	public void execute(Runnable runnable, int priority) {
		// execute priority runnable
		execute(new PRunnable(runnable, priority));
	}
	
	private ExecutorService createExecutor() {
		return new ThreadPoolExecutor(mCore, mMax,
				mTimeout, TimeUnit.MILLISECONDS,
				new PriorityBlockingQueue<Runnable>(QUEUE_INIT_CAPACITY, mQueueComparator), 
				new TaskThreadFactory(mName), 
				new ThreadPoolExecutor.DiscardPolicy());		
	}
	
	private class PRunnable implements Runnable {
		private Runnable runnable;
		private int priority;
		
		public PRunnable(Runnable r, int p) {
			runnable = r;
			priority = p;
		}
		
		@Override
		public void run() {
			if (runnable != null) {
				runnable.run();
			}
		}
	}
	
	private static int priorityFromRunnable(Runnable r) {
		if (r instanceof PRunnable) {
			return ((PRunnable)r).priority;
		} else {
			return Priority.DEFAULT_PRIORITY;
		}
	}
	
	Comparator<Runnable> mQueueComparator = new Comparator<Runnable>() {

		@Override
		public int compare(Runnable r1, Runnable r2) {
			int p1 = priorityFromRunnable(r1);
			int p2 = priorityFromRunnable(r2);
			
			return p2 - p1;
		}
	};
	
	static class TaskThreadFactory implements ThreadFactory {
		private final ThreadGroup mThreadGroup;
		private final AtomicInteger mThreadNumber = new AtomicInteger(1);
		private final String mNamePrefix;

		TaskThreadFactory(String name) {
			SecurityManager s = System.getSecurityManager();

			mThreadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			
			mNamePrefix = name + "#";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(mThreadGroup, r, mNamePrefix + mThreadNumber.getAndIncrement(), 0);

			// no daemon
			if (t.isDaemon())
				t.setDaemon(false);
			
			// normal priority
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			
			return t;
		}
	}
}