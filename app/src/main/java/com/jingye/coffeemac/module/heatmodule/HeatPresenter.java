package com.jingye.coffeemac.module.heatmodule;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hades on 2017/1/13.
 */

public class HeatPresenter implements HeatContract.IHeatPresenter {

    private final String TAG = "HeatPresenter->";

    /**
     * the num of time of check temp
     */
    private final int MAX_HEAT_TIME = 300;
    /**
     * period time
     */
    private final int CHECK_TEMP_TIME = 1000;

    private HeatContract.IHeatView mHeatView;
    private CountDownTimer heatTimer;

    public HeatPresenter(HeatContract.IHeatView iHeatView) {
        this.mHeatView = iHeatView;
        initTimer();
    }

    private void initTimer() {
        heatTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                LogUtil.vendor(TAG + " heating value:" + value);
                if (value <= 0) {
                    sendHeatOverTime();
                    stopTimer();
                }
                if (value % 10 == 7 && value > 10) {
                    checkTemp();
                }

            }
        });
        startTimer();
    }

    private void checkTemp() {
        if (BuildConfig.SERIALPORT_SYSNC) {
            SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.TEMP_GET);
        } else {
            int i = new Random().nextInt(10);
            compareTemp(i * 10);
        }
    }

    /**
     * not reach right temp so send error to server
     */
    private void sendHeatOverTime() {
        LogUtil.vendor(TAG + " i cannot reach the right temp");
        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.MACHINE_WARM_UP, false);
        MyApplication.Instance().setWaitMaintenance(true);
        List<Integer> status = new ArrayList<Integer>();
        MachineStatusReportInfo info = new MachineStatusReportInfo();
        info.setUid(U.getMyVendorNum());
        info.setTimestamp(TimeUtil.getNow_millisecond());
        status.add(MachineStatusCode.MACHINE_WARM_UP);
        info.setStatus(status);
        mHeatView.sendReportError(info.toRemote());
        mHeatView.finishThisWithError();
    }

    private void startTimer() {
        heatTimer.startCountDownTimer(MAX_HEAT_TIME, CHECK_TEMP_TIME, CHECK_TEMP_TIME);
    }

    private void stopTimer() {
        heatTimer.cancelCountDownTimer();
    }

    @Override
    public void compareTemp(int temp) {
        LogUtil.vendor(TAG + "temp:" + temp);
        if (BuildConfig.SERIALPORT_SYSNC) {
            if (temp > SharePrefConfig.getInstance().getKeepTemp()) {
                LogUtil.vendor(TAG + temp + ":" + SharePrefConfig.getInstance().getKeepTemp());
                stopTimer();
                mHeatView.intentToWelcome();
            }
        } else {
            if (temp > 10) {
                stopTimer();
                mHeatView.intentToWelcome();
            }
        }

    }

    @Override
    public void onStop() {
        stopTimer();
    }

}
