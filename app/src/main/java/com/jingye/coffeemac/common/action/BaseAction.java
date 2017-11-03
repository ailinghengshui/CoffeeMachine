package com.jingye.coffeemac.common.action;

import java.util.ArrayList;
import java.util.List;

import com.jingye.coffeemac.inter.Observer;

import android.os.Handler;
import android.os.Message;


public abstract class BaseAction extends Handler {

	protected List<Observer> observerList;
	
	public BaseAction(){
		observerList = new ArrayList<Observer>();
	}

	public void addObserver(Observer observer){
		if(observerList != null){
			if(!observerList.contains(observer))
				observerList.add(observer);
		}
	}
	public void removeObserver(Observer observer){
		if(observerList != null)
			observerList.remove(observer);
	}
	
	public void removeAllObserver(){
		if(observerList != null)
			observerList.clear();
	}
	
	protected void updateUI(Message msg){
		sendMessage(msg);
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if(observerList != null){
			int count = observerList.size();
			for(int i =0;i<count;i++)
				observerList.get(i).updateUI(msg);
		}
	}
	
	public abstract void doAction(Object ...objects);
	
	protected void updateUI(int what,Object...objects){
		Message msg = Message.obtain();
		msg.obj = objects;
		msg.what = what;
		updateUI(msg);
	}
}
