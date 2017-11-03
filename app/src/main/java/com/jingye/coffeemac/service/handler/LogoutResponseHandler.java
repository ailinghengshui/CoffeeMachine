package com.jingye.coffeemac.service.handler;

import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.protocol.response.Response;

public class LogoutResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

		LogoutResult result = new LogoutResult();
		result.setResCode(response.getLinkFrame().resCode);
		if (response.isSuccess()) {
			core.logout();

			core.setMyVendorNum("");
			core.setLastVendorNum("");
        	core.setLastVendorName("");
			core.setLastVendorPwd("");
		}

		postToUI(result.toRemote());
	}
}
