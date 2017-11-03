package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.fragment.ControlStockFragment;
import com.jingye.coffeemac.fragment.ControlNaviFragment;
import com.jingye.coffeemac.fragment.ControlOtherFragment;
import com.jingye.coffeemac.fragment.ControlTestFragment;
import com.jingye.coffeemac.fragment.ControlNaviFragment.OnControlNaviItemClickListener;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.log.LogUtil;

public class MachineControlActivity extends TActivity implements OnClickListener, 
	OnControlNaviItemClickListener{
	
	private static final String TAB_ID = "tab_id";
	
	private ControlNaviFragment mNaviFragment;
	private ControlStockFragment mAddDosingFragment;
	private ControlTestFragment mTestFragment;
	private ControlOtherFragment mOtherFragment;
	
	private FrameLayout mNaviLayout;
	private FrameLayout mAddDosingLayout;
	private FrameLayout mTestLayout;
	private FrameLayout mOtherLayout;
	
	public enum TabId {
		ADD_DOSING,
		TEST,
		OTHER,
	}

	private static final int ADD_DOSING = 0;
	private static final int TEST = 1;
	private static final int OTHER = 2;
	private int curTabId = ADD_DOSING;
	
	public static void start(Activity activity) {
		Intent intent = new Intent();
		intent.setClass(activity, MachineControlActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.machine_control_layout);
		
		initFragments();
		
		if (savedInstanceState == null) {
			setInitFragment();
		} else {
			restoreFragments(savedInstanceState);
		}
	}
	
	private void initFragments() {
		mNaviLayout = (FrameLayout) findViewById(R.id.control_navi_fragment);
		mAddDosingLayout = (FrameLayout) findViewById(R.id.add_dosing_fragment);
		mTestLayout = (FrameLayout) findViewById(R.id.machine_debug_fragment);
		mOtherLayout = (FrameLayout) findViewById(R.id.other_fragment);
	}
	
	private void setInitFragment() {
		mNaviLayout.setVisibility(View.VISIBLE);
		mAddDosingLayout.setVisibility(View.VISIBLE);
		mTestLayout.setVisibility(View.GONE);
		mOtherLayout.setVisibility(View.GONE);
		
		mNaviFragment = new ControlNaviFragment();
		switchContent(mNaviFragment);
		
		curTabId = ADD_DOSING;
		mAddDosingFragment = new ControlStockFragment();
		switchContent(mAddDosingFragment);
	}
	
	private void restoreFragments(Bundle savedInstanceState) {
		try {
			mNaviFragment = (ControlNaviFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ControlNaviFragment.class.getName());
			mAddDosingFragment = (ControlStockFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ControlStockFragment.class.getName());
			mTestFragment = (ControlTestFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ControlTestFragment.class.getName());
			mOtherFragment = (ControlOtherFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ControlOtherFragment.class.getName());
			curTabId = savedInstanceState.getInt(TAB_ID);
			if (curTabId == ADD_DOSING) {
				mAddDosingLayout.setVisibility(View.VISIBLE);
				mTestLayout.setVisibility(View.GONE);
				mOtherLayout.setVisibility(View.GONE);	
			} else if (curTabId == TEST) {
				mAddDosingLayout.setVisibility(View.GONE);
				mTestLayout.setVisibility(View.VISIBLE);
				mOtherLayout.setVisibility(View.GONE);	
			} else if (curTabId == OTHER) {
				mAddDosingLayout.setVisibility(View.GONE);
				mTestLayout.setVisibility(View.GONE);
				mOtherLayout.setVisibility(View.VISIBLE);
			} 
		} catch (Exception e) {
			LogUtil.e("MachineControlActivity", "restoreFragments:" + e.getMessage());
			setInitFragment();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_ID, curTabId);
        try {
            getSupportFragmentManager().putFragment(outState,
                    ControlNaviFragment.class.getName(), mNaviFragment);
            getSupportFragmentManager().putFragment(outState,
                    ControlStockFragment.class.getName(), mAddDosingFragment);
            getSupportFragmentManager().putFragment(outState,
                    ControlTestFragment.class.getName(), mTestFragment);
            getSupportFragmentManager().putFragment(outState,
                    ControlOtherFragment.class.getName(), mOtherFragment);
        } catch (Exception e) {
            LogUtil.e("MachineControlActivity", "onSaveInstanceState:" + e.getMessage());
        }
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		}
	}

	@Override
	public void OnContrulNaviItem(TabId selecctedTab) {
		switch (selecctedTab) {
		case ADD_DOSING:
			if(mAddDosingFragment == null){
				mAddDosingFragment = new ControlStockFragment();
				switchContent(mAddDosingFragment);
			}
			mAddDosingFragment.refresh();
			mAddDosingLayout.setVisibility(View.VISIBLE);
			mTestLayout.setVisibility(View.GONE);
			mOtherLayout.setVisibility(View.GONE);
			break;
		case TEST: 
			if(mTestFragment == null){
				mTestFragment = new ControlTestFragment();
				switchContent(mTestFragment);
			}
			mAddDosingLayout.setVisibility(View.GONE);
			mTestLayout.setVisibility(View.VISIBLE);
			mOtherLayout.setVisibility(View.GONE);	
			break;
		case OTHER: 
			if(mOtherFragment == null){
				mOtherFragment = new ControlOtherFragment();
				switchContent(mOtherFragment);
			}
			mAddDosingLayout.setVisibility(View.GONE);
			mTestLayout.setVisibility(View.GONE);
			mOtherLayout.setVisibility(View.VISIBLE);	
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onReceive(Remote remote) {
		if (remote.getWhat() == ITranCode.ACT_USER) {
			if(remote.getAction() == ITranCode.ACT_USER_LOGOUT){
				LogUtil.vendor("onReceive -> ACT_USER_LOGOUT");
				ProgressDlgHelper.closeProgress();
				LogoutResult result = Ancestor.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
					this.finish();
				}
			}
		}
	}
}
