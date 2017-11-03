package com.jingye.coffeemac.common.component;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.common.adapter.IAdapterHint;
import com.jingye.coffeemac.common.adapter.IViewReclaimer;

public class TListView extends ListView {

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private ImageView arrowImageView;
 
 
 
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;
	
	private IAdapterHint adapterHint;
	private IViewReclaimer viewReclaimer;
	private AnimationDrawable rocketAnimation; 
	public TListView(Context context) {
		super(context);
		// 调用下面初始化的函数
		init(context);
	}

	public TListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 调用下面初始化的函数
		init(context);
	}

	private void init(Context context) {

		// 获得LayoutInflater从给定的上下文。
		inflater = LayoutInflater.from(context);

		// 实例化布局XML文件转换成相应的视图对象。
		
		headView = (LinearLayout) inflater.inflate(R.layout.listview_refresh, null);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		
		rocketAnimation = (AnimationDrawable)arrowImageView.getBackground(); 
		// 设置最小宽度 和高度
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		// 实例化布局XML文件转换成相应的视图对象。
	 

		// 调用下拉刷新的方法
		measureView(headView);
		// d得到原始高度和宽度
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		// 设置填充。视图可能添加的空间要求显示滚动条,这取决于风格和知名度的滚动条
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		// 标签用来识别一个日志消息的来源。标识类或活动日志调用发生
		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		// 添加一个固定视图出现在列表的顶部
		addHeaderView(headView, null, false);
		// 设置监听事件
		setOnScrollListener(mOnScrollListener);
		setRecyclerListener(mRecyclerListener);
 

 

		// 设置状态
		state = DONE;
		// 设置不可刷新状态
		isRefreshable = false;
	}
	
	private RecyclerListener mRecyclerListener = new RecyclerListener() {

		@Override
		public void onMovedToScrapHeap(View view) {
			if (viewReclaimer != null) {
				viewReclaimer.reclaimView(view);
			}
		}
	};
	
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		// 回调方法时要调用的列表或网格已经滚动。这将完成之后调用的滚动方法
		public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
				int arg3) {
			firstItemIndex = firstVisiableItem;
			
			if (adapterHint != null) {
				adapterHint.onHint(firstVisiableItem, arg2);
			}
		}

		/*
		 * 回调方法调用而列表视图或网格视图被滚动。 如果这个视图被滚动,将调用此方法在接下来的一局画卷的呈现 *
		 */
		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			if (adapterHint != null) {
				// mutable when SCROLL_STATE_FLING
				adapterHint.onMutable(arg1 != SCROLL_STATE_IDLE);
			}
		}
	};

	// 触摸事件监听
	public boolean onTouchEvent(MotionEvent event) {
		if (refreshListener != null) {
			refreshListener.hideOthers();
		}
		
		// 判断是否可以刷新
		if (isRefreshable) {
			// 根据动作相应不同的方法
			switch (event.getAction()) {

			// 当按住屏幕向下拉屏幕的时候
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在下拉的时候记录当前位置‘");
				}
				break;

			// 当按住屏幕向上松屏幕的时候
			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {

					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			// 当按住屏幕移动时候
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					/***
					 * ， 当前的位置一直是在head，否则如果当列表超出屏幕的话， 当在上推的时候，列表会同时进行滚动
					 */
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							// 调用改变时候的方法，更新UI
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						} else {
						}
					}
					// 还没有到达显示松开刷新的时候
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							// 调用改变时候的方法，更新UI
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							// 调用改变时候的方法，更新UI
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							// 调用改变时候的方法，更新UI
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}
	
	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		// 根据当前的状态进行判断
		switch (state) {

		// 下拉时候，松开既可刷新
		case RELEASE_To_REFRESH:
			// 设置视图 VISIBLE 可见 ，GONE 不可见
		 
			arrowImageView.setVisibility(View.GONE);
			 
			tipsTextview.setVisibility(View.VISIBLE);
			 

			//tipsTextview.setText("松开即可刷新");
			tipsTextview.setText("");
			Log.v(TAG, "当前状态，松开即可刷新");
			break;

		// 开始时候，下拉刷新
		case PULL_To_REFRESH:
			// 设置视图 VISIBLE 可见 ，GONE 不可见
			arrowImageView.setVisibility(View.GONE);
			rocketAnimation.stop();
			tipsTextview.setVisibility(View.VISIBLE);
			rocketAnimation.stop();

			if (isBack) {
				isBack = false;
				// 现在开始指定的动画。
				tipsTextview.setText("");
				//tipsTextview.setText("下拉刷新");
			} else {
			//	tipsTextview.setText("下拉刷新");
				tipsTextview.setText("");
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);
			// 设置视图 VISIBLE 可见 ，GONE 不可见
			arrowImageView.setVisibility(View.VISIBLE);
			//arrowImageView.play(true);
			rocketAnimation.start();

			// 现在开始指定的动画。
	 
			//tipsTextview.setText("刷新中...");
			tipsTextview.setText("");

			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			// 设置填充。视图可能添加的空间要求显示滚动条
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			// 设置视图 VISIBLE 可见 ，GONE 不可见
			arrowImageView.setVisibility(View.GONE);
			rocketAnimation.stop();
			//arrowImageView.play(false);
		 
			//tipsTextview.setText("下拉刷新");
			tipsTextview.setText("");

			Log.v(TAG, "当前状态");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
		public void hideOthers();//隐藏其他UI组件,输入法.附件选择区等
		public void onSizeChanged();			
	}

	// 设置更新时间
	public void onRefreshComplete(int count) {
		state = DONE;
		 
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	// 下拉刷新的
	private void measureView(View child) {
		// v这组布局参数宽度和高度
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			// 创建一个新组布局参数指定的宽度(填充)和高度（包裹）。
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		// d得到childWidthSpec(高度或宽度)的子视图
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			// 创建一个测量规范基于所提供的大小和模式
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		// 找出一个视图应该多大。父供应约束信息在宽度和高度参数
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		// adapter hint
		adapterHint = adapter != null && adapter instanceof IAdapterHint ? (IAdapterHint) adapter : null;

		// view reclaimer
		viewReclaimer = adapter != null && adapter instanceof IViewReclaimer ? (IViewReclaimer) adapter : null;

		super.setAdapter(adapter);
	}
	
	
	@Override  
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
		super.onSizeChanged(w, h, oldw, oldh);  

		if (refreshListener != null) {
			refreshListener.onSizeChanged();
		}
	}  

}

