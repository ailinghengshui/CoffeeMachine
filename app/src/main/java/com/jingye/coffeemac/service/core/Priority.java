package com.jingye.coffeemac.service.core;

public interface Priority {
	/**
	 * priorities
	 */
	public static final int PRIORITY_LOWEST = -2;
	public static final int PRIORITY_LOWER = -1;
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_HIGHER = 1;
	public static final int PRIORITY_HIGHEST = 2;
	
	/**
	 * default
	 */
	public static final int DEFAULT_PRIORITY = PRIORITY_NORMAL;
}
