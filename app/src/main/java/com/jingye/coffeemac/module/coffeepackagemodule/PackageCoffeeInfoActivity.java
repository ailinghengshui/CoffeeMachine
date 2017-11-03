package com.jingye.coffeemac.module.coffeepackagemodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.CoffeeInfoActivity;
import com.jingye.coffeemac.activity.HomePageActivity;
import com.jingye.coffeemac.activity.PayCartActivity;
import com.jingye.coffeemac.activity.PayCoffeeQrcodeActivity;
import com.jingye.coffeemac.activity.WelcomeActivity;
import com.jingye.coffeemac.adapter.PackageInfoViewHolder;
import com.jingye.coffeemac.adapter.PayCartViewHolder;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.common.adapter.TAdapter;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.common.adapter.TViewHolder;
import com.jingye.coffeemac.common.component.TListView;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.ToolUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jingye.coffeemac.activity.CoffeeInfoActivity.COFFEE_INFO;

public class PackageCoffeeInfoActivity extends TActivity implements CoffeePackageInfoContract.ICoffeePackageInfoView, OnClickListener, TViewHolder.ViewHolderEventListener {

    private static final String TAG = "PackageCoffeeInfoActivity-> ";
    private static final int MAX_IDEL_TIME = 60;
    private Context mContext;

    private boolean foreground;

    private ImageView mBackBtn;
    //    private ImageView mBackHome;
    private ImageView mNetworkStatus;

    private TListView mListView;
    private BaseAdapter mAdapter;
    private List<CartPayItem> mCartPayItems;

    private TextView mTotalPrice;
    private TextView mActualPay;
    private LinearLayout mAliPayCart;
    private LinearLayout mWxPayCart;

    private CountDownTimer mCountDownTimer;

    private String tempDiscount;
    private String tempReductMeet;
    private String tempReductSub;
    private CoffeeInfo mCoffeeInfo;
    private CoffeePackageInfoContract.ICoffeePackageInfoPresenter mPresenter;
    private ImageView mZhimg;
    private ImageView mEnimg;
    private RelativeLayout mCartContainer;
    private TextView mCartNum;
    private Button btnAddToCart;
    private ImageView mAnimImageView;
    private Animation mAnimation;
    private boolean isGoToCartPay = false;
    private LinearLayout mAbcPayCart;

    public static void start(Activity activity, CoffeeInfo info) {
        Intent intent = new Intent();
        intent.setClass(activity, PackageCoffeeInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(COFFEE_INFO, info);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_coffee_info_layout);
        mContext = this;
        proceedExtra();

        initViews();
        initTimer();
        initValue();
        setupCartList();
        initStatus();
        initTitleLanguage();
        initAnimation();

    }

    private void initAnimation() {
        mAnimImageView = (ImageView) findViewById(R.id.cart_anim_icon);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.cart_anim);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateCartGoods();

                mAnimImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initTitleLanguage() {

        mZhimg = (ImageView) findViewById(R.id.home_title_language_zh);
        mEnimg = (ImageView) findViewById(R.id.home_title_language_en);

        mZhimg.setVisibility(View.VISIBLE);
        mEnimg.setVisibility(View.VISIBLE);

        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGECH) {

            mZhimg.setBackgroundResource(R.drawable.zh_sel);
            mEnimg.setBackgroundResource(R.drawable.en_nor);
        } else {
            mZhimg.setBackgroundResource(R.drawable.zh_nor);
            mEnimg.setBackgroundResource(R.drawable.en_sel);
        }

        mZhimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ToolUtil.isFastClick())
                    return;

                if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGECH)
                    return;

                mZhimg.setBackgroundResource(R.drawable.zh_sel);
                mEnimg.setBackgroundResource(R.drawable.en_nor);
                SharePrefConfig.getInstance().setLanguageType(SharePrefConfig.LANGUAGECH);
                ChangeLanguage();
            }
        });

        mEnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ToolUtil.isFastClick())
                    return;

                if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN)
                    return;
                mZhimg.setBackgroundResource(R.drawable.zh_nor);
                mEnimg.setBackgroundResource(R.drawable.en_sel);
                SharePrefConfig.getInstance().setLanguageType(SharePrefConfig.LANGUAGEEN);
                ChangeLanguage();
            }
        });
    }

    private void ChangeLanguage() {

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
            config.locale = Locale.ENGLISH;
            resources.updateConfiguration(config, dm);
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            resources.updateConfiguration(config, dm);
        }
        if (mCoffeeInfo != null) {

            WelcomeActivity.start(this);
            HomePageActivity.start(this, false, 0);
            PackageCoffeeInfoActivity.start(this, mPresenter.getCoffeeInfo());
            finish();
        }
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        mCoffeeInfo = (CoffeeInfo) intent.getSerializableExtra(COFFEE_INFO);
        if (mCoffeeInfo != null) {
            LogUtil.vendor(TAG + mCoffeeInfo.getCoffeeTitle());
            mPresenter = new CoffeePackageInfoPresenter(mCoffeeInfo, this);
        }
    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.pay_cart_back);
        mBackBtn.setOnClickListener(this);
        mNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        mTotalPrice = (TextView) findViewById(R.id.pay_cart_total_price);
        mTotalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTotalPrice.getPaint().setAntiAlias(true);
        mActualPay = (TextView) findViewById(R.id.pay_cart_actual_price);

        mCartContainer = (RelativeLayout) findViewById(R.id.coffee_shopping_cart_layout);
        mCartNum = (TextView) findViewById(R.id.coffee_shopping_cart_indicator);

        mAliPayCart = (LinearLayout) findViewById(R.id.pay_cart_alipay_btn);
        mAliPayCart.setOnClickListener(this);

        mWxPayCart = (LinearLayout) findViewById(R.id.pay_cart_wxpay_btn);
        mWxPayCart.setOnClickListener(this);
        mAbcPayCart = (LinearLayout) findViewById(R.id.pay_cart_abc_btn);
        mAbcPayCart.setOnClickListener(this);

        mCartContainer.setOnClickListener(this);

        btnAddToCart = (Button) findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(this);

        if (AppConfig.isMacForAli()) {
            mAliPayCart.setVisibility(View.VISIBLE);
        } else {
            mAliPayCart.setVisibility(View.GONE);
        }

        if (AppConfig.isMacForWechat()) {
            mWxPayCart.setVisibility(View.VISIBLE);
        } else {
            mWxPayCart.setVisibility(View.GONE);
        }

        if(AppConfig.isMacForAbc()){
            mAbcPayCart.setVisibility(View.VISIBLE);
        }else{
            mAbcPayCart.setVisibility(View.GONE);
        }
    }

    private void initValue() {
        mTotalPrice.setText(String.format(Locale.getDefault(), getString(R.string.cart_total_price), mPresenter.getPrice()));
        mActualPay.setText(String.format(Locale.getDefault(), getString(R.string.cart_total_price), mPresenter.getActualPrice()));

        updateCartGoods();

    }

    private void updateCartGoods() {
        int num = MyApplication.Instance().getCartNums();
        if (num > 0) {
            mCartContainer.setVisibility(View.VISIBLE);
            mCartNum.setText(String.valueOf(num));
        } else {
            mCartContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void setupCartList() {
        // update items from global cache
        mCartPayItems = mPresenter.getCoffeeInfoItems();

        mListView = (TListView) findViewById(R.id.pay_cart_list);
        Map<Integer, Class> viewHolders = new HashMap<Integer, Class>();
        viewHolders.put(0, PackageInfoViewHolder.class);
        mAdapter = new TAdapter(this, this, viewHolders, mCartPayItems);
        mListView.setAdapter(mAdapter);


    }


    private void initTimer() {
        mCountDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
    }

    private void onCountDown(int value) {
        if (value == 0 && foreground) {
            LogUtil.vendor(TAG + " idle time end");
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pay_cart_back_home:
                switchToHome();
                break;
            case R.id.pay_cart_back:
                finish();
                break;
            case R.id.pay_cart_alipay_btn:
                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.AliQr.tag);
                break;
            case R.id.pay_cart_wxpay_btn:
                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.WeiXin.tag);
                break;
            case R.id.pay_cart_abc_btn:
                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.Abc.tag);
                break;
            case R.id.btnAddToCart:
                if (mCoffeeInfo.isPackage()) {
                    if (!CoffeeUtil.isExcceedCartLimit(mCoffeeInfo.getPackageNum())) {
                        CartPayItem item = new CartPayItem();
                        item.setCoffeeInfo(mCoffeeInfo);
                        item.setBuyNum(1);
                        for (int i = 0; i < mCartPayItems.size(); i++) {
                            item.setPackageSugarLevel(i, mCartPayItems.get(i).getSugarLevel());
                        }
                        MyApplication.Instance().addCoffeeToCartPay(item);

                        mAnimImageView.setVisibility(View.VISIBLE);
                        mAnimImageView.startAnimation(mAnimation);
                    } else {
                        ToastUtil.showToast(this, R.string.cart_exceeds_max_num);
                    }

                }
                break;
            case R.id.coffee_shopping_cart_layout:
                switchToCart();

                break;
            default:
                break;
        }
    }


    private void switchToCart() {
        isGoToCartPay = true;
        PayCartActivity.start(this);
    }

    private void switchToHome() {
        HomePageActivity.start(this, false, 0);
    }

    private void onCoffeePay(int payMethod) {
        if (mCartPayItems == null || mCartPayItems.size() <= 0) {
            ToastUtil.showToast(mContext, R.string.pay_cart_no_drinks_to_pay);
            return;
        }

        if (payMethod == PayCoffeeQrcodeActivity.PayMethod.AliWa.tag) {

        } else {
            String coffeeIndents = getCoffeeIndents();
            LogUtil.vendor("coffeeIndents->" + coffeeIndents);
            PayCoffeeQrcodeActivity.start(this, coffeeIndents, payMethod);
        }
    }


    //	[{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1，packageid,1111},{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1},{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1}]
