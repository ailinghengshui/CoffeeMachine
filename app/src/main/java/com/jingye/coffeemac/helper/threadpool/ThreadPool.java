package com.jingye.coffeemac.helper.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jingye.coffeemac.util.log.LogUtil;


/**
 * 线程池 创建线程池，销毁线程池，添加新任务
 * 
 */
public final class ThreadPool {
	/* 单例 */
	private static ThreadPool instance;

	private static int corePoolSize = 3; // 指的是保留的线程池大小。
	private static int maximumPoolSize = 5; // 指的是保留的线程池最大大小。
	private static long keepAliveTime = 1000 * 30; // 指的是空闲线程结束的超时时间。(空闲线程存放30s)
	private static int workQueueCount = 5; // 表示存放任务的队列。

	private ThreadPoolExecutor executor = null;

	private ThreadPool() {
		init();
	}

	public static synchronized ThreadPool getInstance() {
		if (instance == null) {
			instance = new ThreadPool();
		}
		
		return instance;
	}

	public void init() {
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(workQueueCount),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		/**
		 * ThreadPoolExecutor.AbortPolicy：表示拒绝任务并抛出异常
		 * ThreadPoolExecutor.DiscardPolicy：表示拒绝任务但不做任何动作
		 * ThreadPoolExecutor.CallerRunsPolicy：表示拒绝任务，并在调用者的线程中直接执行该任务
		 * ThreadPoolExecutor.DiscardOldestPolicy：表示先丢弃任务队列中的第一个任务，然后把这个任务加进队列
		 */
	}

	/**
	 * 增加新的任务 每增加一个新任务，都要唤醒任务队列
	 * 
	 * @param newTask
	 */
	public void addTask(Runnable newTask) {

		LogUtil.v("ThreadPool", "new task start ");
		if (executor != null) {
			if (executor.isShutdown()) {
				executor.prestartAllCoreThreads(); // FIXME 没有用
			}
			LogUtil.v("ThreadPool", "new task ");
			executor.execute(newTask);
		}
	}

	/**
	 * 销毁线程池
	 */
	public synchronized void destroy() {

		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
			executor = null;
		}
	}

	public void purge() {
		if (executor != null) {
			executor.purge();
		}
	}

	public void shutdown() {
		if (executor != null) {
			executor.shutdown();
		}
	}

}