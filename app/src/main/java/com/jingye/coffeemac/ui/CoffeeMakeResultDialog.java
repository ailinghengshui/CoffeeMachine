package com.jingye.coffeemac.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;

public class CoffeeMakeResultDialog extends Dialog implements View.OnClickListener{

	public interface OnCoffeeMakeResultDialogListener{
        public void OnMakeCoffeeCancel();
        public void OnMakeCoffeeConfirm();
	}

	private Context mContext;
	private OnCoffeeMakeResultDialogListener mListener;
	private boolean isShowCancel;
	private String message;

	private TextView mTitleView;
	private TextView mMessageView;
	private Button mCancel;
	private Button mConform;

	public CoffeeMakeResultDialog(Context context, OnCoffeeMakeResultDialogListener listener, String message) {
		super(context, R.style.dialog_style);
		this.mContext = context;
        this.mListener = listener;
		this.message = message;
		this.isShowCancel = isShowCancel;
	}

	public CoffeeMakeResultDialog(Context context, int theme) {
		super(context, theme);
	}

	public CoffeeMakeResultDialog(Context context) {
		super(context, R.style.dialog_style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_alert_dialog_for_make_coffee);
		
		LinearLayout root = (LinearLayout) findViewById(R.id.easy_edit_dialog_root);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.width = mContext.getResources().getDimensionPixelSize(R.dimen.easy_dialog_width);
        root.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.easy_dialog_title_text_view);
        mTitleView.setText("提示");
		mMessageView = (TextView) findViewById(R.id.easy_dialog_message_text_view);
		mMessageView.setText(message);

        mCancel = (Button) findViewById(R.id.easy_dialog_negative_btn);
        mConform = (Button) findViewById(R.id.easy_dialog_positive_btn);
		mCancel.setOnClickListener(this);
		mConform.setOnClickListener(this);
		findViewById(R.id.easy_dialog_btn_divide_view).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.easy_dialog_negative_btn:
            mListener.OnMakeCoffeeCancel();
			dismiss();
			break;
		case R.id.easy_dialog_positive_btn:
            mListener.OnMakeCoffeeConfirm();
			dismiss();
			break;
		default:
			break;
		}
	}
}
