package com.jingye.coffeemac.service.protocol.request.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.request.Request;

public class MachineStatusReportRequest extends Request {

	private long timestamp;
	private String machineStatusJson;
	
	public MachineStatusReportRequest(String uid, long timestamp, String machineStatusJson) {
		super(uid);
		this.timestamp = timestamp;
		this.machineStatusJson = machineStatusJson;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(machineStatusJson);
        pack.putLong(timestamp);
        
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.MACHINE_STATUS_REPORT;
	}
}
