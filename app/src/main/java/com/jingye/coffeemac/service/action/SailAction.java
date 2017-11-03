package com.jingye.coffeemac.service.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.log.LogUtil;

public class SailAction extends TAction {

	@Override
	public void execute(final Remote remote) {
		if (remote.getAction() == ITranCode.ACT_SYS_FIRE) {
			LogUtil.vendor("启动做事情...");
		}
	}

	public int getWhat() {
		return ITranCode.ACT_SYS;
	}
}
