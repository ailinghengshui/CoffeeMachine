package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.GetDosingResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.marshal.Marshallable;
import com.jingye.coffeemac.service.protocol.marshal.Property;
import com.jingye.coffeemac.service.protocol.request.coffee.GetDosingListRequest;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.GetDosingListResponse;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.List;

public class GetDosingListResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        boolean autoSycStock = false;
        if(task != null){
            GetDosingListRequest request = (GetDosingListRequest)task.getRequest();
            autoSycStock = request.isAuto();
            LogUtil.vendor("GetDosingListResponse-> retrive for GetDosingListRequest, auto is " + autoSycStock);
        }

        GetDosingResult result = new GetDosingResult();
		result.setResCode(response.getLinkFrame().resCode);
        result.setAuto(autoSycStock);
		if (response.isSuccess()) {
            GetDosingListResponse dosingListResponse = (GetDosingListResponse) response;
			List<Marshallable> dosings = dosingListResponse.getCoffeeDosingList().list;
			LogUtil.vendor("收到配料种类为:" + dosings.size());
			for(int i = 0; i < dosings.size(); i++) {
				Property dosing = (Property) dosings.get(i);
				int id = dosing.getInteger(ICoffeeService.DosingType.COFFEE_TYPE_ID);
				String title = dosing.get(ICoffeeService.DosingType.COFFEE_TYPE_TITLE);
//				double stock = Double.parseDouble(dosing.get(ICoffeeService.DosingType.COFFEE_TYPE_STOCK));
                int boxID = dosing.getInteger(ICoffeeService.DosingType.COFFEE_TYPE_BOX_ID);
                double machineConfugured=1.1;
                if(dosing.contains(ICoffeeService.DosingType.MACHINE_CONFUGURED)) {
                    machineConfugured=dosing.getDouble(ICoffeeService.DosingType.MACHINE_CONFUGURED);
                }

                LogUtil.vendor("dosingID: " + id + ", dosingName: " + title + ", boxID: " + boxID);

                CoffeeDosingInfo info = new CoffeeDosingInfo();
                info.setId(id);
                info.setName(title);
                info.setBoxID(boxID);
                info.setFactor(machineConfugured);
				result.addDosing(info);
			}
		} 

		postToUI(result.toRemote());
	}
}
