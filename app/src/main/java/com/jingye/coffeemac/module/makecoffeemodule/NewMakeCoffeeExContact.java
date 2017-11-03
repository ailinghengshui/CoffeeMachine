package com.jingye.coffeemac.module.makecoffeemodule;

import android.support.annotation.StringRes;

import java.util.List;

/**
 * Created by Hades on 2016/11/11.
 */

public interface NewMakeCoffeeExContact {

    interface  NewMakeCoffeeExModel{

    }

    interface NewMakeCoffeeExView{


        void onFetchTimeOut(int value);

        void onMakeCoffeePortTimeOut(int value);

        void onMakeCoffeeTimeRecord(int value);

        void onEncounterError();

        void onShowToast(@StringRes int stringResId);

        void onMakeCoffeeStart();

        void onMakeCoffeeFail();

        void onMakeCoffeeRetry();

        void onMakeCoffeeSuccess(String name);

        void onMakeCoffeeTimeout();

        void setFailed(List<Integer> errors);

        void onMakeCoffeePortTimeOut();

        void makeCoffee();

    }
}
