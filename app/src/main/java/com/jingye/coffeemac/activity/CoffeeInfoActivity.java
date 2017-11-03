package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.ToolUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoffeeInfoActivity extends TActivity implements OnClickListener {

    public static final String COFFEE_INFO = "coffee_info";
    private static final int IDLE_TIMEOUT_VALUE = 60;
    private static final String COFFEE_NUM = "coffee_num";
    private static final String COFFEE_SUGAR_LEVEL_WEIGHT = "coffee_sugar_level_weight";
    private Context mContext;
    private boolean foreground;
    private ImageView mPayBack;
    private RelativeLayout mPayCartLinear;
    private ImageView mPayCart;
    private TextView mPayCartIndicator;
    private ImageView mNetworkStatus;
    private ImageView mZhimg;
    private ImageView mEnimg;
    private ImageView mCoffeeImg;
    private TextView mCoffeeName;
    private TextView mCoffeeVolume;
    private TextView mCoffeePrice;
    private TextView mCoffeeOriPrice;
    private RadioGroup mInfo_sugar_group;
    private RadioGroup mInfo_num_group;
    private TextView mCoffeeSugarNotetitle;
    private TextView mCoffeeSugarNote;
    private TextView mCoffeeSugarTitle;
    private TextView mInfo_nosugar;
    private String mCoffeeNote;
    private LinearLayout mAddToCart;
    private ImageView mAnimImageView;
    private Animation mAnimation;
    private LinearLayout mAdd_to_cart;
    private LinearLayout mAliPay;
    private LinearLayout mWxPay;
//    private ImageView mAdd_to_cart_ali;
//    private ImageView mAliPay_ali;
    private CoffeeInfo mCoffeeInfo;
    private CountDownTimer countDownTimer;
    private PopupWindow mPopupWindow;
    private SugarLevel mAddSugarLevel;
    private boolean isShowAddSugarBar = false;
    private double sugarBase = 0;
    private boolean isGoToCartPay = false;
    private int coffeenum;
    private LinearLayout mAbcPay;
    private TextView tvCoffeeInfoDesc;

    public static void start(Activity activity, CoffeeInfo info,int buyNum,int sugarWeight) {
        Intent intent = new Intent();
        intent.setClass(activity, CoffeeInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(COFFEE_INFO, info);
        intent.putExtra(COFFEE_NUM,buyNum);
        intent.putExtra(COFFEE_SUGAR_LEVEL_WEIGHT,sugarWeight);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffee_info_layout);
        mContext = this;
        proceedExtra();

        initViews();
        initTimer();
        initAnimation();
        initStatus();
        initTitleLanguage();

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
            CoffeeInfoActivity.start(this, mCoffeeInfo,coffeenum,mAddSugarLevel.weight);
            finish();
        }
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        mCoffeeInfo = (CoffeeInfo) intent.getSerializableExtra(COFFEE_INFO);
        if (mCoffeeInfo != null) {
            LogUtil.vendor("[CoffeeInfoActivity] " + mCoffeeInfo.getCoffeeTitle());
            ArrayList<CoffeeDosingInfo> dosingList = mCoffeeInfo.getDosingList();
            for (int i = 0; i < dosingList.size(); i++) {
                CoffeeDosingInfo info = dosingList.get(i);
                LogUtil.vendor("[CoffeeInfoActivity] " + info.toString());
                if (info.getMacConifg() == 1 && info.getValue() > 0) {
                    isShowAddSugarBar = true;
                    sugarBase = info.getValue() / 2;
                }
            }

            if(getIntent().hasExtra(COFFEE_NUM)){
                coffeenum=getIntent().getIntExtra(COFFEE_NUM,1);
            }

        }
    }

    private void initViews() {
        mPayBack = (ImageView) findViewById(R.id.coffee_info_back);
        mPayBack.setOnClickListener(this);
        mPayCartLinear = (RelativeLayout) findViewById(R.id.coffee_shopping_cart_layout);
        mPayCartLinear.setVisibility(View.INVISIBLE);
        mPayCartLinear.setOnClickListener(this);
        tvCoffeeInfoDesc=(TextView)findViewById(R.id.tvCoffeeInfoDesc);

        mPayCart = (ImageView) findViewById(R.id.coffee_shopping_cart);
//        mPayCart.setOnClickListener(this);
        mPayCartIndicator = (TextView) findViewById(R.id.coffee_shopping_cart_indicator);
        mNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        mCoffeeImg = (ImageView) findViewById(R.id.coffee_info_img);
        mCoffeeName = (TextView) findViewById(R.id.coffee_info_name);
        mCoffeeVolume = (TextView) findViewById(R.id.coffee_info_volume);
        mCoffeePrice = (TextView) findViewById(R.id.coffee_info_price);
        mCoffeeOriPrice = (TextView) findViewById(R.id.coffee_info_ori_price);
        mCoffeeSugarNote = (TextView) findViewById(R.id.coffee_info_sugar_note);
        mCoffeeSugarNotetitle = (TextView) findViewById(R.id.coffee_info_sugar_note_title);
        mCoffeeSugarTitle = (TextView) findViewById(R.id.coffee_info_sugar_title);
        mInfo_nosugar = (TextView) findViewById(R.id.coffee_info_nosugar);
        mInfo_sugar_group = (RadioGroup) findViewById(R.id.coffee_info_sugar_group);
        String imgURL = mCoffeeInfo.getImgUrl() == null ? "" : mCoffeeInfo.getImgUrl();
//		ImageLoaderTool.disPlay(imgURL.trim(), mCoffeeImg, R.drawable.coffee_info_img_default);
        ImageLoaderTool.disPlay(CoffeeInfoActivity.this, imgURL.trim(), mCoffeeImg, R.drawable.coffee_info_img_default);
        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
            mCoffeeName.setText(mCoffeeInfo.getCoffeeTitleEn());
            tvCoffeeInfoDesc.setText(mCoffeeInfo.getDescEn());
        } else {
            mCoffeeName.setText(mCoffeeInfo.getCoffeeTitle());
            tvCoffeeInfoDesc.setText(mCoffeeInfo.getDesc());
        }


        mCoffeeNote = "";
        int volume = (int) mCoffeeInfo.getVolume();
        mCoffeeVolume.setText("(" + String.format(Locale.getDefault(), getString(R.string.coffee_info_volume_format), volume) + ")");
        if (volume < 100) {
            String html = null;
            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN)
                html = mCoffeeInfo.getCoffeeTitleEn()
                        + "only have <font color='#f74d3e'>" + volume + "</font>ML";
            else
                html = mCoffeeInfo.getCoffeeTitle()
                        + "的容量只有<font color='#f74d3e'>" + volume + "</font>ML";

            mCoffeeNote += Html.fromHtml(html) + "    ";
        }

        if (mCoffeeInfo.getPrice() == mCoffeeInfo.getDiscount()) {
            mCoffeeOriPrice.setVisibility(View.GONE);
            mCoffeePrice.setText(getString(R.string.coffee_info_price) + "¥" + mCoffeeInfo.getPrice());
        } else {
            mCoffeeOriPrice.setVisibility(View.VISIBLE);
            mCoffeeOriPrice.setText(getString(R.string.coffee_info_oriprice) + "¥" + mCoffeeInfo.getPrice());
            mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mCoffeeOriPrice.getPaint().setAntiAlias(true);
            mCoffeePrice.setText(getString(R.string.coffee_info_price) + "¥" + mCoffeeInfo.getDiscount());
        }

        if (isShowAddSugarBar) {
            if(getIntent().hasExtra(COFFEE_SUGAR_LEVEL_WEIGHT)){
                switch (getIntent().getIntExtra(COFFEE_SUGAR_LEVEL_WEIGHT,1)){
                    case 2:
                        mAddSugarLevel = SugarLevel.LITTLE;
                        break;
                    case 3:
                        mAddSugarLevel = SugarLevel.NORMAL;
                        break;
                    case 4:
                        mAddSugarLevel = SugarLevel.MORE;
                        break;
                    default:
                        mAddSugarLevel = SugarLevel.NONE;
                        break;
                }
            }

            mCoffeeNote += getString(R.string.coffee_info_sugar_note);
            mCoffeeSugarTitle.setText(getResources().getString(R.string.coffee_info_sugar_title)
                    + "(" + getResources().getString(R.string.coffee_info_onesugar) + "="
                    + sugarBase + "g）");
            mInfo_nosugar.setVisibility(View.GONE);
            mInfo_sugar_group.setVisibility(View.VISIBLE);
        } else {
            mAddSugarLevel = SugarLevel.NONEED;
            mCoffeeSugarTitle.setText(getResources().getString(R.string.coffee_info_sugar_title));
            mInfo_nosugar.setVisibility(View.VISIBLE);
            mInfo_sugar_group.setVisibility(View.GONE);
        }


        for (int i = 0; i < 4; i++) {
            final RadioButton rb = new RadioButton(this);
            rb.setId(i);
            rb.setButtonDrawable(android.R.color.transparent);

            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                if (i == 0)
                    rb.setPadding(52, 5, 0, 5);
                else
                    rb.setPadding(48, 5, 0, 5);
            } else {
                if (i == 0)
                    rb.setPadding(38, 5, 0, 5);
                else
                    rb.setPadding(43, 5, 0, 5);
            }

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);

            if (i == (mAddSugarLevel.weight-1)) {
                rb.setChecked(true);
                rb.setTextColor(Color.WHITE);
                rb.setBackgroundResource(R.drawable.radio_sugar_sel);
            } else {
                rb.setTextColor(getResources().getColor(R.color.norcolor));
                rb.setBackgroundResource(R.drawable.radio_sugar_nor);
            }
            if (i == 0) {
                rb.setText(R.string.coffee_info_nosugar);
            } else if (i == 1) {
                rb.setText(R.string.coffee_info_onesugar);
            } else if (i == 2) {
                rb.setText(R.string.coffee_info_twosugar);
            } else if (i == 3) {
                rb.setText(R.string.coffee_info_threesugar);
            }
            mInfo_sugar_group.addView(rb, params);
        }

        mInfo_sugar_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                for (int i = 0; i < 4; i++) {
                    if (i == checkedId) {

                        ((RadioButton) group.getChildAt(i)).setTextColor(Color.WHITE);
                        ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_sel);

                        if (i == 0) {
                            mAddSugarLevel = SugarLevel.NONE;
                        } else if (i == 1) {
                            mAddSugarLevel = SugarLevel.LITTLE;
                        } else if (i == 2) {
                            mAddSugarLevel = SugarLevel.NORMAL;
                        } else if (i == 3) {
                            mAddSugarLevel = SugarLevel.MORE;
                        }
                    } else {
                        ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.norcolor));
                        ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_nor);
                    }
                }
            }
        });

        mInfo_num_group = (RadioGroup) findViewById(R.id.coffee_info_num_group);
        for (int i = 0; i < 9; i++) {
            final RadioButton rb = new RadioButton(this);
            rb.setId(i);
            rb.setButtonDrawable(android.R.color.transparent);
            rb.setPadding(26, 5, 0, 5);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(ScreenUtil.dip2px(8), 0, ScreenUtil.dip2px(8), 0);

//            if (i == coffeenum) {
            if (i == (coffeenum-1)) {
                rb.setChecked(true);
                rb.setTextColor(Color.WHITE);
                rb.setBackgroundResource(R.drawable.radio_sel);
            } else {
                rb.setTextColor(getResources().getColor(R.color.norcolor));
                rb.setBackgroundResource(R.drawable.radio_nor);
            }
            int num = i + 1;
            rb.setText("" + num);
            mInfo_num_group.addView(rb, params);
        }

        mInfo_num_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                for (int i = 0; i < 9; i++) {
                    if (i == checkedId) {

                        ((RadioButton) group.getChildAt(i)).setTextColor(Color.WHITE);
                        ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sel);
                        coffeenum = i + 1;
                    } else {
                        ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.norcolor));
                        ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_nor);
                    }
                }
            }
        });

