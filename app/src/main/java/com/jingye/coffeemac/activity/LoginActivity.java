package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.common.action.TViewWatcher;
import com.jingye.coffeemac.module.heatmodule.HeatActivity;
import com.jingye.coffeemac.net.http.MD5;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.LoginRequestInfo;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.LoginResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.ClearableEditText;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends TActivity implements OnClickListener, TextWatcher {

    public final static int UI_MSG_WHAT_DISABLE_TIMER = 1;
    private Context mContext;
    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private ClearableEditText mLoginTxtAccount;
    private ClearableEditText mLoginTxtPassword;
    private Button mEntranceLogin;
    private String mUserAccount;
    private String mUserPassword;
    private boolean foreground;
    private Timer disableTimer;
    private TextView mLoginTxtErrhint;
    private SafeHandler timerHandler = new SafeHandler(this);

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mContext = this;
        proceedExtras();

        setupLoginPage();
    }

    private void proceedExtras() {
    }

    private void setupLoginPage() {

        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        mLoginTxtAccount = (ClearableEditText) findViewById(R.id.login_account);
        mLoginTxtAccount.addTextChangedListener(this);
        mLoginTxtPassword = (ClearableEditText) findViewById(R.id.login_password);
        mLoginTxtErrhint = (TextView) findViewById(R.id.login_errhint);

        mEntranceLogin = (Button) findViewById(R.id.entrance_login);
        mEntranceLogin.setClickable(true);
        mEntranceLogin.setOnClickListener(this);

        showKeyboard(mLoginTxtAccount);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entrance_login:
                if (!isReachable()) {
//				ToastUtil.showToast(LoginActivity.this, R.string.network_is_not_available);
                    setErrText("网络连接失败，请检查你的网络设置");
                    return;
                }
                hideKeyboard();
                doLogin();
                break;
            default:
                break;
        }
    }

    private void setErrText(String errText) {
        mLoginTxtErrhint.setVisibility(View.VISIBLE);
        mLoginTxtErrhint.setText(errText);
    }

    private boolean isReachable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEntranceLogin.getWindowToken(), 0);
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

    private void sendToService(Remote remote) {
        TViewWatcher.newInstance().send(remote);
    }

    private void doLogin() {
        mUserAccount = mLoginTxtAccount.getText().toString();
        mUserPassword = mLoginTxtPassword.getText().toString();

        if (!isValidStr(mUserAccount)) {
//			ToastUtil.showToast(mContext, R.string.login_account_is_null);
            setErrText(getString(R.string.login_account_is_null));
            showKeyboard(mLoginTxtAccount);
            return;
        }

        if (!isValidStr(mUserPassword)) {
//			ToastUtil.showToast(mContext, R.string.login_password_is_null);
            setErrText(getString(R.string.login_password_is_null));
            showKeyboard(mLoginTxtPassword);
            return;
        }

        LoginRequestInfo info = new LoginRequestInfo();
        info.setUid(mUserAccount);
        info.setPassword(MD5.md5(mUserPassword));
        sendToService(info.toRemote());

        ProgressDlgHelper.showProgress(mContext, "正在登录");
        startLoginDisableTimer();
    }

    private boolean isValidStr(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }

        return dest.length() != 0;
    }

    private void startLoginDisableTimer() {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UI_MSG_WHAT_DISABLE_TIMER;
                timerHandler.sendMessage(message);
            }
        };

        disableTimer = new Timer();
        disableTimer.schedule(timerTask, 30000);
    }

    private void onLoginTimeout() {
        ProgressDlgHelper.closeProgress();
        stopLoginTimer();
//		ToastUtil.showToast(mContext, "登录超时");
        setErrText("登录超时");
    }

    private void stopLoginTimer() {
        if (disableTimer != null) {
            disableTimer.cancel();
            disableTimer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        foreground = false;
    }

    @Override
    public void onReceive(Remote remote) {
        int what = remote.getWhat();
        if (what == ITranCode.ACT_USER) {
            onReceiveUserActions(remote);
        } else if (what == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
    }

    private void onReceiveUserActions(Remote remote) {

        int action = remote.getAction();
        if (action == ITranCode.ACT_USER_LOGIN) {
            ProgressDlgHelper.closeProgress();
            stopLoginTimer();

            LoginResult result = GeneralActionResult.parseObject(remote.getBody());
            if (result.getResCode() == ResponseCode.RES_SUCCESS) {
                ToastUtil.showToast(mContext, "登录成功");
                U.saveAppSet(U.KEY_USER_IS_LOGINED, "true", U.getMyVendorNum());

                boolean isInit = SharePrefConfig.getInstance().isDosingInit(U.getMyVendorNum());
                if (isInit) {
                      HeatActivity.start(LoginActivity.this);
//                    Intent intent = new Intent(mContext, WelcomeActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
                    this.finish();
                } else {
                    Intent intent = new Intent(mContext, NewMaterialConfigActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    this.finish();
                }
            } else if (result.getResCode() == ResponseCode.RES_EUIDPASS || result.getResCode() == ResponseCode.RES_ENONEXIST) {
//				ToastUtil.showToast(mContext, "用户名或密码错误:" + result.getResCode());
                setErrText("用户名或密码错误:" + result.getResCode());
            } else if (result.getResCode() == ResponseCode.RES_ALREADY_LOGINED) {
//				ToastUtil.showToast(mContext, "咖啡机ID已经登录，请勿重复登录");
                setErrText("咖啡机ID已经登录，请勿重复登录");
            } else {
//				ToastUtil.showToast(mContext, "登录失败：" +result.getResCode());
                setErrText("登录失败：" + result.getResCode());
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            return;
        }
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void updateStatus(int status) {

        mHomeNetworkStatus.setVisibility(View.VISIBLE);
        if (status == ITranCode.STATUS_NO_NETWORK
                || status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN) {
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_broken_normal);
        } else if (status == ITranCode.STATUS_LOGGING) {
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connecting_normal);
        } else {

            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connected_normal);
        }
    }

    static class SafeHandler extends Handler {
        WeakReference<LoginActivity> theActivity;

        public SafeHandler(LoginActivity activity) {
            this.theActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UI_MSG_WHAT_DISABLE_TIMER:
                    LoginActivity activity = theActivity.get();
                    if (activity != null) {
                        activity.onLoginTimeout();
                    }
                    break;
            }
        }
    }
}
