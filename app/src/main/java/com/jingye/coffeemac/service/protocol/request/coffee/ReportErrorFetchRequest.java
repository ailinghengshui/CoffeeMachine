package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class ReportErrorFetchRequest extends Request {

	private String code;
	private String goodIds;

	public ReportErrorFetchRequest(String uid, String code, String goodIds) {
		super(uid);
		this.code = code;
		this.goodIds = goodIds;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(code);
		pack.putVarstr(goodIds);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.REPORT_ERROR_FETCH;
	}

}
