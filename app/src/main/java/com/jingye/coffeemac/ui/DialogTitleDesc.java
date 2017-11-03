package com.jingye.coffeemac.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jingye.coffeemac.R;

import org.json.JSONException;


/**
 * Created by Hades on 2016/10/31.
 */

public class DialogTitleDesc extends BaseDialog implements View.OnClickListener {

    private static final String TITLE = "title";
    private static final String DESC = "desc";
    private static final String CANCELSTR = "cancelstr";
    private static final String OKSTR = "okStr";
    private static final String OKENABLE = "okEnable";
    private Button btnCancel;
    private Button btnOk;
    private IDialogTitleDesc mListener;

    public static DialogTitleDesc newInstance(String title,String desc,String cancelStr,String okStr,boolean OkEnable) {
        DialogTitleDesc dialogTitle=new DialogTitleDesc();
        Bundle args=new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putString(CANCELSTR, cancelStr);
        args.putString(OKSTR, okStr);
        args.putBoolean(OKENABLE,OkEnable);
        dialogTitle.setArguments(args);
        return dialogTitle;
    }

    public void setListener(IDialogTitleDesc iDialogTitleDesc){
        this.mListener=iDialogTitleDesc;
    }

    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_title_desc, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(getArguments().getString(TITLE,"假装有信息提示"));

        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        tvDesc.setText(getArguments().getString(DESC,"假装有描述"));

        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnCancel.setText(getArguments().getString(CANCELSTR,"取消"));
        btnOk=(Button)view.findViewById(R.id.btnOk);
        if(getArguments().containsKey(OKENABLE)){
            btnOk.setEnabled(getArguments().getBoolean(OKENABLE,true));
        }
        btnOk.setText(getArguments().getString(OKSTR,"确定"));

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected int setDialogWidth() {
        return 600;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnCancel:
                break;
            case R.id.btnOk:
                if(mListener!=null){
                    try {
                        mListener.onOkClick();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        if(isVisible()){
            dismiss();
        }

    }

    public interface IDialogTitleDesc{
        void onCancelClick();
        void onOkClick() throws JSONException;
    }
}
