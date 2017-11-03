package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.ROLL_BACK_FETCH
		+ "" })
public class RollbackFetchResponse extends SingleResponse {

	@PackIndex(0)
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}