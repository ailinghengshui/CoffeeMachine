package com.jingye.coffeemac.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.ControlMaintanceActivity;
import com.jingye.coffeemac.activity.LoginActivity;
import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.helper.cache.BaseDataCacher;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.LogoutInfo;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.bean.action.VerifyPasswordInfo;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.bean.result.VerifyPasswordResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.MachineStatusCode;
import com.jingye.coffeemac.ui.ControlVeriPwdDialog;
import com.jingye.coffeemac.ui.EasyAlertDialogForSure;
import com.jingye.coffeemac.ui.GenericSettingDialog;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlOtherFragment extends TFragment implements OnClickListener, EasyAlertDialogForSure.OnDialogListener,
        ControlVeriPwdDialog.OnVerifyPwdDialogListener{

    public static final String WASHTIME_PREFERENCES_SET = "清洗时间";

    private Button mMachineLogout;
    private Button mMoveBackground;

    private Button mFixError;
    private Button mSetWashTime;
    private Button mSetMaintanceMode;
    
	public ControlOtherFragment() {
		this.setFragmentId(R.id.other_fragment);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_other, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
        mMachineLogout = (Button) getView().findViewById(R.id.coffee_machine_logout);
        mMachineLogout.setOnClickListener(this);

        mMoveBackground = (Button) getView().findViewById(R.id.coffee_machine_move_background);
        mMoveBackground.setOnClickListener(this);

        mFixError = (Button) getView().findViewById(R.id.coffee_machine_fix_error);
        mFixError.setOnClickListener(this);

        mSetWashTime = (Button) getView().findViewById(R.id.coffee_machine_set_wash_time);
        mSetWashTime.setOnClickListener(mSetWashTimeOnClick);

        mSetMaintanceMode = (Button) getView().findViewById(R.id.coffee_machine_set_maintenance);
        mSetMaintanceMode.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
            case R.id.coffee_machine_logout:
                ControlVeriPwdDialog veriPwdDialog4Logout = new ControlVeriPwdDialog(getActivity(), this,
                        VerifyPasswordInfo.TYPE_LOGOUT);
                veriPwdDialog4Logout.show();
                break;
            case R.id.coffee_machine_move_background:
                ControlVeriPwdDialog veriPwdDialog4Background = new ControlVeriPwdDialog(getActivity(), this,
                        VerifyPasswordInfo.TYPE_MOVE_BACKGROUND);
                veriPwdDialog4Background.show();
                break;
            case R.id.coffee_machine_fix_error:
                String message = getString(R.string.control_fix_error_sure);
                EasyAlertDialogForSure dialog = new EasyAlertDialogForSure(getActivity(), this, false, message);
                dialog.show();
                break;
            case R.id.coffee_machine_set_maintenance:
                Intent intent = new Intent(getActivity(), ControlMaintanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
	}

    private OnClickListener mSetWashTimeOnClick = new OnClickListener() {

        private Map<String, String> map;

        @Override
        public void onClick(View v) {
            map = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            for(String time : MyApplication.Instance().mGlobalWashTimeSet){
                sb.append(",");
                sb.append(time);
            }
            if(sb.length()>0 &&sb.charAt(0)==','){
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
                            try{
                                String tmpTimes = resultMap.get(WASHTIME_PREFERENCES_SET);
                                String[] times = tmpTimes.split(",");
                                for(String time:times){
                                    if(!time.matches("[0-9]{2}:[0-9]{2}")){
                                        throw new Exception();
                                    }
                                }
                                MyApplication.Instance().mGlobalWashTimeSet.clear();
                                for(String time:times){
                                    MyApplication.Instance().mGlobalWashTimeSet.add(time);
                                }
                                SharePrefConfig.getInstance().setWashTime(MyApplication.Instance().mGlobalWashTimeSet);

                                ToastUtil.showToast(getActivity(), MyApplication.Instance().mGlobalWashTimeSet.toString());
                                return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), "输入格式错误");
                            }
                            return false;
                        }
                    });
            dialog.show();
        }

    };

    @Override
    public void OnDialogCancel() {
        //DO NOTHING
    }

    @Override
    public void OnDialogConfirm() {
        // report server

        MyApplication.Instance().setWaitMaintenance(false);

        List<Integer> status = new ArrayList<Integer>();
        MachineStatusReportInfo info = new MachineStatusReportInfo();
        info.setUid(U.getMyVendorNum());
        info.setTimestamp(TimeUtil.getNow_millisecond());
        status.add(MachineStatusCode.SUCCESS);
        info.setStatus(status);
        execute(info.toRemote());
    }


    @Override
    public void verifyPwdCancel() {
        //DO NOTHING
    }

    @Override
    public void verifyPwdConfirm(String password, int type) {
        if(type == VerifyPasswordInfo.TYPE_MOVE_BACKGROUND){
            VerifyPasswordInfo info = new VerifyPasswordInfo();
            info.setPassword(password);
            info.setType(type);
            execute(info.toRemote());
        }else{
            ProgressDlgHelper.showProgress(getActivity(), "正在退出");

            LogoutInfo info = new LogoutInfo();
            info.setUid(U.getMyVendorNum());
            info.setPassword(password);
            execute(info.toRemote());
        }
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_USER) {
            if (remote.getAction() == ITranCode.ACT_USER_VERIFY_PWD) {
                LogUtil.vendor("onReceive -> ACT_USER_VERIFY_PWD");
                VerifyPasswordResult result = Ancestor.parseObject(remote.getBody());
                if(result != null && result.isCorrect()){
                    if(result.getType() == VerifyPasswordInfo.TYPE_MOVE_BACKGROUND){
                        getActivity().moveTaskToBack(true);
                    }
                }else{
                    ToastUtil.showToast(getActivity(), "密码验证失败");
                }
            }else if(remote.getAction() == ITranCode.ACT_USER_LOGOUT){
                LogUtil.vendor("onReceive -> ACT_USER_LOGOUT");
                ProgressDlgHelper.closeProgress();
                LogoutResult result = Ancestor.parseObject(remote.getBody());
                if(result != null && result.getResCode() == 200){
                    U.saveAppSet(U.KEY_USER_IS_LOGINED, "false", U.getMyVendorNum());
                    // clear cache
                    GlobalCached.clear();
                    BaseDataCacher.clearCache();
                    // back to login page
                    LoginActivity.start(getActivity());
                }else{
                    ToastUtil.showToast(getActivity(), "密码验证失败");
                }
            }
        }
    }

}
