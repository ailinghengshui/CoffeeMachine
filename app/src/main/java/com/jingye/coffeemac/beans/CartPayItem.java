package com.jingye.coffeemac.beans;

import android.content.Context;
import android.util.Log;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.service.domain.CoffeeInfo;

import java.util.HashMap;
import java.util.Map;

public class CartPayItem extends TListItem {

	private static final String TAG = CartPayItem.class.getSimpleName();
	private CoffeeInfo coffeeInfo;

	private int buyNum;

	private int sugarLevel;

	private Map<Integer,Integer> mPackageSugarLevel;

	public CoffeeInfo getCoffeeInfo() {
		return coffeeInfo;
	}


	public void setCoffeeInfo(CoffeeInfo coffeeInfo) {
		this.coffeeInfo = coffeeInfo;
	}

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getSugarLevel() {
		return sugarLevel;
	}

	public void setSugarLevel(int sugarLevel) {
		this.sugarLevel = sugarLevel;
	}

	public String getSugarLevelDescri(Context context){
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

	public int getPackageSugarLevel(int position){
		if(!coffeeInfo.isPackage()){
			Log.d(TAG,"coffee donot has packagesugarlevel");
			return 0;
		}else{
			if(mPackageSugarLevel.containsKey(position)){
				return mPackageSugarLevel.get(position);
			}else{
				return 0;
			}
		}
	}

	public int getPackageSugarSize(){
		return mPackageSugarLevel.size();
	}

	public Map<Integer,Integer> getPackageSugarLevelMap(){
		return mPackageSugarLevel;
	}

	public void setPackageSugarLevel(int position,int sugarlvel){
		if(mPackageSugarLevel==null){
			mPackageSugarLevel=new HashMap<Integer, Integer>();
		}
		mPackageSugarLevel.put(position,sugarlvel);
	}

}
