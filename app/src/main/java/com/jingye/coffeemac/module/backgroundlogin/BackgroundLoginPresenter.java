package com.jingye.coffeemac.module.backgroundlogin;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.net.http.MD5;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;
import com.jingye.coffeemac.util.test.EspressoIdlingResource;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;



/**
 * Created by Hades on 2017/3/30.
 */

public class BackgroundLoginPresenter implements BackgroundLoginContract.IBackgroundLoginPresenter {
    private final BackgroundLoginContract.IBackgroundLoginView mBackgroundView;

    public BackgroundLoginPresenter(BackgroundLoginContract.IBackgroundLoginView iBackgroundLoginView) {
        this.mBackgroundView=iBackgroundLoginView;
    }

    @Override
    public void navigatorToActionSetting() {
        mBackgroundView.navigateToSetting();
    }

    @Override
    public void doLogin(String mUserAccount,String mUserPassword) {

        if(!mBackgroundView.isNetWorkConnected()){
            mBackgroundView.setErrText(R.string.network_is_not_available);

            return;
        }
        mBackgroundView.hideKeyboard();

        if (!isValidStr(mUserAccount)) {
            mBackgroundView.setErrText(R.string.login_username_is_null);
            mBackgroundView.showKeyboardAccount();
            return;
        }

        if (!isValidStr(mUserPassword)) {
            mBackgroundView.setErrText(R.string.login_password_is_null);
            mBackgroundView.showKeyboardPassword();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        String host = "";
        String line = "";
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
            line = "jijia_online";
        }else if(AppConfig.BUILD_SERVER==AppConfig.Build.TEST){
            host = StringUtil.HOST_TEST;
            line = "jijia_testline";
        }else {
            host = StringUtil.HOST_LOCAL;
            line = "jijia_testline";
        }
        String url = host + "admins/makerLogin";
        RequestParams params = new RequestParams();
        params.add("login", mUserAccount);
        params.add("password", MD5.md5(mUserPassword));
        params.add("line", line);
        params.add("machineId", U.getMyVendorNum());

        EspressoIdlingResource.increment();
        LogUtil.vendor(U.getMyVendorNum());
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                EspressoIdlingResource.decrement();
                LogUtil.vendor(response.toString());
                mBackgroundView.stopLoginTimer();
                ProgressDlgHelper.closeProgress();
                try {

                    if (response.has("statusCode")) {
                        if (response.getInt("statusCode") != ResponseCode.RES_SUCCESS) {
                            if(response.has("statusMsg")){
                                mBackgroundView.setErrText(response.getString("statusMsg"));
                            }else{
                                mBackgroundView.setErrText(R.string.str_backlogin_no_response);
                            }
                        } else {

                            Admin admin;
                            //intent to machinecontrol
                            if (response.has("results") && response.getJSONObject("results").has("admin")) {
                                admin = JSON.parseObject(response.getJSONObject("results").getString("admin"), Admin.class);

                                mBackgroundView.navigateToRepair(admin);
                            }
                        }
                    } else {
                        mBackgroundView.setErrText("错误次数太多");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                EspressoIdlingResource.decrement();
                mBackgroundView.closeProgress();
                mBackgroundView.stopLoginTimer();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

        mBackgroundView.showProgress("正在登录");

        mBackgroundView.startLoginDisableTimer();

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
}
