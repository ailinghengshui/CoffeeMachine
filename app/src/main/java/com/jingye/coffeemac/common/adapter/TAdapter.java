package com.jingye.coffeemac.common.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.adapter.PayCartPackageViewHolder;
import com.jingye.coffeemac.beans.CartPayItem;

public class TAdapter extends BaseAdapter implements IViewReclaimer{

	private List<?>  items;
	private Context mContext;
	private LayoutInflater mInflater;
	private Map<Integer, Class> viewHolders;
	private TViewHolder.ViewHolderEventListener listener;

	public TAdapter(Context mContext, TViewHolder.ViewHolderEventListener listener,
					Map<Integer, Class> viewHolders, List<?> items) {
		this.mContext = mContext;
		this.listener = listener;
		this.mInflater = LayoutInflater.from(this.mContext);
		this.viewHolders = viewHolders;
		this.items = items;
	}
	
	public int getCount() {
		return items.size();
	}

	public int getItemViewType(int position) {
		TListItem item = (TListItem)items.get(position);
		if(items.get(position) instanceof CartPayItem){
			if(((CartPayItem) items.get(position)).getCoffeeInfo().isPackage()){
				return 1;
			}else{
				return item.getViewType();
			}
		}
		return item.getViewType();
	}
	
	public boolean isEnabled(int position) {
		TListItem item = (TListItem)items.get(position);
		return item.isEnabled();
	}
	
	public int getViewTypeCount()
	{
		return viewHolders.size();
	}
	
	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		TListItem item = (TListItem)items.get(position);
		return item.getViewId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TViewHolder holder = null;
		int viewType = getItemViewType(position);
		TListItem item = (TListItem)getItem(position);
		item.setTag(position);
		if (convertView == null  ) {
			try{
				Class viewHolder = viewHolders.get(viewType) ;
				holder = (TViewHolder)viewHolder.newInstance();
				holder.context = mContext;
				holder.adapter = this;
			} catch (Exception e) {
				e.printStackTrace();
			} 
			convertView = holder.getView(viewType, mInflater) ;
			holder.refresh(item);
			convertView.setTag(holder);
		} else {
			holder = (TViewHolder)convertView.getTag();
			if(!holder.getClass().equals(viewHolders.get(viewType))){
				convertView.setTag(null);//这里释放内存
				holder = null;
				try{
					Class viewHolder = viewHolders.get(viewType) ;
					holder = (TViewHolder)viewHolder.newInstance();
					holder.context = mContext;
					holder.adapter = this;
				} catch (Exception e) {
					e.printStackTrace();
				} 
				convertView = holder.getView(viewType,mInflater) ;
				holder.refresh(item);
				convertView.setTag(holder);
			} else {
				holder.refresh(item);
			}
		}

		holder.setEventListener(listener);
		
		return convertView;
	}
	
	@Override
	public void reclaimView(View view) {
		if (view == null) {
			return;
		}

		// destroy drawing cache
		view.destroyDrawingCache();
		
		TViewHolder holder = (TViewHolder)view.getTag();
		holder.reclaim();
	}
	
	public List<?> getItems() {
		return items;
	}
}
