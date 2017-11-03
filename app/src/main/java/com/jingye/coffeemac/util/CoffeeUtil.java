package com.jingye.coffeemac.util;

import android.content.Context;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;

import java.util.ArrayList;

public class CoffeeUtil {

	public static final int MAX_CART_NUM = 9;

	public static boolean isSufficient(ArrayList<CoffeeDosingInfo> dosingList){
		double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
		boolean isLackWater =(stockWater - MachineMaterialMap.MATERIAL_WATER_LIMIT_VALUE) <= 0;
		double stockCup = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
		boolean isLackCup =(stockCup - MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE) <= 0;
		double stockBox1 =	SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
		boolean isLackBox1 =(stockBox1 - MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE) <= 0;
		double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
		boolean isLackBox2 =(stockBox2 - MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE) <= 0;
		double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
		boolean isLackBox3 =(stockBox3 - MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE) <= 0;
		double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
		boolean isLackBox4 =(stockBox4 - MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE) <= 0;
		double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
		boolean isLackBean =(stockBean - MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE) <= 0;

		if(isLackWater ||  isLackCup){
			return false;
		}

		for(int i = 0; i < dosingList.size(); i++) {
			CoffeeDosingInfo dosing = dosingList.get(i);
			int boxID = dosing.getId();
			if (boxID == MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE && isLackBox1) {
				return false;
			} else if (boxID == MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE && isLackBox2) {
				return false;
			} else if (boxID == MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE && isLackBox3) {
				return false;
			} else if (boxID == MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE && isLackBox4) {
				return false;
			} else if (boxID == MachineMaterialMap.MATERIAL_COFFEE_BEAN && isLackBean) {
				return false;
			}
		}

		return true;
	}

	public static boolean isExcceedCartLimit(int increment){
		int exitNum = MyApplication.Instance().getCartNums();
		if((exitNum + increment) > MAX_CART_NUM){
			return true;
		}

		return false;
	}

	public static String getSugarLevelDescri(Context context, int sugarLevel){
		String description = "";
		switch(sugarLevel){
			case 1:
				description = context.getString(R.string.pay_add_no_sugar);
				break;
			case 2:
				description = context.getString(R.string.pay_add_little_sugar);
				break;
			case 3:
				description = context.getString(R.string.pay_add_middle_sugar);
				break;
			case 4:
				description = context.getString(R.string.pay_add_more_sugar);
				break;
		}

		return description;
	}
}
