package com.jingye.coffeemac.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jingye.coffeemac.R;

public class ToastUtil {

    private static Toast mToast;
    private static View view;
    private static TextView text;

    public static void showToast(Context context, String msg) {
        if (mToast == null) {
            mToast = new Toast(context);
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.toast, null);
                text = (TextView) view.findViewById(R.id.toast);
            }
            text.setText(msg);
            mToast.setView(view);
        } else {
            text.setText(msg);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    static public void showToast(Context context, int resId){
        String msg = context.getResources().getString(resId);
        showToast(context, msg);
    }
}
