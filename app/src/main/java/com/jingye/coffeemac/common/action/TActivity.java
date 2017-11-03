package com.jingye.coffeemac.common.action;

import java.lang.ref.WeakReference;

import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.inter.IServiceBindListener;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.VendorService;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.util.ToolUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

public abstract class TActivity extends FragmentActivity {

    protected String uuid = ToolUtil.getUUID();

    /**
     * UI逻辑层通知通道
     */
    static class RemoteHandler extends Handler {
        WeakReference<TActivity> theActivity;

        public RemoteHandler(TActivity activity) {
            this.theActivity = new WeakReference<TActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            Remote remote = (Remote) message.obj;
            TActivity activity = theActivity.get();
            if (activity != null) {
                activity.onReceive(remote);
                // 如果是被踢掉，关闭所有的activity
                if (remote.getWhat() == ITranCode.ACT_SYS
                        && remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                    activity.onStatusChange(remote);
                }
            }
        }
    }

    protected RemoteHandler handler = new RemoteHandler(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.vendor("activity: " + getClass().getSimpleName() + " onCreate()");

        TViewWatcher.newInstance().bindView(handler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.vendor("activity: " + getClass().getSimpleName() + " onDestroy()");

        TViewWatcher.newInstance().unBindView(handler);
        TViewWatcher.newInstance().removeServiceBinderListener(mServiceBindListener);
    }

    ///------------ 程序前后台切换时通知到service ---------------
    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.Instance().setActive(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.Instance().setActive(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * 页面提交工作，将输入场打包后进入具体的action处理
     *
     * @param remote
     */
    public Remote execute(Remote remote) {
        TViewWatcher.newInstance().execute(remote);
        return remote;
    }

    public Remote executeBackground(Remote remote) {
        TViewWatcher.newInstance().executeBackground(remote);
        return remote;
    }

    public Remote execute(int what, int action, String body) {
        Remote remote = new Remote();
        remote.setWhat(what);
        remote.setAction(action);
        remote.setBody(body);
        TViewWatcher.newInstance().execute(remote);
        return remote;
    }

    public Remote executeBackground(int what, int action, String body) {
        Remote remote = new Remote();
        remote.setWhat(what);
        remote.setAction(action);
        remote.setBody(body);
        TViewWatcher.newInstance().executeBackground(remote);
        return remote;
    }

    /**
     * 接收到消息,需要具体的页面来实现
     *
     * @param
     */
    public abstract void onReceive(Remote remote);

    /**
     * is bound
     *
     * @return bind state
     */
    protected boolean isBound() {
        return VendorService.isBind;
    }

    /**
     * request bind
     */
    protected void requestBind() {
        if (isBound()) {
            // handle bound
            handleBound();
        } else {
            // listen bind
            TViewWatcher.newInstance().addServiceBinderListener(mServiceBindListener);
        }
    }

    /**
     * handle bound
     */
    protected void handleBound() {

    }

    /**
     * service bind listener
     */
    IServiceBindListener mServiceBindListener = new IServiceBindListener() {

        @Override
        public void onBindSuccess() {
            // handle bound
            handleBound();
        }

        @Override
        public void onBindFailed(String errorMessage) {
            // TODO: 2016/11/17 debug log
        }
    };

    private void onStatusChange(Remote remote) {
        StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
        if (notify.getStatus() == ITranCode.STATUS_KICKOUT) {
            finish();
            // TODO
        }
    }

    protected void invisibleKeyboard(boolean isShow) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                inputMethodManager.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
            }

        } else {
            if (getCurrentFocus() == null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            } else {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }

    public void switchContent(TFragment fragment) {
        switchContent(fragment, false);
    }

    protected TFragment switchContent(TFragment fragment, boolean needAddToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        int fragmentId = fragment.getFragmentId();
        Fragment fragment2 = fm.findFragmentById(fragmentId);
        if (fragment2 == null) {
            fragment2 = fragment;
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(fragmentId, fragment);
            if (needAddToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
            fm.executePendingTransactions();
        }

        return (TFragment) fragment2;
    }

}
