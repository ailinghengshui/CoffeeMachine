package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.MachineStatusServerRequest;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

public class MachineStatusServerResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		if (response.isSuccess()) {			
			LogUtil.vendor("receive machine status request from server");
			
			// to get something status
			String machineInfo = "hi, i'm ok";
			
			MachineStatusServerRequest request = new MachineStatusServerRequest(U.getMyVendorNum(), 
					TimeUtil.getNow_millisecond(), machineInfo);
			core.sendRequestToServer(request);
		}
	}
}
