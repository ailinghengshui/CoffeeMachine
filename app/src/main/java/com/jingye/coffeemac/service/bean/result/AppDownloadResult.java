package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class AppDownloadResult extends BeanAncestor {

	private static final long serialVersionUID = 9053771665062417532L;

	private int resCode;
	
	private String iosDownloadURL;
	
	private String androidDownloadURL;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_APP_DOWNLOAD;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public String getIosDownloadURL() {
		return iosDownloadURL;
	}

	public void setIosDownloadURL(String iosDownloadURL) {
		this.iosDownloadURL = iosDownloadURL;
	}

	public String getAndroidDownloadURL() {
		return androidDownloadURL;
	}

	public void setAndroidDownloadURL(String androidDownloadURL) {
		this.androidDownloadURL = androidDownloadURL;
	}
}
