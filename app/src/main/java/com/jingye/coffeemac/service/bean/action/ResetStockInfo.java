package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class ResetStockInfo extends BeanAncestor {
		
	private String uid;

	private int userID;

    private String inventory;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_STOCK_RESET;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	
}