package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.GetNoticeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.GetNoticeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class GetNoticeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        GetNoticeResult result = new GetNoticeResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            GetNoticeResponse noticeResponse = (GetNoticeResponse) response;
            String promotions = noticeResponse.getPromotions();
            LogUtil.e("DEBUG", "promotions : " + promotions);
            result.setPromotions(promotions);

            postToUI(result.toRemote());
        }
	}
}
