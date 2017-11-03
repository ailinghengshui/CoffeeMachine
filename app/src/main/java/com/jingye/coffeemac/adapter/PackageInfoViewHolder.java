package com.jingye.coffeemac.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
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
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.ui.AddSubView;
import com.jingye.coffeemac.ui.CartPayPackageElement;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PackageInfoViewHolder extends TViewHolder implements View.OnClickListener {

	private ImageView mCoffeeImg;
	private TextView mCoffeeName;
	private TextView mCoffeePrice;
	private TextView mCoffeeSugarName;

	private CartPayItem mCartPayItem;
	private TextView tvCartPackageElementSugarHint;
	private LinearLayout llCartPackageElementSugarContainer;
	private TextView tvCartPackageElementSugarNum0;
	private TextView tvCartPackageElementSugarNum1;
	private TextView tvCartPackageElementSugarNum2;
	private TextView tvCartPackageElementSugarNum3;
	private int currentSugarNum=CoffeeInfo.SugarNum0;

	@Override
	protected int getResId() {
		return R.layout.package_info_item;
	}

	@Override
	public void inflate() {
		mCoffeeImg = (ImageView) view.findViewById(R.id.pay_cart_list_item_coffee);
		mCoffeeName = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_name);
		mCoffeeSugarName = (TextView) view.findViewById(R.id.pay_cart_list_item_sugar_name);
		mCoffeePrice = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_original_price);
		tvCartPackageElementSugarHint= (TextView) view.findViewById(R.id.tvCartPackageElementSugarHint);

		llCartPackageElementSugarContainer=(LinearLayout)view.findViewById(R.id.llCartPackageElementSugarContainer);
		tvCartPackageElementSugarNum0=(TextView)view.findViewById(R.id.tvCartPackageElementSugarNum0);
		tvCartPackageElementSugarNum1=(TextView)view.findViewById(R.id.tvCartPackageElementSugarNum1);
		tvCartPackageElementSugarNum2=(TextView)view.findViewById(R.id.tvCartPackageElementSugarNum2);
		tvCartPackageElementSugarNum3=(TextView)view.findViewById(R.id.tvCartPackageElementSugarNum3);

		tvCartPackageElementSugarNum0.setOnClickListener(this);
		tvCartPackageElementSugarNum1.setOnClickListener(this);
		tvCartPackageElementSugarNum2.setOnClickListener(this);
		tvCartPackageElementSugarNum3.setOnClickListener(this);

		initSugarNum(currentSugarNum);

	}

	private void initSugarNum(int currentNum) {
		tvCartPackageElementSugarNum0.setSelected(false);
		tvCartPackageElementSugarNum0.setTextColor(context.getResources().getColor(R.color.black));
		tvCartPackageElementSugarNum1.setSelected(false);
		tvCartPackageElementSugarNum1.setTextColor(context.getResources().getColor(R.color.black));
		tvCartPackageElementSugarNum2.setSelected(false);
		tvCartPackageElementSugarNum2.setTextColor(context.getResources().getColor(R.color.black));
		tvCartPackageElementSugarNum3.setSelected(false);
		tvCartPackageElementSugarNum3.setTextColor(context.getResources().getColor(R.color.black));
		switch (currentNum) {
			case CoffeeInfo.SugarNum1:
				tvCartPackageElementSugarNum1.setSelected(true);
				tvCartPackageElementSugarNum1.setTextColor(context.getResources().getColor(R.color.white));
				break;
			case CoffeeInfo.SugarNum2:
				tvCartPackageElementSugarNum2.setSelected(true);
				tvCartPackageElementSugarNum2.setTextColor(context.getResources().getColor(R.color.white));
				break;
			case CoffeeInfo.SugarNum3:
				tvCartPackageElementSugarNum3.setSelected(true);
				tvCartPackageElementSugarNum3.setTextColor(context.getResources().getColor(R.color.white));
				break;
			default:
				tvCartPackageElementSugarNum0.setSelected(true);
				tvCartPackageElementSugarNum0.setTextColor(context.getResources().getColor(R.color.white));
				break;
		}
	}

	@Override
	public void refresh(TListItem item) {
		mCartPayItem = (CartPayItem) item;
		CoffeeInfo info = mCartPayItem.getCoffeeInfo();
		if (info != null) {
			String imgURL = info.getImgUrl() == null ? "" : info.getImgUrl();
			ImageLoaderTool.disPlay(context, imgURL.trim(), mCoffeeImg, R.drawable.buy_coffee_loading);
			String coffeeName = null;

			if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN&&!TextUtils.isEmpty(info.getCoffeeTitleEn().trim())) {
				coffeeName = info.getCoffeeTitleEn().trim();
			} else {
				coffeeName = info.getCoffeeTitle().trim();
			}
			mCoffeeName.setText(coffeeName);

			mCoffeePrice.setText(context.getResources().getString(R.string.coffee_info_oriprice)+
					String.format(context.getString(R.string.pay_coffee_price_format),
							String.valueOf(info.getPrice())));

			if(needSugar(info)){
				tvCartPackageElementSugarHint.setVisibility(View.INVISIBLE);
				llCartPackageElementSugarContainer.setVisibility(View.VISIBLE);

				initSugarNum(mCartPayItem.getSugarLevel());
			}else{
				tvCartPackageElementSugarHint.setVisibility(View.VISIBLE);
				llCartPackageElementSugarContainer.setVisibility(View.INVISIBLE);
			}


		}

	}

	private boolean needSugar(CoffeeInfo info) {
		List<PackageCoffeeDosingInfo> dosingList = info.getPackageDoing();
		for (int i = 0; i < dosingList.size(); i++) {
			PackageCoffeeDosingInfo dosinfo = dosingList.get(i);
			if (dosinfo.isMachine_configured() == 1 && dosinfo.getValue() > 0) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tvCartPackageElementSugarNum0:
				currentSugarNum=CoffeeInfo.SugarNum0;
				break;
			case R.id.tvCartPackageElementSugarNum1:
				currentSugarNum=CoffeeInfo.SugarNum1;
				break;
			case R.id.tvCartPackageElementSugarNum2:
				currentSugarNum=CoffeeInfo.SugarNum2;
				break;
			case R.id.tvCartPackageElementSugarNum3:
				currentSugarNum=CoffeeInfo.SugarNum3;
				break;
		}

		initSugarNum(currentSugarNum);
		if(eventListener!=null){
			mCartPayItem.setSugarLevel(currentSugarNum);
			eventListener.onViewHolderClick(view,mCartPayItem);
		}
	}
}