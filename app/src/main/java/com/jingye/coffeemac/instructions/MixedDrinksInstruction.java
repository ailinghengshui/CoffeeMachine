package com.jingye.coffeemac.instructions;

import com.jingye.coffeemac.util.HexUtil;

public class MixedDrinksInstruction implements IMixedDrinksInstruction{

    private static final int ORDER = 70;
    private static final int DATA_LENGTH = 27;
    private static final int CONFIG_BYTE = 4;
    private static final int SYNC_PARA = 0;
    private static final int SYNC_PARA_COLD = 1;

    private static final int MAX_WATER = 550;
    private static final int MAX_ACCESSORY = 9;
    private static final int MAX_ACCESSORY_TIME = 250;

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

    public MixedDrinksInstruction(int accessoryNum1, int accessoryNum2, int accessoryNum3, int accessoryNum4,
                                  int accessoryNum5, int accessoryNum6, int accessoryNum1Time, int accessoryNum2Time,
                                  int accessoryNum3Time, int accessoryNum4Time, int accessoryNum5Time, int accessoryNum6Time,
                                  int accessoryNum1Water, int accessoryNum2Water, int accessoryNum3Water, int accessoryNum4Water,
                                  int accessoryNum5Water, int accessoryNum6Water, int washPipleWater) {
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
    }

