package com.jingye.coffeemac.service.handler.coffee;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.jingye.coffeemac.service.bean.result.GetCoffeeResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.enums.ICoffeeService;
import com.jingye.coffeemac.service.protocol.marshal.Marshallable;
import com.jingye.coffeemac.service.protocol.marshal.Property;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.service.protocol.response.coffee.GetCoffeeResponse;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class GetCoffeeResponseHandler extends ResponseHandler {

	private static final String TAG = GetCoffeeResponseHandler.class.getSimpleName();

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);
		
		GetCoffeeResult result = new GetCoffeeResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			GetCoffeeResponse coffeeResponse = (GetCoffeeResponse) response;
			// 咖啡信息
			List<Marshallable> coffees = coffeeResponse.getCoffeeInfos().list;
			LogUtil.vendor("收到咖啡数为:" + coffees.size());
			for(int i = 0; i < coffees.size(); i++) {
				Property coffee = (Property) coffees.get(i);
				int id = coffee.getInteger(ICoffeeService.CoffeeType.COFFEE_TYPE_ID);
				String title = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_TITLE);
				double price = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_PRICE));
                double discount = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DISCOUNT));
				String imgURL = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IMGURL);
				int soldNum = coffee.getInteger(ICoffeeService.CoffeeType.COFFEE_TYPE_SOLD_NUM);
				String dosing = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DOSING);
                double volume = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_VOLUME));
                boolean isNew = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_NEW));
                boolean isHot = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_HOT));
				boolean isAddIce = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_ADD_ICE));
				boolean isSold = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_SOLD));
				boolean isSweet = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_SWEET));
				String titleen = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_TITLE_EN);
				String coffeeDesc=coffee.get(ICoffeeService.CoffeeType.COFFEE_DESC);
				String coffeeDescEn=coffee.get(ICoffeeService.CoffeeType.COFFEE_DESC_EN);

                LogUtil.vendor("[GetCoffeeResponse]" + "coffeeID:" + id + ", coffeeTitle: " + title  + ", coffeeTitleen: " + titleen);
                LogUtil.vendor("[GetCoffeeResponse]" + "imgURL: " + imgURL);
                LogUtil.vendor("[GetCoffeeResponse]" + dosing);
				LogUtil.vendor("[GetCoffeeResponse]" + "isAddIce: " + isAddIce);
				LogUtil.vendor("[GetCoffeeResponse]" + "isSweet: " + isSweet);
				LogUtil.vendor("[GetCoffeeResponse]" + "coffeeDesc: " + coffeeDesc);
				LogUtil.vendor("[GetCoffeeResponse]" + "coffeeDescEn: " + coffeeDescEn);

				
				CoffeeInfo info = new CoffeeInfo();
				info.setCoffeeId(id);
				info.setCoffeeTitle(title);
				info.setPrice(price);
				info.setImgUrl(imgURL);
				info.setSoldNum(soldNum);
                info.setDiscount(discount);
                info.setVolume(volume);
                info.setHot(isHot);
                info.setNew(isNew);
				info.setAddIce(isAddIce);
				info.setIsSold(isSold);
				info.setSweet(isSweet);
				info.setCoffeeTitleEn(titleen);

				if(!TextUtils.isEmpty(coffeeDesc)) {
					info.setDesc(coffeeDesc);
				}else{
					info.setDesc("• "+"美味健康  营养丰富\n       ");
				}
				if(!TextUtils.isEmpty(coffeeDescEn)) {
					info.setDescEn(coffeeDescEn);
				}else{
					info.setDescEn("• "+"Delicious, healthy and nutritious !\n       ");
				}

				if(coffee.contains(ICoffeeService.CoffeeType.COFFEE_PACKAGE)){
					info.setPackage(true);
					LogUtil.vendor("[GetCoffeeResponse]" + "isPackage: " + coffee.get(ICoffeeService.CoffeeType.COFFEE_PACKAGE));
					Log.d("[GetCoffeeResponse]",coffee.get(ICoffeeService.CoffeeType.COFFEE_PACKAGE));
					if(!TextUtils.isEmpty(coffee.get(ICoffeeService.CoffeeType.COFFEE_PACKAGE))){
						List<CoffeeInfo> coffeeInfos=JSON.parseArray(coffee.get(ICoffeeService.CoffeeType.COFFEE_PACKAGE),CoffeeInfo.class);
						for(CoffeeInfo coffeeInfo:coffeeInfos){
							info.addCoffeeToPackage(coffeeInfo);

//							{"coffeeId":1400,"coffeeTitle":"美式","coffeeTitleEn":"Café Americano","imgUrl":"http://coffees.qiniu.jijiakafei.com/jijia_testline__1479373074315.png","price":10,"soldNum":0,"discount":8.4,
//                           "volume":260,"isNew":"true","isHot":"false","isAddIce":"false","isSold":"true","isSweet":"false","doing":[{"id":5,"dosing_name":"咖啡豆","value":8,"is_addable":true,"order":1,"water":230,"ejection":0,"stirvol":0,"stirtime":0,"machine_configured":false,"transform_factor":1}
//							StringBuilder stringBuilder=new StringBuilder();
//							stringBuilder.append("coffeeId:")
//									.append(coffeeInfo.getCoffeeId())
//									.append("coffeeTitle:")
//									.append(coffeeInfo.getCoffeeTitle())
//									.append("coffeeTitleEn:")
//									.append(coffeeInfo.getCoffeeTitleEn())
//									.append("imgUrl:")
//									.append(coffeeInfo.getImgUrl())
//									.append("price:")
//									.append(coffeeInfo.getPrice())
//									.append("soldNum:")
//									.append(coffeeInfo.getSoldNum())
//									.append("discount:")
//									.append(coffeeInfo.getDiscount())
//									.append("volume:")
//									.append(coffeeInfo.getVolume())
//									.append("isNew:")
//									.append(coffeeInfo.isNew())
//									.append("isHot:")
//									.append(coffeeInfo.isHot())
//									.append("isAddIce")
//									.append(coffeeInfo.isAddIce())
//									.append("isSold")
//									.append(coffeeInfo.isSold())
//									.append("isSweet")
//									.append(coffeeInfo.isSweet());
//
//							Log.d(TAG,stringBuilder.toString());
						}
					}

				}else{
					info.setPackage(false);
					try{
						JSONArray array = JSON.parseArray(dosing);
						if (array != null && array.size() > 0) {
							int size = array.size();
							for(int j = 0; j < size; ++j) {
								CoffeeDosingInfo cdi = new CoffeeDosingInfo();
								cdi.fromJSONString(array.getJSONObject(j));
								info.addDosingInfo(cdi);
							}
						}
					}catch(JSONException e){
						e.printStackTrace();
					}
				}

				result.addCoffee(info);
			}

			// 促销信息
			String favorable = coffeeResponse.getFavorable();
			LogUtil.vendor("[GetCoffeeResponse]" + "favorable:" + favorable);
			GetDiscountResult discountInfo = new GetDiscountResult();
			discountInfo.setFavorable(favorable);
			result.setDiscountInfo(discountInfo);
		} 

		postToUI(result.toRemote());
	}

}
