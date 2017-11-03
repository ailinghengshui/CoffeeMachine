package com.jingye.coffeemac.service.core;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.jingye.coffeemac.helper.threadpool.ThreadPool;
import com.jingye.coffeemac.net.client.NetworkEnums;
import com.jingye.coffeemac.net.client.NioClient;
import com.jingye.coffeemac.net.netty.NioResponse;
import com.jingye.coffeemac.service.IRemoteListener;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.RebootNotify;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.LoginInfo;
import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.NetworkUtil;
import com.jingye.coffeemac.util.SharePrefConfigCore;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.Context;
import android.text.TextUtils;

public class VendorCore {
	
	private String myUserid;	
	private String mySessionId;
	private AtomicInteger mSerialId = new AtomicInteger(2);
	
	/**
	 * status
	 */
	public static final int[] sRetainReasons = new int[] {ITranCode.STATUS_FORBIDDEN, ITranCode.STATUS_KICKOUT};
	private AtomicBoolean mInLogin = new AtomicBoolean();
	private AtomicBoolean mLogined = new AtomicBoolean();
	private AtomicInteger mReason = new AtomicInteger(ITranCode.STATUS_UNLOGIN);
	
	private Context context;
	private boolean isUiProcessAlive = false;

	private IRemoteListener listener;

	/** network keeper */
	private NetworkKeeper networkKeeper;

	/** request dispatcher */
	private RequestDispatcher mRequestDispatcher = new RequestDispatcher();
	
	/** response dispatcher */
	private ResponseDispatcher mResponseDispatcher = new ResponseDispatcher();
	
	/** transaction manager */
	private TransactionManager mTransactionManager = new TransactionManager();
	
	/** 网络设备状态变化时延时通知 */
	private Timer mDelayTimer;
	private static final int DELAY = 5 * 1000;
	
	/** client */
	private NioClient mClient = new NioClient(new NioClient.Callback() {
		@Override
		public void onState(int state) {
			switch (state) {
			case NioClient.STATE_READY:
				onNetworkEvent(NetworkEnums.Event.CONN_ESTABLISHED);
				break;
			case NioClient.STATE_DISCONNECTED:
				onNetworkEvent(NetworkEnums.Event.CONN_BROKEN);
				break;
			}
		}
		
		@Override
		public void onPacket(NioResponse response) {
			// receive response
			LogUtil.core("onPacket....");
			receiveResponse(response);
		}
	});

	private static VendorCore instance = new VendorCore();
	
	public static VendorCore sharedInstance() {
		return instance;
	}

	private VendorCore() {
	}

	/**
	 * startup core
	 * 
	 * startup components of core
	 *
	 */
	public void startup() {
		LogUtil.core("startup");
		
		mRequestDispatcher.startup();
		mResponseDispatcher.startup();
		mTransactionManager.startup();
		
		LogUtil.core("login with last");
		
		login(false);
	}
	
	/**
	 * shutdown core
	 * shutdown components of core
	 */
	public void shutdown() {
		LogUtil.core("shutdown");
			
		disconnect();

		// STATUS_UNLOGIN
		updateReason(ITranCode.STATUS_UNLOGIN, false);
		
		// stop network delay timer
		stopDelayTimer();

		if (networkKeeper != null) {
			networkKeeper.stopWork();
			networkKeeper = null;
		}
		
		mRequestDispatcher.shutdown();
		mResponseDispatcher.shutdown();
		mTransactionManager.shutdown();
	}

	/**
	 * @link {@link #request(Remote)}
	 * @param remote
	 */
	public void sendPacket(Remote remote) {
		request(remote);
	}
	
	/**
	 * make a remote request
	 * 
	 * @param remote request, may come from service
	 */
	public void request(Remote remote) {
		// validate
		if (mRequestDispatcher == null) {
			return;
		}
		
		// dispatch
		mRequestDispatcher.dispatch(remote);
	}

	public void setListener(IRemoteListener listener) {
		this.listener = listener;
	}

