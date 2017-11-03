package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class GetMachineConfigResult extends BeanAncestor {

	private int resCode;

	private String workTemp;
	private String keepTemp;
	private String washTime;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_GET_MACHINE_CONIFG;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public String getWorkTemp() {
		return workTemp;
	}

	public void setWorkTemp(String workTemp) {
		this.workTemp = workTemp;
	}

	public String getKeepTemp() {
		return keepTemp;
	}

	public void setKeepTemp(String keepTemp) {
		this.keepTemp = keepTemp;
	}

	public String getWashTime() {
		return washTime;
	}

	public void setWashTime(String washTime) {
		this.washTime = washTime;
	}
}
