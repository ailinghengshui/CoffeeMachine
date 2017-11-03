package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sonicwavenfc.SonicWaveNFC;
import com.alipay.sonicwavenfc.SonicWaveNFCHandler;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.CancelTradeInfo;
import com.jingye.coffeemac.service.bean.action.PaySonicWaveInfo;
import com.jingye.coffeemac.service.bean.action.PayStatusAskInfo;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.PayNotifyResult;
import com.jingye.coffeemac.service.bean.result.PaySonicWaveResult;
import com.jingye.coffeemac.service.bean.result.PayStatusAskResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.CountDownTimer.CountDownCallback;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PayCoffeeSonicWaveActivity extends TActivity implements OnClickListener, SonicWaveNFCHandler {

	public static final String COFFEE_INFO = "coffee_info";
	public static final String COFFEE_SUGAR_WEIGHT = "coffee_sugar_weight";
    // ALI SONIC WAVE
    protected SonicWaveNFC mSonicWaveNFC;
	private ImageView mPayBack;
	private TextView mPayTimer;
	private TextView mGetCoffeeProcess;
    private CoffeeInfo mCoffeeInfo;
    private double mSugarWeight;
    private CountDownTimer countDownTimer;
    private AtomicInteger payAction = new AtomicInteger(0);
    private String mCoffeeIndentFromServer;
    
    public static void start(Activity activity, CoffeeInfo info, double sugarWeight) {
		Intent intent = new Intent();
		intent.setClass(activity, PayCoffeeSonicWaveActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(COFFEE_INFO, info);
		intent.putExtra(COFFEE_SUGAR_WEIGHT, sugarWeight);
		activity.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_coffee_sonicwave_layout);
		proceedExtra();
		
		initViews();
		initPay();

        prepareAliWavePay();
	}
	
	private void proceedExtra(){
		Intent intent = getIntent();
		mCoffeeInfo = (CoffeeInfo) intent.getSerializableExtra(COFFEE_INFO);
		if(mCoffeeInfo != null){
            ArrayList<CoffeeDosingInfo> dosingList = mCoffeeInfo.getDosingList();
            for(int i = 0; i < dosingList.size(); i++){
                LogUtil.vendor("[PayCoffeeSonicWave]" + dosingList.get(i).toString());
            }
        }
		mSugarWeight = intent.getDoubleExtra(COFFEE_SUGAR_WEIGHT, 0);
	}

	private void initViews(){
		mPayBack = (ImageView) findViewById(R.id.pay_qrcode_back_btn);
		mPayTimer = (TextView) findViewById(R.id.pay_qrcode_timer);
		mPayBack.setOnClickListener(this);
		mGetCoffeeProcess = (TextView) findViewById(R.id.pay_coffee_process_tip);
	}

	private void initPay(){
        countDownTimer = new CountDownTimer(new CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
        countDownTimer.startCountDownTimer(90, 1000, 1000);
    }
	
	private void onCountDown(int value){
		mPayTimer.setText(String.format(this.getString(R.string.pay_timer_tip_left), value));
        if(payAction.get() == 0  && (value == 30 || value == 60 )){
            askPayStatus(mCoffeeIndentFromServer);
		}
		
		if(value <= 0){
			this.finish();
		}
	}

    private void askPayStatus(String coffeeIndent){
        if(!TextUtils.isEmpty(coffeeIndent)){
            PayStatusAskInfo info = new PayStatusAskInfo();
            info.setUid(U.getMyVendorNum());
            info.setCoffeeIndent(coffeeIndent);
            execute(info.toRemote());
        }
    }
	
	private void prepareAliWavePay(){
		mSonicWaveNFC = SonicWaveNFC.getInstance();
        mSonicWaveNFC.initSonicWaveNFC(this);
        mSonicWaveNFC.printPara();
        LogUtil.vendor("是否支持接收高频1：" + mSonicWaveNFC.isReceiverSoincWave());
        
        int iTimeoutSeconds = 100;        
        int iMinAmplitude = 20; // 设置接收数据时声波幅度门限值
        mSonicWaveNFC.startReceiveData(iTimeoutSeconds, iMinAmplitude, this, this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.pay_qrcode_back_btn:
            if(payAction.compareAndSet(0, 2)){
                if(countDownTimer != null){
                    countDownTimer.cancelCountDownTimer();
                }
                cancelTrande();
                this.finish();
            }
			break;
		default:
			break;
		}
	}

    public void cancelTrande(){
        if(!TextUtils.isEmpty(mCoffeeIndentFromServer) ){
            CancelTradeInfo info = new CancelTradeInfo();
            info.setUid(U.getMyVendorNum());
            info.setCoffeeIndent(mCoffeeIndentFromServer);
            execute(info.toRemote());
        }
    }

    //----------------ALI SONIC WAVE PAYMENT------------------------------------------
    @Override
    public void onDataReceived(String arg0) {
        LogUtil.vendor("AliSonicWavePay->onDataReceived : " + arg0);

        PaySonicWaveInfo info = new PaySonicWaveInfo();
        info.setUid(U.getMyVendorNum());
        info.setCoffeeId(mCoffeeInfo.getCoffeeId());
        info.setDosing(getSugar());
        info.setProvider((short) 3);
        info.setDynamicID(arg0);
        execute(info.toRemote());
    }

    private String getSugar(){
        JSONArray array = new JSONArray();
        if(mCoffeeInfo != null){
            ArrayList<CoffeeDosingInfo> dosingList = mCoffeeInfo.getDosingList();
            for(int i = 0; i < dosingList.size(); i++){
                CoffeeDosingInfo info = dosingList.get(i);
                if(info.getMacConifg() == 1){  //糖
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("dosingID", info.getId());
                    jsonObj.put("value", mSugarWeight);
                    array.add(jsonObj);
                    break;
                }
            }
        }

        return array.toString();
    }

    @Override
    public void onReceiveDataFailed(int arg0) {
        LogUtil.vendor("AliSonicWavePay->onReceiveDataFailed : " + arg0);
    }

    @Override
    public void onReceiveDataInfo(String arg0) {
        LogUtil.vendor("AliSonicWavePay->onReceiveDataInfo : " + arg0);
    }

    @Override
    public void onReceiveDataStarted() {
        LogUtil.vendor("AliSonicWavePay->onReceiveDataStarted");
    }

    @Override
    public void onReceiveDataTimeout() {
        LogUtil.vendor("AliSonicWavePay->onReceiveDataTimeout");
    }

    @Override
    public void onSendDataFailed(int arg0) {
        LogUtil.vendor("AliSonicWavePay->onSendDataFailed : " + arg0);
    }

    @Override
    public void onSendDataInfo(String arg0) {
        LogUtil.vendor("AliSonicWavePay->onSendDataInfo : " + arg0);
    }

    @Override
    public void onSendDataStarted() {
        LogUtil.vendor("AliSonicWavePay->onSendDataStarted : ");
    }

    @Override
    public void onSendDataTimeout() {
        LogUtil.vendor("AliSonicWavePay->onSendDataTimeout : ");
    }
    //----------------ALI SONIC WAVE PAYMENT------------------------------------------

	@Override
	public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_PAY_SONICWAVE) {
                PaySonicWaveResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null) {
                    String coffeeIndent = result.getCoffeeIndent();
                    String tradeNO = result.getTradeNo();
                    int resCode = result.getResCode();
                    LogUtil.vendor("[AliSonicWavePay]" + "resCode = " + resCode + ", coffeeIndent = " + coffeeIndent + ", tradeNO = " + tradeNO);
                    if (resCode == 200) {
                        onReceivePaySuccess(coffeeIndent);
                    } else {
                        onReceivePayWait(resCode, coffeeIndent);
                    }
                }
            }else if(remote.getAction() == ITranCode.ACT_COFFEE_PAY_NOTIFY){
                PayNotifyResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null){
                    if(result.getResCode() == 200){
                        onReceivePaySuccess(result.getCoffeeIndent());
                    }else{
                        ToastUtil.showToast(this, parseError(result.getResCode()));
                    }
                }
            }else if(remote.getAction() == ITranCode.ACT_COFFEE_ASK_PAY_RESULT){
                PayStatusAskResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && result.getResCode() == 200){
                    String indent = result.getCoffeeIndent();
                    if(!TextUtils.isEmpty(mCoffeeIndentFromServer) && !TextUtils.isEmpty(indent)
                            && mCoffeeIndentFromServer.equals(indent)){
                        onReceivePaySuccess(result.getCoffeeIndent());
                    }
                }
            }
        }
    }

    private void onReceivePaySuccess(String coffeeIndent){
        mCoffeeIndentFromServer = coffeeIndent;
        if(payAction.compareAndSet(0, 1)){
            mPayTimer.setText("");
            mGetCoffeeProcess.setText(R.string.pay_pay_success);
            if(countDownTimer != null){
                countDownTimer.cancelCountDownTimer();
            }

            // get the base dosing list
            ArrayList<CoffeeDosingInfo> baseDosingList = null;
            List<CoffeeInfo> coffeeInfos = MyApplication.Instance().getCoffeeInfos();
            if(coffeeInfos == null){
                ToastUtil.showToast(this, "无法获取咖啡配方");
                return;
            }

            for(CoffeeInfo info : coffeeInfos){
                if(info.getCoffeeId() == mCoffeeInfo.getCoffeeId()){
                    baseDosingList = info.getDosingList();
                    break;
                }
            }

            if(baseDosingList == null){
                ToastUtil.showToast(this, "无法获取咖啡配方");
                return;
            }

            // update sugar formula
            ArrayList<CoffeeDosingInfo> dosingList = new ArrayList<CoffeeDosingInfo>();
            dosingList.addAll(baseDosingList);
            for(CoffeeDosingInfo formula : dosingList ){
                if(formula.getMacConifg() == 1){
                    formula.setValue(mSugarWeight);
                    break;
                }
            }

            // switch to make coffee page
            //MakeCoffeeActivity.start(this, mCoffeeIndentFromServer, mCoffeeInfo.getCoffeeId(), dosingList, false);
        }
    }

    private void onReceivePayWait(int resCode, String coffeeIndent){
        LogUtil.vendor("[AliSonicWavePay]" + "声波支付请求ResCode: " + resCode);
        mCoffeeIndentFromServer = coffeeIndent;
    }

    private int parseError(int resCode){
        int resId = -1;
        switch(resCode){
            case 501:
                resId = R.string.pay_error_waiting_pay;
                break;
            case 502:
                resId = R.string.pay_error_cancel_indent;
                break;
            default:
                resId = R.string.exchange_error_default;
                break;
        }

        return resId;
    }
}
