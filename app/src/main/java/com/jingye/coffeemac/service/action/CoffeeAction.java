package com.jingye.coffeemac.service.action;

import android.text.TextUtils;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.AddStockInfo;
import com.jingye.coffeemac.service.bean.action.AppDownloadInfo;
import com.jingye.coffeemac.service.bean.action.CancelTradeCartInfo;
import com.jingye.coffeemac.service.bean.action.CancelTradeInfo;
import com.jingye.coffeemac.service.bean.action.ExchangeCoffeeByCodeInfo;
import com.jingye.coffeemac.service.bean.action.FetchCoffeeByCodeInfo;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.action.GetDiscountInfo;
import com.jingye.coffeemac.service.bean.action.GetDosingListInfo;
import com.jingye.coffeemac.service.bean.action.GetMachineConfigInfo;
import com.jingye.coffeemac.service.bean.action.GetNoticeInfo;
import com.jingye.coffeemac.service.bean.action.MachineStatusReportInfo;
import com.jingye.coffeemac.service.bean.action.PayQrcodeCartInfo;
import com.jingye.coffeemac.service.bean.action.PayQrcodeInfo;
import com.jingye.coffeemac.service.bean.action.PaySonicWaveInfo;
import com.jingye.coffeemac.service.bean.action.PayStatusAskCartInfo;
import com.jingye.coffeemac.service.bean.action.PayStatusAskInfo;
import com.jingye.coffeemac.service.bean.action.ReportErrorFetchInfo;
import com.jingye.coffeemac.service.bean.action.ResetStockInfo;
import com.jingye.coffeemac.service.bean.action.RollbackFetchByCodeInfo;
import com.jingye.coffeemac.service.bean.action.RollbackCoffeeIndentCart;
import com.jingye.coffeemac.service.bean.action.SyncStockInfo;
import com.jingye.coffeemac.service.bean.result.AddStockResult;
import com.jingye.coffeemac.service.bean.result.GetAdvPicsResult;
import com.jingye.coffeemac.service.bean.result.GetCoffeeResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.bean.result.GetDosingResult;
import com.jingye.coffeemac.service.bean.result.GetMachineConfigResult;
import com.jingye.coffeemac.service.bean.result.ResetStockResult;
import com.jingye.coffeemac.service.bean.result.UpdateStockResult;
import com.jingye.coffeemac.service.core.ResendRequestTask;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.request.coffee.AddStockRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.ExchangeCoffeeByCodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.ExchangeCoffeeByCodeRetryTask;
import com.jingye.coffeemac.service.protocol.request.coffee.FetchCoffeeByCodeRetryTask;
import com.jingye.coffeemac.service.protocol.request.coffee.GetNoticeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.ReportErrorFetchRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.ResetStockRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.AppDownloadRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.CancelTradeCartRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.CancelTradeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.GetAdvPicRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.GetCoffeeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.GetDosingListRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.GetMachineConfigRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.MachineStatusReportRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.PayAliSonicWaveRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeCartRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeCartRetryTask;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.PayQrcodeRetryTask;
import com.jingye.coffeemac.service.protocol.request.coffee.PayStatusAskCartRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.PayStatusAskRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.RollbackCartRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.RollbackFetchCodeRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.RollbackRetryTask;
import com.jingye.coffeemac.service.protocol.request.coffee.UpdateStockRequest;
import com.jingye.coffeemac.service.protocol.request.coffee.FetchCoffeeByCodeRequest;
import com.jingye.coffeemac.util.log.LogUtil;

public class CoffeeAction extends TAction {

