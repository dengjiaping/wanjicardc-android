package com.wjika.client.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/8 15:11.
 * 统一关闭activity
 */
public class ExitManager {

	public static ExitManager instance = new ExitManager();
	private List<Activity> activityList = new LinkedList<>();
	private List<Activity> payActivityList = new LinkedList<>();
	private List<Activity> payPwdActivityList = new LinkedList<>();
	private List<Activity> verifyActivityList = new LinkedList<>();
	private List<Activity> changePhoneActList = new LinkedList<>();
	private List<Activity> loginActList = new LinkedList<>();
	private List<Activity> ecardActList = new LinkedList<>();
	private List<Activity> djActivityList = new LinkedList<>();

	private ExitManager() {
	}

	public void addVerifyActivity(Activity activity) {
		verifyActivityList.add(activity);
	}

	public void closeVerifyActivity() {
		for (Activity activity : verifyActivityList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		verifyActivityList.clear();
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		if (activityList != null && activityList.size() > 0) {

			for (Activity activity : activityList) {
				if (!activity.isFinishing()) {
					activity.finish();
				}
			}
			activityList.clear();
		}
	}

	/**
	 * 返回到指定的历史activity
	 * @param act Activity.class
	 */
	public void toActivity(Class<?> act) {
		if (activityList != null && activityList.size() > 0) {
			List<Activity> listAct = null;
			for (int i = activityList.size() - 1; i >= 0; i--) {
				Activity activity = activityList.get(i);
				if (activity.getClass().getSimpleName().equals(act.getSimpleName())) {
					break;
				} else if (!activity.isFinishing()) {
					activity.finish();
					if (listAct == null) {
						listAct = new ArrayList<>();
					}
					listAct.add(activity);
				}
			}
			if (listAct != null && listAct.size() > 0) {
				activityList.removeAll(listAct);
			}
		}
	}

	public void addPayActivity(Activity activity) {
		payActivityList.add(activity);
	}

	public void closePayActivity() {
		for (Activity activity : payActivityList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		payActivityList.clear();
	}

	public void addPayPwdActivity(Activity activity) {
		payPwdActivityList.add(activity);
	}

	public void closePayPwdActivity() {
		for (Activity activity : payPwdActivityList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		payPwdActivityList.clear();
	}

	public List<Activity> getActivities() {
		return activityList;
	}

	public void remove(Activity activity) {
		if (activityList != null && activityList.contains(activity)) {
			activityList.remove(activity);
		}
	}

	public void addChangePhoneAct(Activity activity) {
		changePhoneActList.add(activity);
	}

	public void closeChangePhoneAct() {
		for (Activity activity : changePhoneActList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		changePhoneActList.clear();
	}

	public void addLoginActivity(Activity activity) {
		loginActList.add(activity);
	}

	public void closeLoginActivity() {
		for (Activity activity : loginActList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		loginActList.clear();
	}

	public void addECardActivity(Activity activity) {
		ecardActList.add(activity);
	}

	public void closeECardActivity() {
		for (Activity activity : ecardActList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		ecardActList.clear();
	}

	public void addDJActivity(Activity activity) {
		djActivityList.add(activity);
	}

	public void closeDJActivity() {
		for (Activity activity : djActivityList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		djActivityList.clear();
	}
}
