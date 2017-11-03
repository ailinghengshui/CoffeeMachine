package com.jingye.coffeemac.service.bean.result;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

/**
 * Created by dblr4287 on 2016/7/7.
 */
public class ActiveNoticeResult extends BeanAncestor {

    private int resCode;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String date;

    private String type;

    @Override
    public int getWhat() {
        return ITranCode.ACT_COFFEE;
    }

    @Override
    public int getAction() {
        return ITranCode.ACT_COFFEE_ACTIVENOTICE;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

}
