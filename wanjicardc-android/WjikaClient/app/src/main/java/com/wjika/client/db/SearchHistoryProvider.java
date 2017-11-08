package com.wjika.client.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class SearchHistoryProvider extends ContentProvider {

	//database
	private DBHelper database;

	// used for the UriMacher
	private static final int MATCHS = 10;
	private static final int MATCH_ID = 20;

	public static final String AUTHORITY = "com.wjika.client.SearchHistoryprovider";

	public static final String BASE_PATH = "history";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_ID_URI = "content://" + AUTHORITY + "/" + BASE_PATH + "/";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/historys";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/history";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, MATCHS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MATCH_ID);
	}

	public SearchHistoryProvider() {
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted;
		switch (uriType) {
			case MATCHS:
				rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, selection, selectionArgs);
				break;
			case MATCH_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, UserTable.COLUMN_USER_ID + "=" + id, null);
				} else {
					rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, UserTable.COLUMN_USER_ID + "=" + id + " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if (getContext() != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public String getType(@NonNull Uri uri) {
		switch (sURIMatcher.match(uri)) {
			case MATCHS:
				return CONTENT_TYPE;
			case MATCH_ID:
				return CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id;
		switch (uriType) {
			case MATCHS:
				id = sqlDB.insert(UserTable.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if (id != -1 && getContext() != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
		return super.bulkInsert(uri, values);
	}

	@Override
	public boolean onCreate() {
		database = new DBHelper(this.getContext());
		return false;
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(UserTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case MATCHS:
				break;
			case MATCH_ID:
				// adding the ID to the original query
				queryBuilder.appendWhere(UserTable.COLUMN_USER_ID + "="
						+ uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		// make sure that potential listeners are getting notified
		if (getContext() != null) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated;
		switch (uriType) {
			case MATCHS:
				rowsUpdated = sqlDB.update(UserTable.TABLE_NAME,
						values,
						selection,
						selectionArgs);
				break;
			case MATCH_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(UserTable.TABLE_NAME,
							values,
							UserTable.COLUMN_USER_ID + "=" + id,
							null);
				} else {
					rowsUpdated = sqlDB.update(UserTable.TABLE_NAME,
							values,
							UserTable.COLUMN_USER_ID + "=" + id + " and " + selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		//getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = {UserTable.COLUMN_USER_ID};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
