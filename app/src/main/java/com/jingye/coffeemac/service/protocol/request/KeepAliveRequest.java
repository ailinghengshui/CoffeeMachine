package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ILinkService;


public class KeepAliveRequest extends Request {

	public KeepAliveRequest(String uid) {
		super(uid);
		// TODO Auto-generated constructor stub
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_LINK;
	}

	@Override
	public short getCommandId() {
		return ILinkService.CommandId.CID_REQUEST_HEARTBEAT;
	}

}
