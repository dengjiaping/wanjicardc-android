package com.wjika.client.utils;

import android.content.Context;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtil;
import com.wjika.client.db.CityDBManager;

/**
 * Created by jacktian on 15/9/9.
 * 定位工具类
 */
public class LocationUtils {

	public static LocationUtils instance = null;
	private final static String LOCATION_PRE_KEY_LAT = "location_pre_lat";
	private final static String LOCATION_PRE_KEY_LON = "location_pre_lon";
	private static final String LOCATION_PRE_KEY_CITY = "location_pre_city";
	private static final String LOCATION_PRE_KEY_CITY_ID = "location_pre_city_ID";
	private static final String LOCATION_PRE_KEY_SELECT_CITY = "loc_pre_selected_city";
	private static final String LOCATION_PRE_KEY_SELECT_CITY_ID = "loc_pre_selected_city_ID";
	private static final String LOCATION_PRE_KEY_DESC = "location_pre_desc";

	private LocationUtils() {
	}

	public static synchronized LocationUtils getInstance() {
		if (instance == null) {
			new LocationUtils();
		}
		return instance;
	}

	/**
	 * @param context Activity实例
	 *                需要回调更新页面操作的Activity需要实现BDLocationListener
	 */
	public static void startLocation(final Context context) {
		final LocationClient locationClient = new LocationClient(context.getApplicationContext());     //声明LocationClient类
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(false);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		locationClient.setLocOption(option);

		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location != null) {
					locationClient.stop();
				}
				setLocation(context, location);

				if (context instanceof BDLocationListener) {
					((BDLocationListener) context).onReceiveLocation(location);
				}

				//Receive Location
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                sb.append(location.getTime());
//                sb.append("\nerror code : ");
//                sb.append(location.getLocType());
//                sb.append("\nlatitude : ");
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");
//                sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());// 单位度
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append("\ndescribe : ");
//                sb.append("gps定位成功");
//
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//                sb.append("\ndescribe : ");
//                sb.append("网络定位成功");
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                sb.append("\ndescribe : ");
//                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                sb.append("\ndescribe : ");
//                sb.append("网络不同导致定位失败，请检查网络是否通畅");
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                sb.append("\ndescribe : ");
//                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//            }
//                sb.append("\nlocationdescribe : ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                List<Poi> list = location.getPoiList();// POI数据
//                if (list != null) {
//                    sb.append("\npoilist size = : ");
//                    sb.append(list.size());
//                    for (Poi p : list) {
//                        sb.append("\npoi= : ");
//                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                    }
//                }
//                sb.append("\ngetAddress : ");
//                sb.append(location.getAddress().province + "-" + location.getAddress().city + "-" + location.getAddress().district + "-" + location.getAddress().street);
//                sb.append("\ngetAddrStr : ");
//                sb.append(location.getAddrStr());
			}
		});    //注册监听函数
		locationClient.start();
	}

	public static void setLocation(Context context, BDLocation location) {

		setLocationLatitude(context, location.getLatitude());
		setLocationLongitude(context, location.getLongitude());
		setLocationDescribe(context, location.getLocationDescribe());
		String locationCity = location.getAddress().city;

		String id = CityDBManager.getAvailableCityId(context, locationCity);
		if (StringUtil.isEmpty(id)) {
			String cityName = "北京市";
			String defaultId = CityDBManager.getLocationCityID(context, cityName);
			LocationUtils.setLocationCity(context, cityName);
			LocationUtils.setLocationCityID(context, defaultId);
		} else {
			LocationUtils.setLocationCity(context, locationCity);
			LocationUtils.setLocationCityID(context, id);
		}
	}

	public static void setLocationCity(Context context, String city) {
		if (StringUtil.isEmpty(city)) {
			city = "北京市";
		}
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_CITY, city);
	}

	public static void setLocationCityID(Context context, String cityid) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_CITY_ID, cityid);
	}

	public static void setLocationDescribe(Context context, String locationDescribe) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_DESC, locationDescribe);
	}

	public static void setLocationLatitude(Context context, double latitude) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_LAT, latitude + "");
	}

	public static void setLocationLongitude(Context context, double longtitude) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_LON, longtitude + "");
	}

	public static String getLocationLatitude(Context context) {
		return PreferencesUtils.getString(context, LOCATION_PRE_KEY_LAT, Double.toString(Double.MIN_VALUE));
	}

	public static String getLocationLongitude(Context context) {
		return PreferencesUtils.getString(context, LOCATION_PRE_KEY_LON, Double.toString(Double.MIN_VALUE));
	}

	public static String getLocationDescribe(Context context) {
		return PreferencesUtils.getString(context, LOCATION_PRE_KEY_DESC, "");
	}

	public static String getLocationCity(Context context) {
		return PreferencesUtils.getString(context, LOCATION_PRE_KEY_CITY, "北京市");
	}

	public static String getLocationCityId(Context context) {
		String cityId = PreferencesUtils.getString(context, LOCATION_PRE_KEY_CITY_ID, "1");
		if (StringUtil.isEmpty(cityId)) {
			cityId = CityDBManager.getLocationCityID(context, getLocationCity(context));
		}
		return cityId;
	}

	public static void setSelectedCity(Context context, String city) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_SELECT_CITY, city);
	}

	public static void setSelectedCityID(Context context, String cityid) {
		PreferencesUtils.putString(context, LOCATION_PRE_KEY_SELECT_CITY_ID, cityid);
	}

	public static String getSelectedCity(Context context) {
		return PreferencesUtils.getString(context, LOCATION_PRE_KEY_SELECT_CITY, "");
	}

	public static String getSelectedCityId(Context context) {
		String cityId = PreferencesUtils.getString(context, LOCATION_PRE_KEY_SELECT_CITY_ID, "");
		return cityId;
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 *
	 * @param context
	 * @return true 表示开启
	 */
	public static boolean isGPSOpen(Context context) {
		LocationManager locationManager
				= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}
		return false;
	}
}
