package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.PayQrcodeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeRetryTask;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.PayQrcodeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayQrcodeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		PayQrcodeRetryTask task = (PayQrcodeRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		
		PayQrcodeResult result = new PayQrcodeResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            PayQrcodeResponse payQrcodeResponse = (PayQrcodeResponse) response;

            String QTUrl = payQrcodeResponse.getQrcodeUrl();
            String price = payQrcodeResponse.getPrice();
            String coffeeIndent = payQrcodeResponse.getCoffeeIndent();
            LogUtil.vendor("PayQrcodeResponse -> " + "[" + QTUrl + ", " + price + ", " + coffeeIndent);

            result.setQrcodeURL(QTUrl);
            result.setPrice(price);
            result.setCoffeeIndent(coffeeIndent);

        }

        postToUI(result.toRemote());
		
	}
}
