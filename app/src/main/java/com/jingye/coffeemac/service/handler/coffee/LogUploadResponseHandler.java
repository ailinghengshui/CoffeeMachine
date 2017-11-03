package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.LogUploadResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.LogUploadResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class LogUploadResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		LogUploadResult result = new LogUploadResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			LogUploadResponse logUploadResponse = (LogUploadResponse) response;

			String date = logUploadResponse.getLogDate();
			String type = logUploadResponse.getType();

			LogUtil.vendor("successfully receive upload log request: " + date + ", " + type);
			result.setDate(date);
			result.setType(type);
		}
		
		postToUI(result.toRemote());
	}
}
