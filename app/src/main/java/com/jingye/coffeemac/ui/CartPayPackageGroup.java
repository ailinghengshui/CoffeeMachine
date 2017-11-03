package com.jingye.coffeemac.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hades on 2016/12/26.
 */

public class CartPayPackageGroup extends LinearLayout {


    private static final String TAG = CartPayPackageGroup.class.getSimpleName();
    private final Context mContext;
    private AddSubView.OnNumChangeListener onNumChangeListener;

    private Map<Integer, Integer> sugarLevelMap = new HashMap<Integer, Integer>();
    private CartPayPackageElement.ISugarLevelChanged mSugarLevelChanged;

    public CartPayPackageGroup(Context context) {
        super(context);
        this.mContext = context;
        setOrientation(VERTICAL);
    }

    public CartPayPackageGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(VERTICAL);
    }

    public CartPayPackageGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        setOrientation(VERTICAL);
    }

    public void setNumListener(AddSubView.OnNumChangeListener onNumChangeListener) {
        this.onNumChangeListener = onNumChangeListener;
    }

    /**
     * setNumListener first
     *
     */
    public void addData(CartPayItem cartPayItem) {
        List<CoffeeInfo> coffeeInfoList=cartPayItem.getCoffeeInfo().getCoffeesPackage();
        int buyNum=cartPayItem.getBuyNum();
        removeAllViews();
        sugarLevelMap.clear();
        for (int i = 0; i < coffeeInfoList.size(); i++) {
            CoffeeInfo coffeeInfo = coffeeInfoList.get(i);
            sugarLevelMap.put(i, cartPayItem.getPackageSugarLevel(i));

            CartPayPackageElement cartPayPackageElement = new CartPayPackageElement(mContext);
            cartPayPackageElement.setIsNeedNumChange(false);

            if (mSugarLevelChanged != null) {
                cartPayPackageElement.setOnSugarLevelChangedListener(mSugarLevelChanged);
            }
            cartPayPackageElement.setInitSugarLevel(cartPayItem.getPackageSugarLevel(i));
            cartPayPackageElement.setData(coffeeInfo, i);

            cartPayPackageElement.setIsNeedSugar(needSugar(coffeeInfo), coffeeInfo);

            this.addView(cartPayPackageElement);
            if(i==(coffeeInfoList.size()-1)){
                CartPayPackageElement cartPayPackageElement1 = new CartPayPackageElement(mContext);
                cartPayPackageElement1.setIsNeedNumChange(true);
                cartPayPackageElement1.setNum(buyNum);
                if (onNumChangeListener != null) {
                    cartPayPackageElement1.setListener(onNumChangeListener);
                } else {
                    Log.d(TAG, "onNumChangeListener is null");
                }
                this.addView(cartPayPackageElement1);
            }
        }
    }

    public void setSugarLevelChangeListener(CartPayPackageElement.ISugarLevelChanged iSugarLevelChanged) {
        this.mSugarLevelChanged = iSugarLevelChanged;
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

}
