package com.jingye.coffeemac.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;

public class ControlAddStockDialog extends Dialog implements View.OnClickListener{

	public interface OnAddStockDialogListener{
        public void addStockCancel();
        public void addStockConfirm(boolean reset, String water, String cupNum, String no1, String no2,
            String no3, String no4, String no5, String no9 );
	}

	private Context mContext;
	private OnAddStockDialogListener mAddStockListener;
    private boolean isReset;

	private TextView mTitleView;

    private EditText mSetDosingNo1Ori;
    private EditText mSetDosingNo2Ori;
    private EditText mSetDosingNo3Ori;
    private EditText mSetDosingNo4Ori;
    private EditText mSetDosingNo5Ori;
    private EditText mSetDosingNo9Ori;
    private EditText mSetDosingNo1;
    private EditText mSetDosingNo2;
    private EditText mSetDosingNo3;
    private EditText mSetDosingNo4;
    private EditText mSetDosingNo5;
    private EditText mSetDosingNo9;

    private EditText mSetDosingCupOri;
    private EditText mSetDosingWaterOri;
    private EditText mSetDosingCup;
    private EditText mSetDosingWater;

	private Button mCancel;
	private Button mConform;
	
	public ControlAddStockDialog(Context context, OnAddStockDialogListener addStockListener, boolean isReset) {
		super(context, R.style.dialog_style); 
		this.mContext = context;
        this.mAddStockListener = addStockListener;
        this.isReset = isReset;
	}

	public ControlAddStockDialog(Context context, int theme) {
		super(context, theme);
	}

	public ControlAddStockDialog(Context context) {
		super(context, R.style.dialog_style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_alert_dialog_for_add_stock);
		
		LinearLayout root = (LinearLayout) findViewById(R.id.easy_edit_dialog_root);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.width = (int) (ScreenUtil.screenWidth * 0.75); //mContext.getResources().getDimensionPixelSize(R.dimen.easy_dialog_width);
        root.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.easy_dialog_title_text_view);
        if(isReset){
            mTitleView.setText("请输入校准的物料量");
        }else{
            mTitleView.setText("请输入增加的物料量");
        }

        mSetDosingNo1Ori = (EditText) findViewById(R.id.control_add_dosing_no1_original);
        mSetDosingNo1Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo1Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
        mSetDosingNo1 = (EditText) findViewById(R.id.control_add_dosing_no1);
        mSetDosingNo1.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo2Ori = (EditText) findViewById(R.id.control_add_dosing_no2_original);
        mSetDosingNo2Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo2Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
        mSetDosingNo2 = (EditText) findViewById(R.id.control_add_dosing_no2);
        mSetDosingNo2.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo3Ori = (EditText) findViewById(R.id.control_add_dosing_no3_original);
        mSetDosingNo3Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo3Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
        mSetDosingNo3 = (EditText) findViewById(R.id.control_add_dosing_no3);
        mSetDosingNo3.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo4Ori = (EditText) findViewById(R.id.control_add_dosing_no4_original);
        mSetDosingNo4Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo4Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
        mSetDosingNo4 = (EditText) findViewById(R.id.control_add_dosing_no4);
        mSetDosingNo4.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo5Ori = (EditText) findViewById(R.id.control_add_dosing_no5_original);
        mSetDosingNo5Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo5Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5));
        mSetDosingNo5 = (EditText) findViewById(R.id.control_add_dosing_no5);
        mSetDosingNo5.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo9Ori = (EditText) findViewById(R.id.control_add_dosing_no9_original);
        mSetDosingNo9Ori.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingNo9Ori.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));
        mSetDosingNo9 = (EditText) findViewById(R.id.control_add_dosing_no9);
        mSetDosingNo9.setInputType(InputType.TYPE_CLASS_NUMBER);

        mSetDosingCupOri = (EditText) findViewById(R.id.control_add_dosing_cupnum_original);
        mSetDosingCupOri.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingCupOri.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
        mSetDosingCup = (EditText) findViewById(R.id.control_add_dosing_cupnum);
        mSetDosingCup.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingWaterOri = (EditText) findViewById(R.id.control_add_dosing_water_original);
        mSetDosingWaterOri.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetDosingWaterOri.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));
        mSetDosingWater = (EditText) findViewById(R.id.control_add_dosing_water);
        mSetDosingWater.setInputType(InputType.TYPE_CLASS_NUMBER);

        mCancel = (Button) findViewById(R.id.easy_dialog_negative_btn);
        mConform = (Button) findViewById(R.id.easy_dialog_positive_btn);
		mCancel.setOnClickListener(this);
		mConform.setOnClickListener(this);
		mCancel.setVisibility(View.VISIBLE);
		findViewById(R.id.easy_dialog_btn_divide_view).setVisibility(View.VISIBLE);

        setCanceledOnTouchOutside(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.easy_dialog_negative_btn:
			mAddStockListener.addStockCancel();
			dismiss();
			break;
		case R.id.easy_dialog_positive_btn:
            String dosingWaterStr = mSetDosingWater.getText().toString();
            String dosingCupNumStr = mSetDosingCup.getText().toString();
            String dosingNo1Str = mSetDosingNo1.getText().toString();
            String dosingNo2Str = mSetDosingNo2.getText().toString();
            String dosingNo3Str = mSetDosingNo3.getText().toString();
            String dosingNo4Str = mSetDosingNo4.getText().toString();
            String dosingNo5Str = mSetDosingNo5.getText().toString();
            String dosingNo9Str = mSetDosingNo9.getText().toString();

            if(TextUtils.isEmpty(dosingWaterStr) || TextUtils.isEmpty(dosingCupNumStr) || TextUtils.isEmpty(dosingNo1Str)
                    || TextUtils.isEmpty(dosingNo2Str) || TextUtils.isEmpty(dosingNo3Str) || TextUtils.isEmpty(dosingNo4Str)
                    || TextUtils.isEmpty(dosingNo5Str) || TextUtils.isEmpty(dosingNo9Str)){
                ToastUtil.showToast(mContext, R.string.control_set_dosing_is_null);
                return;
            }
			
			mAddStockListener.addStockConfirm(isReset, dosingWaterStr, dosingCupNumStr, dosingNo1Str, dosingNo2Str,
                    dosingNo3Str, dosingNo4Str, dosingNo5Str, dosingNo9Str );
			dismiss();
			break;
		default:
			break;
		}
	}
}
