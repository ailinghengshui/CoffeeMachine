package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.bean.BeanAncestor;

public class GeneralActionResult extends BeanAncestor {

	private int what;
	private int action;

	private int resCode;
	private int sequenceId;
	private String description;
	
	@Override
	public int getWhat() {
		return what;
	}

	@Override
	public int getAction() {
		return action;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setWhat(int what) {
		this.what = what;
	}
	
	public void setAction(int action) {
		this.action = action;
	}
}
