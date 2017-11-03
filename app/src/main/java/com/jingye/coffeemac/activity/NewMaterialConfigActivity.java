package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.module.heatmodule.HeatActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.GetDosingListInfo;
import com.jingye.coffeemac.service.bean.action.SyncStockInfo;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetDosingResult;
import com.jingye.coffeemac.service.bean.result.UpdateStockResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;

import java.util.List;

import static com.jingye.coffeemac.R.id.tvMaterialName1;
import static com.jingye.coffeemac.R.id.tvMaterialValue1;

public class NewMaterialConfigActivity extends TActivity implements View.OnClickListener {

    private Context mContext;

    private boolean foreground;

    private EditText mSetDosingWater;
    private EditText mSetDosingCupNum;
    private EditText mSetDosingNo1;
    private EditText mSetDosingNo2;
    private EditText mSetDosingNo3;
    private EditText mSetDosingNo4;
    private EditText mSetDosingNo5;
    private EditText mSetDosingNo9;

    private String dosingWater;
    private String dosingCupNum;
    private String dosingNo1;
    private String dosingNo2;
    private String dosingNo3;
    private String dosingNo4;
    private String dosingNo5;
    private String dosingNo9;

    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private TextView tvMaterialName1;
    private EditText etMaterialValue1;
    private TextView tvMaterialName2;
    private EditText etMaterialValue2;
    private TextView tvMaterialName3;
    private EditText etMaterialValue3;
    private TextView tvMaterialName4;
    private EditText etMaterialValue4;
    private TextView tvMaterialName5;
    private EditText etMaterialValue5;
    private TextView tvMaterialName9;
    private EditText etMaterialValue9;
    private TextView tvMaterialCupName;
    private EditText etMaterialCupValue;
    private TextView tvMaterialWaterName;
    private EditText etMaterialWaterValue;
    private Button btnMaterail;
    private List<CoffeeDosingInfo> mDosings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_material_config);
        mContext = this;
        proceedExtras();

        initView();
        getCoffeeDosingList();

        initStatus();
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }


    private void updateStatus(int status) {
        mHomeNetworkStatus.setVisibility(View.VISIBLE);
        if (status == ITranCode.STATUS_NO_NETWORK
                || status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN) {
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_broken);
        } else if (status == ITranCode.STATUS_LOGGING) {
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connecting);
        } else {
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connected);
        }
    }

    private void proceedExtras(){
    }

    private void initView(){
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        tvMaterialName1=(TextView)findViewById(R.id.tvMaterialName1);
        etMaterialValue1=(EditText)findViewById(R.id.tvMaterialValue1);
        tvMaterialName2=(TextView)findViewById(R.id.tvMaterialName2);
        etMaterialValue2=(EditText)findViewById(R.id.tvMaterialValue2);
        tvMaterialName3=(TextView)findViewById(R.id.tvMaterialName3);
        etMaterialValue3=(EditText)findViewById(R.id.tvMaterialValue3);
        tvMaterialName4=(TextView)findViewById(R.id.tvMaterialName4);
        etMaterialValue4=(EditText)findViewById(R.id.tvMaterialValue4);
        tvMaterialName5=(TextView)findViewById(R.id.tvMaterialName5);
        etMaterialValue5=(EditText)findViewById(R.id.tvMaterialValue5);
        tvMaterialName9=(TextView)findViewById(R.id.tvMaterialName9);
        etMaterialValue9=(EditText)findViewById(R.id.tvMaterialValue9);

        tvMaterialCupName=(TextView)findViewById(R.id.tvMaterialCupName);
        etMaterialCupValue=(EditText)findViewById(R.id.tvMaterialCupValue);
        tvMaterialWaterName=(TextView)findViewById(R.id.tvMaterialWaterName);
        etMaterialWaterValue=(EditText)findViewById(R.id.tvMaterialWaterValue);
        btnMaterail=(Button)findViewById(R.id.btnMaterail);
        btnMaterail.setOnClickListener(this);

        showKeyboard(etMaterialValue1);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnMaterail:
                doInitDosing(mDosings);
                break;
            default:
                break;
        }
    }

    private void getCoffeeDosingList(){
        ProgressDlgHelper.showProgress(this, "获取原料列表");
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    private void doInitDosingName(List<CoffeeDosingInfo> dosings){
        if(dosings == null){
            ToastUtil.showToast(this, R.string.control_doing_list_is_null);
            return;
        }

        for(CoffeeDosingInfo info:dosings){
            if(info.getId()==1){
                continue;
            }else if(info.getId()==2){
                continue;
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_BOX_1){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName1.setText(getString(R.string.str_dosing_1,info.getName()));
                }
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_BOX_2){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName2.setText(getString(R.string.str_dosing_2,info.getName()));
                }
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_BOX_3){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName3.setText(getString(R.string.str_dosing_3,info.getName()));
                }
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_BOX_4){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName4.setText(getString(R.string.str_dosing_4,info.getName()));
                }
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_BOX_5){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName5.setText(getString(R.string.str_dosing_5,info.getName()));
                }
            }else if(info.getBoxID()==MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                if(!TextUtils.isEmpty(info.getName())){
                    tvMaterialName9.setText(getString(R.string.str_dosing_9,info.getName()));
                }
            }
        }

    }

    private void doInitDosing(List<CoffeeDosingInfo> dosings){
        if(dosings == null){
            ToastUtil.showToast(this, R.string.control_doing_list_is_null);
            return;
        }

        dosingWater=etMaterialWaterValue.getText().toString();
        dosingCupNum=etMaterialCupValue.getText().toString();
        dosingNo1=etMaterialValue1.getText().toString();
        dosingNo2=etMaterialValue2.getText().toString();
        dosingNo3=etMaterialValue3.getText().toString();
        dosingNo4=etMaterialValue4.getText().toString();
        dosingNo5=etMaterialValue5.getText().toString();
        dosingNo9=etMaterialValue9.getText().toString();

//        dosingWater = mSetDosingWater.getText().toString();
//        dosingCupNum = mSetDosingCupNum.getText().toString();
//        dosingNo1 = mSetDosingNo1.getText().toString();
//        dosingNo2 = mSetDosingNo2.getText().toString();
//        dosingNo3 = mSetDosingNo3.getText().toString();
//        dosingNo4 = mSetDosingNo4.getText().toString();
//        dosingNo5 = mSetDosingNo5.getText().toString();
//        dosingNo9 = mSetDosingNo9.getText().toString();
//
        if(TextUtils.isEmpty(dosingWater) || TextUtils.isEmpty(dosingCupNum) || TextUtils.isEmpty(dosingNo1)
                || TextUtils.isEmpty(dosingNo2) || TextUtils.isEmpty(dosingNo3) || TextUtils.isEmpty(dosingNo4)
                || TextUtils.isEmpty(dosingNo5) || TextUtils.isEmpty(dosingNo9)){
            ToastUtil.showToast(this, R.string.control_set_dosing_is_null);
            return;
        }
//
        JSONArray array = new JSONArray();
        for(CoffeeDosingInfo info : dosings){
            String value = "";

            if(info.getId() == 1){
                value = dosingWater;
            }else if(info.getId() == 2){
                value = dosingCupNum;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1){
                value = dosingNo1;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2){
                value = dosingNo2;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3){
                value = dosingNo3;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4){
                value = dosingNo4;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5){
                value = dosingNo5;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                value = dosingNo9;
            }

            if(!TextUtils.isEmpty(value)){
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", info.getId());
                jsonObj.put("value", Double.parseDouble(value));
                array.add(jsonObj);
            }
        }

        ProgressDlgHelper.showProgress(this, "更新配料列表");
        SyncStockInfo info = new SyncStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSetDosingWater.getWindowToken(), 0);
    }

    private void showKeyboard(EditText editText) {
        if (!foreground) {
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        foreground = false;
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                ProgressDlgHelper.closeProgress();
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && !result.isAuto()){
                    if(result.getResCode() == 200){
                        doInitDosingName(result.getDosings());
                        mDosings=result.getDosings();
                    }else{
                        ToastUtil.showToast(this, "获取原料失败...");
                    }
                }
            }else if(remote.getAction() == ITranCode.ACT_COFFEE_STOCK_UPDATE){
                ProgressDlgHelper.closeProgress();
                UpdateStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && !result.isAuto()) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(this, "初始化成功");
//                        mSetDosingBtn.setEnabled(false);
                        // update local record
                        SharePrefConfig.getInstance().setDosingValue(dosingNo1, MachineMaterialMap.MATERIAL_BOX_1);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo2, MachineMaterialMap.MATERIAL_BOX_2);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo3, MachineMaterialMap.MATERIAL_BOX_3);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo4, MachineMaterialMap.MATERIAL_BOX_4);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo5, MachineMaterialMap.MATERIAL_BOX_5);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo9, MachineMaterialMap.MATERIAL_COFFEE_BEAN);
                        SharePrefConfig.getInstance().setDosingValue(dosingWater, MachineMaterialMap.MATERIAL_WATER);
                        SharePrefConfig.getInstance().setDosingValue(dosingCupNum, MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);

                        SharePrefConfig.getInstance().setDosingInit(U.getMyVendorNum());

                        MyApplication.Instance().setLastSyncStockTime(TimeUtil.getNow_millisecond());

//                        WelcomeActivity.start(this);
                        HeatActivity.start(this);
                        this.finish();
                    }else{
                        ToastUtil.showToast(this, "初始化失败");
                    }
                }
            }
        }
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
    }
}
