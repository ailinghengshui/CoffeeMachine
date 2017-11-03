package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.bean.BeanAncestor;

public abstract class FetchActionResult extends BeanAncestor {

	private int resCode;
	private int sequenceId;
	public int getResCode() {
		return resCode;
	}
	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
	public int getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}
	
	
}
