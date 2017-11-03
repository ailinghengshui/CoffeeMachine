package com.jingye.coffeemac.service.bean.action;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class MachineStatusReportInfo extends BeanAncestor {
		
	private static final long serialVersionUID = -872074337734555905L;
	
	private String uid;
	private long timestamp;
	private List<Integer> status;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_REPORT_STATUS;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<Integer> getStatus() {
		return status;
	}

	public void setStatus(List<Integer> status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}	
	
	public String toJsonArray(){
		if(status == null || status.size() <= 0){
			return "";
		}
		
		JSONArray array = new JSONArray();
		for(int i = 0; i < status.size(); i++){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("status", status.get(i));
			array.add(jsonObj);
		}

		return array.toString();
	}
}