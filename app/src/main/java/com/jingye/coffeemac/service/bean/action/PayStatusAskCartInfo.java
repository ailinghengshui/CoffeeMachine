package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class PayStatusAskCartInfo extends BeanAncestor {
		
	private String uid;

	private String payIndent;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_ASK_CART_PAY_RESULT;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}
}