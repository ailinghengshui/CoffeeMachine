package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.bean.result.PayQrcodeResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.util.log.LogUtil;

public class PayQrcodeRetryTask extends ResendRequestTask {

	public PayQrcodeRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		
		LogUtil.e("PayQrcodeRetryTask", "pay qrcode request timeout");
		
		PayQrcodeResult result = new PayQrcodeResult();
		result.setResCode(ResponseCode.RES_ETIMEOUT);
		core.notifyListener(result.toRemote());
	}
}
