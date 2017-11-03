package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class ReportErrorFetchInfo extends BeanAncestor {
		
	private String uid;
	private String codes;
	private String goodIds;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_REPORT_ERROR_FETCH;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public String getGoodIds() {
		return goodIds;
	}

	public void setGoodIds(String goodIds) {
		this.goodIds = goodIds;
	}
}