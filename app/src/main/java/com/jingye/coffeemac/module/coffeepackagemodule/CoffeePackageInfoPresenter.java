package com.jingye.coffeemac.module.coffeepackagemodule;

import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 2016/12/30.
 */

public class CoffeePackageInfoPresenter implements CoffeePackageInfoContract.ICoffeePackageInfoPresenter {
    private final CoffeeInfo mCoffeeInfo;
    private final CoffeePackageInfoContract.ICoffeePackageInfoView mCoffeePackageInfoView;

    public CoffeePackageInfoPresenter(CoffeeInfo mCoffeeInfo, CoffeePackageInfoContract.ICoffeePackageInfoView iCoffeePackageInfoView) {
        this.mCoffeeInfo = mCoffeeInfo;
        this.mCoffeePackageInfoView = iCoffeePackageInfoView;
    }

    public double getPrice() {
        return mCoffeeInfo.getPrice();
    }

    public double getActualPrice() {
        return mCoffeeInfo.getDiscount();
    }

    @Override
    public CoffeeInfo getCoffeeInfo() {
        return mCoffeeInfo;
    }

    @Override
    public List<CartPayItem> getCoffeeInfoItems() {
        List<CartPayItem> cartPayItems = new ArrayList<CartPayItem>();
        for (int i = 0; i < mCoffeeInfo.getCoffeesPackage().size(); i++) {
            CoffeeInfo coffeeInfo = mCoffeeInfo.getCoffeesPackage().get(i);
            CartPayItem cartPayItem = new CartPayItem();
            cartPayItem.setCoffeeInfo(coffeeInfo);
            cartPayItems.add(cartPayItem);
            if (needSugar(mCoffeeInfo.getCoffeesPackage().get(i))) {
                cartPayItem.setSugarLevel(mCoffeeInfo.getCoffeesPackage().get(i).getSugarLevel());
            } else {
                cartPayItem.setSugarLevel(0);
            }
        }
        return cartPayItems;
    }

    private boolean needSugar(CoffeeInfo info) {
        List<PackageCoffeeDosingInfo> dosingList = info.getPackageDoing();
        for (int i = 0; i < dosingList.size(); i++) {
            PackageCoffeeDosingInfo dosinfo = dosingList.get(i);
            if (dosinfo.isMachine_configured() == 1 && dosinfo.getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPackageId() {
        return mCoffeeInfo.getCoffeeId();
    }
    @Override
    public void setSugarLevel(int tag, int sugarLevel) {
        mCoffeeInfo.getCoffeesPackage().get(tag).setSugarLevel(sugarLevel);
    }
}
