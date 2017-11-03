package com.jingye.coffeemac.action;

import com.jingye.coffeemac.common.action.TAction;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.log.LogUtil;

public class UserAction extends TAction {

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		LogUtil.vendor("UserAction receive...");
		notifyAll(remote);
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_USER;
	}
}
