package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class RollbackCoffeeIndentCart extends BeanAncestor {
		
	private String uid;
	private String payIndent;
	private String coffeeIndents;
	private String reason;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_ROLL_BACK_CART;
	}

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCoffeeIndents() {
		return coffeeIndents;
	}

	public void setCoffeeIndents(String coffeeIndents) {
		this.coffeeIndents = coffeeIndents;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}