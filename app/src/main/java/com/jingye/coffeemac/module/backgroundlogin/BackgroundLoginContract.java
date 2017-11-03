package com.jingye.coffeemac.module.backgroundlogin;

import com.jingye.coffeemac.beans.Admin;

/**
 * Created by Hades on 2017/3/30.
 */

public interface BackgroundLoginContract {

    interface IBackgroundLoginView{

        void navigateToSetting();

        void hideKeyboard();

        boolean isNetWorkConnected();

        void setErrText(int strId);

        void showKeyboardAccount();

        void showKeyboardPassword();

        void stopLoginTimer();

        void setErrText(String statusMsg);

        void showProgress(String msg);

        void startLoginDisableTimer();

        void navigateToRepair(Admin admin);

        void closeProgress();
    }

    interface IBackgroundLoginPresenter{

        void navigatorToActionSetting();

        void doLogin(String counter,String password);
    }
}
