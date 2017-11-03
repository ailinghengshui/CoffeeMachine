package com.jingye.coffeemac.enums;

import java.io.Serializable;


/**ECP状态
 * @author yangwc
 *
 */
public enum ECPState implements Serializable{
	//未登录
	UnLogin("UnLogin"),
	//在线
	Online("Online"),
	//离线
	Offline("Offline"),
	//离开
	Away("Away"),
	//忙碌
	Busy("Busy"),
	//正在通话中
	Talking("Talking"),
	//隐身登录
	HideLogin("HideLogin"),
	//免打扰
	MobileWork("MobileWork");
	
	private String value;
	
	ECPState(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getText(){
	    if("UnLogin".equals(value)){
            return "未登录";
        }else if("Online".equals(value)){
	        return "在线";
	    }else if("Offline".equals(value)){
            return "离线";
        }else if("Away".equals(value)){
            return "离开";
        }else if("Busy".equals(value)){
            return "忙碌";
        }else if("Talking".equals(value)){
            return "通话中";
        }else if("HideLogin".equals(value)){
            return "隐身";
        }else if("MobileWork".equals(value)){
            return "免打扰";
        }else {
            return "在线";
        }
	}
	
}
