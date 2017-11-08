package com.wjika.client.db;

import android.database.sqlite.SQLiteDatabase;

import com.common.utils.LogUtil;

/**
 * Created by jacktian on 15/9/1.
 * 用户表
 */
public class UserTable {

	private static final String TAG = UserTable.class.getSimpleName();

	// Database table
	static final String TABLE_NAME = "user";
	private static final String COLUMN_ID = "_id";//主键
	public static final String COLUMN_USER_ID = "user_id";//Consumeridid
	public static final String COLUMN_USER_NAME = "name";//用户名称
	public static final String COLUMN_USER_PHONE = "phone";//用户手机号
	public static final String COLUMN_USER_TOKEN = "token";//用户token
	public static final String COLUMN_USER_GENDER = "gender";//用户性别
	public static final String COLUMN_USER_ADDRESS = "address";//用户地址
	public static final String COLUMN_USER_IDNO = "id_no";//身份证号
	public static final String COLUMN_USER_BIRTH_DAY = "birthDay";//生日
	public static final String COLUMN_USER_HEAD_IMG = "head_img";//头像url
	public static final String COLUMN_USER_AUTHENTICATION = "authentication";//实名认证
	private static final String COLUMN_USER_PAY_PWD = "pay_pwd";//是否设置支付密码
	public static final String COLUMN_USER_SET_SECURITY = "set_security";//是否设置安全问题

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY , "
			+ COLUMN_USER_ID + " TEXT, "
			+ COLUMN_USER_NAME + " TEXT, "
			+ COLUMN_USER_PHONE + " TEXT, "
			+ COLUMN_USER_TOKEN + " TEXT, "
			+ COLUMN_USER_GENDER + " BOOLEAN, "
			+ COLUMN_USER_ADDRESS + " TEXT,"
			+ COLUMN_USER_IDNO + " TEXT, "
			+ COLUMN_USER_BIRTH_DAY + " TEXT, "
			+ COLUMN_USER_HEAD_IMG + " TEXT, "
			+ COLUMN_USER_AUTHENTICATION + " INTEGER,"
			+ COLUMN_USER_PAY_PWD + " BOOLEAN,"
			+ COLUMN_USER_SET_SECURITY + " BOOLEAN"
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
