package com.jingye.coffeemac.service.handler.coffee;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.service.bean.result.FetchCoffeeByCodeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.FetchCoffeeByCodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.FetchCoffeeByCodeRetryTask;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.FetchCoffeeByCodeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class FetchCoffeeByCodeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		FetchCoffeeByCodeRetryTask task = (FetchCoffeeByCodeRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		String fetchCode = "";
		long requestTimestamp = 0;
		if (task != null) {
			FetchCoffeeByCodeRequest request = (FetchCoffeeByCodeRequest)task.getRequest();
			if(request != null){
				fetchCode = request.getFetchCode();
				requestTimestamp = request.getTimestamp();
			}
		}

		FetchCoffeeByCodeResult result = new FetchCoffeeByCodeResult();
		result.setResCode(response.getLinkFrame().resCode);
		result.setFetchCode(fetchCode);
		if (response.isSuccess()) {
			FetchCoffeeByCodeResponse verifyResponse = (FetchCoffeeByCodeResponse) response;
			String orderContentStr  = verifyResponse.getOrderContent();
			LogUtil.vendor("FetchCoffeeByCodeResponse-> " + orderContentStr);
            long serverTimestamp = verifyResponse.getTimestamp();
            LogUtil.vendor("FetchCoffeeByCodeResponse-> serverTimestamp is " + serverTimestamp + "; requestTimestamp is" + requestTimestamp);
			if(requestTimestamp != serverTimestamp){
				LogUtil.vendor("FetchCoffeeByCodeResponse-> serverTime not equal to requestTime");
				result.setResCode(320);
//				return;
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
