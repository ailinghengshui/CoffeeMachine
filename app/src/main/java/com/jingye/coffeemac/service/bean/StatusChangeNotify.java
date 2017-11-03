package com.jingye.coffeemac.service.bean;

import com.jingye.coffeemac.service.ITranCode;

public class StatusChangeNotify extends BeanAncestor {

	private int status;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_SYS;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_SYS_STATUS_CHANGE;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
