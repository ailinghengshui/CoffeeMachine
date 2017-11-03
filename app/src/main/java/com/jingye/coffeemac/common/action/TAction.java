package com.jingye.coffeemac.common.action;

import com.jingye.coffeemac.service.Remote;

public abstract class TAction implements IAction{
	/**
	 * 该观察者提供两个主要方法
	 * 1. 收到消息经过action处理后上送到页面
	 * 2. 向服务层提交消息 send
	 */
	protected TViewWatcher watcher = TViewWatcher.newInstance();
	
	/**
	 * 发送数据到服务层
	 * @param remote
	 * @return
	 */
	public boolean send(Remote remote){
		return watcher.send(remote);
	}
	
	/**
	 * 发送数据到UI层
	 * @param remote
	 */
	public void notifyAll(Remote remote){
		watcher.notifyAll(remote);
	}
}
