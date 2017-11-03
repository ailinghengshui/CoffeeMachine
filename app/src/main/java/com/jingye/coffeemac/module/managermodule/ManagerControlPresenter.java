package com.jingye.coffeemac.module.managermodule;

import com.jingye.coffeemac.beans.Admin;

import java.io.Serializable;

/**
 * Created by Hades on 2016/10/25.
 */

public class ManagerControlPresenter {

    private ManagerControlContract.ManagerControlModel mManagerControlModel;
    private ManagerControlContract.ManagerControlView mManagerControlView;

//    public ManagerControlPresenter(ManagerControlContract.ManagerControlView managerControlView) {
//        this.mManagerControlView = managerControlView;
//        mManagerControlModel = new ManagerControlModelImpl();
//    }

    public ManagerControlPresenter(ManagerControlContract.ManagerControlView managerControlView, ManagerControlContract.ManagerControlModel managerControlModel) {
        this.mManagerControlView = managerControlView;
        mManagerControlModel = managerControlModel;
    }

    public void hideAllLayouts() {
        mManagerControlView.hideAllLayouts();
    }

    public void showCurTab(int mCurTab) {
        mManagerControlView.showCurTab(mCurTab);
    }

    public void updateStatus(int status) {
        mManagerControlView.updateStatus(status);
    }

    public void saveAdmin(Admin admin) {
        mManagerControlModel.saveAdmin(admin);
    }

    public Admin getAdmin() {
        return mManagerControlModel.getAdmin();
    }
}
