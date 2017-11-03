package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.PayNotifyResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.PayNotifyResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayNotifyResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		PayNotifyResult result = new PayNotifyResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			PayNotifyResponse payNotifyResponse = (PayNotifyResponse) response;
			
			String coffeeIndent = payNotifyResponse.getCoffeeindent();
			LogUtil.vendor("successfully receive payment notice for coffeeIndent: " + coffeeIndent);
			result.setCoffeeIndent(coffeeIndent);
		}
		
		postToUI(result.toRemote());
	}
}
