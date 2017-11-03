package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

/**
 * Created by dblr4287 on 2016/7/26.
 */

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PRICEUPDATE
        + "" })
public class PriceUpdateResponse extends Response {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @PackIndex(0)
    private String type;

    @Override
    public Unpack unpackBody(Unpack unpack) throws Exception {
        this.type = unpack.popVarstr();
        return null;
    }
}
