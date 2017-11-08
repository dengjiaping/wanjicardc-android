package com.wjika.client.login.utils;

import android.content.Context;

import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtil;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.LocationUtils;

/**
 * Created by jacktian on 15/8/28.
 * 用户信息管理
 */
public class UserCenter {

	public static UserCenter instance = null;
	private static final String USER_PRE_KEY_TOKEN = "user_pre_token";
	private static final String USER_PRE_KEY_UID = "user_pre_uid";
	private static final String USER_PRE_KEY_ISNEW = "user_pre_isnew";
	private static final String USER_PRE_KEY_ISSETPAYPWD = "user_pre_issetpaypwd";
	private static final String USER_PRE_KEY_ISSETNOPWD = "user_pre_issetnopwd";
	private static final String USER_PRE_KEY_ISSETSECURITY = "user_pre_issetsecurity";
	private static final String USER_PRE_KEY_PHONE = "user_pre_phone";
	private static final String USER_PRE_KEY_PAYPWD = "user_pre_pay_pwd";
	private static final String USER_PRE_KEY_PAYPWD_SHA1_SALT = "user_pre_pay_pwd_salt";
	private static final String USER_PRE_KEY_SERVER_TIME = "user_pre_server_time";
	private static final String USER_PRE_KEY_INIT_REAL_TIME = "user_pre_init_real_time";
	private static final String USER_PRE_KEY_APPEAL_STATUS = "user_pre_appeal_status";
	private static final String USER_PRE_KEY_ISAUTHENCATION = "user_pre_isauthencation";
	private static final String USER_PRE_KEY_REALNAME = "user_pre_realname";
	private static final String USER_PRE_KEY_IDENTITY = "user_pre_identity";
	private static final String USER_PRE_KEY_BANKCARD = "user_pre_bankcard";
	private static final String USER_PRE_KEY_AUTHMONEY = "user_pre_authmoney";

	private UserCenter() {
	}

	public static synchronized UserCenter getInstance() {
		if (instance == null) {
			new UserCenter();
		}

		return instance;
	}

	public static boolean isLogin(Context context) {
		String token = PreferencesUtils.getString(context, USER_PRE_KEY_TOKEN);
		return !StringUtil.isEmpty(token);
	}

