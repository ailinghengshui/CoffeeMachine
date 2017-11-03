package com.jingye.coffeemac.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.google.zxing.common.StringUtils;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.BackgroundLoginActivity;
import com.jingye.coffeemac.activity.LoginActivity;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.helper.cache.BaseDataCacher;
import com.jingye.coffeemac.loader.NetLoaderTool;
import com.jingye.coffeemac.module.heatmodule.HeatActivity;
import com.jingye.coffeemac.module.repairmodule.RepairControlActivity;
import com.jingye.coffeemac.net.http.MD5;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.LogoutInfo;
import com.jingye.coffeemac.service.bean.action.VerifyPasswordInfo;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.bean.result.VerifyPasswordResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.ControlVeriPwdDialog;
import com.jingye.coffeemac.ui.DialogTitle;
import com.jingye.coffeemac.ui.DialogTitleDesc;
import com.jingye.coffeemac.ui.DialogTitleInput;
import com.jingye.coffeemac.ui.DialogTitleProgress;
import com.jingye.coffeemac.ui.GenericSettingDialog;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.AppUtil;
import com.jingye.coffeemac.util.NetworkUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.jingye.coffeemac.fragment.ControlOtherFragment.WASHTIME_PREFERENCES_SET;

/**This class implements Setting
 * Created by Hades on 2016/10/31.
 */
public class RepairControlSettingFragment extends TFragment implements View.OnClickListener {

