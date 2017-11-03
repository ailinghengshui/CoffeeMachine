package com.jingye.coffeemac.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.module.managermodule.ManagerControlActivity;
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
import com.jingye.coffeemac.ui.DialogAddStock;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;

import java.util.List;

/**
 * this class implements dosing manager
 * Created by Hades on 2016/10/25.
 */
public class ManagerControlDosingFragment extends TFragment implements View.OnClickListener {

    private static final String ID = "id";
    private static final int DOSING_NOT_MATCH = 406;
    private TextView tvManagerControlMaterialName1;
    private TextView tvManagerControlMaterialName2;
    private TextView tvManagerControlMaterialName3;
    private TextView tvManagerControlMaterialName4;
    private TextView tvManagerControlMaterialName5;
    private TextView tvManagerControlMaterialName9;
    private TextView tvManagerControlMaterialValue1;
    private TextView tvManagerControlMaterialValue2;
    private TextView tvManagerControlMaterialValue3;
    private TextView tvManagerControlMaterialValue4;
    private TextView tvManagerControlMaterialValue5;
    private TextView tvManagerControlMaterialValue9;
    private TextView tvManagerControlCupValue;
    private TextView tvManagerControlWaterValue;
    private Button btnAddStock;
    private Button btnResetStock;
    private List<CoffeeDosingInfo> mDosings;

    private String dosingNo1;
    private String dosingNo2;
    private String dosingNo3;
    private String dosingNo4;
    private String dosingNo5;
    private String dosingNo9;

    private String dosingWater;
    private String dosingCupNum;
    private DialogAddStock.IDialogAddStock mResetStockListener = new DialogAddStock.IDialogAddStock() {
        @Override
        public void onDialogItemCancleClick() {

        }

        @Override
        public void onDialogItemOkClick(String addDialogValue1, String addDialogValue2, String addDialogValue3, String addDialogValue4, String addDialogValue5, String addDialogValue9, String addDialogCupValue, String addDialogWaterValue) {
            if (mDosings == null || mDosings.size() == 0) {
                ToastUtil.showToast(getContext(), "物料为空");
                return;
            } else {
                dosingNo1 = addDialogValue1;
                dosingNo2 = addDialogValue2;
                dosingNo3 = addDialogValue3;
                dosingNo4 = addDialogValue4;
                dosingNo5 = addDialogValue5;
                dosingNo9 = addDialogValue9;
                dosingCupNum = addDialogCupValue;
                dosingWater = addDialogWaterValue;
                doStockReset(mDosings);
            }

        }
    };

    private DialogAddStock.IDialogAddStock mAddStockListener = new DialogAddStock.IDialogAddStock() {
        @Override
        public void onDialogItemCancleClick() {

        }

        @Override
        public void onDialogItemOkClick(String addDialogValue1, String addDialogValue2, String addDialogValue3, String addDialogValue4, String addDialogValue5, String addDialogValue9, String addDialogCupValue, String addDialogWaterValue) {
            if (mDosings == null || mDosings.size() == 0) {
                ToastUtil.showToast(getContext(), "物料为空");
                return;
            } else {
                dosingNo1 = addDialogValue1;
                dosingNo2 = addDialogValue2;
                dosingNo3 = addDialogValue3;
                dosingNo4 = addDialogValue4;
                dosingNo5 = addDialogValue5;
                dosingNo9 = addDialogValue9;
                dosingCupNum = addDialogCupValue;
                dosingWater = addDialogWaterValue;
                doStockAdd(mDosings);
            }


        }
    };


    public ManagerControlDosingFragment() {
//        this.setFragmentId(R.id.dosing_fragment);
        this.setFragmentId(R.id.repair_debug_fragment);
    }

    public static ManagerControlDosingFragment newInstance(int id) {

        Bundle args = new Bundle();

        ManagerControlDosingFragment fragment = new ManagerControlDosingFragment();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_control_dosing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initOrUpdateStock();

        getCoffeeDosingList();
    }

