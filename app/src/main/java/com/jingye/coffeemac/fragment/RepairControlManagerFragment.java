package com.jingye.coffeemac.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.ControlMaintanceActivity;
import com.jingye.coffeemac.activity.WelcomeActivity;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.ui.DialogTitle;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 2016/10/31.
 */
public class RepairControlManagerFragment extends TFragment implements View.OnClickListener {

    private RelativeLayout rlRepairControl;

    private DialogTitle.IDialogTitleClick fixErrorListener = new DialogTitle.IDialogTitleClick() {
        @Override
        public void onDialogTitleCancelClick() {

        }

        @Override
        public void onDialogTitleOkClick() {
            SharePrefConfig.getInstance().setIsNeedLock(false);
            MyApplication.Instance().setNeedRollback(false);
            MyApplication.Instance().setWaitMaintenance(false);
            List<Integer> status = new ArrayList<Integer>();
            MachineStatusReportInfo info = new MachineStatusReportInfo();
            info.setUid(U.getMyVendorNum());
            info.setTimestamp(TimeUtil.getNow_millisecond());
            status.add(MachineStatusCode.SUCCESS);
            info.setStatus(status);
            execute(info.toRemote());
        }
    };
    private RelativeLayout rlRepairWait;

    public RepairControlManagerFragment() {
        this.setFragmentId(R.id.repair_manager_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair_control_manager, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        rlRepairControl = (RelativeLayout) view.findViewById(R.id.rlRepairControl);
        rlRepairWait = (RelativeLayout) view.findViewById(R.id.rlRepairWait);
        rlRepairControl.setOnClickListener(this);
        rlRepairWait.setOnClickListener(this);


    }

    @Override
    public void onReceive(Remote remote) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlRepairControl:
                // repair
                DialogTitle dialogTitle = DialogTitle.newInstance(getString(R.string.control_fix_error_sure));
                dialogTitle.setListener(fixErrorListener);
                dialogTitle.show(getActivity().getSupportFragmentManager(), "fixErrorHint");
                break;
            case R.id.rlRepairWait:
                LogUtil.vendor("wait-----");
                Intent intent = new Intent(getActivity(), ControlMaintanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
