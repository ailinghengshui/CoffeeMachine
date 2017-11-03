package com.jingye.coffeemac.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.LoginActivity;
import com.jingye.coffeemac.application.GlobalCached;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.helper.cache.BaseDataCacher;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.LogoutInfo;
import com.jingye.coffeemac.service.bean.action.VerifyPasswordInfo;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.bean.result.VerifyPasswordResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.ui.DialogTitle;
import com.jingye.coffeemac.ui.DialogTitleDesc;
import com.jingye.coffeemac.ui.DialogTitleProgress;
import com.jingye.coffeemac.ui.GenericSettingDialog;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.AppUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.jingye.coffeemac.fragment.ControlOtherFragment.WASHTIME_PREFERENCES_SET;

/**
 * Created by Hades on 2016/10/26.
 */
public class ManagerControlSettingFragment extends TFragment implements View.OnClickListener {

    private DialogTitleProgress dialogTitleProgress = DialogTitleProgress.newInstance("正在升级，请稍后...", "取消","","");

    public ManagerControlSettingFragment() {
        this.setFragmentId(R.id.setting_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_control_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        (view.findViewById(R.id.btnManagerControlSettingDrive)).setOnClickListener(this);
        (view.findViewById(R.id.btnManagerControlSettingMainControl)).setOnClickListener(this);
        (view.findViewById(R.id.btnSettingExit)).setOnClickListener(this);
        (view.findViewById(R.id.btnManagerControlSettingUpdate)).setOnClickListener(this);
        (view.findViewById(R.id.btnSettingLogout)).setOnClickListener(this);
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
        DialogTitleDesc dialogTitleDesc = DialogTitleDesc.newInstance("当前版本已是最新", "暂无新版本", "取消", "升级到新版本", false);
//        DialogTitle dialogTitle = DialogTitle.newInstance(getString(R.string.str_please_wait));
        switch (view.getId()) {
            case R.id.btnManagerControlSettingDrive:
//                dialogTitle.show(getActivity().getSupportFragmentManager(), "waitHint");


                if (!getActivity().isFinishing()) {
//                    DialogTitleDesc dialogTitleDesc = DialogTitleDesc.newInstance("当前版本4.4.4", "暂无新版本", "取消", "升级到新版本", false);
                    dialogTitleDesc.show(getActivity().getSupportFragmentManager(), "dialogTitleDesc");
                }
                break;

            case R.id.btnManagerControlSettingMainControl:
//                dialogTitle.show(getActivity().getSupportFragmentManager(), "waitHint");
                if (!getActivity().isFinishing()) {
                    dialogTitleDesc.show(getActivity().getSupportFragmentManager(), "dialogTitleDesc1");
                }

                break;
            case R.id.btnManagerControlSettingUpdate:
//                dialogTitle.show(getActivity().getSupportFragmentManager(), "waitHint");
//                dialogTitleProgress.show(getActivity().getSupportFragmentManager(),"dialogTitleProgress");
//                timerCount.start();
                if (!getActivity().isFinishing()) {
                    DialogTitleDesc dialogTitleDesc2 = DialogTitleDesc.newInstance("当前版本是"+ AppUtil.getAppVersionName(getContext()), "暂无新版本", "取消", "升级到新版本", false);
                    dialogTitleDesc2.show(getActivity().getSupportFragmentManager(), "dialogTitleDesc2");
                }
                break;
            case R.id.btnSettingExit:
                if (getActivity().isFinishing()) {


                } else {
                    DialogTitle dialogTitle2 = DialogTitle.newInstance("是否退出用户登录?");
                    dialogTitle2.setListener(new DialogTitle.IDialogTitleClick() {
                        @Override
                        public void onDialogTitleCancelClick() {

                        }

                        @Override
                        public void onDialogTitleOkClick() {
                            getActivity().finish();
                        }
                    });
                    dialogTitle2.show(getActivity().getSupportFragmentManager(), "exitHint");
                }
                break;

            case R.id.btnSettingLogout:
//                ProgressDlgHelper.showProgress(getActivity(), "正在退出");
//
//                LogoutInfo info = new LogoutInfo();
//                info.setUid(U.getMyVendorNum());
//                info.setPassword("test123");
//                execute(info.toRemote());
                Map<String, String> map = new HashMap<String, String>();
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
                break;

        }
    }

    class TimeCount extends CountDownTimer {

        private int i = 0;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            if (!dialogTitleProgress.isHidden() && dialogTitleProgress != null) {
                i += 20;
                dialogTitleProgress.setProgress(i);

            }
        }

        @Override
        public void onFinish() {
            if (!dialogTitleProgress.isHidden()) {
                dialogTitleProgress.dismiss();
            }
        }
    }
}
