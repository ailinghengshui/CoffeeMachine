package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_ADV_PIC
		+ "" })
public class GetAdvPicResponse extends Response {

	public String getAdvPics() {
		return advPics;
	}

	public void setAdvPics(String advPics) {
		this.advPics = advPics;
	}

	private String advPics;

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.advPics = unpack.popVarstr();
		return null;
	}
}
