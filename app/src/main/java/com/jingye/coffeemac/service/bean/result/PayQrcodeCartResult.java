package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class PayQrcodeCartResult extends BeanAncestor {

	private int resCode;

	private String payIndent;
	private String coffeeIndents;
	private String price;
	private String priceOri;
	private String qrCodeUrl;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_PAY_QRCODE_CART;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

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

	public String getQrCodeUrl() {
		return qrCodeUrl;
	}

	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}

	public String getPriceOri() {
		return priceOri;
	}

	public void setPriceOri(String priceOri) {
		this.priceOri = priceOri;
	}


}
