package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.adapter.MakeCoffeeViewHolder;
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
import com.jingye.coffeemac.instructions.CoffeeMachineResultProcess;
import com.jingye.coffeemac.instructions.MixedDrinksInstruction;
import com.jingye.coffeemac.serialport.SerialPortDataWritter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.bean.action.ReportErrorFetchInfo;
import com.jingye.coffeemac.service.bean.action.RollbackCoffeeIndentCart;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.CountDownTimer.CountDownCallback;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.StorageUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MakeCoffeeExActivity extends TActivity implements TViewHolder.ViewHolderEventListener,
        Callback {

    public static final String TAG = "[MakeCoffeeExActivity]";
    public static final String ORDER_CONTENT = "order_content";
    public static final String ORDER_LOCAL = "order_local";
    private static final int QUIT_WAIT_TIME = 10;
    private static final int MAKE_COFFEE_MAX_WAIT_TIME = 280;
    private static final int MAKE_COFFEE_MAX_FETCH_TIME = 300;
    private static final int MAKE_COFFEE_PORT_TIME = 30;
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
    private boolean makeCoffeeOnceMore = true;
    private Context mContext;
    //	private CountDownTimer mQuitTimer;
    private CountDownTimer mMakeCoffeeTimer;
    private CountDownTimer mFecthTimer;
    private CountDownTimer mMakeCoffeePortTimer;
    private AtomicBoolean mWaitPickMode = new AtomicBoolean(false);
    private Timer mQueryPickTimer;
    private AtomicInteger mQueryPickCount = new AtomicInteger();
    // 串口相关
    private CoffeeMachineStatus mMachineStatus;
    // 咖啡订单
    private Boolean mOrderLocal;
    private OrderContent mOrderContent;
    private ArrayList<CoffeeDosingInfo> mCurrentDosingList;
    private int mItemIndex = 0;
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
    // 菜单刷新
    private boolean isRefreshMenu = false;

//	private void onQuitCountDown(int value){
//
//		if(value <= 0){
//			HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu,0);
//		}
//	}
    private boolean isFinish = false;
    private boolean isCancel = false;
    private SafeHandler mUIHandler = new SafeHandler(this);

    public static void start(Activity activity, OrderContent orderContent, boolean local) {
        Intent intent = new Intent();
        intent.setClass(activity, MakeCoffeeExActivity.class);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_coffee_cart_ex_layout);
        mContext = this;
        proceedExtra();
        initView();
        initTimer();
        if (checkCoffeeOrders()) {
            showCoffeeList();
            makeCoffee();
        }
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            mOrderContent = (OrderContent) intent.getSerializableExtra(ORDER_CONTENT);
            mOrderLocal = intent.getBooleanExtra(ORDER_LOCAL, true);
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
    }

    private void initTimer() {
//		mQuitTimer = new CountDownTimer(new CountDownCallback() {
//
//			@Override
//			public void currentInterval(int value) {
//				onQuitCountDown(value);
//			}
//		});

        mFecthTimer = new CountDownTimer(new CountDownCallback() {

            @Override
            public void currentInterval(int value) {

                mMakeCoffee_timer.setText(String.format(getString(R.string.pay_timer_tip), value));

                if ((value <= 0) && isCancel) {
                    HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu, 0);
                }
            }
        });
        mMakeCoffeePortTimer = new CountDownTimer(new CountDownCallback() {
            @Override
            public void currentInterval(int value) {

                if (value <= 0) {
                    mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_PORT_TIMEOUT);
                }
            }
        });
    }

    private boolean checkCoffeeOrders() {
        if (mOrderContent == null || mOrderContent.getItemSize() <= 0) {
            ToastUtil.showToast(this, R.string.make_coffee_order_is_null);
            encounterError();

            return false;
        }

        return true;
    }

    private void encounterError() {
        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
        //mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
        HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu, 0);
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

    private void makeCoffee() {

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
                mMachineStatus = CoffeeMachineStatus.READY;
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

                MixedDrinksInstruction md = new MixedDrinksInstruction(
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
                        pipleWashWater);
                String srcStr = md.getMixedDrinksOrder(item.isAddIce());
                SerialPortDataWritter.writeDataCoffee(srcStr);
            } else {
                cancelMakeCoffeePortTimer();
                LogUtil.e(TAG, "Make Coffee Error: current dosing list is null");
            }
        } else {
            cancelMakeCoffeePortTimer();
            LogUtil.e(TAG, "Make Coffee Error: OrderContentItem is null");
        }
    }

    private void startMakeCoffeeTimer() {
//		cancelMakeCoffeeTimer();
        mMakeCoffeeTimer = new CountDownTimer(new CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onMakeCountDown(value);
            }
        });
        mMakeCoffeeTimer.startCountDownTimer(MAKE_COFFEE_MAX_WAIT_TIME, 1000, 1000);
    }

    private void onMakeCountDown(int value) {
        LogUtil.vendor("Coffee Maker Time Left " + value);
        if (value <= 0) {
            mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_TIMEOUT);
        }
    }

    private void cancelMakeCoffeeTimer() {
        if (mMakeCoffeeTimer != null) {
            mMakeCoffeeTimer.cancelCountDownTimer();
            mMakeCoffeeTimer = null;
        }
    }

    private void startMakeCoffeePortTimer() {

//        cancelMakeCoffeePortTimer();

        mMakeCoffeePortTimer.startCountDownTimer(MAKE_COFFEE_PORT_TIME, 1000, 1000);
    }

    private void cancelMakeCoffeePortTimer() {
        if (mMakeCoffeePortTimer != null) {
            mMakeCoffeePortTimer.cancelCountDownTimer();
            mMakeCoffeePortTimer = null;
        }
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

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT
                && remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE) {

            cancelMakeCoffeePortTimer();

            final String res = remote.getBody();
            LogUtil.vendor(TAG + " make coffee result:" + res);
            if (res.length() == 14) { // 制作咖啡进程
                makeCoffeeOnceMore = false;
                int result = CoffeeMachineResultProcess.processMakeCoffeeResult(res);
                if (result == 1) {
                    // 机器成功开始打咖啡
                    isFinish = false;
                    mMachineStatus = CoffeeMachineStatus.PROCESSING;
                    // start timeout timer
                    startMakeCoffeeTimer();
                    // 退出检测取杯模式
                    quitPickUpModeIfNeeded();
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
                    mMachineStatus = CoffeeMachineStatus.READY;
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
                    if (mItemIndex >= mOrderContent.getItemSize() - 1) {

                        isFinish = true;
                        Message message = new Message();
                        message.what = MSG_UI_MAKE_COFFEE_SUCCESS_ALL;
                        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                            message.obj = item.getItemNameen() + " ";
                        } else {
                            message.obj = item.getItemName();
                        }
                        mUIHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = MSG_UI_MAKE_COFFEE_SUCCESS;
                        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
                        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                            message.obj = item.getItemNameen() + " ";
                        } else {
                            message.obj = item.getItemName();
                        }
                        mUIHandler.sendMessage(message);
                    }
                    mItemIndex++;
                    makeCoffeeOnceMore = true;
                    checkPickCupStatus();
                } else {
                    cancelMakeCoffeeTimer();
                    quitPickUpModeIfNeeded();
                    List<Integer> errors = new ArrayList<Integer>();
                    errors.add(MachineStatusCode.UNKNOW_ERROR);

                    LogUtil.vendor("setMaintenance 11111");
                    MyApplication.Instance().setWaitMaintenance(true);
                    setFailed(errors);
                }
            } else if (res.length() == 16) { //错误报告
                cancelMakeCoffeeTimer();
                List<Integer> status = CoffeeMachineResultProcess.processMakeCoffeeErrorResult(res);
                if (status.contains(MachineStatusCode.ALREADY_HAVE_CUP)) {

                    LogUtil.vendor("ALEADY_HAVE_CUP");
                    if (!mWaitPickMode.get()) {
                        checkPickCupStatus();

                        mMakeCoffee_fetchtip.setText(R.string.make_coffee_fetchcup);
                        mMakeCoffee_continue_btn.setBackgroundResource(R.drawable.make_coffee_continue_selector);
                        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
                        LogUtil.vendor(TAG + "please pick up cup and continue");
                    }
                }
                else if (status.contains(MachineStatusCode.NO_CUP)) {
                    //  2016/11/10 无杯的情况下重试一次
                    if (makeCoffeeOnceMore) {
                        LogUtil.vendor(TAG + "make Coffee Once More:" + makeCoffeeOnceMore);
//						mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
//                        makeCoffee();
						mUIHandler.sendEmptyMessage(MSG_MAKE_COFFEE_ONCE_MORE);
                        makeCoffeeOnceMore = false;
                    } else {
                        LogUtil.vendor(TAG + "make Coffee Once More:" + makeCoffeeOnceMore);
//						setFailed(status);
                        cancelMakeCoffeeTimer();
                        List<Integer> errors = new ArrayList<Integer>();
                        errors.add(MachineStatusCode.NO_CUP);

                        LogUtil.vendor("setMaintenance 222222");
                        MyApplication.Instance().setWaitMaintenance(true);
                        setFailed(errors);
                    }

                } else {
                    quitPickUpModeIfNeeded();
                    setFailed(status);
                }
            }
        } else if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {

                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String type = result.getType();
//                    if (type.equals("102")) {
//
//                        GetCoffeeInfo info = new GetCoffeeInfo();
//                        info.setUid(U.getMyVendorNum());
//                        executeBackground(info.toRemote());
//                    }
//					else if(type.equals("103")) {
//
//						GetAdvPicsInfo info = new GetAdvPicsInfo();
//						info.setUid(U.getMyVendorNum());
//						execute(info.toRemote());
//					}
                }
            }
        }
    }

    private void checkPickCupStatus() {
        mWaitPickMode.compareAndSet(false, true);
        mQueryPickCount.set(0);
        mQueryPickTimer = new Timer();
        mQueryPickTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (mQueryPickCount.getAndIncrement() < 65) {

                    if (isFinish) {
//                        HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu, 0);
                        cancelPickUpTimer();
                    } else {
                        makeCoffee();
                    }
                } else {
                    if (!isFinish) {
                        // 2016/11/7 部分咖啡取货超时
                        LogUtil.vendor("[Rollback] part coffee take out of time");
//						LogUtil.e(TAG, "part coffee take out of time--rollback ");
                        List<Integer> errors = new ArrayList<Integer>();
                        errors.add(MachineStatusCode.ALREADY_HAVE_CUP);
                        setFailed(errors);
                    }
//					mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
                    LogUtil.vendor("[CheckPickCup] please pick up cup and continue");
                    cancelPickUpTimer();
                }
            }
        }, 5000, 5000);
    }

    private void cancelPickUpTimer() {
        if (mQueryPickTimer != null) {
            mQueryPickTimer.cancel();
            mQueryPickTimer = null;
            mQueryPickCount.set(0);
            mWaitPickMode.compareAndSet(true, false);
        }
    }

    private void quitPickUpModeIfNeeded() {
        if (mWaitPickMode.get()) {
            cancelPickUpTimer();
        }
    }

    private void onMakeCoffeeTimeout() {
        LogUtil.e(TAG, "onMakeCoffeeTimeout()");
        List<Integer> errors = new ArrayList<Integer>();
        errors.add(MachineStatusCode.COFFEE_MAKE_TIME_OUT);
        setFailed(errors);
    }

    private void onMakeCoffeePortTimeout() {
        LogUtil.e(TAG, "onMakeCoffeePortTimeout()");
        cancelMakeCoffeePortTimer();
        cancelMakeCoffeeTimer();
        LogUtil.vendor("setMaintenance 33333");
        MyApplication.Instance().setWaitMaintenance(true);
        List<Integer> errors = new ArrayList<Integer>();
        errors.add(MachineStatusCode.MAKE_COFFEE_PORT_TIMEOUT);
        setFailed(errors);
    }

    private void onMakeCoffeeStart() {
        OrderContentItem item = mOrderContent.getOrderContentItemByIndex(mItemIndex);
        int num = mItemIndex + 1;
        if (item != null) {
            String coffeeName = null;
            if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                coffeeName = getNumEn(num) + item.getItemNameen();
                mMakeCoffeeTip.setTextSize(24);
                mMakeCoffee_fetchtip.setTextSize(24);
            } else {
                coffeeName = "第" + num + "杯" + item.getItemName();
                mMakeCoffeeTip.setTextSize(30);
                mMakeCoffee_fetchtip.setTextSize(30);
            }
            mMakeCoffeeName.setText(coffeeName);
            mMakeCoffeeTip.setText(getString(R.string.make_coffee_status));
        } else {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_making);
        }

        mMakeCoffee_layout.setVisibility(View.VISIBLE);
        mMakeCoffee_fetchcup.setVisibility(View.GONE);

        isCancel = false;

    }

    private void onMakeCoffeeFail() {
        if (mOrderLocal) {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_fail_local);
        } else {
            mMakeCoffeeTip.setText(R.string.make_coffee_status_fail_fetch);
        }
    }

    private void onMakeCoffeeSuccess(String name) {
        mMakeCoffee_layout.setVisibility(View.GONE);
        mMakeCoffee_fetchcup.setVisibility(View.VISIBLE);
        isCancel = true;
        if (mMakeCoffeeTimer != null) {
            mMakeCoffeeTimer.cancelCountDownTimer();
        }

        if (null != name)
            mMakeCoffee_fetchtip.setText(name + getResources().getString(R.string.make_coffee_finish_fetchcup));
        else
            mMakeCoffee_fetchtip.setText(getResources().getString(R.string.make_coffee_finish_fetchcup));
        if (isFinish) {
            mMakeCoffee_timer.setVisibility(View.GONE);
            mMakeCoffee_continue_btn.setBackgroundResource(R.drawable.make_coffee_back_selector);
            mMakeCoffee_continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu, 0);
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
                }
            });
        }
    }

    private void onMakeCoffeeRetry() {

        mMakeCoffee_layout.setVisibility(View.GONE);
        mMakeCoffee_fetchcup.setVisibility(View.VISIBLE);
        if (mMakeCoffeeTimer != null) {
            mMakeCoffeeTimer.cancelCountDownTimer();
            mMakeCoffeeTimer = null;
        }
        isCancel = true;
        mMakeCoffee_timer.setVisibility(View.VISIBLE);
        mFecthTimer.startCountDownTimer(MAKE_COFFEE_MAX_FETCH_TIME, 1000, 1000);
    }

    /***
     * DB OPERATION
     */
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

    /***
     * STOCK OPERATION
     */
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

    /***
     * FAIL OPERATION
     */
    private void setFailed(List<Integer> errors) {
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
        mMachineStatus = CoffeeMachineStatus.READY;
        // quit time
        updateListStatus(mItemIndex, MakeCoffeeItem.STATUS_FAIL);
        mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
        //mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
        HomePageActivity.start(MakeCoffeeExActivity.this, isRefreshMenu, 0);
    }

    private void rollBackOrder(List<Integer> errors) {
        if (errors != null
                && errors.contains(MachineStatusCode.COFFEE_MAKE_TIME_OUT))
            return;

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
//		cancelQuitTimer();
//		cancelMakeCoffeeTimer();
//		cancelFecthTimer();
//		MyApplication.Instance().setMakingCoffee(false);
//		stopMediaPlayer();
//		releaseImageViews();
        super.onStop();
    }

    private void setMakeCoffeeStatus(boolean status) {
        MyApplication.Instance().setMakingCoffee(status);
    }

    private void releaseImageViews() {
        //releaseImageView(mMakeCoffeeLoading);
    }

    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//		cancelQuitTimer();
        cancelMakeCoffeeTimer();
        cancelMakeCoffeePortTimer();
        cancelFecthTimer();
        MyApplication.Instance().setMakingCoffee(false);
        stopMediaPlayer();
        releaseImageViews();
    }

    public void cancelFecthTimer() {

        isCancel = false;
        if (mFecthTimer != null) {
            mFecthTimer.cancelCountDownTimer();
        }
    }

