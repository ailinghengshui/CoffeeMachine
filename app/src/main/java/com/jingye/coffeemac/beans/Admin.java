package com.jingye.coffeemac.beans;

import java.io.Serializable;

/**
 * Created by Hades on 2016/10/26.
 */

public class Admin  implements Serializable{


    /**
     * wechatbd : false
     * id : 40
     * role : 8
     * lastLogin : 2016-10-26 15:00:27.0
     * name : 何张和
     */

    private boolean wechatbd;
    private int id;
    private int role;
    private String lastLogin;
    private String name;
    private int recordId;


    public boolean isWechatbd() {
        return wechatbd;
    }

    public void setWechatbd(boolean wechatbd) {
        this.wechatbd = wechatbd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
}
