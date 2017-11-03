package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.EXCHANGE_COFFEE_BY_CODE
		+ "" })
public class ExchangeCoffeeByCodeResponse extends SingleResponse {

	@PackIndex(0)
	private String orderContent ;
	@PackIndex(1)
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}

}
