package com.jingye.coffeemac.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.ControlMaintanceActivity;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.DebugItem;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.ConpMixedDrinksInstruction;
import com.jingye.coffeemac.instructions.IMixedDrinksInstruction;
import com.jingye.coffeemac.instructions.MixedDrinksInstruction;
import com.jingye.coffeemac.instructions.SetTempInstruction;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.ui.DialogTitle;
import com.jingye.coffeemac.ui.DialogTitleInput;
import com.jingye.coffeemac.ui.DialogTitleInputNumber;
import com.jingye.coffeemac.ui.GenericSettingDialog;
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
 * This class implements repair-manager
 * Created by Hades on 2016/10/31.
 */
public class RepairControlDebugFragment extends TFragment {

    private static final Integer TEMP_VALUE = 90;
    private static final Integer TEMP_VALUE_MAX = 100;
    private static final Integer TEMP_VALUE_MIN = 70;
    private static final String TEMP = "温度(" + TEMP_VALUE_MIN + "-" + TEMP_VALUE_MAX + ")";
    private static final int MAX_MAKE_COFFEE_TIME = 280;
    private static final int TIME_PERIOD = 1000;
    private static final String TAG = RepairControlSettingFragment.class.getSimpleName();
    private static final int MAX_WATER_VOLUME = 500;

    private final int ID_SETTING_DEGRESS = 1081;
    private final int ID_GETTING_DEGRESS = 1082;
    private final int ID_CUP_PLACE = 1083;
    private final int ID_CLEAN = 1084;
    private final int ID_ALL_CHECK = 1085;
    private final int ID_COFFEE_TEST1 = 1086;
    private final int ID_COFFEE_TEST2 = 1087;
    private final int ID_REPAIR = 1088;
    private final int ID_WAIT = 1089;
    private final int ID_CONP_WATER = 1096;

    private final int TEST_ESRPESS_COFFEE_VALUE=7;
    private final int TEST_ESRPESS_WATER_VALUE=23;


    private int type = 1;

    private CountDownTimer countDownTimer;
    private DialogTitle.IDialogTitleClick fixErrorListener = new DialogTitle.IDialogTitleClick() {
        @Override
        public void onDialogTitleCancelClick() {

        }

        @Override
        public void onDialogTitleOkClick() {
            SharePrefConfig.getInstance().setIsNeedLock(false);
            MyApplication.Instance().setNeedRollback(false);
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
    private RecyclerView recyclerViewRepairDebug;
    private RepairControlDebugAdapter adapter;
    private List<CoffeeDosingInfo> mFactor;


    public RepairControlDebugFragment() {
        this.setFragmentId(R.id.repair_manager_fragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMakeTimer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair_control_debug, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initTimer();
    }

    private void initTimer() {
        countDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                LogUtil.vendor("coffee test left time" + value);
                if (value < 0) {
                    List<Integer> status = new ArrayList<Integer>();
                    status.add(MachineStatusCode.COFFEE_MAKE_TIME_OUT);
                    testCoffeeError(status);
                    countDownTimer.cancelCountDownTimer();
                }
            }
        });

    }

    private void initView(View view) {

        recyclerViewRepairDebug = (RecyclerView) view.findViewById(R.id.recyclerViewRepairDebug);
        recyclerViewRepairDebug.setLayoutManager(new GridLayoutManager(getContext(), 5));
        adapter = new RepairControlDebugAdapter(createList());
        adapter.setListener(new RepairControlDebugListener() {
            @Override
            public void onRepairCOntrolDebugClick(int id) {
                onClick(id);

            }
        });
        recyclerViewRepairDebug.setAdapter(adapter);

        adapter.update(ID_SETTING_DEGRESS, SharePrefConfig.getInstance().getKeepTemp() + "°C");
        adapter.update(ID_CONP_WATER, SharePrefConfig.getInstance().getConpWater() + "ml");


    }

