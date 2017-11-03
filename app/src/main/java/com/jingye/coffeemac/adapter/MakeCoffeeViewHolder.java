package com.jingye.coffeemac.adapter;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.MakeCoffeeExActivity;
import com.jingye.coffeemac.beans.MakeCoffeeItem;
import com.jingye.coffeemac.beans.OrderContentItem;
import com.jingye.coffeemac.common.adapter.TListItem;
import com.jingye.coffeemac.common.adapter.TViewHolder;
import com.jingye.coffeemac.module.makecoffeemodule.NewMakeCoffeeExActivity;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.SharePrefConfig;

public class MakeCoffeeViewHolder extends TViewHolder {

	private RelativeLayout mCoffeeView;
	private ImageView mCoffeeCupIcon;
	private TextView mCoffeeNum;
	private TextView mCoffeeName;
	private TextView mCoffeeSugar;
	private TextView mCoffeeStatus;
	private ImageView mCoffeeLoad;
	private ImageView mCoffeeDone;

	private MakeCoffeeItem mCoffeeItem;

	@Override
	protected int getResId() {
		return R.layout.make_coffee_item;
	}

	@Override
	public void inflate() {
		mCoffeeView = (RelativeLayout) view.findViewById(R.id.make_coffee_list_item);
		mCoffeeCupIcon = (ImageView) view.findViewById(R.id.make_coffee_list_item_cup);
		mCoffeeNum = (TextView) view.findViewById(R.id.make_coffee_list_item_coffee_num);
		mCoffeeName = (TextView) view.findViewById(R.id.make_coffee_list_item_coffee_name);
		mCoffeeSugar = (TextView) view.findViewById(R.id.make_coffee_list_item_coffee_sugar);
		mCoffeeLoad = (ImageView) view.findViewById(R.id.make_coffee_list_item_loading);
		mCoffeeStatus = (TextView) view.findViewById(R.id.make_coffee_list_item_status);
		mCoffeeDone = (ImageView) view.findViewById(R.id.make_coffee_done);
	}

	@Override
	public void refresh(TListItem item) {
		mCoffeeItem = (MakeCoffeeItem)item;
		int status = mCoffeeItem.getStatus();
		OrderContentItem orderContentItem = mCoffeeItem.getOrderItem();
		if(orderContentItem != null){

			int level = orderContentItem.getSweetLevel();
			if(level > 0){
				mCoffeeSugar.setVisibility(View.VISIBLE);
			}else{
				mCoffeeSugar.setVisibility(View.GONE);
			}
			String sugar = CoffeeUtil.getSugarLevelDescri(context, level);
			if(status == MakeCoffeeItem.STATUS_WAITING){
				//mCoffeeView.setBackgroundColor(Color.parseColor("#00000000"));
				mCoffeeCupIcon.setImageResource(R.drawable.make_coffee_cup);
				if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){
					mCoffeeNum.setText(MakeCoffeeExActivity.getNumEn(mCoffeeItem.getNum()));
					mCoffeeName.setText(orderContentItem.getItemNameen());
				}else{
					mCoffeeNum.setText("第"+mCoffeeItem.getNum()+"杯");
					mCoffeeName.setText(orderContentItem.getItemName());
				}
				mCoffeeNum.setTextColor(Color.parseColor("#262626"));
				mCoffeeNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeName.setTextColor(Color.parseColor("#262626"));
				mCoffeeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeSugar.setText(sugar);
				mCoffeeSugar.setTextColor(Color.parseColor("#262626"));
				mCoffeeSugar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeStatus.setVisibility(View.GONE);
				mCoffeeDone.setVisibility(View.GONE);
				mCoffeeLoad.setVisibility(View.GONE);
			}else if(status == MakeCoffeeItem.STATUS_SUCCESS){
				//mCoffeeView.setBackgroundColor(Color.parseColor("#00000000"));
				mCoffeeCupIcon.setImageResource(R.drawable.make_coffee_cup);
				if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){
					mCoffeeNum.setText(MakeCoffeeExActivity.getNumEn(mCoffeeItem.getNum()));
					mCoffeeName.setText(orderContentItem.getItemNameen());
				}else{
					mCoffeeNum.setText("第"+mCoffeeItem.getNum()+"杯");
					mCoffeeName.setText(orderContentItem.getItemName());
				}
				mCoffeeNum.setTextColor(Color.parseColor("#00a762"));
				mCoffeeNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeName.setTextColor(Color.parseColor("#00a762"));
				mCoffeeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeSugar.setText(sugar);
				mCoffeeSugar.setTextColor(Color.parseColor("#00a762"));
				mCoffeeSugar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeStatus.setVisibility(View.GONE);
				mCoffeeDone.setVisibility(View.VISIBLE);
				mCoffeeLoad.setVisibility(View.GONE);
			}else if(status == MakeCoffeeItem.STATUS_FAIL){
				//mCoffeeView.setBackgroundResource(R.drawable.make_coffee_doing_bg);
				mCoffeeCupIcon.setImageResource(R.drawable.make_coffee_cup_big);
				if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){
					mCoffeeNum.setText(MakeCoffeeExActivity.getNumEn(mCoffeeItem.getNum()));
					mCoffeeName.setText(orderContentItem.getItemNameen());
				}else{
					mCoffeeNum.setText("第"+mCoffeeItem.getNum()+"杯");
					mCoffeeName.setText(orderContentItem.getItemName());
				}
				mCoffeeNum.setTextColor(Color.parseColor("#ff3333"));
				mCoffeeNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeName.setTextColor(Color.parseColor("#ff3333"));
				mCoffeeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeSugar.setText(sugar);
				mCoffeeSugar.setTextColor(Color.parseColor("#ff3333"));
				mCoffeeSugar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeStatus.setVisibility(View.VISIBLE);
				mCoffeeStatus.setText(R.string.make_coffee_cart_makefail);
				mCoffeeStatus.setTextColor(Color.parseColor("#ff3333"));
				mCoffeeStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
				mCoffeeDone.setVisibility(View.GONE);
				mCoffeeLoad.setVisibility(View.GONE);
			}else if(status == MakeCoffeeItem.STATUS_MAKING){
				//mCoffeeView.setBackgroundResource(R.drawable.make_coffee_doing_bg);
				mCoffeeCupIcon.setImageResource(R.drawable.make_coffee_cup_big);
				if(SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN){
					mCoffeeNum.setText(MakeCoffeeExActivity.getNumEn(mCoffeeItem.getNum()));
					mCoffeeName.setText(orderContentItem.getItemNameen());
				}else{
					mCoffeeNum.setText("第"+mCoffeeItem.getNum()+"杯");
					mCoffeeName.setText(orderContentItem.getItemName());
				}
				mCoffeeNum.setTextColor(Color.parseColor("#262626"));
				mCoffeeNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeName.setTextColor(Color.parseColor("#262626"));
				mCoffeeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				mCoffeeSugar.setText(sugar);
				mCoffeeSugar.setTextColor(Color.parseColor("#262626"));
				mCoffeeSugar.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
				mCoffeeStatus.setVisibility(View.VISIBLE);
				mCoffeeStatus.setText(R.string.make_coffee_cart_makeing);
				mCoffeeStatus.setTextColor(Color.parseColor("#262626"));
				mCoffeeStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
				mCoffeeLoad.setVisibility(View.VISIBLE);
				AnimationDrawable animationDrawable = (AnimationDrawable) mCoffeeLoad.getBackground();
				animationDrawable.start();
				mCoffeeDone.setVisibility(View.GONE);
			}
		}
	}


}
