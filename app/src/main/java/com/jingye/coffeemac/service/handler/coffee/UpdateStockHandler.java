package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.UpdateStockResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.request.coffee.UpdateStockRequest;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.log.LogUtil;

public class UpdateStockHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        boolean autoSycStock = false;
        if(task != null){
            UpdateStockRequest request = (UpdateStockRequest)task.getRequest();
            autoSycStock = request.isAuto();
            LogUtil.vendor("UpdateStockResponse-> retrive for UpdateStockRequest, auto is " + autoSycStock);
        }

        UpdateStockResult result = new UpdateStockResult();
        result.setAuto(autoSycStock);
        result.setResCode(response.getLinkFrame().resCode);

		postToUI(result.toRemote());
	}
}
