package com.wjika.client.utils;

import android.content.Context;

import com.common.utils.PreferencesUtils;

/**
 * Created by jacktian on 15/9/24.
 */
public class ConfigUtils {
    public static final String CONFIG_PRE_KEY_SHOW_GUIDE = "pre_key_show_guide";
    public static final String CONFIG_PRE_KEY_OLD_VERSION_CODE = "pre_key_old_version_code";
    public static final String CONFIG_PRE_KEY_IGNORE_DATE = "pre_key_ignore_date";
    public static final String CONFIG_PRE_KEY_PAY_VERIFYPWD_TIMES = "pre_key_loc_verifypwd_times";
    private static final String CONFIG_PRE_KEY_ROOT_AUTH = "pre_key_root_auth";
	private static final String CONFIG_PRE_KEY_LAUNCHER_IMG_URL = "config_pre_key_launcher_img_url";

    public static final String TIMES_SPLIT = ",";
    public static final int TIMES_SPACE = 3 * 60 * 60 * 1000;//3小时

    public static void setOldVersionCode(Context context, int versionCode){
        PreferencesUtils.putInt(context, CONFIG_PRE_KEY_OLD_VERSION_CODE, versionCode);
    }

    public static int getOldVersionCode(Context context){
        return PreferencesUtils.getInt(context, CONFIG_PRE_KEY_OLD_VERSION_CODE);
    }

    public static void setShowGuide(Context context, boolean isShow){
        PreferencesUtils.putBoolean(context, CONFIG_PRE_KEY_SHOW_GUIDE, isShow);
    }

    public static boolean isShowGuide(Context context){
        return PreferencesUtils.getBoolean(context, CONFIG_PRE_KEY_SHOW_GUIDE);
    }

    public static void setIgnoreDate(Context context, long date){
        PreferencesUtils.putLong(context, CONFIG_PRE_KEY_IGNORE_DATE, date);
    }

    public static long getIgnoreDate(Context context){
        return PreferencesUtils.getLong(context, CONFIG_PRE_KEY_IGNORE_DATE);
    }

    public static void setLocVerifyPWDTimes(Context context, String times){
        PreferencesUtils.putString(context, CONFIG_PRE_KEY_PAY_VERIFYPWD_TIMES, times);
    }

    public static String getLocVerifyPWDTimes(Context context){
        return PreferencesUtils.getString(context, CONFIG_PRE_KEY_PAY_VERIFYPWD_TIMES);
    }

    public static boolean isRootAuth(Context context) {
        return PreferencesUtils.getBoolean(context, CONFIG_PRE_KEY_ROOT_AUTH, true);
    }

    public static void setRootAuth(Context context, boolean auth){
        PreferencesUtils.putBoolean(context, CONFIG_PRE_KEY_ROOT_AUTH, auth);
    }

	public static void setLauncherImgUrl(Context context, String url) {
		PreferencesUtils.putString(context, CONFIG_PRE_KEY_LAUNCHER_IMG_URL, url);
	}

	public static String getLauncherImgUrl(Context context) {
		return PreferencesUtils.getString(context, CONFIG_PRE_KEY_LAUNCHER_IMG_URL);
	}
}
