package com.jingye.coffeemac.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.ControlMaintanceActivity;
import com.jingye.coffeemac.activity.WelcomeActivity;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.MixedDrinksInstruction;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.ui.DialogTitle;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 2016/10/25.
 */
public class ManagerControlManagerFragment extends TFragment implements View.OnClickListener {

    private static final int MAX_MEKE_COFFEE_TIME = 280;
    private int type = 1;

    private DialogTitle.IDialogTitleClick fixErrorListener = new DialogTitle.IDialogTitleClick() {
        @Override
        public void onDialogTitleCancelClick() {

        }

        @Override
        public void onDialogTitleOkClick() {

            SharePrefConfig.getInstance().setIsNeedLock(false);
            MyApplication.Instance().setWaitMaintenance(false);

            List<Integer> status = new ArrayList<Integer>();
            MachineStatusReportInfo info = new MachineStatusReportInfo();
            info.setUid(U.getMyVendorNum());
            info.setTimestamp(TimeUtil.getNow_millisecond());
            status.add(MachineStatusCode.SUCCESS);
            info.setStatus(status);
            execute(info.toRemote());
        }
    };
    private CountDownTimer mCountCoffeeMakerTimer;

    public ManagerControlManagerFragment() {
        this.setFragmentId(R.id.manager_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_control_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        (view.findViewById(R.id.llManagerMalfunctionRepair)).setOnClickListener(this);
        (view.findViewById(R.id.llManagerAllCheck)).setOnClickListener(this);
        (view.findViewById(R.id.llManagerCoffeeTest1)).setOnClickListener(this);
        (view.findViewById(R.id.llManagerCoffeeTest2)).setOnClickListener(this);
        (view.findViewById(R.id.llManagerCoffeeWait)).setOnClickListener(this);

        initTimer();
    }

    private void initTimer() {
        mCountCoffeeMakerTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                LogUtil.vendor("test make coffee left" + value);
                if (value < 0) {
                    mCountCoffeeMakerTimer.cancelCountDownTimer();
                }
            }
        });
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK) {
                String result = remote.getBody();
                LogUtil.vendor(CoffeeMachineResultProcess.processCheckResult(result));
                ToastUtil.showToast(getActivity(), CoffeeMachineResultProcess.processCheckResult(result));
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE) {
                final String res = remote.getBody();

                if (res.length() == 14) { // 制作咖啡进程
                    int result = CoffeeMachineResultProcess.processMakeCoffeeResult(res);
                    if (result == 1) {
                        startMakeTimer();
                        ToastUtil.showToast(getActivity(), "开始制作");
                    } else if (result == 2) {
                        stopMakeTimer();
                        ToastUtil.showToast(getActivity(), "完成制作");
                        // 更新库存
                        updateStock();
                    } else {
                        stopMakeTimer();
                        // 2016/11/7 goto Maintenance
                        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.UNKNOW_ERROR,false);
                        MyApplication.Instance().setWaitMaintenance(true);
                    }
                } else if (res.length() == 16) { //错误报告
                    stopMakeTimer();
                    List<Integer> status = CoffeeMachineResultProcess.processMakeCoffeeErrorResult(res);
                    if (status.contains(MachineStatusCode.ALREADY_HAVE_CUP)) {
                        ToastUtil.showToast(getContext(), R.string.str_already_have_cup);
                    } else if (status.contains(MachineStatusCode.NO_CUP)) {
                        ToastUtil.showToast(getContext(), getString(R.string.str_no_cup));
                    }
                } else {
                    stopMakeTimer();
                }
            }
        }
    }

    private void startMakeTimer() {
        mCountCoffeeMakerTimer.startCountDownTimer(MAX_MEKE_COFFEE_TIME, 1000, 1000);
    }

    private void stopMakeTimer() {
        mCountCoffeeMakerTimer.cancelCountDownTimer();
    }

    private void updateStock() {

        double resumeBox1 = 0;
        double resumeBox2 = 0;
        double resumeBox3 = 0;
        double resumeBox4 = 0;
        double resumeBox5 = 0;
        double resumeWater = 75;
        double resumeBean = 0;
        double resumeCupNum = 1;

        if (type == 1) {

            resumeBean += 8;
            resumeWater += 50;
        } else if (type == 2) {

            resumeBox1 += 5;
            resumeBox2 += 5;
            resumeBox3 += 5;
            resumeBox4 += 5;
            resumeBox5 += 5;
            resumeWater += 200;
        }

        LogUtil.vendor("calculate resume: [" + resumeWater + "," + resumeBox1 + "," + resumeBox2 + "," + resumeBox3 + "," + resumeBox4
                + "," + resumeBox5 + "," + resumeBean + "," + resumeCupNum + "]");

        // stock
        double stockBox1 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        double stockBox5 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
        double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double stockCupNum = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        LogUtil.vendor("calculate stock: [" + stockWater + "," + stockBox1 + "," + stockBox2 + "," + stockBox3 + "," + stockBox4
                + "," + stockBox5 + "," + stockBean + "," + stockCupNum + "]");

        double leftWater = stockWater - resumeWater;
        BigDecimal leftWaterBD = new BigDecimal(leftWater);
        leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftWater < 0) {
            leftWater = 0;
        }
        double leftBox1 = stockBox1 - resumeBox1;
        BigDecimal leftBox1BD = new BigDecimal(leftBox1);
        leftBox1 = leftBox1BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox1 < 0) {
            leftBox1 = 0;
        }
        double leftBox2 = stockBox2 - resumeBox2;
        BigDecimal leftBox2BD = new BigDecimal(leftBox2);
        leftBox2 = leftBox2BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox2 < 0) {
            leftBox2 = 0;
        }
        double leftBox3 = stockBox3 - resumeBox3;
        BigDecimal leftBox3BD = new BigDecimal(leftBox3);
        leftBox3 = leftBox3BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox3 < 0) {
            leftBox3 = 0;
        }
        double leftBox4 = stockBox4 - resumeBox4;
        BigDecimal leftBox4BD = new BigDecimal(leftBox4);
        leftBox4 = leftBox4BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox4 < 0) {
            leftBox4 = 0;
        }
        double leftBox5 = stockBox5 - resumeBox5;
        BigDecimal leftBox5BD = new BigDecimal(leftBox5);
        leftBox5 = leftBox5BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox5 < 0) {
            leftBox5 = 0;
        }
        double leftBean = stockBean - resumeBean;
        BigDecimal leftBeanBD = new BigDecimal(leftBean);
        leftBean = leftBeanBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBean < 0) {
            leftBean = 0;
        }
        double leftCupNum = stockCupNum - resumeCupNum;

        // update local stock
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater),
                MachineMaterialMap.MATERIAL_WATER);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox1),
                MachineMaterialMap.MATERIAL_BOX_1);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox2),
                MachineMaterialMap.MATERIAL_BOX_2);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox3),
                MachineMaterialMap.MATERIAL_BOX_3);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox4),
                MachineMaterialMap.MATERIAL_BOX_4);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox5),
                MachineMaterialMap.MATERIAL_BOX_5);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBean),
                MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftCupNum),
                MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llManagerMalfunctionRepair:
                DialogTitle dialogTitle = DialogTitle.newInstance(getString(R.string.control_fix_error_sure));
                dialogTitle.setListener(fixErrorListener);
                dialogTitle.show(getActivity().getSupportFragmentManager(), "fixErrorHint");
                break;
            case R.id.llManagerAllCheck:
                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CHECKING);
                break;
            case R.id.llManagerCoffeeTest1:
                makeCoffee(1);
                break;
            case R.id.llManagerCoffeeTest2:
                makeCoffee(2);
                break;

            case R.id.llManagerCoffeeWait:
                LogUtil.vendor("wait-----");
                Intent intent = new Intent(getActivity(), ControlMaintanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
    }

    private void makeCoffee(int tp) {

        type = tp;
        if (type == 1) {

            MixedDrinksInstruction md = new MixedDrinksInstruction(
                    9, 0, 0, 0, 0, 0,
                    MachineMaterialMap.transferToMachine(8, 1.1),
                    MachineMaterialMap.transferToMachine(0, 0),
                    MachineMaterialMap.transferToMachine(0, 0),
                    MachineMaterialMap.transferToMachine(0, 0),
                    MachineMaterialMap.transferToMachine(0, 0),
                    MachineMaterialMap.transferToMachine(0, 0),
                    50, 0, 0, 0, 0, 0, 0);
            String srcStr = md.getMixedDrinksOrder(false);
            SerialPortDataWritter.writeDataCoffee(srcStr);

        } else if (type == 2) {
            MixedDrinksInstruction md = new MixedDrinksInstruction(
                    1, 2, 3, 4, 5, 0,
                    MachineMaterialMap.transferToMachine(8, 1.1),
                    MachineMaterialMap.transferToMachine(8, 1.3),
                    MachineMaterialMap.transferToMachine(8, 1.5),
                    MachineMaterialMap.transferToMachine(8, 1.1),
                    MachineMaterialMap.transferToMachine(8, 1.1),
                    MachineMaterialMap.transferToMachine(0, 0),
                    40, 40, 40, 40, 40, 0, 0);
            String srcStr = md.getMixedDrinksOrder(false);
            SerialPortDataWritter.writeDataCoffee(srcStr);
        }
    }
}
