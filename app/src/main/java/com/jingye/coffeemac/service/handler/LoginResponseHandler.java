package com.jingye.coffeemac.service.handler;

import com.jingye.coffeemac.service.bean.result.LoginResult;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.enums.IAuthService;
import com.jingye.coffeemac.service.protocol.response.LoginResponse;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.log.LogUtil;

public class LoginResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);
		
		LoginResult result = new LoginResult();
		result.setResCode(response.getLinkFrame().resCode);

		if (response.isSuccess()) {
			core.setLogined();
			
			LoginResponse loginResponse = (LoginResponse) response;
			int status = loginResponse.getStatus();
			if (status != IAuthService.LoginRetType.NOTUSER) {
				onAfterLogin();
			}
			result.setRetType(status);
			
			LogUtil.vendor("LOGIN RET TYPE is " + status);
            LogUtil.vendor("SESSION ID is " + loginResponse.getSessionId());
			core.setMySessionId(loginResponse.getSessionId());
            core.setLastVendorName(loginResponse.getVendorName());
		} else {
			LogUtil.vendor("ERROR: FAIL TO LOGIN");

			int reason = IAuthService.KickoutReason.LoginError;
			int resCode = response.getLinkFrame().resCode;
			if (resCode == ResponseCode.RES_EUIDPASS) {
				reason = IAuthService.KickoutReason.PwdWrong;
			} else if (resCode == ResponseCode.RES_ALREADY_LOGINED) {
				reason = IAuthService.KickoutReason.MacAddressError;
			} else if (resCode == ResponseCode.RES_ENONEXIST) {
				reason = IAuthService.KickoutReason.UserNotExist;
			}
			core.handleKickout(reason);
		}

		postToUI(result.toRemote());
	}

	private void onAfterLogin() {
//		core.startReportStatusTimer();
	}
}