	public static boolean isNewUser(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_ISNEW);
	}

	public static String getToken(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_TOKEN);
	}

	public static void setToken(Context context, String token) {
		PreferencesUtils.putString(context, USER_PRE_KEY_TOKEN, token);
	}

	private static String getUid(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_UID);
	}

	public static void saveLoginInfo(Context context, UserEntity user) {
		if (user != null) {
			String uid = getUid(context);
			String userId = user.getId() + "";
			if (!userId.equals(uid)) {
				ConfigUtils.setLocVerifyPWDTimes(context, "");
			}

			PreferencesUtils.putString(context, USER_PRE_KEY_TOKEN, user.getToken());
			//登录接口返回的id就是consumerId
			PreferencesUtils.putString(context, USER_PRE_KEY_UID, user.getId() + "");
			PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISNEW, user.isNewUser());
			PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETPAYPWD, user.isSetPayPassword());
			PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETSECURITY, user.isSetSecurity());
			PreferencesUtils.putString(context, USER_PRE_KEY_PHONE, user.getPhone());
			PreferencesUtils.putInt(context, USER_PRE_KEY_APPEAL_STATUS, user.getAppealStatus());
			PreferencesUtils.putString(context, USER_PRE_KEY_REALNAME, user.getUserRealName());
			PreferencesUtils.putString(context, USER_PRE_KEY_IDENTITY, user.getIdentity());
			if (2 == user.getAuthentication()) {
				PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISAUTHENCATION, true);
			} else {
				PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISAUTHENCATION, false);
			}
			setUserPaypwdSalt(context, user.getPayPwdSalt());
		}
	}

	public static void cleanLoginInfo(Context context) {
		PreferencesUtils.putString(context, USER_PRE_KEY_TOKEN, "");
		PreferencesUtils.putString(context, USER_PRE_KEY_UID, "");
		PreferencesUtils.putString(context, USER_PRE_KEY_PHONE, "");
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISNEW, false);
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETPAYPWD, false);
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETSECURITY, false);
		PreferencesUtils.putString(context, USER_PRE_KEY_PAYPWD, "");
		PreferencesUtils.putString(context, USER_PRE_KEY_PAYPWD_SHA1_SALT, "");
		PreferencesUtils.putInt(context, USER_PRE_KEY_APPEAL_STATUS, -1);
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETSECURITY, true);
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISAUTHENCATION, false);
		LocationUtils.setSelectedCity(context, "");
		LocationUtils.setSelectedCityID(context, "");
	}

	public static void setUserToOld(Context context) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISNEW, false);
	}

	public static void setUserSetPaypwd(Context context, boolean status) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETPAYPWD, status);
	}

	public static boolean isSetPaypwd(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_ISSETPAYPWD);
	}

	public static void setUserSetNopwd(Context context, boolean status) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETNOPWD + getUid(context), status);
	}

	public static boolean issetNopwd(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_ISSETNOPWD + getUid(context), true);
	}

	public static boolean hasSetNopwdKey(Context context) {
		return PreferencesUtils.hasSharedPreferenceKey(context, USER_PRE_KEY_ISSETNOPWD + getUid(context));
	}

	public static void setUserPhone(Context context, String phone) {
		PreferencesUtils.putString(context, USER_PRE_KEY_PHONE, phone);
	}

	public static String getUserPhone(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_PHONE);
	}

	public static void setUserPaypwd(Context context, String pwd) {
		PreferencesUtils.putString(context, USER_PRE_KEY_PAYPWD, pwd);
	}

	public static String getUserPaypwd(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_PAYPWD);
	}

	public static void setUserPaypwdSalt(Context context, String salt) {
		PreferencesUtils.putString(context, USER_PRE_KEY_PAYPWD_SHA1_SALT, salt);
	}

	public static String getUserPaypwdSalt(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_PAYPWD_SHA1_SALT);
	}

	public static void setUserServerTime(Context context, long time) {
		PreferencesUtils.putLong(context, USER_PRE_KEY_SERVER_TIME, time);
	}

	public static long getUserServerTime(Context context) {
		return PreferencesUtils.getLong(context, USER_PRE_KEY_SERVER_TIME);
	}

	public static void setUserInitRealTime(Context context, long time) {
		PreferencesUtils.putLong(context, USER_PRE_KEY_INIT_REAL_TIME, time);
	}

	public static long getUserInitRealTime(Context context) {
		return PreferencesUtils.getLong(context, USER_PRE_KEY_INIT_REAL_TIME);
	}

	public static void setSecurity(Context context, boolean isSet) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISSETSECURITY, isSet);
	}

	public static boolean isSetSecurity(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_ISSETSECURITY);
	}

	public static void setAppealStatus(Context context, int status) {
		PreferencesUtils.putInt(context, USER_PRE_KEY_APPEAL_STATUS, status);
	}

	public static int getAppealStatus(Context context) {
		return PreferencesUtils.getInt(context, USER_PRE_KEY_APPEAL_STATUS);
	}

	public static boolean isAuthencaiton(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_ISAUTHENCATION);
	}

	public static void setAuthenticatiton(Context context, boolean authencaiton) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_ISAUTHENCATION, authencaiton);
	}

	public static void setUserRealName(Context context, String name) {
		PreferencesUtils.putString(context, USER_PRE_KEY_REALNAME, name);
	}

	public static String getUserRelName(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_REALNAME);
	}

	public static void setUserIdentity(Context context, String identity) {
		PreferencesUtils.putString(context, USER_PRE_KEY_IDENTITY, identity);
	}

	public static String getUserIdentity(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_IDENTITY);
	}

	public static void setAuthBankCard(Context context, String bankcard) {
		PreferencesUtils.putString(context, USER_PRE_KEY_BANKCARD, bankcard);
	}

	public static String getAuthBankCard(Context context) {
		return PreferencesUtils.getString(context, USER_PRE_KEY_BANKCARD);
	}

	public static void setAuthMoney(Context context, boolean setmoney) {
		PreferencesUtils.putBoolean(context, USER_PRE_KEY_AUTHMONEY + getUid(context), setmoney);
	}

	public static boolean getAuthMoney(Context context) {
		return PreferencesUtils.getBoolean(context, USER_PRE_KEY_AUTHMONEY + getUid(context), false);
	}

}
