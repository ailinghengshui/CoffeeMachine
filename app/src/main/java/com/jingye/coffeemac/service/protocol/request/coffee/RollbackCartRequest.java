package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class RollbackCartRequest extends Request {

	private String payIndent;
	private String coffeeIndents;
	private String reason;

	public RollbackCartRequest(String uid, String payIndent, String coffeeIndents, String reason) {
		super(uid);
		this.payIndent = payIndent;
		this.coffeeIndents = coffeeIndents;
		this.reason = reason;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(payIndent);
		pack.putVarstr(coffeeIndents);
		pack.putVarstr(reason);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.ROLL_BACK_CART;
	}

}
