package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.ResetStockResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;

public class ResetStockHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

		ResetStockResult result = new ResetStockResult();
        result.setResCode(response.getLinkFrame().resCode);

		postToUI(result.toRemote());
	}
}
