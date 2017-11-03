package com.jingye.coffeemac.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.U;

public class HelpFragment extends TFragment{

    private TextView mHelpMacNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
        mHelpMacNo = (TextView) getActivity().findViewById(R.id.fragment_help_mac_no);
        mHelpMacNo.setText(getString(R.string.help_instruction_no)+U.getMyVendorName());
    }

	@Override
	public void onReceive(Remote remote) {
	}
}
