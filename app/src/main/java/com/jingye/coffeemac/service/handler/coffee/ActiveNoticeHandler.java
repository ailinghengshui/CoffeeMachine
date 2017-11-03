package com.jingye.coffeemac.service.handler.coffee;

import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.ActiveNoticeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

/**
 * Created by dblr4287 on 2016/7/7.
 */
public class ActiveNoticeHandler extends ResponseHandler {

    @Override
    public void processResponse(Response response) {

        ActiveNoticeResult result = new ActiveNoticeResult();
        result.setResCode(response.getLinkFrame().resCode);

        if (response.isSuccess()) {
            ActiveNoticeResponse activenoticeresponse = (ActiveNoticeResponse) response;

            String type = activenoticeresponse.getType();

            LogUtil.vendor("successfully receive ActiveNotice log request: " + ", " + type);
            result.setType(type);
        }

        postToUI(result.toRemote());
    }
}
