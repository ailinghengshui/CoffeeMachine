package com.jingye.coffeemac.instructions;

import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.util.HexUtil;
import com.jingye.coffeemac.util.SharePrefConfig;

/**
 * Created by Hades on 2017/9/12.
 */

public class ConpMixedDrinksInstruction implements IMixedDrinksInstruction {

    private static final int ORDER = 70;
    //    private static final int DATA_LENGTH = 27;
    private static final int DATA_LENGTH = 39;
    private static final int CONFIG_BYTE = 4;
    private static final int SYNC_PARA = 16;
    private static final int SYNC_PARA_COLD = 1;

    private static final int MAX_WATER = 550;
    private static final int MAX_ACCESSORY = 9;
    private static final int MAX_ACCESSORY_TIME = 65535;
    private static final int MAX_DELAY_TIME = 256;
    private static final int COFFEEBOXNUM = 9;

    // 料盒编号，1byte
    int accessoryNum1;
    int accessoryNum2;
    int accessoryNum3;
    int accessoryNum4;
    int accessoryNum5;
    int accessoryNum6;
    // 料盒出料时间，1byte
    int accessoryNum1Time;
    int accessoryNum2Time;
    int accessoryNum3Time;
    int accessoryNum4Time;
    int accessoryNum5Time;
    int accessoryNum6Time;
    // 料盒水量，2byte
    int accessoryNum1Water;
    int accessoryNum2Water;
    int accessoryNum3Water;
    int accessoryNum4Water;
    int accessoryNum5Water;
    int accessoryNum6Water;
    // 清洗管道水量
    int washPipleWater;

    //延时出水时间
    private int delayTime1;
    private int delayTime2;
    private int delayTime3;
    private int delayTime4;
    private int delayTime5;
    private int delayTime6;

    public ConpMixedDrinksInstruction(int accessoryNum1, int accessoryNum2, int accessoryNum3, int accessoryNum4,
                                      int accessoryNum5, int accessoryNum6, int accessoryNum1Time, int accessoryNum2Time,
                                      int accessoryNum3Time, int accessoryNum4Time, int accessoryNum5Time, int accessoryNum6Time,
                                      int accessoryNum1Water, int accessoryNum2Water, int accessoryNum3Water, int accessoryNum4Water,
                                      int accessoryNum5Water, int accessoryNum6Water, int washPipleWater) {
        this(accessoryNum1, accessoryNum2, accessoryNum3, accessoryNum4,
                accessoryNum5, accessoryNum6, accessoryNum1Time, accessoryNum2Time,
                accessoryNum3Time, accessoryNum4Time, accessoryNum5Time, accessoryNum6Time,
                accessoryNum1Water, accessoryNum2Water, accessoryNum3Water, accessoryNum4Water,
                accessoryNum5Water, accessoryNum6Water, washPipleWater, 0, 0, 0, 0, 0, 0);
    }

    public ConpMixedDrinksInstruction(int accessoryNum1, int accessoryNum2, int accessoryNum3, int accessoryNum4,
                                      int accessoryNum5, int accessoryNum6, int accessoryNum1Time, int accessoryNum2Time,
                                      int accessoryNum3Time, int accessoryNum4Time, int accessoryNum5Time, int accessoryNum6Time,
                                      int accessoryNum1Water, int accessoryNum2Water, int accessoryNum3Water, int accessoryNum4Water,
                                      int accessoryNum5Water, int accessoryNum6Water, int washPipleWater, int delayTime1, int delayTime2,
                                      int delayTime3, int delayTime4, int delayTime5, int delayTime6) {
        this.accessoryNum1 = accessoryNum1;
        this.accessoryNum2 = accessoryNum2;
        this.accessoryNum3 = accessoryNum3;
        this.accessoryNum4 = accessoryNum4;
        this.accessoryNum5 = accessoryNum5;
        this.accessoryNum6 = accessoryNum6;
        this.accessoryNum1Time = accessoryNum1Time;
        this.accessoryNum2Time = accessoryNum2Time;
        this.accessoryNum3Time = accessoryNum3Time;
        this.accessoryNum4Time = accessoryNum4Time;
        this.accessoryNum5Time = accessoryNum5Time;
        this.accessoryNum6Time = accessoryNum6Time;
        this.accessoryNum1Water = accessoryNum1Water;
        this.accessoryNum2Water = accessoryNum2Water;
        this.accessoryNum3Water = accessoryNum3Water;
        this.accessoryNum4Water = accessoryNum4Water;
        this.accessoryNum5Water = accessoryNum5Water;
        this.accessoryNum6Water = accessoryNum6Water;
        this.washPipleWater = washPipleWater;
        this.delayTime1 = delayTime1;
        this.delayTime2 = delayTime2;
        this.delayTime3 = delayTime3;
        this.delayTime4 = delayTime4;
        this.delayTime5 = delayTime5;
        this.delayTime6 = delayTime6;
    }

