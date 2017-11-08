package com.wjika.client.db;

import android.database.sqlite.SQLiteDatabase;

import com.common.utils.LogUtil;

/**
 * Created by jacktian on 15/9/12.
 * 城市表
 */
class CityTable {

	private static final String TAG = CardTable.class.getSimpleName();

	// Database table
	static final String TABLE_NAME = "city";
	private static final String COLUMN_ID = "_id";//主键
	static final String COLUMN_CITY_ID = "city_id";
	static final String COLUMN_CITY_NAME = "name";
	static final String COLUMN_CITY_PY_SHORT = "py_short";
	static final String COLUMN_CITY_AVAILABLE = "available";
	static final String COLUMN_CITY_POPULAR = "popular";
	static final String COLUMN_CITY_PARENT_ID = "parent_id";
	static final String COLUMN_CITY_LEVEL = "level";
	private static final String COLUMN_CITY_HOT_ORDER = "hot_order";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY , "
			+ COLUMN_CITY_ID + " TEXT, "
			+ COLUMN_CITY_HOT_ORDER + " TEXT, "
			+ COLUMN_CITY_NAME + " TEXT, "
			+ COLUMN_CITY_PY_SHORT + " TEXT, "
			+ COLUMN_CITY_AVAILABLE + " INTEGER, "
			+ COLUMN_CITY_POPULAR + " INTEGER, "
			+ COLUMN_CITY_PARENT_ID + " TEXT, "
			+ COLUMN_CITY_LEVEL + " INTEGER"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		LogUtil.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