	public void notifyListener(Remote remote) {
		if (listener != null) {
			listener.onReceive(remote);
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getMyVendorNum() {
		return myUserid;
	}
	
	public void setMyVendorNum(String vendorNum) {
		myUserid = vendorNum;
	}

	public String getLastVendorNum() {
		return SharePrefConfigCore.getInstance(getContext()).getLastUserId();
	}
	
	public boolean hasLastVendorNum() {
		return !TextUtils.isEmpty(getLastVendorNum());
	}

	public void setLastVendorNum(String vendorNum) {
		SharePrefConfigCore.getInstance(getContext()).setLastUserId(vendorNum);
		U.saveMyVendorNum(vendorNum);
	}

    public void setLastVendorName(String vendorName){
        if(!TextUtils.isEmpty(vendorName)){
            U.saveMyVendorName(vendorName);
        }
    }
	
	public void setLastVendorPwd(String phonePwd){
		SharePrefConfigCore.getInstance(getContext()).setLastUserPwd(phonePwd);
	}
	
	public String getLastVendorPwd() {
		return SharePrefConfigCore.getInstance(getContext()).getLastUserPwd();
	}
	
	public String getMySessionId() {
		if(TextUtils.isEmpty(mySessionId)){
			String lastSessionId = SharePrefConfigCore.getInstance(getContext()).getLastSessionId();
			this.mySessionId = lastSessionId;
		}
		return mySessionId;
	}

	public void setMySessionId(String mySessionId) {
		this.mySessionId = mySessionId;
		SharePrefConfigCore.getInstance(getContext()).setLastSessionId(mySessionId);
	}

	public boolean isConnected() {
		return mClient.isConnected();
	}
	
	public boolean isInLogin() {
		return mInLogin.get();
	}

	private boolean setInLogin(boolean in) {		
		boolean set = mInLogin.compareAndSet(!in, in);
		
		if (set) {
			LogUtil.core("set INLOGIN " + in);
		}
		
		return set;
	}
	
	public boolean isLogined() {
		return mLogined.get();
	}
	
	public void setLogined() {
		// set
		setLogined(true);
		
		// reset
		resetReason();

		// notify
		notifyStatus(ITranCode.STATUS_LOGINED);

		// stop
		if (networkKeeper != null) {
			networkKeeper.stopReconnect();
		}
	}
	
	private boolean setLogined(boolean in) {
		// finish in login
		setInLogin(false);
		
		// set login status
		boolean set =  mLogined.compareAndSet(!in, in);
		
		if (set) {
			LogUtil.core("set LOGINED " + in);
		}

		return set;
	}
	
	private boolean canLogin() {
		return mReason.get() != ITranCode.STATUS_KICKOUT;
	}
	
	private void resetReason() {
		LogUtil.core("reset reason");
		
		// reset
		mReason.set(ITranCode.STATUS_UNLOGIN);
	}
	
	private void updateReason(int reason, boolean notify) {	
		// retain
		if (!isRetainReason(reason) && isRetainReason(mReason.get())) {
			return;
		}
		
		LogUtil.core("update reason " + reason);

		// set
		mReason.set(reason);

		// notify
		if (notify) {
			notifyStatus(reason);
		}
	}

	public boolean isUiProcessAlive() {
		return isUiProcessAlive;
	}

	public void setUiProcessAlive(boolean isUiProcessAlive) {
		this.isUiProcessAlive = isUiProcessAlive;
	}
	
	public void addRequestRetryTimer(ResendRequestTask task, int retry, int period) {
		mTransactionManager.pend(task.getRequest(), task, period, retry);
	}

	public ResendRequestTask cancelRequestRetryTimer(int sid) {
		return mTransactionManager.revoke2(sid);
	}
	
	public void pendRequest(Request request, int retry, int period) {
		mTransactionManager.pend(request, null, period, retry);
	}
	
	public Request revokeRequest(int sid) {
		return mTransactionManager.revoke(sid);
	}

	public boolean sendRequestToServer(Request request) {
		boolean accept = false;
		
		if (isLogined() || (request.getLinkFrame().serviceId == ServiceID.SVID_LITE_AUTH && isConnected())) {
			request.getLinkFrame().serialId = getSerialId();
			mClient.sendPacket(request);
			
			accept = true;
		} else {
			// TODO: maybe we need reconnect here
			if (canLogin() && hasLastVendorNum()) {
				login(false);
				LogUtil.core("login because of send request but network is broken");
			}
		}
		
		return accept;
	}
	
	private void receiveResponse(NioResponse response) {
		mResponseDispatcher.dispatchPacket(response);
	}

	public void connect() {
		if (TextUtils.isEmpty(getMyVendorNum())) {
			LogUtil.d("core", "connect but uid is null");
			return;
		}
		
		// user
		mClient.setUid(getMyVendorNum());
	
		// connect
		mClient.connect();
	}
	
	public void disconnect() {
		// disconnect
		mClient.disconnect();

		// state out
		setLogined(false);
	}
	
	public void login(boolean fireEvent) {
		// notify only when delay timer has expired
		fireEvent &= (mDelayTimer == null);
		
		login(null, fireEvent);
	}

	
	public void login(String password) {
		login(password, false);
	}

	public void logout() {
		disconnect();

		// STATUS_UNLOGIN notify
		updateReason(ITranCode.STATUS_UNLOGIN, true);
	}

	public void handleKickout(int reason) {
		LogUtil.core("handle kick, reason is " + reason);

		/**
		 * reset user
		 */
		String isLogined = U.queryAppSet(U.KEY_USER_IS_LOGINED, getMyVendorNum());
		LogUtil.e("core", "isLogined is " + isLogined);
		if(!isLogined.equals("true")){
			setMyVendorNum("");

			setLastVendorNum("");
			setLastVendorName("");
			setLastVendorPwd("");
		}

		/**
		 * disconnect anyway
		 */
		disconnect();

		/**
		 * status notify
		 */
		updateReason(ITranCode.STATUS_CONNECT_FAILED, true);
	}

	public void onNetworkEvent(NetworkEnums.Event event) {
		LogUtil.core("on network event " + event);
		
		switch (event) {
		case CONN_ESTABLISHED:
			onConnectionEstablished();
			break;
		case CONN_BROKEN:
			onConnectionBroken();
			break;
		case KEEP_ALIVE_TIMEOUT:
			onKeepAliveTimeout();
			break;
		case NETWORK_UNAVAILABLE:
			onNetworkUnavailable();
			break;
		case NETWORK_AVAILABLE:
			onNetworkAvailable();
			break;
		case NETWORK_CHANGE:
			onNetworkChange();
			break;
		default:;
		}
	}

	private void onConnectionEstablished() {
		if (hasLastVendorNum()) {
			requestLogin(getLastVendorPwd());
		}
	}

	private void onConnectionBroken() {
		if (canLogin() && hasLastVendorNum()) {
			if (networkKeeper != null) {
				networkKeeper.startReconnect();
			}
		}
		
		// if current state is LOGINED, delay to notify
		if (mLogined.get()) {
			startDelayTimer();
		}
		
		// state out
		setLogined(false);

		// STATUS_CONNECT_FAILED
		int status = (networkKeeper != null && networkKeeper.isReachable() ? ITranCode.STATUS_CONNECT_FAILED
				: ITranCode.STATUS_NO_NETWORK);
		
		/** notify only when delay timer has expired */
		updateReason(status, mDelayTimer == null);
	}

	private void onKeepAliveTimeout() {	
		// disconnect
		disconnect();

		// notify STATUS_CONNECT_FAILED
		updateReason(ITranCode.STATUS_CONNECT_FAILED, true);
		
		// 开始自动重连
		if (canLogin() && hasLastVendorNum()) {
			if (networkKeeper != null) {
				networkKeeper.startReconnect();
			}
		}
	}

	private void onNetworkUnavailable() {
		// if current state is LOGINED, delay to notify
		if (mLogined.get()) {
			startDelayTimer();
		}
		
		// disconnect
		disconnect();
		
		if (networkKeeper != null) {
			networkKeeper.startReconnect();
		}

		// STATUS_NO_NETWORK notify
		updateReason(ITranCode.STATUS_NO_NETWORK, mDelayTimer == null);
	}

	private void onNetworkAvailable() {
		// skip
		if (isLogined() || isInLogin()) {
			return;
		}
	
		// STATUS_UNLOGIN 
		updateReason(ITranCode.STATUS_UNLOGIN, false);
		
		if (canLogin() && hasLastVendorNum()) {
			LogUtil.core("network available, login");
			
			login(true);
		}
		
		stopDelayTimer();
	}

	private void onNetworkChange() {
		/** keep current state as not to notify if i'm logined now*/
		if (mLogined.get()) {
			startDelayTimer();
		}
		
		if (canLogin() && hasLastVendorNum()) {
			// disconnect
			disconnect();
			
			// STATUS_UNLOGIN
			updateReason(ITranCode.STATUS_UNLOGIN, false);
			
			// 重新连接
			if (networkKeeper != null) {
				networkKeeper.stopReconnect();
				networkKeeper.startReconnect();
			}
		}
	}

	private void login(String password, boolean fireEvent) {

		if (isLogined() || isInLogin())
			return;

		if (canLogin() && hasLastVendorNum() || !TextUtils.isEmpty(password)) {
			
			// 启动连接保持模块
			if (networkKeeper == null) {
				networkKeeper = new NetworkKeeper(context);
				networkKeeper.startWork();
			}
			
			if (!networkKeeper.isReachable()) {
				updateReason(ITranCode.STATUS_NO_NETWORK, mDelayTimer == null);
				return;
			}

			// 已经有用户了,登录去			
			String vendorNum = getLastVendorNum();
			if (!TextUtils.isEmpty(vendorNum)) {
				setMyVendorNum(vendorNum);
			}
			
			String vendorPwd = password;
			if(TextUtils.isEmpty(vendorPwd)){
				vendorPwd = getLastVendorPwd();
			}
			
			if (fireEvent) {
				notifyStatus(ITranCode.STATUS_LOGGING);
			}
			
			if (isConnected()) {
				requestLogin(vendorPwd);
			} else {
				// 网络操作放到线程池去做
				ThreadPool.getInstance().addTask(new Runnable() {
					
					@Override
					public void run() {
						connect();
					}
				});
			}
		}
	}

	private void requestLogin(String password) {
		if (isLogined()) {
			LogUtil.core("request login: logined");
			return;
		}
		
		String vendorNum = getMyVendorNum();
		if (TextUtils.isEmpty(vendorNum)) {
			LogUtil.core("request login: phone number absent");
			return;
		}
		
		if(TextUtils.isEmpty(password)) {
			LogUtil.core("request login: phone password absent");
			return;
		}
		
		if (!setInLogin(true)) {
			LogUtil.core("request login: in progress");
			return;
		}
		
		LoginInfo info = new LoginInfo();
		info.setUid(vendorNum);
		info.setPassword(password);
		info.setMacAddress(NetworkUtil.getActiveMacAddress(context));
		request(info.toRemote());

		LogUtil.core("request login: send for " + vendorNum + "," + password);
	}

    public void requestReboot(){
        RebootNotify notify = new RebootNotify();
        notifyListener(notify.toRemote());

        LogUtil.core("ca, request reboot coffee machine");
    }

	private short getSerialId() {
		short sid = (short)mSerialId.getAndAdd(1);
		
		if (sid == 0 || sid == 1) {
			sid = (short)mSerialId.getAndSet(2);
		}
		
		return sid;
	}

	private void notifyStatus(int status) {
		U.setUserStatus(context, status);

		StatusChangeNotify notify = new StatusChangeNotify();
		notify.setStatus(status);
		notifyListener(notify.toRemote());

		LogUtil.core("notify status " + status);
	}
	
	private static boolean isRetainReason(int reason) {
		for (int r : sRetainReasons) {
			if (r == reason) {
				return true;
			}
		}
		
		return false;
	}

	private void startDelayTimer() {
		if (mDelayTimer != null) {
			return;
		}

		mDelayTimer = new Timer();
		mDelayTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				onDelay();
			}
		}, DELAY);
	}

	private void stopDelayTimer() {
		if (mDelayTimer == null) {
			return;
		}

		mDelayTimer.cancel();
		mDelayTimer = null;
	}
	
	private void onDelay() {
		stopDelayTimer();
		if (networkKeeper!=null && !networkKeeper.isReachable()) {
			updateReason(ITranCode.STATUS_NO_NETWORK, true);
		}
	}
}
