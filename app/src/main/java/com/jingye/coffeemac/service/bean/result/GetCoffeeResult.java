package com.jingye.coffeemac.service.bean.result;

import java.util.ArrayList;
import java.util.List;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;
import com.jingye.coffeemac.service.domain.CoffeeInfo;

public class GetCoffeeResult extends BeanAncestor {

	private int resCode;
	
	private List<CoffeeInfo> coffees;

	private GetDiscountResult discountInfo;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_GET_COFFEE;
	}

	public List<CoffeeInfo> getCoffees() {
		return coffees;
	}

	public void setCoffees(List<CoffeeInfo> coffees) {
		this.coffees = coffees;
	}
	
	public void addCoffee(CoffeeInfo coffee) {
		if (coffees == null) {
			coffees = new ArrayList<CoffeeInfo>();
		}
		coffees.add(coffee);
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public GetDiscountResult getDiscountInfo() {
		return discountInfo;
	}

	public void setDiscountInfo(GetDiscountResult discountInfo) {
		this.discountInfo = discountInfo;
	}
}
