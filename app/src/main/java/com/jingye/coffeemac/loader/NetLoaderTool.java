package com.jingye.coffeemac.loader;

import android.text.TextUtils;
import android.util.Log;

import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.AppUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Hades on 2017/2/9.
 */

public class NetLoaderTool {

    private static AsyncHttpClient client=new AsyncHttpClient();

    public static void post(String url, Map<String, Object> param, final INetLoaderListener listener) {
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        RequestParams params = new RequestParams();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null&&!TextUtils.isEmpty(new String(responseBody))) {
                    listener.onSuccess(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null&&!TextUtils.isEmpty(error.toString())) {
                    listener.onFailure(error.toString());
                }

            }
        });
    }

    public static void get(String url,Map<String,Object> param,final INetLoaderListener listener){
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params=new RequestParams();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null&&!TextUtils.isEmpty(new String(responseBody))) {
                    listener.onSuccess(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null&&!TextUtils.isEmpty(error.toString())) {
                    listener.onFailure(error.toString());
                }

            }
        });

    }

    public interface INetLoaderListener {
        void onSuccess(String success);

        void onFailure(String error);
    }
}
