package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.AddStockResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;

public class AddStockHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        AddStockResult result = new AddStockResult();
        result.setResCode(response.getLinkFrame().resCode);

		postToUI(result.toRemote());
	}
}
