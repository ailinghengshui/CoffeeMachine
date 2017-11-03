package com.jingye.coffeemac.module.heatmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.BackgroundLoginActivity;
import com.jingye.coffeemac.activity.WaitMaintanceActivity;
import com.jingye.coffeemac.activity.WelcomeActivity;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.Random;

/**
 * Created by Hades on 2017/1/13.
 */

public class HeatActivity extends TActivity implements HeatContract.IHeatView {

    private static final String TAG = "HeatActivity->";
    private HeatContract.IHeatPresenter mPresenter;
    private RelativeLayout rlHeatBackground;

    public static void start(Activity activity) {

        Intent intent = new Intent();
        intent.setClass(activity, HeatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);
        rlHeatBackground = (RelativeLayout) findViewById(R.id.rlHeatBackground);
        LogUtil.vendor(TAG + ":" + MyApplication.Instance().isNeedRollback());
        mPresenter = new HeatPresenter(HeatActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.Instance().isNeedRollback()) {
            rlHeatBackground.setBackgroundResource(R.drawable.background_heat_rollback);
        } else {
            rlHeatBackground.setBackgroundResource(R.drawable.background_heat);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ToastUtil.showToast(this, R.string.string_heating);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEMP_GET) {
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processGetTemp2Result(res);
                if (!result.equals("error")) {
                    try {
                        mPresenter.compareTemp(Integer.parseInt(result));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtil.vendor(TAG + "get temp error");
                }
            }
        }
    }


    @Override
    public void sendReportError(Remote remote) {
        execute(remote);
    }

    @Override
    public void finishThisWithError() {
        Intent intent = new Intent(HeatActivity.this, WaitMaintanceActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void intentToWelcome() {
        WelcomeActivity.start(this);
        finish();
    }
}
