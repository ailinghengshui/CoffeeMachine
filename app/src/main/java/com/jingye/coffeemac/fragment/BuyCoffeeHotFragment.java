package com.jingye.coffeemac.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.activity.CoffeeInfoActivity;
import com.jingye.coffeemac.adapter.HomeGridAdapter;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.loader.NetLoaderTool;
import com.jingye.coffeemac.module.coffeepackagemodule.PackageCoffeeInfoActivity;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.action.GetNoticeInfo;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.bean.result.GetCoffeeResult;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.bean.result.GetNoticeResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.service.domain.PackageCoffeeDosingInfo;
import com.jingye.coffeemac.service.protocol.MachineMaterialMap;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.ui.GridViewExt;
import com.jingye.coffeemac.ui.GridViewExt.OnItemClickListenerExt;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.NetworkUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BuyCoffeeHotFragment extends TFragment implements OnClickListener {

    public static final int UPDATE_COFFEE_THRESHOLD = 60 * 1000;
    private static final String TAG = "BuyCoffeeHotFragment->";
    private static int PAGE_SIZE = 8;
    private Button btnCoffee;
    private Button btnCoffees;
    private OnShowInfoHotListener showInfoListener;
    private OnTouchScreenListener touchScreenListener;
    private List<CoffeeInfo> mHotCoffees = new ArrayList<CoffeeInfo>();
    private ViewFlipper mViewFlipper;
    private LinearLayout mPageIndicatorBar;
    private ImageView mPageIndicatorPrevious;
    private ImageView mPageIndicatorNext;
    private RadioGroup mPage_indicator_group;
    private ImageView mHomeLoading;
    private ImageView mHomeReload;
    private int mPageNum = 0;
    private int mPageIndex = 0;
    private OnItemClickListenerExt mGridItemClickListener = new OnItemClickListenerExt() {

        @Override
        public boolean onItemClick(CoffeeInfo info, View view) {
            if (info != null && !info.isLackMaterials()) {

                if (info.isPackage()) {
                    PackageCoffeeInfoActivity.start(getActivity(), info);
                } else {
                    CoffeeInfoActivity.start(getActivity(), info, 1, 0);
                }

                return true;
            }

            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_coffee, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (showInfoListener == null) {
            showInfoListener = (OnShowInfoHotListener) activity;
        }

        if (touchScreenListener == null) {
            touchScreenListener = (OnTouchScreenListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView();
//        setSelectButton(CoffeeFilter.COFFEE_ORDINATOR);
        fetchAllCoffees();
        getNotice();

        getPayMethod(U.getMyVendorNum());
    }

    private void getPayMethod(String coffeeMachineId) {
        String host;
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        } else if (AppConfig.BUILD_SERVER == AppConfig.Build.TEST) {
            host = StringUtil.HOST_TEST;
        } else {
            host = StringUtil.HOST_LOCAL;
        }
        String url = host + "vendingMachines/getPayMethod";
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("vendingMachineId",coffeeMachineId);
        param.put("token",generateToken());
        NetLoaderTool.INetLoaderListener listener=new NetLoaderTool.INetLoaderListener() {
            @Override
            public void onSuccess(String success) {
                LogUtil.vendor(TAG+success);

                try {
                    org.json.JSONObject jsonObject=new org.json.JSONObject(success);
                    if(jsonObject.has("results")){
                        if(jsonObject.getJSONObject("results").has("isMacForAbc")&&jsonObject.getJSONObject("results").has("isMacForAli")&&jsonObject.getJSONObject("results").has("isMacForWechat")){

                            AppConfig.setMacForAbc(jsonObject.getJSONObject("results").getBoolean("isMacForAbc"));
                            AppConfig.setMacForAli(jsonObject.getJSONObject("results").getBoolean("isMacForAli"));
                            AppConfig.setMacForWechat(jsonObject.getJSONObject("results").getBoolean("isMacForWechat"));
                        }else{
                            LogUtil.vendor("error response");
                        }
                    }else{
                        LogUtil.vendor("error response");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String error) {
                LogUtil.vendor(TAG+"error");
            }
        };
        NetLoaderTool.get(url,param,listener);
    }

    private String generateToken(){
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("sign", "jinyekeji2016");
        tokenJson.put("time", TimeUtil.getNow_millisecond());
        String source = tokenJson.toString();
        String token = null;
        try {
            token = AESUtil.encrypt("442bef40be3e8188", source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private void setSelectButton(CoffeeFilter filter) {
        switch (filter) {
            case COFFEE_PACKAGE:
                PAGE_SIZE = 6;
                btnCoffees.setSelected(true);
                btnCoffees.setTextColor(getResources().getColor(R.color.white));
                btnCoffee.setSelected(false);
                btnCoffee.setTextColor(getResources().getColor(R.color.black));
                break;
            default:
                PAGE_SIZE = 8;
                btnCoffees.setSelected(false);
                btnCoffees.setTextColor(getResources().getColor(R.color.black));
                btnCoffee.setSelected(true);
                btnCoffee.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void findView() {
        mViewFlipper = (ViewFlipper) getView().findViewById(R.id.myFlipper);
        mViewFlipper.setOnClickListener(new ViewFlipperClickEvent());
        mViewFlipper.setLongClickable(true);

        mPageIndicatorBar = (LinearLayout) getView().findViewById(R.id.page_indicator_layout);
        mPageIndicatorBar.setVisibility(View.INVISIBLE);
        mPageIndicatorPrevious = (ImageView) getView().findViewById(R.id.page_indicator_previous);
        mPageIndicatorPrevious.setOnClickListener(this);
        mPageIndicatorNext = (ImageView) getView().findViewById(R.id.page_indicator_next);
        mPageIndicatorNext.setOnClickListener(this);
        mPage_indicator_group = (RadioGroup) getView().findViewById(R.id.page_indicator_group);

        mHomeLoading = (ImageView) getView().findViewById(R.id.home_loading_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) mHomeLoading.getBackground();
        animationDrawable.start();
        mHomeLoading.setVisibility(View.GONE);

        mHomeReload = (ImageView) getView().findViewById(R.id.home_reload);
        btnCoffee = (Button) getView().findViewById(R.id.btnCoffee);
        btnCoffees = (Button) getView().findViewById(R.id.btnCoffees);
        mHomeReload.setOnClickListener(this);
        btnCoffee.setOnClickListener(this);
        btnCoffees.setOnClickListener(this);

    }

    private void fetchAllCoffees() {
        List<CoffeeInfo> cacheInfos = MyApplication.Instance().getCoffeeInfos();
        if (cacheInfos == null || cacheInfos.size() <= 0) {
            getCoffeeInfoFromServer();
        } else if (TimeUtil.getNow_millisecond() -
                MyApplication.Instance().getLastCoffeeInfoUpdateTime() >= UPDATE_COFFEE_THRESHOLD) {
            getCoffeeInfoFromServer();
            MyApplication.Instance().setCoffeeFilter(CoffeeFilter.COFFEE_ORDINATOR);
            setSelectButton(CoffeeFilter.COFFEE_ORDINATOR);
        } else {
            mHotCoffees.clear();

            setSelectButton(MyApplication.Instance().getCoffeeFilter());
            for (CoffeeInfo info : cacheInfos) {
                if (info.isSold()) {
                    switch (MyApplication.Instance().getCoffeeFilter()) {
                        case COFFEE_PACKAGE:
                            if (info.isPackage()) {
                                mHotCoffees.add(info);
                            }
                            break;
                        case COFFEE_ORDINATOR:
                            if (!info.isPackage()) {
                                mHotCoffees.add(info);
                            }
                            break;
                    }
                }
            }


            initFragment();
        }
    }

    private void fetchCoffeeLocal() {
        List<CoffeeInfo> cacheInfos = MyApplication.Instance().getCoffeeInfos();
        mHotCoffees.clear();
        for (CoffeeInfo info : cacheInfos) {
            if (info.isSold()) {
                switch (MyApplication.Instance().getCoffeeFilter()) {
                    case COFFEE_PACKAGE:
                        if (info.isPackage()) {
                            mHotCoffees.add(info);
                        }
                        break;
                    case COFFEE_ORDINATOR:
                        if (!info.isPackage()) {
                            mHotCoffees.add(info);
                        }
                        break;
                }
            }
        }

        initFragment();
    }

    private void getCoffeeInfoFromServer() {
        mHomeLoading.setVisibility(View.VISIBLE);

        setCoffeessBtnEnable(false);

        LogUtil.vendor("cache is empty or cache info is outdated, requery");
        GetCoffeeInfo info = new GetCoffeeInfo();
        info.setUid(U.getMyVendorNum());
        executeBackground(info.toRemote());
    }

    private void getNotice() {
        LogUtil.vendor("get notices from server");

        GetNoticeInfo notice = new GetNoticeInfo();
        notice.setUid(U.getMyVendorNum());
        executeBackground(notice.toRemote());
    }

    @Override
    public void onReceive(Remote remote) {
        int what = remote.getWhat();
        if (what == ITranCode.ACT_COFFEE) {
            int action = remote.getAction();
            if (action == ITranCode.ACT_COFFEE_GET_COFFEE) {
                mHomeLoading.setVisibility(View.GONE);
                GetCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    // get all coffees
                    List<CoffeeInfo> coffees = result.getCoffees();

                    if (coffees != null) {
                        mHotCoffees.clear();
                        for (CoffeeInfo info : coffees) {
//                            if(!info.isAddIce() && info.isSold()){
                            if (info.isSold()) {

                                switch (MyApplication.Instance().getCoffeeFilter()) {
                                    case COFFEE_PACKAGE:
                                        if (info.isPackage()) {
                                            mHotCoffees.add(info);
                                        }
                                        break;
                                    default:
                                        if (!info.isPackage()) {
                                            mHotCoffees.add(info);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // init fragment
                    initFragment();
                    // show discount information
                    //try{
                    //    showDiscountInfo(result.getDiscountInfo());
                    //}catch(Exception e){e.printStackTrace();}
                } else if (result != null && result.getResCode() == ResponseCode.RES_ETIMEOUT) {
                    mHomeReload.setVisibility(View.VISIBLE);
                    setCoffeessBtnEnable(false);
                    ToastUtil.showToast(getActivity(), "获取咖啡信息超时,请重试！");
                } else {
                    mHomeReload.setVisibility(View.VISIBLE);
                    setCoffeessBtnEnable(false);
                    ToastUtil.showToast(getActivity(), "获取咖啡信息错误：" + result.getResCode());
                }
            } else if (action == ITranCode.ACT_COFFEE_NOTICE) {
                GetNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    showNotices(result);
                }
            }
        }
    }

    private void setCoffeessBtnEnable(boolean enabled) {
        if (enabled) {
            if (btnCoffees.isSelected()) {
                btnCoffees.setTextColor(getResources().getColor(R.color.white));
            } else {
                btnCoffees.setTextColor(getResources().getColor(R.color.black));
            }
            btnCoffees.setEnabled(true);
        } else {
            btnCoffees.setTextColor(getResources().getColor(R.color.white));
            btnCoffees.setEnabled(false);
        }
    }

    private void showNotices(GetNoticeResult result) {
        if (result == null)
            return;
        List<String> notices = result.getNoticeList();
        if (notices != null && notices.size() > 0) {
            String message = "";
            for (String notice : notices) {
                message += notice;
            }
            showInfoListener.OnShowInfo(message);
        }
    }

    private void showDiscountInfo(GetDiscountResult discountInfo) {
        if (discountInfo == null)
            return;
        String info = null;
        if (discountInfo.getDiscount() != null && discountInfo.getReductMeet() != null) {
            double showDiscount = Double.parseDouble(discountInfo.getDiscount()) * 10;
            BigDecimal showDiscountBD = new BigDecimal(showDiscount);
            showDiscount = showDiscountBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            info = String.format(Locale.getDefault(), getString(R.string.home_discount_reduction),
                    String.valueOf(showDiscount), discountInfo.getReductMeet(), discountInfo.getReductSub());
        } else if (discountInfo.getDiscount() != null && discountInfo.getReductMeet() == null) {
            double showDiscount = Double.parseDouble(discountInfo.getDiscount()) * 10;
            BigDecimal showDiscountBD = new BigDecimal(showDiscount);
            showDiscount = showDiscountBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            info = String.format(Locale.getDefault(), getString(R.string.home_discount_only), String.valueOf(showDiscount));
        } else if (discountInfo.getDiscount() == null && discountInfo.getReductMeet() != null) {
            info = String.format(Locale.getDefault(), getString(R.string.home_reduction_only),
                    String.valueOf(discountInfo.getReductMeet()), String.valueOf(discountInfo.getReductSub()));
        }

        showInfoListener.OnShowInfo(info);
    }

    private void checkLackMaterials() {
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        boolean isLackWater = (stockWater - MachineMaterialMap.MATERIAL_WATER_LIMIT_VALUE) <= 0;
        double stockCup = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        boolean isLackCup = (stockCup - MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE) <= 0;
        double stockBox1 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        boolean isLackBox1 = (stockBox1 - MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE) <= 0;
        double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        boolean isLackBox2 = (stockBox2 - MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE) <= 0;
        double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        boolean isLackBox3 = (stockBox3 - MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE) <= 0;
        double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        boolean isLackBox4 = (stockBox4 - MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE) <= 0;
        double stockBox5 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_5);
        boolean isLackBox5 = (stockBox5 - MachineMaterialMap.MATERIAL_BOX_5_LIMIT_VALUE) <= 0;
        double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        boolean isLackBean = (stockBean - MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE) <= 0;

        LogUtil.vendor("CHECK STOCK: [" + stockWater + "," + stockBox1 + "," + stockBox2 + "," + stockBox3 + ","
                + stockBox4 + "," + stockBox5 + "," + stockBean + "," + stockCup + "]");
        LogUtil.vendor("CHECK STOCK: [" + isLackWater + "," + isLackBox1 + "," + isLackBox2 + "," + isLackBox3 + ","
                + isLackBox4 + "," + isLackBox5 + "," + isLackBean + "," + isLackCup + "]");

        for (CoffeeInfo info : mHotCoffees) {
            info.setLackMaterials(false);
            if (info.isPackage()) {
                for (CoffeeInfo coffee : info.getCoffeesPackage()) {
                    List<PackageCoffeeDosingInfo> dosingList = coffee.getPackageDoing();
                    if(dosingList!=null) {
                        for (int i = 0; i < dosingList.size(); i++) {
                            PackageCoffeeDosingInfo dosing = dosingList.get(i);
                            int boxID = dosing.getId();
                            if (isLackWater || isLackCup) {
                                info.setLackMaterials(true);
                                break;
                            }

                            if (isLackBox1 && boxID == MachineMaterialMap.MATERIAL_BOX_1) {
                                info.setLackMaterials(true);
                                break;
                            } else if (isLackBox2 && boxID == MachineMaterialMap.MATERIAL_BOX_2) {
                                info.setLackMaterials(true);
                                break;
                            } else if (isLackBox3 && boxID == MachineMaterialMap.MATERIAL_BOX_3) {
                                info.setLackMaterials(true);
                                break;
                            } else if (isLackBox4 && boxID == MachineMaterialMap.MATERIAL_BOX_4) {
                                info.setLackMaterials(true);
                                break;
                            } else if (isLackBox5 && boxID == MachineMaterialMap.MATERIAL_BOX_5) {
                                info.setLackMaterials(true);
                                break;
                            } else if (isLackBean && boxID == MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE) {
                                info.setLackMaterials(true);
                                break;
                            }
                        }
                    }
                }

            } else {
                ArrayList<CoffeeDosingInfo> dosingList = info.getDosingList();
                for (int i = 0; i < dosingList.size(); i++) {
                    CoffeeDosingInfo dosing = dosingList.get(i);
                    int boxID = dosing.getId();
                    if (isLackWater || isLackCup) {
                        info.setLackMaterials(true);
                        break;
                    }

                    if (isLackBox1 && boxID == MachineMaterialMap.MATERIAL_BOX_1) {
                        info.setLackMaterials(true);
                        break;
                    } else if (isLackBox2 && boxID == MachineMaterialMap.MATERIAL_BOX_2) {
                        info.setLackMaterials(true);
                        break;
                    } else if (isLackBox3 && boxID == MachineMaterialMap.MATERIAL_BOX_3) {
                        info.setLackMaterials(true);
                        break;
                    } else if (isLackBox4 && boxID == MachineMaterialMap.MATERIAL_BOX_4) {
                        info.setLackMaterials(true);
                        break;
                    } else if (isLackBox5 && boxID == MachineMaterialMap.MATERIAL_BOX_5) {
                        info.setLackMaterials(true);
                        break;
                    } else if (isLackBean && boxID == MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE) {
                        info.setLackMaterials(true);
                        break;
                    }
                }
            }
        }
    }


    private boolean hasPackage(List<CoffeeInfo> coffeeInfos) {
        for (CoffeeInfo c : coffeeInfos) {
            if (c.isPackage()) {
                return true;
            }
        }
        return false;

    }

    private void initFragment() {
        // check stock
        checkLackMaterials();

        if (hasPackage(MyApplication.Instance().getCoffeeInfos())) {
            setCoffeessBtnEnable(true);
            switch (MyApplication.Instance().getCoffeeFilter()){
                case COFFEE_PACKAGE:
                    setSelectButton(CoffeeFilter.COFFEE_PACKAGE);
                    break;
                default:
                    setSelectButton(CoffeeFilter.COFFEE_ORDINATOR);
                    break;
            }
//            btnCoffees.setEnabled(true);
        } else {
            setCoffeessBtnEnable(false);
            setSelectButton(CoffeeFilter.COFFEE_ORDINATOR);
            MyApplication.Instance().setCoffeeFilter(CoffeeFilter.COFFEE_ORDINATOR);
//            btnCoffees.setTextColor(getResources().getColor(R.color.white));
//            btnCoffees.setEnabled(false);
        }
        // initialize page indicator
        mPageIndex = 0;

        int coffeeCount = mHotCoffees.size();
        mPageNum = (int) (Math.ceil(((double) coffeeCount / (double) PAGE_SIZE)));
        if (mPageNum > 1) {
            mPageIndicatorBar.setVisibility(View.VISIBLE);
        } else {
            mPageIndicatorBar.setVisibility(View.INVISIBLE);
        }
        mPageIndicatorPrevious.setEnabled(false);
        if (mPageNum <= 1) {
            mPageIndicatorNext.setEnabled(false);
        } else {
            mPageIndicatorNext.setEnabled(true);
        }
        mPage_indicator_group.removeAllViews();
        for (int i = 0; i < mPageNum; i++) {
            final RadioButton rb = new RadioButton(getActivity());
            rb.setId(i);
            rb.setButtonDrawable(android.R.color.transparent);
            rb.setPadding(25, 5, 0, 5);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);

            if (i == 0) {
                rb.setChecked(true);
                rb.setTextColor(Color.WHITE);
                rb.setBackgroundResource(R.drawable.radio_sel);
            } else {
                rb.setTextColor(getResources().getColor(R.color.norcolor));
                rb.setBackgroundResource(R.drawable.radio_nor);
            }
            int num = i + 1;
            rb.setText("" + num);
            mPage_indicator_group.addView(rb, params);
        }

        mPage_indicator_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                LogUtil.i("", "---onCheckedChanged---" + checkedId);
                mPageIndex = checkedId;
                for (int i = 0; i < mPageNum; i++) {
                    if (i == checkedId) {

                        if (group.getChildAt(i) != null) {
                            ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                            ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_sel);
                        }
                    } else {
                        if (group.getChildAt(i) != null) {
                            ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.norcolor));
                            ((RadioButton) group.getChildAt(i)).setBackgroundResource(R.drawable.radio_nor);
                        }
                    }
                }

                if (checkedId > 0) {
                    mPageIndicatorPrevious.setEnabled(true);
                } else {
                    mPageIndicatorPrevious.setEnabled(false);
                }

                if (checkedId < mPageNum - 1) {
                    mPageIndicatorNext.setEnabled(true);
                } else {
                    mPageIndicatorNext.setEnabled(false);
                }
                loadGrid(checkedId);
                mViewFlipper.setDisplayedChild(checkedId);
                onTouchScreen();

            }
        });
        mViewFlipper.removeAllViews();
        for (int i = 0; i < mPageNum; i++) {
            GridViewExt gridview = new GridViewExt(this.getActivity());
            gridview.setOnItemClickListener(mGridItemClickListener);
            mViewFlipper.addView(gridview);
        }

        // default show first page
        GridViewExt grid = (GridViewExt) mViewFlipper.getChildAt(mPageIndex);
        if (grid != null) {
            if (MyApplication.Instance().getCoffeeFilter() == CoffeeFilter.COFFEE_PACKAGE) {
                grid.setColumnCount(3);
            } else {
                grid.setColumnCount(4);
            }
            HomeGridAdapter adapter = new HomeGridAdapter(this.getActivity(), loadPage(mPageIndex, mHotCoffees), showInfoListener);
            grid.setAdapter(adapter);
        }
    }

    public void refreshFragment() {
        LogUtil.vendor("menu refreshing...");

        checkLackMaterials();

        for (int i = 0; i < mPageNum; i++) {
            GridViewExt grid = (GridViewExt) mViewFlipper.getChildAt(i);
            if (grid != null) {
                HomeGridAdapter adapter = new HomeGridAdapter(this.getActivity(), loadPage(i, mHotCoffees), showInfoListener);
                if (MyApplication.Instance().getCoffeeFilter() == CoffeeFilter.COFFEE_PACKAGE) {
                    grid.setColumnCount(3);
                } else {
                    grid.setColumnCount(4);
                }
                grid.setAdapter(adapter);
            }
        }
    }

    private List<CoffeeInfo> loadPage(final int pageNum, final List<CoffeeInfo> info) {
        List<CoffeeInfo> coffees = new ArrayList<CoffeeInfo>();
        int index = pageNum * PAGE_SIZE;
        int left = info.size() - pageNum * PAGE_SIZE;
        int count = left >= PAGE_SIZE ? PAGE_SIZE : left;
        for (int i = index; i < index + count; i++) {
            coffees.add(mHotCoffees.get(i));
        }

        return coffees;
    }

    private void loadGrid(int pagePosition) {
        GridViewExt itemGrid = (GridViewExt) mViewFlipper.getChildAt(pagePosition);
        if (itemGrid != null) {
            BaseAdapter adapter = itemGrid.getAdapter();
            if (adapter == null) {
                if (MyApplication.Instance().getCoffeeFilter() == CoffeeFilter.COFFEE_PACKAGE) {
                    itemGrid.setColumnCount(3);
                } else {
                    itemGrid.setColumnCount(4);
                }
                itemGrid.setAdapter(new HomeGridAdapter(this.getActivity(), loadPage(pagePosition, mHotCoffees), showInfoListener));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.vendor("BuyCoffeeHotFragment->onResume");
        showInfoListener.OnUpdateCartGoods(null);
    }

    public void loadPageByLeft() {
        if (mPageNum == 0) {
            return;
        }

        if (mPageIndex < mPageNum - 1) {
            mPageIndex++;
            updatePageIndicatorStatus(mPageIndex, mPageNum);
        }
    }

    public void loadPageByRight() {
        if (mPageNum == 0) {
            return;
        }

        if (mPageIndex > 0) {
            mPageIndex--;
            updatePageIndicatorStatus(mPageIndex, mPageNum);
        }
    }

    private void onTouchScreen() {
        if (touchScreenListener != null) {
            touchScreenListener.OnTouchScreenHome();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.page_indicator_previous:
                if (mPageIndex <= 0)
                    return;
                mPageIndex--;
                updatePageIndicatorStatus(mPageIndex, mPageNum);

                break;
            case R.id.page_indicator_next:
                if (mPageIndex >= mPageNum - 1)
                    return;
                mPageIndex++;
                updatePageIndicatorStatus(mPageIndex, mPageNum);

                break;
            case R.id.home_reload:
                mHomeReload.setVisibility(View.GONE);
                fetchAllCoffees();
                break;
            case R.id.btnCoffee:
                MyApplication.Instance().setCoffeeFilter(CoffeeFilter.COFFEE_ORDINATOR);
                setSelectButton(CoffeeFilter.COFFEE_ORDINATOR);
                fetchCoffeeLocal();
                break;
            case R.id.btnCoffees:
                MyApplication.Instance().setCoffeeFilter(CoffeeFilter.COFFEE_PACKAGE);
                setSelectButton(CoffeeFilter.COFFEE_PACKAGE);
                fetchCoffeeLocal();
                break;
        }
    }

    private void updatePageIndicatorStatus(int pageIndex, int pageNum) {

        if (pageIndex > 0) {
            mPageIndicatorPrevious.setEnabled(true);
        } else {
            mPageIndicatorPrevious.setEnabled(false);
        }

        if (pageIndex < pageNum - 1) {
            mPageIndicatorNext.setEnabled(true);
        } else {
            mPageIndicatorNext.setEnabled(false);
        }

        ((RadioButton) mPage_indicator_group.getChildAt(pageIndex)).setChecked(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseImageViews();
    }

    private void releaseImageViews() {
        releaseImageView(mHomeLoading);
    }

    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public enum CoffeeFilter {
        COFFEE_ORDINATOR,
        COFFEE_PACKAGE;
    }

    public interface OnShowInfoHotListener {
        public void OnShowInfo(String info);

        public void OnUpdateCartGoods(View startView);
    }

    public interface OnTouchScreenListener {
        public void OnTouchScreenHome();
    }

    public class ViewFlipperClickEvent implements OnClickListener {

        @Override
        public void onClick(View v) {
            GridViewExt gv = (GridViewExt) mViewFlipper.getChildAt(mPageIndex);
            if (gv != null) {
                gv.unCheckPressed();
            }
        }
    }
}
