package com.jingye.coffeemac.application;

import java.util.List;

import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetCoffeeResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.util.TimeUtil;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DataCacheHandler extends Handler {
	@Override
	public void handleMessage(Message message) {		
		Remote remote = (Remote) message.obj;
		if (remote.getWhat() == ITranCode.ACT_COFFEE) {
			if (remote.getAction() == ITranCode.ACT_COFFEE_GET_COFFEE) {
				GetCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
					// coffee information
					List<CoffeeInfo> coffees = result.getCoffees();
					if(coffees != null){
						MyApplication.Instance().getCoffeeInfos().clear();
						MyApplication.Instance().getCoffeeInfos().addAll(coffees);
					}
					// discount
					GetDiscountResult discountInfo = result.getDiscountInfo();
					MyApplication.Instance().setDiscountInfo(discountInfo);
					// update timestamp
					MyApplication.Instance().setLastCoffeeInfoUpdateTime(TimeUtil.getNow_millisecond());
				}
			}
		} 
	}
}
