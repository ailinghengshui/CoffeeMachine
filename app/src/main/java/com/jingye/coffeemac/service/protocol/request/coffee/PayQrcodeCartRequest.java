package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class PayQrcodeCartRequest extends Request {

	private String coffeeIndents;
	private short provider;

	public PayQrcodeCartRequest(String uid, String coffeeIndents, short provider) {
		super(uid);
		this.coffeeIndents = coffeeIndents;
		this.provider = provider;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(coffeeIndents);
        pack.putShort(provider);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.PAY_QRCODE_CART;
	}

	public String getCoffeeIndents() {
		return coffeeIndents;
	}

	public void setCoffeeIndents(String coffeeIndents) {
		this.coffeeIndents = coffeeIndents;
	}

	public short getProvider() {
		return provider;
	}

	public void setProvider(short provider) {
		this.provider = provider;
	}
}
