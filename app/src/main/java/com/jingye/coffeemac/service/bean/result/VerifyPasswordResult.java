package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class VerifyPasswordResult extends BeanAncestor {

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    private boolean isCorrect;

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

}
