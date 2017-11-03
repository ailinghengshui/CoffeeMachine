package com.jingye.coffeemac.service.action;

import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.core.VendorCore;

public abstract class TAction implements IAction{
	
	public VendorCore core = VendorCore.sharedInstance();
	
	public void post2UI(Remote remote){
		core.notifyListener(remote);
	}
}
