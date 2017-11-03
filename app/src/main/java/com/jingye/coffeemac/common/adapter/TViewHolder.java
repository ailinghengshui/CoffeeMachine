package com.jingye.coffeemac.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class TViewHolder {

	protected View view;
 
	public Context context;
	
	public BaseAdapter adapter;
	protected ViewHolderListener listener;
	
	public void setListener(ViewHolderListener listener)
	{
		this.listener = listener;
	}
	
	public TViewHolder() {
		
	}
	
	protected abstract int getResId();
	 
	public View getView(int viewType,LayoutInflater mInflater) {
		int resId = getResId();
		view = mInflater.inflate(resId, null);
		inflate();
		return view;
	}
	
	public void reclaim() {
		
	}
	
	public abstract void inflate();
	
	public abstract void refresh(TListItem item);

	public interface ViewHolderEventListener {
		boolean onViewHolderLongClick(View view, TListItem item);
		boolean onViewHolderClick(View view, TListItem item);
		void onItemChangeNotify();
	}

	protected ViewHolderEventListener eventListener;

	public ViewHolderEventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(ViewHolderEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	protected boolean isMutable() {
		return adapter != null && adapter instanceof IAdapterHint ? ((IAdapterHint) adapter).isMutable() : false;
	}
}
