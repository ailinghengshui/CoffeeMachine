package com.jingye.coffeemac.instructions;

import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class CoffeeMachineResultProcess {

    public static String processSetTempResult(String input){
        if(input.substring(6, 8).equals("02")){
            return "success";
        }else{
            return "error";
        }
    }

    public static int processMakeCoffeeResult(String input) {
        if (input.substring(8, 10).equals("01")) {
            return 1;   //start
        } else if(input.substring(8, 10).equals("02")){
            return 2;   //finish
        }else {
            return -1;
        }
    }

	public static List<Integer> processMakeCoffeeErrorResult(String input) {
        List<Integer> status = new ArrayList<Integer>();
        if (input.substring(8, 10).equals("01")) {
            status.add(MachineStatusCode.MACHINE_WARM_UP);

        }else if (input.substring(8, 10).equals("02")) {
            status.add(MachineStatusCode.TEMPERATURE_SENSOR_ERROR);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.TEMPERATURE_SENSOR_ERROR,true);
        }else if (input.substring(8, 10).equals("04")) {
            status.add(MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH,true);
        }else if (input.substring(8, 10).equals("08")) {
            status.add(MachineStatusCode.GRINDER_MOTOR_TIMEOUT);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.GRINDER_MOTOR_TIMEOUT,true);
        }else if (input.substring(8, 10).equals("10")) {
            status.add(MachineStatusCode.FLOW_METER_DETECTION_TIMEOUT);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.FLOW_METER_DETECTION_TIMEOUT,true);
        }else if (input.substring(8, 10).equals("20")) {
            status.add(MachineStatusCode.BREW_MOTOR_TIMEOUT);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.BREW_MOTOR_TIMEOUT,true);
        }else if (input.substring(8, 10).equals("40")) {
            status.add(MachineStatusCode.PUMPING_TIMEOUT);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.PUMPING_TIMEOUT,true);
        }else if (input.substring(8, 10).equals("80")) {
            status.add(MachineStatusCode.POWDER_SOLENOID_ABNORMAL);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.POWDER_SOLENOID_ABNORMAL,true);
        }else if (input.substring(10, 12).equals("01")) {
            status.add(MachineStatusCode.NO_CUP);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.NO_CUP,true);
        }else if (input.substring(10, 12).equals("02")) {
            status.add(MachineStatusCode.ALREADY_HAVE_CUP);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.ALREADY_HAVE_CUP,true);
        }else if (input.substring(10, 12).equals("04")) {
            status.add(MachineStatusCode.FOLL_CUP_SYSTEM_COMMUNICATE_ERROR);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.FOLL_CUP_SYSTEM_COMMUNICATE_ERROR,true);
        }else if (input.substring(10, 12).equals("08")) {
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.BLOCK_CUP,true);
            status.add(MachineStatusCode.BLOCK_CUP);
        }else if (input.substring(10, 12).equals("10")) {
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.MOVE_CUP_MOTOR_RUN_TIMEOUT,true);
            status.add(MachineStatusCode.MOVE_CUP_MOTOR_RUN_TIMEOUT);
        }else if (input.substring(10, 12).equals("20")) {
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT,true);
            status.add(MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT);
        }else if (input.substring(10, 12).equals("40")) {
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.CUP_BARREL_MOTOR_RUN_TIMEOUT,true);
            status.add(MachineStatusCode.CUP_BARREL_MOTOR_RUN_TIMEOUT);
        }else if(input.substring(10, 12).equals("80")){
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.MACHINE_BUSY,true);
            status.add(MachineStatusCode.MACHINE_BUSY);
        }

        if ((input.substring(8, 10).equals("01"))||(input.substring(10, 12).equals("02"))||(input.substring(10, 12).equals("01"))){
            MyApplication.Instance().setWaitMaintenance(false);
        }else{
            MyApplication.Instance().setWaitMaintenance(true);
        }

        if(status.size() <= 0){
            status.add(MachineStatusCode.UNKNOW_ERROR);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.UNKNOW_ERROR,true);
            MyApplication.Instance().setWaitMaintenance(true);
        }

        return status;
    }

    public static String processGetTemp2Result(String input){
        if(input.substring(6, 8).equals("02")) {
            String temp = input.substring(10, 12);
            int res;
            try {
                res = Integer.parseInt(temp, 16);
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

            return res + "";
        }

        return "error";
    }

    public static int processCupDropResult(String input){
        if(input.substring(8, 10).equals("01")){
            return 1;
        }else if(input.substring(8, 10).equals("02")){
            return 2;
        }else if(input.substring(8, 10).equals("03")){
            return 3;
        }else if(input.substring(8, 10).equals("04")){
            return 4;
        }else{
            return -1;
        }
    }

    public static int processCupTurnResult(String input){
        if(input.substring(8, 10).equals("01")){
            return 1;
        }else if(input.substring(8, 10).equals("02")){
            return 2;
        }else if(input.substring(8, 10).equals("03")){
            return 3;
        }else if(input.substring(8, 10).equals("04")){
            return 4;
        }else{
            return -1;
        }
    }

    public static String processCheckResult(String input){
        String result = "";
        if(input.substring(6, 8).equals("01")){
            result = "全检失败：" + input.substring(6, 8);
        }else if(input.substring(6, 8).equals("02")){
            String code1 = input.substring(8, 10);
            String code2 = input.substring(10, 12);
            if(code1.equals("00") && code2.equals("00")){
                result = "全检成功";
            }else{
                result = "机器故障：" + code1 + "," + code2;
            }
        }
        return result;
    }

    public static int processWashingResult(String input){
        if(input.substring(6, 8).equals("01")){
            String code = input.substring(8, 10);
            if(code.equals("01")){
                return 1;
            }else if(code.equals("02")){
                return 2;
            }else if(code.equals("03")){
                return 3;
            }
        }

        return 0;
    }

    /**
     *
     * @param input
     * @return 1：有杯
     *         2：没杯
     */
    public static int processCheckCupResult(String input){
        if(input.substring(4,6).equals("60")){
            String code=input.substring(10,12);
            LogUtil.vendor(code);
            if(code.equals("70")){
                return 1;
            }else if (code.equals("71")){
                return 2;
            }else{
                return 3;
            }
        }

        return 0;
    }

    /**
     *
     * @param input
     * @return
     */
    public static int processGetMachineTypeResult(String input){
        if(input.substring(4,6).equals("61")){
            if(input.substring(8,10).equals("94")&&input.substring(10,12).equals("27")){
                return 1;
            }else{
                return 0;
            }
        }
        return 0;

    }
}
