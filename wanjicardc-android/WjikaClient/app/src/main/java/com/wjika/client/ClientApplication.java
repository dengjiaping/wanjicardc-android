package com.wjika.client;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.crashreport.CrashReport;
import com.wjika.client.init.InitializeService;
import com.wjika.client.utils.PackerNg;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by jacktian on 15/8/19.
 * application
 */
public class ClientApplication extends Application {

	public static final String CHANNEL_KEY = "UMENG_CHANNEL";
	private static final String ENV_NAME = "env_name";
	public static boolean test = false;

	/**
	 * patch manager
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		InitializeService.start(this);
		//图片加载初始化
		Fresco.initialize(this);
		//需要在应用主进程中初始化的操作
		if (isMainProcess()) {
		}
	}

	public static void initBugly(Context context) {
		CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(context);
		userStrategy.setAppChannel(getChannel(context));
		userStrategy.setAppVersion(getAppVersion(context));
		CrashReport.initCrashReport(context, "900007541", false, userStrategy);
		CrashReport.setUserId(DeviceUtil.getDeviceId(context));
	}

	public boolean isMainProcess() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (ActivityManager.RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取渠道信息，兼容两种打包方式
	 */
	public static String getChannel(Context context) {
		String channel = PackerNg.getMarket(context);
		if (TextUtils.isEmpty(channel)) {
			channel = getAppMetaData(context, CHANNEL_KEY);
		}
		return channel;
	}

	/**
	 * 获取application中指定的meta-data
	 *
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		String resultData = "test";
		if (ctx == null || StringUtil.isEmpty(key)) {
			return resultData;
		}
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key) == null ? "test" : applicationInfo.metaData.getString(key);
					}
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return resultData;
	}

	public static String getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getAppConfigs(Context context) {
		Properties properties = new Properties();
		try {
			properties.load(context.getAssets().open("config.properties"));
			String env = properties.getProperty(ENV_NAME);
			if (env != null && env.equals("test")) {
				test = true;
			} else {
				test = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
