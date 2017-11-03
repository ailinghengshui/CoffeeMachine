package com.jingye.coffeemac.module.makecoffeemodule;

import com.jingye.coffeemac.service.protocol.MachineStatusCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 2016/11/11.
 */
public class NewMakeCoffeeExPresenter {
    private final NewMakeCoffeeExContact.NewMakeCoffeeExView mNewMakeCoffeeExView;
    private final NewMakeCoffeeExContact.NewMakeCoffeeExModel mNewMakeCoffeeExModel;

    public NewMakeCoffeeExPresenter(NewMakeCoffeeExContact.NewMakeCoffeeExView newMakeCoffeeExView) {
        this.mNewMakeCoffeeExView=newMakeCoffeeExView;
        this.mNewMakeCoffeeExModel=new NewMakeCoffeeExModelImp();
    }

    public void setFecthTimer(int value) {
        mNewMakeCoffeeExView.onFetchTimeOut(value);
    }

    public void setMakeCoffeePortTimer(int value) {
        mNewMakeCoffeeExView.onMakeCoffeePortTimeOut(value);
    }

    public void setMakeCoffeeTimer(int value) {
        mNewMakeCoffeeExView.onMakeCoffeeTimeRecord(value);
    }

    public void encounterError() {
        mNewMakeCoffeeExView.onEncounterError();
    }

    public void showToast(int stringResId) {
        mNewMakeCoffeeExView.onShowToast(stringResId);
    }

    public void onMakeCoffeeStart() {
        mNewMakeCoffeeExView.onMakeCoffeeStart();
    }

    public void onMakeCoffeeFail() {
        mNewMakeCoffeeExView.onMakeCoffeeFail();
    }

    public void onMakeCoffeeRetry() {
        mNewMakeCoffeeExView.onMakeCoffeeRetry();
    }

    public void onMakeCoffeeSuccess(String obj) {
        mNewMakeCoffeeExView.onMakeCoffeeSuccess(obj);
    }

    public void onMakeCoffeeTimeout() {
        mNewMakeCoffeeExView.onMakeCoffeeTimeout();
    }

    public void setFailed(List<Integer> errors) {
        mNewMakeCoffeeExView.setFailed(errors);
    }

    public void onMakeCoffeePortTimeOut() {
        mNewMakeCoffeeExView.onMakeCoffeePortTimeOut();
    }

    public void makeCoffee() {
        mNewMakeCoffeeExView.makeCoffee();
    }

    public List<Integer> selectFailed(List<Integer> status) {
        List<Integer> errors=new ArrayList<Integer>();
        if(status.contains(MachineStatusCode.MACHINE_WARM_UP)){
            errors.add(MachineStatusCode.MACHINE_WARM_UP);
        }else if(status.contains(MachineStatusCode.TEMPERATURE_SENSOR_ERROR)){
            errors.add(MachineStatusCode.TEMPERATURE_SENSOR_ERROR);
        }else if(status.contains(MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH)){
            errors.add(MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH);
        }else if(status.contains(MachineStatusCode.GRINDER_MOTOR_TIMEOUT)){
            errors.add(MachineStatusCode.GRINDER_MOTOR_TIMEOUT);
        }else if(status.contains(MachineStatusCode.FLOW_METER_DETECTION_TIMEOUT)){
            errors.add(MachineStatusCode.FLOW_METER_DETECTION_TIMEOUT);
        }else if(status.contains(MachineStatusCode.BREW_MOTOR_TIMEOUT)){
            errors.add(MachineStatusCode.BREW_MOTOR_TIMEOUT);
        }else if(status.contains(MachineStatusCode.PUMPING_TIMEOUT)){
            errors.add(MachineStatusCode.PUMPING_TIMEOUT);
        }else if(status.contains(MachineStatusCode.POWDER_SOLENOID_ABNORMAL)){
            errors.add(MachineStatusCode.POWDER_SOLENOID_ABNORMAL);
        }else if(status.contains(MachineStatusCode.FOLL_CUP_SYSTEM_COMMUNICATE_ERROR)){
            errors.add(MachineStatusCode.FOLL_CUP_SYSTEM_COMMUNICATE_ERROR);
        }else if(status.contains(MachineStatusCode.BLOCK_CUP)){
            errors.add(MachineStatusCode.BLOCK_CUP);
        }else if(status.contains(MachineStatusCode.MOVE_CUP_MOTOR_RUN_TIMEOUT)){
            errors.add(MachineStatusCode.MOVE_CUP_MOTOR_RUN_TIMEOUT);
        }else if(status.contains(MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT)){
            errors.add(MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT);
        }else if(status.contains(MachineStatusCode.CUP_BARREL_MOTOR_RUN_TIMEOUT)){
            errors.add(MachineStatusCode.CUP_BARREL_MOTOR_RUN_TIMEOUT);
        }else if(status.contains(MachineStatusCode.MACHINE_BUSY)){
            errors.add(MachineStatusCode.MACHINE_BUSY);
        }else{
            errors.add(MachineStatusCode.UNKNOW_ERROR);
        }
        return errors;
    }

}
