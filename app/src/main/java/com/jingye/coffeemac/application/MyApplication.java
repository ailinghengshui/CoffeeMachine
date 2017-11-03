package com.jingye.coffeemac.application;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.action.CoffeeAction;
import com.jingye.coffeemac.action.LoginAction;
import com.jingye.coffeemac.action.SysAction;
import com.jingye.coffeemac.action.UserAction;
import com.jingye.coffeemac.beans.CartPayItem;
import com.jingye.coffeemac.common.action.TActionFactory;
import com.jingye.coffeemac.common.action.TViewWatcher;
import com.jingye.coffeemac.common.database.TDatabase;
import com.jingye.coffeemac.domain.OrderFetchStatus;
import com.jingye.coffeemac.fragment.BuyCoffeeHotFragment;
import com.jingye.coffeemac.helper.cache.BaseDataCacher;
import com.jingye.coffeemac.helper.cache.ImageCacher;
import com.jingye.coffeemac.helper.cache.LruCache;
import com.jingye.coffeemac.loader.ImageLoaderConfig;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.VendorService;
import com.jingye.coffeemac.service.bean.result.GetDiscountResult;
import com.jingye.coffeemac.service.domain.CoffeeDosingInfo;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.util.CommonUtil;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.log.AppErrorLogHandler;
import com.jingye.coffeemac.util.log.LogUtil;
import com.jingye.coffeemac.util.multicard.MultiCard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    public static Set<String> mGlobalWashTimeSet = new HashSet<String>();
    private static MyApplication instance;
    // 接收系统时间改变的广播，执行定时清洗功能
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String time = sdf.format(now);
                if (mGlobalWashTimeSet.contains(time)) {
                    LogUtil.vendor("start to execute washing task");
                    new WashingTask().execute();
                }
            }
        }
    };
    protected boolean isCoreService = false;
    private boolean active;
    private boolean isMachineIdle;
    private boolean isMakingCoffee;
    private boolean isWaitMaintenance;
    private int maintenanceCode;
    private boolean isDesk = false;
    private DataCacheHandler dataHandler;
    // indent status cache
    private LruCache<String, OrderFetchStatus> indentTimeoutRequest = new LruCache<String, OrderFetchStatus>(500);
    // cart pay items
    private ArrayList<CartPayItem> cartPayItems = new ArrayList<CartPayItem>();
    // 咖啡信息相关
    private long lastCoffeeInfoUpdateTime = -1;
    // 同步库存信息
    private long lastSyncStockTime = -1;
    // 冲管道时间
    private long lastWashPipleTime = -1;
    // 咖啡配方信息
    private List<CoffeeInfo> coffeeInfos;
    // discount information
    private GetDiscountResult discountInfo;

    //显示是否需要退款
    private boolean isNeedRollback = false;
    private BuyCoffeeHotFragment.CoffeeFilter mCoffeeFilter= BuyCoffeeHotFragment.CoffeeFilter.COFFEE_ORDINATOR;

    public static MyApplication Instance() {
        return instance;
    }

    public boolean isMachineIdle() {
        return isMachineIdle;
    }

    public void setMachineIdle(boolean isMachineIdle) {
        this.isMachineIdle = isMachineIdle;
    }

    public boolean isMakingCoffee() {
        return isMakingCoffee;
    }

    public void setMakingCoffee(boolean isMakingCoffee) {
        this.isMakingCoffee = isMakingCoffee;
    }

    public boolean isWaitMaintenance() {
        return isWaitMaintenance;
    }

    public void setWaitMaintenance(boolean isWaitMaintenance) {
        this.isWaitMaintenance = isWaitMaintenance;
    }

    public OrderFetchStatus getIndentStatus(String fetchCode) {
        if (TextUtils.isEmpty(fetchCode)) {
            return null;
        }

        OrderFetchStatus indentStatus = indentTimeoutRequest.get(fetchCode);
        return indentStatus;
    }

    public void setWaitMaintenanceCode(int code, boolean isNeedRollback) {
        this.isNeedRollback = isNeedRollback;
        this.maintenanceCode = code;
    }

    public boolean getDesk() {
        return isDesk;
    }

    public void setDesk(boolean isDesk) {
        this.isDesk = isDesk;
    }

    public int getWaitMaintenanceCode() {
        return maintenanceCode;
    }

    public boolean isNeedRollback() {
        return isNeedRollback;
    }

    public void setNeedRollback(boolean isNeedRollback){
        this.isNeedRollback=isNeedRollback;
    }

    public void addIndentStatus(OrderFetchStatus status) {
        indentTimeoutRequest.put(status.getFetchCode(), status);
    }

    public void removeIndentStatus(String fetchCode) {
        if (TextUtils.isEmpty(fetchCode))
            return;
        indentTimeoutRequest.remove(fetchCode);
    }

    public ArrayList<CartPayItem> getCartPayItems() {
        return cartPayItems;
    }

    public void addCoffeeToCartPay(CartPayItem item) {
        if (cartPayItems == null) {
            cartPayItems = new ArrayList<CartPayItem>();
        }

        boolean isNew = true;
        for (CartPayItem payItem : cartPayItems) {
            if (item.getCoffeeInfo().isPackage()) {
                if(item.getCoffeeInfo().getCoffeeId()==payItem.getCoffeeInfo().getCoffeeId()&&item.getPackageSugarSize()==payItem.getPackageSugarSize()
                        &&item.getPackageSugarLevelMap().equals(payItem.getPackageSugarLevelMap())){
                    payItem.setBuyNum(payItem.getBuyNum() + item.getBuyNum());
                    isNew = false;
                    break;
                }

            } else {
                if (item.getCoffeeInfo().getCoffeeId() == payItem.getCoffeeInfo().getCoffeeId()
                        && item.getSugarLevel() == payItem.getSugarLevel()) {
                    payItem.setBuyNum(payItem.getBuyNum() + item.getBuyNum());
                    isNew = false;
                    break;
                }
            }
        }

        if (isNew) {

            cartPayItems.add(item);
        }
    }

    public void removeCartPay(CartPayItem item) {

        for (int i = 0; i < cartPayItems.size(); i++) {
            CartPayItem cpi = cartPayItems.get(i);
            if(cpi.getCoffeeInfo().isPackage()){
                if (cpi.getCoffeeInfo().getCoffeeId() == item.getCoffeeInfo().getCoffeeId()
                        && cpi.getPackageSugarSize() == item.getPackageSugarSize()
                        &&cpi.getPackageSugarLevelMap().equals(item.getPackageSugarLevelMap())) {
                    cartPayItems.remove(i);
                    break;
                }

            }else {
                if (cpi.getCoffeeInfo().getCoffeeId() == item.getCoffeeInfo().getCoffeeId()
                        && cpi.getSugarLevel() == item.getSugarLevel()) {
                    cartPayItems.remove(i);
                    break;
                }
            }
        }
    }

    private boolean isEqual(Map<Integer, Integer> packageSugarLevelMap, Map<Integer, Integer> packageSugarLevelMap1) {
        packageSugarLevelMap.equals(packageSugarLevelMap1);
        for(Map.Entry<Integer,Integer> entry:packageSugarLevelMap.entrySet()){
            Log.d("TAG","TAG:"+entry.getKey()+":"+entry.getValue());
        }
        for(Map.Entry<Integer,Integer> entry:packageSugarLevelMap1.entrySet()){
            Log.d("TAG","TAG:"+entry.getKey()+":"+entry.getValue());
        }
        return true;
    }

    public void clearCartPay() {
        if (cartPayItems != null) {
            cartPayItems.clear();
        }
    }

    public int getCartNums() {
        int num = 0;
        if (cartPayItems == null)
            return num;
        for (CartPayItem item : cartPayItems) {
            if (item.getCoffeeInfo().isPackage()) {
                num += item.getBuyNum() * item.getCoffeeInfo().getPackageNum();
            } else {
                num += item.getBuyNum();
            }
        }

        return num;
    }

    public long getLastCoffeeInfoUpdateTime() {
        return lastCoffeeInfoUpdateTime;
    }

    public void setLastCoffeeInfoUpdateTime(long time) {
        lastCoffeeInfoUpdateTime = time;
    }

    public long getLastSyncStockTime() {
        return lastSyncStockTime;
    }

    public void setLastSyncStockTime(long lastSyncStockTime) {
        this.lastSyncStockTime = lastSyncStockTime;
    }

    public long getLastWashPipleTime() {
        return lastWashPipleTime;
    }

    public void setLastWashPipleTime(long lastWashPipleTime) {
        this.lastWashPipleTime = lastWashPipleTime;
    }

    public BuyCoffeeHotFragment.CoffeeFilter getCoffeeFilter(){
        return mCoffeeFilter;
    }

    public void setCoffeeFilter(BuyCoffeeHotFragment.CoffeeFilter coffeeFilter){
        this.mCoffeeFilter=coffeeFilter;
    }

    public List<CoffeeInfo> getCoffeeInfos() {
        return coffeeInfos;
    }

    public ArrayList<CoffeeDosingInfo> getDosingListInfoByCoffeeID(int coffeeID) {
        List<CoffeeInfo> coffeeInfos = getCoffeeInfos();
        if (coffeeInfos == null) {
            ToastUtil.showToast(this, getString(R.string.pay_qrcode_error_norecipe));
            LogUtil.e("vendor", "can't get the coffeeInfos in cache");
            return null;
        }

        ArrayList<CoffeeDosingInfo> dosingList = new ArrayList<CoffeeDosingInfo>();
        CoffeeInfo coffee = getCoffeeInfoByCoffeeID(coffeeID);
        if (coffee != null) {
            ArrayList<CoffeeDosingInfo> baseDosingList = coffee.getDosingList();
            for (int j = 0; j < baseDosingList.size(); j++) {
                CoffeeDosingInfo info = baseDosingList.get(j);

                CoffeeDosingInfo newInfo = new CoffeeDosingInfo();
                newInfo.setId(info.getId());
                newInfo.setName(info.getName());
                newInfo.setValue(info.getValue());
                newInfo.setOrder(info.getOrder());
                newInfo.setWater(info.getWater());
                newInfo.setEjection(info.getEjection());
                newInfo.setStirtime(info.getStirtime());
                newInfo.setStirvol(info.getStirvol());
                newInfo.setFactor(info.getFactor());
                newInfo.setMacConifg(info.getMacConifg());
                newInfo.setBoxID(info.getBoxID());
                dosingList.add(newInfo);
            }
        }

        return dosingList;
    }

    public String getCoffeeNameByCoffeeID(int coffeeID) {
        String coffeeName = "";
        List<CoffeeInfo> coffeeList = getCoffeeInfos();
        if (coffeeList != null) {
            for (CoffeeInfo coffee : coffeeList) {
                if (coffee.getCoffeeId() == coffeeID) {

                    if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
                        coffeeName = coffee.getCoffeeTitleEn();
                    } else {
                        coffeeName = coffee.getCoffeeTitle();
                    }
                    break;
                }
            }
        }
        return coffeeName;
    }

    public CoffeeInfo getCoffeeInfoByCoffeeID(int coffeeID) {
        List<CoffeeInfo> coffeeList = getCoffeeInfos();
        if (coffeeList != null) {
            for (CoffeeInfo coffee : coffeeList) {
                if (coffee.getCoffeeId() == coffeeID) {
                    return coffee;
                }
            }
        }

        return null;
    }

    public GetDiscountResult getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(GetDiscountResult discountInfo) {
        this.discountInfo = discountInfo;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;

        // 系统启动则启动
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ApplicationInfo applicationInfo = instance.getApplicationInfo();
        List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfos.size(); i++) {
            RunningAppProcessInfo runningAppProcessInfo = runningAppProcessInfos.get(i);
            if (android.os.Process.myPid() == runningAppProcessInfo.pid) {
                if (runningAppProcessInfo.processName
                        .equals(applicationInfo.processName)) {
                    LogUtil.systemOut("我是主进程啊");
                    isCoreService = false;
                } else if (runningAppProcessInfo.processName
                        .equals(applicationInfo.processName + ":core")) {
                    LogUtil.systemOut("我是服务进程啊");
                    isCoreService = true;
                }
                break;
            }
        }

        // 如果在这里启动，则代码首次安装启动，因为服务正常情况是在开机被激活的哦
        if (!VendorService.active) { // 如果后台服务没有启动则在这里启动
            LogUtil.systemOut("Create Vendor Service");
            Intent service = new Intent(this, VendorService.class);
            service.putExtra(VendorService.EXTRA_FROM, VendorService.FROM_PACKAGE);
            this.startService(service);
        }

        if (!isCoreService) {
            // 注册客户端action
            TActionFactory factory = TActionFactory.newInstance();
            factory.registerAction(new UserAction());
            factory.registerAction(new CoffeeAction());
            factory.registerAction(new LoginAction());
            factory.registerAction(new SysAction());

            // 初始化多媒体卡信息
            MultiCard.init();

            // 初始化屏幕信息
            ScreenUtil.GetInfo(MyApplication.this);

            // 初始化缓存
            initDataCache();

            // 初始化广播监听
            registerBroadcast();
        } else {
            TDatabase.getInstance().openDatabase(); // 数据库就放在服务层了喽
        }

        // 错误日志
        AppErrorLogHandler.getInstance(this);
        // COMMON
        CommonUtil.init();
        // 图片加载器