//        coffeenum = 1;




        boolean isSweet = mCoffeeInfo.isSweet();
        if (isSweet) {

            mCoffeeNote += getString(R.string.coffee_info_sweet_note) + "    ";
        }
        mAdd_to_cart = (LinearLayout) findViewById(R.id.coffee_info_add_to_cart);
        mAdd_to_cart.setOnClickListener(this);
        mAliPay = (LinearLayout) findViewById(R.id.coffee_info_alipay);
        mAliPay.setOnClickListener(this);
        mWxPay = (LinearLayout) findViewById(R.id.coffee_info_wxpay);
        mWxPay.setOnClickListener(this);
        mAbcPay = (LinearLayout) findViewById(R.id.coffee_info_abcpay);
        mAbcPay.setOnClickListener(this);
//        mAdd_to_cart_ali = (ImageView) findViewById(R.id.coffee_info_alipay_add_to_cart);
//        mAdd_to_cart_ali.setOnClickListener(this);
//        mAliPay_ali = (ImageView) findViewById(R.id.coffee_info_alipay_alipay);
//        mAliPay_ali.setOnClickListener(this);

        if (AppConfig.isMacForAli()) {
            mAliPay.setVisibility(View.VISIBLE);
        } else {
            mAliPay.setVisibility(View.GONE);
        }

        if (AppConfig.isMacForWechat()) {
            mWxPay.setVisibility(View.VISIBLE);
        } else {
            mWxPay.setVisibility(View.GONE);
        }

        if (AppConfig.isMacForAbc()) {
            mAbcPay.setVisibility(View.VISIBLE);
        } else {
            mAbcPay.setVisibility(View.GONE);
        }


        mAddToCart = (LinearLayout) findViewById(R.id.coffee_info_add_to_cart);
        mAddToCart.setOnClickListener(this);

        updateCartGoods();



        if (null != mCoffeeNote && mCoffeeNote.length() > 0) {
            mCoffeeSugarNotetitle.setVisibility(View.VISIBLE);
            mCoffeeSugarNote.setVisibility(View.VISIBLE);
            mCoffeeSugarNote.setText(mCoffeeNote);
        } else {
            mCoffeeSugarNotetitle.setVisibility(View.INVISIBLE);
            mCoffeeSugarNote.setVisibility(View.INVISIBLE);
        }
    }

    private void initTimer() {
        countDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
    }

    private void onCountDown(int value) {
        if (value == 0 && foreground) {
            this.finish();
        }
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

    private void updateCartGoods() {
        int num = MyApplication.Instance().getCartNums();
        if (num > 0) {
            mPayCartLinear.setVisibility(View.VISIBLE);
            mPayCartIndicator.setText(String.valueOf(num));
        } else {
            mPayCartLinear.setVisibility(View.INVISIBLE);
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffee_info_back:
                this.finish();
                ;
                break;
            case R.id.coffee_shopping_cart_layout:
                switchToCart();
                break;
            case R.id.coffee_info_add_to_cart:
                if (mCoffeeInfo.isPackage()) {
                    if (!CoffeeUtil.isExcceedCartLimit((coffeenum * mCoffeeInfo.getPackageNum()))) {
                        CartPayItem item = new CartPayItem();
                        item.setCoffeeInfo(mCoffeeInfo);
                        item.setBuyNum(coffeenum);
                        for (int i = 0; i < item.getCoffeeInfo().getPackageNum(); i++) {

                            if (needSugar(item.getCoffeeInfo())) {
                                item.setPackageSugarLevel(i, 1);
                            } else {
                                item.setPackageSugarLevel(i, 0);
                            }
                        }
                        MyApplication.Instance().addCoffeeToCartPay(item);

                        mAnimImageView.setVisibility(View.VISIBLE);
                        mAnimImageView.startAnimation(mAnimation);
                    } else {
                        ToastUtil.showToast(this, R.string.cart_exceeds_max_num);
                    }

                } else {

                    if (!CoffeeUtil.isExcceedCartLimit(coffeenum)) {
                        CartPayItem item = new CartPayItem();
                        item.setCoffeeInfo(mCoffeeInfo);
                        item.setBuyNum(coffeenum);
                        item.setSugarLevel(mAddSugarLevel.getWeight());
                        MyApplication.Instance().addCoffeeToCartPay(item);

                        mAnimImageView.setVisibility(View.VISIBLE);
                        mAnimImageView.startAnimation(mAnimation);
                    } else {
                        ToastUtil.showToast(this, R.string.cart_exceeds_max_num);
                    }
                }
                break;

            case R.id.coffee_info_alipay:
                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.AliQr.tag);
                break;

            case R.id.coffee_info_wxpay:

                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.WeiXin.tag);
                break;
            case R.id.coffee_info_abcpay:
                onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.Abc.tag);
                break;
            default:
                break;
        }
    }

    private void onCoffeePay(int payMethod) {

//        if (payMethod != PayCoffeeQrcodeActivity.PayMethod.AliWa.tag) {
        String coffeeIndents = getCoffeeIndents();
        PayCoffeeQrcodeActivity.start(this, coffeeIndents, payMethod);
//        }
    }

    private String getCoffeeIndents() {
        JSONArray coffeeIndents = new JSONArray();
        int num = coffeenum;
        while (num-- > 0) {
            CoffeeInfo coffeeInfo = mCoffeeInfo;
            if (coffeeInfo != null) {
                int goodSid = coffeeInfo.getCoffeeId();
                int sugarLevel = mAddSugarLevel.getWeight();

                JSONArray dosings = new JSONArray();
                ArrayList<CoffeeDosingInfo> dosingList = coffeeInfo.getDosingList();
                for (int j = 0; j < dosingList.size(); j++) {
                    CoffeeDosingInfo dosing = dosingList.get(j);
                    if (dosing.getMacConifg() == 1) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("dosingID", dosing.getId());

                        double sugarWeight = 0;
                        if (sugarLevel == CoffeeInfoActivity.SugarLevel.NONE.getWeight()) {
                            sugarWeight = 0;
                        } else if (sugarLevel == CoffeeInfoActivity.SugarLevel.LITTLE.getWeight()) {
                            sugarWeight = dosing.getValue() * 0.5;
                        } else if (sugarLevel == CoffeeInfoActivity.SugarLevel.NORMAL.getWeight()) {
                            sugarWeight = dosing.getValue();
                        } else if (sugarLevel == CoffeeInfoActivity.SugarLevel.MORE.getWeight()) {
                            sugarWeight = dosing.getValue() * 1.5;
                        }

                        jsonObj.put("value", sugarWeight);
                        dosings.add(jsonObj);
                        break;
                    }
                }
                JSONObject indent = new JSONObject();
                indent.put("goodsid", goodSid);
                indent.put("dosing", dosings.toString());
                indent.put("level", sugarLevel);

                coffeeIndents.add(indent);
            }
        }
        return coffeeIndents.toString();
    }

    private void switchToCart() {
        isGoToCartPay = true;
        PayCartActivity.start(this);
    }

    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("vendor", "CoffeeInfoPage->onStart");
        if (countDownTimer != null)
            countDownTimer.startCountDownTimer(IDLE_TIMEOUT_VALUE, 1000, 1000);
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

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.e("vendor", "CoffeeInfoPage->onStop");
        if (countDownTimer != null)
            countDownTimer.cancelCountDownTimer();
        dismissPopupWindow();
    }

    @Override
    public void onReceive(Remote remote) {
        // system action
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
//        else if (remote.getWhat() == ITranCode.ACT_COFFEE) {
//            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {
//
//                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
//                if (result != null && result.getResCode() == 200) {
//                    String type = result.getType();
//					if(type.equals("102")) {
//
//						GetCoffeeInfo info = new GetCoffeeInfo();
//						info.setUid(U.getMyVendorNum());
//						executeBackground(info.toRemote());
//					}
//					else if(type.equals("103")) {
//
//						GetAdvPicsInfo info = new GetAdvPicsInfo();
//						info.setUid(U.getMyVendorNum());
//						execute(info.toRemote());
//					}
//                }
//            }
//        }
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

    public enum SugarLevel {

        NONEED("不加糖", 0), NONE("无糖", 1), LITTLE("少糖", 2), NORMAL("普通", 3), MORE("多糖", 4);

        private String name;
        private int weight;

        private SugarLevel(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
