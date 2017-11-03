package com.jingye.coffeemac.activity;

import android.os.Bundle;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.service.Remote;

public class ControlMaintanceActivity extends TActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_maintance_page);
	}

	@Override
	public void onReceive(Remote remote) {

	}
}
