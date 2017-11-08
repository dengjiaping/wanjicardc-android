package com.wjika.client.main.controller;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseTabsDrawerActivity;
import com.wjika.client.card.controller.CardFragment;
import com.wjika.client.cardpackage.controller.CardPackageActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.launcher.controller.LauncherActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.adapter.CityAdapter;
import com.wjika.client.market.controller.MarketFragment;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.PersonalFragment;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by jacktian on 16/4/26.
 * 主页
 */
public class MainActivity extends BaseTabsDrawerActivity implements BDLocationListener {

	public static final String BROADCAST_ACTION_LOACTION_OK = "com.wjk.loaction.ok";
	public static final String BROADCAST_ACTION_MENU_SHOW_STATUS = "com.wjk.menu.show.status";
	public static final String REQUEST_TO_WHICH_TAB = "request_to_which_tab";
	public static final int REQUEST_TO_MARKET = 1;//切到包子商城tab
	public static final int REQUEST_TO_CARD = 2;//切换到卡商城
	public static final int BROADCAST_LOCATION_STATUS_FAILED = 0x1;
	public static final int BROADCAST_LOCATION_STATUS_SUCCESS = 0x2;
	public static final int BROADCAST_LOCATION_STATUS_NO_ACTIVE = 0x3;
	public static final int BROADCAST_LOCATION_SELECT_CITY = 0x4;
	public static final int BROADCAST_LOCATION_START = 0x5;
	public static final int BROADCAST_LOCATION_NO_GPS = 0x6;
	public static final int REQUEST_ACT_PAYPWD_CODE = 10;
	private static final int REQUEST_GET_SERVER_TIME_CODE = 0x6;
	private static final int REQUEST_GET_CITY_LIST_CODE = 0x1;
	private static final int REQUEST_NET_IMG_LAUNCHER = 0x2;
	public static final long DIFF_DEFAULT_BACK_TIME = 2000;
	private final static int MSG_UPDATE_CITY_OK = 1;
	private final static int MSG_CITY_DATA_OK = 2;

