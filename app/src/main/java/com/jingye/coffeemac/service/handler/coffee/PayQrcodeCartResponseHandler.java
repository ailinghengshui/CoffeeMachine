package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.PayQrcodeCartResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeCartRetryTask;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.PayQrcodeCartResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayQrcodeCartResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		PayQrcodeCartRetryTask task = (PayQrcodeCartRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);

		PayQrcodeCartResult result = new PayQrcodeCartResult();
		result.setResCode(response.getLinkFrame().resCode);

		if (response.isSuccess()) {
            PayQrcodeCartResponse payQrcodeResponse = (PayQrcodeCartResponse) response;

            String payIndent = payQrcodeResponse.getPayIndent();
            String coffeeIndents = payQrcodeResponse.getCoffeeIndents();
            String QTUrl = payQrcodeResponse.getQrcodeUrl();
            String price = payQrcodeResponse.getPrice();
            String priceOri = payQrcodeResponse.getPriceOri();

            LogUtil.vendor("PayQrcodeCartResponse -> " + "payIndent: " + payIndent + "; coffeeIndents: " + coffeeIndents + "; QTUrl:" + QTUrl +
                    "; price:" + price + ", priceOri:" + priceOri);

            result.setPayIndent(payIndent);
            result.setCoffeeIndents(coffeeIndents);
            result.setQrCodeUrl(QTUrl);
            result.setPrice(price);
            result.setPriceOri(priceOri);
        }

        postToUI(result.toRemote());

    }
}