    private void onClick(int id) {
        switch (id) {
            case ID_SETTING_DEGRESS:
                setDegree();
                break;
            case ID_GETTING_DEGRESS:
                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.TEMP_GET);
                break;
            case ID_CUP_PLACE:
                if (MyApplication.Instance().getDesk()) {
                    ToastUtil.showToast(getContext(), "桌上型不支持该功能");
                } else {
                    SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CUP_DROP);
                }
                break;
            case ID_CLEAN:
                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.WASHING);
                break;
            case ID_ALL_CHECK:
                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CHECKING);
                break;
            case ID_COFFEE_TEST1:
                makeCoffee(1);
                break;
            case ID_COFFEE_TEST2:
                makeCoffee(2);
                break;
            case ID_REPAIR:
                DialogTitle dialogTitle = DialogTitle.newInstance(getString(R.string.control_fix_error_sure));
                dialogTitle.setListener(fixErrorListener);
                dialogTitle.show(getActivity().getSupportFragmentManager(), "fixErrorHint");
                break;
            case ID_WAIT:
                LogUtil.vendor("wait-----");
                Intent intent = new Intent(getActivity(), ControlMaintanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case ID_CONP_WATER:
                setCompWater();
                break;
            default:
                break;
        }
    }

    private void setCompWater() {
        DialogTitleInputNumber compDialog = DialogTitleInputNumber.newInstance(getString(R.string.str_title_comp_water), getString(R.string.str_desc_comp_water), getString(R.string.str_hint_comp_water));
        compDialog.setListener(new DialogTitleInputNumber.IDialogTitleInputListener() {
            @Override
            public void onDialogTitleInputCancelClick() {

            }

            @Override
            public void onDialogTitleInputOkClick(String num) {
                try {
                    int temp = Integer.parseInt(num);
                    if (temp < 0 || temp > MAX_WATER_VOLUME) {
                        ToastUtil.showToast(getActivity(), num + " 超出范围");
                        return;
                    }

                    SharePrefConfig.getInstance().setConpWater(temp);
                    adapter.update(ID_CONP_WATER, SharePrefConfig.getInstance().getConpWater() + "ml");
                } catch (Exception e) {
                    ToastUtil.showToast(getActivity(), "输入格式错误");
                }
            }
        });
        compDialog.show(getActivity().getSupportFragmentManager(), "compDialog");
    }

    private List<DebugItem> createList() {
        List<DebugItem> items = new ArrayList<DebugItem>();

        items.add(new DebugItem(ID_SETTING_DEGRESS, R.drawable.icon_problem_degree_setting, "设置温度"));
        items.add(new DebugItem(ID_GETTING_DEGRESS, R.drawable.icon_problem_degree_getting, "获取温度"));
        items.add(new DebugItem(ID_CUP_PLACE, R.drawable.icon_problem_cup_place, "落杯"));
        items.add(new DebugItem(ID_CLEAN, R.drawable.icon_problem_clean, "清洗"));
        items.add(new DebugItem(ID_ALL_CHECK, R.drawable.icon_all_check, "全检"));
        items.add(new DebugItem(ID_COFFEE_TEST1, R.drawable.icon_coffee_test1, "意式浓缩测试"));
        items.add(new DebugItem(ID_COFFEE_TEST2, R.drawable.icon_coffee_test2, "打饮料测试"));
        items.add(new DebugItem(ID_REPAIR, R.drawable.icon_problem_repair, "故障修复"));
        items.add(new DebugItem(ID_WAIT, R.drawable.icon_wait, "设置维护模式"));
        items.add(new DebugItem(ID_CONP_WATER, R.drawable.icon_problem_degree_setting, "默认补偿水量"));
        return items;
    }

    @Override
    public void onReceive(Remote remote) {

        // 串口操作
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_SYNC) {
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processSetTempResult(res);
                if (result.equals("success")) {
                    ToastUtil.showToast(getActivity(), "设置温度成功");
                } else {
                    ToastUtil.showToast(getActivity(), "设置温度失败");
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEMP_GET) {
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processGetTemp2Result(res);
                if (!result.equals("error")) {
                    ToastUtil.showToast(getActivity(), "锅炉当前温度：" + result);
                } else {
                    ToastUtil.showToast(getActivity(), "获取锅炉温度失败");
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CUP_DROP) {
                String res = remote.getBody();
                int result = CoffeeMachineResultProcess.processCupDropResult(res);
                if (result == 1) {
                    ToastUtil.showToast(getActivity(), "落杯中");
                } else if (result == 2) {
                    ToastUtil.showToast(getActivity(), "落杯完成");
                } else if (result == 3) {
                    ToastUtil.showToast(getActivity(), "无杯");
                } else if (result == 4) {
                    ToastUtil.showToast(getActivity(), "有错误");
                } else {
                    ToastUtil.showToast(getActivity(), "落杯失败");
                }
            }
//            else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CUP_TURN) {
//                String res = remote.getBody();
//                int result = CoffeeMachineResultProcess.processCupTurnResult(res);
//                if (result == 1) {
//                    ToastUtil.showToast(getActivity(), "转动中");
//                } else if (result == 2) {
//                    ToastUtil.showToast(getActivity(), "转动完成");
//                } else if (result == 3) {
//                    ToastUtil.showToast(getActivity(), "无杯");
//                } else if (result == 4) {
//                    ToastUtil.showToast(getActivity(), "有错误");
//                } else {
//                    ToastUtil.showToast(getActivity(), "杯桶转动失败");
//                }
//            }
            else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK) {
                String result = remote.getBody();
                LogUtil.vendor(CoffeeMachineResultProcess.processCheckResult(result));
                ToastUtil.showToast(getActivity(), CoffeeMachineResultProcess.processCheckResult(result));
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING) {
                String res = remote.getBody();
                if (!TextUtils.isEmpty(res) && res.length() == 14) {
                    int result = CoffeeMachineResultProcess.processWashingResult(res);
                    if (result == 1) {
                        ToastUtil.showToast(getActivity(), "清洗中");
                    } else if (result == 2) {
                        ToastUtil.showToast(getActivity(), "清洗完成");
                        updateStockAfterWash();
                    } else if (result == 3) {
                        ToastUtil.showToast(getActivity(), "水量超限");
                    } else {
                        ToastUtil.showToast(getActivity(), "清洗失败");
                    }
                } else {
                    ToastUtil.showToast(getActivity(), "清洗失败");
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE) {
                final String res = remote.getBody();

                if (res.length() == 14) { // 制作咖啡进程
                    startMakeTimer();
                    int result = CoffeeMachineResultProcess.processMakeCoffeeResult(res);
                    if (result == 1) {
                        ToastUtil.showToast(getActivity(), "开始制作");
                    } else if (result == 2) {
                        stopMakeTimer();
                        ToastUtil.showToast(getActivity(), "完成制作");
                        // 更新库存
                        updateStock();
                    } else {
                        // 2016/11/7 goto Maintenance
                        stopMakeTimer();
                        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.UNKNOW_ERROR, false);
                        MyApplication.Instance().setWaitMaintenance(true);
                    }
                } else if (res.length() == 16) { //错误报告
                    stopMakeTimer();
                    List<Integer> status = CoffeeMachineResultProcess.processMakeCoffeeErrorResult(res);
                    if (status.contains(MachineStatusCode.ALREADY_HAVE_CUP)) {
                        ToastUtil.showToast(getContext(), R.string.str_already_have_cup);
                    } else if (status.contains(MachineStatusCode.NO_CUP)) {
                        ToastUtil.showToast(getContext(), getString(R.string.str_no_cup));
                    }else {
                        testCoffeeError(status);
                    }
                } else {
                    stopMakeTimer();
                }
            }
        }
    }

    private void testCoffeeError(List<Integer> status) {
        LogUtil.vendor(TAG+"resport test coffee error");
        if(status!=null) {
            MachineStatusReportInfo info = new MachineStatusReportInfo();
            info.setUid(U.getMyVendorNum());
            info.setTimestamp(TimeUtil.getNow_millisecond());
            info.setStatus(status);
            execute(info.toRemote());
        }
    }

    private void startMakeTimer() {
        countDownTimer.startCountDownTimer(MAX_MAKE_COFFEE_TIME, TIME_PERIOD, TIME_PERIOD);
    }

    private void stopMakeTimer() {
        countDownTimer.cancelCountDownTimer();
    }

    private void updateStock() {

        double resumeBox1 = 0;
        double resumeBox2 = 0;
        double resumeBox3 = 0;
        double resumeBox4 = 0;
        double resumeBox5 = 0;
        double resumeWater = 0;
        double resumeBean = 0;
        double resumeCupNum = 1;

        if (type == 1) {

            resumeBean += TEST_ESRPESS_COFFEE_VALUE*2;
            resumeWater += TEST_ESRPESS_WATER_VALUE*2;
        } else if (type == 2) {

            resumeBox1 += 8;
            resumeBox2 += 8;
            resumeBox3 += 8;
            resumeBox4 += 8;
            resumeBox5 += 8;
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

    private void updateStockAfterWash() {
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double leftWater = stockWater - 150;
        BigDecimal leftWaterBD = new BigDecimal(leftWater);
        leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftWater < 0) {
            leftWater = 0;
        }
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater), MachineMaterialMap.MATERIAL_WATER);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.llRepairDebugDegreeSetting:
//                //   set degree
//                setDegree();
//                break;
//            case R.id.llRepairDebugDegreeGetting:
//                // get degree
//                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.TEMP_GET);
//                break;
////            case R.id.llRepairDebugCupRotate:
////                //  rotate cup
////                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CUP_TURN);
////                break;
//            case R.id.llRepairDebugCupPlace:
//                //  place cup
//                if (MyApplication.Instance().getDesk()) {
//                    ToastUtil.showToast(getContext(), "桌上型不支持该功能");
//                } else {
//                    SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CUP_DROP);
//                }
//                break;
//            case R.id.llRepairDebugClean:
//                //  clean
//                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.WASHING);
//                break;
//            case R.id.llRepairDebugAllcheck:
//                //  all check
//                SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CHECKING);
//                break;
//            case R.id.llRepairDebugCoffeeTest1:
//                makeCoffee(1);
//                break;
//            case R.id.llRepairDebugCoffeeTest2:
//                makeCoffee(2);
//                break;
//            case R.id.rlRepairControl:
//                // repair
//                DialogTitle dialogTitle = DialogTitle.newInstance(getString(R.string.control_fix_error_sure));
//                dialogTitle.setListener(fixErrorListener);
//                dialogTitle.show(getActivity().getSupportFragmentManager(), "fixErrorHint");
//                break;
//            case R.id.rlRepairWait:
//                LogUtil.vendor("wait-----");
//                Intent intent = new Intent(getActivity(), ControlMaintanceActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                break;
//        }
//    }



    private void makeCoffee(int tp) {

        type = tp;
        if (type == 1) {



            if(MyApplication.Instance().getDesk()) {
                if (mFactor == null) {
                    showToastHint("矫正因子为空");
                    LogUtil.vendor(TAG+"矫正因子为空");
                } else {

                    MixedDrinksInstruction md = new MixedDrinksInstruction(
                            9, 0, 0, 0, 0, 0,
                            MachineMaterialMap.transferToMachine(8, getFactor(MachineMaterialMap.MATERIAL_COFFEE_BEAN)),
                            MachineMaterialMap.transferToMachine(0, 0),
                            MachineMaterialMap.transferToMachine(0, 0),
                            MachineMaterialMap.transferToMachine(0, 0),
                            MachineMaterialMap.transferToMachine(0, 0),
                            MachineMaterialMap.transferToMachine(0, 0),
                            40, 0, 0, 0, 0, 0, 0);
                    String srcStr = md.getMixedDrinksOrder(false);
                    SerialPortDataWritter.writeDataCoffee(srcStr);

                }
            }else {
                IMixedDrinksInstruction md = new ConpMixedDrinksInstruction(
                        9, 9, 0, 0, 0, 0,
                        MachineMaterialMap.transferToMachine(TEST_ESRPESS_COFFEE_VALUE, 1.1),
                        MachineMaterialMap.transferToMachine(TEST_ESRPESS_COFFEE_VALUE, 1.1),
                        MachineMaterialMap.transferToMachine(0, 0),
                        MachineMaterialMap.transferToMachine(0, 0),
                        MachineMaterialMap.transferToMachine(0, 0),
                        MachineMaterialMap.transferToMachine(0, 0),
                        TEST_ESRPESS_WATER_VALUE, TEST_ESRPESS_WATER_VALUE, 0, 0, 0, 0, 0);
                String srcStr = md.getMixedDrinksOrder(false);
                SerialPortDataWritter.writeDataCoffee(srcStr);
            }

        } else if (type == 2) {
            IMixedDrinksInstruction md = new ConpMixedDrinksInstruction(
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

    private void showToastHint(String string) {
        ToastUtil.showToast(getContext(),string);
    }

    private void setDegree() {
        DialogTitleInputNumber dialogTitleInputNumber = DialogTitleInputNumber.newInstance("配置：", TEMP, "请输入温度");
        dialogTitleInputNumber.setListener(new DialogTitleInputNumber.IDialogTitleInputListener() {
            @Override
            public void onDialogTitleInputCancelClick() {

            }

            @Override
            public void onDialogTitleInputOkClick(String password) {
                try {
                    int temp = Integer.parseInt(password);
                    if (temp < TEMP_VALUE_MIN || temp > TEMP_VALUE_MAX) {
                        ToastUtil.showToast(getActivity(), password + "设置超出范围");
                        return;
                    }
                    SharePrefConfig.getInstance().setTemp(temp, temp);
                    adapter.update(ID_SETTING_DEGRESS, SharePrefConfig.getInstance().getKeepTemp() + "°C");
                    SetTempInstruction instruction = new SetTempInstruction(temp, temp);
                    SerialPortDataWritter.writeDataCoffee(instruction.getSetTempOrder());
                } catch (Exception e) {
                    ToastUtil.showToast(getActivity(), "输入格式错误");
                }
            }
        });
        dialogTitleInputNumber.show(getActivity().getSupportFragmentManager(), "dialogTitleInputNumber");


//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put(TEMP, TEMP_VALUE.toString());
//        GenericSettingDialog dialog = new GenericSettingDialog(getActivity(), map,
//                new GenericSettingDialog.OnGenericSettingDialog() {
//
//                    @Override
//                    public void onCancel() {
//                    }
//
//                    @Override
//                    public boolean onConfirm(Map<String, String> resultMap) {
//                        try {
//                            int temp = Integer.parseInt(resultMap.get(TEMP));
//                            if (temp < TEMP_VALUE_MIN || temp > TEMP_VALUE_MAX) {
//                                ToastUtil.showToast(getActivity(), TEMP + "设置超出范围");
//                                return false;
//                            }
//                            SharePrefConfig.getInstance().setTemp(temp, temp);
//                            SetTempInstruction instruction = new SetTempInstruction(temp, temp);
//                            SerialPortDataWritter.writeDataCoffee(instruction.getSetTempOrder());
////                            ToastUtil.showToast(getActivity(), instruction.getSetTempOrder());
//                            return true;
//                        } catch (Exception e) {
//                            ToastUtil.showToast(getActivity(), "输入格式错误");
//                        }
//                        return false;
//                    }
//                });
//        dialog.show();
    }

    public void setDosing(List<CoffeeDosingInfo> factor) {
        this.mFactor=factor;
    }

    private double getFactor(int boxId){
        if(mFactor!=null){
            for (int i = 0; i < mFactor.size(); i++) {
                if(mFactor.get(i).getBoxID()==boxId){
                    return mFactor.get(i).getFactor();
                }
            }
        }
        return 1.1;
    }

    interface RepairControlDebugListener {
        void onRepairCOntrolDebugClick(int id);
    }

    class RepairControlDebugAdapter extends RecyclerView.Adapter<RepairControlDebugAdapter.ViewHolder> {


        private List<DebugItem> mDebugItems;
        private RepairControlDebugListener mListener;

        public RepairControlDebugAdapter(List<DebugItem> debugItems) {
            this.mDebugItems = debugItems;
        }

        public void setListener(RepairControlDebugListener listener) {
            this.mListener = listener;

        }

        @Override
        public RepairControlDebugAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_control_debug, parent, false);

            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RepairControlDebugAdapter.ViewHolder holder, int position) {
            final DebugItem debugItem = mDebugItems.get(position);
            holder.ivControldebugItemIcon.setImageResource(debugItem.getResId());
            holder.tvControldebugItemTitle.setText(debugItem.getTitle());

            if (TextUtils.isEmpty(debugItem.getHint())) {
                holder.tvControldebugItemHint.setVisibility(View.GONE);
            } else {
                holder.tvControldebugItemHint.setVisibility(View.VISIBLE);
                holder.tvControldebugItemHint.setText(debugItem.getHint());
            }

            holder.ivControldebugItemIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRepairCOntrolDebugClick(debugItem.getId());
                    }
                }
            });
            holder.tvControldebugItemTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRepairCOntrolDebugClick(debugItem.getId());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mDebugItems.size();
        }

        public void update(int i, String s) {
            if (mDebugItems != null) {
                for (int i1 = 0; i1 < mDebugItems.size(); i1++) {
                    if (mDebugItems.get(i1).getId() == i) {
                        mDebugItems.get(i1).setHint(s);
                        notifyItemChanged(i1);
                    }
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {


            private final ImageView ivControldebugItemIcon;
            private final TextView tvControldebugItemTitle;
            private final TextView tvControldebugItemHint;

            public ViewHolder(View itemView) {
                super(itemView);
                ivControldebugItemIcon = (ImageView) itemView.findViewById(R.id.ivControldebugItemIcon);
                tvControldebugItemTitle = (TextView) itemView.findViewById(R.id.tvControldebugItemTitle);
                tvControldebugItemHint = (TextView) itemView.findViewById(R.id.tvControldebugItemHint);

            }
        }
    }
}
