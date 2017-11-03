package com.jingye.coffeemac.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.Remote;

public class ToDoFragment extends TFragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

	@Override
	public void onReceive(Remote remote) {
	}
}