//		ImageLoaderConfig.checkImageLoaderConfig(this);
        //加载自动清洗时间
        loadWashTime();
    }

    private void loadWashTime() {
        Set<String> timeSet = SharePrefConfig.getInstance().getWashTime();
        if (timeSet != null) {
            mGlobalWashTimeSet.addAll(timeSet);
        } else {
            String[] DEFAULT_WASH_TIMEPOINT = {"13:00", "20:00"};
            mGlobalWashTimeSet.addAll(Arrays.asList(DEFAULT_WASH_TIMEPOINT));
        }
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
    }

    public void onTerminate() {
        if (!isCoreService) {
            TViewWatcher.newInstance().unBindView(dataHandler);
            LogUtil.d("MyApplication", "application terminate");
        } else {
            TDatabase.getInstance().closeDatabase();// 当然关闭也在服务层
        }
    }

    @Override
    public void onLowMemory() {
        LogUtil.vendor("onLowMemory");
        super.onLowMemory();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private void initDataCache() {
        BaseDataCacher.instance();
        ImageCacher.newInstance();

        coffeeInfos = new ArrayList<CoffeeInfo>();
        dataHandler = new DataCacheHandler();
        TViewWatcher.newInstance().bindView(dataHandler);
    }

    private class WashingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // check machine idle status
            while (!isMachineIdle()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // send the wash start order
            Remote remote = new Remote();
            remote.setWhat(ITranCode.ACT_COFFEE_SERIAL_PORT);
            remote.setAction(ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING_START);
            TViewWatcher.newInstance().notifyAll(remote);
            return true;
        }
    }
}