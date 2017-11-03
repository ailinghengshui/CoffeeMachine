package com.jingye.coffeemac.service.handler;

import android.util.Pair;

import com.jingye.coffeemac.service.handler.coffee.AddStockHandler;
import com.jingye.coffeemac.service.handler.coffee.AppDownloadResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.CancelTradeCartHandler;
import com.jingye.coffeemac.service.handler.coffee.CancelTradeHandler;
import com.jingye.coffeemac.service.handler.coffee.ExchangeCoffeeByCodeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.FetchCoffeeByCodeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.FetchCoffeeByQRResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.GetAdvPicsResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.GetCoffeeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.GetDosingListResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.GetMachineConfigResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.GetNoticeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.LogUploadResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.MachineStatusReportResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.MachineStatusServerResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.ActiveNoticeHandler;
import com.jingye.coffeemac.service.handler.coffee.PayNotifyResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.PayQrcodeCartResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.PayQrcodeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.PaySonicWaveResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.PayStatusCartResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.PayStatusResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.ReportErrorFetchResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.ResetStockHandler;
import com.jingye.coffeemac.service.handler.coffee.RollbackCartIndentResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.RollbackFetchCodeResponseHandler;
import com.jingye.coffeemac.service.handler.coffee.UpdateStockHandler;
import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.response.KeepAliveResponse;
import com.jingye.coffeemac.service.protocol.response.LoginResponse;
import com.jingye.coffeemac.service.protocol.response.LogoutResponse;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.ResponseID;
import com.jingye.coffeemac.service.protocol.response.coffee.AddStockResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.AppDownloadResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.CancelTradeCartResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.CancelTradeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.ExchangeCoffeeByCodeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.FetchCoffeeByCodeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.FetchCoffeeByQRResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.GetAdvPicResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.GetCoffeeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.GetDosingListResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.GetMachineConfigResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.GetNoticeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.LogUploadResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.MachineStatusReportResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.MachineStatusServerResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.ActiveNoticeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PayNotifyResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PayQrcodeCartResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PayQrcodeResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PaySonicWaveResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PayStatusAskCartResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.PayStatusAskResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.ReportErrorFetchResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.ResetStockResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.RollbackCartResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.RollbackFetchResponse;
import com.jingye.coffeemac.service.protocol.response.coffee.UpdateStockResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseFactory {
	private static ResponseFactory sInstance = new ResponseFactory();
	
	public static ResponseFactory getInstance() {
		return sInstance;
	}
	
	private ResponseFactory() {
		registerResponses();
	}
	
	private Map<Pair<Short, Short>, Class<? extends Response>> mResponses = new ConcurrentHashMap<Pair<Short, Short>, Class<? extends Response>>();
	private Map<Pair<Short, Short>, Integer> mPriorities = new ConcurrentHashMap<Pair<Short, Short>, Integer>();
	private Map<Class<? extends Response>, ResponseHandler> mHandlers = new ConcurrentHashMap<Class<? extends Response>, ResponseHandler>();

	private void registerResponses() {
		
		/**心跳管理**/
		registerResponse(KeepAliveResponse.class, null);

		/**登录部分**/
		registerResponse(LoginResponse.class, new LoginResponseHandler());
		registerResponse(LogoutResponse.class, new LogoutResponseHandler());
		
		/**咖啡部分**/
		registerResponse(GetCoffeeResponse.class, new GetCoffeeResponseHandler());
		registerResponse(FetchCoffeeByCodeResponse.class, new FetchCoffeeByCodeResponseHandler());
		registerResponse(PayNotifyResponse.class, new PayNotifyResponseHandler());
		registerResponse(RollbackFetchResponse.class, new RollbackFetchCodeResponseHandler());
		registerResponse(FetchCoffeeByQRResponse.class, new FetchCoffeeByQRResponseHandler());
        registerResponse(GetDosingListResponse.class, new GetDosingListResponseHandler());
        registerResponse(UpdateStockResponse.class, new UpdateStockHandler());
		registerResponse(AddStockResponse.class, new AddStockHandler());
		registerResponse(ResetStockResponse.class, new ResetStockHandler());
		registerResponse(ExchangeCoffeeByCodeResponse.class, new ExchangeCoffeeByCodeResponseHandler());

		/**运维管理**/
		registerResponse(MachineStatusServerResponse.class, new MachineStatusServerResponseHandler());
		registerResponse(MachineStatusReportResponse.class, new MachineStatusReportResponseHandler());
		registerResponse(AppDownloadResponse.class, new AppDownloadResponseHandler());
		registerResponse(GetMachineConfigResponse.class, new GetMachineConfigResponseHandler());
		registerResponse(LogUploadResponse.class, new LogUploadResponseHandler());
		registerResponse(ActiveNoticeResponse.class, new ActiveNoticeHandler());
		
		/**支付部分**/
		registerResponse(PayQrcodeResponse.class, new PayQrcodeResponseHandler());
		registerResponse(PayNotifyResponse.class, new PayNotifyResponseHandler());
		registerResponse(PayStatusAskResponse.class, new PayStatusResponseHandler());
		registerResponse(PaySonicWaveResponse.class, new PaySonicWaveResponseHandler());
        registerResponse(CancelTradeResponse.class, new CancelTradeHandler());
		registerResponse(PayQrcodeCartResponse.class, new PayQrcodeCartResponseHandler());
		registerResponse(PayStatusAskCartResponse.class, new PayStatusCartResponseHandler());
		registerResponse(CancelTradeCartResponse.class, new CancelTradeCartHandler());
		registerResponse(RollbackCartResponse.class, new RollbackCartIndentResponseHandler());
		registerResponse(ReportErrorFetchResponse.class, new ReportErrorFetchResponseHandler());

		/**广告**/
		registerResponse(GetAdvPicResponse.class, new GetAdvPicsResponseHandler());
		registerResponse(GetNoticeResponse.class, new GetNoticeResponseHandler());
	}
	
	private void registerResponse(Class<? extends Response> clazz, ResponseHandler handler) {
		ResponseID annotation = (ResponseID) clazz.getAnnotation(ResponseID.class);
		if (annotation == null) {
			return;
		}
		
		short sid = annotation.service();
		String[] commands = annotation.command();
		if (commands != null && commands.length != 0) {
			for (String command : commands) {
				String[] parts = command.split("#");
				
				if (parts != null && parts.length != 0) {
					short cid = Short.parseShort(parts[0]);

					if (parts.length >= 2) {
						int priority = Integer.parseInt(parts[1]);
						mPriorities.put(new Pair<Short, Short>(sid, cid), priority);
					}
					
					mResponses.put(new Pair<Short, Short>(sid, cid), clazz);
				}				
			}
		}

		if (handler != null) {
			mHandlers.put(clazz, handler);
		}
	}
	
	public boolean existsResponse(LinkFrame header) {		
		return header == null || mResponses == null ? false : mResponses.containsKey(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public Class<? extends Response> queryResponseClass(LinkFrame header) {		
		return header == null || mResponses == null ? null : mResponses.get(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public Integer queryResponsePriority(LinkFrame header) {		
		return header == null || mPriorities == null ? null : mPriorities.get(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public ResponseHandler queryResponseHandler(Response response) {		
		return response == null || mHandlers == null ? null : mHandlers.get(response.getClass());
	}
	
	public Response newResponse(LinkFrame header) {	
		// query class
		Class<? extends Response> clazz = queryResponseClass(header);

		if (clazz == null) {
			return null;
		}
		
		try {
			// new
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
