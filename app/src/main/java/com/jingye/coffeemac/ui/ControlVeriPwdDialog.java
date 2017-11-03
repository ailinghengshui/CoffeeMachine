package com.jingye.coffeemac.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.ToastUtil;

public class ControlVeriPwdDialog extends Dialog implements View.OnClickListener{

	public interface OnVerifyPwdDialogListener{
        public void verifyPwdCancel();
        public void verifyPwdConfirm(String password, int action);
	}

	private Context mContext;
	private OnVerifyPwdDialogListener mVerifyPwdDialogListener;
    private int mType;

	private TextView mTitleView;
    private EditText mVerifyPassword;


	private Button mCancel;
	private Button mConform;

	public ControlVeriPwdDialog(Context context, OnVerifyPwdDialogListener listener, int type) {
		super(context, R.style.dialog_style);
		this.mContext = context;
        this.mVerifyPwdDialogListener = listener;
        this.mType = type;
	}

	public ControlVeriPwdDialog(Context context, int theme) {
		super(context, theme);
	}

	public ControlVeriPwdDialog(Context context) {
		super(context, R.style.dialog_style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_alert_dialog_for_verify_pwd);
		
		LinearLayout root = (LinearLayout) findViewById(R.id.easy_edit_dialog_root);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.width = mContext.getResources().getDimensionPixelSize(R.dimen.easy_dialog_width);
        root.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.easy_dialog_title_text_view);
        mTitleView.setText("密码验证");

        mVerifyPassword = (EditText) findViewById(R.id.control_verify_password);

        mCancel = (Button) findViewById(R.id.easy_dialog_negative_btn);
        mConform = (Button) findViewById(R.id.easy_dialog_positive_btn);
		mCancel.setOnClickListener(this);
		mConform.setOnClickListener(this);
		mCancel.setVisibility(View.VISIBLE);
		findViewById(R.id.easy_dialog_btn_divide_view).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.easy_dialog_negative_btn:
            mVerifyPwdDialogListener.verifyPwdCancel();
			dismiss();
			break;
		case R.id.easy_dialog_positive_btn:
            String verifyPwd = mVerifyPassword.getText().toString();
            if(TextUtils.isEmpty(verifyPwd)){
                ToastUtil.showToast(mContext, "密码不能为空");
                return;
            }

            mVerifyPwdDialogListener.verifyPwdConfirm(verifyPwd, mType);
			dismiss();
			break;
		default:
			break;
		}
	}
}
