package com.jingye.coffeemac.fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.AddStockInfo;
import com.jingye.coffeemac.service.bean.action.GetDosingListInfo;
import com.jingye.coffeemac.service.bean.action.ResetStockInfo;
import com.jingye.coffeemac.service.bean.result.AddStockResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetDosingResult;
import com.jingye.coffeemac.service.bean.result.ResetStockResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.ui.ControlAddStockDialog;
import com.jingye.coffeemac.ui.ControlAddStockDialog.OnAddStockDialogListener;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class ControlStockFragment extends TFragment implements OnClickListener{

    private boolean isStockReset;

    private String dosingWater;
    private String dosingCupNum;
    private String dosingNo1;
    private String dosingNo2;
    private String dosingNo3;
    private String dosingNo4;
    private String dosingNo5;
    private String dosingNo9;

    private EditText mSetDosingWater;
    private EditText mSetDosingCupNum;
    private EditText mSetDosingNo1;
    private EditText mSetDosingNo2;
    private EditText mSetDosingNo3;
    private EditText mSetDosingNo4;
    private EditText mSetDosingNo5;
    private EditText mSetDosingNo9;

    private Button mAddStockBtn;
    private Button mResetStockBtn;
	
	public ControlStockFragment() {
		this.setFragmentId(R.id.add_dosing_fragment);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_add_dosing, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initOrUpdateStock();
    }
    
    private void initView(){
        mSetDosingWater = (EditText) getView().findViewById(R.id.set_dosing_water);
        mSetDosingWater.setEnabled(false);
        mSetDosingCupNum = (EditText) getView().findViewById(R.id.set_dosing_cup);
        mSetDosingCupNum.setEnabled(false);
        mSetDosingNo1 = (EditText) getView().findViewById(R.id.set_dosing_no1);
        mSetDosingNo1.setEnabled(false);
        mSetDosingNo2 = (EditText) getView().findViewById(R.id.set_dosing_no2);
        mSetDosingNo2.setEnabled(false);
        mSetDosingNo3 = (EditText) getView().findViewById(R.id.set_dosing_no3);
        mSetDosingNo3.setEnabled(false);
        mSetDosingNo4 = (EditText) getView().findViewById(R.id.set_dosing_no4);
        mSetDosingNo4.setEnabled(false);
        mSetDosingNo5 = (EditText) getView().findViewById(R.id.set_dosing_no5);
        mSetDosingNo5.setEnabled(false);
        mSetDosingNo9 = (EditText) getView().findViewById(R.id.set_dosing_no9);
        mSetDosingNo9.setEnabled(false);

        mAddStockBtn = (Button) getView().findViewById(R.id.control_add_stock);
        mAddStockBtn.setOnClickListener(this);
        mResetStockBtn = (Button) getView().findViewById(R.id.control_reset_stock);
        mResetStockBtn.setOnClickListener(this);
    }

    private void initOrUpdateStock(){
        mSetDosingWater.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));
        mSetDosingCupNum.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
        mSetDosingNo1.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
        mSetDosingNo2.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
        mSetDosingNo3.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
        mSetDosingNo4.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
        mSetDosingNo5.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5));
        mSetDosingNo9.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.control_add_stock:
                updateStock();
                break;
            case R.id.control_reset_stock:
                resetStock();
                break;
        }
    }

    private void updateStock(){
        ControlAddStockDialog dialog = new ControlAddStockDialog(getActivity(), addStockDialogListener, false);
        dialog.show();
    }

    private void resetStock(){
        ControlAddStockDialog dialog = new ControlAddStockDialog(getActivity(), addStockDialogListener, true);
        dialog.show();
    }

    private OnAddStockDialogListener addStockDialogListener = new OnAddStockDialogListener(){

        @Override
        public void addStockCancel() {
            //DO NOTHING
        }

        @Override
        public void addStockConfirm(boolean reset, String water, String cupNum, String no1, String no2, String no3,
                                    String no4, String no5, String no9) {
            isStockReset = reset;
            dosingWater = water;
            dosingCupNum = cupNum;
            dosingNo1 = no1;
            dosingNo2 = no2;
            dosingNo3 = no3;
            dosingNo4 = no4;
            dosingNo5 = no5;
            dosingNo9 = no9;

            getCoffeeDosingList();
        }
    };

    private void getCoffeeDosingList(){
        ProgressDlgHelper.showProgress(getActivity(), "获取配料列表");
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                ProgressDlgHelper.closeProgress();
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && !result.isAuto()) {
                    if (result.getResCode() == 200) {
                        if (isStockReset) {
                            doStockReset(result.getDosings());
                        } else {
                            doStockAdd(result.getDosings());
                        }
                    } else {
                        ToastUtil.showToast(getActivity(), "获取配料失败");
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_STOCK_ADD) {
                ProgressDlgHelper.closeProgress();
                AddStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(getActivity(), "添加物料成功");

                        // update local record
                        double water = Double.parseDouble(dosingWater) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(water), MachineMaterialMap.MATERIAL_WATER);
                        double cupNum = Double.parseDouble(dosingCupNum) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(cupNum), MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
                        double dosing1 = Double.parseDouble(dosingNo1) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing1), MachineMaterialMap.MATERIAL_BOX_1);
                        double dosing2 = Double.parseDouble(dosingNo2) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing2), MachineMaterialMap.MATERIAL_BOX_2);
                        double dosing3 = Double.parseDouble(dosingNo3) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing3), MachineMaterialMap.MATERIAL_BOX_3);
                        double dosing4 = Double.parseDouble(dosingNo4) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing4), MachineMaterialMap.MATERIAL_BOX_4);
                        double dosing5 = Double.parseDouble(dosingNo5) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing5), MachineMaterialMap.MATERIAL_BOX_5);
                        double dosing9 = Double.parseDouble(dosingNo9) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing9), MachineMaterialMap.MATERIAL_COFFEE_BEAN);

                        //update ui
                        initOrUpdateStock();
                    } else {
                        ToastUtil.showToast(getActivity(), "添加物料失败");
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_STOCK_RESET) {
                ProgressDlgHelper.closeProgress();
                ResetStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(getActivity(), "矫正物料成功");

                        // update local record
                        double water = Double.parseDouble(dosingWater);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(water), MachineMaterialMap.MATERIAL_WATER);
                        double cupNum = Double.parseDouble(dosingCupNum);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(cupNum), MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
                        double dosing1 = Double.parseDouble(dosingNo1);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing1), MachineMaterialMap.MATERIAL_BOX_1);
                        double dosing2 = Double.parseDouble(dosingNo2);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing2), MachineMaterialMap.MATERIAL_BOX_2);
                        double dosing3 = Double.parseDouble(dosingNo3);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing3), MachineMaterialMap.MATERIAL_BOX_3);
                        double dosing4 = Double.parseDouble(dosingNo4);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing4), MachineMaterialMap.MATERIAL_BOX_4);
                        double dosing5 = Double.parseDouble(dosingNo5);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing5), MachineMaterialMap.MATERIAL_BOX_5);
                        double dosing9 = Double.parseDouble(dosingNo9);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing9), MachineMaterialMap.MATERIAL_COFFEE_BEAN);

                        //update ui
                        initOrUpdateStock();
                    } else {
                        ToastUtil.showToast(getActivity(), "矫正物料失败");
                    }
                }
            }
        }
    }

    private void doStockAdd(List<CoffeeDosingInfo> dosings){
        JSONArray array = new JSONArray();

        for(CoffeeDosingInfo info : dosings){
            double totalValue = 0;
            double addValue = 0;

            if(info.getId() == 1){
                addValue = TextUtils.isEmpty(dosingWater) ? 0 : Double.parseDouble(dosingWater);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
            }else if(info.getId() == 2){
                addValue = TextUtils.isEmpty(dosingCupNum) ? 0 : Double.parseDouble(dosingCupNum);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1){
                addValue = TextUtils.isEmpty(dosingNo1) ? 0 : Double.parseDouble(dosingNo1);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2){
                addValue = TextUtils.isEmpty(dosingNo2) ? 0 : Double.parseDouble(dosingNo2);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3){
                addValue = TextUtils.isEmpty(dosingNo3) ? 0 : Double.parseDouble(dosingNo3);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4){
                addValue = TextUtils.isEmpty(dosingNo4) ? 0 : Double.parseDouble(dosingNo4);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5){
                addValue = TextUtils.isEmpty(dosingNo5) ? 0 : Double.parseDouble(dosingNo5);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                addValue = TextUtils.isEmpty(dosingNo9) ? 0 : Double.parseDouble(dosingNo9);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
            }

            if(addValue <= 0)
                continue;

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", info.getId());
            jsonObj.put("add_value", addValue);
            jsonObj.put("total_value", totalValue);
            array.add(jsonObj);
        }

        ProgressDlgHelper.showProgress(getActivity(), "更新配料库存");
        AddStockInfo info = new AddStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setUserID(1);
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    private void doStockReset(List<CoffeeDosingInfo> dosings){
        JSONArray array = new JSONArray();

        for(CoffeeDosingInfo info : dosings){
            double actualValue = 0;
            double currentValue = 0;

            if(info.getId() == 1){
                actualValue = TextUtils.isEmpty(dosingWater) ? 0 : Double.parseDouble(dosingWater);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
            }else if(info.getId() == 2){
                actualValue = TextUtils.isEmpty(dosingCupNum) ? 0 : Double.parseDouble(dosingCupNum);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1){
                actualValue = TextUtils.isEmpty(dosingNo1) ? 0 : Double.parseDouble(dosingNo1);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2){
                actualValue = TextUtils.isEmpty(dosingNo2) ? 0 : Double.parseDouble(dosingNo2);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3){
                actualValue = TextUtils.isEmpty(dosingNo3) ? 0 : Double.parseDouble(dosingNo3);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4){
                actualValue = TextUtils.isEmpty(dosingNo4) ? 0 : Double.parseDouble(dosingNo4);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5){
                actualValue = TextUtils.isEmpty(dosingNo5) ? 0 : Double.parseDouble(dosingNo5);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                actualValue = TextUtils.isEmpty(dosingNo9) ? 0 : Double.parseDouble(dosingNo9);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
            }

            if(actualValue < 0)
                continue;

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", info.getId());
            jsonObj.put("actual_value", actualValue);
            jsonObj.put("current_value", currentValue);
            array.add(jsonObj);
        }

        ProgressDlgHelper.showProgress(getActivity(), "矫正配料库存");

        ResetStockInfo info = new ResetStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setUserID(1);
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    public void refresh(){
        initOrUpdateStock();
    }
}
