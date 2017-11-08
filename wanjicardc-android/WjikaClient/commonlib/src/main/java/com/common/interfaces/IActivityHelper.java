package com.common.interfaces;

import android.view.View;

import java.io.File;

/**
 * 
 * @author songxudong activity或fragment基类接口，为子类提供的工具方法
 */
public interface IActivityHelper {

	public void goHome();

	public void tell(String telePhoneNum);

	public void sendSms(String telePhoneNum, String msg);

	public void installApp(File file);

	public void recommandToYourFriend(String url, String shareTitle);

	public void hideKeyboard(View view);

	public void killApp();

}
