package com.jingye.coffeemac.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;

/**
 * Created by Hades on 2016/10/31.
 */

public class DialogAddStock extends BaseDialog implements View.OnClickListener {

    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";
    private static final String NAME3 = "name3";
    private static final String NAME4 = "name4";
    private static final String NAME5 = "name5";
    private static final String NAME9 = "name9";

    private static final int FOUR=4;
    private static final int FIVE=5;
    private static final int TWO=2;
    private static final String TITLE1 = "title1";
    private static final String TITLE2 = "title2";
    private IDialogAddStock mListener;
    private Context mContext;
    private TextView tvAddDialogHint1;
    private TextView tvAddDialogHint2;
    private TextView tvAddDialogHint3;
    private TextView tvAddDialogHint4;
    private TextView tvAddDialogHint5;
    private TextView tvAddDialogHint9;
    private TextView tvAddDialogHintCupValue;
    private TextView tvAddDialogHintWaterValue;
    private Button btnCancel;
    private Button btnOk;
    private TextView tvAddDialogTitle1;
    private TextView tvAddDialogTitle2;
    private TextView tvAddDialogName1;
    private TextView tvAddDialogName2;
    private TextView tvAddDialogName3;
    private TextView tvAddDialogName4;
    private TextView tvAddDialogName5;
    private TextView tvAddDialogName9;
    private EditText etAddDialogValue1;
    private EditText etAddDialogValue2;
    private EditText etAddDialogValue3;
    private EditText etAddDialogValue4;
    private EditText etAddDialogValue5;
    private EditText etAddDialogValue9;
    private EditText etAddDialogCupValue;
    private EditText etAddDialogWaterValue;

    public static DialogAddStock newInstance(String title1, String title2, String name1,String name2,String name3,String name4,String name5,String name9) {
        DialogAddStock dialogAddStock = new DialogAddStock();
        Bundle args = new Bundle();
        args.putString(TITLE1, title1);
        args.putString(TITLE2, title2);
        args.putString(NAME1, name1);
        args.putString(NAME2, name2);
        args.putString(NAME3, name3);
        args.putString(NAME4, name4);
        args.putString(NAME5, name5);
        args.putString(NAME9, name9);
        dialogAddStock.setArguments(args);
        return dialogAddStock;
    }

    public void setListener(IDialogAddStock iDialogAddStock){
        this.mListener=iDialogAddStock;
    }

    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_stock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initValues();

