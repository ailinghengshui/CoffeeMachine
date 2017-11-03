package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;


@ResponseID(service = ServiceID.SVID_LITE_AUTH, command = { IAuthService.CommandId.CID_REQ_CODE
		+ "" })
public class ReqCodeResponse extends SingleResponse {

}
