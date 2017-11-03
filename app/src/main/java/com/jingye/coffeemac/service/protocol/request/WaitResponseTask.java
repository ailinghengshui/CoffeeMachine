package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.core.ResendRequestTask;

public class WaitResponseTask extends ResendRequestTask {

	public WaitResponseTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {
		// do noting
	}

}
