package com.jingye.coffeemac.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.adapter.FragmentTabAdapter;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.common.action.TActivity;
import com.jingye.coffeemac.fragment.BuyCoffeeHotFragment;
import com.jingye.coffeemac.fragment.ExchangeFragment;
import com.jingye.coffeemac.fragment.FetchCoffeeFragment;
import com.jingye.coffeemac.fragment.HelpFragment;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.bean.StatusChangeNotify;
import com.jingye.coffeemac.service.bean.action.GetAdvPicsInfo;
import com.jingye.coffeemac.service.bean.action.GetCoffeeInfo;
import com.jingye.coffeemac.service.bean.result.ActiveNoticeResult;
import com.jingye.coffeemac.service.bean.result.GeneralActionResult;
import com.jingye.coffeemac.service.domain.Ancestor;
import com.jingye.coffeemac.ui.MarqueeTextView;
import com.jingye.coffeemac.util.CountDownTimer;
import com.jingye.coffeemac.util.SharePrefConfig;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToolUtil;
import com.jingye.coffeemac.util.U;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePageActivity extends TActivity implements OnClickListener, View.OnTouchListener,
        GestureDetector.OnGestureListener, BuyCoffeeHotFragment.OnShowInfoHotListener,
        BuyCoffeeHotFragment.OnTouchScreenListener, FetchCoffeeFragment.OnTouchScreenListener,
        ExchangeFragment.OnTouchScreenListener {

    public static final String REFRESH_MENU = "menu_refresh";
    public static final String INDEX = "index";
    private static final int IDLE_TIMEOUT_VALUE = 100;
    private static final int IDLE_RESET_TIMER_LIMIT = 30 * 1000;
    private static final int FLING_MIN_DISTANCE = 100;
    private static final int FLING_MIN_VELOCITY = 200;
    private static final String TAG = "HomePageActivity->";
    public List<Fragment> fragments;
    private RelativeLayout mHomeTitleBar;
    private ImageView mHomeNetworkStatus;
    private ImageView mZhimg;
    private ImageView mEnimg;
    private RadioGroup mTabRg;
    private FrameLayout mPayCartLinear;
    private ImageView mPayCart;
    private TextView mPayCartIndicator;
    private RelativeLayout mHornLayout;
    private MarqueeTextView mMarqueeTv;
    private ImageView mHornIcon;
    private BuyCoffeeHotFragment hotCoffeeFragment;
    private int mCurrentTabIndex = 0;
    private CountDownTimer idleDownTimer;
    private long lastResetTimerStamp;
    private GestureDetector mGestureDetector;
    private ShoppingCartAnim cartAnimation;
    private View mHornLine;
    private boolean isNeedRefreshCoffee = false;
    private boolean isForeground;

    public static void start(Activity activity, boolean refreshMenu, int index) {
        Intent intent = new Intent();
        intent.setClass(activity, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(REFRESH_MENU, refreshMenu);
        intent.putExtra(INDEX, index);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_layout);
        proceedExtra();

        initView();
        initTabBar();
        initIdleTimer();
        initGestrue();
        initStatus();
        initTitleLanguage();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private void proceedExtra() {
        Intent intent = getIntent();
        mCurrentTabIndex = intent.getIntExtra(INDEX, 0);
    }

    private void initTitleLanguage() {

        mZhimg = (ImageView) findViewById(R.id.home_title_language_zh);
        mEnimg = (ImageView) findViewById(R.id.home_title_language_en);
        mZhimg.setVisibility(View.VISIBLE);
        mEnimg.setVisibility(View.VISIBLE);

        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGECH) {

            mZhimg.setBackgroundResource(R.drawable.zh_sel);
            mEnimg.setBackgroundResource(R.drawable.en_nor);
        } else {
            mZhimg.setBackgroundResource(R.drawable.zh_nor);
            mEnimg.setBackgroundResource(R.drawable.en_sel);
        }
        mZhimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ToolUtil.isFastClick())
                    return;
                if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGECH)
                    return;
                mZhimg.setBackgroundResource(R.drawable.zh_sel);
                mEnimg.setBackgroundResource(R.drawable.en_nor);
                SharePrefConfig.getInstance().setLanguageType(SharePrefConfig.LANGUAGECH);
                ChangeLanguage();
            }
        });

        mEnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ToolUtil.isFastClick())
                    return;
                if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN)
                    return;
                mZhimg.setBackgroundResource(R.drawable.zh_nor);
                mEnimg.setBackgroundResource(R.drawable.en_sel);
                SharePrefConfig.getInstance().setLanguageType(SharePrefConfig.LANGUAGEEN);
                ChangeLanguage();
            }
        });
    }


    private void ChangeLanguage() {

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (SharePrefConfig.getInstance().getLanguageType() == SharePrefConfig.LANGUAGEEN) {
            config.locale = Locale.ENGLISH;
            config.fontScale = 1;
            resources.updateConfiguration(config, dm);
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            config.fontScale = 1;
            resources.updateConfiguration(config, dm);
        }

        WelcomeActivity.start(this);
        HomePageActivity.start(this, false, mCurrentTabIndex);
        finish();
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.vendor("HomePageActivity -> onNewIntent");

        boolean isRefreshMenu = intent.getBooleanExtra(REFRESH_MENU, false);
        if (isRefreshMenu) {
            if (hotCoffeeFragment != null) {
                hotCoffeeFragment.refreshFragment();
            }
        }
    }

    private void initView() {
        mHornLayout = (RelativeLayout) findViewById(R.id.home_title_horn_linear);
        mHornLine = findViewById(R.id.home_title_horn_line);
        mHornIcon = (ImageView) findViewById(R.id.home_title_horn);
        mMarqueeTv = (MarqueeTextView) findViewById(R.id.home_marquee);
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
        mHomeNetworkStatus = (ImageView) findViewById(R.id.home_title_network_status);

        mPayCartLinear = (FrameLayout) findViewById(R.id.coffee_shopping_cart_layout);
        mPayCartLinear.setVisibility(View.INVISIBLE);
        mPayCart = (ImageView) findViewById(R.id.coffee_shopping_cart);
        mPayCart.setOnClickListener(this);
        mPayCartIndicator = (TextView) findViewById(R.id.coffee_shopping_cart_indicator);
    }

    private void initTabBar() {
        hotCoffeeFragment = new BuyCoffeeHotFragment();
        fragments = new ArrayList<Fragment>();
        fragments.add(hotCoffeeFragment);
//      fragments.add(new ToDoFragment());
//      fragments.add(new ToDoFragment());
        fragments.add(new FetchCoffeeFragment());
        fragments.add(new ExchangeFragment());
        fragments.add(new HelpFragment());

        mTabRg = (RadioGroup) findViewById(R.id.tabs_home_page);
        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, mTabRg, mCurrentTabIndex);

        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                LogUtil.vendor(TAG+"Extra----" + index + "clicked");
                resetIdleTimer();
                mCurrentTabIndex = index;
                if (mCurrentTabIndex == 0) {
//                    mHornLayout.setVisibility(View.VISIBLE);
                    setHornLayout(View.VISIBLE);
                } else if (mCurrentTabIndex == 1) {
//                    mHornLayout.setVisibility(View.INVISIBLE);
                    setHornLayout(View.INVISIBLE);
                } else if (mCurrentTabIndex == 2) {
//                    mHornLayout.setVisibility(View.INVISIBLE);
                    setHornLayout(View.INVISIBLE);
                } else if (mCurrentTabIndex == 3) {
//                    mHornLayout.setVisibility(View.INVISIBLE);
                    setHornLayout(View.INVISIBLE);
                }
            }
        });
    }

    private void setHornLayout(int visibility) {
        mHornLayout.setVisibility(visibility);
        mHornLine.setVisibility(visibility);
        mHornIcon.setVisibility(visibility);
        mMarqueeTv.setVisibility(visibility);
    }

    private void initIdleTimer() {
        lastResetTimerStamp = TimeUtil.getNow_millisecond();
        idleDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
    }

    private void onCountDown(int value) {
        LogUtil.vendor(TAG + value);
        if (value <= 0 ) {
            if(isForeground) {
                WelcomeActivity.start(this);
                this.finish();
            }else{
                LogUtil.vendor(TAG+" filter this");
            }
        }
    }

    private void resetIdleTimer() {
        LogUtil.vendor(TAG + "resetIdleTimer");
        if (TimeUtil.getNow_millisecond() - lastResetTimerStamp
                > IDLE_RESET_TIMER_LIMIT) {
            if (idleDownTimer != null) {
                idleDownTimer.cancelCountDownTimer();
                idleDownTimer.startCountDownTimer(IDLE_TIMEOUT_VALUE, 1000, 1000);
                lastResetTimerStamp = TimeUtil.getNow_millisecond();
            }
        }
    }

    private void initGestrue() {
        mGestureDetector = new GestureDetector(this, this);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.vendor("HomePageActivity->onStart");
        if (idleDownTimer != null)
            idleDownTimer.startCountDownTimer(IDLE_TIMEOUT_VALUE, 1000, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.vendor("HomePageActivity->onResume");
        isForeground = true;


        Log.d("mCurrentTabIndex", "mCurrentTabIndex" + mCurrentTabIndex);
        if (mCurrentTabIndex == 0) {
//            mHornLayout.setVisibility(View.VISIBLE);
            setHornLayout(View.VISIBLE);
            mTabRg.check(R.id.tab_buy_coffee_hot);
        } else if (mCurrentTabIndex == 1) {
//            mHornLayout.setVisibility(View.INVISIBLE);
            setHornLayout(View.INVISIBLE);
            mTabRg.check(R.id.tab_buy_coffee_fetch);
        } else if (mCurrentTabIndex == 2) {
//            mHornLayout.setVisibility(View.INVISIBLE);
            setHornLayout(View.INVISIBLE);
            mTabRg.check(R.id.tab_buy_coffee_exchange);
        } else if (mCurrentTabIndex == 3) {
//            mHornLayout.setVisibility(View.INVISIBLE);
            setHornLayout(View.INVISIBLE);
            mTabRg.check(R.id.tab_buy_coffee_help);
        }

        if (isNeedRefreshCoffee) {
            LogUtil.vendor(TAG+"isNeedRefreshCoffee" + isNeedRefreshCoffee);
            GetCoffeeInfo info = new GetCoffeeInfo();
            info.setUid(U.getMyVendorNum());
            executeBackground(info.toRemote());
            isNeedRefreshCoffee = false;
        }

        LogUtil.vendor(TAG+"isWaitMantennance:" + MyApplication.Instance().isWaitMaintenance());
        if (MyApplication.Instance().isWaitMaintenance()) {
            Intent intent = new Intent();
            intent.setClass(this, WaitMaintanceActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.vendor("HomePageActivity->onPause");
        isForeground = false;
        if (idleDownTimer != null)
            idleDownTimer.cancelCountDownTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.vendor("HomePageActivity->onStop");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffee_shopping_cart:
                PayCartActivity.start(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        } else if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_ACTIVENOTICE) {
                ActiveNoticeResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.getResCode() == 200) {
                    String type = result.getType();
                    if (type.equals("102")) {
                        isNeedRefreshCoffee = true;
                    }
//                    else if(type.equals("103")) {
//
//                        GetAdvPicsInfo info = new GetAdvPicsInfo();
//                        info.setUid(U.getMyVendorNum());
//                        execute(info.toRemote());
//                    }
                }
            }
        }
    }

    private void initStatus() {
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void updateStatus(int status) {

        if (status == ITranCode.STATUS_NO_NETWORK
                || status == ITranCode.STATUS_CONNECT_FAILED
                || status == ITranCode.STATUS_FORBIDDEN
                || status == ITranCode.STATUS_UNLOGIN) {
            mHomeNetworkStatus.setVisibility(View.VISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_broken);
        } else if (status == ITranCode.STATUS_LOGGING) {
            mHomeNetworkStatus.setVisibility(View.VISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connecting);
        } else {
            mHomeNetworkStatus.setVisibility(View.INVISIBLE);
            mHomeNetworkStatus.setImageResource(R.drawable.home_network_status_connected);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("---onFling---- ");
        if (mCurrentTabIndex == 0 && hotCoffeeFragment != null) {
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                try {
                    hotCoffeeFragment.loadPageByLeft();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("ERROR", TAG+"something wrong with loadPageByLeft");
                }
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                try {
                    hotCoffeeFragment.loadPageByRight();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("ERROR", TAG+"something wrong with loadPageByRight");
                }
            }
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void OnShowInfo(String info) {
        if (!TextUtils.isEmpty(info) && mCurrentTabIndex == 0) {
//            mHornLayout.setVisibility(View.VISIBLE);
            setHornLayout(View.VISIBLE);
            mMarqueeTv.setText(info);
            mMarqueeTv.setMarquee(true);
        } else {
            //mHomeTitleBar.setBackgroundColor(Color.parseColor("#00000000"));
            setHornLayout(View.INVISIBLE);
        }
    }

    @Override
    public void OnUpdateCartGoods(View startView) {

        resetIdleTimer();
        if (null != startView) {
            cartAnimation = new ShoppingCartAnim(HomePageActivity.this);
            cartAnimation.startAnim(startView, (View) mPayCartLinear);

        } else {
            int num = MyApplication.Instance().getCartNums();
            if (num > 0) {
                mPayCartLinear.setVisibility(View.VISIBLE);
                mPayCartIndicator.setText(String.valueOf(num));
            } else {
                mPayCartLinear.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void OnTouchScreenHome() {
        resetIdleTimer();
    }

    @Override
    public void OnTouchScreenFetch() {
        resetIdleTimer();
    }

    @Override
    public void OnTouchScreenExchange() {
        resetIdleTimer();
    }


    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        if (cartAnimation != null) {
            try {
                cartAnimation.root.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onLowMemory();
        }
    }


    /*
     * 购物车添加动画
     */
    public class ShoppingCartAnim {
        public ViewGroup root;//动画层
        private ImageView buyImg;//播放动画的参照imageview
        private int[] start_location = new int[2];// 这是用来存储动画开始位置的X、Y坐标;
        private int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标;

        public ShoppingCartAnim(Activity activity) {
            buyImg = new ImageView(activity);//buyImg是动画的图片
            buyImg.setImageResource(R.drawable.coffee_shopping_cart_icon);// 设置buyImg的图片
            root = (ViewGroup) activity.getWindow().getDecorView();//创建一个动画层
            root.addView(buyImg);//将动画参照imageview放入
        }


        /**
         * 将image图片添加到动画层并放在起始坐标位置
         *
         * @param view     播放动画的view
         * @param location 起始位置
         * @return
         */
        private View addViewFromAnimLayout(View view, int[] location) {
            int x = location[0];
            int y = location[1];
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = x;
            lp.topMargin = y;
            view.setLayoutParams(lp);
            return view;
        }


        public void startAnim(View startView, View endView) {
            // 这是获取起始目标view在屏幕的X、Y坐标（这也是动画开始的坐标）
            startView.getLocationInWindow(start_location);
            // 购物车结束位置
            endView.getLocationInWindow(end_location);
            //将动画图片和起始坐标绘制成新的view，用于播放动画
            //将image图片添加到动画层
            /**这里为什么不直接传一个图片而是传一个imageview呢？
             * 因为我这样做的目的是clone动画播放控件，为什么要clone呢？
             * 因为如果用户连续点击添加购物车的话，如果只用一个imageview去播放动画的话，这个动画就会成还没播放完就回到原点重新播放。
             * 而如果clone一个imageview去播放，那么这个动画还没播放完，用户再点击添加购物车以后我们还是clone一个新的imageview去播放。
             * 这样动画就会出现好几个点而不是一个点还没播放完又缩回去。
             * 说的通俗点，就是依靠这个方法，把参照对象和起始位置穿进去，得到一个clone的对象来播放动画
             */View run_view = addViewFromAnimLayout(buyImg, start_location);

            // 计算位移
            int endX = end_location[0] - start_location[0];
            int endY = end_location[1] - start_location[1];

            //平移动画 绘制X轴 0到结束的x轴
            TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                    endX, 0, 0);
            //设置线性插值器
            translateAnimationX.setInterpolator(new LinearInterpolator());
            // 动画重复执行的次数
            translateAnimationX.setRepeatCount(0);
            //设置动画播放完以后消失，终止填充
            translateAnimationX.setFillAfter(true);

            //平移动画 绘制Y轴
            TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                    0, endY);
            translateAnimationY.setInterpolator(new AccelerateInterpolator());
            translateAnimationY.setRepeatCount(0);
            translateAnimationX.setFillAfter(true);

            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setRepeatCount(0);
            animation.setFillAfter(true);

            //将两个动画放在动画播放集合里
            // 设置false使每个子动画都使用自己的插值器
            AnimationSet set = new AnimationSet(false);
            //设置动画播放完以后消失，终止填充
            set.setFillAfter(false);
            set.addAnimation(translateAnimationY);
            set.addAnimation(translateAnimationX);
            set.setDuration(800);// 动画的执行时间
            /**
             * 动画开始播放的时候，参照对象要显示出来，如果不显示的话这个动画会看不到任何东西。
             * 因为不管用户点击几次动画，播放的imageview都是从参照对象buyImg中clone来的
             * */
            buyImg.setVisibility(View.VISIBLE);
            run_view.startAnimation(set);
            // 动画监听事件
            set.setAnimationListener(new Animation.AnimationListener() {
                // 动画的开始
                @Override
                public void onAnimationStart(Animation animation) {

                }

                //动画重复中
                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                // 动画的结束
                @Override
                public void onAnimationEnd(Animation animation) {
                    //动画播放完以后，参照对象要隐藏
                    buyImg.setVisibility(View.GONE);
                    //结束后访问数据

                    int num = MyApplication.Instance().getCartNums();
                    if (num > 0) {
                        mPayCartLinear.setVisibility(View.VISIBLE);
                        mPayCartIndicator.setText(String.valueOf(num));
                    } else {
                        mPayCartLinear.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

    }
}
