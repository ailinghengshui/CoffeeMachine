package com.jingye.coffeemac.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.ToastUtil;


/**
 * Created by Hades on 2016/10/31.
 */

public class DialogTitleInput extends BaseDialog implements View.OnClickListener {


    private static final String HINT = "hint";
    private static final String TITLE = "title";
    private static final String DESC = "desc";
    private EditText etInput;
    private Button btnCancel;
    private Button btnOk;

    private IDialogTitleInputListener mListener;

    public static DialogTitleInput newInstance(String title, String desc,String hint) {
        DialogTitleInput dialogTitle=new DialogTitleInput();
        Bundle args=new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putString(HINT,hint);
        dialogTitle.setArguments(args);
        return dialogTitle;
    }

    public void setListener(IDialogTitleInputListener listener){
        this.mListener=listener;
    }

    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_title_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(getArguments().getString(TITLE,"假装有信息提示"));

        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        tvDesc.setText(getArguments().getString(DESC,"假装有描述"));

        etInput=(EditText)view.findViewById(R.id.etInput);

        etInput.setHint(getArguments().getString(HINT,"假装有提示"));

        etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

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

        switch (view.getId()){
            case R.id.btnCancel:
                if(mListener!=null){
                    mListener.onDialogTitleInputCancelClick();
                }
                break;
            case R.id.btnOk:
                if(mListener!=null){
                    if(TextUtils.isEmpty(etInput.getText().toString().trim())){
                        ToastUtil.showToast(getActivity(),"密码不能为空");
                        return;
                    }
                    mListener.onDialogTitleInputOkClick(etInput.getText().toString().trim());
                }
                break;
        }
        if(isVisible()){
            dismiss();
        }

    }

    public interface IDialogTitleInputListener{
        void  onDialogTitleInputCancelClick();
        void  onDialogTitleInputOkClick(String password);
    }
}
