package com.jingye.coffeemac.helper.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import com.jingye.coffeemac.util.log.LogUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class DialogMaker {

	private static List<View> dialogViewList = new ArrayList<View>();
	private static HashMap<String, AlertDialog> dialogMap = new HashMap<String, AlertDialog>();
	private static int dialogViewCount = -1;

	private static ProgressDialog progressDialog;

	/**
	 * 创建一个可带输入框的对话框
	 * 
	 * @param context
	 *            设备上下文
	 * @param title
	 *            对话框的标题
	 * @param btnName
	 *            对话框按钮名字列表
	 * @param message
	 *            对话框显示的消息
	 * @param clickListener
	 *            监听器
	 * @param dialogKey
	 *            唯一标示对话框的key
	 * @param isEditView
	 *            对话框是否可编辑
	 * @return
	 */
	public static AlertDialog makeAlert(Context context, String title,
			String[] btnName, String message, OnClickListener clickListener,
			String dialogKey, boolean isEditView) {
		if (null == context) {
			return null;
		}
		if (null == btnName || btnName.length < 1) {
			return null;
		}
		int count = btnName.length > 3 ? 3 : btnName.length;
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		for (int i = 0; i < count; i++) {
			switch (i) {
			case 0:
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, btnName[i],
						clickListener);
				break;
			case 1:
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, btnName[i],
						clickListener);
				break;
			case 2:
				dialog.setButton(DialogInterface.BUTTON_NEUTRAL, btnName[i],
						clickListener);
				break;
			default:
				break;
			}
		}
		EditText et = null;
		if (isEditView) {
			et = new EditText(context);
			dialog.setView(et);
		}
		dialog.setTitle(title);
		if (null != message)
			dialog.setMessage(message);
		if (null != clickListener && null != dialogKey) {
			dialogViewList.add(et);
			dialogViewCount++;

			dialogMap.put(dialogKey, dialog);
		}
		return dialog;
	}

	public static ProgressDialog showProgressDialog(Context context, String title,
			String message) {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
		} else if (progressDialog.getContext() != context) {
			// maybe existing dialog is running in a destroyed activity cotext
			// we should recreate one
			LogUtil.e("dialog", "there is a leaked window here,orign context: "
					+ progressDialog.getContext() + " now: " + context);
			dismissProgressDialog();
			progressDialog = new ProgressDialog(context);
		}
		
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);

		progressDialog.show();
		
		return progressDialog;
	}

	public static void dismissProgressDialog() {
		if (null == progressDialog) {
			return;
		}
		
		try {
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			// maybe we catch IllegalArgumentException here.
		}
	}

	public static void setMessage(String message) {
		if (null != progressDialog && progressDialog.isShowing()
				&& !TextUtils.isEmpty(message)) {
			progressDialog.setMessage(message);
		}
	}

	public static EditText getCurrentDialogEditText() {
		EditText et = null;
		if (dialogViewCount > -1) {

			et = (EditText) dialogViewList.get(dialogViewCount);
			dialogViewCount--;
		}
		return et;
	}

	public static AlertDialog getDialogByDialogKey(String dialogKey,
			boolean isRemove) {
		if (null == dialogKey)
			return null;
		if (isRemove && dialogMap.containsKey(dialogKey)) {
			dialogViewCount = -1;
			dialogViewList.clear();
			return dialogMap.remove(dialogKey);
		}
		return dialogMap.get(dialogKey);
	}

	public void removeDialogByDialogKey(String dialogKey) {
		if (dialogMap.containsKey(dialogKey)) {
			dialogViewCount = -1;
			dialogViewList.clear();
			dialogMap.remove(dialogKey);
		}
	}

	public static AlertDialog getDefineProgressDialog(Context context,
			String title, View contentView) {
		title = checkString(title, "");
		AlertDialog.Builder builder = new Builder(context);

		builder.setTitle(title);
		builder.setView(contentView);

		return builder.create();
	}

	private static String checkString(String str, String defaultValue) {
		if (null == str || str.trim().length() < 1) {
			if (null != defaultValue)
				str = defaultValue;
		}
		return str;
	}

}
