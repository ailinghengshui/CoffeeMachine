package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.log.LogUtil;

public class ExchangeCoffeeByCodeRetryTask extends ResendRequestTask {

	public ExchangeCoffeeByCodeRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {
		LogUtil.vendor("ExchangeCoffeeByCodeRetryTask-> timeout");
		ExchangeCoffeeByCodeRequest request = (ExchangeCoffeeByCodeRequest)getRequest();
		String fetchCode = request.getFetchCode();
		LogUtil.vendor("ExchangeCoffeeByCodeRetryTask-> fetchCode is " + fetchCode);
		Remote remote = new Remote();
		remote.setWhat(ITranCode.ACT_COFFEE);
		remote.setAction(ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_CODE_TIME_OUT);
		remote.setBody(fetchCode);
		core.notifyListener(remote);
	}
}
