package com.jingye.coffeemac.helper.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;



import com.jingye.coffeemac.util.ToolUtil;

import android.os.Handler;

public class Cacher extends Handler{

	protected static Map<String,SoftReference<Object>> softCacher = new HashMap<String, SoftReference<Object>>();
	
	public static final int DEFAULT_CACHER_SIZE = 500;
	
	protected  int cacheSize ;

	protected Cacher() {
		cacheSize = DEFAULT_CACHER_SIZE;
	}

	public void setCacherSize(int size){
		cacheSize = size;
	}
	
	public static String getDefaultCachePath(){
		String sdDir = ToolUtil.getSDPath();
		if(null == sdDir)
			return null;
		return sdDir + "/ldcache/";
	}
	
	protected void put(String key, Object value){
		
	}
	protected  Object get(String key) {
		return null;

	}

	protected void removeKey(String key){

	}
}
