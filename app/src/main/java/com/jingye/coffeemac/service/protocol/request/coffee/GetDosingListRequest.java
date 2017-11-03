package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.request.Request;

public class GetDosingListRequest extends Request {

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    boolean auto;

	public GetDosingListRequest(String uid, boolean auto) {
		super(uid);
        this.auto = auto;
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.GET_DOSING;
	}
}
