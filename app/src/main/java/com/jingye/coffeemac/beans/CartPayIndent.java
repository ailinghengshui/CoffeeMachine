package com.jingye.coffeemac.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;

import java.util.ArrayList;

public class CartPayIndent{

	private String indentID;

	private int coffeeID;

	private ArrayList<CoffeeDosingInfo> dosings = new ArrayList<CoffeeDosingInfo>();

	public String getIndentID() {
		return indentID;
	}

	public void setIndentID(String indentID) {
		this.indentID = indentID;
	}

	public int getCoffeeID() {
		return coffeeID;
	}

	public void setCoffeeID(int coffeeID) {
		this.coffeeID = coffeeID;
	}

	public ArrayList<CoffeeDosingInfo> getDosings() {
		return dosings;
	}

	public void setDosings(ArrayList<CoffeeDosingInfo> dosings) {
		this.dosings = dosings;
	}

	public void fromJSONObject(JSONObject jsonObject) throws JSONException {
		try{
			if(jsonObject.containsKey("indentid")){
				indentID = jsonObject.getString("indentid");
			}
			if(jsonObject.containsKey("goodsid")){
				coffeeID = jsonObject.getIntValue("goodsid");
			}
			String dosingStr;
			if(jsonObject.containsKey("dosing")){
				dosingStr = jsonObject.getString("dosing");
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
					JSONObject jsonObject = array.getJSONObject(j);
					int dosingID = -1;
					double value = -1;
					if(jsonObject.containsKey("dosingID")){
						dosingID = jsonObject.getIntValue("dosingID");
					}
					if(jsonObject.containsKey("value")){
						value = jsonObject.getDoubleValue("value");
					}

					CoffeeDosingInfo cdi = new CoffeeDosingInfo();
					cdi.setId(dosingID);
					cdi.setValue(value);
					dosings.add(cdi);
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
}
