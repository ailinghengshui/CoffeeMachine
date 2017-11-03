package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.marshal.ArrayMable;
import com.jingye.coffeemac.service.protocol.marshal.Property;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_DOSING
		+ "" })

public class GetDosingListResponse extends Response {

    @PackIndex(0)
	private ArrayMable coffeeDosingList;
	
	public Unpack unpackBody(Unpack unpack) throws Exception {
        setCoffeeDosingList(new ArrayMable(Property.class));
        coffeeDosingList.unmarshal(unpack);
		return null;
	}

    public ArrayMable getCoffeeDosingList() {
        return coffeeDosingList;
    }

    public void setCoffeeDosingList(ArrayMable coffeeDosingList) {
        this.coffeeDosingList = coffeeDosingList;
    }
}
