package com.jingye.coffeemac.beans;

import com.jingye.coffeemac.common.adapter.TListItem;

public class MakeCoffeeItem extends TListItem {

	public static final int STATUS_WAITING = 0;
	public static final int STATUS_FAIL = -1;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_MAKING = 2;
	//public static final int STATUS_RETRY = 3;

	private OrderContentItem orderItem;

	private int status;

	private int num;

	public OrderContentItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderContentItem orderItem) {
		this.orderItem = orderItem;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}
