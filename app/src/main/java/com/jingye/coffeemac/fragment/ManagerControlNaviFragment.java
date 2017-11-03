package com.jingye.coffeemac.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.beans.Admin;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.module.managermodule.ManagerControlActivity;
import com.jingye.coffeemac.service.Remote;

/**
 * Created by Hades on 2016/10/25.
 */
public class ManagerControlNaviFragment extends TFragment implements View.OnClickListener {

    private static final String ADMIN = "admin";
    private static final String TAB = "tab";
    private TextView mDosingTxt;
    private TextView mManagerTxt;
    private TextView mCheckTxt;
    private TextView mSettingTxt;
    private TextView mUserTxt;
    private TextView mHintTxt;
    private TextView mTagTxt;

    public interface IManagerControlNaviItemClick {
        void onManagerControlNaviItemClick(int itemId);
    }

    private IManagerControlNaviItemClick mManagerControlNaviItemClick;

    public ManagerControlNaviFragment() {
        this.setFragmentId(R.id.manager_control_navi_fragment);
    }

    public static ManagerControlNaviFragment newInstance(Admin admin) {
        return newInstance(admin, ManagerControlActivity.DOSING);

    }

    public static ManagerControlNaviFragment newInstance(Admin admin, int tab) {
        ManagerControlNaviFragment managerControlNaviFragment = new ManagerControlNaviFragment();
        Bundle args = new Bundle();
        args.putSerializable(ADMIN, admin);
        args.putInt(TAB, tab);
        managerControlNaviFragment.setArguments(args);
        return managerControlNaviFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_control_navi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHintTxt = (TextView) view.findViewById(R.id.user_hint);
        mUserTxt = (TextView) view.findViewById(R.id.user_name);
        mTagTxt = (TextView) view.findViewById(R.id.user_tag);
        mDosingTxt = (TextView) view.findViewById(R.id.manager_control_dosing_txt);
        mManagerTxt = (TextView) view.findViewById(R.id.manager_control_manager_txt);
        mCheckTxt = (TextView) view.findViewById(R.id.manager_control_checkin_txt);
        mSettingTxt = (TextView) view.findViewById(R.id.manager_control_setting_txt);

        mHintTxt.setText("管理员");
        mTagTxt.setText("咖啡机管理员");

        if (getArguments().containsKey(ADMIN)) {
            mUserTxt.setText(getString(R.string.str_single_string, ((Admin) getArguments().getSerializable(ADMIN)).getName()));

        }
        mDosingTxt.setOnClickListener(this);
        mManagerTxt.setOnClickListener(this);
        mCheckTxt.setOnClickListener(this);
        mSettingTxt.setOnClickListener(this);

        initTabs();
        setSelectTab(getArguments().getInt(TAB, ManagerControlActivity.DOSING));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mManagerControlNaviItemClick == null) {
            mManagerControlNaviItemClick = (IManagerControlNaviItemClick) context;
        }
    }

    private void setSelectTab(int i) {
        switch (i) {
            case ManagerControlActivity.DOSING:
                mDosingTxt.setSelected(true);
                mDosingTxt.setTextSize(40);
                break;
            case ManagerControlActivity.MANAGER:
                mManagerTxt.setSelected(true);
                mManagerTxt.setTextSize(40);
                break;
            case ManagerControlActivity.CHECKIN:
                mCheckTxt.setSelected(true);
                mCheckTxt.setTextSize(40);
                break;
            case ManagerControlActivity.SETTING:
                mSettingTxt.setSelected(true);
                mSettingTxt.setTextSize(40);
                break;
        }
    }

    private void initTabs() {
        mDosingTxt.setSelected(false);
        mDosingTxt.setTextSize(24);
        mManagerTxt.setSelected(false);
        mManagerTxt.setTextSize(24);
        mCheckTxt.setSelected(false);
        mCheckTxt.setTextSize(24);
        mSettingTxt.setSelected(false);
        mSettingTxt.setTextSize(24);
    }

    @Override
    public void onReceive(Remote remote) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manager_control_dosing_txt:
                initTabs();
                setSelectTab(ManagerControlActivity.DOSING);

                if (mManagerControlNaviItemClick != null) {
                    mManagerControlNaviItemClick.onManagerControlNaviItemClick(ManagerControlActivity.DOSING);
                }
                break;
            case R.id.manager_control_manager_txt:
                initTabs();
                setSelectTab(ManagerControlActivity.MANAGER);

                if (mManagerControlNaviItemClick != null) {
                    mManagerControlNaviItemClick.onManagerControlNaviItemClick(ManagerControlActivity.MANAGER);
                }
                break;
            case R.id.manager_control_checkin_txt:
                initTabs();
                setSelectTab(ManagerControlActivity.CHECKIN);

                if (mManagerControlNaviItemClick != null) {
                    mManagerControlNaviItemClick.onManagerControlNaviItemClick(ManagerControlActivity.CHECKIN);
                }
                break;
            case R.id.manager_control_setting_txt:
                initTabs();
                setSelectTab(ManagerControlActivity.SETTING);

                if (mManagerControlNaviItemClick != null) {
                    mManagerControlNaviItemClick.onManagerControlNaviItemClick(ManagerControlActivity.SETTING);
                }
                break;
        }

    }
}