	@Override
	public void execute(Remote remote) {
		switch (remote.getAction()) {
		case ITranCode.ACT_COFFEE_GET_COFFEE:
			getCoffeeInfos(remote);
			break;
		case ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_CODE:
			fetchCoffeeByCode(remote);
			break;
		case ITranCode.ACT_COFFEE_ROLL_BACK_FETCH_COFFEE_BY_CODE:
			rollbackFetchCoffeeByCode(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_QRCODE:
			payCoffeeQrcode(remote);
			break;
		case ITranCode.ACT_COFFEE_ASK_PAY_RESULT:
			payStatusAsk(remote);
			break;
		case ITranCode.ACT_COFFEE_ASK_CART_PAY_RESULT:
			payStatusAskCart(remote);
			break;
		case ITranCode.ACT_COFFEE_APP_DOWNLOAD:
			getAppDownloadURL(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_SONICWAVE:
			paySonicWave(remote);
			break;
		case ITranCode.ACT_COFFEE_REPORT_STATUS:
			reportStatus(remote);
			break;
        case ITranCode.ACT_COFFEE_CANCEL_TRADE:
            cancelTrade(remote);
            break;
		case ITranCode.ACT_COFFEE_CANCEL_TRADE_CART:
			cancelTradeCart(remote);
			break;
        case ITranCode.ACT_COFFEE_STOCK_UPDATE:
            updateStock(remote);
            break;
		case ITranCode.ACT_COFFEE_STOCK_RESET:
			resetStock(remote);
			break;
        case ITranCode.ACT_COFFEE_DOSING_LIST:
            getCoffeeDosingList(remote);
            break;
		case ITranCode.ACT_COFFEE_STOCK_ADD:
			addStock(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_QRCODE_CART:
			PayCoffeeQrcodeCart(remote);
			break;
		case ITranCode.ACT_COFFEE_ROLL_BACK_CART:
			rollbackCartIndents(remote);
			break;
		case ITranCode.ACT_COFFEE_GET_MACHINE_CONIFG:
			getMachineConfig(remote);
			break;
		case ITranCode.ACT_COFFEE_GET_ADV_PICS:
			getAdvPics(remote);
			break;
		case ITranCode.ACT_COFFEE_REPORT_ERROR_FETCH:
			reportErrorFetch(remote);
			break;
		case ITranCode.ACT_COFFEE_EXCHANGE_COFFEE:
			exchangeCoffee(remote);
			break;
		case ITranCode.ACT_COFFEE_NOTICE:
			getNotice(remote);
			break;
		default:
			LogUtil.vendor("don't recognized coffee action: " + remote.getAction());
			break;
		}
	}

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}
	
	private void getCoffeeInfos(Remote remote){
		GetCoffeeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		GetCoffeeRequest request = new GetCoffeeRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getCoffeeInfos->timeout");

				GetCoffeeResult result = new GetCoffeeResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 1, 15);
	}
	
	private void fetchCoffeeByCode(Remote remote){
		FetchCoffeeByCodeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String fetchCode = info.getFetchCode();
		long timestamp = info.getTimestamp();
		LogUtil.vendor("fetch coffee by code request: " + fetchCode + ", " + timestamp);
		FetchCoffeeByCodeRequest request = new FetchCoffeeByCodeRequest(info.getUid(), fetchCode, timestamp);
		core.sendRequestToServer(request);
		FetchCoffeeByCodeRetryTask task = new FetchCoffeeByCodeRetryTask(request);
		core.addRequestRetryTimer(task, 0, 15);
	}

	private void exchangeCoffee(Remote remote){
		ExchangeCoffeeByCodeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		String fetchCode = info.getFetchCode();
		long timestamp = info.getTimestamp();
		LogUtil.vendor("exchange coffee by code request: " + fetchCode + ", " + timestamp);
		ExchangeCoffeeByCodeRequest request = new ExchangeCoffeeByCodeRequest(info.getUid(), fetchCode, timestamp);
		core.sendRequestToServer(request);
		ExchangeCoffeeByCodeRetryTask task = new ExchangeCoffeeByCodeRetryTask(request);
		core.addRequestRetryTimer(task, 0, 15);
	}
	
	private void rollbackFetchCoffeeByCode(Remote remote) {
		RollbackFetchByCodeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String fetchCode = info.getFetchCode();
		long timestamp = info.getTimestamp();
		LogUtil.vendor("rollback coffee code request for fetchCode " + fetchCode + " at " + timestamp);
		RollbackFetchCodeRequest request = new RollbackFetchCodeRequest(info.getUid(),
				fetchCode, timestamp);
		core.sendRequestToServer(request);
		RollbackRetryTask task = new RollbackRetryTask(request);
		core.addRequestRetryTimer(task, 1, 15);
	}
	
	private void payCoffeeQrcode(Remote remote){
		PayQrcodeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		int coffeeId = info.getCoffeeId();
		String dosing = info.getDosing();
		short provider = info.getProvider();
		LogUtil.vendor("pay coffee by qrcode: " + "[" + coffeeId + ", " + dosing + ", " + provider + "]");
		PayQrcodeRequest request = new PayQrcodeRequest(info.getUid(), coffeeId, dosing, provider);
		core.sendRequestToServer(request);	
		PayQrcodeRetryTask task = new PayQrcodeRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}

	private void PayCoffeeQrcodeCart(Remote remote){
		PayQrcodeCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		String coffeeIndents = info.getCoffeeIndents();
		short provider = info.getProvider();
		PayQrcodeCartRequest request = new PayQrcodeCartRequest(info.getUid(), coffeeIndents, provider);
		core.sendRequestToServer(request);
		PayQrcodeCartRetryTask task = new PayQrcodeCartRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}
	
	private void paySonicWave(Remote remote){
		PaySonicWaveInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		int coffeeId = info.getCoffeeId();
		String dosing = info.getDosing();
		short provider = info.getProvider();
		String dynamicID = info.getDynamicID();
		LogUtil.vendor("pay ali sonicwave: " + "[" + coffeeId + ", " + dosing + ", " + provider + ", " + dynamicID + "]");
		PayAliSonicWaveRequest request = new PayAliSonicWaveRequest(info.getUid(), coffeeId, dosing, provider, dynamicID);
		core.sendRequestToServer(request);	
	}
	
	private void payStatusAsk(Remote remote){
		PayStatusAskInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String coffeeIndent = info.getCoffeeIndent();
		LogUtil.vendor("ask pay status for indent: " + coffeeIndent);
		PayStatusAskRequest request = new PayStatusAskRequest(info.getUid(), coffeeIndent);
		core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] payStatusAsk->timeout");
			}
		}, 0, 30);
	}

	private void payStatusAskCart(Remote remote){
		PayStatusAskCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		String payIndent = info.getPayIndent();
		LogUtil.vendor("ask pay status for indent: " + payIndent);
		PayStatusAskCartRequest request = new PayStatusAskCartRequest(info.getUid(), payIndent);
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] payStatusAskCart->timeout");
			}
		}, 0, 15);
	}
	
	private void getAppDownloadURL(Remote remote){
		AppDownloadInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		AppDownloadRequest request = new AppDownloadRequest(info.getUid());
		core.sendRequestToServer(request);	
	}
	
	private void reportStatus(Remote remote){
		MachineStatusReportInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		long timestamp = info.getTimestamp();
		String machineStatusJson = info.toJsonArray();
		LogUtil.vendor("report machine status to server: " + timestamp + " -> " + machineStatusJson);
		if(!TextUtils.isEmpty(machineStatusJson)){
			MachineStatusReportRequest request = new MachineStatusReportRequest(info.getUid(), timestamp, machineStatusJson);
			core.sendRequestToServer(request);	
		}
	}

    private void cancelTrade(Remote remote){
        CancelTradeInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        String coffeeIndent = info.getCoffeeIndent();
        if(!TextUtils.isEmpty(coffeeIndent)){
            CancelTradeRequest request = new CancelTradeRequest(info.getUid(), coffeeIndent);
            core.sendRequestToServer(request);
        }
    }

	private void cancelTradeCart(Remote remote){
		CancelTradeCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		final String payIndent = info.getPayIndent();
		if(!TextUtils.isEmpty(payIndent)){
			CancelTradeCartRequest request = new CancelTradeCartRequest(info.getUid(), payIndent);
			core.sendRequestToServer(request);
			core.addRequestRetryTimer(new ResendRequestTask(request) {
				@Override
				public void onTimeout() {
					LogUtil.vendor("[CoffeeAction] cancelTradeCart->"+payIndent+"--timeout");
				}
			},3,4*60);
		}
	}

    private void updateStock(Remote remote){
        final SyncStockInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        LogUtil.vendor("update stock to server:  " + info.getInventory());
        UpdateStockRequest request = new UpdateStockRequest(info.getUid(), info.getInventory(), info.isAuto());
        core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
            @Override
            public void onTimeout() {
                LogUtil.vendor("[CoffeeAction] updateStock->timeout");

                UpdateStockResult result = new UpdateStockResult();
                result.setAuto(info.isAuto());
                result.setResCode(ResponseCode.RES_ETIMEOUT);
                core.notifyListener(result.toRemote());
            }
        }, 0, 20);
    }

	private void resetStock(Remote remote){
		final ResetStockInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		LogUtil.vendor("reset stock to server:  " + info.getInventory());
		ResetStockRequest request = new ResetStockRequest(info.getUid(), info.getUserID(), info.getInventory());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] resetStock->timeout");

				ResetStockResult result = new ResetStockResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 0, 30);
	}

	private void addStock(Remote remote){
		final AddStockInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		LogUtil.vendor("add stock to server:  " + info.getInventory());
		AddStockRequest request = new AddStockRequest(info.getUid(), info.getUserID(), info.getInventory());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] addStock->timeout");

				AddStockResult result = new AddStockResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 0, 30);
	}

    private void getCoffeeDosingList(Remote remote){
        final GetDosingListInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        GetDosingListRequest request = new GetDosingListRequest(info.getUid(), info.isAuto());
        core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
            @Override
            public void onTimeout() {
                LogUtil.vendor("[CoffeeAction] getCoffeeDosingList->timeout");

                GetDosingResult result = new GetDosingResult();
                result.setResCode(ResponseCode.RES_ETIMEOUT);
                result.setAuto(info.isAuto());
                core.notifyListener(result.toRemote());
            }
        }, 0, 30);
    }

	private void rollbackCartIndents(Remote remote){
		RollbackCoffeeIndentCart info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		LogUtil.e("TEST", info.getCoffeeIndents());
		RollbackCartRequest request = new RollbackCartRequest(info.getUid(), info.getPayIndent(), info.getCoffeeIndents()
			, info.getReason());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] rollbackCartIndents->timeout");
				// TODO make it more intelligent
			}
		}, 0, 30);
	}

	private void getMachineConfig(Remote remote){
		GetMachineConfigInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		GetMachineConfigRequest request = new GetMachineConfigRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getMachineConfig->timeout");
				GetMachineConfigResult result = new GetMachineConfigResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 1, 15);
	}

	private void getAdvPics(Remote remote){
		LogUtil.e("[DEBUG]",  "getAdvPics from remote");
		GetAdvPicsInfo info =  Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		GetAdvPicRequest request = new GetAdvPicRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getAdvPics->timeout");
				GetAdvPicsResult result = new GetAdvPicsResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 2, 10);
	}

	private void reportErrorFetch(Remote remote) {
		ReportErrorFetchInfo info = Ancestor.parseObject(remote.getBody());
		if (info == null) return;
		LogUtil.vendor("reportErrorFetch-> " + info.getCodes() + ";" + info.getGoodIds());
		ReportErrorFetchRequest request = new ReportErrorFetchRequest(info.getUid(), info.getCodes(), info.getGoodIds());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] reportErrorFetch->timeout");
			}
		}, 0, 20);
	}

	private void getNotice(Remote remote){
		GetNoticeInfo info = Ancestor.parseObject(remote.getBody());
		if (info == null) return;
		GetNoticeRequest request = new GetNoticeRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getNotice->timeout");
			}
		}, 1, 15);
	}
}
