package com.jingye.coffeemac.service.action;

import android.text.TextUtils;

import com.jingye.coffeemac.net.http.MD5;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.LoginInfo;
import com.jingye.coffeemac.service.bean.action.LoginRequestInfo;
import com.jingye.coffeemac.service.bean.action.LogoutInfo;
import com.jingye.coffeemac.service.bean.action.VerifyPasswordInfo;
import com.jingye.coffeemac.service.bean.result.LogoutResult;
import com.jingye.coffeemac.service.bean.result.VerifyPasswordResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.request.KeepAliveRequest;
import com.jingye.coffeemac.service.protocol.request.LoginNewRequest;
import com.jingye.coffeemac.service.protocol.request.LogoutRequest;
import com.jingye.coffeemac.util.log.LogUtil;

public class UserAction extends TAction {

	@Override
	public void execute(Remote remote) {
		switch (remote.getAction()) {
		case ITranCode.ACT_USER_LOGIN_REQUEST:
			requestLogin(remote);
			break;
		case ITranCode.ACT_USER_LOGIN:
			login(remote);
			break;
		case ITranCode.ACT_USER_LOGOUT:
			logout(remote);
			break;
		case ITranCode.ACT_USER_KEEPALIVE:
			requestKeepalive(remote);
			break;
        case ITranCode.ACT_USER_VERIFY_PWD:
            verifyPwd(remote);
            break;
		default:
			LogUtil.vendor("don't recognized user action: " + remote.getAction());
			break;
		}
	}

	@Override
	public int getWhat() {
		return ITranCode.ACT_USER;
	}
	
	private void requestLogin(Remote remote){
		if (!core.isLogined()) {
			LoginRequestInfo info = Ancestor.parseObject(remote.getBody());
			core.setMyVendorNum(info.getUid());
			if (!core.isConnected()) {
				connectServer();
			}
			core.login(info.getPassword());
		}
	}
	
	private void login(Remote remote) {
		if (!core.isLogined()) {
			try {
				LoginInfo loginInfo = Ancestor.parseObject(remote.getBody());
				
				core.setMyVendorNum(loginInfo.getUid());
				if (!core.isConnected()) {
					connectServer();
				}
				
				if (core.isConnected()) {
					LoginNewRequest lr = generateLoginRequest(loginInfo);
					core.sendRequestToServer(lr);
					
					// TODO make it more intelligent
					core.setLastVendorNum(loginInfo.getUid());
					core.setLastVendorPwd(loginInfo.getPassword());
				} else {
					LogUtil.vendor("not login because of connection failed");
				}
			} catch (Exception e) { // TODO: 通知上层，登录失败
			}
		}
	}	

	private void logout(Remote remote) {
		LogoutInfo info = Ancestor.parseObject(remote.getBody());
		if(info != null ){
			String password = info.getPassword();
			LogoutRequest request = new LogoutRequest(info.getUid());
			request.setPassword(MD5.md5(password));
			core.sendRequestToServer(request);
			core.addRequestRetryTimer(new ResendRequestTask(request) {
				@Override
				public void onTimeout() {
					LogUtil.vendor("[UserAction] logout->timeout");

					LogoutResult result = new LogoutResult();
					result.setResCode(ResponseCode.RES_ETIMEOUT);
					core.notifyListener(result.toRemote());
				}
			}, 0, 15);
		}
	}

	private void connectServer() {
		// 先判断网络有没有连上
		int retryCount = 0;
		while (!core.isConnected() && retryCount < 30) {
			if (retryCount % 30 == 0) { // 每隔30s才重新连一次  //check here
				core.connect();
			}
			++retryCount;
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				LogUtil.vendor("connect thread was interrupt");
			}
		}

		if (!core.isConnected()) { // 还是没有连上
			// 通知上层
			LogUtil.vendor("server is not available, please check the network");

			// 网络断掉
			core.disconnect();
		}
	}

//	private LoginRequest generateLoginRequest(LoginInfo loginInfo) {
//		LoginRequest lr = new LoginRequest(loginInfo.getUid());
//		lr.setPassword(loginInfo.getPassword());
//
//		return lr;
//	}

	private LoginNewRequest generateLoginRequest(LoginInfo loginInfo) {
		LoginNewRequest lr = new LoginNewRequest(loginInfo.getUid());
		lr.setPassword(loginInfo.getPassword());
		String macAddress = loginInfo.getMacAddress();
		LogUtil.vendor("macAddress is " + macAddress);
		lr.setMacAddress(macAddress);

		return lr;
	}

	private void requestKeepalive(Remote remote) {
		KeepAliveRequest request = new KeepAliveRequest(core.getMyVendorNum());
		core.sendRequestToServer(request);
	}

    private void verifyPwd(Remote remote){
        VerifyPasswordInfo info = Ancestor.parseObject(remote.getBody());
        boolean ret = false;
        if(info != null ){
            String password = info.getPassword();
            String vendorPwd = core.getLastVendorPwd();
            if(!TextUtils.isEmpty(vendorPwd) && vendorPwd.equals(MD5.md5(password))){
                ret = true;
            }else if(TextUtils.isEmpty(vendorPwd)){
				ret = true;
			}
        }

        VerifyPasswordResult result = new VerifyPasswordResult();
        result.setCorrect(ret);
        result.setType(info.getType());

        core.notifyListener(result.toRemote());
    }
}
