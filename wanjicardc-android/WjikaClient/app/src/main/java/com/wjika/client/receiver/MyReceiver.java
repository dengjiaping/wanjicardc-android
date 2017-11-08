package com.wjika.client.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.common.utils.DeviceUtil;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.launcher.controller.LauncherActivity;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.pay.controller.ConsumeResultActivity;
import com.wjika.client.utils.ExitManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by me on 6/5/15.
 * 极光推送消息通知接收
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
//		LogUtil.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			if (bundle != null){
				/*String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				Intent service = new Intent(context, Service.class);
				service.setAction(UserService.ACTION_UPDATE_JPUSH_REG_ID);
				service.putExtra(UserService.ARGS_ID, regId);
				context.startService(service);*/
			}
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			if (bundle != null)
				processCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			if (bundle != null){
//				LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//				LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
				processCustomMessage(context, bundle, notifactionId);
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			if (bundle != null){
				String data = bundle.getString(JPushInterface.EXTRA_EXTRA);
				if (data != null && data.contains("type")){
					try {
						JSONObject jsonObject = new JSONObject(data);
						int key = jsonObject.optInt("type");

						switch (key){
							case 0:
								break;
							case 1://消费通知
							{
								List<Activity> activities = ExitManager.instance.getActivities();
								String merchName = jsonObject.getString("mn");
								String amount = jsonObject.getString("am");
								if (activities != null && activities.size() > 0 && DeviceUtil.isAppOnForeground(context)){
									Context context1 = activities.get(activities.size() - 1);
									Intent intent1 = new Intent(context1, ConsumeResultActivity.class);
									intent1.putExtra(ConsumeResultActivity.EXTRA_ORDERNO, merchName);
									intent1.putExtra(ConsumeResultActivity.EXTRA_AMOUNT, amount);
									context1.startActivity(intent1);
								}else {
									Intent intent1 = new Intent(context, LauncherActivity.class);
									intent1.putExtra(LauncherActivity.EXTRA_TAG, LauncherActivity.PUSH_CONSUMES_RESULT_ACTIVITY);
									intent1.putExtra(ConsumeResultActivity.EXTRA_ORDERNO, merchName);
									intent1.putExtra(ConsumeResultActivity.EXTRA_AMOUNT, amount);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent1);
								}
								break;
                            }
                            case 2://普通通知
                            {
                                if (!DeviceUtil.isAppOnForeground(context)){
                                    Intent intent1 = new Intent(context, LauncherActivity.class);
                                    intent1.putExtra(LauncherActivity.EXTRA_TAG, LauncherActivity.PUSH_NORMAL_ACTIVITY);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                }
                            }
                                break;
                            case 3://登出通知
								if (!DeviceUtil.isAppOnForeground(context)){
									Intent intent1 = new Intent(context, LauncherActivity.class);
									intent1.putExtra(LauncherActivity.EXTRA_TAG, LauncherActivity.PUSH_LOGOUT_ACTIVITY);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent1);
								}
                                break;
                            case 4://活动通知
                            {
                                String url = jsonObject.getString("link");
                                String webTitle = jsonObject.getString("title");
                                if (!DeviceUtil.isAppOnForeground(context)){
                                    Intent intent1 = new Intent(context, LauncherActivity.class);
                                    intent1.putExtra(LauncherActivity.EXTRA_TAG, LauncherActivity.PUSH_WEBVIEW_ACTIVITY);
                                    intent1.putExtra(WebViewActivity.EXTRA_URL, url);
                                    intent1.putExtra(WebViewActivity.EXTRA_TITLE, webTitle);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                }else {
                                    Intent intent2 = new Intent(context, WebViewActivity.class);
                                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent2.putExtra(WebViewActivity.EXTRA_URL, url);
                                    intent2.putExtra(WebViewActivity.EXTRA_TITLE, webTitle);
                                    context.startActivity(intent2);
                                }
                            }
                                break;
							case 6: {//跳转包子商城
								if (DeviceUtil.isAppOnForeground(context)) {
									Intent marketIntent = new Intent(context, MainActivity.class);
									marketIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_MARKET);
									marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(marketIntent);
								} else {
									Intent intent1 = new Intent(context, LauncherActivity.class);
									intent1.putExtra(LauncherActivity.EXTRA_TAG, LauncherActivity.PUSH_MAKET_ACTIVITY);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent1);
								}
								break;
							}
							default:
								break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			/*if (bundle != null){
				Intent i = new Intent(context, MainActivity.class);
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}*/
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//			if (bundle != null)
//				LogUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			if (connected) {
				/*Intent service = new Intent(context, Service.class);
				service.setAction(UserService.ACTION_UPDATE_JPUSH_REG_ID);
				service.putExtra(UserService.ARGS_ID, JPushInterface.getRegistrationID(context));
				context.startService(service);*/
			}
		} else {
//			LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		if (bundle != null){
			StringBuilder sb = new StringBuilder();
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
				} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
				} else {
					sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
				}
			}
			return sb.toString();
		}else {
			return "";
		}
	}

	private void processCustomMessage(Context context, Bundle bundle){
	}

	private void processCustomMessage(Context context, Bundle bundle, int notifactionId) {
		if (bundle != null){
			String data = bundle.getString(JPushInterface.EXTRA_EXTRA);
			if (data != null && data.contains("type")){
				try {
					JSONObject jsonObject = new JSONObject(data);
					int key = jsonObject.optInt("type");
					switch (key){
						case 1:
						{
							List<Activity> activities = ExitManager.instance.getActivities();
							String merchName = jsonObject.getString("mn");
							String amount = jsonObject.getString("am");
//							boolean appOnForeground = DeviceUtil.isAppOnForeground(context);
							if (activities != null && activities.size() > 0 && DeviceUtil.isTopActivy(context)){
								Context context1 = activities.get(activities.size() - 1);
								Intent intent1 = new Intent(context1, ConsumeResultActivity.class);
								intent1.putExtra(ConsumeResultActivity.EXTRA_ORDERNO, merchName);
								intent1.putExtra(ConsumeResultActivity.EXTRA_AMOUNT, amount);
								context1.startActivity(intent1);
								JPushInterface.clearNotificationById(context1, notifactionId);
							}
							break;
						}
						case 3:
						case 5:
						{
							Intent intentLogout = new Intent(BaseActivity.ACTION_PUSH_RECEIVER);
							intentLogout.putExtra("data",data);
							LocalBroadcastManager.getInstance(context).sendBroadcast(intentLogout);
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processCustomMessage(Context context, Bundle bundle, boolean openApp) {
		/*if (openApp) {
			Intent i = new Intent(context, MainActivity.class);
			i.putExtras(bundle);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		if (TextUtils.isEmpty(message)) {
			message = bundle.getString(JPushInterface.EXTRA_ALERT);
		}
		RetVal<PaymentOrder> messageObj = null;
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (extraJson.length() > 0) {
					messageObj = new RetVal<>();
					messageObj.Code = 0;
					messageObj.Msg = message;
					messageObj.Toast = message;
					messageObj.Val = new PaymentOrder();
					messageObj.Val.Paymentno = extraJson.getString("pn");
					messageObj.Val.Amount = extraJson.getDouble("am");
					messageObj.Val.MerId = extraJson.getLong("mi");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (messageObj != null) {
			EventBus.getDefault().post(new Events.EventPaymentOrder("", messageObj));
		}*/
	}
}
