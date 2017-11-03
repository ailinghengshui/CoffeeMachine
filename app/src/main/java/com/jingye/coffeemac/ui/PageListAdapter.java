package com.jingye.coffeemac.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PageListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int resource;
	private List<? extends Map<String, ?>> data;
	private String[] from;
	private int[] to;

	public PageListAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resouce, String[] from,
                           int[] to) {

		this.data = data;
		this.resource = resouce;
		this.data = data;
		this.from = from;
		this.to = to;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data == null ? 0 : data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public String get(int position, Object key) {
		Map<String, ?> map = (Map<String, ?>) getItem(position);
		return map.get(key).toString();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(resource, null);
		Map<String, ?> item = data.get(position);
		int count = to.length;
		for (int i = 0; i < count; i++) {
			View v = convertView.findViewById(to[i]);
			bindView(v, item, from[i]);
		}
		convertView.setTag(position);
		return convertView;
	}

	private void bindView(View view, Map<String, ?> item, String from) {
		Object data = item.get(from);
		if (view instanceof TextView) {
			((TextView) view).setText(data == null ? "" : data.toString());
		}
	}

}
