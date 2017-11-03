package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.PayStatusAskCartResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.PayStatusAskCartRequest;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayStatusCartResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        String payIndent = null;
        if (task != null) {
            PayStatusAskCartRequest request = (PayStatusAskCartRequest)task.getRequest();
            payIndent = request.getPayIndent();
            LogUtil.vendor("PayStatusResponse-> retrive for PayStatusAskCartRequest, indent is " + payIndent);
        }

		if (response.isSuccess() && payIndent!= null) {
            PayStatusAskCartResult result = new PayStatusAskCartResult();
            result.setResCode(response.getLinkFrame().resCode);
            result.setPayIndent(payIndent);
			postToUI(result.toRemote());
		}
	}
}
