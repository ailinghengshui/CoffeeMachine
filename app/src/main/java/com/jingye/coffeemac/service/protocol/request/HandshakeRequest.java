package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ILinkService;
import com.jingye.coffeemac.service.protocol.pack.Pack;


/**
 * 发起握手请求，获取密钥
 * User: jingege
 * Date: 1/13/12
 * Time: 10:14 AM
 */
public class HandshakeRequest extends Request{


    String m;
    int product;

    public HandshakeRequest(String uid,String m,int product) {
        super(uid);
        this.m = m;
        this.product = product;
    }

    @Override
    public Pack packRequest() {
        Pack pack = new Pack();

        pack.putVarstr(uid);
        pack.putInt(product);
        pack.putVarstr(m);
        return pack;
    }

    @Override
    public short getServiceId() {
        return ServiceID.SVID_LITE_LINK;
    }

    @Override
    public short getCommandId() {
        return ILinkService.CommandId.CID_EXCHANGE_KEY;
    }
}
