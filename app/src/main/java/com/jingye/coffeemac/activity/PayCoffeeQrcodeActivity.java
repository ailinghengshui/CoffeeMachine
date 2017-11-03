package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.barcode.QRCodeEncoder;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.beans.OrderContentItem;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.module.makecoffeemodule.NewMakeCoffeeExActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.CancelTradeCartInfo;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.action.PayQrcodeCartInfo;
import com.jingye.coffeemac.service.bean.action.PayStatusAskCartInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.PayNotifyResult;
import com.jingye.coffeemac.service.bean.result.PayQrcodeCartResult;
import com.jingye.coffeemac.service.bean.result.PayStatusAskCartResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.CountDownTimer.CountDownCallback;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class PayCoffeeQrcodeActivity extends TActivity implements OnClickListener {

    public static final String TAG = "PayCoffeeQrcodeActivity -> ";

    public static final String COFFEE_INDENTS = "coffee_indents";
    public static final String COFFEE_PAY_METHOD = "coffee_pay";

    private boolean foreground;

    private ImageView mNetworkStatus;

    private ImageView mPayBack;
    private TextView mPayTimer;
    private TextView mPayOperationTip;
    private TextView mGetCoffeeProcess;
    private ImageView mPayQrcode;
    private RelativeLayout mPayDetailParent;
    private TextView mPayDetailTotaltitle;
    private TextView mPayDetailFavortitle;
    private TextView mPayDetailActualtitle;
    private ImageView mPayDetailTotalimg;
    private ImageView mPayDetailFavorimg;
    private ImageView mPayDetailActualimg;
    private TextView mPayDetailTotal;
    private TextView mPayDetailFavor;
    private TextView mPayDetailActual;
    private TextView mStatetitle;

    private String mCoffeeIndents;
    private int mPayMethod;
    private CountDownTimer countDownTimer;

    private String mPayIndent;
    private ArrayList<OrderContentItem> mOrderItems = new ArrayList<OrderContentItem>();

    private AtomicInteger payAction = new AtomicInteger(0);  // fail:-1 success:1

    public static void start(Activity activity, String coffeeIndents, int payMethod) {
        Intent intent = new Intent();
        intent.setClass(activity, PayCoffeeQrcodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(COFFEE_INDENTS, coffeeIndents);
        intent.putExtra(COFFEE_PAY_METHOD, payMethod);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_coffee_qrcode_layout);
        proceedExtra();

        initViews();
        initPay();
        initStatus();

        requestPay();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.vendor(TAG + "->onNewIntent");
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            mCoffeeIndents = intent.getStringExtra(COFFEE_INDENTS);
            mPayMethod = intent.getIntExtra(COFFEE_PAY_METHOD, 1);
        }
    }

    private void initViews() {

        mPayBack = (ImageView) findViewById(R.id.pay_qrcode_back_btn);
        mPayTimer = (TextView) findViewById(R.id.pay_qrcode_timer);
        mNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);
        mPayBack.setOnClickListener(this);

        mPayOperationTip = (TextView) findViewById(R.id.pay_coffee_operation_tip);
        mPayOperationTip.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mGetCoffeeProcess = (TextView) findViewById(R.id.pay_coffee_process_tip);

        mPayQrcode = (ImageView) findViewById(R.id.pay_coffee_qrcode);

        mPayDetailParent = (RelativeLayout) findViewById(R.id.pay_coffee_detail_linear);
        mPayDetailTotaltitle = (TextView) findViewById(R.id.pay_coffee_detail_totoal_price_title);
        mPayDetailFavortitle = (TextView) findViewById(R.id.pay_coffee_detail_favor_title);
        mPayDetailActualtitle = (TextView) findViewById(R.id.pay_coffee_detail_actual_pay_title);
        mPayDetailTotalimg = (ImageView) findViewById(R.id.pay_coffee_detail_totoal_price_img);
        mPayDetailFavorimg = (ImageView) findViewById(R.id.pay_coffee_detail_favor_img);
        mPayDetailActualimg = (ImageView) findViewById(R.id.pay_coffee_detail_actual_pay_img);
        mPayDetailTotal = (TextView) findViewById(R.id.pay_coffee_detail_totoal_price);
        mPayDetailFavor = (TextView) findViewById(R.id.pay_coffee_detail_favor);
        mPayDetailActual = (TextView) findViewById(R.id.pay_coffee_detail_actual_pay);
        mStatetitle = (TextView) findViewById(R.id.pay_coffee_process_tip_title);

    }

    private void initPay() {
        mGetCoffeeProcess.setText(R.string.pay_generate_qrcode);
        onChangePayMethod(mPayMethod);
        countDownTimer = new CountDownTimer(new CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
        countDownTimer.startCountDownTimer(90, 1000, 1000);
    }

    private void onChangePayMethod(int payMethod) {
        if (payMethod == PayMethod.AliQr.tag) {
            mPayOperationTip.setText(R.string.pay_qrcode_ali_info);
        } else if (payMethod == PayMethod.WeiXin.tag) {
            mPayOperationTip.setText(R.string.pay_qrcode_wx_info);
        } else if (payMethod == PayMethod.Abc.tag) {
            mPayOperationTip.setText(R.string.pay_qrcode_abc_info);
        } else {
            LogUtil.vendor("unknown pay method");
        }
    }

    private void onCountDown(int value) {
        LogUtil.vendor(TAG + value);
        mPayTimer.setText(String.format(this.getString(R.string.pay_timer_tip_left), value));
        if (payAction.get() == 0 && (value == 60 || value == 45
                || value == 30 || value == 15)) {
            askPayStatus(mPayIndent);
        }

        if (value == 0 && payAction.get() != 1 && foreground) {
            LogUtil.vendor("GO HOME @PayCoffeeQrcode");
            if (payAction.compareAndSet(0, -1)) {
                cancelTrade();
            }
            HomePageActivity.start(this, false, 0);
            finish();
        }
    }

    private void requestPay() {
        if (!isReachable()) {
            ToastUtil.showToast(PayCoffeeQrcodeActivity.this, R.string.network_is_not_available);
            return;
        }

        ProgressDlgHelper.showProgress(this, getString(R.string.pay_request_qrcode));
        PayQrcodeCartInfo info = new PayQrcodeCartInfo();
        info.setUid(U.getMyVendorNum());
        info.setCoffeeIndents(mCoffeeIndents);
        info.setProvider((short) mPayMethod);
        execute(info.toRemote());
    }

    private boolean isReachable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    private void askPayStatus(String coffeeIndent) {
        if (!TextUtils.isEmpty(coffeeIndent)) {
            PayStatusAskCartInfo info = new PayStatusAskCartInfo();
            info.setUid(U.getMyVendorNum());
            info.setPayIndent(mPayIndent);
            execute(info.toRemote());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_qrcode_back_btn:
                if (payAction.compareAndSet(0, -1)) {
//                if(countDownTimer != null){
                    countDownTimer.cancelCountDownTimer();
//                }
                    cancelTrade();
                    this.finish();
                }
                break;
            default:
                break;
        }
    }

    public void cancelTrade() {
        if (!TextUtils.isEmpty(mPayIndent)) {
            CancelTradeCartInfo info = new CancelTradeCartInfo();
            info.setUid(U.getMyVendorNum());
            info.setPayIndent(mPayIndent);
            execute(info.toRemote());
        }
    }

    @Override
    public void onReceive(Remote remote) {
        // coffee action
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_PAY_QRCODE_CART) {
                ProgressDlgHelper.closeProgress();
                PayQrcodeCartResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null) {
                    if (result.getResCode() == 200) {
                        onReceivePayQRCode(result);
                    } else {
                        ToastUtil.showToast(this, parseError(result.getResCode()));
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_PAY_NOTIFY) {
                PayNotifyResult result = GeneralActionResult.parseObject(remote.getBody());
//				if(result != null){
//					if(result.getResCode() == 200){
//						doPaySuccessful();
//					}else{
//                        ToastUtil.showToast(this, parseError(result.getResCode()));
//					}
//				}

                if (result != null && result.getResCode() == 200) {
                    String indent = result.getCoffeeIndent();
                    if (!TextUtils.isEmpty(mPayIndent) && !TextUtils.isEmpty(indent)
                            && mPayIndent.equals(indent)) {
                        if (foreground) {
                            doPaySuccessful();
                        } else {
                            LogUtil.vendor(TAG + " filter this " + indent);
                        }
                    }
                } else {
                    if (result == null) {

                    } else {
                        ToastUtil.showToast(this, parseError(result.getResCode()));
                    }
                }

            } else if (remote.getAction() == ITranCode.ACT_COFFEE_ASK_CART_PAY_RESULT) {
                PayStatusAskCartResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String indent = result.getPayIndent();
                    if (!TextUtils.isEmpty(mPayIndent) && !TextUtils.isEmpty(indent)
                            && mPayIndent.equals(indent)) {
                        if (foreground) {
                            doPaySuccessful();
                        } else {
                            LogUtil.vendor(TAG + " filter this " + indent);
                        }
                    }
                }
            }
//			else if(remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE){
//
//				ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
//				if(result != null && result.getResCode() == 200){
//					String type = result.getType();
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
//				}
//			}
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

    private void doPaySuccessful() {
        MyApplication.Instance().clearCartPay();
        LogUtil.vendor("doPaySuccessful() prepare");
        if (payAction.compareAndSet(0, 1)) {
            LogUtil.vendor("doPaySuccessful() start");
            mPayTimer.setText("");
            mGetCoffeeProcess.setText(String.format(Locale.getDefault(), getString(R.string.pay_pay_success)));
//			if(countDownTimer != null){
            countDownTimer.cancelCountDownTimer();
//				countDownTimer = null;
//			}

            OrderContent orderContent = new OrderContent();
            orderContent.setOrderID(mPayIndent);
            orderContent.setItems(mOrderItems);
            NewMakeCoffeeExActivity.start(this, orderContent, true);
            finish();
        }
    }

    private void onReceivePayQRCode(PayQrcodeCartResult result) {
        mPayIndent = result.getPayIndent();
        mOrderItems.clear();
        String resultCoffeeIndents = result.getCoffeeIndents();
        if (!TextUtils.isEmpty(resultCoffeeIndents)) {
            try {
                JSONArray array = JSON.parseArray(resultCoffeeIndents);
                if (array != null && array.size() > 0) {
                    int size = array.size();
                    for (int i = 0; i < size; ++i) {
                        OrderContentItem item = new OrderContentItem();
                        // parse from json
                        JSONObject jsonObject = array.getJSONObject(i);
                        int itemID = -1;
                        if (jsonObject.containsKey("goodsid")) {
                            itemID = jsonObject.getIntValue("goodsid");
                            item.setItemID(String.valueOf(itemID));
                            String itemName = MyApplication.Instance().getCoffeeNameByCoffeeID(itemID);
                            item.setItemName(itemName);
                        }
                        if (jsonObject.containsKey("indentid")) {
                            String indentID = jsonObject.getString("indentid");
                            item.setGoodID(indentID);
                        }
                        item.setAddIce(MyApplication.Instance().getCoffeeInfoByCoffeeID(itemID).isAddIce());
                        if (jsonObject.containsKey("dosing")) {
                            ArrayList<CoffeeDosingInfo> dosingList = MyApplication.Instance().getDosingListInfoByCoffeeID(itemID);
                            String dosingStr = jsonObject.getString("dosing");
                            JSONArray dosingArray = JSON.parseArray(dosingStr);
                            if (dosingArray != null && dosingArray.size() > 0) {
                                int dosingSize = dosingArray.size();
                                for (int j = 0; j < dosingSize; ++j) {
                                    JSONObject dosingJB = dosingArray.getJSONObject(j);
                                    int dosingID = -1;
                                    double value = -1;
                                    if (dosingJB.containsKey("dosingID")) {
                                        dosingID = dosingJB.getIntValue("dosingID");
                                    }
                                    if (dosingJB.containsKey("value")) {
                                        value = dosingJB.getDoubleValue("value");
                                    }

                                    for (int k = 0; k < dosingList.size(); k++) {
                                        CoffeeDosingInfo base = dosingList.get(k);
                                        if (base != null && dosingID == base.getId()) {
                                            base.setValue(value);
                                            break;
                                        }
                                    }
                                }
                            }
                            item.setDosings(dosingList);
                        }
                        if (jsonObject.containsKey("level")) {
                            int level = jsonObject.getIntValue("level");
                            item.setSweetLevel(level);
                        }

                        mOrderItems.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "something error on parse json");
            }
        } else {
            LogUtil.e(TAG, "Coffee Indents is null");
        }

        mGetCoffeeProcess.setText(String.format(Locale.getDefault(), getString(R.string.pay_waiting_scan)));
        String qrcodeURL = result.getQrCodeUrl();
        if (!TextUtils.isEmpty(qrcodeURL)) {
            Bitmap logobitmap = null;
            if (mPayMethod == PayMethod.AliQr.tag) {
                logobitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alilogo);
            } else if (mPayMethod == PayMethod.WeiXin.tag) {
                logobitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wxlogo);
            } else if (mPayMethod == PayMethod.Abc.tag) {
                logobitmap = BitmapFactory.decodeResource(getResources(), R.drawable.abclogo);
            }

            Bitmap qrcodeAndroid = generateQrCodeBitmap(qrcodeURL, 360);
            if (null != logobitmap)
                qrcodeAndroid = QRCodeEncoder.addLogo(qrcodeAndroid, logobitmap);
            if (qrcodeAndroid != null) {
                mPayQrcode.setVisibility(View.VISIBLE);
                mPayQrcode.setImageBitmap(qrcodeAndroid);
            }
        }
        mPayDetailParent.setVisibility(View.VISIBLE);
        mPayDetailTotaltitle.setVisibility(View.VISIBLE);
        mPayDetailFavortitle.setVisibility(View.VISIBLE);
        mPayDetailActualtitle.setVisibility(View.VISIBLE);
        mPayDetailTotal.setVisibility(View.VISIBLE);
        mPayDetailFavor.setVisibility(View.VISIBLE);
        mPayDetailActual.setVisibility(View.VISIBLE);
        mPayDetailTotalimg.setVisibility(View.VISIBLE);
        mPayDetailFavorimg.setVisibility(View.VISIBLE);
        mPayDetailActualimg.setVisibility(View.VISIBLE);
        mStatetitle.setVisibility(View.VISIBLE);
        double priceOri = Double.parseDouble(result.getPriceOri());
        double price = Double.parseDouble(result.getPrice());
        double favour = priceOri - price;
        mPayDetailTotal.setText(String.format(Locale.getDefault(), "¥%.2f", priceOri));
        if (favour < 0.01) {
            mPayDetailFavor.setVisibility(View.GONE);
        } else {
            mPayDetailFavor.setText(String.format(Locale.getDefault(), "¥%.2f", favour));
        }
        mPayDetailActual.setText(String.format(Locale.getDefault(), "¥%.2f", price));
    }

    private Bitmap generateQrCodeBitmap(String content, int size) {
        Bitmap bitmap = null;
        try {
            if (!TextUtils.isEmpty(content)) {
                bitmap = QRCodeEncoder.getQrCodeBitmap(this, content, size);
            }
        } catch (Exception e) {
            LogUtil.e("PayCoffeeCart", "createQRCode error:" + e.getMessage());
        }
        return bitmap;
    }

    private int parseError(int resCode) {
        int resId = 0;
        switch (resCode) {
            case 501:
                resId = R.string.pay_error_waiting_pay;
                break;
            case 502:
                resId = R.string.pay_error_cancel_indent;
                break;
            case ResponseCode.RES_ETIMEOUT:
                resId = R.string.pay_error_timeout;
                break;
            case 403:
                resId = R.string.exchange_error_dosing_not_enough;
                break;
            case 307:
                resId = R.string.pay_no_coupon;
                break;
            case 308:
                resId = R.string.pay_restore_price;
                break;
            default:
                resId = R.string.exchange_error_default;
                break;
        }

        return resId;
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
        countDownTimer.cancelCountDownTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ProgressDlgHelper.closeProgress();
    }

    public enum PayMethod {
        AliQr(1),
        WeiXin(2),
        AliWa(3),
        Abc(4),;

        public int tag;

        PayMethod(int tag) {
            this.tag = tag;
        }
    }
}
