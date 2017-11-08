package com.wjika.client.db;

import android.database.sqlite.SQLiteDatabase;

import com.common.utils.LogUtil;

class CardTable {

	private static final String TAG = CardTable.class.getSimpleName();
	// Database table
	static final String TABLE_NAME = "card";
	private static final String COLUMN_ID = "_id";//主键
	static final String COLUMN_CARD_ID = "card_id";//卡id
	private static final String COLUMN_CARD_NAME = "name";//卡名称
	private static final String COLUMN_CARD_FACE_VALUE = "face_value";//卡面值
	private static final String COLUMN_CARD_SALE_PRICE = "sale_price";//卡购买价格
	private static final String COLUMN_CARD_COVER = "cover";//卡背景图片
	private static final String COLUMN_CARD_SALE_NUMBER = "sale_num";//卡购买数量
	private static final String COLUMN_CARD_STATUS = "status";//卡状态
	private static final String COLUMN_USER_ID = "user_id";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY , "
			+ COLUMN_CARD_ID + " TEXT, "
			+ COLUMN_USER_ID + " TEXT, "
			+ COLUMN_CARD_NAME + " TEXT, "
			+ COLUMN_CARD_FACE_VALUE + " FLOAT, "
			+ COLUMN_CARD_SALE_PRICE + " FLOAT, "
			+ COLUMN_CARD_COVER + " TEXt, "
			+ COLUMN_CARD_SALE_NUMBER + " INTEGER, "
			+ COLUMN_CARD_STATUS + " INTEGER"
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
