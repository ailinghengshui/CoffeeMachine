package com.jingye.coffeemac.service.core;

import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.action.IAction;
import com.jingye.coffeemac.service.action.TActionFactory;
import com.jingye.coffeemac.util.log.LogUtil;

public class RequestDispatcher extends TaskExecutor {

	private static final String NAME = "Request";
	private static final int CORE = 3;
	private static final int MAX = 5;
	private static final int KEEP_ALIVE = 30 * 1000;

	public RequestDispatcher() {
		super(NAME, CORE, MAX, KEEP_ALIVE);
	}
	
	public void dispatch(Remote remote) {
		if (remote == null) {
			LogUtil.core("dispatch remote: remote is NULL");
			
			return;
		}
		
		TActionFactory factory = TActionFactory.newInstance();
		if (factory == null) {
			LogUtil.core("dispatch remote: NO factory");

			return;
		}
		
		int what = remote.getWhat();
		
		LogUtil.core("dispatch remote: what " + what);

		IAction action = factory.getAction(what);
		
		if (action == null) {
			LogUtil.core("dispatch remote: NO action");
			
			return;
		}
		
		dispatchAction(action, remote);
	}
	
	private void dispatchAction(final IAction action, final Remote remote) {
		execute(new Runnable() {
			@Override
			public void run() {
				executeAction(action, remote);
			}			
		});
	}
	
	private void executeAction(IAction action, Remote remote) {
		int what = remote.getWhat();
		
		LogUtil.core("execute remote: what " + what + " action: " + remote.getAction());
		
		action.execute(remote);
	}
}