package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;

@ResponseID(service = ServiceID.SVID_LITE_AUTH, command = { IAuthService.CommandId.CID_LOGOUT
		+ "" })
public class LogoutResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		return null;
	}
}
