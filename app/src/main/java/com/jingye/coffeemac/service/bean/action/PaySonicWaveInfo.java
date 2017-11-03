package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class PaySonicWaveInfo extends BeanAncestor {

	private static final long serialVersionUID = 7657797138988196172L;

	private String uid;
	
	private int coffeeId;
	private String dosing;
	private short provider;
	private String dynamicID;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_PAY_SONICWAVE;
	}

	public int getCoffeeId() {
		return coffeeId;
	}

	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}

	public String getDosing() {
		return dosing;
	}

	public void setDosing(String dosing) {
		this.dosing = dosing;
	}

	public short getProvider() {
		return provider;
	}

	public void setProvider(short provider) {
		this.provider = provider;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDynamicID() {
		return dynamicID;
	}

	public void setDynamicID(String dynamicID) {
		this.dynamicID = dynamicID;
	}	
}