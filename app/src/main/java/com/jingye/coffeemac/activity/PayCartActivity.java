package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.adapter.PayCartPackageViewHolder;
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
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PayCartActivity extends TActivity implements OnClickListener, TViewHolder.ViewHolderEventListener {

    private static final String TAG = PayCartActivity.class.getSimpleName();
    private Context mContext;

    private boolean foreground;

    private ImageView mBackBtn;
    private ImageView mBackHome;
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
    private LinearLayout mAbcPayCart;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PayCartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_cart_layout);
        mContext = this;
        proceedExtra();

        initViews();
        initTimer();
        setupCartList();
        initStatus();
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        if (intent != null) {
        }
    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.pay_cart_back);
        mBackBtn.setOnClickListener(this);
        mBackHome = (ImageView) findViewById(R.id.pay_cart_back_home);
        mBackHome.setOnClickListener(this);
        mNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        mTotalPrice = (TextView) findViewById(R.id.pay_cart_total_price);
        mTotalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTotalPrice.getPaint().setAntiAlias(true);
        mActualPay = (TextView) findViewById(R.id.pay_cart_actual_price);

        mAliPayCart = (LinearLayout) findViewById(R.id.pay_cart_alipay_btn);
        mAliPayCart.setOnClickListener(this);

        mWxPayCart = (LinearLayout) findViewById(R.id.pay_cart_wxpay_btn);
        mWxPayCart.setOnClickListener(this);
        mAbcPayCart = (LinearLayout) findViewById(R.id.pay_cart_abc_btn);
        mAbcPayCart.setOnClickListener(this);

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

        if (AppConfig.isMacForAbc()) {
            mAbcPayCart.setVisibility(View.VISIBLE);
        } else {
            mAbcPayCart.setVisibility(View.GONE);
        }

    }

    private void setupCartList() {
        // update items from global cache
        mCartPayItems = MyApplication.Instance().getCartPayItems();

        mListView = (TListView) findViewById(R.id.pay_cart_list);
        Map<Integer, Class> viewHolders = new HashMap<Integer, Class>();
        viewHolders.put(0, PayCartViewHolder.class);
        viewHolders.put(1, PayCartPackageViewHolder.class);
        mAdapter = new TAdapter(this, this, viewHolders, mCartPayItems);
        mListView.setAdapter(mAdapter);

        getDiscountInfo();
        updateTotalPrice();
    }

    private void getDiscountInfo() {
        GetDiscountResult discountInfo = MyApplication.Instance().getDiscountInfo();
        if (discountInfo != null) {
            tempDiscount = discountInfo.getDiscount();
            tempReductMeet = discountInfo.getReductMeet();
            tempReductSub = discountInfo.getReductSub();
        }
    }

    private void updateTotalPrice() {
        try {
            double total = 0;
            int totalNum = 0;
            for (CartPayItem item : mCartPayItems) {

                Log.d(TAG, "TAG:" + item.getCoffeeInfo().getDiscount());
                if (!item.getCoffeeInfo().isPackage()) {
                    total += (item.getCoffeeInfo().getDiscount() * item.getBuyNum());
                    totalNum += item.getBuyNum();
                }
            }
            BigDecimal totalPrice = new BigDecimal(String.valueOf(total));
            // 是否折上折
            BigDecimal favourDiscout = new BigDecimal("0.00000001");
            if (tempDiscount != null && new BigDecimal(tempDiscount).compareTo(new BigDecimal("0.00000001")) == 1) {
                favourDiscout = (new BigDecimal("1").subtract(new BigDecimal(tempDiscount))).multiply(totalPrice);
            }
            // 是否有满减
            BigDecimal favourMeetsub = new BigDecimal("0");
            if (tempReductMeet != null && new BigDecimal(tempReductMeet).compareTo(new BigDecimal("0.00000001")) == 1) {
                if (totalPrice.subtract(favourDiscout).subtract(new BigDecimal(tempReductMeet)).compareTo(new BigDecimal("0")) != -1) {
                    favourMeetsub = new BigDecimal(tempReductSub);
                }
            }
            BigDecimal actualPay = totalPrice.subtract(favourMeetsub).subtract(favourDiscout);

            for (CartPayItem item : mCartPayItems) {
                if (item.getCoffeeInfo().isPackage()) {
                    Log.d(TAG, "TAG" + String.valueOf(item.getCoffeeInfo().getDiscount() * item.getBuyNum()));
                    actualPay = actualPay.add(new BigDecimal(String.valueOf(item.getCoffeeInfo().getDiscount() * item.getBuyNum())));
                    totalNum += item.getBuyNum();
                }
            }

            // PRICE
            mActualPay.setText(String.format(Locale.getDefault(), getString(R.string.cart_actual_pay),
                    actualPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));


            total = 0;
            for (CartPayItem item : mCartPayItems) {
                total += (item.getCoffeeInfo().getPrice() * item.getBuyNum());
            }
            totalPrice = new BigDecimal(String.valueOf(total));
            mTotalPrice.setText(String.format(Locale.getDefault(), getString(R.string.cart_total_price), totalPrice.doubleValue()));


        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("PAY CART", "UPDATE PRICE ERROR!");
        }
    }

    private void initTimer() {
        mCountDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                LogUtil.vendor("value:"+value);
                onCountDown(value);
            }
        });
    }

    private void onCountDown(int value) {
        if (value == 0 && foreground) {
            LogUtil.vendor("GO HOME @PayCart");
            HomePageActivity.start(this, false, 0);
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
            default:
                break;
        }
    }

    private void switchToHome() {
        HomePageActivity.start(this, false, 0);
    }

    private void onCoffeePay(int payMethod) {
        if (mCartPayItems == null || mCartPayItems.size() <= 0) {
            ToastUtil.showToast(mContext, R.string.pay_cart_no_drinks_to_pay);
            return;
        }

//        if (payMethod == PayCoffeeQrcodeActivity.PayMethod.AliWa.tag) {
//
//        } else {
        String coffeeIndents = getCoffeeIndents();
        LogUtil.vendor("coffeeIndents->" + coffeeIndents);
        PayCoffeeQrcodeActivity.start(this, coffeeIndents, payMethod);
//        }
    }


    private String getCoffeeIndents() {
        JSONArray coffeeIndents = new JSONArray();
        for (int i = 0; i < mCartPayItems.size(); i++) {
            CartPayItem item = mCartPayItems.get(i);
            int num = item.getBuyNum();
            while (num-- > 0) {
                CoffeeInfo coffeeInfo = item.getCoffeeInfo();
                if (coffeeInfo != null) {
                    if (coffeeInfo.isPackage()) {
                        for (int k = 0; k < processPackageCoffee(item).size(); k++) {
                            coffeeIndents.add(processPackageCoffee(item).get(k));
                        }

                    } else {
                        int goodSid = coffeeInfo.getCoffeeId();
                        JSONArray dosings = new JSONArray();
                        ArrayList<CoffeeDosingInfo> dosingList = coffeeInfo.getDosingList();
                        for (int j = 0; j < dosingList.size(); j++) {
                            CoffeeDosingInfo dosing = dosingList.get(j);
                            if (dosing.getMacConifg() == 1) {
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("dosingID", dosing.getId());

                                double sugarWeight = 0;
                                int sugarLevel = item.getSugarLevel();
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
                        indent.put("level", item.getSugarLevel());
                        coffeeIndents.add(indent);
                    }
                }
            }
        }

        Log.d(TAG, coffeeIndents.toString());
        return coffeeIndents.toString();
    }


    //	[{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1},{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1},{"dosing":"[{\"dosingID\":9,\"value\":0}]","goodsid":1401,"level":1}]
//	packageid
    private List<JSONObject> processPackageCoffee(CartPayItem item) {
        CoffeeInfo coffeeInfo = item.getCoffeeInfo();
        List<JSONObject> packageJSON = new ArrayList<JSONObject>();

        for (int i = 0; i < coffeeInfo.getCoffeesPackage().size(); i++) {

            JSONObject object = new JSONObject();
            object.put("goodsid", coffeeInfo.getCoffeesPackage().get(i).getCoffeeId());
            object.put("packageid", coffeeInfo.getCoffeeId());

            JSONArray dosings = new JSONArray();

            List<PackageCoffeeDosingInfo> dosingList = coffeeInfo.getCoffeesPackage().get(i).getPackageDoing();
            for (int j = 0; j < dosingList.size(); j++) {
                PackageCoffeeDosingInfo dosing = dosingList.get(j);
                if (dosing.isMachine_configured() == 1) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("dosingID", dosing.getId());

                    double sugarWeight = 0;
                    int sugarLevel = item.getPackageSugarLevel(i);
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

            object.put("dosing", dosings.toString());
            object.put("level", item.getPackageSugarLevel(i));

            packageJSON.add(object);
        }
        return packageJSON;

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

    @Override
    public boolean onViewHolderLongClick(View view, TListItem item) {
        return false;
    }

    @Override
    public boolean onViewHolderClick(View view, TListItem item) {
        if (item instanceof CartPayItem) {
            CartPayItem cartPayItem = (CartPayItem) item;
            MyApplication.Instance().removeCartPay(cartPayItem);
            for (int i = 0; i < mCartPayItems.size(); i++) {
                CartPayItem cpi = mCartPayItems.get(i);
                if (cpi.getCoffeeInfo().isPackage()) {
                    if (cpi.getCoffeeInfo().getCoffeeId() == cartPayItem.getCoffeeInfo().getCoffeeId()
                            && cpi.getPackageSugarSize() == cartPayItem.getPackageSugarSize()
                            && cpi.getPackageSugarLevelMap().equals(cartPayItem.getPackageSugarLevelMap())) {
                        mCartPayItems.remove(i);
                        break;
                    }

                } else {
                    if (cpi.getCoffeeInfo().getCoffeeId() == cartPayItem.getCoffeeInfo().getCoffeeId()
                            && cpi.getSugarLevel() == cartPayItem.getSugarLevel()) {
                        mCartPayItems.remove(i);
                        break;
                    }
                }
            }
            mAdapter.notifyDataSetChanged();

            updateTotalPrice();

            return true;
        }

        return false;
    }

    @Override
    public void onItemChangeNotify() {
        updateTotalPrice();
    }

    @Override
    public void onStart() {
        super.onStart();
        startTimer();
    }

    private void startTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.startCountDownTimer(60, 1000, 1000);

    }

    private void stopTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancelCountDownTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        foreground = false;
    }
}