    private void getCoffeeDosingList() {
        ProgressDlgHelper.showProgress(getActivity(), "获取配料列表");
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    private void initOrUpdateStock() {
        tvManagerControlWaterValue.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));
        tvManagerControlCupValue.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
        tvManagerControlMaterialValue1.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
        tvManagerControlMaterialValue2.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
        tvManagerControlMaterialValue3.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
        tvManagerControlMaterialValue4.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
        tvManagerControlMaterialValue5.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5));
        tvManagerControlMaterialValue9.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));

    }

    private void initView(View view) {
        tvManagerControlMaterialName1 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName1);
        tvManagerControlMaterialName2 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName2);
        tvManagerControlMaterialName3 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName3);
        tvManagerControlMaterialName4 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName4);
        tvManagerControlMaterialName5 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName5);
        tvManagerControlMaterialName9 = (TextView) view.findViewById(R.id.tvManagerControlMaterialName9);

        tvManagerControlMaterialValue1 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue1);
        tvManagerControlMaterialValue2 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue2);
        tvManagerControlMaterialValue3 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue3);
        tvManagerControlMaterialValue4 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue4);
        tvManagerControlMaterialValue5 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue5);
        tvManagerControlMaterialValue9 = (TextView) view.findViewById(R.id.tvManagerControlMaterialValue9);
        tvManagerControlCupValue = (TextView) view.findViewById(R.id.tvManagerControlCupValue);
        tvManagerControlWaterValue = (TextView) view.findViewById(R.id.tvManagerControlWaterValue);

        btnAddStock = (Button) view.findViewById(R.id.btnAddStock);
        btnResetStock = (Button) view.findViewById(R.id.btnResetStock);
        btnAddStock.setOnClickListener(this);
        btnResetStock.setOnClickListener(this);

    }


    @Override
    public void onReceive(Remote remote) {

        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                ProgressDlgHelper.closeProgress();
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && !result.isAuto()) {
                    if (result.getResCode() == 200) {
                        doInitDosingName(result.getDosings());
                        mDosings = result.getDosings();
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

//                         update local record
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
                    } else if (result.getResCode() == DOSING_NOT_MATCH) {
                        ToastUtil.showToast(getContext(), "物料匹配失败，请退出登录后再添加物料");
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
                    } else if (result.getResCode() == DOSING_NOT_MATCH) {
                        ToastUtil.showToast(getContext(), "物料匹配失败，请退出登录后再矫正物料");

                    } else {
                        ToastUtil.showToast(getActivity(), "矫正物料失败");
                    }
                }
            }
        }

    }

    public List<CoffeeDosingInfo> getFactor(){
        return mDosings;
    }


    private void doInitDosingName(List<CoffeeDosingInfo> dosings) {
        if (dosings == null) {
            ToastUtil.showToast(getActivity(), R.string.control_doing_list_is_null);
            return;
        }

        for (CoffeeDosingInfo info : dosings) {
            if (info.getId() == 1) {
                continue;
            } else if (info.getId() == 2) {
                continue;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName1.setText(getString(R.string.str_dosing_1, info.getName()));
                }
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName2.setText(getString(R.string.str_dosing_2, info.getName()));
                }
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName3.setText(getString(R.string.str_dosing_3, info.getName()));
                }
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName4.setText(getString(R.string.str_dosing_4, info.getName()));
                }
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName5.setText(getString(R.string.str_dosing_5, info.getName()));
                }
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN) {
                if (!TextUtils.isEmpty(info.getName())) {
                    tvManagerControlMaterialName9.setText(getString(R.string.str_dosing_9, info.getName()));
                }
            }
        }

    }


    private void doStockAdd(List<CoffeeDosingInfo> dosings) {
        JSONArray array = new JSONArray();

        for (CoffeeDosingInfo info : dosings) {
            double totalValue = 0;
            double addValue = 0;

            if (info.getId() == 1) {
                addValue = TextUtils.isEmpty(dosingWater) ? 0 : Double.parseDouble(dosingWater);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
            } else if (info.getId() == 2) {
                addValue = TextUtils.isEmpty(dosingCupNum) ? 0 : Double.parseDouble(dosingCupNum);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1) {
                addValue = TextUtils.isEmpty(dosingNo1) ? 0 : Double.parseDouble(dosingNo1);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2) {
                addValue = TextUtils.isEmpty(dosingNo2) ? 0 : Double.parseDouble(dosingNo2);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3) {
                addValue = TextUtils.isEmpty(dosingNo3) ? 0 : Double.parseDouble(dosingNo3);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4) {
                addValue = TextUtils.isEmpty(dosingNo4) ? 0 : Double.parseDouble(dosingNo4);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5) {
                addValue = TextUtils.isEmpty(dosingNo5) ? 0 : Double.parseDouble(dosingNo5);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN) {
                addValue = TextUtils.isEmpty(dosingNo9) ? 0 : Double.parseDouble(dosingNo9);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
            }

            if (addValue <= 0)
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
        info.setUserID(getArguments().getInt(ID, 1));
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    private void doStockReset(List<CoffeeDosingInfo> dosings) {
        JSONArray array = new JSONArray();

        for (CoffeeDosingInfo info : dosings) {
            double actualValue = 0;
            double currentValue = 0;

            if (info.getId() == 1) {
                actualValue = TextUtils.isEmpty(dosingWater) ? 0 : Double.parseDouble(dosingWater);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
            } else if (info.getId() == 2) {
                actualValue = TextUtils.isEmpty(dosingCupNum) ? 0 : Double.parseDouble(dosingCupNum);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1) {
                actualValue = TextUtils.isEmpty(dosingNo1) ? 0 : Double.parseDouble(dosingNo1);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2) {
                actualValue = TextUtils.isEmpty(dosingNo2) ? 0 : Double.parseDouble(dosingNo2);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3) {
                actualValue = TextUtils.isEmpty(dosingNo3) ? 0 : Double.parseDouble(dosingNo3);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4) {
                actualValue = TextUtils.isEmpty(dosingNo4) ? 0 : Double.parseDouble(dosingNo4);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5) {
                actualValue = TextUtils.isEmpty(dosingNo5) ? 0 : Double.parseDouble(dosingNo5);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN) {
                actualValue = TextUtils.isEmpty(dosingNo9) ? 0 : Double.parseDouble(dosingNo9);
                currentValue = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
            }

            if (actualValue < 0)
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
        info.setUserID(getArguments().getInt(ID, 1));
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddStock:
                DialogAddStock addStockDialog = DialogAddStock.newInstance("料盒库存添加", "非料盒库存添加",
                        tvManagerControlMaterialName1.getText().toString(),
                        tvManagerControlMaterialName2.getText().toString(),
                        tvManagerControlMaterialName3.getText().toString(),
                        tvManagerControlMaterialName4.getText().toString(),
                        tvManagerControlMaterialName5.getText().toString(),
                        tvManagerControlMaterialName9.getText().toString());
                addStockDialog.setListener(mAddStockListener);
                addStockDialog.show(getActivity().getSupportFragmentManager(), "addStock");
                break;
            case R.id.btnResetStock:
                DialogAddStock resetStockDialog = DialogAddStock.newInstance("料盒库存校准", "非料盒库存校准",
                        tvManagerControlMaterialName1.getText().toString(),
                        tvManagerControlMaterialName2.getText().toString(),
                        tvManagerControlMaterialName3.getText().toString(),
                        tvManagerControlMaterialName4.getText().toString(),
                        tvManagerControlMaterialName5.getText().toString(),
                        tvManagerControlMaterialName9.getText().toString());
                resetStockDialog.setListener(mResetStockListener);
                resetStockDialog.show(getActivity().getSupportFragmentManager(), "resetStock");
                break;
        }

    }
}
