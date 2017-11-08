package com.wjika.client.launcher.controller;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.common.utils.DeviceUtil;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.utils.VersionInfoUtils;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.pay.controller.ConsumeResultActivity;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.ExitManager;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jacktian on 15/8/26.
 * 启动页、闪屏页
 */
public class LauncherActivity extends BaseActivity {

	private static final long LAUNCH_MIN_TIME = 2000L;
	private static final int MSG_LEADIN_CITY_OK = 0;
	public static final String LAUNCHER_IMG_PATH = "launcher.png";
	public static final String EXTRA_TAG = "extra_tag";
	public static final int PUSH_CONSUMES_RESULT_ACTIVITY = 1;
	public static final int PUSH_WEBVIEW_ACTIVITY = 2;
	public static final int PUSH_NORMAL_ACTIVITY = 3;
	public static final int PUSH_LOGOUT_ACTIVITY = 4;
	public static final int PUSH_MAKET_ACTIVITY = 6;
	private static final String signature = "96:A4:6E:7D:9B:9A:00:47:B9:EF:6E:85:74:3F:F6:3F";

	private int versionCode = 13;
	private long mLaunchTime;
	private int pushFrom;
	private String merchName;
	private String amount;
	private String webUrl;
	private String webTitle;
	private boolean canBack;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_LEADIN_CITY_OK:
					gotoActivity();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_act);
		//为避免异常退出引起的isNewUser数据未设置，启动应用时检查并复位数据
		if (UserCenter.isNewUser(this)) {
			UserCenter.setUserToOld(this);
		}
		if (!LogUtil.isDebug && !signature.equals(DeviceUtil.getAppMD5Signature(this))) {
			canBack = true;
			ToastUtil.longShow(this, "您的版本非官方发布版本，请选择正规商店下载");
			return;
		}

		String launcherImgPath = getFilesDir().getAbsolutePath() + File.separator  + LAUNCHER_IMG_PATH;
		if (new File(launcherImgPath).exists()) {
			findViewById(R.id.launcher_img).setBackgroundDrawable(new BitmapDrawable(getResources(), launcherImgPath));
		}

		ExitManager.instance.remove(this);//nohistory属性的activity不在ExitManager里记录
		pushFrom = getIntent().getIntExtra(EXTRA_TAG, 0);
		merchName = getIntent().getStringExtra(ConsumeResultActivity.EXTRA_ORDERNO);
		amount = getIntent().getStringExtra(ConsumeResultActivity.EXTRA_AMOUNT);
		webUrl = getIntent().getStringExtra(WebViewActivity.EXTRA_URL);
		webTitle = getIntent().getStringExtra(WebViewActivity.EXTRA_TITLE);
		mLaunchTime = SystemClock.elapsedRealtime();
		initCityInfo();
	}

	private void initCityInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (isNewVersion()) {
					CityUtils.intCityVersion(LauncherActivity.this);
				}
				CityDBManager.saveDB(LauncherActivity.this);
				mHandler.sendEmptyMessageDelayed(MSG_LEADIN_CITY_OK, 100);
			}
		}).start();
	}

	private void gotoActivity() {
		long elapsed = SystemClock.elapsedRealtime() - mLaunchTime;
		if (elapsed >= LAUNCH_MIN_TIME) {
			performGotoActivity();
			finish();
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if (LauncherActivity.this.isFinishing()) {
						return;
					}
					cancel();
					performGotoActivity();
					finish();
				}
			}, LAUNCH_MIN_TIME - elapsed);
		}
	}

	private void performGotoActivity() {
		/*if (CheckRootUtil.isDeviceRooted() && DeviceUtil.isEmulator(LauncherActivity.this)) {
			LauncherActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtil.shortShow(LauncherActivity.this, "暂不支持虚拟机登陆，请使用移动设备登陆！");
				}
			});
			finish();
		} else */
		if (isNewVersion()) {
			if (StringUtil.isEmpty(DeviceUtil.getDeviceIdData(this))) {
				DeviceUtil.setDeviceIdData(this);
			}

			ConfigUtils.setOldVersionCode(this, versionCode);
			startActivity(new Intent(LauncherActivity.this, GuideActivity.class));
		} else {
				if (ConfigUtils.isShowGuide(this)) {
					startActivity(new Intent(LauncherActivity.this, GuideActivity.class));
				} else {
					switch (pushFrom) {
						case PUSH_CONSUMES_RESULT_ACTIVITY: {
							Intent intent = new Intent(LauncherActivity.this, ConsumeResultActivity.class);
							intent.putExtra(ConsumeResultActivity.EXTRA_FROM, ConsumeResultActivity.FROM_PUSH);
							intent.putExtra(ConsumeResultActivity.EXTRA_ORDERNO, merchName);
							intent.putExtra(ConsumeResultActivity.EXTRA_AMOUNT, amount);
							startActivity(intent);
							break;
						}
						case PUSH_WEBVIEW_ACTIVITY: {
							Intent intent = new Intent(this, WebViewActivity.class);
							intent.putExtra(WebViewActivity.EXTRA_FROM, WebViewActivity.FROM_PUSH);
							intent.putExtra(WebViewActivity.EXTRA_URL, webUrl);
							intent.putExtra(WebViewActivity.EXTRA_TITLE, webTitle);
							startActivity(intent);
							break;
						}
						case PUSH_LOGOUT_ACTIVITY: {
							Intent intentMain = new Intent(LauncherActivity.this, MainActivity.class);
							intentMain.putExtra("extra_from","logout");
							startActivity(intentMain);
							break;
						}
						case PUSH_MAKET_ACTIVITY:{
							Intent marketIntent = new Intent(this, MainActivity.class);
							marketIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_MARKET);
							startActivity(marketIntent);
							break;
						}
						default: {
							Intent intentMain = new Intent(LauncherActivity.this, MainActivity.class);
							startActivity(intentMain);
							break;
						}
					}
				}
		}
	}

	private boolean isNewVersion() {
		versionCode = VersionInfoUtils.getVersionCode(this);
		return ConfigUtils.getOldVersionCode(this) < versionCode;
	}

	@Override
	public void onBackPressed() {
		if (canBack) {
			super.onBackPressed();
		}
	}
}
