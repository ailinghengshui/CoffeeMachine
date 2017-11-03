package com.jingye.coffeemac.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.common.adapter.TViewHolder;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.ui.AddSubView;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;

import java.util.ArrayList;

public class PayCartViewHolder extends TViewHolder implements AddSubView.OnNumChangeListener,RadioGroup.OnCheckedChangeListener {

	private ImageView mCoffeeImg;
	private TextView mCoffeeName;
	private TextView mCoffeeOriPrice;
	private TextView mCoffeePrice;
    private TextView mCoffeeSugarName;
	private RadioGroup mCoffeeSugar;
	private LinearLayout mAddSubViewLinear;
	private AddSubView mSerNumView;

	private CartPayItem mCartPayItem;

	@Override
	protected int getResId() {
		return R.layout.pay_cart_item;
	}

	@Override
	public void inflate() {
		mCoffeeImg = (ImageView) view.findViewById(R.id.pay_cart_list_item_coffee);
		mCoffeeName = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_name);
        mCoffeeSugarName = (TextView) view.findViewById(R.id.pay_cart_list_item_sugar_name);
		mCoffeeOriPrice = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_original_price);
		mCoffeePrice = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_price);
		mCoffeeSugar = (RadioGroup) view.findViewById(R.id.pay_cart_list_item_sugar_group);
		for(int i=0;i<4;i++){
			final RadioButton rb = new RadioButton(context);
			rb.setId(i);
			rb.setButtonDrawable(android.R.color.transparent);

            if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){

                if(i == 0)
                    rb.setPadding(38, 5, 0, 5);
                else
                    rb.setPadding(34, 5, 0, 5);
            }else{

                if(i == 0)
                    rb.setPadding(23, 5, 0, 5);
                else
                    rb.setPadding(27, 5, 0, 5);
            }

			RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
					ScreenUtil.dip2px(60), RadioGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(ScreenUtil.dip2px(5), 0, ScreenUtil.dip2px(5), 0);

			if(i==0){
				rb.setChecked(true);
				rb.setTextColor(Color.WHITE);
				rb.setBackgroundResource(R.drawable.radio_sugar_sel);
			}else{
				rb.setTextColor(context.getResources().getColor(R.color.norcolor));
				rb.setBackgroundResource(R.drawable.radio_sugar_nor);
			}
			if(i == 0){
				rb.setText(R.string.coffee_info_nosugar);
			}else if(i == 1){
				rb.setText(R.string.coffee_info_onesugar);
			}else if(i == 2){
				rb.setText(R.string.coffee_info_twosugar);
			}else if(i == 3){
				rb.setText(R.string.coffee_info_threesugar);
			}
			mCoffeeSugar.addView(rb,params);
		}

		mCoffeeSugar.setOnCheckedChangeListener(this);
		mAddSubViewLinear = (LinearLayout) view.findViewById(R.id.pay_cart_list_item_num);
		mSerNumView = new AddSubView(context);
		mSerNumView.setOnNumChangeListener(this);
		mAddSubViewLinear.addView(mSerNumView);
	}

	@Override
	public void refresh(TListItem item) {
		mCartPayItem = (CartPayItem)item;
		CoffeeInfo info = mCartPayItem.getCoffeeInfo();
		if (info != null) {
			String imgURL = info.getImgUrl() == null ? "" : info.getImgUrl();
//			ImageLoaderTool.disPlay(imgURL.trim(), mCoffeeImg, R.drawable.buy_coffee_loading);
			ImageLoaderTool.disPlay(context,imgURL.trim(), mCoffeeImg, R.drawable.buy_coffee_loading);
			String coffeeName = null;
			if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){
				coffeeName = info.getCoffeeTitleEn().trim();
			}else{
				coffeeName = info.getCoffeeTitle().trim();
			}
			mCoffeeName.setText(coffeeName);

			if(info.getPrice() != info.getDiscount()){
				mCoffeePrice.setVisibility(View.VISIBLE);
				mCoffeePrice.setText(context.getResources().getString(R.string.coffee_info_price)+
						String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getDiscount())));
				mCoffeeOriPrice.setVisibility(View.VISIBLE);
				mCoffeeOriPrice.setText(context.getResources().getString(R.string.coffee_info_oriprice)+
						String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getPrice())));
				mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				mCoffeeOriPrice.getPaint().setAntiAlias(true);
			}else{
				mCoffeePrice.setText(context.getResources().getString(R.string.coffee_info_price)+
						String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getDiscount())));
				mCoffeeOriPrice.setVisibility(View.GONE);
			}

            ArrayList<CoffeeDosingInfo> dosingList = info.getDosingList();
            mCoffeeSugar.setVisibility(View.GONE);
            mCoffeeSugarName.setText(R.string.coffee_info_nosugar_note);
            for(int i = 0; i < dosingList.size(); i++) {
                CoffeeDosingInfo dosinfo = dosingList.get(i);
                if (dosinfo.getMacConifg() == 1 && dosinfo.getValue() > 0) {
                    mCoffeeSugar.setVisibility(View.VISIBLE);
                    mCoffeeSugarName.setText(R.string.coffee_info_sugar_title);
                    break;
                }
            }
        }

		mCoffeeSugar.check(mCartPayItem.getSugarLevel()-1);

		if(mSerNumView != null){
			mSerNumView.setNum(mCartPayItem.getBuyNum());
		}

	}


	@Override
	public void onNumChange(View view, int num) {
	}

	@Override
	public void onNumChangeAdd(View view, int num) {

		if(mCartPayItem.getCoffeeInfo().isPackage()){
			if(!CoffeeUtil.isExcceedCartLimit(mCartPayItem.getCoffeeInfo().getPackageNum())){
				if (mCartPayItem != null) {
					mCartPayItem.setBuyNum(num);
				}

				if (eventListener != null) {
					eventListener.onItemChangeNotify();
				}
			}else{
				AddSubView asv = (AddSubView) view;
				asv.setNum(asv.getNum() - 1);
				ToastUtil.showToast(context, R.string.cart_exceeds_max_num);
			}

		}else {
			if (!CoffeeUtil.isExcceedCartLimit(1)) {
				if (mCartPayItem != null) {
					mCartPayItem.setBuyNum(num);
				}

				if (eventListener != null) {
					eventListener.onItemChangeNotify();
				}
			} else {
				AddSubView asv = (AddSubView) view;
				asv.setNum(asv.getNum() - 1);
				ToastUtil.showToast(context, R.string.cart_exceeds_max_num);
			}
		}
	}

	@Override
	public void onNumChangeSub(View view, int num) {

        if ((num == 0)&&(eventListener != null)&&(mCartPayItem != null)) {
            eventListener.onViewHolderClick(view,mCartPayItem);
            return;
        }

		if(mCartPayItem != null){
			mCartPayItem.setBuyNum(num);
		}

		if (eventListener != null) {
			eventListener.onItemChangeNotify();
		}
	}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        for (int i = 0; i < 4; i++) {
            if (i == checkedId) {
                if(mCartPayItem != null){
                    mCartPayItem.setSugarLevel(i+1);
                }
                ((RadioButton) group.getChildAt(i)).setTextColor(Color.WHITE);
                ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_sel);
            } else {
                ((RadioButton) group.getChildAt(i)).setTextColor(context.getResources().getColor(R.color.norcolor));
                ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_nor);
            }
        }

        if (eventListener != null) {
            eventListener.onItemChangeNotify();
        }
    }
}