    private static final String TAG = "RepairControlSettingFragment->";
    final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/vendor/apk/" + "Coffee-Mac.apk";
    private Button btnControlSettingBackstage;
    private Button btnSettingCoffeeExit;
    private Button btnSettingLogout;
    private Button rlControlSettingDriveUpdate;
    private Button rlControlSettingMainControlUpdate;
    private Button rlControlSettingUpdate;
    private Button btnSetting;
    //    private ControlVeriPwdDialog.OnVerifyPwdDialogListener mListener = new ControlVeriPwdDialog.OnVerifyPwdDialogListener() {
//        @Override
//        public void verifyPwdCancel() {
//
//        }
//
//        @Override
//        public void verifyPwdConfirm(String password, int type) {
//            if (type == VerifyPasswordInfo.TYPE_MOVE_BACKGROUND) {
//                VerifyPasswordInfo info = new VerifyPasswordInfo();
//                info.setPassword(password);
//                info.setType(type);
//                execute(info.toRemote());
//            } else {
//                ProgressDlgHelper.showProgress(getActivity(), "正在退出");
//
//                LogoutInfo info = new LogoutInfo();
//                info.setUid(U.getMyVendorNum());
//                info.setPassword(password);
//                execute(info.toRemote());
//            }
//        }
//    };
    private int mRecordId;
    private DialogTitleProgress dialogTitleProgress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialogTitleProgress.setProgress(msg.what);
        }
    };

    public RepairControlSettingFragment() {
        this.setFragmentId(R.id.repair_setting_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        btnControlSettingBackstage = (Button) view.findViewById(R.id.btnControlSettingBackstage);
        rlControlSettingDriveUpdate = (Button) view.findViewById(R.id.btnControlSettingDriveUpdate);
        rlControlSettingMainControlUpdate = (Button) view.findViewById(R.id.btnControlSettingMainControlUpdate);
        rlControlSettingUpdate = (Button) view.findViewById(R.id.btnControlSettingUpdate);
        btnSettingCoffeeExit = (Button) view.findViewById(R.id.btnSettingCoffeeExit);
        btnSettingLogout = (Button) view.findViewById(R.id.btnSettingLogout);

        btnControlSettingBackstage.setOnClickListener(this);
        btnSettingCoffeeExit.setOnClickListener(this);
        btnSettingLogout.setOnClickListener(this);
        rlControlSettingDriveUpdate.setOnClickListener(this);
        rlControlSettingMainControlUpdate.setOnClickListener(this);
        rlControlSettingUpdate.setOnClickListener(this);

        btnSetting = (Button) view.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_USER) {
            if (remote.getAction() == ITranCode.ACT_USER_VERIFY_PWD) {
                LogUtil.vendor("onReceive -> ACT_USER_VERIFY_PWD");
                VerifyPasswordResult result = Ancestor.parseObject(remote.getBody());
                if (result != null && result.isCorrect()) {
                    if (result.getType() == VerifyPasswordInfo.TYPE_MOVE_BACKGROUND) {
                        getActivity().moveTaskToBack(true);
                    }
                } else {
                    ToastUtil.showToast(getActivity(), "密码验证失败");
                }
            } else if (remote.getAction() == ITranCode.ACT_USER_LOGOUT) {
                LogUtil.vendor("onReceive -> ACT_USER_LOGOUT");
                ProgressDlgHelper.closeProgress();
                LogoutResult result = Ancestor.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    U.saveAppSet(U.KEY_USER_IS_LOGINED, "false", U.getMyVendorNum());
                    // clear cache
                    GlobalCached.clear();
                    BaseDataCacher.clearCache();
                    // back to login page
                    LoginActivity.start(getActivity());
                    getActivity().finish();
                } else {
                    ToastUtil.showToast(getActivity(), "密码验证失败");
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnControlSettingBackstage:
                // enter backstage without logout coffee machine
                moveBackground();

//                enterBackstage();
                break;
            case R.id.btnControlSettingDriveUpdate:
                //  update drive
                showHintDialog();
                break;
            case R.id.btnControlSettingMainControlUpdate:
                //  update main control board
                showHintDialog();
                break;
            case R.id.btnControlSettingUpdate:
                // update online

                if (!NetworkUtil.isNetworkConnected(getContext())) {
                    ToastUtil.showToast(getContext(), "网络连接失败，请检查你的网络设置");
                    return;
                } else {
                    checkUpdateVerison();
                }
                break;
            case R.id.btnSettingCoffeeExit:
                // enter backstage with logout coffee machine
                loginExit();
//                enterBackstageAndLogout();
                break;
            case R.id.btnSettingLogout:
                //  user logout
                if (!getActivity().isFinishing()) {
                    loginOut();
                    HeatActivity.start(getActivity());
                    getActivity().finish();
                }
                break;
            case R.id.btnSetting:
                Map<String, String> map = new HashMap<String, String>();
                StringBuilder sb = new StringBuilder();
                for (String time : MyApplication.Instance().mGlobalWashTimeSet) {
                    sb.append(",");
                    sb.append(time);
                }
                if (sb.length() > 0 && sb.charAt(0) == ',') {
                    sb.deleteCharAt(0);
                }
                map.put(WASHTIME_PREFERENCES_SET, sb.toString());

                GenericSettingDialog dialog = new GenericSettingDialog(
                        getActivity(), map,
                        new GenericSettingDialog.OnGenericSettingDialog() {

                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public boolean onConfirm(Map<String, String> resultMap) {
                                try {
                                    String tmpTimes = resultMap.get(WASHTIME_PREFERENCES_SET);
                                    String[] times = tmpTimes.split(",");
                                    for (String time : times) {
                                        if (!time.matches("[0-9]{2}:[0-9]{2}")) {
                                            throw new Exception();
                                        }
                                    }
                                    MyApplication.Instance().mGlobalWashTimeSet.clear();
                                    for (String time : times) {
                                        MyApplication.Instance().mGlobalWashTimeSet.add(time);
                                    }
                                    SharePrefConfig.getInstance().setWashTime(MyApplication.Instance().mGlobalWashTimeSet);

                                    ToastUtil.showToast(getActivity(), MyApplication.Instance().mGlobalWashTimeSet.toString());
                                    return true;
                                } catch (Exception e) {
                                    ToastUtil.showToast(getActivity(), "输入格式错误");
                                }
                                return false;
                            }
                        });
                dialog.show();
                break;
        }
    }

    private void checkUpdateVerison() {

        deleteOldApp();
        String host = "";
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        }else if(AppConfig.BUILD_SERVER==AppConfig.Build.TEST){
            host = StringUtil.HOST_TEST;
        }else {
            host = StringUtil.HOST_LOCAL;
        }

        com.alibaba.fastjson.JSONObject tokenJson = new com.alibaba.fastjson.JSONObject();
        tokenJson.put("sign", "jinyekeji2016");
        tokenJson.put("time", TimeUtil.getNow_millisecond());
        String source = tokenJson.toString();
        String token = null;
        try {
            token = AESUtil.encrypt("442bef40be3e8188", source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = host + "admins/apkUpgrade";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("versionCode", AppUtil.getAppVersionCode(getContext()));
        param.put("versionName", "Coffee-Mac-" + BuildConfig.VERSION_NAME);
        param.put("token", token);

        ProgressDlgHelper.showProgress(getContext(), "获取版本信息");
        NetLoaderTool.get(url, param, new NetLoaderTool.INetLoaderListener() {
            @Override
            public void onSuccess(String success) {
                ProgressDlgHelper.closeProgress();
                LogUtil.vendor(TAG + success);
                handleSuccessResult(success);
            }

            @Override
            public void onFailure(String error) {
                LogUtil.vendor(TAG + error);
                showErrorMessage(R.string.err_get_version);
                ProgressDlgHelper.closeProgress();
            }
        });
    }

    private void deleteOldApp() {
        File file=new File(filePath);
        if(file!=null&&file.exists()&&file.isFile()){
            file.delete();
        }

        LogUtil.vendor(TAG+"exists"+file.exists());
    }

    private void handleSuccessResult(String success) {
        if (!TextUtils.isEmpty(success)) {
            try {
                final JSONObject result = new JSONObject(success);
                if (result.has("statusCode")) {
                    int statusCode = result.getInt("statusCode");
                    switch (statusCode) {
                        case 200:
                            showUpdateDialog(result.getJSONObject("results"));
                            break;
                        case 111:
                            showNoUpdateDialog(result.getString("statusMsg"));
                            break;
                        case 112:
                            showNoUpdateDialog(result.getString("statusMsg"));
                            break;
                        default:
                            showErrorMessage(R.string.err_get_version);
                            break;
                    }
                } else {
                    showErrorMessage(R.string.err_get_version);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage(R.string.err_get_version);
        }
    }

    private void showUpdateDialog(final JSONObject results) throws JSONException {
        if (results.has("url") && results.has("desc")) {
            DialogTitleDesc updateDialog;
            if (!TextUtils.isEmpty(results.getString("desc"))) {
                updateDialog = DialogTitleDesc.newInstance("当前版本" + AppUtil.getAppVersionName(getContext()), results.getString("desc"), "取消", "升级到新版本", true);
            } else {
                updateDialog = DialogTitleDesc.newInstance("当前版本" + AppUtil.getAppVersionName(getContext()), "新版本", "取消", "升级到新版本", true);
            }
            updateDialog.setListener(new DialogTitleDesc.IDialogTitleDesc() {
                @Override
                public void onCancelClick() {

                }

                @Override
                public void onOkClick() throws JSONException {
                    if (results.has("url")) {
                        download(results.getString("url"));
                    } else {
                        showErrorMessage(R.string.err_get_version);
                    }

                }
            });
            updateDialog.show(getActivity().getSupportFragmentManager(), "updateDialog");
        } else {
            showErrorMessage(R.string.err_get_version);
        }
    }

    private void showNoUpdateDialog(String statusMsg) {
        DialogTitleDesc noUpdateDialog;
        if (!TextUtils.isEmpty(statusMsg)) {
            noUpdateDialog = DialogTitleDesc.newInstance("当前版本" + AppUtil.getAppVersionName(getContext()), statusMsg, "取消", "升级到新版本", false);
        } else {
            noUpdateDialog = DialogTitleDesc.newInstance("当前版本" + AppUtil.getAppVersionName(getContext()), "暂无新版本", "取消", "升级到新版本", false);
        }
        noUpdateDialog.setListener(null);
        noUpdateDialog.show(getActivity().getSupportFragmentManager(), "noUpdateDialog");
    }

    private void showErrorMessage(int resId) {
        ToastUtil.showToast(getContext(), resId);
    }

    private void download(String url) {

        dialogTitleProgress = DialogTitleProgress.newInstance("正在下载", "取消", url, filePath);

        dialogTitleProgress.setListener(new DialogTitleProgress.IDownloadDialogListener() {
            @Override
            public void onDownloadSuccess() {
                ToastUtil.showToast(getContext(), "下载完成");
                installApp(filePath);
            }
        });
        dialogTitleProgress.show(getActivity().getSupportFragmentManager(), "dialogTitleProgress");

    }

    private void installApp(String filePath) {
        if (filePath == null || filePath.trim().length() == 0) {
            ToastUtil.showToast(getContext(), "文件存储地址出错");
            LogUtil.vendor(TAG + "file path is null");
            return;
        }
        File file = new File(filePath);
        if (file != null && file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
                i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
//            if (installUseRoot(filePath)) {
//                LogUtil.vendor(TAG + "install success");
//            }
            else {
                ToastUtil.showToast(getContext(), "安装失败");
                LogUtil.vendor(TAG + "install failed");
            }
        } else {
            ToastUtil.showToast(getContext(), "Apk出错");
            LogUtil.vendor(TAG + "apk error");
        }

    }

    private boolean installUseRoot(String filePath) {
        boolean result = false;
        Process process = null;
        OutputStream outputStream = null;
        BufferedReader errorStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = process.getOutputStream();

            String command = "pm install -r " + filePath + "\n";
            outputStream.write(command.getBytes());
            outputStream.flush();
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                msg.append(line);
            }
            LogUtil.vendor(TAG + " install msg " + msg);
            if (!msg.toString().contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            LogUtil.vendor(TAG + " exception " + e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                outputStream = null;
                errorStream = null;
                process.destroy();
            }
        }
        return result;
    }


    private void moveBackground() {
        DialogTitleInput dialogTitleInput = DialogTitleInput.newInstance("密码验证：", "请输入密码:", "请输入密码");
        dialogTitleInput.setListener(new DialogTitleInput.IDialogTitleInputListener() {
            @Override
            public void onDialogTitleInputCancelClick() {

            }

            @Override
            public void onDialogTitleInputOkClick(String password) {
                VerifyPasswordInfo info = new VerifyPasswordInfo();
                info.setPassword(password);
                info.setType(VerifyPasswordInfo.TYPE_MOVE_BACKGROUND);
                execute(info.toRemote());

            }
        });
        dialogTitleInput.show(getActivity().getSupportFragmentManager(), "dialogTileInput");
    }

    private void loginExit() {
        DialogTitleInput dialogTitleInput2 = DialogTitleInput.newInstance("密码验证：", "请输入密码:", "请输入密码");
        dialogTitleInput2.setListener(new DialogTitleInput.IDialogTitleInputListener() {
            @Override
            public void onDialogTitleInputCancelClick() {

            }

            @Override
            public void onDialogTitleInputOkClick(String password) {
                ProgressDlgHelper.showProgress(getActivity(), "正在退出");

                LogoutInfo info = new LogoutInfo();
                info.setUid(U.getMyVendorNum());
                info.setPassword(password);
                execute(info.toRemote());

            }
        });
        dialogTitleInput2.show(getActivity().getSupportFragmentManager(), "dialogTileInput2");
    }

    private void loginOut() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        String host = "";
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        }else if(AppConfig.BUILD_SERVER==AppConfig.Build.TEST){
            host = StringUtil.HOST_TEST;
        }else {
            host = StringUtil.HOST_LOCAL;
        }
        String url = host + "admins/makerLoginOut";
        RequestParams params = new RequestParams();
        params.put("recordId", mRecordId);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ProgressDlgHelper.closeProgress();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

//    private void enterBackstageAndLogout() {
//        ControlVeriPwdDialog veriPwdDialog4Background = new ControlVeriPwdDialog(getActivity(), mListener,
//                VerifyPasswordInfo.TYPE_LOGOUT);
//        veriPwdDialog4Background.show();
//    }


//    private void enterBackstage() {
//        ControlVeriPwdDialog veriPwdDialog4Background = new ControlVeriPwdDialog(getActivity(), mListener,
//                VerifyPasswordInfo.TYPE_MOVE_BACKGROUND);
//        veriPwdDialog4Background.show();
//    }

    private void showHintDialog() {
        DialogTitleDesc dialogTitleDesc = DialogTitleDesc.newInstance("当前版本已是最新", "暂无新版本", "取消", "升级到新版本", false);
//        DialogTitle dialogTitle=DialogTitle.newInstance(getString(R.string.str_please_wait));
        dialogTitleDesc.show(getActivity().getSupportFragmentManager(), "hintDialog");
    }

    public void setRecordId(int recordId) {
        this.mRecordId = recordId;
    }
}
