package com.jingye.coffeemac.helper.cache;

import java.util.HashSet;
import java.util.Set;

import com.jingye.coffeemac.util.log.LogUtil;

public class UserInfoCache {
	/** use a hash set to maintain user ID */
	private Set<String> uids = new HashSet<String>();
	
	private static UserInfoCache instance;
	
	public static synchronized UserInfoCache getInstance() {
		if (instance == null) {
			instance = new UserInfoCache();
		}
		
		return instance;
	}
	
	synchronized public boolean hasUserId(String uid) {
		return uids.contains(uid); 
	}
	
	synchronized public void updateUserIds(Set<String> uids) {	
		if (uids == null) {
			return;
		}
		
		LogUtil.vendor("UserInfoCache update: " + uids);
		this.uids.addAll(uids);
		LogUtil.vendor("UserInfoCache now:" + this.uids);
	}
}
