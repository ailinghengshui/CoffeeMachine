package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class PayQrcodeRequest extends Request {
	
	private int coffeeId;
	private String dosing;
	private short provider;
	
	public PayQrcodeRequest(String uid, int coffeeId, String dosing, short provider) {
		super(uid);
		this.coffeeId = coffeeId;
		this.dosing = dosing;
		this.provider = provider;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putInt(coffeeId);
        pack.putVarstr(dosing);
        pack.putShort(provider);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.PAY_QRCODE;
	}

	public int getCoffeeId() {
		return coffeeId;
	}

	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}

	public String getDosing() {
		return dosing;
	}

	public void setDosing(String dosing) {
		this.dosing = dosing;
	}

	public short getProvider() {
		return provider;
	}

	public void setProvider(short provider) {
		this.provider = provider;
	}

}
