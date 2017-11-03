package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.AppDownloadResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.AppDownloadResponse;
import com.jingye.coffeemac.util.log.LogUtil;

public class AppDownloadResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		AppDownloadResult result = new AppDownloadResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			AppDownloadResponse coffeeResponse = (AppDownloadResponse) response;
			String iosDownloadURL = coffeeResponse.getIosDownloadUrl();
			String androidDownloadURL = coffeeResponse.getAndroidDownloadUrl();
            LogUtil.e("vendor", "[AppDownloadResponse]" + "iosDownloadURL: " + iosDownloadURL + ", androidURL = " + androidDownloadURL);
			result.setAndroidDownloadURL(androidDownloadURL);
			result.setIosDownloadURL(iosDownloadURL);
		} 

		postToUI(result.toRemote());
	}
}
