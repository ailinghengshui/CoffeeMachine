package com.jingye.coffeemac.service.protocol.response.coffee;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.pack.PackIndex;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PAY_QRCODE_CART
		+ "" })
public class PayQrcodeCartResponse extends SingleResponse {

	@PackIndex(0)
	private String payIndent;
	@PackIndex(1)
	private String coffeeIndents;
	@PackIndex(2)
	private String price;
	@PackIndex(3)
	private String priceOri;
	@PackIndex(4)
	private String qrcodeUrl;

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}

	public String getCoffeeIndents() {
		return coffeeIndents;
	}

	public void setCoffeeIndents(String coffeeIndents) {
		this.coffeeIndents = coffeeIndents;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}

	public String getPriceOri() {
		return priceOri;
	}

	public void setPriceOri(String priceOri) {
		this.priceOri = priceOri;
	}

}
