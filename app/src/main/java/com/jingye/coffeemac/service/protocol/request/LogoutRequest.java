package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;
import com.jingye.coffeemac.service.protocol.pack.Pack;

public class LogoutRequest extends Request {

	private String password;

	public LogoutRequest(String uid) {
		super(uid);
	}

	@Override
	public Pack packRequest() {
		Pack p = new Pack();
		p.putVarstr(password);
		return p;
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_AUTH;
	}

	@Override
	public short getCommandId() {
		return IAuthService.CommandId.CID_LOGOUT;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
