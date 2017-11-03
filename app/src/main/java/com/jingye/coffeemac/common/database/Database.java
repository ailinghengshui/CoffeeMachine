package com.jingye.coffeemac.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class Database  	extends SQLiteOpenHelper {
	
	 
		public static final String name = "yiliao";
		public static final int version = 1;
		
		
		public Database(Context mContext) 
		{
			super(mContext,name, null, version);
  
		}
 
		public void onCreate(SQLiteDatabase db) {
			 
		 
		}

		 
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 
			
		}

	}

