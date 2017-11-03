package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.RollbackCartResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;

public class RollbackCartIndentResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

		RollbackCartResult result = new RollbackCartResult();
		result.setResCode(response.getLinkFrame().resCode);
		postToUI(result.toRemote());
	}
}
