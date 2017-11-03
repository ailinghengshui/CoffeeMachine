package com.jingye.coffeemac.service.bean;

public class Timetag {
	private String uid;
	private String tag;
	private int value;
	
	public Timetag() {
		
	}
	
	public Timetag(String uid, String tag, int value) {
		this.setUid(uid);
		this.tag = tag;
		this.setValue(value);
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
