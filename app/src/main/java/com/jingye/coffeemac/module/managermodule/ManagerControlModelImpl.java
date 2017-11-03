package com.jingye.coffeemac.module.managermodule;

import com.jingye.coffeemac.beans.Admin;

/**
 * Created by Hades on 2016/10/25.
 */
public class ManagerControlModelImpl implements ManagerControlContract.ManagerControlModel {

    private Admin admin;

    @Override
    public void saveAdmin(Admin admin) {
        this.admin=admin;
    }

    @Override
    public Admin getAdmin() {
        if(admin!=null){
            return admin;
        }
        Admin admin = new Admin();
        admin.setName("未知名人士");
        return admin;
    }
}
