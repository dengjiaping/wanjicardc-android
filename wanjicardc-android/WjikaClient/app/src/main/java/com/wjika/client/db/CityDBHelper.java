package com.wjika.client.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class CityDBHelper extends SQLiteOpenHelper {

	public static final String DB_DIR_NAME = "sql";
	public static final String DATABASE_NAME = "wjika_city.db";
	private static final int DATABASE_VERSION = 2;
	private static SQLiteDatabase database;
	private static CityDBHelper instance = null;

	CityDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized static CityDBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new CityDBHelper(context);
		}
		return instance;
	}

	/**
	 * 打开或创建数据库
	 */
	void openOrCreateDatabase(Context context) {
		if (database != null) {
			database.close();
		}
		String path = context.getDir(DB_DIR_NAME, Context.MODE_PRIVATE).getAbsolutePath() + File.separator + DATABASE_NAME;
		database = SQLiteDatabase.openOrCreateDatabase(path, null);
	}

	/**
	 * 获得SQLiteDatabase对象
	 */
	static synchronized SQLiteDatabase getDatabase(Context context) {
		if (database == null) {
			String path = context.getDir(DB_DIR_NAME, Context.MODE_PRIVATE).getAbsolutePath() + File.separator + DATABASE_NAME;
			database = SQLiteDatabase.openOrCreateDatabase(path, null);
		}
		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CityTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		CityTable.onUpgrade(db, oldVersion, newVersion);
	}
}