package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.RESET_STOCK
        + "" })
public class ResetStockResponse extends Response {

    @Override
    public Unpack unpackBody(Unpack unpack) throws Exception {
        return null;
    }

}
