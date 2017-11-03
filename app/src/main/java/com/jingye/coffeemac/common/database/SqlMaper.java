package com.jingye.coffeemac.common.database;

public abstract class SqlMaper {

	public static final int ST_INSERT = 3;
	public static final int ST_DELETE = 4;
	public static final int ST_UPDATE = 5;
	public static final int ST_SELECT = 6;
	public static final int ST_SCRIPT = 9;
	
	private String id;
	private String script;
	private String cacheRefreshs;
	private int type;
 
	public String getCacheRefreshs() {
		return cacheRefreshs;
	}
	public void setCacheRefreshs(String cacheRefreshs) {
		this.cacheRefreshs = cacheRefreshs;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) { 
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
}
