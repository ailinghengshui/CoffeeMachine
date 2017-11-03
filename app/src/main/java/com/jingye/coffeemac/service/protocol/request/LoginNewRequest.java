package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;
import com.jingye.coffeemac.service.protocol.pack.Pack;

public class LoginNewRequest extends Request {

	private String password;

	private String macAddress;

	public LoginNewRequest(String uid) {
		super(uid);
	}

	@Override
	public Pack packRequest() {
		Pack p = new Pack();
		p.putVarstr(uid);
		p.putVarstr(password);
		p.putVarstr(macAddress);
		return p;
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_AUTH;
	}

	@Override
	public short getCommandId() {
		return IAuthService.CommandId.CID_LOGIN_NEW;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
}
