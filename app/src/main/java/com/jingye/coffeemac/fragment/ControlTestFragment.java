package com.jingye.coffeemac.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.MixedDrinksInstruction;
import com.jingye.coffeemac.instructions.SetTempInstruction;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.ui.GenericSettingDialog;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ControlTestFragment extends TFragment implements OnClickListener {

	private static final String TEMP = "温度(70-100)";
	private static final Integer TEMP_VALUE = 90;
	private static final Integer TEMP_VALUE_MAX = 100;
	private static final Integer TEMP_VALUE_MIN = 70;

	private Button mSetTemp;
	private Button mGetTemp;
	private Button mCupTurn;
	private Button mCupDrop;
	private Button mWashing;
	private Button mCoffee;
	private Button mDrink;
	private int type = 1;
	private OnClickListener mSetTempOnClick = new OnClickListener() {

		private Map<String, String> map;

		@Override
		public void onClick(View v) {
			map = new HashMap<String, String>();
			map.put(TEMP, TEMP_VALUE.toString());
			GenericSettingDialog dialog = new GenericSettingDialog(getActivity(), map,
					new GenericSettingDialog.OnGenericSettingDialog() {

						@Override
						public void onCancel() {
						}

						@Override
						public boolean onConfirm(Map<String, String> resultMap) {
                            try{
                                int temp = Integer.parseInt(resultMap.get(TEMP));
                                if (temp < TEMP_VALUE_MIN || temp > TEMP_VALUE_MAX) {
                                    ToastUtil.showToast(getActivity(), TEMP  + "设置超出范围");
                                    return false;
                                }
                                SetTempInstruction instruction = new SetTempInstruction(0, temp);
                                SerialPortDataWritter.writeDataCoffee(instruction.getSetTempOrder());
								ToastUtil.showToast(getActivity(), instruction.getSetTempOrder());
								return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), "输入格式错误");
                            }
                            return false;
						}
					});
			dialog.show();
		}
	};

	public ControlTestFragment() {
		this.setFragmentId(R.id.machine_debug_fragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_control_test, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
	}

	private void initView() {
		/*设置锅炉温度*/
		mSetTemp = (Button) getView().findViewById(R.id.control_test_set_temp_instruction);
		mSetTemp.setOnClickListener(mSetTempOnClick);
		/*获取锅炉温度*/
		mGetTemp = (Button) getView().findViewById(R.id.control_test_temp_get_instruction);
		mGetTemp.setOnClickListener(this);
		/*杯桶转动*/
		mCupTurn = (Button) getView().findViewById(R.id.control_test_cup_turn_instruction);
		mCupTurn.setOnClickListener(this);
		/*落杯*/
		mCupDrop = (Button) getView().findViewById(R.id.control_test_cup_drop_instruction);
		mCupDrop.setOnClickListener(this);
		/*清洗粉盒*/
		mWashing = (Button) getView().findViewById(R.id.control_test_washing_instruction);
		mWashing.setOnClickListener(this);
		/*打咖啡测试1*/
		mCoffee = (Button) getView().findViewById(R.id.control_test_coffee1_instruction);
		mCoffee.setOnClickListener(this);
		/*打咖啡测试2*/
		mDrink = (Button) getView().findViewById(R.id.control_test_coffee2_instruction);
		mDrink.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.control_test_temp_get_instruction:
			SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.TEMP_GET);
			break;
		case R.id.control_test_cup_drop_instruction:
			SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CUP_DROP);
			break;
		case R.id.control_test_cup_turn_instruction:
			SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CUP_TURN);
			break;
        case R.id.control_test_washing_instruction:
            SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.WASHING);
            break;
		case R.id.control_test_coffee1_instruction:
			makeCoffee(1);
			break;
		case R.id.control_test_coffee2_instruction:
			makeCoffee(2);
			break;
		default:
			break;
		}
	}


	private void makeCoffee(int tp) {

		type = tp;
		if(type == 1) {

			MixedDrinksInstruction md = new MixedDrinksInstruction(
					9, 0, 0, 0, 0, 0,
					MachineMaterialMap.transferToMachine(8, 1.1),
					MachineMaterialMap.transferToMachine(0, 0),
					MachineMaterialMap.transferToMachine(0, 0),
					MachineMaterialMap.transferToMachine(0, 0),
					MachineMaterialMap.transferToMachine(0, 0),
					MachineMaterialMap.transferToMachine(0, 0),
					50, 0, 0, 0, 0, 0, 15);
			String srcStr = md.getMixedDrinksOrder(false);
			SerialPortDataWritter.writeDataCoffee(srcStr);

		}else if(type == 2) {
			MixedDrinksInstruction md = new MixedDrinksInstruction(
					1, 2, 3, 4, 5, 0,
					MachineMaterialMap.transferToMachine(8, 1.1),
					MachineMaterialMap.transferToMachine(8, 1.3),
					MachineMaterialMap.transferToMachine(8, 1.5),
					MachineMaterialMap.transferToMachine(8, 1.1),
					MachineMaterialMap.transferToMachine(8, 1.1),
					MachineMaterialMap.transferToMachine(0, 0),
					40, 40, 40, 40, 40, 0, 15);
			String srcStr = md.getMixedDrinksOrder(false);
			SerialPortDataWritter.writeDataCoffee(srcStr);
		}
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
				} else if(result == 2){
					ToastUtil.showToast(getActivity(), "落杯完成");
				} else if(result == 3){
					ToastUtil.showToast(getActivity(), "无杯");
				} else if(result == 4){
					ToastUtil.showToast(getActivity(), "有错误");
				} else{
					ToastUtil.showToast(getActivity(), "落杯失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CUP_TURN) {
				String res = remote.getBody();
				int result = CoffeeMachineResultProcess.processCupTurnResult(res);
				if (result == 1) {
					ToastUtil.showToast(getActivity(), "转动中");
				} else if(result == 2){
					ToastUtil.showToast(getActivity(), "转动完成");
				} else if(result == 3){
					ToastUtil.showToast(getActivity(), "无杯");
				} else if(result == 4){
					ToastUtil.showToast(getActivity(), "有错误");
				} else{
					ToastUtil.showToast(getActivity(), "杯桶转动失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK) {
				String result = remote.getBody();
				ToastUtil.showToast(getActivity(), result);
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING) {
				String res = remote.getBody();
				if(!TextUtils.isEmpty(res) && res.length() == 14){
					int result = CoffeeMachineResultProcess.processWashingResult(res);
					if (result == 1) {
						ToastUtil.showToast(getActivity(), "清洗中");
					} else if(result == 2){
						ToastUtil.showToast(getActivity(), "清洗完成");
						updateStockAfterWash();
					} else if(result == 3){
						ToastUtil.showToast(getActivity(), "水量超限");
					} else{
						ToastUtil.showToast(getActivity(), "清洗失败");
					}
				}else{
					ToastUtil.showToast(getActivity(), "清洗失败");
				}
			}else if(remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE){
				final String res = remote.getBody();

				if (res.length() == 14) { // 制作咖啡进程
					int result = CoffeeMachineResultProcess.processMakeCoffeeResult(res);
					if(result == 1){
						ToastUtil.showToast(getActivity(), "开始制作");
					}else if(result == 2){
						ToastUtil.showToast(getActivity(), "完成制作");
						// 更新库存
						updateStock();
					}else{
						ToastUtil.showToast(getActivity(), "制作失败");
					}
				}
			}
		}
	}

	private void updateStockAfterWash(){
		double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
		double leftWater = stockWater - 150;
		BigDecimal leftWaterBD = new BigDecimal(leftWater);
		leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftWater < 0){ leftWater = 0; }
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater), MachineMaterialMap.MATERIAL_WATER);
	}



	private void updateStock(){

		double resumeBox1 = 0;
		double resumeBox2 = 0;
		double resumeBox3 = 0;
		double resumeBox4 = 0;
		double resumeBox5 = 0;
		double resumeWater = 75;
		double resumeBean = 0;
		double resumeCupNum = 1;

		if(type == 1) {

			resumeBean += 8;
			resumeWater += 50;
		}else if(type ==2) {

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
		double stockBox1 =	SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
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
		if(leftWater < 0){ leftWater = 0; }
		double leftBox1 = stockBox1 - resumeBox1;
		BigDecimal leftBox1BD = new BigDecimal(leftBox1);
		leftBox1 = leftBox1BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox1 < 0){leftBox1 = 0;}
		double leftBox2 = stockBox2 - resumeBox2;
		BigDecimal leftBox2BD = new BigDecimal(leftBox2);
		leftBox2 = leftBox2BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox2 < 0){leftBox2 = 0;}
		double leftBox3 = stockBox3 - resumeBox3;
		BigDecimal leftBox3BD = new BigDecimal(leftBox3);
		leftBox3 = leftBox3BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox3 < 0){leftBox3 = 0;}
		double leftBox4 = stockBox4 - resumeBox4;
		BigDecimal leftBox4BD = new BigDecimal(leftBox4);
		leftBox4 = leftBox4BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox4 < 0){leftBox4 = 0;}
		double leftBox5 = stockBox5 - resumeBox5;
		BigDecimal leftBox5BD = new BigDecimal(leftBox5);
		leftBox5 = leftBox5BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox5 < 0){leftBox5 = 0;}
		double leftBean = stockBean - resumeBean;
		BigDecimal leftBeanBD = new BigDecimal(leftBean);
		leftBean = leftBeanBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBean < 0){leftBean = 0;}
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
}