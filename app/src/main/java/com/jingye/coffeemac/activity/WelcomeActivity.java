package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.SetTempInstruction;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.loader.NetLoaderTool;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.action.GetDosingListInfo;
import com.jingye.coffeemac.service.bean.action.GetMachineConfigInfo;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.bean.action.SyncStockInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetAdvPicsResult;
import com.jingye.coffeemac.service.bean.result.GetDosingResult;
import com.jingye.coffeemac.service.bean.result.GetMachineConfigResult;
import com.jingye.coffeemac.service.bean.result.LogUploadResult;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.bean.result.UpdateStockResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.InstallUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.StorageUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;
import com.jingye.coffeemac.util.qiniuutil.Auth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class WelcomeActivity extends TActivity implements OnClickListener {

    private static final String TAG = "WelcomePage";
    private static final int THRESHOLD_SYSC_STOCK = 10 * 60 * 1000;   //10 mins
    private static final int THRESHOLD_ADV_UPDATE = 3 * 60 * 1000;    //3 mins
    private static final int MAX_WASH_TIME = 90;
    private static final int LOCATION_TIME = 60 * 1000;
    private static final java.lang.String QINIU_BUCKET = "coffees";
    private Context mContext;
    private ViewFlipper mFlipper;
    private LinearLayout mNotifyBar;
    private TextView mDescriLabel;
    private FrameLayout mContentArea;
    private ImageView mBuyCoffee;

    private boolean foreground;
    private boolean isNeedRefreshAds = false;
    private long mQuitTimeStamp;
    private CountDownTimer washTimer;
    private boolean isNeedRefreshTemp = false;
    private CountDownTimer mTimerTempSet;
    private boolean isNeedRefreshCoffee = false;
    private boolean isNeedReboot = false;
    private LocationClient mLocationClient;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        mContext = this;

        initViews();
        initFlipper();
        initStatus();
        getMachineConfig();
        getCoffeeAdvs();
        initTimer();

        reportMacVersion();

        initLocation();

    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
//        initLocationClient(LOCATION_TIME,"gcj02",false);
        initLocationClient(LOCATION_TIME, "bd09ll", false);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
    }

    /**
     * @param span          扫描间隔
     * @param coorType      坐标系类型 gcj02 bd09ll
     * @param isNeedAddress
     */
    private void initLocationClient(int span, String coorType, boolean isNeedAddress) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(coorType);
        option.setScanSpan(span);
        option.setOpenGps(true);
        option.setIsNeedAddress(isNeedAddress);
        mLocationClient.setLocOption(option);

    }

    private void initTimer() {
        mTimerTempSet = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                LogUtil.vendor(TAG + "set temp time: " + value);
                if (value <= 0) {
                    ProgressDlgHelper.closeProgress();
                    LogUtil.vendor(TAG + "set temperature failed!");
                    ToastUtil.showToast(WelcomeActivity.this, R.string.welcome_set_temperature_fail);
                }
            }
        });
    }

    private void startTempSetTimer() {
        mTimerTempSet.startCountDownTimer(MAX_WASH_TIME, 1000, 1000);
    }

    private void stopTempSetTimer() {
        mTimerTempSet.cancelCountDownTimer();
    }

    private void initViews() {
        // network status bar
        mNotifyBar = (LinearLayout) findViewById(R.id.status_notify_bar);
        mDescriLabel = (TextView) findViewById(R.id.status_desc_label);

        mContentArea = (FrameLayout) findViewById(R.id.welcome_page_parent);
        mContentArea.setOnClickListener(this);
        mBuyCoffee = (ImageView) findViewById(R.id.welcome_buy_coffee_btn);
        mBuyCoffee.setOnClickListener(this);
    }

    private void initFlipper() {
        mFlipper = (ViewFlipper) findViewById(R.id.welcome_flipper);
//      mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
//      mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void updateStatus(int status) {
        String descTips = "";
        if (status == ITranCode.STATUS_NO_NETWORK) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_is_not_available);
        } else if (status == ITranCode.STATUS_CONNECT_FAILED) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_server_is_not_available);
        } else if (status == ITranCode.STATUS_FORBIDDEN) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_forbidden_login);
        } else {
            mNotifyBar.setVisibility(View.GONE);
            if (status == ITranCode.STATUS_LOGGING) {
                descTips = getString(R.string.network_connecting);
            }
        }

        mDescriLabel.setText(descTips);
    }

    private void getMachineConfig() {
        if (!AppConfig.isSerialportSysnc())
            return;
        GetMachineConfigInfo info = new GetMachineConfigInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.vendor("WelcomeActivity->onNewIntent");
        syncStockFromServer(false);
        syncAdvFromServer();
        // clear cart cache
        MyApplication.Instance().clearCartPay();

    }

    private void syncStockFromServer(boolean isNow) {
        if (isNow || (TimeUtil.getNow_millisecond() -
                MyApplication.Instance().getLastSyncStockTime() >= THRESHOLD_SYSC_STOCK)) {
            getCoffeeDosingList();
        }
    }

    private void getCoffeeAdvs() {
        LogUtil.vendor(TAG + "get advertisements from server");
        // get adv local first
        List<String> advPicUrlsList = SharePrefConfig.getInstance().getAdvImgs();
        if (advPicUrlsList != null && advPicUrlsList.size() > 0) {
            for (int i = 0; i < advPicUrlsList.size(); i++) {
                String advPicUrl = advPicUrlsList.get(i);
                mFlipper.addView(addImageByURL(advPicUrl));
            }

            mFlipper.setAutoStart(true);
            mFlipper.setFlipInterval(8 * 1000);
            mFlipper.startFlipping();
        }
        // check update adv from server
        syncAdvFromServer();
    }

    private void syncAdvFromServer() {

        long time = SharePrefConfig.getInstance().getAdvUpdateTime();
        if (TimeUtil.getNow_millisecond() - time >= THRESHOLD_ADV_UPDATE) {
            GetAdvPicsInfo info = new GetAdvPicsInfo();
            info.setUid(U.getMyVendorNum());
            execute(info.toRemote());
        }
    }

    private void getCoffeeDosingList() {
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        info.setAuto(true);
        execute(info.toRemote());
    }

    private void showDefaultAdvPics() {
        int childCount = mFlipper.getChildCount();
        if (childCount <= 0) {
            mFlipper.addView(addImageById(R.drawable.welcome_1));
            mFlipper.addView(addImageById(R.drawable.welcome_2));
            mFlipper.setAutoStart(true);
            mFlipper.setFlipInterval(8 * 1000);
            mFlipper.startFlipping();
        }
    }

    private void reportMacVersion() {
        try {


            JSONObject tokenJson = new JSONObject();
            tokenJson.put("sign", "jinyekeji2016");
            tokenJson.put("time", TimeUtil.getNow_millisecond());
            String source = tokenJson.toString();
            String token = AESUtil.encrypt("442bef40be3e8188", source);  // token
            String version = InstallUtil.getVersionName(mContext);       // version
            int machineId = Integer.parseInt(U.getMyVendorNum());        // machineId

            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            String host = "";
            if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
                host = StringUtil.HOST_ONLINE;
            } else if (AppConfig.BUILD_SERVER == AppConfig.Build.TEST) {
                host = StringUtil.HOST_TEST;
            } else {
                host = StringUtil.HOST_LOCAL;
            }
            String url = host + "vendingMachines/versionUpdate";

            RequestParams params=new RequestParams();
            params.put("token",token);
            params.put("machineId",machineId);
            params.put("macVersion",version);
            LogUtil.vendor("token:"+token+"machineId:"+machineId+"macVersion:"+version);
            client.post(url,params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    LogUtil.vendor("token:"+responseString);
                }
            });

