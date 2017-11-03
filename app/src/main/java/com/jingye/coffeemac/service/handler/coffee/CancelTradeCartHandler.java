package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.CancelTradeCartResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class CancelTradeCartHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);
		CancelTradeCartResponse cancelTradeCartResponse= (CancelTradeCartResponse) response;

		LogUtil.vendor("CancelTradeCartHandler: " + ", " + cancelTradeCartResponse.getOrderIdentifier());
	}
}
