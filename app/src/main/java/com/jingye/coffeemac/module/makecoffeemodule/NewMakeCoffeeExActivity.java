package com.jingye.coffeemac.module.makecoffeemodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.HomePageActivity;
import com.jingye.coffeemac.activity.MakeCoffeeExActivity;
import com.jingye.coffeemac.adapter.MakeCoffeeViewHolder;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.MakeCoffeeItem;
import com.jingye.coffeemac.beans.OrderContent;
import com.jingye.coffeemac.beans.OrderContentItem;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.common.adapter.TAdapter;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.common.adapter.TViewHolder;
import com.jingye.coffeemac.common.component.TListView;
import com.jingye.coffeemac.common.dbhelper.CoffeeIndentDbHelper;
import com.jingye.coffeemac.domain.CoffeeIndent;
import com.jingye.coffeemac.instructions.CoffeeMachineInstruction;
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.ConpMixedDrinksInstruction;
import com.jingye.coffeemac.instructions.IMixedDrinksInstruction;
import com.jingye.coffeemac.instructions.MixedDrinksInstruction;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.loader.NetLoaderTool;
import com.jingye.coffeemac.module.heatmodule.HeatActivity;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.bean.action.ReportErrorFetchInfo;
import com.jingye.coffeemac.service.bean.action.RollbackCoffeeIndentCart;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.StorageUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.ToolUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class NewMakeCoffeeExActivity extends TActivity implements SurfaceHolder.Callback, NewMakeCoffeeExContact.NewMakeCoffeeExView, TViewHolder.ViewHolderEventListener, View.OnClickListener {
    public static final String ORDER_CONTENT = "order_content";
    public static final String ORDER_LOCAL = "order_local";
    private static final int MAKE_COFFEE_MAX_WAIT_TIME = 280;
    private static final int CHECK_PICK_STATUS_MAX_WAIT_TIME = 60;
    private static final String TAG = "[MakeCoffeeCart]";
    private static final int MAKE_COFFEE_PORT_TIME = 60;
    private static final int MAKE_COFFEE_MAX_FETCH_TIME = 300;
    private static final int MAX_WAIT_LAST_CUP_TIME = 30;

    private static final String MAKE_COFFEE_DESK_NOCUP = "make_coffee_desk_nocup";
    /***
     * OPERATION
     */
    private static final int MSG_UI_MAKE_COFFEE_START = 0;
    private static final int MSG_UI_MAKE_COFFEE_FAIL = 1;
    private static final int MSG_UI_MAKE_COFFEE_RETRY = 2;
    private static final int MSG_UI_MAKE_COFFEE_SUCCESS = 3;
    private static final int MSG_UI_MAKE_COFFEE_SUCCESS_ALL = 4;
    private static final int MSG_MAKE_COFFEE_TIMEOUT = 5;
    private static final int MSG_MAKE_COFFEE_PORT_TIMEOUT = 6;
    private static final int MSG_MAKE_COFFEE_ONCE_MORE = 7;
    private static final int MSG_MAKE_COFFEE_DESK_NO_CUP = 8;
    // 咖啡订单
    private Boolean mOrderLocal;
    private OrderContent mOrderContent;
    private Context mContext;
    private MakeCoffeeExActivity.CoffeeMachineStatus mMachineStatus;
    //制作界面
    private LinearLayout mMakeCoffee_layout;
    //取杯继续
    private LinearLayout mMakeCoffee_fetchcup;
    private TextView mMakeCoffee_fetchtip;
    private Button mMakeCoffee_continue_btn;
    private TextView mMakeCoffee_timer;
    // 制作列表
    private TListView mListView;
    private BaseAdapter mAdapter;
    private List<MakeCoffeeItem> mMakeCoffeeItems = new ArrayList<MakeCoffeeItem>();
    private TextView mMakeCoffeeTip;
    private TextView mMakeCoffeeName;
    private boolean isNeedWashCurrent = false;
    // 视频广告
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private String videoPath;
    private ImageView mAdvImg;
    private boolean isCancel = false;
    private CountDownTimer mFecthTimer;
    private CountDownTimer mMakeCoffeePortTimer;
    private CountDownTimer mMakeCoffeeTimer;
    private CountDownTimer mQueryPickTimer;
    private AtomicBoolean mWaitPickMode = new AtomicBoolean(false);
    // 菜单刷新
    private boolean isRefreshMenu = false;
    private NewMakeCoffeeExPresenter mPresenter;
    private int mItemIndex = 0;
    private ArrayList<CoffeeDosingInfo> mCurrentDosingList;
    //是否是最后一杯
    private boolean isFinish = false;
    private boolean makeCoffeeOnceMore = true;
    private AtomicInteger mQueryPickCount = new AtomicInteger();
    private CountDownTimer mLastCupTimer;
    private SafeHandler mUIHandler;
    private ImageView mFetchImg;
    private RelativeLayout rlMakecoffeeThumbup;
    private RelativeLayout rlMakecoffeeKeepup;
    private RelativeLayout rlMakecoffeeThumbdown;
    private ImageView ivMakecoffeeThumbupCheck;
    private ImageView ivMakecoffeeKeepupCheck;
    private ImageView ivMakecoffeeThumbdownCheck;
    private boolean isFirstClick = true;
    private TextView tvMakeCoffeeCommentTitle;

    public static void start(Activity activity, OrderContent orderContent, boolean local) {
        Intent intent = new Intent();
        intent.setClass(activity, NewMakeCoffeeExActivity.class);
        intent.putExtra(ORDER_CONTENT, orderContent);
        intent.putExtra(ORDER_LOCAL, local);
        activity.startActivity(intent);
    }

    public static String getNumEn(int num) {
        if (num == 1) {
            return num + "st    ";
        } else if (num == 2) {
            return num + "nd    ";
        } else if (num == 3) {
            return num + "rd    ";
        } else
            return num + "th    ";
    }

    @Override
    public void onMakeCoffeePortTimeOut() {
        LogUtil.e(TAG, "onMakeCoffeePortTimeout()");
        cancelMakeCoffeePortTimer();
        cancelMakeCoffeeTimer();
        LogUtil.vendor("setMaintenance 33333");
        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.MAKE_COFFEE_PORT_TIMEOUT, true);
        MyApplication.Instance().setWaitMaintenance(true);
        List<Integer> errors = new ArrayList<Integer>();
        errors.add(MachineStatusCode.MAKE_COFFEE_PORT_TIMEOUT);
        mPresenter.setFailed(errors);
    }

    @Override
    public void onMakeCoffeeTimeout() {
        LogUtil.e(TAG, "onMakeCoffeeTimeout()");
        LogUtil.vendor("setMaintenance 44444");
        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.COFFEE_MAKE_TIME_OUT, true);
        MyApplication.Instance().setWaitMaintenance(true);
        List<Integer> errors = new ArrayList<Integer>();
        errors.add(MachineStatusCode.COFFEE_MAKE_TIME_OUT);
        mPresenter.setFailed(errors);
    }

    @Override
    public void setFailed(List<Integer> errors) {
        // update indent status in DB
        try {
            OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
            updateCoffeeIndent(mOrderContent.getOrderID(), Integer.parseInt(item.getItemID()),
                    CoffeeIndent.STATUS_COFFEE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // rollback order
        rollBackOrder(errors);
        // report server
        MachineStatusReportInfo info = new MachineStatusReportInfo();
        info.setUid(U.getMyVendorNum());
        info.setTimestamp(TimeUtil.getNow_millisecond());
        List<Integer> status = new ArrayList<Integer>();
        for (Integer error : errors) {
            status.add(error);
        }
        info.setStatus(status);
        execute(info.toRemote());
        // machine status
        mMachineStatus = MakeCoffeeExActivity.CoffeeMachineStatus.READY;
        // quit time
        updateListStatus(mItemIndex, MakeCoffeeItem.STATUS_FAIL);
        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
        //mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
        if (errors.contains(MachineStatusCode.MACHINE_WARM_UP)) {
            HeatActivity.start(NewMakeCoffeeExActivity.this);
        } else {
            HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
        }
    }

    @Override
    public void onMakeCoffeeSuccess(String name) {

        mMakeCoffee_layout.setVisibility(View.GONE);
        mMakeCoffee_fetchcup.setVisibility(View.VISIBLE);
        isCancel = true;
        mMakeCoffeeTimer.cancelCountDownTimer();


        if (name.equals(MAKE_COFFEE_DESK_NOCUP) && MyApplication.Instance().getDesk()) {
            mMakeCoffee_fetchtip.setText(getString(R.string.str_place_cup));
            ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_put_cup, mFetchImg);
        } else {
            if (MyApplication.Instance().getDesk()) {
                if (null != name) {
                    mMakeCoffee_fetchtip.setText(name + getResources().getString(R.string.make_coffee_finish_desk_fetchcup));
                } else {
                    mMakeCoffee_fetchtip.setText(getResources().getString(R.string.make_coffee_finish_desk_fetchcup));
                }

                ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg_desk, mFetchImg);


            } else {
                if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                    ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg_en, mFetchImg);
                } else {
                    ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg, mFetchImg);
                }
                if (null != name) {
                    mMakeCoffee_fetchtip.setText(name + getResources().getString(R.string.make_coffee_finish_fetchcup));
                } else {
                    mMakeCoffee_fetchtip.setText(getResources().getString(R.string.make_coffee_finish_fetchcup));
                }
            }
        }


        if (isFinish) {
            if (!MyApplication.Instance().getDesk()) {
                quitPickUpModeIfNeeded();
                mMakeCoffee_timer.setVisibility(View.VISIBLE);
                cancelFecthTimer();
            } else {
                mMakeCoffee_timer.setVisibility(View.GONE);
            }
            startLastCupTimer();
            mMakeCoffee_continue_btn.setBackgroundResource(R.drawable.make_coffee_back_selector);
            mMakeCoffee_continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    cancelPickUpTimer();

                    stopLastCupTimer();
                    HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
                }
            });

        } else {
            mMakeCoffee_timer.setVisibility(View.VISIBLE);
            mFecthTimer.startCountDownTimer(MAKE_COFFEE_MAX_FETCH_TIME, 1000, 1000);
            mMakeCoffee_continue_btn.setBackgroundResource(R.drawable.make_coffee_continue_selector);
            mMakeCoffee_continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation cycleAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_info);
                    mMakeCoffee_fetchtip.startAnimation(cycleAnim);
                    if (MyApplication.Instance().getDesk()) {
                        if (ToolUtil.above5Mi()) {
                            mPresenter.makeCoffee();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onMakeCoffeeRetry() {
        mMakeCoffee_layout.setVisibility(View.GONE);
        mMakeCoffee_fetchcup.setVisibility(View.VISIBLE);
        mMakeCoffeeTimer.cancelCountDownTimer();
        isCancel = true;
        mMakeCoffee_timer.setVisibility(View.VISIBLE);
        mFecthTimer.startCountDownTimer(MAKE_COFFEE_MAX_FETCH_TIME, 1000, 1000);
    }

    @Override
    public void onMakeCoffeeFail() {
        if (mOrderLocal) {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_fail_local);
        } else {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_fail_fetch);
        }
    }

    @Override
    public void onMakeCoffeeStart() {
        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
        int num = mItemIndex + 1;
        if (item != null) {
            String coffeeName = null;
            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                if (!TextUtils.isEmpty(item.getItemNameen())) {
                    coffeeName = getNumEn(num) + item.getItemNameen();
                    mMakeCoffeeTip.setTextSize(24);
                    mMakeCoffee_fetchtip.setTextSize(24);
                } else {
                    coffeeName = getNumEn(num) + item.getItemName();
                    mMakeCoffeeTip.setTextSize(24);
                    mMakeCoffee_fetchtip.setTextSize(24);
                }
            } else {
                coffeeName = "第" + num + "杯" + item.getItemName();
                mMakeCoffeeTip.setTextSize(30);
                mMakeCoffee_fetchtip.setTextSize(30);
            }
//            if (item.isAddIce()) {
//                coffeeName = coffeeName + "冷饮";
//            }
            mMakeCoffeeName.setText(coffeeName);
            mMakeCoffeeTip.setText(getString(R.string.make_coffee_status));
        } else {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_making);
        }

        mMakeCoffee_layout.setVisibility(View.VISIBLE);
        mMakeCoffee_fetchcup.setVisibility(View.GONE);

        isCancel = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_make_coffee_ex);

        mPresenter = new NewMakeCoffeeExPresenter(this);
        mUIHandler = new SafeHandler(mPresenter);
        mContext = this;
        initView();
        initTimer();
        proceedExtra();

        //LogUtil.vendor();

    }

    private void initTimer() {

        mFecthTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                mPresenter.setFecthTimer(value);
            }
        });
        mMakeCoffeePortTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                mPresenter.setMakeCoffeePortTimer(value);
            }
        });
        mMakeCoffeeTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                mPresenter.setMakeCoffeeTimer(value);
            }
        });


        mQueryPickTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                if (value > 0) {
                    LogUtil.vendor("pick cup status count:" + value);
                    if (isFinish) {
//                        quitPickUpModeIfNeeded();
                    } else {
                        mPresenter.makeCoffee();
                    }
                } else {
                    if (!isFinish) {
                        // 2016/11/7 部分咖啡取货超时
                        LogUtil.vendor("[Rollback] part coffee take out of time");
//						LogUtil.e(TAG, "part coffee take out of time--rollback ");
                        List<Integer> errors = new ArrayList<Integer>();
                        errors.add(MachineStatusCode.ALREADY_HAVE_CUP);
                        mPresenter.setFailed(errors);
                    }
//					mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
                    LogUtil.vendor("[CheckPickCup] please pick up cup and continue");
//                    cancelPickUpTimer();
                    if (!MyApplication.Instance().getDesk()) {
                        quitPickUpModeIfNeeded();
                    }
                }
            }
        });

        mLastCupTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {
            @Override
            public void currentInterval(int value) {
                if (MyApplication.Instance().getDesk() && (value % 5 == 0)) {
                    SerialPortDataWritter.writeDataCoffee(CoffeeMachineInstruction.CHECK_CUP);
                }
                if (!MyApplication.Instance().getDesk()) {
                    mMakeCoffee_timer.setText(String.format(getString(R.string.pay_timer_tip), value));
                }

                LogUtil.vendor("last cup timer" + value);
                if (value <= 0) {
                    HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
                }
            }
        });

