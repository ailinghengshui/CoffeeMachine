package com.jingye.coffeemac.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;

import java.util.ArrayList;

public class OrderContentItem extends Ancestor {

	private String itemID;

	private String itemName;

	private String itemNameen;

	private String goodID;

	private boolean isAddIce=false;
	private ArrayList<CoffeeDosingInfo> dosings = new ArrayList<CoffeeDosingInfo>();
	private int sweetLevel = -1;

	public boolean isAddIce() {
		return isAddIce;
	}

	public void setAddIce(boolean addIce) {
		isAddIce = addIce;
	}

	public int getSweetLevel() {
		return sweetLevel;
	}

	public void setSweetLevel(int sweetLevel) {
		this.sweetLevel = sweetLevel;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemNameen() {
		if(null == itemNameen)
			return itemName;
		return itemNameen;
	}

	public void setItemNameen(String itemNameen) {
		this.itemNameen = itemNameen;
	}

	public String getGoodID() {
		return goodID;
	}

	public void setGoodID(String goodID) {
		this.goodID = goodID;
	}

	public ArrayList<CoffeeDosingInfo> getDosings() {
		return dosings;
	}

	public void setDosings(ArrayList<CoffeeDosingInfo> dosings) {
		this.dosings = dosings;
	}

	public void fromJSONObject(JSONObject jsonObject) throws JSONException {
		try{
			if(jsonObject.containsKey("item_id")){
				itemID = String.valueOf(jsonObject.getLongValue("item_id"));
			}
			if(jsonObject.containsKey("item_name")){
				itemName = jsonObject.getString("item_name");
			}
			if(jsonObject.containsKey("item_name_en")){
				itemNameen = jsonObject.getString("item_name_en");
			}else{
				itemNameen = jsonObject.getString("item_name");
			}
			if(jsonObject.containsKey("good_id")){
				goodID = String.valueOf(jsonObject.getIntValue("good_id"));
			}

			if(jsonObject.containsKey("add_ice")){
				isAddIce=jsonObject.getBooleanValue("add_ice");
			}

			String dosingStr;
			if(jsonObject.containsKey("dosages")){
				dosingStr = jsonObject.getString("dosages");
				setDosingsfromJSONArray(dosingStr);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	private void setDosingsfromJSONArray(String dosingStr) throws JSONException {
		try{
			JSONArray array = JSON.parseArray(dosingStr);
			if (array != null && array.size() > 0) {
				int size = array.size();
				for(int j = 0; j < size; ++j) {
					CoffeeDosingInfo cdi = new CoffeeDosingInfo();
					cdi.fromJSONString(array.getJSONObject(j));
					dosings.add(cdi);
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
}
