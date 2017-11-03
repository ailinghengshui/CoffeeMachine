package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.PayStatusAskResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.PayStatusAskRequest;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayStatusResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        String coffeeIndent = null;
        if (task != null) {
            PayStatusAskRequest request = (PayStatusAskRequest)task.getRequest();
            coffeeIndent = request.getCoffeeindent();
            LogUtil.vendor("PayStatusResponse-> retrive for PayStatusAskRequest, indent is " + coffeeIndent);
        }

		if (response.isSuccess() && coffeeIndent!= null) {
            PayStatusAskResult result = new PayStatusAskResult();
            result.setResCode(response.getLinkFrame().resCode);
            result.setCoffeeIndent(coffeeIndent);
			postToUI(result.toRemote());
		}
	}
}
