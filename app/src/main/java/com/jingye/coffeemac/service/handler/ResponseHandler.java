package com.jingye.coffeemac.service.handler;

import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.core.VendorCore;
import com.jingye.coffeemac.service.protocol.response.Response;


public abstract class ResponseHandler {
	abstract public void processResponse(Response response);
	
	protected VendorCore core = VendorCore.sharedInstance();
	protected void postToUI(Remote remote) {
		core.notifyListener(remote);
	}
}
