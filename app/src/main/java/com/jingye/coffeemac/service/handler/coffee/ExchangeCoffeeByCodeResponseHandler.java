package com.jingye.coffeemac.service.handler.coffee;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.service.bean.result.ExchangeCoffeeByCodeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.ExchangeCoffeeByCodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.ExchangeCoffeeByCodeRetryTask;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.ExchangeCoffeeByCodeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class ExchangeCoffeeByCodeResponseHandler extends ResponseHandler {

	private static final String TAG = ExchangeCoffeeByCodeResponseHandler.class.getSimpleName();

	@Override
	public void processResponse(Response response) {

		ExchangeCoffeeByCodeRetryTask task = (ExchangeCoffeeByCodeRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		String fetchCode = "";
		long requestTimestamp = 0;
		if (task != null) {
			ExchangeCoffeeByCodeRequest request = (ExchangeCoffeeByCodeRequest)task.getRequest();
			if(request != null){
				fetchCode = request.getFetchCode();
				requestTimestamp = request.getTimestamp();
			}
		}

		ExchangeCoffeeByCodeResult result = new ExchangeCoffeeByCodeResult();
		result.setResCode(response.getLinkFrame().resCode);
		result.setFetchCode(fetchCode);
		if (response.isSuccess()) {
			ExchangeCoffeeByCodeResponse verifyResponse = (ExchangeCoffeeByCodeResponse) response;
			String orderContentStr  = verifyResponse.getOrderContent();
			LogUtil.vendor(TAG+"-> " + orderContentStr);
            long serverTimestamp = verifyResponse.getTimestamp();
            LogUtil.vendor(TAG+"-> serverTime is " + serverTimestamp + "; requestTime is " + requestTimestamp);
			if(requestTimestamp != serverTimestamp){
				LogUtil.vendor(TAG+"-> serverTime not equal to requestTime");
				result.setResCode(320);
			}
			result.setTimestamp(requestTimestamp);

			try{
				OrderContent orderContent = new OrderContent();
				orderContent.fromJSONObject(JSON.parseObject(orderContentStr));
				result.setOrderContent(orderContent);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		// notify UI
		postToUI(result.toRemote());
	}
}
