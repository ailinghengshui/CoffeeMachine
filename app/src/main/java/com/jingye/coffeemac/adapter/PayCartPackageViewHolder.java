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
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.common.adapter.TViewHolder;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.ui.AddSubView;
import com.jingye.coffeemac.ui.CartPayPackageElement;
import com.jingye.coffeemac.ui.CartPayPackageGroup;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;

import java.util.ArrayList;
import java.util.Map;

public class PayCartPackageViewHolder extends TViewHolder implements AddSubView.OnNumChangeListener, CartPayPackageElement.ISugarLevelChanged {

	private ImageView mCoffeeImg;
	private TextView mCoffeeName;
	private TextView mCoffeeOriPrice;
	private TextView mCoffeePrice;

	private CartPayItem mCartPayItem;
	private CartPayPackageGroup grpPayPackage;

	@Override
	protected int getResId() {
		return R.layout.pay_cart_package_item;
	}

	@Override
	public void inflate() {

		mCoffeeImg=(ImageView)view.findViewById(R.id.ivCartPackageItemIcon);
		mCoffeeName = (TextView) view.findViewById(R.id.tvCartPackagePackageItemName);
		mCoffeeOriPrice = (TextView) view.findViewById(R.id.tvCartPackagePackageItemOriginalPrice);
		mCoffeePrice = (TextView) view.findViewById(R.id.tvCartPackagePackageItemPrice);
		grpPayPackage= (CartPayPackageGroup) view.findViewById(R.id.grpPayPackage);

//
	}

	@Override
	public void refresh(TListItem item) {
		mCartPayItem = (CartPayItem)item;
		CoffeeInfo info = mCartPayItem.getCoffeeInfo();
		if (info != null) {
			String imgURL = info.getImgUrl() == null ? "" : info.getImgUrl();
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

			grpPayPackage.setNumListener(this);
			grpPayPackage.setSugarLevelChangeListener(this);


			grpPayPackage.addData(mCartPayItem);


//            ArrayList<CoffeeDosingInfo> dosingList = info.getDosingList();
//            mCoffeeSugar.setVisibility(View.GONE);
//            mCoffeeSugarName.setText(R.string.coffee_info_nosugar_note);
//            for(int i = 0; i < dosingList.size(); i++) {
//                CoffeeDosingInfo dosinfo = dosingList.get(i);
//                if (dosinfo.getMacConifg() == 1 && dosinfo.getValue() > 0) {
//                    mCoffeeSugar.setVisibility(View.VISIBLE);
//                    mCoffeeSugarName.setText(R.string.coffee_info_sugar_title);
//                    break;
//                }
//            }
        }

//		mCoffeeSugar.check(mCartPayItem.getSugarLevel()-1);
//
//		if(mSerNumView != null){
//			mSerNumView.setNum(mCartPayItem.getBuyNum());
//		}

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
	public void onSugarLevelChanged( int position,int sugarlevel) {
		if(mCartPayItem!=null){

			mCartPayItem.setPackageSugarLevel(position,sugarlevel);
//			mCartPayItem.getCoffeeInfo().getCoffeesPackage().get(position).setSugerLevel(coffeeInfo.getSugerLevel());
		}

	}

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//        for (int i = 0; i < 4; i++) {
//            if (i == checkedId) {
//                if(mCartPayItem != null){
//                    mCartPayItem.setSugarLevel(i+1);
//                }
//                ((RadioButton) group.getChildAt(i)).setTextColor(Color.WHITE);
//                ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_sel);
//            } else {
//                ((RadioButton) group.getChildAt(i)).setTextColor(context.getResources().getColor(R.color.norcolor));
//                ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sugar_nor);
//            }
//        }
//
//        if (eventListener != null) {
//            eventListener.onItemChangeNotify();
//        }
//    }
}
