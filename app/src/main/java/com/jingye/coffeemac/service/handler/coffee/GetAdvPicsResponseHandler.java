package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.GetAdvPicsResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.GetAdvPicResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class GetAdvPicsResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        GetAdvPicsResult result = new GetAdvPicsResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            GetAdvPicResponse advPicResponse = (GetAdvPicResponse) response;
            String advPicsUrlJson = advPicResponse.getAdvPics();
            LogUtil.e("DEBUG", "advPicsUrlJson : " + advPicsUrlJson);
            result.setAdvImgUrls(advPicsUrlJson);
        }

        postToUI(result.toRemote());
	}
}
