package com.jingye.coffeemac.fragment;

import java.util.HashMap;
import java.util.Map;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.MachineControlActivity;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.Remote;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ControlNaviFragment extends TFragment implements OnClickListener{
    
	public interface OnControlNaviItemClickListener {
		public void OnContrulNaviItem(MachineControlActivity.TabId selecctedTab);
	}
	
	private OnControlNaviItemClickListener mNaviItemClickListener;
	
	private MachineControlActivity.TabId selectedTab;
	
	private Map<MachineControlActivity.TabId, Integer> itemLayouts = 
			new HashMap<MachineControlActivity.TabId, Integer>();


	public ControlNaviFragment() {
		this.setFragmentId(R.id.control_navi_fragment);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_navi, container, false);
    }
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if (mNaviItemClickListener == null) {
			mNaviItemClickListener = (OnControlNaviItemClickListener) activity;
		}
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
    	itemLayouts.put(MachineControlActivity.TabId.ADD_DOSING, R.id.control_add_dosing);
    	initNaviItem(R.id.control_add_dosing, R.string.control_add_dosing, -1);
    	getView().findViewById(R.id.control_add_dosing).setOnClickListener(this);
    	
    	itemLayouts.put(MachineControlActivity.TabId.TEST, R.id.control_test_machine);
    	initNaviItem(R.id.control_test_machine, R.string.control_test_machine, -1);
    	getView().findViewById(R.id.control_test_machine).setOnClickListener(this);
    	
    	itemLayouts.put(MachineControlActivity.TabId.OTHER, R.id.control_other);
    	initNaviItem(R.id.control_other, R.string.control_other, -1);
		getView().findViewById(R.id.control_other).setOnClickListener(this);
    	
    	selectedTab = MachineControlActivity.TabId.ADD_DOSING;
		setItemSelected(selectedTab, true);
    }
    
    private void initNaviItem(int id, int caption, int image) {
		View view = getActivity().findViewById(id);
		if (view == null) {
			return;
		}

		TextView text = (TextView) view.findViewById(R.id.navi_title);
		text.setText(getString(caption));
//		Drawable left = getResources().getDrawable(image);
//		left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
//		text.setCompoundDrawables(left, null, null, null);
	}
    
    private void setItemSelected(MachineControlActivity.TabId tab, boolean selected) {
		int layoutid = itemLayouts.get(tab);
		View view = getActivity().findViewById(layoutid);
		if (view == null) {
			return;
		}
		view.setSelected(selected);
	}

    public void switchContentByTag(MachineControlActivity.TabId tabId) {
		if (tabId == selectedTab) {
			return;
		}

		setItemSelected(selectedTab, false);
		selectedTab = tabId;
		setItemSelected(selectedTab, true);
		
		if(mNaviItemClickListener != null){
			mNaviItemClickListener.OnContrulNaviItem(tabId);
		}
	}


    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.control_add_dosing:
			switchContentByTag(MachineControlActivity.TabId.ADD_DOSING);
			break;
		case R.id.control_test_machine:
            switchContentByTag(MachineControlActivity.TabId.TEST);
			break;
		case R.id.control_other:
            switchContentByTag(MachineControlActivity.TabId.OTHER);
			break;
		default:
			break;
		}
    }

	@Override
	public void onReceive(Remote remote) {
		
	}  
}
