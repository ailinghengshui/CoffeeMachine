package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class FetchCoffeeByQRResult extends BeanAncestor {

	private int resCode;

	private OrderContent orderContent;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_QR;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public OrderContent getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(OrderContent orderContent) {
		this.orderContent = orderContent;
	}
}
