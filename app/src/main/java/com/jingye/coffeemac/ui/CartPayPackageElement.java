package com.jingye.coffeemac.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.util.SharePrefConfig;

/**
 * Created by Hades on 2016/12/26.
 */

public class CartPayPackageElement extends LinearLayout implements View.OnClickListener {

    private final Context mContext;
    private TextView tvCartPackageElementName;
    private TextView tvCartPackageElementSugarNum0;
    private TextView tvCartPackageElementSugarNum1;
    private TextView tvCartPackageElementSugarNum2;
    private TextView tvCartPackageElementSugarNum3;
    private int currentSugarNum = CoffeeInfo.SugarNum0;
    private AddSubView mCartPachageElementNum;
    private AddSubView.OnNumChangeListener mOnNumChangeListener;
    private LinearLayout llCartPachageElementNumContainer;
    private TextView tvCartPackageElementSugarHint;
    private LinearLayout llCartPackageElementSugarContainer;
    private CoffeeInfo coffeeInfo;
    private ISugarLevelChanged mSugarLevelChangedListener;
    private int mPosition;
    private RelativeLayout rlCartPachageElementNumContainer;

    public CartPayPackageElement(Context context) {
        super(context);
        this.mContext = context;
        init();

    }

    public CartPayPackageElement(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public CartPayPackageElement(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public void setOnSugarLevelChangedListener(ISugarLevelChanged iSugarLevelChanged) {
        this.mSugarLevelChangedListener = iSugarLevelChanged;
    }

    public void setListener(AddSubView.OnNumChangeListener onNumChangeListener) {
        this.mOnNumChangeListener = onNumChangeListener;
        mCartPachageElementNum.setOnNumChangeListener(mOnNumChangeListener);
    }

    public void setData(CoffeeInfo coffeeInfo, int position) {
        this.mPosition = position;
        this.coffeeInfo = coffeeInfo;
        String coffeeName;
        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN && !TextUtils.isEmpty(coffeeInfo.getCoffeeTitleEn())) {
            coffeeName = coffeeInfo.getCoffeeTitleEn();
        } else {
            coffeeName = coffeeInfo.getCoffeeTitle();
        }

        tvCartPackageElementName.setText(coffeeName);

    }

    public void setIsNeedNumChange(boolean isNeedNumChange) {
        if (isNeedNumChange) {
            rlCartPachageElementNumContainer.setVisibility(GONE);
            llCartPachageElementNumContainer.setVisibility(VISIBLE);
        } else {
            rlCartPachageElementNumContainer.setVisibility(VISIBLE);
            llCartPachageElementNumContainer.setVisibility(GONE);
        }
    }

    public void setIsNeedSugar(boolean isNeedSugar, CoffeeInfo coffeeInfo) {
        if (isNeedSugar) {
            tvCartPackageElementSugarHint.setVisibility(INVISIBLE);
            llCartPackageElementSugarContainer.setVisibility(VISIBLE);
        } else {

            tvCartPackageElementSugarHint.setText(mContext.getString(R.string.coffee_info_nosugar_note));

            tvCartPackageElementSugarHint.setVisibility(VISIBLE);
            llCartPackageElementSugarContainer.setVisibility(INVISIBLE);
        }
    }

    public void setNum(int num) {
        mCartPachageElementNum.setNum(num);
    }

    private void init() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.item_cart_package_element, this, true);
        tvCartPackageElementName = (TextView) root.findViewById(R.id.tvCartPackageElementName);
        tvCartPackageElementSugarNum0 = (TextView) root.findViewById(R.id.tvCartPackageElementSugarNum0);
        tvCartPackageElementSugarNum1 = (TextView) root.findViewById(R.id.tvCartPackageElementSugarNum1);
        tvCartPackageElementSugarNum2 = (TextView) root.findViewById(R.id.tvCartPackageElementSugarNum2);
        tvCartPackageElementSugarNum3 = (TextView) root.findViewById(R.id.tvCartPackageElementSugarNum3);

        tvCartPackageElementSugarHint = (TextView) root.findViewById(R.id.tvCartPackageElementSugarHint);
        llCartPackageElementSugarContainer = (LinearLayout) root.findViewById(R.id.llCartPackageElementSugarContainer);
        rlCartPachageElementNumContainer = (RelativeLayout) root.findViewById(R.id.rlCartPachageElementNumContainer);

        initSugarNum(currentSugarNum);

        tvCartPackageElementSugarNum0.setOnClickListener(this);
        tvCartPackageElementSugarNum1.setOnClickListener(this);
        tvCartPackageElementSugarNum2.setOnClickListener(this);
        tvCartPackageElementSugarNum3.setOnClickListener(this);

        llCartPachageElementNumContainer = (LinearLayout) root.findViewById(R.id.llCartPachageElementNumContainer);

        LinearLayout llCartPachageElementNum = (LinearLayout) root.findViewById(R.id.llCartPachageElementNum);
        mCartPachageElementNum = new AddSubView(mContext);
        llCartPachageElementNum.addView(mCartPachageElementNum);
    }

    private void initSugarNum(int currentNum) {
        tvCartPackageElementSugarNum0.setSelected(false);
        tvCartPackageElementSugarNum0.setTextColor(mContext.getResources().getColor(R.color.black));
        tvCartPackageElementSugarNum1.setSelected(false);
        tvCartPackageElementSugarNum1.setTextColor(mContext.getResources().getColor(R.color.black));
        tvCartPackageElementSugarNum2.setSelected(false);
        tvCartPackageElementSugarNum2.setTextColor(mContext.getResources().getColor(R.color.black));
        tvCartPackageElementSugarNum3.setSelected(false);
        tvCartPackageElementSugarNum3.setTextColor(mContext.getResources().getColor(R.color.black));
        switch (currentNum) {
            case CoffeeInfo.SugarNum1:
                tvCartPackageElementSugarNum1.setSelected(true);
                tvCartPackageElementSugarNum1.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case CoffeeInfo.SugarNum2:
                tvCartPackageElementSugarNum2.setSelected(true);
                tvCartPackageElementSugarNum2.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case CoffeeInfo.SugarNum3:
                tvCartPackageElementSugarNum3.setSelected(true);
                tvCartPackageElementSugarNum3.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            default:
                tvCartPackageElementSugarNum0.setSelected(true);
                tvCartPackageElementSugarNum0.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCartPackageElementSugarNum0:
                currentSugarNum = CoffeeInfo.SugarNum0;
                break;
            case R.id.tvCartPackageElementSugarNum1:
                currentSugarNum = CoffeeInfo.SugarNum1;
                break;
            case R.id.tvCartPackageElementSugarNum2:
                currentSugarNum = CoffeeInfo.SugarNum2;
                break;
            case R.id.tvCartPackageElementSugarNum3:
                currentSugarNum = CoffeeInfo.SugarNum3;
                break;
        }

        initSugarNum(currentSugarNum);

        if (mSugarLevelChangedListener != null) {
            mSugarLevelChangedListener.onSugarLevelChanged(mPosition,currentSugarNum);
        }
    }

    public void setInitSugarLevel(int packageSugarLevel) {
        initSugarNum(packageSugarLevel);
    }

    public interface ISugarLevelChanged {
        void onSugarLevelChanged( int position,int sugarlevel);
    }

}
