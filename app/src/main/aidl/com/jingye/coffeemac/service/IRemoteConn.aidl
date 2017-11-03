package com.jingye.coffeemac.service;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.IRemoteConnCall;
interface IRemoteConn {
    boolean isTaskRunning();   
    void stopRunningTask(); 
	void registerCallback(IRemoteConnCall remoteConn); 
	void unregisterCallback(IRemoteConnCall remoteConn); 
	void send(in Remote remote);
}