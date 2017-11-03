package com.jingye.coffeemac.common.database;

public class SelectMaper  extends SqlMaper{
	
	private String parameterClass;
	private String returnClass;
	private boolean cached;

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String getParameterClass() {
		return parameterClass;
	}

	public String getReturnClass() {
		return returnClass;
	}

	public void setReturnClass(String returnClass) {
		this.returnClass = returnClass;
	}

	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}
}
