package com.jingye.coffeemac.common.adapter;

import java.util.List;
import java.util.Map;
import android.content.Context;

public class TAdapterWithHint extends TAdapter implements IAdapterHint {
	IAdapterHint adapterHint;
	
	@SuppressWarnings("rawtypes")
	public TAdapterWithHint(Context context,
			TViewHolder.ViewHolderEventListener listener,
			Map<Integer, Class> holders, List items, IAdapterHint adapterHint) {
		super(context, listener, holders, items);
		
		setAdapterHint(adapterHint);
	}
	
	public void setAdapterHint(IAdapterHint adapterHint) {
		this.adapterHint = adapterHint;
		
		if (adapterHint != null) {
			adapterHint.setAdapter(this);
		}
	}

	@Override
	public void onMutable(boolean mutable) {
		if (adapterHint != null) {
			adapterHint.onMutable(mutable);
		}
	}

	@Override
	public boolean isMutable() {
		return adapterHint != null ? adapterHint.isMutable() : false;
	}

	@Override
	public void onHint(int first, int count) {
		if (adapterHint != null) { 
			adapterHint.onHint(first, count);
		}
	}

	@Override
	public int[] getHint() {
		return adapterHint != null ? adapterHint.getHint() : null;
	}

	@Override
	public void setAdapter(TAdapter adapter) {

	}
}
