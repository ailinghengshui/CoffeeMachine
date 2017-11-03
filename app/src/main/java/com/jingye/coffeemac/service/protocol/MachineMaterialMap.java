package com.jingye.coffeemac.service.protocol;

public class MachineMaterialMap {
	
	// 料盒对应配料关联
	public static final int MATERIAL_WATER = 10;  //水
	
	public static final int MATERIAL_BOX_1 = 1;
	
	public static final int MATERIAL_BOX_2 = 2;
	
	public static final int MATERIAL_BOX_3 = 3;
	
	public static final int MATERIAL_BOX_4 = 4;

    public static final int MATERIAL_BOX_5 = 5;
	
	public static final int MATERIAL_COFFEE_BEAN = 9; //咖啡豆

    public static final int MATERIAL_COFFEE_CUP_NUM = 100; //咖啡杯

    // 最低库存值
    public static final int MATERIAL_WATER_LIMIT_VALUE = 1500;  //ml

    public static final int MATERIAL_BOX_1_LIMIT_VALUE = 0;   //g

    public static final int MATERIAL_BOX_2_LIMIT_VALUE = 0;   //g

    public static final int MATERIAL_BOX_3_LIMIT_VALUE = 0;   //g

    public static final int MATERIAL_BOX_4_LIMIT_VALUE = 0;   //g

    public static final int MATERIAL_BOX_5_LIMIT_VALUE = 0;   //g

    public static final int MATERIAL_COFFEE_BEAN_LIMIT_VALUE = 0; //g

    public static final int MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE = 4; //个

    public static final String  DOSING_NAME_1 ="dosing_name_1" ;
    public static final String  DOSING_NAME_2 ="dosing_name_2" ;
    public static final String  DOSING_NAME_3 ="dosing_name_3" ;
    public static final String  DOSING_NAME_4 ="dosing_name_4" ;
    public static final String  DOSING_NAME_5 ="dosing_name_5" ;
    public static final String  DOSING_NAME_9 ="dosing_name_9" ;

    public static int transferToMachine(double value, double factor){
        int ret = (int)((value / factor) * 10.0);
        return ret;
    }
}
