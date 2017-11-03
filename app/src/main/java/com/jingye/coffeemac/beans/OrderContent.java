package com.jingye.coffeemac.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;

import java.util.ArrayList;

public class OrderContent extends Ancestor {

	private String orderID;

	private ArrayList<OrderContentItem> items = new ArrayList<OrderContentItem>();

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public ArrayList<OrderContentItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<OrderContentItem> items) {
		this.items = items;
	}

	public void fromJSONObject(JSONObject jsonObject) throws JSONException {
		try{
			// order id
			if(jsonObject.containsKey("order_id")){
				orderID = jsonObject.getString("order_id");
			}
			// order content items
			String goods;
			if(jsonObject.containsKey("goods")){
				goods = jsonObject.getString("goods");
				setOrderItemsfromJSONArray(goods);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	private void setOrderItemsfromJSONArray(String itemsStr) throws JSONException {
		try{
			JSONArray array = JSON.parseArray(itemsStr);
			if (array != null && array.size() > 0) {
				int size = array.size();
				for(int j = 0; j < size; ++j) {
					OrderContentItem item = new OrderContentItem();
					item.fromJSONObject(array.getJSONObject(j));
					items.add(item);
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	public int getItemSize(){
		return items.size();
	}

	public OrderContentItem getOrderContentItemByIndex(int index){
		try{
			if(index <= items.size() - 1){
				return items.get(index);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}
}
