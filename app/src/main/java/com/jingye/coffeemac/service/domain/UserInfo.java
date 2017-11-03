package com.jingye.coffeemac.service.domain;

public class UserInfo extends Ancestor {

	private static final long serialVersionUID = -8221796392738294897L;

	public static final String TYPE_INIT = "I";
	public static final String TYPE_MIXIN = "M";

	/** state */
	public static final String STATE_INIT = "I";
	public static final String STATE_COMMIT = "C";

	private String uid;
	private String belongto;
	private String state;
	private String type;

	public String getUid() {
		return uid;
	}
	
	public void setUid(String id) {
		this.uid = id;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isMixinUser(){
		return TYPE_MIXIN.equals(type);
	}

	public String getBelongto() {
		return belongto;
	}

	public void setBelongto(String belongto) {
		this.belongto = belongto;
	}
}
