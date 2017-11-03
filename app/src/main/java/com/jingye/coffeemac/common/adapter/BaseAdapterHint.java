package com.jingye.coffeemac.common.adapter;

import android.util.Log;

public abstract class BaseAdapterHint implements IAdapterHint {
	protected TAdapter adapter;

	private boolean mutable;
	private int hintFirst;
	private int hintCount;

	@Override
	public void setAdapter(TAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onMutable(boolean mutableNow) {
		// mutable -> immutable
		boolean becomeImmutable = mutable && !mutableNow;
		
		// set
		mutable = mutableNow;
		
		// notify
		if (becomeImmutable) {
			immutableNow();
		}
	}

	@Override
	public boolean isMutable() {
		return mutable;
	}

	@Override
	public void onHint(int first, int count) {
		// same
		if (hintFirst == first && hintCount == count) {
			return;
		}

		// set
		hintFirst = first;
		hintCount = count;
		
		// don't hint when mutable
		if (!mutable) {	
			// notify
			hintNow();
		}
	}

	@Override
	public int[] getHint() {
		return new int[] {hintFirst, hintCount};
	}
	
	private void immutableNow() {
		// post invalidate to adapter
//		adapter.notifyDataSetInvalidated();
		Log.v("Vincent", "notifyDataSetChanged");
		adapter.notifyDataSetChanged();
	}
	
	protected abstract void hintNow();
}
