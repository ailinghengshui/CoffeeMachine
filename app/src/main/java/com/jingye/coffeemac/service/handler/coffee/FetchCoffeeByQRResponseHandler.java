package com.jingye.coffeemac.service.handler.coffee;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.service.bean.result.FetchCoffeeByCodeResult;
import com.jingye.coffeemac.service.bean.result.FetchCoffeeByQRResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.FetchCoffeeByCodeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.FetchCoffeeByQRResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class FetchCoffeeByQRResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		if (response.isSuccess()) {
			FetchCoffeeByQRResult result = new FetchCoffeeByQRResult();
			result.setResCode(response.getLinkFrame().resCode);

			FetchCoffeeByQRResponse verifyResponse = (FetchCoffeeByQRResponse) response;
			String orderContentStr  = verifyResponse.getOrderContent();
			LogUtil.vendor("FetchCoffeeByQRResponseHandler-> " + orderContentStr);

			try{
				OrderContent orderContent = new OrderContent();
				orderContent.fromJSONObject(JSON.parseObject(orderContentStr));
				result.setOrderContent(orderContent);
			}catch(Exception e){
				e.printStackTrace();
			}

			// notify UI
			postToUI(result.toRemote());
		}


	}
}
