package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class GetDiscountInfo extends BeanAncestor {
		
	private String uid;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_GET_DISCOUNT;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
}