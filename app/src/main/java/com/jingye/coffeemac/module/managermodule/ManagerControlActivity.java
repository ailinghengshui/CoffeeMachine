package com.jingye.coffeemac.module.managermodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.BackgroundLoginActivity;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.fragment.ManagerControlCheckInFragment;
import com.jingye.coffeemac.fragment.ManagerControlDosingFragment;
import com.jingye.coffeemac.fragment.ManagerControlManagerFragment;
import com.jingye.coffeemac.fragment.ManagerControlNaviFragment;
import com.jingye.coffeemac.fragment.ManagerControlSettingFragment;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;

public class ManagerControlActivity extends TActivity implements ManagerControlContract.ManagerControlView, ManagerControlNaviFragment.IManagerControlNaviItemClick {

    public static final int DOSING = 0;
    public static final int MANAGER = 1;
    public static final int CHECKIN = 2;
    public static final int SETTING = 3;
    private static final String ADMIN = "admin";
    private Context mContext;
    private ManagerControlPresenter mManagerControlPresenter;
    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private FrameLayout mNaviLayout;
    private ManagerControlNaviFragment mNaviFragment;
    private FrameLayout mDosingLayout;
    private FrameLayout mManagerLayout;
    private FrameLayout mCheckinLayout;
    private FrameLayout mSettingLayout;
    private int mCurTab = DOSING;
    private ManagerControlDosingFragment mDosingFragment;
    private ManagerControlManagerFragment mManagerFragment;
    private ManagerControlCheckInFragment mCheckInFragment;
    private ManagerControlSettingFragment mSettingFragment;

    public static void start(Activity activity, Admin admin) {
        Intent intent = new Intent();
        intent.setClass(activity, ManagerControlActivity.class);
        intent.putExtra(ADMIN, admin);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_control);
        mContext = this;
        mManagerControlPresenter = new ManagerControlPresenter(this,new ManagerControlModelImpl());

        progressIntent();

        initView();

        initStatus();


        if (savedInstanceState == null) {
            initFragment();

        } else {
            //Todo restore fragments

        }
    }

    private void progressIntent() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(ADMIN)) {
                mManagerControlPresenter.saveAdmin((Admin) getIntent().getSerializableExtra(ADMIN));
            }
        }
    }

    private void initFragment() {
        mNaviFragment = ManagerControlNaviFragment.newInstance(mManagerControlPresenter.getAdmin());
        switchContent(mNaviFragment);

        mManagerControlPresenter.hideAllLayouts();
        mManagerControlPresenter.showCurTab(mCurTab);

    }


    @Override
    public void showCurTab(int mCurTab) {
        switch (mCurTab) {
            case DOSING:
                if (mDosingFragment == null) {
                    mDosingFragment = ManagerControlDosingFragment.newInstance(mManagerControlPresenter.getAdmin().getId());
                    switchContent(mDosingFragment);
                }
                mDosingLayout.setVisibility(View.VISIBLE);
                break;
            case MANAGER:
                if (mManagerFragment == null) {
                    mManagerFragment = new ManagerControlManagerFragment();
                    switchContent(mManagerFragment);
                }
                mManagerLayout.setVisibility(View.VISIBLE);
                break;
            case CHECKIN:
                //create checkInFragment
                if (mCheckInFragment == null) {
                    mCheckInFragment = new ManagerControlCheckInFragment();
                    mCheckInFragment.setAdminId(mManagerControlPresenter.getAdmin().getId());
                    switchContent(mCheckInFragment);
                }
                mCheckinLayout.setVisibility(View.VISIBLE);
                break;
            case SETTING:
                if (mSettingFragment == null) {
                    mSettingFragment = new ManagerControlSettingFragment();
                    switchContent(mSettingFragment);
                }
                mSettingLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void updateStatus(int status) {
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


    @Override
    public void hideAllLayouts() {
        mDosingLayout.setVisibility(View.GONE);
        mManagerLayout.setVisibility(View.GONE);
        mCheckinLayout.setVisibility(View.GONE);
        mSettingLayout.setVisibility(View.GONE);
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        mManagerControlPresenter.updateStatus(status);

    }

    private void initView() {
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);
        mNaviLayout = (FrameLayout) findViewById(R.id.manager_control_navi_fragment);
        mDosingLayout = (FrameLayout) findViewById(R.id.dosing_fragment);
        mManagerLayout = (FrameLayout) findViewById(R.id.manager_fragment);
        mCheckinLayout = (FrameLayout) findViewById(R.id.checkin_fragment);
        mSettingLayout = (FrameLayout) findViewById(R.id.setting_fragment);
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                mManagerControlPresenter.updateStatus(notify.getStatus());
            }
        }

    }

    @Override
    public void onManagerControlNaviItemClick(int itemId) {
        if (mCurTab == itemId) {

        } else {
            mCurTab = itemId;
            mManagerControlPresenter.hideAllLayouts();
            mManagerControlPresenter.showCurTab(mCurTab);
        }

    }
}
