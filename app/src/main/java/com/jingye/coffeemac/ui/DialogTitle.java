package com.jingye.coffeemac.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jingye.coffeemac.R;


/**
 * Created by Hades on 2016/10/31.
 */

public class DialogTitle extends BaseDialog implements View.OnClickListener {

    private IDialogTitleClick mListener;

    public interface IDialogTitleClick{
        void onDialogTitleCancelClick();
        void onDialogTitleOkClick();

    }
    private static final String TITLE = "title";
    private Button btnCancel;
    private Button btnOk;

    public static DialogTitle newInstance(String title) {
        DialogTitle dialogTitle=new DialogTitle();
        Bundle args=new Bundle();
        args.putString(TITLE, title);
        dialogTitle.setArguments(args);
        return dialogTitle;
    }

    public void setListener(IDialogTitleClick iDialogTitleClick){
        this.mListener=iDialogTitleClick;
    }

    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_title, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(getArguments().getString(TITLE,"假装有信息提示"));

        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnOk=(Button)view.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected int setDialogWidth() {
        return 600;
    }

    @Override
    public void onClick(View view) {
        if(isVisible()){
            switch (view.getId()){
                case R.id.btnCancel:
                    if(mListener!=null){
                        mListener.onDialogTitleCancelClick();
                    }
                    break;
                case R.id.btnOk:
                    if(mListener!=null) {
                        mListener.onDialogTitleOkClick();
                    }
                    break;
                default:
                    break;
            }
            dismiss();
        }

    }
}
