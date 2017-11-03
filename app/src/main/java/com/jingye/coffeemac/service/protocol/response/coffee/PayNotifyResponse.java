package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PAY_RESULT
		+ "" })
public class PayNotifyResponse extends Response {

	private String coffeeindent;
	
	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.coffeeindent = unpack.popVarstr();
		return null;
	}

	public String getCoffeeindent() {
		return coffeeindent;
	}

	public void setCoffeeindent(String coffeeindent) {
		this.coffeeindent = coffeeindent;
	}
}