    // 根据配置好的参数，返回打混合饮料的指令
    public String getMixedDrinksOrder(boolean isAddIce) {
        if (checkValues()) {
            StringBuilder res = new StringBuilder();
            res.append(CoffeeMachineInstruction.START_TAG).append(" ");
            res.append(HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS)).append(" ");
            res.append(HexUtil.Int2HexString(ORDER)).append(" ");
            res.append(HexUtil.Int2HexString(DATA_LENGTH)).append(" ");
            // 数据块
            // 1
            res.append(HexUtil.Int2HexString(accessoryNum1)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum1Time)).append(" ");
            if (accessoryNum1Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum1Water)).append(" ");
            // 2
            res.append(HexUtil.Int2HexString(accessoryNum2)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum2Time)).append(" ");
            if (accessoryNum2Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum2Water)).append(" ");
            // 3
            res.append(HexUtil.Int2HexString(accessoryNum3)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum3Time)).append(" ");
            if (accessoryNum3Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum3Water)).append(" ");
            // 4
            res.append(HexUtil.Int2HexString(accessoryNum4)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum4Time)).append(" ");
            if (accessoryNum4Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum4Water)).append(" ");
            // 5
            res.append(HexUtil.Int2HexString(accessoryNum5)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum5Time)).append(" ");
            if (accessoryNum5Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum5Water)).append(" ");
            // 6
            res.append(HexUtil.Int2HexString(accessoryNum6)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum6Time)).append(" ");
            if (accessoryNum6Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum6Water)).append(" ");
            res.append(HexUtil.Int2HexString(CONFIG_BYTE)).append(" ");
            if(isAddIce){
                res.append(HexUtil.Int2HexString(SYNC_PARA_COLD)).append(" ");
            }else {
                res.append(HexUtil.Int2HexString(SYNC_PARA)).append(" ");
            }
            res.append(HexUtil.Int2HexString(washPipleWater)).append(" ");
            res.append(HexUtil.Int2HexString(getVerify())).append(" ");
            res.append(CoffeeMachineInstruction.END_TAG);
            return res.toString();
        } else {
            // 配置的参数出错，无法获取命令
            return "";
        }
    }

    private int getVerify() {
        return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH
                ^ accessoryNum1 ^ accessoryNum1Time ^ accessoryNum2 ^ accessoryNum2Time
                ^ accessoryNum3 ^ accessoryNum3Time ^ accessoryNum4 ^ accessoryNum4Time
                ^ accessoryNum5 ^ accessoryNum5Time ^ accessoryNum6 ^ accessoryNum6Time
                ^ (accessoryNum1Water >> 8) ^ (accessoryNum1Water & 0xff)
                ^ (accessoryNum2Water >> 8) ^ (accessoryNum2Water & 0xff)
                ^ (accessoryNum3Water >> 8) ^ (accessoryNum3Water & 0xff)
                ^ (accessoryNum4Water >> 8) ^ (accessoryNum4Water & 0xff)
                ^ (accessoryNum5Water >> 8) ^ (accessoryNum5Water & 0xff)
                ^ (accessoryNum6Water >> 8) ^ (accessoryNum6Water & 0xff)
                ^ CONFIG_BYTE ^ SYNC_PARA ^ washPipleWater;
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
        return true;
    }

    public int getAccessoryNum1() {
        return accessoryNum1;
    }

    public void setAccessoryNum1(int accessoryNum1) {
        this.accessoryNum1 = accessoryNum1;
    }

    public int getAccessoryNum2() {
        return accessoryNum2;
    }

    public void setAccessoryNum2(int accessoryNum2) {
        this.accessoryNum2 = accessoryNum2;
    }

    public int getAccessoryNum3() {
        return accessoryNum3;
    }

    public void setAccessoryNum3(int accessoryNum3) {
        this.accessoryNum3 = accessoryNum3;
    }

    public int getAccessoryNum4() {
        return accessoryNum4;
    }

    public void setAccessoryNum4(int accessoryNum4) {
        this.accessoryNum4 = accessoryNum4;
    }

    public int getAccessoryNum5() {
        return accessoryNum5;
    }

    public void setAccessoryNum5(int accessoryNum5) {
        this.accessoryNum5 = accessoryNum5;
    }

    public int getAccessoryNum1Time() {
        return accessoryNum1Time;
    }

    public void setAccessoryNum1Time(int accessoryNum1Time) {
        this.accessoryNum1Time = accessoryNum1Time;
    }

    public int getAccessoryNum2Time() {
        return accessoryNum2Time;
    }

    public void setAccessoryNum2Time(int accessoryNum2Time) {
        this.accessoryNum2Time = accessoryNum2Time;
    }

    public int getAccessoryNum3Time() {
        return accessoryNum3Time;
    }

    public void setAccessoryNum3Time(int accessoryNum3Time) {
        this.accessoryNum3Time = accessoryNum3Time;
    }

    public int getAccessoryNum4Time() {
        return accessoryNum4Time;
    }

    public void setAccessoryNum4Time(int accessoryNum4Time) {
        this.accessoryNum4Time = accessoryNum4Time;
    }

    public int getAccessoryNum5Time() {
        return accessoryNum5Time;
    }

    public void setAccessoryNum5Time(int accessoryNum5Time) {
        this.accessoryNum5Time = accessoryNum5Time;
    }

    public int getAccessoryNum1Water() {
        return accessoryNum1Water;
    }

    public void setAccessoryNum1Water(int accessoryNum1Water) {
        this.accessoryNum1Water = accessoryNum1Water;
    }

    public int getAccessoryNum2Water() {
        return accessoryNum2Water;
    }

    public void setAccessoryNum2Water(int accessoryNum2Water) {
        this.accessoryNum2Water = accessoryNum2Water;
    }

    public int getAccessoryNum3Water() {
        return accessoryNum3Water;
    }

    public void setAccessoryNum3Water(int accessoryNum3Water) {
        this.accessoryNum3Water = accessoryNum3Water;
    }

    public int getAccessoryNum4Water() {
        return accessoryNum4Water;
    }

    public void setAccessoryNum4Water(int accessoryNum4Water) {
        this.accessoryNum4Water = accessoryNum4Water;
    }

    public int getAccessoryNum5Water() {
        return accessoryNum5Water;
    }

    public void setAccessoryNum5Water(int accessoryNum5Water) {
        this.accessoryNum5Water = accessoryNum5Water;
    }
}