//            JSONObject obj = new JSONObject();
//            obj.put("token", token);
//            obj.put("machineId", machineId);
//            obj.put("macVersion", version);
//            LogUtil.vendor(obj.toString());
//            StringEntity entity = new StringEntity(obj.toString(), "utf-8");
//
//            client.post(mContext, url, entity, "application/json", new AsyncHttpResponseHandler() {
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    LogUtil.vendor("[Version Report] statusCode is " + statusCode);
//                    LogUtil.vendor("responseBody----->"+new String(responseBody));
//                    if (statusCode == 200) {
//                        LogUtil.vendor("Version Report Successfully!");
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    error.printStackTrace();
//                    LogUtil.e("vendor", "Version Report Failed!");
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "Something wrong with report mac version!");
        }
    }

    @Override
    public void onReceive(Remote remote) {
        // system action
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
        // user action
        if (remote.getWhat() == ITranCode.ACT_USER) {
            if (remote.getAction() == ITranCode.ACT_USER_LOGOUT) {
                LogUtil.vendor("onReceive -> ACT_USER_LOGOUT");
                ProgressDlgHelper.closeProgress();
                LogoutResult result = Ancestor.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    this.finish();
                }
            }
        }
        // coffee action
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.isAuto()) {
                    if (result.getResCode() == 200) {
                        syncStock(result.getDosings());
                    } else {
                        ToastUtil.showToast(this, R.string.welcome_get_dosing_list_fail);
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_STOCK_UPDATE) {
                UpdateStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.isAuto()) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(this, R.string.welcome_sync_stock_success);
                        MyApplication.Instance().setLastSyncStockTime(TimeUtil.getNow_millisecond());
                    } else {
                        ToastUtil.showToast(this, R.string.welcome_sync_stock_fail);
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_GET_MACHINE_CONIFG) {
                GetMachineConfigResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result.getResCode() == 200) {
                    String workTemp = result.getWorkTemp();
                    String keepTemp = result.getKeepTemp();
                    saveWorkTemp(workTemp, keepTemp);
                    if (isNeedRefreshTemp) {
                        LogUtil.vendor("isNeedReFreshTemp:" + isNeedRefreshTemp);
                        setTemp(workTemp, keepTemp);
                    }
                    String washTime = result.getWashTime();
                    saveWashTime(washTime);
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_GET_ADV_PICS) {
                GetAdvPicsResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result.getResCode() == 200) {

                    SharePrefConfig.getInstance().setAdvImgs(result.getAdvImgUrls());
                    mFlipper.removeAllViews();
                    List<String> advPicUrls = result.getAdvImgList();
                    if (advPicUrls == null || advPicUrls.size() <= 0) {
                        showDefaultAdvPics();
                        return;
                    }

                    for (int i = 0; i < advPicUrls.size(); i++) {
                        String advPicUrl = advPicUrls.get(i);
                        mFlipper.addView(addImageByURL(advPicUrl));
                    }

                    mFlipper.setAutoStart(true);
                    mFlipper.setFlipInterval(8 * 1000);
                    mFlipper.startFlipping();
                    isNeedRefreshAds = false;
                } else {
                    showDefaultAdvPics();
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_LOG_UPLOAD) {

                LogUploadResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    doUploadLog(result);
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {
                Log.d(" Welcome notice", " i receive notice");
                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String type = result.getType();

                    if (type.equals("101")) {
                        SharePrefConfig.getInstance().setIsNeedLock(false);
                    } else if (type.equals("102")) {
                        isNeedRefreshCoffee = true;

                    } else if (type.equals("103")) {

                        isNeedRefreshAds = true;
                        GetAdvPicsInfo info = new GetAdvPicsInfo();
                        info.setUid(U.getMyVendorNum());
                        execute(info.toRemote());
                    } else if (type.equals("104")) {
                        SharePrefConfig.getInstance().setIsNeedLock(true);
                    } else if (type.equals("105")) {
                        isNeedRefreshTemp = true;
                    } else if (type.equals("106")) {
                        isNeedReboot = true;
                    }
                }
            }
        }
        // machine action
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING_START) {

                beginWashing();
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING) {
                if (!foreground) {
                    return;
                }

                String res = remote.getBody();
                if (!TextUtils.isEmpty(res) && res.length() == 14) {
                    int result = CoffeeMachineResultProcess.processWashingResult(res);
                    if (result == 1) {
                        ToastUtil.showToast(this, getString(R.string.welcome_start_wash_machine));
                    } else if (result == 2) {
                        ToastUtil.showToast(this, getString(R.string.welcome_wash_machine_success));
                        LogUtil.vendor("washing machine successfully!");
                        stopWashTimer();
                        ProgressDlgHelper.closeProgress();

                        // update stock
                        updateStockAfterWash();
                        syncStockFromServer(true);
                    } else {
                        ToastUtil.showToast(this, getString(R.string.welcome_wash_machine_fail));
                        LogUtil.vendor("washing machine failed!");
                        stopWashTimer();
                        ProgressDlgHelper.closeProgress();
                        // report server
                        List<Integer> status = new ArrayList<Integer>();
                        MachineStatusReportInfo info = new MachineStatusReportInfo();
                        info.setUid(U.getMyVendorNum());
                        info.setTimestamp(TimeUtil.getNow_millisecond());
                        status.add(MachineStatusCode.WASHING_MACHINE_FAILED);
                        MyApplication.Instance().setWaitMaintenance(false);
                        info.setStatus(status);
                        execute(info.toRemote());
                    }
                } else {
                    ToastUtil.showToast(this, getString(R.string.welcome_wash_machine_fail));
                    LogUtil.vendor("washing machine failed!");
                    stopWashTimer();
                    ProgressDlgHelper.closeProgress();
                    // report server
                    List<Integer> status = new ArrayList<Integer>();
                    MachineStatusReportInfo info = new MachineStatusReportInfo();
                    info.setUid(U.getMyVendorNum());
                    info.setTimestamp(TimeUtil.getNow_millisecond());
                    status.add(MachineStatusCode.WASHING_MACHINE_FAILED);
                    MyApplication.Instance().setWaitMaintenance(false);
                    info.setStatus(status);
                    execute(info.toRemote());
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_SYNC) {
                if (!foreground) {
                    return;
                }
                ProgressDlgHelper.closeProgress();
                stopTempSetTimer();
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processSetTempResult(res);
                if (result.equals("success")) {
                    isNeedRefreshTemp = false;
                    LogUtil.vendor("set temperature successfully!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_success);
                } else {
                    LogUtil.vendor("set temperature failed!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_fail);
                }
            }
        }
    }

    private void setTemp(String workTemp, String keepTemp) {
        ProgressDlgHelper.showProgress(this, getString(R.string.welcome_set_temperature));
        startTempSetTimer();
        SetTempInstruction instruction = new SetTempInstruction((int) (Float.parseFloat(workTemp)), (int) (Float.parseFloat(keepTemp)));
        SerialPortDataWritter.writeDataCoffee(instruction.getSetTempOrder());

    }

    //---------------------log upload----------------------
    private void doUploadLog(LogUploadResult result) {
        String type = result.getType();
        String date = result.getDate();
        if (!TextUtils.isEmpty(type) && type.equals("info") && !TextUtils.isEmpty(date)) {
            logUpload(date);
        } else if (!TextUtils.isEmpty(type) && type.equals("error")) {
            logUploadError();
        }
    }

    private void uploadFileToQiniu(String path, String key) {
        UploadManager uploadManager = new UploadManager();
        String token = Auth.create().uploadToken(QINIU_BUCKET, key);
        uploadManager.put(path, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, org.json.JSONObject response) {

                if (info.isOK()) {
                    LogUtil.vendor("Upload Success");
                } else {
                    LogUtil.vendor("Upload Fail");
                }
                LogUtil.vendor("qiniu " + key + ",\r\n " + "status " + info.statusCode);
            }
        }, null);
    }

    //    "jijia_online_" + fileName + "_"+vendingMachineId
    private void logUpload(String date) {
        try {
            LogUtil.vendor("START TO UPLOAD LOG : " + date);
            String fileName = "vendor_log_" + date + ".log";
            String target = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/vendor/log/" + fileName;
            String path = StorageUtil.getReadPath(fileName, target);
            if (!TextUtils.isEmpty(path)) {

                StringBuffer key = new StringBuffer();
                key.append(U.getMyVendorNum())
                        .append("jijia");
                if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
                    key.append("_online");
                } else {
                    key.append("_testline");

                }
                key.append("_"+fileName);
                uploadFileToQiniu(path, key.toString());
            } else {
                LogUtil.vendor("path not exsit");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("LOG_UPLOAD", "something wrong with log-upload");
        }
    }

    private void logUploadError() {
        try {
            LogUtil.vendor("START TO UPLOAD ERROR LOG");

            String fileName = "AppErrorLog.log";
            String target = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/vendor/log/" + fileName;
            String path = StorageUtil.getReadPath(fileName, target);
            if (!TextUtils.isEmpty(path)) {

                StringBuffer key = new StringBuffer();
                key.append(U.getMyVendorNum())
                        .append("jijia");
                if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
                    key.append("_online");
                } else {
                    key.append("_testline");

                }
                key.append("_"+fileName);
                uploadFileToQiniu(path, key.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("LOG_UPLOAD", "something wrong with log-upload");
        }
    }
    //---------------------log upload----------------------

    private void saveWorkTemp(String workTemp, String keepTemp) {
        try {
            LogUtil.vendor("work temp is " + workTemp + "," + keepTemp);
            SharePrefConfig.getInstance().setTemp(workTemp, keepTemp);
        } catch (Exception e) {
            LogUtil.e("Welcome", "set work temp error");
        }
    }

    private void saveWashTime(String washTime) {
        try {
            LogUtil.vendor("wash time is " + washTime);
            MyApplication.Instance().mGlobalWashTimeSet.clear();
            if (washTime != null && !TextUtils.isEmpty(washTime)) {
                JSONArray array = JSON.parseArray(washTime);
                int size = array.size();
                for (int i = 0; i < size; i++) {
                    String time = array.get(i).toString();
                    MyApplication.Instance().mGlobalWashTimeSet.add(time);
                }
            }
            SharePrefConfig.getInstance().setWashTime(MyApplication.Instance().mGlobalWashTimeSet);
        } catch (Exception e) {
            LogUtil.e("Welcome", "set wash time error");
        }
    }

    private void updateStockAfterWash() {
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double leftWater = stockWater - 150;
        BigDecimal leftWaterBD = new BigDecimal(leftWater);
        leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftWater < 0) {
            leftWater = 0;
        }
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater),
                MachineMaterialMap.MATERIAL_WATER);
    }

    private void syncStock(List<CoffeeDosingInfo> dosings) {
        if (dosings == null) {
            ToastUtil.showToast(this, R.string.control_doing_list_is_null);
            return;
        }

        double dosingWater = getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double dosingCupNum = getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        double dosingNo1 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        double dosingNo2 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        double dosingNo3 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        double dosingNo4 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        double dosingNo5 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
        double dosingNo9 = getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);

        JSONArray array = new JSONArray();
        for (CoffeeDosingInfo info : dosings) {
            double value = 0;
            if (info.getId() == 1) {
                value = dosingWater;
            } else if (info.getId() == 2) {
                value = dosingCupNum;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1) {
                value = dosingNo1;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2) {
                value = dosingNo2;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3) {
                value = dosingNo3;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4) {
                value = dosingNo4;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_5) {
                value = dosingNo5;
            } else if (info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN) {
                value = dosingNo9;
            }

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", info.getId());
            jsonObj.put("value", value);
            array.add(jsonObj);
        }

        SyncStockInfo info = new SyncStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    private double getDosingValue(int dosingID) {
        double value = SharePrefConfig.getInstance().getDosingValue(dosingID);
        return value;
    }

    public View addImageById(int id) {
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(id);

        return iv;
    }

    public View addImageByURL(String url) {
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new ViewFlipper.LayoutParams(ScreenUtil.screenWidth, ScreenUtil.screenHeight));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        String imgURL = url;
        if (imgURL == null) {
            imgURL = "";
        }
        ImageLoaderTool.disPlayGif(WelcomeActivity.this, imgURL.trim(), iv);
        return iv;
    }

    private void beginWashing() {
        ProgressDlgHelper.showProgress(this, getString(R.string.welcome_start_wash_machine));
        startWashTimer();
        // send order
        SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.WASHING);

    }

    private void startWashTimer() {
        washTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                LogUtil.vendor("wash time: " + value);
                if (value <= 0) {
                    ProgressDlgHelper.closeProgress();
                    ToastUtil.showToast(WelcomeActivity.this, getString(R.string.welcome_wash_machine_fail));
                    LogUtil.vendor("timer washing machine failed!");
                }
            }
        });
        washTimer.startCountDownTimer(MAX_WASH_TIME, 1000, 1000);
    }

    private void stopWashTimer() {
        washTimer.cancelCountDownTimer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_page_parent:
            case R.id.welcome_buy_coffee_btn:
                HomePageActivity.start(this, false, 0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mQuitTimeStamp) > 2000) {
                ToastUtil.showToast(this, R.string.welcome_enter_control_panel_tip);
                mQuitTimeStamp = System.currentTimeMillis();
            } else {
//				MachineControlActivity.start(this);
                BackgroundLoginActivity.start(this);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.Instance().setMachineIdle(true);
        foreground = true;
//        if (isNeedRefreshAds) {
//
//            LogUtil.vendor("isNeedRefreshAds:" + isNeedRefreshAds);
//            GetAdvPicsInfo info = new GetAdvPicsInfo();
//            info.setUid(U.getMyVendorNum());
//            execute(info.toRemote());
//        }

        if (SharePrefConfig.getInstance().isNeedLock()) {
            MyApplication.Instance().setWaitMaintenance(true);
            MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.LOCK, false);
            Intent intent = new Intent();
            intent.setClass(this, WaitMaintanceActivity.class);
            startActivity(intent);
        }

        if (isNeedRefreshTemp) {
            getMachineConfig();
        }

        if (isNeedRefreshCoffee) {
            GetCoffeeInfo info = new GetCoffeeInfo();
            info.setUid(U.getMyVendorNum());
            executeBackground(info.toRemote());
            isNeedRefreshCoffee = false;
        }

        if (isNeedReboot) {
            if (!MyApplication.Instance().isMakingCoffee()) {
                LogUtil.vendor("Reboot coffee machine");
                String cmd = "su -c reboot";
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.Instance().setMachineIdle(false);
        foreground = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ProgressDlgHelper.closeProgress();
    }

    private String generateToken(){
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("sign", "jinyekeji2016");
        tokenJson.put("time", TimeUtil.getNow_millisecond());
        String source = tokenJson.toString();
        String token = null;
        try {
            token = AESUtil.encrypt("442bef40be3e8188", source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private void updateLocationToWeb(String coffeeMachineId,double latitude,double longtitude) {
        String host;
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        } else if (AppConfig.BUILD_SERVER == AppConfig.Build.TEST) {
            host = StringUtil.HOST_TEST;
        } else {
            host = StringUtil.HOST_LOCAL;
        }
        String url = host + "vendingMachines/uploadLongitudeAndLatitude";
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("vendingMachineId",coffeeMachineId);
        param.put("latitude",latitude);
        param.put("longtitude",longtitude);
        param.put("token",generateToken());
        NetLoaderTool.INetLoaderListener listener=new NetLoaderTool.INetLoaderListener() {
            @Override
            public void onSuccess(String success) {
                LogUtil.vendor(TAG+"success");

            }

            @Override
            public void onFailure(String error) {
                LogUtil.vendor(TAG+"error");
            }
        };
        NetLoaderTool.post(url,param,listener);

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型


            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {

                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());    //获取纬度信息

                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());    //获取经度信息


                LogUtil.vendor(sb.toString());


                updateLocationToWeb(U.getMyVendorNum(),location.getLatitude(),location.getLongitude());


                if (mLocationClient.isStarted()) {
                    mLocationClient.stop();
                }
            }


        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
