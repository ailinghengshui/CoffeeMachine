package com.jingye.coffeemac.helper.dialog;

import com.jingye.coffeemac.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class EasyProgressDialog extends Dialog {

	private Context mContext;

	public EasyProgressDialog(Context context, int style) {
		super(context, style);
		mContext = context;
		WindowManager.LayoutParams Params = getWindow().getAttributes();
		Params.width = LayoutParams.MATCH_PARENT;
		Params.height = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) Params);
	}

	public EasyProgressDialog(Context context) {

		this(context, R.style.easy_dialog_style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_progress_dialog);
	}
	
}