	public static String launcherImgPath;
	private long mBackTime = -1;
	private View vToolBar;
	private String locationCity;
	private String locationCityName;
	private PopupWindow pop;
	String[] locationOldCity;
	private boolean isShowCityChangeDialog = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_UPDATE_CITY_OK: {
					new Thread(new Runnable() {
						@Override
						public void run() {
							List<CityEntity> cityEntityList = CityDBManager.getAvailableCity(MainActivity.this);
							Message msg = mHandler.obtainMessage();
							msg.obj = cityEntityList;
							msg.what = MSG_CITY_DATA_OK;
							mHandler.sendMessage(msg);
						}
					}).start();
					break;
				}
				case MSG_CITY_DATA_OK: {
					closeProgressDialog();
					List<CityEntity> cityEntityList = (List<CityEntity>) msg.obj;
					if (cityEntityList != null) {
						showMenu(vToolBar, cityEntityList);
					}
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isHasFragment = true;
		ViewInjectUtils.inject(this);
		launcherImgPath = getFilesDir().getAbsolutePath() + File.separator + LauncherActivity.LAUNCHER_IMG_PATH;
		locationOldCity = getOldLocationCity();
		checkUpdateVersion();
		LocationUtils.startLocation(this);
		syncServerTime();
		updateLauncherImg();
		setCurrentTab(getIntent().getIntExtra(REQUEST_TO_WHICH_TAB, 0));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ("logout".equals(getIntent().getStringExtra("extra_from"))) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ToastUtil.shortShow(MainActivity.this, getResources().getString(R.string.str_token_invalid));
				}
			}, 1000);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		int whichTab = intent.getIntExtra(REQUEST_TO_WHICH_TAB, 0);
		setCurrentTab(whichTab);
	}

	private void syncServerTime() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_GET_SERVER_TIME,
				REQUEST_GET_SERVER_TIME_CODE,
				FProtocol.HttpMethod.POST,
				params);
	}

	private void updateLauncherImg() {
		IdentityHashMap<String, String> param = new IdentityHashMap<>();
		param.put("size", findBestSize().size);
		param.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_POST_LAUNCHER_IMG, REQUEST_NET_IMG_LAUNCHER, FProtocol.HttpMethod.POST, param);
	}

	private ScreenSize findBestSize() {
		int width = DeviceUtil.getWidth(this);
		int height = DeviceUtil.getHeight(this);
		int sum = width + height;

		String[] hdpi = ScreenSize.HDPI.size.split("\\*");
		int sumHdpi = Integer.valueOf(hdpi[0]) + Integer.valueOf(hdpi[1]);

		String[] xdpi = ScreenSize.XDPI.size.split("\\*");
		int sumXdpi = Integer.valueOf(xdpi[0]) + Integer.valueOf(xdpi[1]);

		String[] xxdpi = ScreenSize.XXDPI.size.split("\\*");
		int sumXXdpi = Integer.valueOf(xxdpi[0]) + Integer.valueOf(xxdpi[1]);

		int smallHalfDiff = (sumXdpi - sumHdpi) / 2;
		int bigHalfDiff = (sumXXdpi - sumXdpi) / 2;

		int gap = sumXdpi - sum;
		if (gap < 0) {//大于XDPI
			if (-gap > bigHalfDiff) {
				return ScreenSize.XXDPI;
			} else {
				return ScreenSize.XDPI;
			}
		} else if (gap > 0) {//小于XDPI
			if (gap < smallHalfDiff) {
				return ScreenSize.XDPI;
			} else {
				return ScreenSize.HDPI;
			}
		} else {
			return ScreenSize.XDPI;
		}
	}

	public enum ScreenSize {
		HDPI("480*800"), XDPI("720*1280"), XXDPI("1080*1920");

		String size;

		ScreenSize(String size) {
			this.size = size;
		}
	}

	public void showCityMenu(View view) {
		vToolBar = view;
		loadData();
	}

	private void loadData() {
		showProgressDialog();
		updateCity();
	}

	private void updateCity() {
		String version = CityUtils.getCityAreaVersion(this);
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("cityVersion", version);
		requestHttpData(Constants.Urls.URL_GET_CITY_LIST, REQUEST_GET_CITY_LIST_CODE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, final String data) {
		if (this.isFinishing() || data == null) {
			return;
		}
		Entity entity = Parsers.getResponseSatus(data);
		if (RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
			switch (requestCode) {
				case REQUEST_GET_CITY_LIST_CODE: {
					showProgressDialog();
					if (!StringUtil.isEmpty(data)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								CityDBManager.updateCitys(MainActivity.this.getApplicationContext(),
										Parsers.getUpdateCityList(MainActivity.this.getApplicationContext(), data));
								mHandler.sendEmptyMessage(MSG_UPDATE_CITY_OK);
							}
						}).start();
					}
					break;
				}
				case REQUEST_GET_SERVER_TIME_CODE: {
					long serverTime = Parsers.getServerTime(data);
					long initRealTime = Calendar.getInstance().getTimeInMillis() / 1000;
					UserCenter.setUserServerTime(this, serverTime);
					UserCenter.setUserInitRealTime(this, initRealTime);
					break;
				}
				case REQUEST_NET_IMG_LAUNCHER:
					updateLauncherImg(Parsers.getLauncherImg(data));
					break;
				default:
					super.success(requestCode, data);
					break;
			}
		} else {
			mHandler.sendEmptyMessage(MSG_UPDATE_CITY_OK);
			super.success(requestCode, data);
		}
	}

	private void updateLauncherImg(final String imgUrl) {
		String preImgUrl = ConfigUtils.getLauncherImgUrl(this);
		if (!TextUtils.isEmpty(imgUrl) && (!imgUrl.equals(preImgUrl) || (imgUrl.equals(preImgUrl) && !new File(launcherImgPath).exists()))) {
			Request request = new Request.Builder().url(imgUrl).build();
			new OkHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
				}

				@Override
				public void onResponse(Response response) throws IOException {
					InputStream is = null;
					byte[] buf = new byte[2048];
					int len;
					FileOutputStream fos = null;
					try {
						is = response.body().byteStream();
						fos = new FileOutputStream(new File(launcherImgPath));
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
						}
						fos.flush();
						ConfigUtils.setLauncherImgUrl(MainActivity.this, imgUrl);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (is != null)
								is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							if (fos != null)
								fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (this.isFinishing()) {
			return;
		}
		switch (requestCode) {
			case REQUEST_GET_CITY_LIST_CODE: {
				showProgressDialog();
				mHandler.sendEmptyMessage(MSG_UPDATE_CITY_OK);
				break;
			}
		}
	}

	@Override
	protected void importantInit() {
	}

	@Override
	protected void addTabs() {
		addTab(initTabView(R.drawable.navigation_ic_menu_selector, R.string.main_tab_menu), MainFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_action_selector, R.string.main_tab_action), MarketFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_store_selector, R.string.main_tab_store), CardFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_person_selector, R.string.main_tab_personal), PersonalFragment.class, null);
	}

	private View initTabView(int tabIcon, int tabText) {
		ViewGroup tab = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
		ImageView imageView = (ImageView) tab.findViewById(R.id.navigation);
		imageView.setImageResource(tabIcon);

		TextView textView = (TextView) tab.findViewById(R.id.txt_navigation);
		textView.setText(tabText);
		return tab;
	}

	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
	}

	@Override
	public void onBackPressed() {
		long nowTime = System.currentTimeMillis();
		long diff = nowTime - mBackTime;
		if (diff >= DIFF_DEFAULT_BACK_TIME) {
			mBackTime = nowTime;
			Toast.makeText(getApplicationContext(), R.string.toast_back_again_exit, Toast.LENGTH_SHORT).show();
		} else {
			ExitManager.instance.exit();
		}
	}

	private void showMenu(View view, final List<CityEntity> cityEntityList) {
		View popView = LayoutInflater.from(this).inflate(R.layout.city_list_menu, null, false);

		pop = new PopupWindow(popView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		TextView tvLocationCity = (TextView) popView.findViewById(R.id.txt_current_city);
		tvLocationCity.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = CityDBManager.getAvailableCityId(MainActivity.this, locationCityName);
				LocationUtils.setSelectedCity(MainActivity.this, locationCityName);
				LocationUtils.setSelectedCityID(MainActivity.this, id);
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_SELECT_CITY);
				LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
				pop.dismiss();
			}
		});
		updatePopMenu(pop);

		final TextView tvLocationStatus = (TextView) popView.findViewById(R.id.tv_location_status);
		GridView mGridCitys = (GridView) popView.findViewById(R.id.grid_city);
		ScrollView sv = (ScrollView) popView.findViewById(R.id.scrollview);
		View vBlank = popView.findViewById(R.id.vBlank);
		LinearLayout llLoactionStatus = (LinearLayout) popView.findViewById(R.id.ll_location_status);

		vBlank.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});

		llLoactionStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvLocationStatus.setText("定位中...");
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_START);
				LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
				LocationUtils.startLocation(MainActivity.this);
			}
		});

		if (cityEntityList != null) {
			final CityAdapter adapter = new CityAdapter(this, cityEntityList);
			mGridCitys.setAdapter(adapter);
			mGridCitys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CityEntity cityEntity = adapter.getItem(position);
					if (MainActivity.this != null && cityEntity != null) {
						LocationUtils.setSelectedCity(MainActivity.this, cityEntity.getName());
						LocationUtils.setSelectedCityID(MainActivity.this, cityEntity.getId());
						Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
						intent.putExtra("status", BROADCAST_LOCATION_SELECT_CITY);
						LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
					}
					pop.dismiss();
				}
			});
		}
		sv.smoothScrollTo(0, 0);
		pop.showAsDropDown(view);
		sendPopMenuShowStatusBroadcast(false);
		pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				sendPopMenuShowStatusBroadcast(true);
			}
		});
	}

	private String[] getOldLocationCity() {
		String[] city = new String[2];
		if (StringUtil.isEmpty(LocationUtils.getSelectedCity(this))) {
			city[0] = LocationUtils.getLocationCity(this);
		} else {
			city[0] = LocationUtils.getSelectedCity(this);
		}

		if (StringUtil.isEmpty(LocationUtils.getSelectedCityId(this))) {
			city[1] = LocationUtils.getLocationCityId(this);
		} else {
			city[1] = LocationUtils.getSelectedCityId(this);
		}
		return city;
	}

	/**
	 * @param status true:菜单消失   false:菜单显示
	 */
	private void sendPopMenuShowStatusBroadcast(boolean status) {
		Intent intent = new Intent(BROADCAST_ACTION_MENU_SHOW_STATUS);
		intent.putExtra("menu_dismiss", status);
		LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
	}

	private void updatePopMenu(PopupWindow pop) {
		View popView = pop.getContentView();
		TextView tvLocationCity = (TextView) popView.findViewById(R.id.txt_current_city);
		TextView tvCurrentCityKey = (TextView) popView.findViewById(R.id.tv_location_city_key);
		LinearLayout llCurrentCityValue = (LinearLayout) popView.findViewById(R.id.ll_location_city_value);
		LinearLayout llLoactionStatus = (LinearLayout) popView.findViewById(R.id.ll_location_status);
		View vLine = popView.findViewById(R.id.v_Line);
		TextView tvLocationStatus = (TextView) popView.findViewById(R.id.tv_location_status);

		if (StringUtil.isEmpty(locationCity)) {
			tvLocationStatus.setText("定位失败，请点击重试");
			llLoactionStatus.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.VISIBLE);
			tvCurrentCityKey.setVisibility(View.GONE);
			llCurrentCityValue.setVisibility(View.GONE);
		} else {
			llLoactionStatus.setVisibility(View.GONE);
			vLine.setVisibility(View.GONE);
			tvCurrentCityKey.setVisibility(View.VISIBLE);
			llCurrentCityValue.setVisibility(View.VISIBLE);
			tvLocationCity.setText(locationCity);
		}
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		locationCityName = bdLocation.getAddress().city;
		locationCity = CityUtils.getCityShortName(locationCityName);
		if (StringUtil.isEmpty(locationCityName)) {
			if (LocationUtils.isGPSOpen(this)) {
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_STATUS_FAILED);
				LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
			} else {
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_NO_GPS);
				LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
			}
			if (pop != null && pop.isShowing()) {
				updatePopMenu(pop);
			}

		} else if (!StringUtil.isEmpty(CityDBManager.getAvailableCityId(this, locationCityName))) { //定位成功，并且定位城市也开通了服务
			if (!CityUtils.getCityShortName(LocationUtils.getLocationCityId(this)).equals(locationOldCity[1]) && !isShowCityChangeDialog) {
				isShowCityChangeDialog = true;
				StringBuffer sb = new StringBuffer();
				sb.append("系统定位到您在").append(locationCity).append(",").append("需要切换至").append(locationCity).append("吗?");
				showAlertDialog("", sb.toString(), "取消", "确定", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String id = CityDBManager.getAvailableCityId(MainActivity.this, locationCityName);
						LocationUtils.setSelectedCity(MainActivity.this, locationCityName);
						LocationUtils.setSelectedCityID(MainActivity.this, id);
						Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
						intent.putExtra("status", BROADCAST_LOCATION_SELECT_CITY);
						LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
						closeDialog();
					}
				});
			}
			Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
			intent.putExtra("status", BROADCAST_LOCATION_STATUS_SUCCESS);
			LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
			if (pop != null && pop.isShowing()) {
				updatePopMenu(pop);
			}
		} else {
			Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
			intent.putExtra("status", BROADCAST_LOCATION_STATUS_NO_ACTIVE);
			LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
		}
	}

	public void showLocationDialog(String title, String msg, String cancelText, String okText, View.OnClickListener onCancelListener, View.OnClickListener onOkListener) {
		showAlertDialog(title, msg, cancelText, okText, onCancelListener, onOkListener);
	}

	public void showLocationDialog(String title, String msg, String okText, View.OnClickListener onOkListener) {
		showAlertDialog(title, msg, okText, onOkListener);
	}

	public void closeLocationDialog() {
		closeDialog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode && REQUEST_ACT_PAYPWD_CODE == requestCode) {
			startActivity(new Intent(this, CardPackageActivity.class));
		}
	}
}
