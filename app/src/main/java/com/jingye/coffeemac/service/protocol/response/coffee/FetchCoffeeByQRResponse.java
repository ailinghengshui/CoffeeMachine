package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.FECTH_COFFEE_BY_QRCODE
		+ "" })
public class FetchCoffeeByQRResponse extends SingleResponse {

	@PackIndex(0)
	private String orderContent ;

	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}
}
