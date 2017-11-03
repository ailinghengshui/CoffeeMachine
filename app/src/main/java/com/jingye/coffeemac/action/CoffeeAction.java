package com.jingye.coffeemac.action;

import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TAction;
import com.jingye.coffeemac.domain.OrderFetchStatus;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.RollbackFetchByCodeInfo;
import com.jingye.coffeemac.service.bean.result.ExchangeCoffeeByCodeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetAdvPicsResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.bean.result.RollbackFetchCodeResult;
import com.jingye.coffeemac.service.bean.result.FetchCoffeeByCodeResult;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

public class CoffeeAction extends TAction {

	private static final String TAG = "CoffeeAction->";

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		if(remote.getAction() == ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_CODE){
			FetchCoffeeByCodeResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null){
				String fetchCode = result.getFetchCode();
				MyApplication.Instance().removeIndentStatus(fetchCode);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_EXCHANGE_COFFEE){
			ExchangeCoffeeByCodeResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null){
				String fetchCode = result.getFetchCode();
				MyApplication.Instance().removeIndentStatus(fetchCode);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_FETCH_COFFEE_BY_CODE_TIME_OUT){
//			ProgressDlgHelper.showProgress(MyApplication.Instance(), "取货超时,正在撤销");
			LogUtil.vendor(TAG+"fetch time out");
			String fetchCode = remote.getBody();
			OrderFetchStatus orderStatus = MyApplication.Instance().getIndentStatus(fetchCode);
			if(orderStatus != null && orderStatus.getStatus() == OrderFetchStatus.ORDER_STATUS_REQUESTING){
				orderStatus.setStatus(OrderFetchStatus.ORDER_STATUS_TIMEOUT);
			}
			// send the roll back request
			long timestamp = TimeUtil.getNow_millisecond();
			RollbackFetchByCodeInfo info = new RollbackFetchByCodeInfo();
			info.setUid(U.getMyVendorNum());
			info.setFetchCode(fetchCode);
			info.setTimestamp(timestamp);
			send(info.toRemote());
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_FETCH_CODE_ROLLBACK){
			RollbackFetchCodeResult result = GeneralActionResult.parseObject(remote.getBody());
			ProgressDlgHelper.closeProgress();
			if(result != null && result.getResCode() == 200){
				LogUtil.vendor(TAG+"roll back success");
//				ToastUtil.showToast(MyApplication.Instance(),"成功撤销");
				String fetchCode = result.getFetchCode();
				MyApplication.Instance().removeIndentStatus(fetchCode);
			}else{
				LogUtil.vendor(TAG+"roll back fail");
//				ToastUtil.showToast(MyApplication.Instance(),"撤销失败");
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_GET_DISCOUNT){
			GetDiscountResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null && result.getResCode() == 200){
				MyApplication.Instance().setDiscountInfo(result);
			}
		}
//		else if(remote.getAction() == ITranCode.ACT_COFFEE_GET_ADV_PICS){
//			GetAdvPicsResult result = GeneralActionResult.parseObject(remote.getBody());
//			if(result != null && result.getResCode() == 200){
//				SharePrefConfig.getInstance().setAdvImgs(result.getAdvImgUrls());
//			}
//		}
		
		notifyAll(remote);
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_COFFEE;
	}
}
