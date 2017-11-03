package com.jingye.coffeemac.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.adapter.HomeGridAdapter;
import com.jingye.coffeemac.service.domain.CoffeeInfo;
import com.jingye.coffeemac.util.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class GridViewExt extends LinearLayout {
	
	public List<HashMap<String, Object>> tableRowsList;
	OnItemClickListenerExt onItemClickEvent;
	onLongPressExt onLongPress;
	int checkRowID = -1;
	int checkColumnID = -1;
	int lastRowCount = -1;
	private Context mContext;
	private List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
	private HomeGridAdapter adapter;
	private int ColumnCount;

	public GridViewExt(Context context) {
		this(context, null);
	}

	public GridViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		int resouceID = -1;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridViewExt);
		int N = typedArray.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.GridViewExt_ColumnCount:
				resouceID = typedArray.getInt(R.styleable.GridViewExt_ColumnCount, 0);
				setColumnCount(resouceID);
				break;

			}
		}
		typedArray.recycle();
	}

	public int getColumnCount() {
		return ColumnCount;
	}

	public void setColumnCount(int count) {
		this.ColumnCount = count;
	}

	public void setOnItemClickListener(OnItemClickListenerExt click) {
		this.onItemClickEvent = click;
	}

	public void setOnLongPressListener(onLongPressExt longPress) {
		this.onLongPress = longPress;
	}

	void bindView() {
		removeAllViews();

		int count = adapter.getCount();
		TableCell[] cell = null;
		int j = 0;
		LinearLayout layout;
		tableRowsList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < count; i++) {
			j++;
			final int position = i;
			if (j > getColumnCount() || i == 0) {
				cell = new TableCell[getColumnCount()];
			}

			final View view = adapter.getView(i, null, null);
            View hotArea = view.findViewById(R.id.coffee_content_area);
            if(hotArea != null){
                hotArea.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unCheckPressed();
                        checkRowID = -1;
                        checkColumnID = -1;
                        if (onItemClickEvent != null ) {
                            onItemClickEvent.onItemClick((CoffeeInfo)adapter.getItem(position), view);
                        }
                    }
                });

                hotArea.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongPress != null) {
                            onLongPress.onLongPress(v);
                        }
                        return true;
                    }
                });
            }

			cell[j - 1] = new TableCell(view);
			if (j == getColumnCount()) {
				lastRowCount = j;
				j = 0;
				HashMap<String, Object> map = new HashMap<String, Object>();
				TableRow tr = new TableRow(cell);
				map.put("tableRow", tr);
				tableRowsList.add(map);
				layout = new LinearLayout(getContext());
				addLayout(layout, cell, tr.getSize(), tr);
			} else if (i >= count - 1 && j > 0) {
				lastRowCount = j;
				HashMap<String, Object> map = new HashMap<String, Object>();
				TableRow tr = new TableRow(cell);
				map.put("tableRow", tr);
				tableRowsList.add(map);
				layout = new LinearLayout(getContext());
				addLayout(layout, cell, j, tr);
			}
		}
	}

	private void addLayout(LinearLayout layout, TableCell[] cell, int size,
			TableRow tr) {

		int screenWidth = ScreenUtil.screenWidth;
		int screenHeight = ScreenUtil.screenHeight;
		int titleHeight = ScreenUtil.dip2px(75);
		int marqueeHeight = ScreenUtil.dip2px(48);
        int indicatorHeight = ScreenUtil.dip2px(148);
		int availableHeight = screenHeight - titleHeight - indicatorHeight - marqueeHeight;
		int availableWidth = screenWidth - ScreenUtil.dip2px(200);
		LinearLayout.LayoutParams params;
		if(getColumnCount()!=0) {
			 params = new LinearLayout.LayoutParams(availableWidth / getColumnCount(), availableHeight / 2);
		}else{
			 params = new LinearLayout.LayoutParams(availableWidth / 4, availableHeight / 2);
		}
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		for (int k = 0; k < size; k++) {
			View remoteView = (View) tr.getCellValue(k).getValue();
			layout.addView(remoteView, k, params);
		}

		LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		firstParams.leftMargin = 0;
		firstParams.rightMargin = 0;
		addView(layout, firstParams);
	}

    public BaseAdapter getAdapter(){
        return this.adapter;
    }

	public void setAdapter(HomeGridAdapter appsAdapter) {
		this.adapter = appsAdapter;
		this.setOrientation(LinearLayout.VERTICAL);
		bindView();
	}

	public void checkPressed(int tableRowId, int tableRowColumnId) {
		ViewGroup view = (ViewGroup) this.getChildAt(tableRowId);
		checkColumnID = tableRowColumnId;
		checkRowID = tableRowId;
		changeImageState(view.getChildAt(tableRowColumnId), obj);
	}

	private void changeImageState(View v, List<HashMap<String, Object>> list) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			View view = (View) list.get(i).get("touch");
			view.setPressed(false);
			list.remove(i);
		}
		v.setPressed(true);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("touch", v);
		list.add(map);

	}

	public void unCheckPressed() {
		if (checkColumnID != -1 && checkRowID != -1) {
			ViewGroup view = (ViewGroup) this.getChildAt(checkRowID);
			view.getChildAt(checkColumnID).setPressed(false);
		}
	}

	public interface OnItemClickListenerExt {
		public boolean onItemClick(CoffeeInfo info, View view);
	}

	public interface onLongPressExt {
		public boolean onLongPress(View view);
	}

	static public class TableCell {
		private Object value;

		public TableCell(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return value;
		}
	}

	public class TableRow {
		private TableCell[] cell;

		public TableRow(TableCell[] cell) {
			this.cell = cell;
		}

		public int getSize() {
			return cell.length;
		}

		public TableCell getCellValue(int index) {
			if (index >= getSize()) {
				return null;
			}
			return cell[index];
		}

		public int getCellCount() {
			return cell.length;
		}

		public int getLastCellCount() {
			return lastRowCount;
		}
	}
}
