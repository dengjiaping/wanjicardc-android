package com.wjika.client.djpay.utils;

import android.content.Context;

import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtil;

/**
 * Created by liuzhichao on 2016/11/24.
 */

public final class DJUserCenter {

	private static final String DJ_USER_TOKEN = "dj_user_token";
	private static final String DJ_USER_HAVE_CARD = "dj_user_have_card";
	private static final String DJ_USER_PHONE = "dj_user_phone";
	private static final String DJ_USER_DJURL = "user_pre_djurl";

	public static boolean isLogin(Context context) {
		String token = PreferencesUtils.getString(context, DJ_USER_TOKEN);
		if (StringUtil.isEmpty(token)) {
			return false;
		} else {
			return true;
		}
	}

	public static void setToken(Context context, String token) {
		PreferencesUtils.putString(context, DJ_USER_TOKEN, token);
	}

	public static String getToken(Context context) {
		return PreferencesUtils.getString(context, DJ_USER_TOKEN);
	}

	public static void setBankCard(Context context, boolean haveBankCard) {
		PreferencesUtils.putBoolean(context, DJ_USER_HAVE_CARD, haveBankCard);
	}

	public static boolean haveBankCard(Context context) {
		return PreferencesUtils.getBoolean(context, DJ_USER_HAVE_CARD);
	}

	public static void setPhone(Context context, String phone) {
		PreferencesUtils.putString(context, DJ_USER_PHONE, phone);
	}

	public static String getPhone(Context context) {
		return PreferencesUtils.getString(context, DJ_USER_PHONE);
	}

	public static void setDJUrl(Context context, String url) {
		PreferencesUtils.putString(context, DJ_USER_DJURL, url);
	}

	public static String getDJUrl(Context context) {
		return PreferencesUtils.getString(context, DJ_USER_DJURL);
	}

	public static void cleanLoginInfo(Context context) {
		setToken(context,"");
	}
}
