package com.jingye.coffeemac.instructions;

public class CoffeeMachineInstruction {

	public static final String START_TAG = "AA";
	public static final int ADDRESS = 16;
	public static final String END_TAG = "EE";

	//获取锅炉当前温度
	public static final String TEMP_GET = "AA 10 38 00 28 EE";
	//落杯
	public static final String CUP_DROP = "AA 10 21 01 00 30 EE";
	//杯筒转动
	public static final String CUP_TURN = "AA 10 23 01 00 32 EE";
	//全检
	public static final String CHECKING = "AA 10 0A 00 1A EE";
	//清洗
	public static final String WASHING = "AA 10 35 02 00 1E 39 EE";
	//查询是否有杯
	public static final String CHECK_CUP="AA 10 60 00 70 EE";
	//查询是否为桌上型
	public static final String GET_MACHINE_TYPE="AA 10 61 00 71 EE";
}