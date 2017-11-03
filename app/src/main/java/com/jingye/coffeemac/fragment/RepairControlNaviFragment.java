package com.jingye.coffeemac.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.BackgroundLoginActivity;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.module.repairmodule.RepairControlActivity;
import com.jingye.coffeemac.service.Remote;

import org.jboss.netty.util.internal.CaseIgnoringComparator;

/**
 * This class implements operator-tabs
 * Created by Hades on 2016/10/27.
 */
public class RepairControlNaviFragment extends TFragment implements View.OnClickListener {

    private static final String ADMIN = "admin";
    private static final String TAB = "tab";
    private TextView mUserHintTxt;
    private TextView mUserNameTxt;
    private TextView mDebugTxt;
    private TextView mManagerTxt;
    private TextView mCheckinTxt;
    private TextView mSettingTxt;
    private TextView mUserTagTxt;
    private IRepairControlNavi mItemClickListener;

    public RepairControlNaviFragment() {
        this.setFragmentId(R.id.repair_control_navi_fragment);
    }

    public static RepairControlNaviFragment newInstance(Admin admin) {
        return newInstance(admin, RepairControlActivity.REPAIR_DEBUG);
    }

    public static RepairControlNaviFragment newInstance(Admin admin, int tab) {
        RepairControlNaviFragment repairControlNaviFragment = new RepairControlNaviFragment();
        Bundle args = new Bundle();
        args.putSerializable(ADMIN, admin);
        args.putInt(TAB, tab);
        repairControlNaviFragment.setArguments(args);
        return repairControlNaviFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IRepairControlNavi) {
            this.mItemClickListener = (IRepairControlNavi) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair_control_navi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserHintTxt = (TextView) view.findViewById(R.id.user_hint);
        mUserNameTxt = (TextView) view.findViewById(R.id.user_name);
        mUserTagTxt = (TextView) view.findViewById(R.id.user_tag);
        mDebugTxt = (TextView) view.findViewById(R.id.repair_control_debug_txt);
        mManagerTxt = (TextView) view.findViewById(R.id.repair_control_manager_txt);
        mCheckinTxt = (TextView) view.findViewById(R.id.repair_control_checkin_txt);
        mSettingTxt = (TextView) view.findViewById(R.id.repair_control_setting_txt);


        if (getArguments() != null && getArguments().containsKey(ADMIN)) {
            mUserNameTxt.setText(getString(R.string.str_single_string, ((Admin) getArguments().getSerializable(ADMIN)).getName()));
            if (((Admin) getArguments().getSerializable(ADMIN)).getRole() == BackgroundLoginActivity.USER_QUALITY) {
                mUserHintTxt.setText("主管");
                mUserTagTxt.setText("品质主管");
            } else if (((Admin) getArguments().getSerializable(ADMIN)).getRole() == BackgroundLoginActivity.USER_DIRECTOR_OF_OPERATIONS) {
                mUserHintTxt.setText("主管");
                mUserTagTxt.setText("运营主管");
            } else if (((Admin) getArguments().getSerializable(ADMIN)).getRole() == BackgroundLoginActivity.USER_MANAGER) {
                mUserHintTxt.setText("加料员");
                mUserTagTxt.setText("加料员");
            } else if (((Admin) getArguments().getSerializable(ADMIN)).getRole() == BackgroundLoginActivity.USER_OPERATIONS_MANAGER) {
                mUserHintTxt.setText("经理");
                mUserTagTxt.setText("运营经理");
            } else {
                mUserHintTxt.setText("维修师");
                mUserTagTxt.setText("咖啡机维修师");
            }
        }

        mDebugTxt.setOnClickListener(this);
        mManagerTxt.setOnClickListener(this);
        mCheckinTxt.setOnClickListener(this);
        mSettingTxt.setOnClickListener(this);

        initTabs();
        setSelectTab(getArguments().getInt(TAB, RepairControlActivity.REPAIR_DEBUG));

    }

    @Override
    public void onReceive(Remote remote) {

    }

    private void setSelectTab(int i) {
        switch (i) {
            case RepairControlActivity.REPAIR_DEBUG:
                mDebugTxt.setSelected(true);
                mDebugTxt.setTextSize(40);
                break;
            case RepairControlActivity.REPAIR_MANAGER:
                mManagerTxt.setSelected(true);
                mManagerTxt.setTextSize(40);
                break;
            case RepairControlActivity.REPAIR_CHECKIN:
                mCheckinTxt.setSelected(true);
                mCheckinTxt.setTextSize(40);
                break;
            case RepairControlActivity.REPAIR_SETTING:
                mSettingTxt.setSelected(true);
                mSettingTxt.setTextSize(40);
                break;
        }
    }

    private void initTabs() {
        mDebugTxt.setSelected(false);
        mDebugTxt.setTextSize(24);
        mManagerTxt.setSelected(false);
        mManagerTxt.setTextSize(24);
        mCheckinTxt.setSelected(false);
        mCheckinTxt.setTextSize(24);
        mSettingTxt.setSelected(false);
        mSettingTxt.setTextSize(24);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repair_control_debug_txt:
                initTabs();
                setSelectTab(RepairControlActivity.REPAIR_DEBUG);
                if (mItemClickListener != null) {
                    mItemClickListener.onRepairControlNaviItemClick(RepairControlActivity.REPAIR_DEBUG);
                }
                break;
            case R.id.repair_control_manager_txt:
                initTabs();
                setSelectTab(RepairControlActivity.REPAIR_MANAGER);
                if (mItemClickListener != null) {
                    mItemClickListener.onRepairControlNaviItemClick(RepairControlActivity.REPAIR_MANAGER);
                }
                break;
            case R.id.repair_control_checkin_txt:
                initTabs();
                setSelectTab(RepairControlActivity.REPAIR_CHECKIN);
                if (mItemClickListener != null) {
                    mItemClickListener.onRepairControlNaviItemClick(RepairControlActivity.REPAIR_CHECKIN);
                }
                break;
            case R.id.repair_control_setting_txt:
                initTabs();
                setSelectTab(RepairControlActivity.REPAIR_SETTING);
                if (mItemClickListener != null) {
                    mItemClickListener.onRepairControlNaviItemClick(RepairControlActivity.REPAIR_SETTING);
                }
                break;
        }

    }

    public interface IRepairControlNavi {
        void onRepairControlNaviItemClick(int itemId);
    }
}
