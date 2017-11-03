package com.jingye.coffeemac.common.adapter;

public interface IAdapterHint {
	/**
	 * notify mutable state change
	 * @param mutable true mutable false immutable
	 */
	public void onMutable(boolean mutable);
	
	/**
	 * get mutable state
	 */
	public boolean isMutable();
	
	/**
	 * notify range hint
	 * 
	 * @param first
	 * @param count
	 */
	public void onHint(int first, int count);
	
	/**
	 * get range hint
	 * @return
	 */
	public int[] getHint();
	
	/**
	 * setup adapter
	 * 
	 * @param adapter
	 */
	public void setAdapter(TAdapter adapter);
}
