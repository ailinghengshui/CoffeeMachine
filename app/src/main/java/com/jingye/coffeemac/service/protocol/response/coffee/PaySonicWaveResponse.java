package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PAY_ALI_SONICWAVE
		+ "" })
public class PaySonicWaveResponse extends SingleResponse {

	@PackIndex(0)
	private String coffeeIndent;
	@PackIndex(1)
	private String tradeNO;
	
	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public String getTradeNO() {
		return tradeNO;
	}

	public void setTradeNO(String tradeNO) {
		this.tradeNO = tradeNO;
	}
}
