package com.jingye.coffeemac.module.repairmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.fragment.ManagerControlCheckInFragment;
import com.jingye.coffeemac.fragment.ManagerControlDosingFragment;
import com.jingye.coffeemac.fragment.RepairControlDebugFragment;
import com.jingye.coffeemac.fragment.RepairControlManagerFragment;
import com.jingye.coffeemac.fragment.RepairControlNaviFragment;
import com.jingye.coffeemac.fragment.RepairControlSettingFragment;
import com.jingye.coffeemac.module.managermodule.ManagerControlContract;
import com.jingye.coffeemac.module.managermodule.ManagerControlModelImpl;
import com.jingye.coffeemac.module.managermodule.ManagerControlPresenter;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.util.U;

public class RepairControlActivity extends TActivity implements ManagerControlContract.ManagerControlView,RepairControlNaviFragment.IRepairControlNavi {

    public static final int REPAIR_DEBUG=11;
    public static final int REPAIR_MANAGER=12;
    public static final int REPAIR_CHECKIN=13;
    public static final int REPAIR_SETTING=14;
    private static final String ADMIN = "admin";
    private RepairControlActivity mContext;
    private ManagerControlPresenter mPresenter;
    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private FrameLayout mNaviLayout;
    private FrameLayout mDebugLayout;
    private FrameLayout mManagerLayout;
    private FrameLayout mCheckinLayout;
    private FrameLayout mSettingLayout;
    private RepairControlNaviFragment mNaviFragment;
    private int mCurTab=REPAIR_DEBUG;
    private RepairControlDebugFragment mDebugFragment;
    private ManagerControlDosingFragment mDosingFragment;
//    private RepairControlManagerFragment mManagerFragment;
    private RepairControlSettingFragment mSettingFragment;
    private ManagerControlCheckInFragment mCheckInFragment;

    public static void start(Activity activity, Admin admin){
        Intent intent=new Intent();
        intent.setClass(activity,RepairControlActivity.class);
        intent.putExtra(ADMIN,admin);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_control);

        mContext=this;
        mPresenter=new ManagerControlPresenter(this,new ManagerControlModelImpl());

        progressIntent();
        initView();

        initStatus();

        if(savedInstanceState==null){
            initFragment();
        }else{
            //Todo restore from savedInstanceState
        }

    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        mPresenter.updateStatus(status);
    }

    private void initFragment() {
        mNaviFragment =  RepairControlNaviFragment.newInstance(mPresenter.getAdmin());
        switchContent(mNaviFragment);

        mPresenter.hideAllLayouts();
        mPresenter.showCurTab(mCurTab);

    }

    private void initView() {
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);
        mNaviLayout = (FrameLayout) findViewById(R.id.manager_control_navi_fragment);
        mDebugLayout = (FrameLayout) findViewById(R.id.repair_debug_fragment);
        mManagerLayout = (FrameLayout) findViewById(R.id.repair_manager_fragment);
        mCheckinLayout = (FrameLayout) findViewById(R.id.checkin_fragment );
        mSettingLayout = (FrameLayout) findViewById(R.id.repair_setting_fragment);
    }

    private void progressIntent() {
        if(getIntent()!=null&&getIntent().hasExtra(ADMIN)){
            mPresenter.saveAdmin((Admin) getIntent().getSerializableExtra(ADMIN));
        }
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                mPresenter.updateStatus(notify.getStatus());
            }
        }

    }

    @Override
    public void hideAllLayouts() {
        mDebugLayout.setVisibility(View.GONE);
        mManagerLayout.setVisibility(View.GONE);
        mCheckinLayout.setVisibility(View.GONE);
        mSettingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showCurTab(int mCurTab) {
        switch (mCurTab) {
            case REPAIR_DEBUG:
//                if (mDebugFragment == null) {
//                    mDebugFragment = new RepairControlDebugFragment();
//                    switchContent(mDebugFragment);
//                }
                if (mDosingFragment == null) {
                    mDosingFragment = ManagerControlDosingFragment.newInstance(mPresenter.getAdmin().getId());
                    switchContent(mDosingFragment);
                }
                mDebugLayout.setVisibility(View.VISIBLE);
                break;
            case REPAIR_MANAGER:
                if (mDebugFragment == null) {
                    mDebugFragment = new RepairControlDebugFragment();
                    mDebugFragment.setDosing(mDosingFragment.getFactor());
                    switchContent(mDebugFragment);
                }
//                mDebugLayout.setVisibility(View.VISIBLE);
//                if(mManagerFragment==null){
//                    mManagerFragment=new RepairControlManagerFragment();
//                    switchContent(mManagerFragment);
//                }
                mManagerLayout.setVisibility(View.VISIBLE);
                break;
            case REPAIR_CHECKIN:
                if (mCheckInFragment == null) {
                    mCheckInFragment = new ManagerControlCheckInFragment();
                    mCheckInFragment.setAdminId(mPresenter.getAdmin().getId());
                    switchContent(mCheckInFragment);
                }
                mCheckinLayout.setVisibility(View.VISIBLE);

                break;
            case REPAIR_SETTING:
                if(mSettingFragment==null){
                    mSettingFragment=new RepairControlSettingFragment();
                    mSettingFragment.setRecordId(mPresenter.getAdmin().getRecordId());
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
    public void onRepairControlNaviItemClick(int itemId) {
        mPresenter.hideAllLayouts();
        mPresenter.showCurTab(itemId);
    }
}