//        mQueryPickTimer = new Timer();


    }

    private void startLastCupTimer() {
        mLastCupTimer.startCountDownTimer(MAX_WAIT_LAST_CUP_TIME, 1000, 1000);
    }

    private void stopLastCupTimer() {
        mLastCupTimer.cancelCountDownTimer();
    }

    @Override
    public void onMakeCoffeePortTimeOut(int value) {

//          2016/11/11 下位机请求超时
        if (value <= 0) {
            mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_PORT_TIMEOUT);
        }
    }

    @Override
    public void onMakeCoffeeTimeRecord(int value) {
        //  2016/11/11 记录咖啡机打咖啡的制作时间
        LogUtil.vendor("Coffee Maker Time Left " + value);
        if (value <= 0) {

            mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_TIMEOUT);
        }
    }

    @Override
    public void onFetchTimeOut(int value) {
        mMakeCoffee_timer.setText(String.format(getString(R.string.pay_timer_tip), value));


        if ((value <= 0) && isCancel) {
            LogUtil.vendor("[Rollback] part coffee take out of time");
//						LogUtil.e(TAG, "part coffee take out of time--rollback ");

            if (MyApplication.Instance().getDesk()) {
                List<Integer> errors = new ArrayList<Integer>();
                errors.add(MachineStatusCode.ALREADY_HAVE_CUP);
                mPresenter.setFailed(errors);
            }

            HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
        }
    }

    private void initView() {
        mMakeCoffee_layout = (LinearLayout) findViewById(R.id.make_coffee_layout);
        mMakeCoffee_fetchcup = (LinearLayout) findViewById(R.id.make_coffee_fetchcup);
        mMakeCoffee_fetchtip = (TextView) findViewById(R.id.make_coffee_fetchtip);
        mMakeCoffee_continue_btn = (Button) findViewById(R.id.make_coffee_continue_btn);
        mMakeCoffee_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation cycleAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_info);
                mMakeCoffee_fetchtip.startAnimation(cycleAnim);
            }
        });
        mMakeCoffee_timer = (TextView) findViewById(R.id.make_coffee_timer);
        mMakeCoffee_layout.setVisibility(View.VISIBLE);
        mMakeCoffee_fetchcup.setVisibility(View.GONE);
        isCancel = false;
        mMakeCoffeeTip = (TextView) findViewById(R.id.make_coffee_tip);
        mMakeCoffeeName = (TextView) findViewById(R.id.make_coffee_name);
        mListView = (TListView) findViewById(R.id.make_coffee_list);
        // video adv
        String videoTarget = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/vendor/video/adv_1.mp4";
        videoPath = StorageUtil.getReadPath("adv_1.mp4", videoTarget);
        mSurfaceView = (SurfaceView) findViewById(R.id.make_coffee_adv);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        mAdvImg = (ImageView) findViewById(R.id.make_coffee_adv_img);
        mFetchImg = (ImageView) findViewById(R.id.make_coffee_fetchimg);


        rlMakecoffeeThumbup = (RelativeLayout) findViewById(R.id.rlMakecoffeeThumbup);
        rlMakecoffeeKeepup = (RelativeLayout) findViewById(R.id.rlMakecoffeeKeepup);
        rlMakecoffeeThumbdown = (RelativeLayout) findViewById(R.id.rlMakecoffeeThumbdown);
        ivMakecoffeeThumbupCheck = (ImageView) findViewById(R.id.ivMakecoffeeThumbupCheck);
        ivMakecoffeeKeepupCheck = (ImageView) findViewById(R.id.ivMakecoffeeKeepupCheck);
        ivMakecoffeeThumbdownCheck = (ImageView) findViewById(R.id.ivMakecoffeeThumbdownCheck);

        rlMakecoffeeThumbup.setOnClickListener(this);
        rlMakecoffeeKeepup.setOnClickListener(this);
        rlMakecoffeeThumbdown.setOnClickListener(this);

        tvMakeCoffeeCommentTitle = (TextView) findViewById(R.id.textView2);

        if (MyApplication.Instance().getDesk()) {
            ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg_desk, mFetchImg);
        } else {
            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg_en, mFetchImg);
            } else {
                ImageLoaderTool.disPlayLocalGif(NewMakeCoffeeExActivity.this, R.drawable.make_coffee_fetchbg, mFetchImg);
            }
        }


    }

    private void proceedExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            mOrderContent = (OrderContent) intent.getSerializableExtra(ORDER_CONTENT);
            mOrderLocal = intent.getBooleanExtra(ORDER_LOCAL, true);
            if (mOrderContent == null || mOrderContent.getItemSize() <= 0) {
                mPresenter.showToast(R.string.make_coffee_order_is_null);
                mPresenter.encounterError();
                return;
            } else {
                showCoffeeList();
                mPresenter.makeCoffee();
            }
        } else {
            mPresenter.encounterError();
        }

    }

    @Override
    public void makeCoffee() {
        startMakeCoffeePortTimer();
        // send order to machine
        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
        if (item != null) {
            ArrayList<CoffeeDosingInfo> dosingList = item.getDosings();
            if (dosingList.size() < 6) {
                for (int k = 0; k < dosingList.size(); k++) {
                    if (dosingList.get(k).getValue() == 0 && dosingList.get(k).getWater() == 0) {
                        dosingList.get(k).setOrder(100);
                        dosingList.get(k).setId(0);
                    }
                }
                for (int l = dosingList.size(); l < 6; l++) {
                    CoffeeDosingInfo info = new CoffeeDosingInfo();
                    info.setId(0);
                    info.setOrder(100);
                    info.setValue(0);
                    dosingList.add(info);
                }
            }
            Collections.sort(dosingList);
            mCurrentDosingList = dosingList;
            if (mCurrentDosingList != null) {
                // machine status
                mMachineStatus = MakeCoffeeExActivity.CoffeeMachineStatus.READY;
                // generate make coffee order
                int pipleWashWater = 0;
                if (TimeUtil.getNow_millisecond() - MyApplication.Instance().getLastWashPipleTime()
                        >= 3 * 60 * 60 * 1000) {
                    pipleWashWater = 15;
                    MyApplication.Instance().setLastWashPipleTime(TimeUtil.getNow_millisecond());
                    isNeedWashCurrent = true;
                } else {
                    isNeedWashCurrent = false;
                }

                IMixedDrinksInstruction md;
                    md = new ConpMixedDrinksInstruction(
                            mCurrentDosingList.get(0).getId(),
                            mCurrentDosingList.get(1).getId(),
                            mCurrentDosingList.get(2).getId(),
                            mCurrentDosingList.get(3).getId(),
                            mCurrentDosingList.get(4).getId(),
                            mCurrentDosingList.get(5).getId(),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(0).getValue(), mCurrentDosingList.get(0).getFactor()),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(1).getValue(), mCurrentDosingList.get(1).getFactor()),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(2).getValue(), mCurrentDosingList.get(2).getFactor()),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(3).getValue(), mCurrentDosingList.get(3).getFactor()),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(4).getValue(), mCurrentDosingList.get(4).getFactor()),
                            MachineMaterialMap.transferToMachine(mCurrentDosingList.get(5).getValue(), mCurrentDosingList.get(5).getFactor()),
                            mCurrentDosingList.get(0).getWater(),
                            mCurrentDosingList.get(1).getWater(),
                            mCurrentDosingList.get(2).getWater(),
                            mCurrentDosingList.get(3).getWater(),
                            mCurrentDosingList.get(4).getWater(),
                            mCurrentDosingList.get(5).getWater(),
                            pipleWashWater,
                            mCurrentDosingList.get(0).getStirtime(),
                            mCurrentDosingList.get(1).getStirtime(),
                            mCurrentDosingList.get(2).getStirtime(),
                            mCurrentDosingList.get(3).getStirtime(),
                            mCurrentDosingList.get(4).getStirtime(),
                            mCurrentDosingList.get(5).getStirtime()
                    );