//	packageid
    private String getCoffeeIndents() {
        JSONArray coffeeIndents = new JSONArray();
        for (int i = 0; i < mCartPayItems.size(); i++) {
            CartPayItem item = mCartPayItems.get(i);

            CoffeeInfo coffeeInfo = item.getCoffeeInfo();
            if (coffeeInfo != null) {

                int sugarLevel = 0;
                JSONArray dosings = new JSONArray();
                List<PackageCoffeeDosingInfo> dosingList = coffeeInfo.getPackageDoing();
                for (int j = 0; j < dosingList.size(); j++) {
                    PackageCoffeeDosingInfo dosing = dosingList.get(j);
                    if (dosing.isMachine_configured() == 1) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("dosingID", dosing.getId());

                        double sugarWeight = 0;
                        sugarLevel = item.getSugarLevel();
                        if (sugarLevel == CoffeeInfo.SugarNum0) {
                            sugarWeight = 0;
                        } else if (sugarLevel == CoffeeInfo.SugarNum1) {
                            sugarWeight = dosing.getValue() * 0.5;
                        } else if (sugarLevel == CoffeeInfo.SugarNum2) {
                            sugarWeight = dosing.getValue();
                        } else if (sugarLevel == CoffeeInfo.SugarNum3) {
                            sugarWeight = dosing.getValue() * 1.5;
                        }

                        jsonObj.put("value", sugarWeight);
                        dosings.add(jsonObj);
                        break;
                    }
                }

                JSONObject indent = new JSONObject();
                indent.put("packageid", mPresenter.getPackageId());
                indent.put("goodsid", item.getCoffeeInfo().getCoffeeId());
                indent.put("level", sugarLevel);
                indent.put("dosing", dosings.toString());
                coffeeIndents.add(indent);
            }
        }
        return coffeeIndents.toString();


    }


    @Override
    public void onReceive(Remote remote) {
        // system action
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        } else if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {

                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String type = result.getType();
                    if (type.equals("102")) {

//                        GetCoffeeInfo info = new GetCoffeeInfo();
//                        info.setUid(U.getMyVendorNum());
//                        executeBackground(info.toRemote());
                    }
//					else if(type.equals("103")) {
//
//						GetAdvPicsInfo info = new GetAdvPicsInfo();
//						info.setUid(U.getMyVendorNum());
//						execute(info.toRemote());
//					}
                }
            }
        }
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void updateStatus(int status) {

        if (status == ITranCode.STATUS_NO_NETWORK
                || status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN) {
            mNetworkStatus.setVisibility(View.VISIBLE);
            mNetworkStatus.setImageResource(R.drawable.home_network_status_broken);
        } else if (status == ITranCode.STATUS_LOGGING) {
            mNetworkStatus.setVisibility(View.VISIBLE);
            mNetworkStatus.setImageResource(R.drawable.home_network_status_connecting);
        } else {
            mNetworkStatus.setVisibility(View.INVISIBLE);
            mNetworkStatus.setImageResource(R.drawable.home_network_status_connected);
        }
    }

    @Override
    public boolean onViewHolderLongClick(View view, TListItem item) {
        return false;
    }

    @Override
    public boolean onViewHolderClick(View view, TListItem item) {
        if (item instanceof CartPayItem) {
            CartPayItem cartPayItem = (CartPayItem) item;


            if (item.getTag() < mCartPayItems.size()) {
                mCartPayItems.get(item.getTag()).setSugarLevel(cartPayItem.getSugarLevel());
                mPresenter.setSugarLevel(item.getTag(),cartPayItem.getSugarLevel());
            }

            return true;
        }

        return false;
    }

    @Override
    public void onItemChangeNotify() {

    }

    @Override
    public void onStart() {
        super.onStart();
        startTimerCount();

    }

    private void startTimerCount() {
        mCountDownTimer.startCountDownTimer(MAX_IDEL_TIME, 1000, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimerCount();
    }

    private void stopTimerCount() {
        mCountDownTimer.cancelCountDownTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        foreground = true;

        if (isGoToCartPay) {
            isGoToCartPay = false;
            updateCartGoods();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        foreground = false;
    }
}
