package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.log.LogUtil;

public class FetchCoffeeByCodeRetryTask extends ResendRequestTask {

	public FetchCoffeeByCodeRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {
		LogUtil.vendor("FetchCoffeeByCodeRetryTask-> timeout");
		FetchCoffeeByCodeRequest request = (FetchCoffeeByCodeRequest)getRequest();
		String fetchCode = request.getFetchCode();
		LogUtil.vendor("FetchCoffeeByCodeRetryTask-> fetchCode is " + fetchCode);
		Remote remote = new Remote();
		remote.setWhat(ITranCode.ACT_COFFEE);
		remote.setAction(ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_CODE_TIME_OUT);
		remote.setBody(fetchCode);
		core.notifyListener(remote);
	}
}
