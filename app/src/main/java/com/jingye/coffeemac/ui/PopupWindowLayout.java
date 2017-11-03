package com.jingye.coffeemac.ui;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.ScreenUtil;

public class PopupWindowLayout extends LinearLayout {

	Context mContext;
	private OnClickCallback mCallback;

	public PopupWindowLayout(Context context) {
		this(context, null);
	}

	public PopupWindowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupWindowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(HORIZONTAL);
		setBackgroundResource(R.drawable.popwindow_bg);
	}

	public void initViews(Context context, List<String> titles) {
		initViews(context, titles, true);
	}

	public void initViews(Context context, List<String> titles, boolean hasDraw) {
		this.mContext = context;
		setLayoutContent(mContext, titles, hasDraw);
	}

	public void setClickListener(OnClickCallback callback) {
		this.mCallback = callback;
	}

	private void setLayoutContent(Context context, final List<String> titles, boolean hasDraw) {
		removeAllViews();
		if (titles != null && titles.size() > 0) {
			// 不带箭头
			if (!hasDraw) {
				for (int i = 0; i < titles.size(); i++) {
					final int index = i;
					final TextView textView = new TextView(context);
					// 文本
					textView.setText(titles.get(i));
					// 颜色
					textView.setTextColor(getContext().getResources().getColor(R.color.white));
					float titleSize = getContext().getResources().getDimension(R.dimen.coffee_info_payment_popwindow);
					// 字体
					textView.setTextSize(ScreenUtil.px2sp(context, titleSize));
					textView.setGravity(Gravity.CENTER);
					//textView.setPadding(20, 20, 20, 20);
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
					params.gravity = Gravity.CENTER;
					textView.setLayoutParams(params);
					addView(textView);
					if (i < titles.size() - 1) {
						LayoutParams layoutParams = new LayoutParams(ScreenUtil.dip2px(7), ScreenUtil.dip2px(74));
						layoutParams.gravity = Gravity.CENTER_VERTICAL;
						View view = new View(context);
						view.setLayoutParams(layoutParams);
						view.setBackgroundResource(R.drawable.popwindow_split);
						addView(view);
					}
					textView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mCallback != null) {
								mCallback.onItemClick(PopupWindowLayout.this, titles.size(), index);
							}
						}
					});
				}
			}
		} else {
			throw new RuntimeException("标题个数小于0");
		}
	}

	public interface OnClickCallback {
		public void onItemClick(LinearLayout parentView, int size, int index);
	}

}
