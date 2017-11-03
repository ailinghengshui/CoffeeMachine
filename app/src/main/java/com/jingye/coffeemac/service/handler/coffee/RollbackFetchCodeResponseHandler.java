package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.RollbackFetchCodeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.RollbackFetchCodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.RollbackRetryTask;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.RollbackFetchResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class RollbackFetchCodeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		RollbackRetryTask task = (RollbackRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		String fetchCode = null;
		long requestTimestamp = -1;
		if (task != null) {
			RollbackFetchCodeRequest request = (RollbackFetchCodeRequest)task.getRequest();
			if(request != null){
				fetchCode = request.getFetchCode();
				requestTimestamp = request.getTimestamp();
			}
		}

		RollbackFetchCodeResult result = new RollbackFetchCodeResult();
		result.setResCode(response.getLinkFrame().resCode);
		result.setFetchCode(fetchCode);

		if (response.isSuccess()) {
			RollbackFetchResponse rollbackFetchResponse = (RollbackFetchResponse) response;
			long serverTimestamp = rollbackFetchResponse.getTimestamp();
			LogUtil.vendor("RollbackFetchCodeResponseHandler-> serverTimestamp is " + serverTimestamp + "; requestTimestamp is" + requestTimestamp);
			if(requestTimestamp != serverTimestamp){
				LogUtil.vendor("RollbackFetchCodeResponseHandler-> serverTimestamp not equal to requestTimestamp");
				return;
			}
		}

		postToUI(result.toRemote());
	}
}
