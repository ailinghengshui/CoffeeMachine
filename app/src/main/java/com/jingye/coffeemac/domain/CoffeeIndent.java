package com.jingye.coffeemac.domain;

import com.jingye.coffeemac.service.domain.Ancestor;

public class CoffeeIndent extends Ancestor {

	public static final int STATUS_COFFEE_START = 0;
	public static final int STATUS_COFFEE_DONE = 1;
	public static final int STATUS_COFFEE_ERROR = -1;
	
	private String coffeeindent;	
	private int coffeeid;
	private String dosing;
	private int status;

	public String getCoffeeindent() {
		return coffeeindent;
	}
	
	public void setCoffeeindent(String coffeeindent) {
		this.coffeeindent = coffeeindent;
	}
	
	public int getCoffeeid() {
		return coffeeid;
	}
	
	public void setCoffeeid(int coffeeid) {
		this.coffeeid = coffeeid;
	}
	
	public String getDosing() {
		return dosing;
	}
	
	public void setDosing(String dosing) {
		this.dosing = dosing;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
