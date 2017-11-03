package com.jingye.coffeemac.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.SetTempInstruction;
import com.jingye.coffeemac.module.heatmodule.HeatActivity;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends TActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private static final String TAG = "SplashActivity-> ";
    boolean isNeedLogin = true;
    private Context mContext;
    private Timer mSetTempTimer;
    private AtomicInteger mSetTempQueryCount = new AtomicInteger();
    private AtomicInteger mGetMachineCodeQueryCount = new AtomicInteger();
    private Timer mGetVersionCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);
        mContext = this;
        // Request Bind
        requestBind();
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_SYNC) {
                ProgressDlgHelper.closeProgress();
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processSetTempResult(res);
                if (result.equals("success")) {
                    if (mSetTempTimer != null) {
                        mSetTempTimer.cancel();
                    }
                    LogUtil.vendor(TAG+"set temperature successfully!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_success);
                    startMachine();
                } else {
                    LogUtil.vendor(TAG+"set temperature failed!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_fail);
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_GET_MACHINE_TYPE) {
                String res = remote.getBody();
                int code = CoffeeMachineResultProcess.processGetMachineTypeResult(res);
                LogUtil.vendor(res);
                if (code == 1) {
                    LogUtil.vendor(TAG+"desk true");
                    ToastUtil.showToast(SplashActivity.this,"desk true");
                    MyApplication.Instance().setDesk(true);
                } else {
                    LogUtil.vendor(TAG+"desk false");
                }

            }
        }
    }

    @Override
    protected void handleBound() {
        if (AppConfig.isSerialportSysnc()) {
            sendSetTempInstruction();
            getMachineCode();
        } else {
            startMachine();
        }
    }

    private void getMachineCode() {
        mGetMachineCodeQueryCount.set(0);
        mGetVersionCode = new Timer();
        mGetVersionCode.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mGetMachineCodeQueryCount.getAndIncrement() < 3) {
                    SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.GET_MACHINE_TYPE);
                } else {
                    mGetVersionCode.cancel();
                    mGetVersionCode = null;
                }
            }
        }, 500, 1000);
    }

    private void sendSetTempInstruction() {
        ProgressDlgHelper.showProgress(this, getString(R.string.welcome_set_temperature));
        //开始每隔5秒发送指令，设置咖啡机温度
        mSetTempQueryCount.set(0);
        mSetTempTimer = new Timer();
        mSetTempTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mSetTempQueryCount.getAndIncrement() < 3) {
                    SetTempInstruction instruction = new SetTempInstruction(SharePrefConfig.getInstance().getWorkTemp(), SharePrefConfig.getInstance().getKeepTemp());
                    SerialPortDataWritter.writeDataCoffee(instruction.getSetTempOrder());
                } else {
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDlgHelper.closeProgress();
                            //// TODO: 2017/11/3 假设温度设置完毕
                            ToastUtil.showToast(SplashActivity.this, getString(R.string.welcome_set_temperature_timeout));
                        }
                    });

                    if (mSetTempTimer != null) {
                        mSetTempTimer.cancel();
                        mSetTempTimer = null;
                    }
                }
            }
        }, 0, 5000);
    }

    private void startMachine() {
        startVendor();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isNeedLogin) {
                    showLoginPage();
                } else {
                    boolean isInit = SharePrefConfig.getInstance().isDosingInit(U.getMyVendorNum());
                    if (isInit) {
                        showWelcomePage();
                    } else {
                        showStorageConfigPage();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void startVendor() {
        String vendorNum = U.getMyVendorNum();
        if (!TextUtils.isEmpty(vendorNum)) {
            String isLogined = U.queryAppSet(U.KEY_USER_IS_LOGINED, vendorNum);
            if (isLogined.equals("true")) {
                GlobalCached.activeVendor = vendorNum;
                isNeedLogin = false;
            } else {
                isNeedLogin = true;
            }
        } else {
            isNeedLogin = true;
        }
    }

    private void showLoginPage() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }

    private void showWelcomePage() {

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

        HeatActivity.start(SplashActivity.this);
//        WelcomeActivity.start(this);
//        Intent intent = new Intent(mContext, WelcomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
        this.finish();
    }

    private void showStorageConfigPage() {
        Intent intent = new Intent(mContext, NewMaterialConfigActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }
}
