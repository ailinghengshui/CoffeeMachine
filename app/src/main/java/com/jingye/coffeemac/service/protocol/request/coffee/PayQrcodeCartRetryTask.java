package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.bean.result.PayQrcodeCartResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayQrcodeCartRetryTask extends ResendRequestTask {

	public PayQrcodeCartRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		
		LogUtil.e("PayQrcodeCartRetryTask", "pay qrcode request timeout");

		PayQrcodeCartResult result = new PayQrcodeCartResult();
		result.setResCode(ResponseCode.RES_ETIMEOUT);
		core.notifyListener(result.toRemote());
	}
}
