package com.jingye.coffeemac.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.fragment.BuyCoffeeHotFragment;
import com.jingye.coffeemac.loader.ImageLoaderTool;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.util.CoffeeUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;

import java.util.List;

public class HomeGridAdapter extends BaseAdapter {

    private List<CoffeeInfo> mCoffeeList;

    private Context mContext;

    private BuyCoffeeHotFragment.OnShowInfoHotListener listener;

    public HomeGridAdapter(Context context, List<CoffeeInfo> info, BuyCoffeeHotFragment.OnShowInfoHotListener listener) {
        this.mContext = context;
        this.mCoffeeList = info;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mCoffeeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCoffeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final viewHolder holder;
        final CoffeeInfo info = mCoffeeList.get(position);
        if (convertView == null) {
            holder = new viewHolder();
            if (info.isPackage()) {
                convertView = inflater.inflate(R.layout.home_coffee_grid_big_item, parent);

            } else {
                convertView = inflater.inflate(R.layout.home_coffee_grid_item, parent);
            }
            holder.mCoffeeImg = (ImageView) convertView.findViewById(R.id.coffee_info_img);
            holder.mCoffeeName = (TextView) convertView.findViewById(R.id.coffee_info_name);
            if (info.isPackage()) {
                holder.mCoffeeSecondName = (TextView) convertView.findViewById(R.id.tvCoffeePackageSencondTitle);
            }
            holder.mCoffeePrice = (TextView) convertView.findViewById(R.id.coffee_info_price);
            holder.mCoffeeOriPrice = (TextView) convertView.findViewById(R.id.coffee_info_ori_price);
            holder.mCoffeeSoldOut = (ImageView) convertView.findViewById(R.id.coffee_info_sold_out);
            holder.mCoffeeHot = (ImageView) convertView.findViewById(R.id.coffee_info_hot);
            holder.mCoffeeSweet = (ImageView) convertView.findViewById(R.id.coffee_info_sweet);
            holder.mCoffeeNew = (ImageView) convertView.findViewById(R.id.coffee_info_new);
            holder.mCoffeeDrinkType = (ImageView) convertView.findViewById(R.id.coffee_info_drink_type);
            holder.mCoffeeCart = (RelativeLayout) convertView.findViewById(R.id.coffee_info_cartbtn);

            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }


        String imgURL = info.getImgUrl();
//		ImageLoaderTool.disPlay(imgURL.trim(), holder.mCoffeeImg, R.drawable.coffee_info_img_default);
        if (info.isPackage()) {
            ImageLoaderTool.disPlay(mContext, imgURL.trim(), holder.mCoffeeImg, R.drawable.package_coffee_info_img_default);
        } else {
            ImageLoaderTool.disPlay(mContext, imgURL.trim(), holder.mCoffeeImg, R.drawable.coffee_info_img_default);
        }
        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
            holder.mCoffeeName.setText(info.getCoffeeTitleEn());
            holder.mCoffeeName.setTextSize(12);

            if (holder.mCoffeeSecondName != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < info.getCoffeesPackage().size(); i++) {
                    stringBuilder.append(info.getCoffeesPackage().get(i).getCoffeeTitleEn());
                    if (i != info.getCoffeesPackage().size() - 1) {
                        stringBuilder.append("+");
                    }

                }
                holder.mCoffeeSecondName.setText(stringBuilder);
            }
        } else {
            holder.mCoffeeName.setText(info.getCoffeeTitle());
            holder.mCoffeeName.setTextSize(18);

            if (holder.mCoffeeSecondName != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < info.getCoffeesPackage().size(); i++) {
                    stringBuilder.append(info.getCoffeesPackage().get(i).getCoffeeTitle());
                    if (i != info.getCoffeesPackage().size() - 1) {
                        stringBuilder.append("+");
                    }

                }
                holder.mCoffeeSecondName.setText(stringBuilder);
            }
        }

        if (info.isLackMaterials()) {
            holder.mCoffeeSoldOut.setVisibility(View.VISIBLE);
            holder.mCoffeeHot.setVisibility(View.INVISIBLE);
            holder.mCoffeeNew.setVisibility(View.INVISIBLE);
            holder.mCoffeeSweet.setVisibility(View.INVISIBLE);
            holder.mCoffeeDrinkType.setVisibility(View.INVISIBLE);
        } else {
            holder.mCoffeeSoldOut.setVisibility(View.GONE);
            if (info.isHot()) {
                holder.mCoffeeHot.setVisibility(View.VISIBLE);
            } else {
                holder.mCoffeeHot.setVisibility(View.GONE);
            }
            if (info.isNew()) {
                holder.mCoffeeNew.setVisibility(View.VISIBLE);
            } else {
                holder.mCoffeeNew.setVisibility(View.GONE);
            }

            if(!info.isPackage()) {
                holder.mCoffeeDrinkType.setVisibility(View.VISIBLE);

                if (info.isAddIce()) {
                    holder.mCoffeeDrinkType.setImageResource(R.drawable.coffee_info_cold_drink);
                } else {
                    holder.mCoffeeDrinkType.setImageResource(R.drawable.coffee_info_hot_drink);
                }
            }

            if (info.isSweet()) {
                holder.mCoffeeSweet.setVisibility(View.VISIBLE);
            } else {
                holder.mCoffeeSweet.setVisibility(View.GONE);
            }


            holder.mCoffeeCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.isPackage()) {
                        if (!CoffeeUtil.isExcceedCartLimit(info.getPackageNum())) {
                            CartPayItem item = new CartPayItem();
                            item.setCoffeeInfo(info);
                            item.setBuyNum(1);
                            for (int i = 0; i < info.getPackageNum(); i++) {
                                if (needSugar(info.getCoffeesPackage().get(i))) {
                                    item.setPackageSugarLevel(i, 1);
                                } else {
                                    item.setPackageSugarLevel(i, 0);
                                }
                            }
                            MyApplication.Instance().addCoffeeToCartPay(item);
                            listener.OnUpdateCartGoods(holder.mCoffeeCart);
                        } else {
                            ToastUtil.showToast(mContext, R.string.cart_exceeds_max_num);
                        }


                    } else {

                        if (!CoffeeUtil.isExcceedCartLimit(1)) {
                            CartPayItem item = new CartPayItem();
                            item.setCoffeeInfo(info);
                            item.setBuyNum(1);
                            item.setSugarLevel(1);
                            MyApplication.Instance().addCoffeeToCartPay(item);
                            listener.OnUpdateCartGoods(holder.mCoffeeCart);
                        } else {
                            ToastUtil.showToast(mContext, R.string.cart_exceeds_max_num);
                        }
                    }
                }
            });
        }

        if (info.getPrice() == info.getDiscount()) {
            holder.mCoffeeOriPrice.setVisibility(View.GONE);
            holder.mCoffeePrice.setText("¥" + info.getPrice());
        } else {
            holder.mCoffeeOriPrice.setVisibility(View.VISIBLE);
            holder.mCoffeeOriPrice.setText("¥" + info.getPrice());
            holder.mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCoffeeOriPrice.getPaint().setAntiAlias(true);
            holder.mCoffeePrice.setText("¥" + info.getDiscount());
        }

        return convertView;
    }

    private boolean needSugar(CoffeeInfo info) {
        List<PackageCoffeeDosingInfo> dosingList = info.getPackageDoing();
        if(dosingList!=null) {
            for (int i = 0; i < dosingList.size(); i++) {
                PackageCoffeeDosingInfo dosinfo = dosingList.get(i);
                if (dosinfo.isMachine_configured() == 1 && dosinfo.getValue() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public class viewHolder {
        private ImageView mCoffeeImg;
        private TextView mCoffeeName;
        private TextView mCoffeeSecondName;
        private TextView mCoffeePrice;
        private TextView mCoffeeOriPrice;
        private ImageView mCoffeeSoldOut;
        private ImageView mCoffeeHot;
        private ImageView mCoffeeSweet;
        private ImageView mCoffeeNew;
        private ImageView mCoffeeDrinkType;
        private RelativeLayout mCoffeeCart;
    }
}