        mContext=view.getContext();

    }

    private void initValues() {
        tvAddDialogTitle1.setText(getArguments().getString(TITLE1, "操作类型1"));
        tvAddDialogTitle2.setText(getArguments().getString(TITLE2, "标题2"));

        tvAddDialogName1.setText(getArguments().getString(NAME1,"一号"));
        tvAddDialogName2.setText(getArguments().getString(NAME2,"二号"));
        tvAddDialogName3.setText(getArguments().getString(NAME3,"三号"));
        tvAddDialogName4.setText(getArguments().getString(NAME4,"四号"));
        tvAddDialogName5.setText(getArguments().getString(NAME5,"五号"));
        tvAddDialogName9.setText(getArguments().getString(NAME9,"九号"));


//        etAddDialogValue1.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
//        etAddDialogValue2.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
//        etAddDialogValue3.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
//        etAddDialogValue4.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
//        etAddDialogValue5.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5));
//        etAddDialogValue9.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));
//        etAddDialogCupValue.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
//        etAddDialogWaterValue.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));

        etAddDialogValue1.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
        etAddDialogValue2.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
        etAddDialogValue3.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
        etAddDialogValue4.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
        etAddDialogValue5.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5));
        etAddDialogValue9.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));
        etAddDialogCupValue.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
        etAddDialogWaterValue.setHint("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));

        etAddDialogValue1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue1,tvAddDialogHint1,FOUR);
                }
            }
        });

        etAddDialogValue2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue2,tvAddDialogHint2,FOUR);
                }
            }
        });
        etAddDialogValue3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue3,tvAddDialogHint3,FOUR);
                }
            }
        });
        etAddDialogValue4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue4,tvAddDialogHint4,FOUR);
                }
            }
        });

        etAddDialogValue5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue5,tvAddDialogHint5,FOUR);
                }
            }
        });
        etAddDialogValue9.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogValue9,tvAddDialogHint9,FOUR);
                }
            }
        });

        etAddDialogCupValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogCupValue,tvAddDialogHintCupValue,TWO);
                }
            }
        });

        etAddDialogWaterValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    onChangeLengthListener(etAddDialogWaterValue,tvAddDialogHintWaterValue,FIVE);
                }
            }
        });

    }

    /**
     * 监听 EditText的位数变化，当失去焦点的时候
     * @param localValue
     * @param localHint
     * @param length
     */
    private void onChangeLengthListener(EditText localValue, TextView localHint, int length) {
        if(localValue.getText().toString().length()<length){
            localHint.setVisibility(View.VISIBLE);
        }else{
            localHint.setVisibility(View.INVISIBLE);
        }
    }

    private void initView(View view) {
        tvAddDialogTitle1 = (TextView) view.findViewById(R.id.tvAddDialogTitle1);
        tvAddDialogTitle2 = (TextView) view.findViewById(R.id.tvAddDialogTitle2);
        tvAddDialogName1= (TextView) view.findViewById(R.id.tvAddDialogName1);
        tvAddDialogName2= (TextView) view.findViewById(R.id.tvAddDialogName2);
        tvAddDialogName3= (TextView) view.findViewById(R.id.tvAddDialogName3);
        tvAddDialogName4= (TextView) view.findViewById(R.id.tvAddDialogName4);
        tvAddDialogName5= (TextView) view.findViewById(R.id.tvAddDialogName5);
        tvAddDialogName9= (TextView) view.findViewById(R.id.tvAddDialogName9);

        tvAddDialogHint1= (TextView) view.findViewById(R.id.tvAddDialogHint1);
        tvAddDialogHint2= (TextView) view.findViewById(R.id.tvAddDialogHint2);
        tvAddDialogHint3= (TextView) view.findViewById(R.id.tvAddDialogHint3);
        tvAddDialogHint4= (TextView) view.findViewById(R.id.tvAddDialogHint4);
        tvAddDialogHint5= (TextView) view.findViewById(R.id.tvAddDialogHint5);
        tvAddDialogHint9 = (TextView) view.findViewById(R.id.tvAddDialogHint9);
        tvAddDialogHintCupValue = (TextView) view.findViewById(R.id.tvAddDialogHintCupValue);
        tvAddDialogHintWaterValue = (TextView) view.findViewById(R.id.tvAddDialogHintWaterValue);

        etAddDialogValue1= (EditText) view.findViewById(R.id.etAddDialogValue1);
        etAddDialogValue2= (EditText) view.findViewById(R.id.etAddDialogValue2);
        etAddDialogValue3= (EditText) view.findViewById(R.id.etAddDialogValue3);
        etAddDialogValue4= (EditText) view.findViewById(R.id.etAddDialogValue4);
        etAddDialogValue5= (EditText) view.findViewById(R.id.etAddDialogValue5);
        etAddDialogValue9= (EditText) view.findViewById(R.id.etAddDialogValue9);
        etAddDialogCupValue= (EditText) view.findViewById(R.id.etAddDialogCupValue);
        etAddDialogWaterValue= (EditText) view.findViewById(R.id.etAddDialogWaterValue);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnOk = (Button) view.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

    }

    @Override
    protected int setDialogWidth() {
        return 1460;
    }

    @Override
    public void onClick(View view) {
        if (isVisible()) {
            switch (view.getId()){
                case R.id.btnCancel:
                    if(mListener!=null){
                        mListener.onDialogItemCancleClick();
                    }
                    dismiss();
                    break;
                case R.id.btnOk:
                    if(mListener!=null){

                        String dosingWaterStr = etAddDialogWaterValue.getText().toString();
                        String dosingCupNumStr = etAddDialogCupValue.getText().toString();
                        String dosingNo1Str = etAddDialogValue1.getText().toString();
                        String dosingNo2Str = etAddDialogValue2.getText().toString();
                        String dosingNo3Str = etAddDialogValue3.getText().toString();
                        String dosingNo4Str = etAddDialogValue4.getText().toString();
                        String dosingNo5Str = etAddDialogValue5.getText().toString();
                        String dosingNo9Str = etAddDialogValue9.getText().toString();

                        if(TextUtils.isEmpty(dosingWaterStr) || TextUtils.isEmpty(dosingCupNumStr) || TextUtils.isEmpty(dosingNo1Str)
                                || TextUtils.isEmpty(dosingNo2Str) || TextUtils.isEmpty(dosingNo3Str) || TextUtils.isEmpty(dosingNo4Str)
                                || TextUtils.isEmpty(dosingNo5Str) || TextUtils.isEmpty(dosingNo9Str)){
                            ToastUtil.showToast(mContext, R.string.control_set_dosing_is_null);
                            return;
                        }
                        mListener.onDialogItemOkClick(dosingNo1Str,dosingNo2Str,dosingNo3Str,dosingNo4Str,dosingNo5Str,dosingNo9Str,dosingCupNumStr,dosingWaterStr);
                        dismiss();
                    }
                    break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //设置为不可取消
        dialog.setCancelable(false);
        //设置为dialog外点击不可取消
        dialog.setCanceledOnTouchOutside(false);
        //设置点击返回键或搜索键不可取消
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });
        return dialog;
    }

    public interface IDialogAddStock{
        void onDialogItemCancleClick();
        void onDialogItemOkClick(String addDialogValue1,String addDialogValue2,String addDialogValue3,String addDialogValue4,
                                 String addDialogValue5,String addDialogValue9,String addDialogCupValue,String addDialogWaterValue);
    }
}
