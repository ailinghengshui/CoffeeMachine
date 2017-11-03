package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ILinkService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;


@ResponseID(service = ServiceID.SVID_LITE_LINK, command = { ILinkService.CommandId.CID_HEARTBEAT
		+ "" })
public class KeepAliveResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		return null;
	}

}
