package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class ExchangeCoffeeByCodeRequest extends Request {

	private String fetchCode;

	private long timestamp;

	public ExchangeCoffeeByCodeRequest(String uid, String fetchCode, long timestamp) {
		super(uid);
		this.fetchCode = fetchCode;
		this.timestamp = timestamp;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(fetchCode);
        pack.putLong(timestamp);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.EXCHANGE_COFFEE_BY_CODE;
	}

	public String getFetchCode() {
		return fetchCode;
	}

	public void setFetchCode(String fetchCode) {
		this.fetchCode = fetchCode;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
