package com.jingye.coffeemac.common.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.util.log.LogUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

public class TDatabase {

	private int resRawDatabase = R.raw.vendor;
	private Context mContext = MyApplication.Instance().getApplicationContext();

	private void copyDataBase() {
		boolean isDbExist = false;
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(filename, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
			Log.e("qt", "SQLiteException !" + e.toString());
		} finally {
			if (checkDB != null) {
				isDbExist = true;
				checkDB.close();
			}
		}
		// 数据库不存在则拷贝
		if (!isDbExist) {
			Log.e("qt", " db is NOT exist");
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(filename);// 写入流
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			InputStream is = mContext.getResources().openRawResource(
					resRawDatabase);
			byte[] buffer = new byte[8192];
			int count = 0;
			try {
				while ((count = is.read(buffer)) > 0) {
					os.write(buffer, 0, count);
					os.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private SQLiteDatabase db;
	public final static String filename = "/data"
			+ Environment.getDataDirectory().getAbsolutePath()
			+ "/com.jingye.coffeemac/vendor.db";
	
	private static TDatabase database = new TDatabase();

	private TDatabase() {
		TAnalyzer.newInstance();
		db = null;
	}

	public static TDatabase getInstance() {
		return database;
	}

	public SQLiteDatabase openDatabase() {

		if (db == null) {
			copyDataBase();
			db = SQLiteDatabase.openOrCreateDatabase(filename, null);
			LogUtil.vendor("打开数据库");
			if (db != null) {
				int curVersion = getCurVersion();
				LogUtil.vendor("数据库版本:" + curVersion);
				if (curVersion < TAnalyzer.version) {
					LogUtil.vendor("需要升级数据库:当前版本:" + curVersion);
					onUpdate(getCurVersion(), TAnalyzer.version);
					applyVersion();
					LogUtil.vendor("升级数据库成功:当前版本:" + TAnalyzer.version);
				}
			} else {
				LogUtil.vendor("打开或者创建数据库失败,严重错误");
			}
		}

		return db;
	}

	protected int getCurVersion() {
		Cursor mCursor = null;
		mCursor = db.rawQuery(
				"select pvalue from appset where pkey='dbversion'", null);
		int curVersion = 1;

		while (mCursor != null && mCursor.moveToNext()) {
			
			curVersion = Integer.valueOf(mCursor.getString(0));
		}
		if (mCursor != null && (!mCursor.isClosed()))
			mCursor.close();

		return curVersion;
	}

	protected void applyVersion() {
		db.execSQL(String
				.format("insert or replace into appset (pkey,pvalue) values('dbversion','%d')",
						TAnalyzer.version));
	}

	public void onUpdate(int oldVersion, int newVersion) {
		//TODO
	}

	public void closeDatabase() {
		if (db != null && db.isOpen()) {
			db.close();
			LogUtil.vendor("关闭数据库");
		}
		db = null;
	}

	public void rebuildDatabase() {
		// delete all data
		String deleteTimetagSql = "delete from timetag";
		db.execSQL(deleteTimetagSql);

		rebuildPhoneTable();
	}
	
	private void rebuildPhoneTable() {
		String rebuildPhoneSql = "insert or replace into appset(pkey,pvalue,pReamrk)" +
				" select 'localsmstime','false',''" +
				" union select 'rosterinit','false',''" +
				" union select 'phone','',''" +
				" union select 'nickname','',''" +
				" union select 'photopath','',''";
		
		db.execSQL(rebuildPhoneSql);
	}
}
