package com.jingye.coffeemac.service.bean.result;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

public class GetDiscountResult extends BeanAncestor {

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private int resCode;

    public String getFavorable() {
        return favorable;
    }

    public void setFavorable(String favorable) {
        this.favorable = favorable;
    }

    private String favorable;

    public String getDiscount(){
        if(TextUtils.isEmpty(favorable)){
            return null;
        }

        try{
            JSONObject json = JSONObject.parseObject(favorable);
            if(json == null) return null;
            if(json.containsKey("discount")){
                String discount = json.getString("discount");
                return discount;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public String getReductMeet(){
        if(TextUtils.isEmpty(favorable)){
            return null;
        }

        try{
            JSONObject json = JSONObject.parseObject(favorable);
            if(json == null) return null;
            if(json.containsKey("reduction")){
                String reduct = json.getString("reduction");
                if(!TextUtils.isEmpty(reduct) && reduct.contains("_")){
                    String result[] = reduct.split("_");
                    return result[0];
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public String getReductSub(){
        if(TextUtils.isEmpty(favorable)){
            return null;
        }

        try{
            JSONObject json = JSONObject.parseObject(favorable);
            if(json == null) return null;
            if(json.containsKey("reduction")){
                String reduct = json.getString("reduction");
                if(!TextUtils.isEmpty(reduct) && reduct.contains("_")){
                    String result[] = reduct.split("_");
                    return result[1];
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_GET_DISCOUNT;
	}
}
