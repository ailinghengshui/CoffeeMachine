package com.jingye.coffeemac.enums;

/**
 * ECP终端标识
 * @author zsw
 */
public enum ECPTerminal {
	
	//PC登录
	PC("0"),
	//迪威科技视讯终端
	DIWEI("1"),
	//Android终端
	Android("2"),
	//Iphone终端
	Iphone("3"),
	//win mobile终端
	WM("4"),
	//塞班终端
	Symbian("5"),
	//Brew终端
	Brew("6"),
	//Win Phone终端
	WP7("7"),
	//Ipad终端
	Ipad("8");
	private String value;
	ECPTerminal(String value){
		this.value = value;
	}
	public String getValue(){
		return value;
	}
}
