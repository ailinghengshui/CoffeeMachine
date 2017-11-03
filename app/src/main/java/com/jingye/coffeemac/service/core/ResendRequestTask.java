package com.jingye.coffeemac.service.core;

import com.jingye.coffeemac.service.protocol.request.Request;

/**
 * 重试发包的任务接口类，重试时需要的具体操作由具体的人物类自己实现
 */
public abstract class ResendRequestTask {
	protected VendorCore core = VendorCore.sharedInstance();
	
	protected Request request;
	
	public ResendRequestTask(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
	
	/**
	 * retry
	 * 
	 * default implement sends request again
	 * 
	 * @return whether retry again
	 */
	public boolean retry() {
		// send again
		resend(request);
		
		return true;
	}
	
	/**
	 * reach max retry count
	 */
	public abstract void onTimeout();
	
	/**
	 * send request again
	 * 
	 * @param request
	 */
	public static void resend(Request request) {
		VendorCore.sharedInstance().sendRequestToServer(request);
	}
}
