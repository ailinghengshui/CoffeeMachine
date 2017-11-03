package com.jingye.coffeemac.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.MakeCoffeeExActivity;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.domain.OrderFetchStatus;
import com.jingye.coffeemac.module.makecoffeemodule.NewMakeCoffeeExActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.ExchangeCoffeeByCodeInfo;
import com.jingye.coffeemac.service.bean.result.ExchangeCoffeeByCodeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.ui.DigitsEditText;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.NetworkUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

public class ExchangeFragment extends TFragment implements OnClickListener {

    private static final String TAG = ExchangeFragment.class.getSimpleName();
    private DigitsEditText mEditText;

    private boolean canExchange = false;

    private OnTouchScreenListener touchScreenListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (touchScreenListener == null) {
            touchScreenListener = (OnTouchScreenListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initStatus();
    }

    private void initStatus() {
        int status = U.getUserStatus(getActivity());
        updateStatus(status);
    }

    private void updateStatus(int status) {

        if (status == ITranCode.STATUS_NO_NETWORK
                || status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN
                || status == ITranCode.STATUS_LOGGING) {
            canExchange = false;
        } else {
            canExchange = true;
        }

    }

    private void initView() {
        initEditText();
        initDigitsPad();
    }

    private void initEditText() {
        mEditText = (DigitsEditText) getView().findViewById(R.id.exchange_input_edit);
    }

    private void initDigitsPad() {
        getView().findViewById(R.id.keyboard_number1).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number2).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number3).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_back).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number4).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number5).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number6).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_clear).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number7).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number8).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number9).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_star).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number0).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_pound).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyboard_number1:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_1);
                break;
            case R.id.keyboard_number2:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_2);
                break;
            case R.id.keyboard_number3:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_3);
                break;
            case R.id.keyboard_number4:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_4);
                break;
            case R.id.keyboard_number5:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_5);
                break;
            case R.id.keyboard_number6:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_6);
                break;
            case R.id.keyboard_number7:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_7);
                break;
            case R.id.keyboard_number8:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_8);
                break;
            case R.id.keyboard_number9:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_9);
                break;
            case R.id.keyboard_number0:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_0);
                break;
            case R.id.keyboard_back:
                onTouchScreen();
                onDelete();
                break;
            case R.id.keyboard_clear:
                onTouchScreen();
                onDeleteAll();
                break;
            case R.id.keyboard_star:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_STAR);
                break;
            case R.id.keyboard_pound:
                onTouchScreen();
                keyPressed(KeyEvent.KEYCODE_POUND);
                break;
            case R.id.keyboard_sure:
                onTouchScreen();
                doExchangeCoffee();
                onDeleteAll();
                break;
            default:
                break;
        }
    }

    private void onTouchScreen() {
        if (touchScreenListener != null) {
            touchScreenListener.OnTouchScreenExchange();
        }
    }

    private void keyPressed(int keyCode) {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mEditText.onKeyDown(keyCode, event);
    }

    private void onDeleteAll() {
        final Editable digits = mEditText.getText();
        if (digits != null)
            digits.clear();
    }

    private void onDelete() {
        keyPressed(KeyEvent.KEYCODE_DEL);
    }

    private void doExchangeCoffee() {
        // check network
        if (!NetworkUtil.isNetAvailable(getActivity())) {
            ToastUtil.showToast(getActivity(), R.string.network_failed_unavailable);
            return;
        }

        if (!canExchange) {
            ToastUtil.showToast(getActivity(), R.string.exchange_error_netorlogin);
            return;
        }

        // check code
        String fetchCode = mEditText.getText().toString();
        if (TextUtils.isEmpty(fetchCode)) {
            ToastUtil.showToast(getActivity(), getString(R.string.exchange_code_is_null));
            return;
        }
        // check the old status
        OrderFetchStatus orderStatus = MyApplication.Instance().getIndentStatus(fetchCode);
        if (orderStatus != null && orderStatus.getStatus() == OrderFetchStatus.ORDER_STATUS_REQUESTING) {
            ToastUtil.showToast(getActivity(), R.string.exchange_is_requesting);
            return;
        }
        // set the status is requesting
        long timestamp = TimeUtil.getNow_millisecond();
        if (orderStatus != null) {
            orderStatus.setStatus(OrderFetchStatus.ORDER_STATUS_REQUESTING);
        } else {
            OrderFetchStatus orderStatusNew = new OrderFetchStatus();
            orderStatusNew.setFetchCode(fetchCode);
            orderStatusNew.setStatus(OrderFetchStatus.ORDER_STATUS_REQUESTING);
            orderStatusNew.setTimestamp(timestamp);
            MyApplication.Instance().addIndentStatus(orderStatusNew);
        }
        // send the request to server
        ProgressDlgHelper.showProgress(getActivity(), "正在验证");

        ExchangeCoffeeByCodeInfo info = new ExchangeCoffeeByCodeInfo();
        info.setUid(U.getMyVendorNum());
        info.setFetchCode(fetchCode);
        info.setTimestamp(timestamp);
        execute(info.toRemote());
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_EXCHANGE_COFFEE) { // 取货码兑换
                ProgressDlgHelper.closeProgress();
                ExchangeCoffeeByCodeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    OrderContent orderContent = result.getOrderContent();
                    if (orderContent != null && orderContent.getItemSize() > 0) {
                        if(BuildConfig.SERIALPORT_SYSNC) {
                            NewMakeCoffeeExActivity.start(getActivity(), orderContent, false);
                        }else{
                            ToastUtil.showToast(getContext(),"make coffee");
                        }

                    } else {
                        LogUtil.e("ExchangeFragment", "receive exchange code request, but order is null");
                    }
                } else {
                    if (result == null) {
                        LogUtil.vendor(TAG + "-> result is null");
                        ToastUtil.showToast(getActivity(), parseError(0));
                    } else {
                        ToastUtil.showToast(getActivity(), parseError(result.getResCode()));
                    }
                }
            }
        }
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
    }

    private int parseError(int resCode) {
        int resId = 0;
        switch (resCode) {
            case 301:
                resId = R.string.exchange_error_no_coffeeindent;
                break;
            case 302:
                resId = R.string.exchange_error_have_fetch;
                break;
            case 303:
                resId = R.string.exchange_error_system;
                break;
            case 306:
                resId = R.string.exchange_error_no_pay;
                break;
            case 307:
                resId = R.string.exchange_error_drawback;
                break;
            case 308:
                resId = R.string.exchange_error_have_fetch_by_other_mac;
                break;
            case 309:
                resId = R.string.exchange_error_cant_exchange;
                break;
            case 310:
                resId = R.string.exchange_error_exceed;
                break;
            case 311:
                resId = R.string.exchange_error_fetchcode;
                break;
            case 313:
                resId = R.string.exchange_error_lowstocks;
                break;
            case 320:
                resId = R.string.exchange_error_validate_timeout;
                break;
            default:
                resId = R.string.exchange_error_default;
                break;
        }

        return resId;
    }

    public interface OnTouchScreenListener {
        public void OnTouchScreenExchange();
    }
}
