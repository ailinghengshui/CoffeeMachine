package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class ExchangeCoffeeByCodeResult extends BeanAncestor {

	private int resCode;

	private String fetchCode;

	private OrderContent orderContent;
	
	private long timestamp;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_EXCHANGE_COFFEE;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFetchCode() {
		return fetchCode;
	}

	public void setFetchCode(String fetchCode) {
		this.fetchCode = fetchCode;
	}

	public OrderContent getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(OrderContent orderContent) {
		this.orderContent = orderContent;
	}
}