//                MixedDrinksInstruction md = new MixedDrinksInstruction(
//                        mCurrentDosingList.get(0).getId(),
//                        mCurrentDosingList.get(1).getId(),
//                        mCurrentDosingList.get(2).getId(),
//                        mCurrentDosingList.get(3).getId(),
//                        mCurrentDosingList.get(4).getId(),
//                        mCurrentDosingList.get(5).getId(),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(0).getValue(), mCurrentDosingList.get(0).getFactor()),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(1).getValue(), mCurrentDosingList.get(1).getFactor()),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(2).getValue(), mCurrentDosingList.get(2).getFactor()),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(3).getValue(), mCurrentDosingList.get(3).getFactor()),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(4).getValue(), mCurrentDosingList.get(4).getFactor()),
//                        MachineMaterialMap.transferToMachine(mCurrentDosingList.get(5).getValue(), mCurrentDosingList.get(5).getFactor()),
//                        mCurrentDosingList.get(0).getWater(),
//                        mCurrentDosingList.get(1).getWater(),
//                        mCurrentDosingList.get(2).getWater(),
//                        mCurrentDosingList.get(3).getWater(),
//                        mCurrentDosingList.get(4).getWater(),
//                        mCurrentDosingList.get(5).getWater(),
//                        pipleWashWater);

                String srcStr = md.getMixedDrinksOrder(item.isAddIce());

                if (BuildConfig.SERIALPORT_SYSNC) {
                    SerialPortDataWritter.writeDataCoffee(srcStr);
                } else {
                    LogUtil.vendor(TAG + srcStr);
                }

            } else {
                cancelMakeCoffeePortTimer();
                LogUtil.e(TAG, "Make Coffee Error: current dosing list is null");
            }
        } else {
            cancelMakeCoffeePortTimer();
            LogUtil.e(TAG, "Make Coffee Error: OrderContentItem is null");
        }
    }


    private void cancelMakeCoffeePortTimer() {
        mMakeCoffeePortTimer.cancelCountDownTimer();
    }

    private void startMakeCoffeePortTimer() {
        mMakeCoffeePortTimer.startCountDownTimer(MAKE_COFFEE_PORT_TIME, 1000, 1000);
    }

    private void showCoffeeList() {
        for (int i = 0; i < mOrderContent.getItemSize(); i++) {
            MakeCoffeeItem item = new MakeCoffeeItem();
            item.setStatus(MakeCoffeeItem.STATUS_WAITING);
            OrderContentItem orderContentItem = mOrderContent.getOrderContentItemByIndex(i);
            item.setOrderItem(orderContentItem);
            item.setNum(i + 1);
            mMakeCoffeeItems.add(item);
        }

        Map<Integer, Class> viewHolders = new HashMap<Integer, Class>();
        viewHolders.put(0, MakeCoffeeViewHolder.class);
        mAdapter = new TAdapter(this, this, viewHolders, mMakeCoffeeItems);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onShowToast(int stringResId) {
        ToastUtil.showToast(this, stringResId);
    }

    @Override
    public void onEncounterError() {
        //  2016/11/11 获取制作咖啡列表失败
        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
        HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE) {

                cancelMakeCoffeePortTimer();

                final String res = remote.getBody();
                LogUtil.vendor(TAG + " make coffee result:" + res);
                if (res.length() == 14) { // 制作咖啡进程
//                makeCoffeeOnceMore = false;
//                mQueryPickTimer.cancelCountDownTimer();
//                cancelPickUpTimer();
//                quitPickUpModeIfNeeded();
                    int result = CoffeeMachineResultProcess.processMakeCoffeeResult(res);
                    if (result == 1) {
                        // 机器成功开始打咖啡
                        isFinish = false;
                        mMachineStatus = MakeCoffeeExActivity.CoffeeMachineStatus.PROCESSING;
                        // start timeout timer
                        startMakeCoffeeTimer();
                        // 退出检测取杯模式
                        if (!MyApplication.Instance().getDesk()) {
                            quitPickUpModeIfNeeded();
                        }
                        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_START);
                        updateListStatus(mItemIndex, MakeCoffeeItem.STATUS_MAKING);
                        // 更新到本地数据库中
                        try {
                            OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                            updateCoffeeIndent(mOrderContent.getOrderID(), Integer.parseInt(item.getItemID()),
                                    CoffeeIndent.STATUS_COFFEE_START);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (result == 2) {
                        // 机器成功完成打咖啡操作
                        mMachineStatus = MakeCoffeeExActivity.CoffeeMachineStatus.READY;
                        // 清除制作咖啡超时Timer
                        cancelMakeCoffeeTimer();
                        // 更新最后清洗时间
                        MyApplication.Instance().setLastWashPipleTime(TimeUtil.getNow_millisecond());
                        updateListStatus(mItemIndex, MakeCoffeeItem.STATUS_SUCCESS);
                        // 更新到本地数据库中
                        try {
                            OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                            updateCoffeeIndent(mOrderContent.getOrderID(), Integer.parseInt(item.getItemID()),
                                    CoffeeIndent.STATUS_COFFEE_DONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 更新库存
                        updateStock(mCurrentDosingList);
                        // 判断完成OR继续

                        //  2016/11/17 Debug log
                        LogUtil.vendor("mItemIndex:" + mItemIndex + ",mOrderContent.getItemSize():" + (mOrderContent.getItemSize()));
                        if (mItemIndex < (mOrderContent.getItemSize() - 1)) {
                            Message message = new Message();
                            message.what = MSG_UI_MAKE_COFFEE_SUCCESS;
                            OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                                message.obj = item.getItemNameen() + " ";
                            } else {
                                message.obj = item.getItemName();
                            }
                            mUIHandler.sendMessage(message);
                        } else {
                            isFinish = true;
                            Message message = new Message();
                            message.what = MSG_UI_MAKE_COFFEE_SUCCESS_ALL;
                            OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                                if (!TextUtils.isEmpty(item.getItemNameen())) {
                                    message.obj = item.getItemNameen() + " ";
                                } else {
                                    message.obj = item.getItemName();
                                }
                            } else {
                                message.obj = item.getItemName();
                            }
                            mUIHandler.sendMessage(message);
                        }

//                    if (mItemIndex >= mOrderContent.getItemSize() - 1) {
//
//                        isFinish = true;
//                        Message message = new Message();
//                        message.what = MSG_UI_MAKE_COFFEE_SUCCESS_ALL;
//                        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
//                        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
//                            message.obj = item.getItemNameen() + " ";
//                        } else {
//                            message.obj = item.getItemName();
//                        }
//                        mUIHandler.sendMessage(message);
//                    } else {
//                        Message message = new Message();
//                        message.what = MSG_UI_MAKE_COFFEE_SUCCESS;
//                        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
//                        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
//                            message.obj = item.getItemNameen() + " ";
//                        } else {
//                            message.obj = item.getItemName();
//                        }
//                        mUIHandler.sendMessage(message);
//                    }
                        mItemIndex++;
                        if (!MyApplication.Instance().getDesk()) {
                            checkPickCupStatus();
                        }
                    } else {
                        cancelMakeCoffeeTimer();
                        if (!MyApplication.Instance().getDesk()) {
                            quitPickUpModeIfNeeded();
                        }
                        List<Integer> errors = new ArrayList<Integer>();
                        errors.add(MachineStatusCode.UNKNOW_ERROR);

                        LogUtil.vendor("setMaintenance 11111");
                        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.UNKNOW_ERROR, true);
                        MyApplication.Instance().setWaitMaintenance(true);
                        setFailed(errors);
                    }
                } else if (res.length() == 16) { //错误报告
                    cancelMakeCoffeeTimer();
                    List<Integer> status = CoffeeMachineResultProcess.processMakeCoffeeErrorResult(res);
                    if (status.contains(MachineStatusCode.ALREADY_HAVE_CUP)) {
                        if (!MyApplication.Instance().getDesk()) {
                            if (!mWaitPickMode.get()) {
                                checkPickCupStatus();

                                mMakeCoffee_fetchtip.setText(R.string.make_coffee_fetchcup);
                                mMakeCoffee_continue_btn.setBackgroundResource(R.drawable.make_coffee_continue_selector);
                                mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
                                LogUtil.vendor(TAG + "please pick up cup and continue");
                            }
                        }
                    } else if (status.contains(MachineStatusCode.NO_CUP)) {

                        if (!MyApplication.Instance().getDesk()) {
                            //  2016/11/10 无杯的情况下重试一次
                            quitPickUpModeIfNeeded();
                            if (makeCoffeeOnceMore) {
//                        ToastUtil.showToast(NewMakeCoffeeExActivity.this, " Make Coffee One More time");
                                LogUtil.vendor(TAG + "make Coffee Once More:" + makeCoffeeOnceMore);
//                        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
//                        mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_ONCE_MORE);
                                mUIHandler.sendEmptyMessageDelayed(MSG_MAKE_COFFEE_ONCE_MORE, 100);

                                makeCoffeeOnceMore = false;
                            } else {

                                if (!isFinish) {
                                    LogUtil.vendor(TAG + "make Coffee Once More:" + makeCoffeeOnceMore);
//						setFailed(status);
                                    List<Integer> errors = new ArrayList<Integer>();
                                    errors.add(MachineStatusCode.NO_CUP);

                                    LogUtil.vendor("setMaintenance 222222");
                                    MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.NO_CUP, true);
                                    MyApplication.Instance().setWaitMaintenance(true);
                                    setFailed(errors);
                                }
                            }
                        } else {
                            if (!isFinish) {
                                mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_DESK_NO_CUP);
                            }
                        }

                    } else if (status.contains(MachineStatusCode.MACHINE_WARM_UP)) {

                        if (!MyApplication.Instance().getDesk()) {
                            quitPickUpModeIfNeeded();
                        }
                        List<Integer> errors = new ArrayList<Integer>();

                        errors.add(MachineStatusCode.MACHINE_WARM_UP);

                        MyApplication.Instance().setWaitMaintenanceCode(MachineStatusCode.MACHINE_WARM_UP, true);
                        setFailed(errors);
                    } else {
                        if (!MyApplication.Instance().getDesk()) {
                            quitPickUpModeIfNeeded();
                        }

                        mPresenter.setFailed(mPresenter.selectFailed(status));
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK_CUP) {
                final String res = remote.getBody();
                int code = CoffeeMachineResultProcess.processCheckCupResult(res);
                if (code == 2) {
                    stopLastCupTimer();
                    HomePageActivity.start(NewMakeCoffeeExActivity.this, isRefreshMenu, 0);
                }

            }
        }
