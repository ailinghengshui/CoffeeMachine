package com.jingye.coffeemac.module.managermodule;

import com.jingye.coffeemac.beans.Admin;

/**
 * Created by Hades on 2016/10/25.
 */

public interface ManagerControlContract {

    interface ManagerControlModel{

        void saveAdmin(Admin admin);

        Admin getAdmin();
    }

    interface ManagerControlView{

        void hideAllLayouts();

        void showCurTab(int mCurTab);

        void updateStatus(int status);
    }
}
