package com.jingye.coffeemac.helper.dialog;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;


public class EasyAlertDialog extends Dialog
{
	public static final int NO_TEXT_COLOR = -99999999;
	public static final int NO_TEXT_SIZE =  -99999999;

	private Context mContext;
	private TextView titleTV;
	private TextView messageTV;
	
	private Button positiveBtn,negativeBtn,neutralBtn;
	
	private String title="",message="",positiveBtnTitle="确定",negativeBtnTitle="取消";
	private int titleTextColor = NO_TEXT_COLOR,msgTextColor=NO_TEXT_COLOR,positiveBtnTitleTextColor=NO_TEXT_COLOR,negativeBtnTitleTextColor=NO_TEXT_COLOR;
	private float titleTextSize = NO_TEXT_SIZE,msgTextSize=NO_TEXT_SIZE,positiveBtnTitleTextSize=NO_TEXT_SIZE,negativeBtnTitleTextSize=NO_TEXT_SIZE;
	
	private int resourceId;
	
	private boolean isPositiveBtnOn=false,isNegativeBtnOn=false;
	
	private View.OnClickListener positiveBtnListener,negativeBtnListener;

	public EasyAlertDialog(Context context,int resourceId, int style){
		super(context, style);
		mContext = context;
		if(-1!=resourceId){
			setContentView(resourceId);
			this.resourceId = resourceId;
		}
		WindowManager.LayoutParams Params = getWindow().getAttributes();
		Params.width = LayoutParams.FILL_PARENT;
		Params.height = LayoutParams.FILL_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) Params);
	}
	
	public EasyAlertDialog(Context context, int style){
		this(context,-1,style);
		resourceId = R.layout.easyalert_dialog_default_layout;
	}

	public EasyAlertDialog(Context context){
		
		this(context,R.style.easy_dialog_style);
		resourceId = R.layout.easyalert_dialog_default_layout;
	}
	
	public void setTitle(String title){
		if(null !=title){
			this.title = title;
			if(null !=titleTV)
				titleTV.setText(title);
		}
		
	}
	
	public void setTitleTextColor(int color){
		titleTextColor = color;
		if(null !=titleTV && NO_TEXT_COLOR!=color)
			titleTV.setTextColor(color);
	}
	public void setMessageTextColor(int color){
		msgTextColor = color;
		if(null !=messageTV && NO_TEXT_COLOR!=color)
			messageTV.setTextColor(color);
		
	}
	public void setMessageTextSize(float size){
		msgTextSize = size;
		if(null !=messageTV && NO_TEXT_SIZE!=size)
			messageTV.setTextSize(size);
	}
	public void setTitleTextSize(float size){
		titleTextSize = size;
		if(null !=titleTV && NO_TEXT_SIZE!=size)
			titleTV.setTextSize(size);
	}
	
	public void setMessage(String message){
		if(null != message){
			this.message = message;
			if(null !=messageTV )
				messageTV.setText(message);
		}
	}
	public void addPositiveButton(String title,int color,float size,View.OnClickListener positiveBtnListener){
		isPositiveBtnOn = true;
		positiveBtnTitle = title;
		positiveBtnTitleTextColor = color;
		positiveBtnTitleTextSize = size;
		this.positiveBtnListener = positiveBtnListener;
	}

	public void addNegativeButton(String title,int color,float size,View.OnClickListener negativeBtnListener){
		isNegativeBtnOn = true;
		negativeBtnTitle = title;
		negativeBtnTitleTextColor = color;
		negativeBtnTitleTextSize = size;
		this.negativeBtnListener = negativeBtnListener;
	}
	
	public void addPositiveButton(String title, View.OnClickListener positiveBtnListener){
		isPositiveBtnOn = true;
		positiveBtnTitle = title;
		positiveBtnTitleTextColor = NO_TEXT_COLOR;
		positiveBtnTitleTextSize = NO_TEXT_SIZE;
		this.positiveBtnListener = positiveBtnListener;
	}

	public void addNegativeButton(String title, View.OnClickListener negativeBtnListener){
		isNegativeBtnOn = true;
		negativeBtnTitle = title;
		negativeBtnTitleTextColor = NO_TEXT_COLOR;
		negativeBtnTitleTextSize = NO_TEXT_SIZE;
		this.negativeBtnListener = negativeBtnListener;
	}
	
	public static EasyAlertDialog show(Context context, String title, String message,
			String posBtn, String negBtn, View.OnClickListener clickListener) {
		EasyAlertDialog alert = new EasyAlertDialog(context);
		alert.addPositiveButton(posBtn, clickListener);
		alert.addNegativeButton(negBtn, clickListener);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
		return alert;
	}
	public static EasyAlertDialog show(Context context,int resourceId, String title, String message,
			String posBtn, String negBtn, View.OnClickListener clickListener) {
		EasyAlertDialog alert = new EasyAlertDialog(context);
		if(!Util.isEmpty(title)){
			alert.setResourceId(resourceId);
		}
		alert.addPositiveButton(posBtn, clickListener);
		alert.addNegativeButton(negBtn, clickListener);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
		return alert;
	}
	public static EasyAlertDialog show(Context context,int resourceId, String title, String message,
			String posBtn, View.OnClickListener clickListener) {
		EasyAlertDialog alert = new EasyAlertDialog(context);
		if(!Util.isEmpty(title)){
			alert.setResourceId(resourceId);
		}
		alert.addPositiveButton(posBtn, clickListener);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
		return alert;
	}
	public static EasyAlertDialog show(Context context, String title, String message,
			String posBtn, View.OnClickListener clickListener) {
		EasyAlertDialog alert = new EasyAlertDialog(context);
		alert.addPositiveButton(posBtn, clickListener);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
		return alert;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(resourceId);
		try{
			titleTV = (TextView) findViewById(R.id.EasyDialogTitleTV);
			titleTV.setText(title);
			if(NO_TEXT_COLOR!=titleTextColor)
				titleTV.setTextColor(titleTextColor);
			if(NO_TEXT_SIZE!=titleTextSize)
				titleTV.setTextSize(titleTextSize);
			messageTV = (TextView)findViewById(R.id.EasyDialogMessageTV);
			messageTV.setText(message);
			if(NO_TEXT_COLOR!=msgTextColor)
				messageTV.setTextColor(msgTextColor);
			if(NO_TEXT_SIZE!=msgTextSize)
				messageTV.setTextSize(msgTextSize);
			
			positiveBtn = (Button) findViewById(R.id.EasyDialogPositiveBtn);
			if(isPositiveBtnOn){
				positiveBtn.setVisibility(View.VISIBLE);
				if(NO_TEXT_COLOR!=positiveBtnTitleTextColor){
					positiveBtn.setTextColor(positiveBtnTitleTextColor);
				}
				if(NO_TEXT_SIZE!=positiveBtnTitleTextSize){
					positiveBtn.setTextSize(positiveBtnTitleTextSize);
				}
				positiveBtn.setText(positiveBtnTitle);
				positiveBtn.setOnClickListener(positiveBtnListener);
			}
			negativeBtn = (Button) findViewById(R.id.EasyDialogNegativeBtn);
			if(isNegativeBtnOn){
				negativeBtn.setVisibility(View.VISIBLE);
				if(NO_TEXT_COLOR!=this.negativeBtnTitleTextColor){
					negativeBtn.setTextColor(negativeBtnTitleTextColor);
				}
				if(NO_TEXT_SIZE!=this.negativeBtnTitleTextSize){
					negativeBtn.setTextSize(negativeBtnTitleTextSize);
				}
				negativeBtn.setText(negativeBtnTitle);
				negativeBtn.setOnClickListener(negativeBtnListener);
			}
		}catch(Exception e){
			
		}
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public Button getPositiveBtn() {
		return positiveBtn;
	}

	public Button getNegativeBtn() {
		return negativeBtn;
	}
}
