package com.jingye.coffeemac.module.coffeepackagemodule;

import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.service.domain.CoffeeInfo;

import java.util.List;

/**
 * Created by Hades on 2016/12/30.
 */

public interface CoffeePackageInfoContract {

    interface ICoffeePackageInfoView{

    }

    interface ICoffeePackageInfoModel{

    }

    interface ICoffeePackageInfoPresenter{

        double getPrice();

        double getActualPrice();

        CoffeeInfo getCoffeeInfo();

        List<CartPayItem> getCoffeeInfoItems();

        int getPackageId();
        void setSugarLevel(int tag, int sugarLevel);
    }
}
