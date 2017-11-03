package com.jingye.coffeemac.common.component;

 
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
 
/**
 * 仿Launcher中的WorkSapce，可以左右滑动切换屏幕的类，可以设置第一屏向右滑动监听器和最后一屏向左滑动监听器
 */
public class TWorkSpace extends ViewGroup {
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mCurScreen;
	private int mDefaultScreen = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private static final int SNAP_VELOCITY = 400;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	/*** 当前显示的控件 */
	private int mCurrentScreen = 0;
	// 记录最后一次移动的x坐标
	private float mLastMotionX;

	public TWorkSpace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	  public static final int[] Workspace = {
          0x7f01001b
      };
	  public static final int Workspace_defaultScreen = 0;
	public TWorkSpace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
		
		TypedArray a = context.obtainStyledAttributes(attrs, Workspace, defStyle, 0);
        mDefaultScreen = a.getInt(Workspace_defaultScreen, 0);
        a.recycle();
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		// LogUtil.e(TAG, "moving to screen "+mCurScreen);
		scrollTo(mCurScreen * width, 0);
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
		}
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			//边界判断,第一屏不能向右滑动，最后一屏不能向左滑动
            if ((deltaX < 0 && mCurrentScreen == 0)
                    || (deltaX > 0 && mCurrentScreen == getChildCount() - 1)) {
                return false;
            }
			scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY) {
			    if(mCurScreen > 0){
		             // Fling enough to move left
	                snapToScreen(mCurScreen - 1);
			    }else{//第一屏向右滑动监听器
			        if(mOnFirstScreenMoveRightListener!=null){
	                    mOnFirstScreenMoveRightListener.onLastScreenMoveRight();
	                }
			    }
			}else if(velocityX < -SNAP_VELOCITY){
			    if(mCurScreen < getChildCount() - 1){
		             // Fling enough to move right
	                snapToScreen(mCurScreen + 1);
			    }else{//最后一屏向左滑动监听器
			        if(mOnLastScreenMoveLeftListener!=null){
	                    mOnLastScreenMoveLeftListener.onLastScreenMoveLeft();
	                }
			    }
			}else {
                snapToDestination();
            }
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;

			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
    /**
     * 第一屏向右滑动监听器
     * @author yangwc
     *
     */
    public interface OnFirstScreenMoveRightListener{
        public void onLastScreenMoveRight();
    }
	
    private OnFirstScreenMoveRightListener mOnFirstScreenMoveRightListener;
    
    public void setOnFirstScreenMoveRightListener(OnFirstScreenMoveRightListener l){
        mOnFirstScreenMoveRightListener = l;
    }
    
	/**
	 * 最后一屏向左滑动监听器
	 * @author yangwc
	 *
	 */
	public interface OnLastScreenMoveLeftListener{
	    public void onLastScreenMoveLeft();
	}
	   
    private OnLastScreenMoveLeftListener mOnLastScreenMoveLeftListener;
    
    public void setOnLastScreenMoveLeftListener(OnLastScreenMoveLeftListener l){
        mOnLastScreenMoveLeftListener = l;
    }
}
