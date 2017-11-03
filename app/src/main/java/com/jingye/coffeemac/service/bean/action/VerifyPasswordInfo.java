package com.jingye.coffeemac.service.bean.action;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class VerifyPasswordInfo extends BeanAncestor {

    public static final int TYPE_MOVE_BACKGROUND = 1;
    public static final int TYPE_LOGOUT = 2;

	
	private String uid;
	private String password;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

	@Override
	public int getWhat() {
		return ITranCode.ACT_USER;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_USER_VERIFY_PWD;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}