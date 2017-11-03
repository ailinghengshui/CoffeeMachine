package com.jingye.coffeemac.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jingye.coffeemac.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GenericSettingDialog extends Dialog implements View.OnClickListener{

	public interface OnGenericSettingDialog{
        public void onCancel();
        public boolean onConfirm(Map<String, String> resultMap);
	}
	
	private Context mContext;
	private OnGenericSettingDialog mOnGenericSettingDialog;
	private final Map<String, String> mProperties;
	
	private TextView mTitleView;
	private Button mCancel;
	private Button mConform;
	private LinearLayout mContentList;
	
	public GenericSettingDialog(Context context, Map<String, String> propertis, OnGenericSettingDialog mOnGenericSettingDialog) {
		super(context, R.style.dialog_style); 
		this.mContext = context; 
		this.mProperties = propertis;
        this.mOnGenericSettingDialog = mOnGenericSettingDialog;
	}

	public GenericSettingDialog(Context context, int theme) {
		super(context, theme);
		this.mProperties = new HashMap<String, String>();
	}

	public GenericSettingDialog(Context context) {
		super(context, R.style.dialog_style);
		this.mProperties = new HashMap<String, String>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_alert_dialog_with_text_list);
		
		LinearLayout root = (LinearLayout) findViewById(R.id.easy_edit_dialog_root);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.width = mContext.getResources().getDimensionPixelSize(R.dimen.easy_dialog_width);
        root.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.easy_dialog_title_text_view);
        mTitleView.setText("配置：");
        mCancel = (Button) findViewById(R.id.easy_dialog_negative_btn);
        mConform = (Button) findViewById(R.id.easy_dialog_positive_btn);
		mCancel.setOnClickListener(this);
		mConform.setOnClickListener(this);
		mCancel.setVisibility(View.VISIBLE);
		findViewById(R.id.easy_dialog_btn_divide_view).setVisibility(View.VISIBLE);
		
		mContentList = (LinearLayout) findViewById(R.id.easy_alert_dialog_text_list);
		initPropertis();
	}
	
	private void initPropertis(){
		if(mProperties==null||mProperties.size()==0){
			return;
		}
		Set<String> keys = mProperties.keySet();
		for(String key: keys){
			View view = View.inflate(mContext, R.layout.easy_alert_dialog_list_item, null);
			TextView keyText = (TextView) view.findViewById(R.id.easy_alert_dialog_key);
			keyText.setText(key);
			EditText valueText = (EditText) view.findViewById(R.id.easy_alert_dialog_value);
			valueText.setText(mProperties.get(key)+"");
			mContentList.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.easy_dialog_negative_btn:
			mOnGenericSettingDialog.onCancel();			
			dismiss();
			break;
		case R.id.easy_dialog_positive_btn:
			if(mProperties!=null&&mProperties.size()>=0){
				int size = mProperties.size();
				for(int i=0;i<size;i++){
					View view = mContentList.getChildAt(i);
					TextView keyText = (TextView) view.findViewById(R.id.easy_alert_dialog_key);
					EditText valueText = (EditText) view.findViewById(R.id.easy_alert_dialog_value);
                    mProperties.put(keyText.getText().toString(), valueText.getText().toString());
				}
			}
			if(mOnGenericSettingDialog.onConfirm(mProperties)){
				dismiss();
			}
			break;
		default:
			break;
		}
	}
}
