package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.fragment.RepairControlNaviFragment;
import com.jingye.coffeemac.module.backgroundlogin.BackgroundLoginContract;
import com.jingye.coffeemac.module.backgroundlogin.BackgroundLoginPresenter;
import com.jingye.coffeemac.module.managermodule.ManagerControlActivity;
import com.jingye.coffeemac.module.repairmodule.RepairControlActivity;
import com.jingye.coffeemac.net.http.MD5;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.ClearableEditText;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.NetworkUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;
import com.jingye.coffeemac.util.test.EspressoIdlingResource;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static android.view.View.GONE;


public class BackgroundLoginActivity extends TActivity implements View.OnClickListener, TextWatcher, BackgroundLoginContract.IBackgroundLoginView {

    //维修师id
    public static final int USER_REPAIRER = 4;
    //加料员id
    public static final int USER_MANAGER = 7;
    //品质测试师id
    public static final int USER_QUALITY = 5;
    //运营主管id
    public static final int USER_DIRECTOR_OF_OPERATIONS = 3;
    //运营经理id
    public static final int USER_OPERATIONS_MANAGER = 2;
    private static final int BG_UI_MSG_WHAT_DISABLE_TIMER = 0x000011;
    private Context mContext;
    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private ImageView mBgLoginBack;
    private ClearableEditText mBgLoginTxtAccount;
    private ClearableEditText mBgLoginTxtPassword;
    private TextView mBgLoginTxtErrhint;
    private Button mEntranceBgLogin;
    private boolean foreground;

    private Timer disableTimer;
    private Button mHomeNetworkSetting;
    private SafeHandler timerHandler = new SafeHandler(this);
    private BackgroundLoginContract.IBackgroundLoginPresenter mPresenter;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, BackgroundLoginActivity.class);
        activity.startActivity(intent);
    }

    private void onLoginTimeout() {
        ProgressDlgHelper.closeProgress();
        stopLoginTimer();
        setErrText("登录超时");
    }

    @Override
    public void stopLoginTimer() {
        if (disableTimer != null) {
            disableTimer.cancel();
            disableTimer = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_login);

        mContext = this;

        initView();
        initStatus();
        mPresenter=new BackgroundLoginPresenter(this);
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void initView() {
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);
        mHomeNetworkSetting = (Button) findViewById(R.id.home_title_network_setting);

        mBgLoginBack = (ImageView) findViewById(R.id.bglogin_back);
        mBgLoginBack.setOnClickListener(this);

        mBgLoginTxtAccount = (ClearableEditText) findViewById(R.id.bglogin_account);
        mBgLoginTxtAccount.addTextChangedListener(this);

        mBgLoginTxtPassword = (ClearableEditText) findViewById(R.id.bglogin_password);
        mBgLoginTxtErrhint = (TextView) findViewById(R.id.bglogin_errhint);

        mEntranceBgLogin = (Button) findViewById(R.id.entrance_bglogin);
        mEntranceBgLogin.setClickable(true);
        mEntranceBgLogin.setOnClickListener(this);

        showKeyboard(mBgLoginTxtAccount);

        mHomeNetworkSetting.setVisibility(View.VISIBLE);
        mHomeNetworkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.navigatorToActionSetting();
            }
        });

    }

    private void showKeyboard(EditText editText) {
        if (!foreground) {
            return;
        }

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }


    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bglogin_back:
                this.finish();
                break;
            case R.id.entrance_bglogin:
                mPresenter.doLogin(mBgLoginTxtAccount.getText().toString(),mBgLoginTxtPassword.getText().toString());

                break;
        }
    }



    private void updateStatus(int status) {

        if (status == ITranCode.STATUS_NO_NETWORK) {
            mHomeNetworkStatus.setVisibility(View.GONE);

        } else if (status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN) {
            mHomeNetworkStatus.setVisibility(View.VISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_broken_normal);
        } else if (status == ITranCode.STATUS_LOGGING) {
            mHomeNetworkStatus.setVisibility(View.VISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connecting_normal);
        } else {
            mHomeNetworkStatus.setVisibility(View.VISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connected_normal);
        }
    }

    @Override
    public void startLoginDisableTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = BG_UI_MSG_WHAT_DISABLE_TIMER;
                timerHandler.sendMessage(message);
            }
        };
        disableTimer = new Timer();
        disableTimer.schedule(timerTask, 30000);

    }

    @Override
    public void navigateToRepair(Admin admin) {
        if(admin==null){
            setErrText("发生未知错误，请重试!");
            return;
        }else{
            if (admin.getRole() == USER_MANAGER) {
                RepairControlActivity.start(BackgroundLoginActivity.this, admin);
                BackgroundLoginActivity.this.finish();
            } else if (admin.getRole() == USER_REPAIRER) {
                RepairControlActivity.start(BackgroundLoginActivity.this, admin);
                BackgroundLoginActivity.this.finish();
            } else if (admin.getRole() == USER_QUALITY) {
                RepairControlActivity.start(BackgroundLoginActivity.this, admin);
                BackgroundLoginActivity.this.finish();
            } else if (admin.getRole() == USER_DIRECTOR_OF_OPERATIONS) {
                RepairControlActivity.start(BackgroundLoginActivity.this, admin);
                BackgroundLoginActivity.this.finish();
            } else if (admin.getRole() == USER_OPERATIONS_MANAGER) {
                RepairControlActivity.start(BackgroundLoginActivity.this, admin);
                BackgroundLoginActivity.this.finish();
            } else {
                setErrText("该账号没有进入后台的权限");
            }
        }
    }

    @Override
    public void closeProgress() {
        ProgressDlgHelper.closeProgress();
    }


    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEntranceBgLogin.getWindowToken(), 0);
    }

    @Override
    public boolean isNetWorkConnected() {
        return NetworkUtil.isNetworkConnected(mContext);
    }

    @Override
    public void setErrText(int strId) {
        mBgLoginTxtErrhint.setText(getString(strId));
        mBgLoginTxtErrhint.setVisibility(View.VISIBLE);
    }

    @Override
    public void showKeyboardAccount() {
        showKeyboard(mBgLoginTxtAccount);
    }

    @Override
    public void showKeyboardPassword() {
        showKeyboard(mBgLoginTxtPassword);
    }

    @Override
    public void setErrText(String errText) {
        mBgLoginTxtErrhint.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(errText)) {
            mBgLoginTxtErrhint.setText(errText);
        }else{
            mBgLoginTxtErrhint.setText(getString(R.string.str_backlogin_no_response));
        }
    }

    @Override
    public void showProgress(String msg) {
        ProgressDlgHelper.showProgress(mContext, msg);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 0) {
            return;
        }

    }

    @Override
    public void navigateToSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource(){
        return EspressoIdlingResource.getIdlingResource();
    }

    static class SafeHandler extends Handler {
        WeakReference<BackgroundLoginActivity> theActivity;

        public SafeHandler(BackgroundLoginActivity activity) {
            this.theActivity = new WeakReference<BackgroundLoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BG_UI_MSG_WHAT_DISABLE_TIMER:
                    BackgroundLoginActivity activity = theActivity.get();
                    if (activity != null) {
                        activity.onLoginTimeout();
                    }
                    break;
            }
        }
    }


}
