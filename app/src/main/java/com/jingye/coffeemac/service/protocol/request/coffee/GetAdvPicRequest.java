package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.request.Request;

public class GetAdvPicRequest extends Request {

	public GetAdvPicRequest(String uid) {
		super(uid);
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.GET_ADV_PIC;
	}
}
