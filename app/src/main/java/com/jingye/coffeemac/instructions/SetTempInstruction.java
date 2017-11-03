package com.jingye.coffeemac.instructions;

import com.jingye.coffeemac.util.HexUtil;

public class SetTempInstruction {

	private static final int ORDER = 0x30;
	private static final int DATA_LENGTH = 0x03;
	private static final int PARA = 0x00;
	
	private int temp1;
	private int temp2;
	
	public SetTempInstruction(int temp1, int temp2){
		this.temp1 = temp1;
		this.temp2 = temp2;
	}
	
	public String getSetTempOrder(){
		String res = "";
		res += CoffeeMachineInstruction.START_TAG + " ";
		res += HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS) + " ";
		res += HexUtil.Int2HexString(ORDER) + " ";
		res += HexUtil.Int2HexString(DATA_LENGTH) + " ";
		res += HexUtil.Int2HexString(temp1) + " ";
		res += HexUtil.Int2HexString(temp2) + " ";
		res += HexUtil.Int2HexString(PARA) + " ";
		res += HexUtil.Int2HexString(getVerify()) + " ";
		res += CoffeeMachineInstruction.END_TAG;
		return res;
	}
	
	private int getVerify(){
		return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH ^ temp1 ^ temp2 ^ PARA;
	}
}
