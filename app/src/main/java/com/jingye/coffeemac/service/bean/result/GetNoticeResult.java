package com.jingye.coffeemac.service.bean.result;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.bean.BeanAncestor;

import java.util.ArrayList;
import java.util.List;

public class GetNoticeResult extends BeanAncestor {

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private int resCode;

    public String getPromotions() {
        return promotions;
    }

    public void setPromotions(String promotions) {
        this.promotions = promotions;
    }

    private String promotions;

    public List<String> getNoticeList(){
        if(TextUtils.isEmpty(promotions)){
            return null;
        }

        List<String> noticeList = new ArrayList<String>();
        try{
            JSONArray array = JSONArray.parseArray(promotions);
            if(array != null){
                int size = array.size();
                for(int i = 0; i < size; i++){
                    String notice = array.getString(i);
                    noticeList.add(notice);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        return noticeList;
    }

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_NOTICE;
	}
}
