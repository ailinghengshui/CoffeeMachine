package com.jingye.coffeemac.inter;

public interface IServiceBindListener {

	public void onBindSuccess();
	
	public void onBindFailed(String errorMessage);
}
