package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.MACHINE_STATUS_REPORT
		+ "" })
public class MachineStatusReportResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		return null;
	}
}