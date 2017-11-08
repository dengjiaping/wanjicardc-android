package com.wjika.client.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.StreamUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CityDBManager {

	private final static int DB_VERSION = 2;
	private static final String TABLE_CITY = CityTable.TABLE_NAME;

	/**
	 * 将Assets里面数据库拷贝到sql文件里面，同时打开数据库
	 */
	public static void saveDB(Context context) {
		int dbVersion = CityUtils.getCityDBVersion(context);
		if (dbVersion != DB_VERSION) {
			try {
				AssetManager assetManager = context.getAssets();
				InputStream is = assetManager.open(CityDBHelper.DATABASE_NAME);
				StreamUtil.saveStreamToFile(is, context.getDir(CityDBHelper.DB_DIR_NAME, 0).getAbsolutePath() + File.separator + CityDBHelper.DATABASE_NAME);

				CityUtils.setCityDBVersion(context, DB_VERSION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CityDBHelper dataHelper = new CityDBHelper(context);
		dataHelper.openOrCreateDatabase(context);
	}

	public static List<CityEntity> getAllProvince(Context context) {
		List<CityEntity> provinceList = new ArrayList<>();
		String selection = CityTable.COLUMN_CITY_LEVEL + " = 1";
		Cursor cursor = null;
		try {
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					CityEntity city = new CityEntity();
					city.setId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					city.setPinYinShort(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PY_SHORT)));
					city.setIsAvailable(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_AVAILABLE)));
					if (cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_POPULAR)) == 1) {
						city.setIsPopular("true");
					} else {
						city.setIsPopular("false");
					}

					city.setParentId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID)));
					city.setLevel(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_LEVEL)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					provinceList.add(city);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return provinceList;
	}

	public static List<CityEntity> getCityByProvinceId(Context context, String provinceId) {
		List<CityEntity> cityEntityList = new ArrayList<>();
		Cursor cursor = null;
		try {
			String selection = CityTable.COLUMN_CITY_PARENT_ID + " = " + provinceId;
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					CityEntity city = new CityEntity();
					city.setId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					city.setPinYinShort(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PY_SHORT)));
					city.setIsAvailable(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_AVAILABLE)));
					if (cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_POPULAR)) == 1) {
						city.setIsPopular("true");
					} else {
						city.setIsPopular("false");
					}

					city.setParentId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID)));
					city.setLevel(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_LEVEL)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					cityEntityList.add(city);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return cityEntityList;
	}

	public static List<CityEntity> getAvailableCity(Context context) {
		List<CityEntity> cityList = new ArrayList<>();
		Cursor cursor = null;
		try {
			String selection = CityTable.COLUMN_CITY_AVAILABLE + " = 1" + " and " + CityTable.COLUMN_CITY_LEVEL + " =2";
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					CityEntity city = new CityEntity();
					city.setId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					city.setPinYinShort(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PY_SHORT)));
					city.setIsAvailable(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_AVAILABLE)));
					if (cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_POPULAR)) == 1) {
						city.setIsPopular("true");
					} else {
						city.setIsPopular("false");
					}
					city.setParentId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID)));
					city.setLevel(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_LEVEL)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					cityList.add(city);
				}
				while (cursor.moveToNext());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return cityList;
	}

	public static List<CityEntity> getCurrentCityDistrict(Context context, String parentId) {
		List<CityEntity> cityList = new ArrayList<>();
		Cursor cursor = null;
		try {
			String selection = CityTable.COLUMN_CITY_PARENT_ID + " = " + parentId;
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					CityEntity city = new CityEntity();
					city.setId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					city.setPinYinShort(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PY_SHORT)));
					city.setIsAvailable(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_AVAILABLE)));
					if (cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_POPULAR)) == 1) {
						city.setIsPopular("true");
					} else {
						city.setIsPopular("false");
					}

					city.setParentId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID)));
					city.setLevel(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_LEVEL)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					cityList.add(city);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return cityList;
	}

	public static List<CityEntity> getCurrentCityStreet(Context context, String parentId) {
		List<CityEntity> cityList = new ArrayList<>();
		Cursor cursor = null;
		try {
			String selection = CityTable.COLUMN_CITY_PARENT_ID + " = " + parentId;
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					CityEntity city = new CityEntity();
					city.setId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					city.setPinYinShort(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PY_SHORT)));
					city.setIsAvailable(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_AVAILABLE)));
					if (cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_POPULAR)) == 1) {
						city.setIsPopular("true");
					} else {
						city.setIsPopular("false");
					}

					city.setParentId(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID)));
					city.setLevel(cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_LEVEL)));
					city.setName(cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME)));
					cityList.add(city);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return cityList;
	}

	public static boolean updateCitys(Context context, List<CityEntity> cityEntityList) {
		if (cityEntityList != null && cityEntityList.size() > 0) {
//			ContentValues[] contentValueList = new ContentValues[cityEntityList.size()];
			for (int i = 0; i < cityEntityList.size(); i++) {
				CityEntity cityEntity = cityEntityList.get(i);
				ContentValues contentValues = new ContentValues();
				contentValues.put(CityTable.COLUMN_CITY_ID, cityEntity.getId());
				contentValues.put(CityTable.COLUMN_CITY_NAME, cityEntity.getName());
				contentValues.put(CityTable.COLUMN_CITY_AVAILABLE, (cityEntity.isAvailable() ? 1 : 0));
				contentValues.put(CityTable.COLUMN_CITY_POPULAR, (cityEntity.isPopular() ? 1 : 0));
				contentValues.put(CityTable.COLUMN_CITY_PY_SHORT, cityEntity.getPinYinShort());
				contentValues.put(CityTable.COLUMN_CITY_PARENT_ID, cityEntity.getParentId());
				contentValues.put(CityTable.COLUMN_CITY_LEVEL, cityEntity.getLevel());
//                contentValues.put(CityTable.COLUMN_CITY_HOT_ORDER, cityEntity.ge());
//				contentValueList[i] = contentValues;
				SQLiteDatabase sqLiteDatabase = CityDBHelper.getDatabase(context);
				String[] args = {cityEntity.getId()};
				switch (cityEntity.getOperationType()) {
					case INSERT:
						sqLiteDatabase.insert(TABLE_CITY, null, contentValues);
						break;
					case UPDATE:
						sqLiteDatabase.update(TABLE_CITY, contentValues, CityTable.COLUMN_CITY_ID + "=?", args);
						break;
					case DELETE:
						sqLiteDatabase.delete(TABLE_CITY, CityTable.COLUMN_CITY_ID + "=?", args);
						break;
				}
			}
		}
		return false;
	}

	public static String getAvailableCityId(Context context, String name) {
		String id = null;
		String selection = CityTable.COLUMN_CITY_NAME + " like " + "'" + name + "%" + "' and " + CityTable.COLUMN_CITY_AVAILABLE + " = 1";
		Cursor cursor = null;
		try {
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID));
				LocationUtils.setLocationCityID(context, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return id;
	}

	public static String getLocationCityID(Context context, String name) {
		String id = null;
		String selection = CityTable.COLUMN_CITY_NAME + " like " + "'" + name + "%" + "'";
		Cursor cursor = null;
		try {
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID));
				LocationUtils.setLocationCityID(context, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return id;
	}

	public static String getNameById(Context context, String id) {
		String address = "";
		String selection = CityTable.COLUMN_CITY_ID + " = " + id;
		Cursor cursor = null;
		try {
			cursor = CityDBHelper.getDatabase(context).query(TABLE_CITY, null, selection, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String cityAddress = cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_NAME));
				String parrentId = cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_CITY_PARENT_ID));
				String provinceName = getNameById(context, parrentId);
				address = provinceName + cityAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return address;
	}
}

