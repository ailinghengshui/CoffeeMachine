package com.jingye.coffeemac.common.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class Dataset implements IDataset {

	private SQLiteDatabase db;

	public Dataset() {
	}
	
	public void open() {
		db = TDatabase.getInstance().openDatabase();
	}

	public void rebuildData() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public void close() {
//		if (db != null) {
//			db.close();
//			db = null;
//		}
	}

	public static Object exeMethod(Object AObject, String AMethodName,
			Object[] AParameters) {
		try {
			Class[] AClasses = new Class[AParameters.length];
			for (int i = 0; i < AClasses.length; i++) {
				AClasses[i] = AParameters[i].getClass();
			}
			java.lang.reflect.Method m = AObject.getClass().getMethod(
					AMethodName, AClasses);
			Object rObject = m.invoke(AObject, AParameters);
			return rObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object exeStaticMethod(Class AClasses, String AMethodName,
			Object[] AParameters) {
		try {
			java.lang.reflect.Method m = null;
			java.lang.reflect.Method[] ms = AClasses.getMethods();
			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getName().equals(AMethodName)) {
					m = ms[i];
					break;
				}
			}
			if (m == null)
				return null;
			Object rObject = m.invoke(AClasses.newInstance(), AParameters);
			return rObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, List<String>> clzMethodMap = new HashMap<String, List<String>>();

	public static List<String> reflectMethods(Class AClass) {
		if (clzMethodMap.containsKey(AClass.getName()))
			return clzMethodMap.get(AClass.getName());
		List<String> methods = new ArrayList<String>();
		try {

			java.lang.reflect.Method[] ms = AClass.getMethods();
			for (int i = 0; i < ms.length; i++) {
				java.lang.reflect.Method m = ms[i];

				String sName = m.getName();
				if (sName.length() <= 3)
					continue;
				if (!sName.subSequence(0, 3).equals("get"))
					continue;
				if (sName.equals("getClass"))
					continue;
				methods.add(sName.substring(3));

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		clzMethodMap.put(AClass.getName(), methods);
		return methods;
	}

	public String reflect(final String script, Object parameter) {
		if (parameter == null)
			return script;
		String _script = new String(script);
		if (parameter instanceof String || parameter instanceof Integer
				|| parameter instanceof Double || parameter instanceof Long) {

			String value = String.valueOf(parameter);
			value = sqliteEscape(value);
			_script = _script.replaceAll("#value#", "'" + value + "'");
			_script = _script.replaceAll("\\$value\\$", value);
			return _script;
		}
		List<String> gets = reflectMethods(parameter.getClass());
		for (int i = 0; i < gets.size(); i++) {
			try {
				String name = gets.get(i);
				java.lang.reflect.Method m = parameter.getClass().getMethod(
						"get" + name);
				Object valueObject = m.invoke(parameter);
				String value = "";
				if (valueObject != null) {
					if (valueObject.getClass() == byte.class
							|| valueObject.getClass() == byte[].class)
						value = new String((byte[]) valueObject);
					else
						value = String.valueOf(valueObject);
				}

				value = sqliteEscape(value);

				_script = _script.replaceAll("#" + name.toLowerCase(Locale.getDefault()) + "#",
						"'" + value + "'");
				_script = _script.replaceAll(
						"\\$" + name.toLowerCase(Locale.getDefault()) + "\\$", value);

			} catch (Exception e) {
				break;
			}
		}

		return _script;
	}
	
    public int execSQL(String sql){
    	try{
    		db.execSQL(sql);
    		return 0;
    	}catch (Exception e) {
    		e.printStackTrace();
			return -1;
		}
    }
    
	public int delete(String id, Object parameter) throws TException {
		if (db == null)
			open();
		TAnalyzer analyzer = TAnalyzer.newInstance();
		SqlMaper maper = analyzer.enter(id);
		if (maper == null)
			throw new TException(id + " not find");
		if (!(maper instanceof DeleteMaper))
			throw new TException(id + " not correct map ");

		DeleteMaper deleteMaper = (DeleteMaper) maper;
		String sql = null;
		if (null == parameter)
			sql = deleteMaper.getScript();
		sql = reflect(deleteMaper.getScript(), parameter);

		 
		execSQL(sql);
		if (deleteMaper.getCacheRefreshs() != null)
			TCacher.newInstance().store.remove4Keys(deleteMaper
					.getCacheRefreshs().split(";"));
		close();
		return 0;
	}

	public int delete(String id) throws TException {
		return delete(id, null);
	}
 
	public long getAutoIncSeq(String tablename) {
		if (db == null)
			open();
		
		Cursor mCursor = null;
		mCursor = db.rawQuery(
				"select seq from sqlite_sequence where name='" +tablename +"'", null);
		long seq = -1;

		while (mCursor != null && mCursor.moveToNext()) {
			seq = mCursor.getLong(0);
		}
		
		if (mCursor != null && (!mCursor.isClosed()))
			mCursor.close();
		
		// if the row does not exist, create it
		if (seq == -1) {
			seq = 1;
			String insertMSgSeqSql = "insert or replace into sqlite_sequence (name, seq) values('"
					+ tablename + "', '1')";
			db.execSQL(insertMSgSeqSql);
		}
		
		String updateSql = "UPDATE sqlite_sequence set seq='" + (seq + 1)
				+ "' where name='" + tablename + "'";
	 
		execSQL(updateSql);
		
		return seq;
	}
	
	public long insert(String id, Object parameter,String tablename) throws TException {
	
		if(insert(id,parameter)!=0)
			return -1;
		Cursor mCursor = null;
		mCursor = db.rawQuery(
				"select seq from sqlite_sequence where name='" +tablename +"'", null);
		long seq = -1;

		while (mCursor != null && mCursor.moveToNext()) {

			seq = mCursor.getLong(0);
		 
		}
		if (mCursor != null && (!mCursor.isClosed()))
			mCursor.close();

		return seq;
	}
	
	
	public int insert(String id, Object parameter) throws TException {

		if (db == null)
			open();
		TAnalyzer analyzer = TAnalyzer.newInstance();
		SqlMaper maper = analyzer.enter(id);
		if (maper == null)
			throw new TException(id + " not find");
		if (!(maper instanceof InsertMaper))
			throw new TException(id + " not correct maper ");

		InsertMaper insertMaper = (InsertMaper) maper;
		String sql = reflect(insertMaper.getScript(), parameter);
		execSQL(sql);

		if (insertMaper.getCacheRefreshs() != null)
			TCacher.newInstance().store.remove4Keys(insertMaper
					.getCacheRefreshs().split(";"));

		close();
		return 0;
	}

	public int insert(String id) throws TException {

		return insert(id, null);
	}

	public List queryForList(String id, Object parameter) throws TException {
		return queryForList(id, parameter, 999999);
	}

	private boolean isSimpleClass(Class myClass) {
		if (myClass == Integer.class) {
			return true;
		} else if (myClass == String.class) {
			return true;
		} else if (myClass == Double.class) {
			return true;

		} else if (myClass == Long.class) {
			return true;

		} else if (myClass == Float.class) {
			return true;
		}
		return false;
	}

	private List<Object> excuteQuerySimple(Class returnClass, Cursor mCursor,
			int[] iColumns) {
		List<Object> returnList = new ArrayList<Object>();
		if (!isSimpleClass(returnClass))
			return returnList;

		while (mCursor.moveToNext())
			for (int i = 0; i < iColumns.length; i++) {
				{
					if (returnClass == Integer.class) {
						returnList.add(Integer.valueOf(mCursor
								.getInt(iColumns[i])));
					} else if (returnClass == String.class) {

						returnList.add(String.valueOf(mCursor
								.getString(iColumns[i])));
					} else if (returnClass == Double.class) {

						returnList.add(Double.valueOf(mCursor
								.getDouble(iColumns[i])));

					} else if (returnClass == Long.class) {
						returnList.add(Long.valueOf(mCursor
								.getLong(iColumns[i])));

					} else if (returnClass == Float.class) {
						returnList.add(Float.valueOf(mCursor
								.getFloat(iColumns[i])));
					}
				}
			}
		if (mCursor != null && (!mCursor.isClosed()))
			mCursor.close();
		close();
		return returnList;
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(String id, Object parameter, int limit)
			throws TException {

		long start = new java.util.Date().getTime();
		List returnList = new ArrayList();
		TAnalyzer analyzer = TAnalyzer.newInstance();
		SqlMaper maper = analyzer.enter(id);
		if (maper == null)
			throw new TException(id + " not find");
		if (!(maper instanceof SelectMaper))
			throw new TException(id + " not correct maper ");

		SelectMaper selectMaper = (SelectMaper) maper;
		String sql = reflect(selectMaper.getScript(), parameter);
		if (selectMaper.isCached()) {
			List cacherList = TCacher.newInstance().store.get("_" + id + "_"
					+ sql);
			if (cacherList != null) {
				return cacherList;
			}
		}

		if (db == null)
			open();
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		String[] cColumns = mCursor.getColumnNames();
		int[] iColumns = new int[cColumns.length];
		for (int i = 0; i < cColumns.length; i++) {
			iColumns[i] = mCursor.getColumnIndex(cColumns[i]);
		}
		try {
			Class returnClass = Class.forName(selectMaper.getReturnClass());
			// ld对简单数据类型的处理
			returnList = excuteQuerySimple(returnClass, mCursor, iColumns);
			if (returnList.size() > 0) {
				return returnList;
			}
			Map<String, Class[]> mapClass = new HashMap<String, Class[]>();
			java.lang.reflect.Method[] ms = returnClass.getMethods();
			for (int i = 0; i < ms.length; i++) {
				java.lang.reflect.Method m = ms[i];
				Class[] parameterClasses = m.getParameterTypes();
				if (parameterClasses.length == 1
						&& m.getName().startsWith("set")) {
					mapClass.put(m.getName(), parameterClasses);

				}
			}
			int iIndex = 0;
			while (mCursor.moveToNext()) {

				if (limit <= iIndex)
					break;
				Object returnObject = returnClass.newInstance();

				for (int i = 0; i < iColumns.length; i++) {

					String propertyName = cColumns[i].toLowerCase()
							.substring(0, 1).toUpperCase()
							+ cColumns[i].toLowerCase().substring(1);
					propertyName = "set" + propertyName;
					Class[] parameterTypes = mapClass.get(propertyName);
					if (parameterTypes != null) {

						java.lang.reflect.Method mMethod = returnObject
								.getClass().getMethod(propertyName,
										parameterTypes);
						if (mMethod != null) {
							if (parameterTypes[0] == int.class) {
								mMethod.invoke(returnObject,
										mCursor.getInt(iColumns[i]));
							} else if (parameterTypes[0] == String.class) {
								mMethod.invoke(returnObject,
										mCursor.getString(iColumns[i]));
							} else if (parameterTypes[0] == double.class) {
								mMethod.invoke(returnObject,
										mCursor.getDouble(iColumns[i]));
							} else if (parameterTypes[0] == long.class) {
								mMethod.invoke(returnObject,
										mCursor.getLong(iColumns[i]));
							} else if (parameterTypes[0] == byte[].class) {
								mMethod.invoke(returnObject, mCursor.getBlob(i));
							} else if (parameterTypes[0] == byte.class) {
								mMethod.invoke(returnObject, mCursor.getBlob(i));
							}
						}
					}

				}
				returnList.add(returnObject);
				iIndex++;

			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if (mCursor != null && (!mCursor.isClosed()))
				mCursor.close();
			close();
		}
		long end = new java.util.Date().getTime();
		if (selectMaper.isCached())
			TCacher.newInstance().store.put("_" + id + "_" + sql, returnList);
		return returnList;
	}

	public List queryForList(String id) throws TException {
		return queryForList(id, null);
	}

	public Object queryForObject(String id, Object parameter) throws TException {
		List mList = queryForList(id, parameter);
		if (mList.size() > 0) {
			return mList.get(0);
		} else {
			return null;
		}

	}

	public Object queryForObject(String id) throws TException {
		return queryForObject(id, null);
	}

	public int update(String id, Object parameter) throws TException {
		if (db == null)
			open();
		TAnalyzer analyzer = TAnalyzer.newInstance();
		SqlMaper maper = analyzer.enter(id);
		if (maper == null)
			throw new TException(id + " not find");
		if (!(maper instanceof UpdateMaper))
			throw new TException(id + " not correct maper ");

		UpdateMaper updateMaper = (UpdateMaper) maper;
		String sql = reflect(updateMaper.getScript(), parameter);
		 execSQL(sql);
		if (updateMaper.getCacheRefreshs() != null)
			TCacher.newInstance().store.remove4Keys(updateMaper
					.getCacheRefreshs().split(";")); 
		close();
		return 0;
	}

	public int update(String id) throws TException {
		return update(id, null);
	}

	public static final String validating(String str) {
		for (int i = 1; i < 32; i++) {

			str = str.replace((char) i, ' ');

		}
		return str;
	}

	public static String sqliteEscape(String keyWord) {
		if (keyWord == null)
			return "";
		// keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		// keyWord = keyWord.replace("[", "/[");
		// keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		// keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}

	public void exeSQL(String sql) {
		if (db == null)
			open();
		String[] sqls = sql.split(";");
		for (int i = 0; i < sqls.length; i++) {
			db.execSQL((validating(sqls[i].replaceAll("'&amp", ""))));

		}
		close();
	}

	public void scriptRunner(String id) throws TException {
		if (db == null)
			open();
		TAnalyzer analyzer = TAnalyzer.newInstance();
		SqlMaper maper = analyzer.enter(id);
		if (maper == null)
			throw new TException(id + " not find");
		if (!(maper instanceof ScriptMaper))
			throw new TException(id + " not correct maper ");

		ScriptMaper scriptMaper = (ScriptMaper) maper;
		String regular = scriptMaper.getRegular();
		String[] sqls = scriptMaper.getScript().split(regular);
		for (int i = 0; i < sqls.length; i++) {
			db.execSQL(validating(sqls[i].replaceAll("'&amp", "")));

		}
		close();
		if (scriptMaper.getCacheRefreshs() != null)
			TCacher.newInstance().store.remove4Keys(scriptMaper
					.getCacheRefreshs().split(";"));
	}
}