    @Override
    public String getMixedDrinksOrder(boolean isAddIce) {
        if (checkValues()) {
            StringBuilder res = new StringBuilder();
            res.append(CoffeeMachineInstruction.START_TAG).append(" ");
            res.append(HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS)).append(" ");
            res.append(HexUtil.Int2HexString(ORDER)).append(" ");
            res.append(HexUtil.Int2HexString(DATA_LENGTH)).append(" ");

            // 数据块
            // 1
            res.append(drinkOrderContent(1,accessoryNum1, accessoryNum1Time, accessoryNum1Water, delayTime1));


            // 2
            res.append(drinkOrderContent(2,accessoryNum2, accessoryNum2Time, accessoryNum2Water, delayTime2));
//            res.append(HexUtil.Int2HexString(accessoryNum2)).append(" ");
//            if (accessoryNum2Time <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum2Time)).append(" ");
//            if (accessoryNum2Water <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum2Water)).append(" ");
//            res.append(HexUtil.Int2HexString(delayTime2)).append(" ");

            // 3
            res.append(drinkOrderContent(3,accessoryNum3, accessoryNum3Time, accessoryNum3Water, delayTime3));
//            res.append(HexUtil.Int2HexString(accessoryNum3)).append(" ");
//            if (accessoryNum3Time <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum3Time)).append(" ");
//            if (accessoryNum3Water <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum3Water)).append(" ");
//            res.append(HexUtil.Int2HexString(delayTime3)).append(" ");


            // 4
            res.append(drinkOrderContent(4,accessoryNum4, accessoryNum4Time, accessoryNum4Water, delayTime4));
//            res.append(HexUtil.Int2HexString(accessoryNum4)).append(" ");
//            if (accessoryNum4Time <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum4Time)).append(" ");
//            if (accessoryNum4Water <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum4Water)).append(" ");
//            res.append(HexUtil.Int2HexString(delayTime4)).append(" ");

            // 5
            res.append(drinkOrderContent(5,accessoryNum5, accessoryNum5Time, accessoryNum5Water, delayTime5));
//            res.append(HexUtil.Int2HexString(accessoryNum5)).append(" ");
//            if (accessoryNum5Time <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum5Time)).append(" ");
//            if (accessoryNum5Water <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum5Water)).append(" ");
//            res.append(HexUtil.Int2HexString(delayTime5)).append(" ");


            // 6
            res.append(drinkOrderContent(6,accessoryNum6, accessoryNum6Time, accessoryNum6Water, delayTime6));
//            res.append(HexUtil.Int2HexString(accessoryNum6)).append(" ");
//            if (accessoryNum6Time <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum6Time)).append(" ");
//            if (accessoryNum6Water <= 0xFF) {
//                res.append("00 ");
//            }
//            res.append(HexUtil.Int2HexString(accessoryNum6Water)).append(" ");
//            res.append(HexUtil.Int2HexString(delayTime6)).append(" ");


            if (isAddIce) {
                res.append(HexUtil.Int2HexString(SYNC_PARA_COLD)).append(" ");
            } else {
                res.append(HexUtil.Int2HexString(SYNC_PARA)).append(" ");
            }
            res.append(HexUtil.Int2HexString(CONFIG_BYTE)).append(" ");
            res.append(HexUtil.Int2HexString(washPipleWater)).append(" ");
            res.append(HexUtil.Int2HexString(getVerify(isAddIce))).append(" ");
            res.append(CoffeeMachineInstruction.END_TAG);
            return res.toString();
        } else {
            // 配置的参数出错，无法获取命令
            return "";
        }
    }

    private String drinkOrderContent(int orderid,int num, int time, int water, int delayTime) {
        StringBuilder res = new StringBuilder();
        res.append(HexUtil.Int2HexString(num)).append(" ");
        if (time <= 0xFF) {
            res.append("00 ");
        }
        res.append(HexUtil.Int2HexString(time)).append(" ");

        if (!SharePrefConfig.getInstance().getLastCoffee()&& num == COFFEEBOXNUM) {
            water += SharePrefConfig.getInstance().getConpWater();

            changeLocalWater(orderid,water);

        } else if (SharePrefConfig.getInstance().getLastCoffee()&& num != COFFEEBOXNUM) {
            if(water>40){
                water-=SharePrefConfig.getInstance().getConpWater();
                changeLocalWater(orderid,water);
            }


        }

        if(num==COFFEEBOXNUM){
            SharePrefConfig.getInstance().setLastCoffee(true);
        }else if(0==num){

        }else{
            SharePrefConfig.getInstance().setLastCoffee(false);
        }

        if (water <= 0xFF) {
            res.append("00 ");
        }
        res.append(HexUtil.Int2HexString(water)).append(" ");


        res.append(HexUtil.Int2HexString(delayTime)).append(" ");
        return res.toString();
    }

    private void changeLocalWater(int orderid,int water) {
        if(orderid==1){
            accessoryNum1Water=water;
        }else if(orderid==2){
            accessoryNum2Water=water;
        }else if(orderid==3){
            accessoryNum3Water=water;
        }else if(orderid==4){
            accessoryNum4Water=water;
        }else if(orderid==5){
            accessoryNum5Water=water;
        }else if(orderid==6){
            accessoryNum6Water=water;
        }else{

        }
    }


    private int getVerify(boolean isAddIce) {
        return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH
                ^ accessoryNum1
                ^ (accessoryNum1Time >> 8) ^ (accessoryNum1Time & 0xff)
                ^ delayTime1
                ^ accessoryNum2
                ^ (accessoryNum2Time >> 8) ^ (accessoryNum2Time & 0xff)
                ^ delayTime2
                ^ accessoryNum3
                ^ (accessoryNum3Time >> 8) ^ (accessoryNum3Time & 0xff)
                ^ delayTime3
                ^ accessoryNum4
                ^ (accessoryNum4Time >> 8) ^ (accessoryNum4Time & 0xff)
                ^ delayTime4
                ^ accessoryNum5
                ^ (accessoryNum5Time >> 8) ^ (accessoryNum5Time & 0xff)
                ^ delayTime5
                ^ accessoryNum6
                ^ (accessoryNum6Time >> 8) ^ (accessoryNum6Time & 0xff)
                ^ delayTime6
                ^ (accessoryNum1Water >> 8) ^ (accessoryNum1Water & 0xff)
                ^ (accessoryNum2Water >> 8) ^ (accessoryNum2Water & 0xff)
                ^ (accessoryNum3Water >> 8) ^ (accessoryNum3Water & 0xff)
                ^ (accessoryNum4Water >> 8) ^ (accessoryNum4Water & 0xff)
                ^ (accessoryNum5Water >> 8) ^ (accessoryNum5Water & 0xff)
                ^ (accessoryNum6Water >> 8) ^ (accessoryNum6Water & 0xff)
                ^ CONFIG_BYTE ^ (isAddIce ? SYNC_PARA_COLD : SYNC_PARA) ^ washPipleWater;
    }

    private boolean checkValues() {
        if (accessoryNum1 < 0 || accessoryNum1 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum2 < 0 || accessoryNum2 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum3 < 0 || accessoryNum3 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum4 < 0 || accessoryNum4 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum5 < 0 || accessoryNum5 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum1Time < 0 || accessoryNum1Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum2Time < 0 || accessoryNum2Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum3Time < 0 || accessoryNum3Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum4Time < 0 || accessoryNum4Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum5Time < 0 || accessoryNum5Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum1Water < 0 || accessoryNum1Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum2Water < 0 || accessoryNum2Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum3Water < 0 || accessoryNum3Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum4Water < 0 || accessoryNum4Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum5Water < 0 || accessoryNum5Water > MAX_WATER) {
            return false;
        }

        if (delayTime1 < 0 || delayTime1 > MAX_DELAY_TIME) {
            return false;
        }
        if (delayTime2 < 0 || delayTime2 > MAX_DELAY_TIME) {
            return false;
        }
        if (delayTime3 < 0 || delayTime3 > MAX_DELAY_TIME) {
            return false;
        }
        if (delayTime4 < 0 || delayTime4 > MAX_DELAY_TIME) {
            return false;
        }
        if (delayTime5 < 0 || delayTime5 > MAX_DELAY_TIME) {
            return false;
        }

        if (delayTime6 < 0 || delayTime6 > MAX_DELAY_TIME) {
            return false;
        }
        return true;
    }
}
