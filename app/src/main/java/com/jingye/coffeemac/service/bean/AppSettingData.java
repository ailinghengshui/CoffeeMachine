package com.jingye.coffeemac.service.bean;

import com.jingye.coffeemac.service.ITranCode;

public class AppSettingData extends BeanAncestor {

	private String key;
	private String value;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_SYS;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_SYS_SAVEAPPSET;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