//	public void cancelQuitTimer() {
//		if (mQuitTimer != null) {
//			mQuitTimer.cancelCountDownTimer();
//		}
//	}

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
            mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_START);
            makeCoffee();

            return true;
        }

        return false;
    }

    @Override
    public void onItemChangeNotify() {
    }

    /******************************************************
     * MediaPlayer Start
     ****************************************************/
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

    protected void pauseVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    protected void resumeVideo() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
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

    /******************************************************
     * MediaPlayer End
     ****************************************************/
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDisplay(surfaceHolder);

        if (!TextUtils.isEmpty(videoPath)) {
            playVideo();
        } else {
            mAdvImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public enum CoffeeMachineStatus {
        READY, PROCESSING
    }

    static class SafeHandler extends Handler {
        WeakReference<MakeCoffeeExActivity> theActivity;

        public SafeHandler(MakeCoffeeExActivity activity) {
            this.theActivity = new WeakReference<MakeCoffeeExActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            MakeCoffeeExActivity activity = theActivity.get();
            if (activity == null) return;

            switch (message.what) {
                case MSG_UI_MAKE_COFFEE_START:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_START");
                    activity.onMakeCoffeeStart();
                    break;
                case MSG_UI_MAKE_COFFEE_FAIL:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_FAIL");
                    activity.onMakeCoffeeFail();
                    break;
                case MSG_UI_MAKE_COFFEE_RETRY:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_RETRY");
                    activity.onMakeCoffeeRetry();
                    break;
                case MSG_UI_MAKE_COFFEE_SUCCESS:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_SUCCESS");
                    activity.onMakeCoffeeSuccess((String) message.obj);
                    break;
                case MSG_UI_MAKE_COFFEE_SUCCESS_ALL:
                    LogUtil.vendor("MSG_UI_MAKE_COFFEE_ALL");
                    activity.onMakeCoffeeSuccess((String) message.obj);
                    break;
                case MSG_MAKE_COFFEE_TIMEOUT:   // make coffee timeout
                    LogUtil.vendor("MSG_MAKE_COFFEE_TIMEOUT");
                    activity.onMakeCoffeeTimeout();
                    break;

                case MSG_MAKE_COFFEE_PORT_TIMEOUT:   // make coffee port timeout
                    LogUtil.vendor("MSG_MAKE_COFFEE_PORT_TIMEOUT");
                    activity.onMakeCoffeePortTimeout();
                    break;
				case MSG_MAKE_COFFEE_ONCE_MORE:
					LogUtil.vendor("MSG_MAKE_COFFEE_ONCE_MORE");
					activity.makeCoffee();
					break;
            }
        }
    }
}
