package com.jingye.coffeemac.common.action;

import com.jingye.coffeemac.service.Remote;

/**
 * @author zhousq
 *
 */
public interface IAction {
	/**
	 * 来自页面层的提交，这里是页面逻辑层的重点内容
	 * @param remote
	 */
	public void execute(Remote remote);
	/**
	 * 来自服务层的推送消息
	 * @param remote
	 */
	public void receive(Remote remote);
	
	public int getKey();
}
