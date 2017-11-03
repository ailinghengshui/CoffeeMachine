package com.jingye.coffeemac.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.log.LogUtil;

public class PageList extends LinearLayout {

	private PageListAdapter simpleAdapter;

	private onItemClickListener itemClick;
	private List<HashMap<String, Object>> app = new ArrayList<HashMap<String, Object>>();

	public PageList(Context context) {
		this(context, null);
	}

	public PageList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void changeImageState(View v, List<HashMap<String, Object>> list) {
		try{
			int size = list.size();
			for (int i = 0; i < size; i++) {
				View view = (View) list.get(i).get("click");
				view.setBackgroundResource(R.drawable.top_num_normal);
				list.remove(i);
			}
			v.setBackgroundResource(R.drawable.top_num_focus);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("click", v);
			list.add(map);
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.e("vendor", "something wrong with changeImageState");
		}
	}

	public void setCheckPage(int position) {
		View view = this.getChildAt(position);
		changeImageState(view, app);
	}

	private void bindLinearLayout() {
		removeAllViews();
		app.clear();

		final int count = simpleAdapter.getCount();
		for (int i = 0; i < count; i++) {
			final View view = simpleAdapter.getView(i, null, null);
			final int index = i;
			if (i == 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("click", view);
				app.add(map);
				view.setBackgroundResource(R.drawable.top_num_focus);
			}
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClick != null) {
						itemClick.onItemClick(index, view);
						changeImageState(view, app);
					}
				}
			});

			LayoutParams parms = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			parms.leftMargin = 20;
			addView(view, parms);
		}
	}

	public void setSimpleAdapter(PageListAdapter adapter) {
		this.simpleAdapter = adapter;
		bindLinearLayout();
	}

	public interface onItemClickListener {
		public void onItemClick(int position, View fristView);
	}

	public void setOnItemClick(onItemClickListener click) {
		this.itemClick = click;
	}

}
