package com.jingye.coffeemac.common.database;

import java.util.List;

public class TCacher {

	private static TCacher cacher = null;
	private boolean active;
	
	private int size;
	public  TLRUCache<String, List> store = null;
 
	private TCacher()
	{
		size = 5;
		store = new TLRUCache<String, List>(size);
 
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	} 

	public static TCacher newInstance()
	{
		if(cacher == null)
			cacher = new TCacher();
		return cacher;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

}
