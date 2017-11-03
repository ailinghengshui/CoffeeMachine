package com.jingye.coffeemac.action;

import com.jingye.coffeemac.common.action.TAction;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.protocol.ResponseCode;

public class LoginAction extends TAction {

	@Override
	public void execute(Remote remote) {

	}

	@Override
	public void receive(Remote remote) {
		if (remote.getAction() == ITranCode.ACT_USER_LOGIN) {
			GeneralActionResult result = GeneralActionResult.parseObject(remote.getBody());
			if (result.getResCode() == ResponseCode.RES_SUCCESS) {
				// TODO SOMETHING
			}
		}
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_USER;
	}

}
