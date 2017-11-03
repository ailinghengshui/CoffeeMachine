package com.jingye.coffeemac.helper.cache;

import java.util.Hashtable;
import java.util.Map;

public class BaseDataCacher extends Cacher {

	public static Map<String, Object> cacher = new Hashtable<String, Object>();
	
	private static BaseDataCacher baseCacher;
	
	private BaseDataCacher(){
		super();
		setCacherSize(5000);
	}
	
	public static BaseDataCacher instance(){
		if(baseCacher == null)
			baseCacher = new BaseDataCacher();
		return baseCacher;
	}

	@Override
	public void put(String key, Object value) {
		// TODO Auto-generated method stub
		cacher.put(key, value);
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return cacher.get(key);
	}

	@Override
	public void removeKey(String key) {
		// TODO Auto-generated method stub
		cacher.remove(key);
	}
	
	public static void clearCache(){
		cacher.clear();
		System.gc();
	}
	
}
