package com.jingye.coffeemac.module.heatmodule;

import com.jingye.coffeemac.service.Remote;

/**
 * Created by Hades on 2017/1/13.
 */

public interface HeatContract {

    interface IHeatView{


        void sendReportError(Remote remote);

        void finishThisWithError();

        void intentToWelcome();

    }

    interface IHeatPresenter{

        void compareTemp(int temp);

        void onStop();

    }
}
