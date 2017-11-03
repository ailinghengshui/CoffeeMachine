package com.jingye.coffeemac.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;

/**
 * Created by dblr4287 on 2016/7/7.
 */
public class WaitMaintanceActivity extends TActivity {

    private boolean isForeground = false;
    private long mQuitTimeStamp;
    private ImageView ivMaintanceBackground;
    private TextView tvMaintanceCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_maintance_page);

        ivMaintanceBackground=(ImageView)findViewById(R.id.ivMaintanceBackground);
        tvMaintanceCode=(TextView)findViewById(R.id.tvMaintanceCode);

        if(MyApplication.Instance().isNeedRollback()){
            ivMaintanceBackground.setImageResource(R.drawable.wait_maintance_background_rollback);
        }else{
            ivMaintanceBackground.setImageResource(R.drawable.wait_maintance_background);
        }

        if(MyApplication.Instance().getWaitMaintenanceCode()== MachineStatusCode.LOCK){
            tvMaintanceCode.setText(getString(R.string.locked));
        }else{
            tvMaintanceCode.setText(getString(R.string.error_code)+MyApplication.Instance().getWaitMaintenanceCode());
        }
    }

    @Override
    public void onReceive(Remote remote) {
        // coffee action

        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {

                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String type = result.getType();
                    if (type.equals("101")) {

                        if (isForeground) {
                            MyApplication.Instance().setWaitMaintenance(false);
                            finish();
                        }
                    }
//                    else if (type.equals("102")) {
//
//                        GetCoffeeInfo info = new GetCoffeeInfo();
//                        info.setUid(U.getMyVendorNum());
//                        executeBackground(info.toRemote());
//                    }
//                    else if(type.equals("103")) {
//
//                        GetAdvPicsInfo info = new GetAdvPicsInfo();
//                        info.setUid(U.getMyVendorNum());
//                        execute(info.toRemote());
//                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mQuitTimeStamp) > 2000) {
                ToastUtil.showToast(this, R.string.welcome_enter_control_panel_tip);
                mQuitTimeStamp = System.currentTimeMillis();
            } else {
//                MachineControlActivity.start(this);
                BackgroundLoginActivity.start(this);
//                MachineControlActivity.start(this);
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}