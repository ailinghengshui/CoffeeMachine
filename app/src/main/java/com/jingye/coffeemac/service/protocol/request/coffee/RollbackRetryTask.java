package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.bean.result.RollbackFetchCodeResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.log.LogUtil;

public class RollbackRetryTask extends ResendRequestTask {

	public RollbackRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {
		LogUtil.vendor("[CoffeeAction] rollbackFetchCoffeeByCode->timeout");

		RollbackFetchCodeResult result = new RollbackFetchCodeResult();
		result.setResCode(ResponseCode.RES_ETIMEOUT);
		core.notifyListener(result.toRemote());
	}
}
