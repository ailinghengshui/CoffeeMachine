package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.marshal.ArrayMable;
import com.jingye.coffeemac.service.protocol.marshal.Property;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_COFFEE
		+ "" })

public class GetCoffeeResponse extends Response {
	@PackIndex(0)
	private ArrayMable coffeeInfos;
	@PackIndex(1)
	private String favorable;

	
	public Unpack unpackBody(Unpack unpack) throws Exception {
		setCoffeeInfos(new ArrayMable(Property.class));
		coffeeInfos.unmarshal(unpack);
		this.favorable = unpack.popVarstr();
		return null;
	}

	public ArrayMable getCoffeeInfos() {
		return coffeeInfos;
	}

	public void setCoffeeInfos(ArrayMable coffeeInfos) {
		this.coffeeInfos = coffeeInfos;
	}

	public String getFavorable() {
		return favorable;
	}

	public void setFavorable(String favorable) {
		this.favorable = favorable;
	}
}