//        else if (remote.getWhat() == ITranCode.ACT_COFFEE) {
//            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {
//
//                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
//                if (result != null && result.getResCode() == 200) {
//                    String type = result.getType();
//                    if (type.equals("102")) {
//
//                        GetCoffeeInfo info = new GetCoffeeInfo();
//                        info.setUid(U.getMyVendorNum());
//                        executeBackground(info.toRemote());
//                    }
//                }
//            }
//        }

    }

    private void checkPickCupStatus() {
        mWaitPickMode.compareAndSet(false, true);
        mQueryPickTimer.startCountDownTimer(CHECK_PICK_STATUS_MAX_WAIT_TIME, 5000, 5000);

    }

    private void rollBackOrder(List<Integer> errors) {

        if (mOrderLocal) {
            RollbackCoffeeIndentCart info = new RollbackCoffeeIndentCart();
            info.setUid(U.getMyVendorNum());
            String orderID = mOrderContent.getOrderID();
            info.setPayIndent(orderID);
            JSONArray array = new JSONArray();
            for (int i = mItemIndex; i < mOrderContent.getItemSize(); i++) {
                OrderContentItem orderContent = mOrderContent.getOrderContentItemByIndex(i);
                array.add(orderContent.getGoodID());
            }
            info.setCoffeeIndents(array.toString());

            String reason = "";
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) {
                    reason += ";";
                }
                reason += errors.get(i);
            }
            info.setReason(reason);
            execute(info.toRemote());
        } else {
            ReportErrorFetchInfo info = new ReportErrorFetchInfo();
            info.setUid(U.getMyVendorNum());
            // goodIds
            JSONArray goodIds = new JSONArray();
            for (int i = mItemIndex; i < mOrderContent.getItemSize(); i++) {
                OrderContentItem orderContent = mOrderContent.getOrderContentItemByIndex(i);
                goodIds.add(orderContent.getGoodID());
            }
            info.setGoodIds(goodIds.toString());
            // reasons
            JSONArray reasons = new JSONArray();
            for (int i = 0; i < errors.size(); i++) {
                reasons.add(errors.get(i));
            }
            info.setCodes(reasons.toString());
            execute(info.toRemote());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setMakeCoffeeStatus(true);
    }

    @Override
    public void onStop() {
        setMakeCoffeeStatus(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelFecthTimer();
        MyApplication.Instance().setMakingCoffee(false);
        stopMediaPlayer();
    }

    private void stopMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void cancelFecthTimer() {
        isCancel = false;
        mFecthTimer.cancelCountDownTimer();
    }

    private void setMakeCoffeeStatus(boolean status) {
        MyApplication.Instance().setMakingCoffee(status);
    }

    private void updateStock(ArrayList<CoffeeDosingInfo> dosingList) {
        if (dosingList == null) {
            return;
        }

        // resume
        double resumeBox1 = 0;
        double resumeBox2 = 0;
        double resumeBox3 = 0;
        double resumeBox4 = 0;
        double resumeBox5 = 0;
        double resumeWater = isNeedWashCurrent ? 75 : 0;
        double resumeBean = 0;
        double resumeCupNum = 1;

        Iterator<CoffeeDosingInfo> it = dosingList.iterator();
        while (it.hasNext()) {
            CoffeeDosingInfo info = it.next();
            int id = info.getId();
            double value = info.getValue();
            if (id == MachineMaterialMap.MATERIAL_BOX_1) {
                resumeBox1 += value;
            } else if (id == MachineMaterialMap.MATERIAL_BOX_2) {
                resumeBox2 += value;
            } else if (id == MachineMaterialMap.MATERIAL_BOX_3) {
                resumeBox3 += value;
            } else if (id == MachineMaterialMap.MATERIAL_BOX_4) {
                resumeBox4 += value;
            } else if (id == MachineMaterialMap.MATERIAL_BOX_5) {
                resumeBox5 += value;
            } else if (id == MachineMaterialMap.MATERIAL_COFFEE_BEAN) {
                resumeBean += value;
            }

            int water = info.getWater();
            resumeWater += water;
        }
        LogUtil.vendor("calculate resume: [" + resumeWater + "," + resumeBox1 + "," + resumeBox2 + "," + resumeBox3 + "," + resumeBox4
                + "," + resumeBox5 + "," + resumeBean + "," + resumeCupNum + "]");

        // stock
        double stockBox1 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        double stockBox5 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
        double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double stockCupNum = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        LogUtil.vendor("calculate stock: [" + stockWater + "," + stockBox1 + "," + stockBox2 + "," + stockBox3 + "," + stockBox4
                + "," + stockBox5 + "," + stockBean + "," + stockCupNum + "]");

        double leftWater = stockWater - resumeWater;
        BigDecimal leftWaterBD = new BigDecimal(leftWater);
        leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftWater < 0) {
            leftWater = 0;
        }
        double leftBox1 = stockBox1 - resumeBox1;
        BigDecimal leftBox1BD = new BigDecimal(leftBox1);
        leftBox1 = leftBox1BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox1 < 0) {
            leftBox1 = 0;
        }
        double leftBox2 = stockBox2 - resumeBox2;
        BigDecimal leftBox2BD = new BigDecimal(leftBox2);
        leftBox2 = leftBox2BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox2 < 0) {
            leftBox2 = 0;
        }
        double leftBox3 = stockBox3 - resumeBox3;
        BigDecimal leftBox3BD = new BigDecimal(leftBox3);
        leftBox3 = leftBox3BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox3 < 0) {
            leftBox3 = 0;
        }
        double leftBox4 = stockBox4 - resumeBox4;
        BigDecimal leftBox4BD = new BigDecimal(leftBox4);
        leftBox4 = leftBox4BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox4 < 0) {
            leftBox4 = 0;
        }
        double leftBox5 = stockBox5 - resumeBox5;
        BigDecimal leftBox5BD = new BigDecimal(leftBox5);
        leftBox5 = leftBox5BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBox5 < 0) {
            leftBox5 = 0;
        }
        double leftBean = stockBean - resumeBean;
        BigDecimal leftBeanBD = new BigDecimal(leftBean);
        leftBean = leftBeanBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (leftBean < 0) {
            leftBean = 0;
        }
        double leftCupNum = stockCupNum - resumeCupNum;

        // update local stock
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater),
                MachineMaterialMap.MATERIAL_WATER);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox1),
                MachineMaterialMap.MATERIAL_BOX_1);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox2),
                MachineMaterialMap.MATERIAL_BOX_2);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox3),
                MachineMaterialMap.MATERIAL_BOX_3);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox4),
                MachineMaterialMap.MATERIAL_BOX_4);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox5),
                MachineMaterialMap.MATERIAL_BOX_5);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBean),
                MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftCupNum),
                MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);

        // check stock, if reach alarm value, report server
        /*
        if(leftWater <= MachineMaterialMap.MATERIAL_WATER_ALARM_VALUE
                || leftBox1 <= MachineMaterialMap.MATERIAL_BOX_1_ALARM_VALUE
                || leftBox2 <= MachineMaterialMap.MATERIAL_BOX_2_ALARM_VALUE
                || leftBox3 <= MachineMaterialMap.MATERIAL_BOX_3_ALARM_VALUE
                || leftBox4 <= MachineMaterialMap.MATERIAL_BOX_4_ALARM_VALUE
                || leftBean <= MachineMaterialMap.MATERIAL_COFFEE_BEAN_ALARM_VALUE
                || leftCupNum <= MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_ALARM_VALUE){
            // report server
            List<Integer> status = new ArrayList<Integer>();
            MachineStatusReportInfo info = new MachineStatusReportInfo();
            info.setUid(U.getMyVendorNum());
            info.setTimestamp(TimeUtil.getNow_millisecond());
            status.add(MachineStatusCode.MATERIAL_DEFICIENCY);
            info.setStatus(status);
            execute(info.toRemote());
        }*/

        // check stock, if reach limit value, refresh menu
        if (leftWater <= MachineMaterialMap.MATERIAL_WATER_LIMIT_VALUE
                || leftBox1 <= MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE
                || leftBox2 <= MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE
                || leftBox3 <= MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE
                || leftBox4 <= MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE
                || leftBox5 <= MachineMaterialMap.MATERIAL_BOX_5_LIMIT_VALUE
                || leftBean <= MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE
                || leftCupNum <= MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE) {
            isRefreshMenu = true;
        }
    }

    private void updateCoffeeIndent(String orderID, int coffeeID, int status) {
        CoffeeIndent para = new CoffeeIndent();
        para.setCoffeeindent(orderID);
        para.setCoffeeid(coffeeID);
        para.setDosing("");

        CoffeeIndent oldIndent = CoffeeIndentDbHelper.getCoffeeIndent(para);
        if (oldIndent != null) {
            oldIndent.setStatus(status);
            CoffeeIndentDbHelper.updateCoffeeIndentStatus(oldIndent);
        } else {
            CoffeeIndent newIndent = new CoffeeIndent();
            newIndent.setCoffeeindent(orderID);
            newIndent.setCoffeeid(coffeeID);
            newIndent.setDosing("");
            newIndent.setStatus(status);
            CoffeeIndentDbHelper.insertCoffeeIndent(newIndent);
        }

        LogUtil.vendor("update coffee indent status to DB ->  " + "orderID:" + orderID
                + "; coffeeID:" + coffeeID + "; status:" + status);
    }

    private void updateListStatus(int index, int status) {
        try {
            if (mMakeCoffeeItems != null && index < mMakeCoffeeItems.size()) {
                mMakeCoffeeItems.get(index).setStatus(status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "SOMETHING WRONG WITH updateListStatus()");
        }
    }

    private void quitPickUpModeIfNeeded() {
        if (mWaitPickMode.get()) {
            cancelPickUpTimer();
        }
    }

    private void cancelPickUpTimer() {
        mQueryPickTimer.cancelCountDownTimer();
        mWaitPickMode.compareAndSet(true, false);
    }

    private void startMakeCoffeeTimer() {
        mMakeCoffeeTimer.startCountDownTimer(MAKE_COFFEE_MAX_WAIT_TIME, 1000, 1000);
    }

    private void cancelMakeCoffeeTimer() {
        mMakeCoffeeTimer.cancelCountDownTimer();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDisplay(surfaceHolder);

        if (!TextUtils.isEmpty(videoPath)) {
            playVideo();
            mAdvImg.setVisibility(View.GONE);
        } else {
            mSurfaceView.setVisibility(View.GONE);
            mAdvImg.setVisibility(View.VISIBLE);
        }

    }

    protected void playVideo() {
        try {
            LogUtil.e("DEBUG", "PLAY VIDEO");
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(videoPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                setMediaPlayerListener();
                mMediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("DEBUG", "MEDIA PLAYER HAVE EXCEPTION");
        }
    }

    private void setMediaPlayerListener() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.vendor("MEDIA PLAYER ON COMPLETION!");
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("DEBUG", "MEDIA PLAYER HAVE SOMETHING WRONG");
                return true;
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();// 播放视频
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onViewHolderLongClick(View view, TListItem item) {
        return false;
    }

    @Override
    public boolean onViewHolderClick(View view, TListItem item) {
        if (item instanceof MakeCoffeeItem) {
            // update status
            MakeCoffeeItem makeCoffeeItem = (MakeCoffeeItem) item;
            makeCoffeeItem.setStatus(MakeCoffeeItem.STATUS_MAKING);
            mAdapter.notifyDataSetChanged();
            // send make coffee order again
            //  2016/11/14 send make coffee order again
            mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_START);
            mPresenter.makeCoffee();

            return true;
        }

        return false;
    }

    @Override
    public void onItemChangeNotify() {

    }




    @Override
    public void onClick(View v) {
        int commentStar = -1;
        switch (v.getId()) {
            case R.id.rlMakecoffeeThumbup:
                commentStar = 0;
                break;
            case R.id.rlMakecoffeeKeepup:
                commentStar = 1;
                break;
            case R.id.rlMakecoffeeThumbdown:
                commentStar = 2;
                break;

            default:
                break;
        }
        if (isFirstClick) {
            startTitleAnimation();
            initCommentView();
            isFirstClick = false;
            setCommentSelect(v.getId());
            sendComment(commentStar);
        } else {
            ToastUtil.showToast(NewMakeCoffeeExActivity.this, R.string.str_make_coffee_commented);
        }
    }

    private void startTitleAnimation() {
        Animation out = AnimationUtils.loadAnimation(NewMakeCoffeeExActivity.this, R.anim.push_up_out);
        final Animation in = AnimationUtils.loadAnimation(NewMakeCoffeeExActivity.this, R.anim.push_up_in);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvMakeCoffeeCommentTitle.setText(R.string.str_make_coffee_thanks_for_comment);
                tvMakeCoffeeCommentTitle.startAnimation(in);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvMakeCoffeeCommentTitle.startAnimation(out);
    }


    private void setCommentSelect(int id) {
        switch (id) {
            case R.id.rlMakecoffeeThumbup:
                rlMakecoffeeThumbup.setSelected(true);
                ivMakecoffeeThumbupCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.rlMakecoffeeKeepup:
                rlMakecoffeeKeepup.setSelected(true);
                ivMakecoffeeKeepupCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.rlMakecoffeeThumbdown:
                rlMakecoffeeThumbdown.setSelected(true);
                ivMakecoffeeThumbdownCheck.setVisibility(View.VISIBLE);
                break;

        }

    }

    private void sendComment(int commentStar) {

        String host;
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        } else if (AppConfig.BUILD_SERVER == AppConfig.Build.TEST) {
            host = StringUtil.HOST_TEST;
        } else {
            host = StringUtil.HOST_LOCAL;
        }
        String url = host + "vendingMachines/userEvaluation";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("vendingMachineId", U.getMyVendorNum());
        param.put("token", generateToken());
        param.put("rate", commentStar);
        param.put("orderId", mOrderContent.getOrderID());
        NetLoaderTool.INetLoaderListener listener = new NetLoaderTool.INetLoaderListener() {
            @Override
            public void onSuccess(String success) {
                LogUtil.vendor(TAG + "success");
            }

            @Override
            public void onFailure(String error) {
                LogUtil.vendor(TAG + "error");
            }
        };
        NetLoaderTool.post(url, param, listener);

    }

    private String generateToken() {
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


    private void initCommentView() {
        rlMakecoffeeThumbup.setSelected(false);
        rlMakecoffeeKeepup.setSelected(false);
        rlMakecoffeeThumbdown.setSelected(false);
    }

    static class SafeHandler extends Handler {
        WeakReference<NewMakeCoffeeExPresenter> thePresenter;

        public SafeHandler(NewMakeCoffeeExPresenter thePresenter) {
            this.thePresenter = new WeakReference<NewMakeCoffeeExPresenter>(thePresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            NewMakeCoffeeExPresenter presenter = thePresenter.get();
            if (thePresenter == null) return;
            LogUtil.vendor("msg.what:" + msg.what + ",msg.obj:" + msg.obj);
            switch (msg.what) {
                case MSG_UI_MAKE_COFFEE_START:
                    presenter.onMakeCoffeeStart();
                    break;
                case MSG_UI_MAKE_COFFEE_FAIL:
                    presenter.onMakeCoffeeFail();
                    break;
                case MSG_UI_MAKE_COFFEE_RETRY:
                    presenter.onMakeCoffeeRetry();
                    break;
                case MSG_UI_MAKE_COFFEE_SUCCESS:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_SUCCESS");
                    presenter.onMakeCoffeeSuccess((String) msg.obj);
                    break;
                case MSG_UI_MAKE_COFFEE_SUCCESS_ALL:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_SUCCESS_ALL");
                    presenter.onMakeCoffeeSuccess((String) msg.obj);
                    break;
                case MSG_MAKE_COFFEE_TIMEOUT:
                    presenter.onMakeCoffeeTimeout();
                    break;
                case MSG_MAKE_COFFEE_PORT_TIMEOUT:
                    presenter.onMakeCoffeePortTimeOut();
                    break;
                case MSG_MAKE_COFFEE_ONCE_MORE:
                    presenter.makeCoffee();
                    break;
                case MSG_MAKE_COFFEE_DESK_NO_CUP:
                    presenter.onMakeCoffeeSuccess(MAKE_COFFEE_DESK_NOCUP);
                    break;
            }
        }
    }
}
