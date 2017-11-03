package com.jingye.coffeemac.common.database;

import java.util.List;

/**
 * 数据集接口
 * 
 */
public interface IDataset {
	
	public final static String splitSQL = "@;@";

	public void rebuildData();
	
	public Object queryForObject(String id, Object parameter) throws TException;

	public Object queryForObject(String id) throws TException;

	public List queryForList(String id, Object parameter) throws TException;

	public List queryForList(String id, Object parameter, int limit)
			throws TException;

	public List queryForList(String id) throws TException;

	public int delete(String id, Object parameter) throws TException;

	public int delete(String id) throws TException;

	public int update(String id, Object parameter) throws TException;

	public int update(String id) throws TException;

	public long getAutoIncSeq(String tablename);
	
	public int insert(String id, Object parameter) throws TException;

	public int insert(String id) throws TException;

	public void scriptRunner(String id) throws TException;

	public void exeSQL(String sql);

}
