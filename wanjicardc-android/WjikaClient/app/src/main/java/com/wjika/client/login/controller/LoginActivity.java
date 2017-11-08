package com.wjika.client.login.controller;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.EnCryptionUtils;
import com.common.utils.NetWorkUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.ClientApplication;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.db.CityDBHelper;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.PayVerifyPWDActivity;
import com.wjika.client.person.controller.ComplainMessageFirstActivity;
import com.wjika.client.person.controller.FindPassByAuthActivity;
import com.wjika.client.person.controller.InputOldPhoneActivity;
import com.wjika.client.person.controller.PayPasswordActivity;
import com.wjika.client.person.controller.VerifySafeQuestionActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.StreamUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jacktian on 15/8/27.
 * 登录
 */
public class LoginActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_FROM = "extra_from";
	public static final int FROM_MAIN_CARD_PKG = 8;
	public static final int FROM_CARD_DETAIL_CODE = 11;
	public static final int FROM_SETTING_ALTER_PHONE = 5;
	public static final int FROM_INPUT_OLD_PHONE = 10;
	public static final int FROM_MESSAGE_CENTER = 7;
	public static final int FROM_PERSON_CENTER = 9;
	public static final int FROM_MAIN_ACTIVITY = 12;
	private static final int REQUEST_GET_SIGN_CODE = 0x1;
	private static final int REQUEST_LOGIN = 0x2;
	private static final int REQUEST_VERIFY_PHONE = 0x3;
	private static final int REQUEST_FOR_PERSON_CENTER = 0x4;//登陆成功跳转个人中心弹出实名提示

	@ViewInject(R.id.login_phone)
	private TextView loginPhone;
	@ViewInject(R.id.input_phone)
	private EditText mInputPhone;
	@ViewInject(R.id.input_signcode)
	private EditText mInputSignCode;
	@ViewInject(R.id.btn_get_signcode)
	private TextView mBtnGetSignCode;
	@ViewInject(R.id.btn_login)
	private TextView mBtnLogin;
	@ViewInject(R.id.btn_login_change_phone)
	private TextView btnLoginChangePhone;

	private CountDownTimer countDownTimer;
	private String phone = "";
	private int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);
		ExitManager.instance.addChangePhoneAct(this);
		ExitManager.instance.addLoginActivity(this);
		ViewInjectUtils.inject(this);
		InputUtil.editIsEmpty(mBtnLogin, mInputPhone, mInputSignCode);
		InputUtil.editIsEmpty(mBtnGetSignCode, mInputPhone);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_act_login");
		from = getIntent().getIntExtra(EXTRA_FROM, 0);
		initView();
	}

	private void initView() {
		mBtnGetSignCode.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		btnLoginChangePhone.setOnClickListener(this);
		if (FROM_SETTING_ALTER_PHONE == from || FROM_INPUT_OLD_PHONE == from) {
			setLeftTitle(getString(R.string.verify_new_phone));
			loginPhone.setText(R.string.login_input_new_phone);
			mInputPhone.setHint(R.string.login_input_new_phone_again);
			mBtnLogin.setText(R.string.login_finish);
			btnLoginChangePhone.setVisibility(View.INVISIBLE);
		} else {
			setLeftTitle(getString(R.string.login));
			mBtnTitleRight.setVisibility(View.VISIBLE);
			mBtnTitleRight.setImageResource(R.drawable.ic_close_button);
			mBtnTitleRight.setOnClickListener(this);
			mBtnTitleLeft.setVisibility(View.GONE);
		}
		countDownTimer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				long time = millisUntilFinished / 1000;
				mBtnGetSignCode.setEnabled(false);
				mBtnGetSignCode.setText(String.format(getString(R.string.person_verification_code_time), String.valueOf(time)));
			}

			@Override
			public void onFinish() {
				if (!TextUtils.isEmpty(mInputPhone.getText())) {
					mBtnGetSignCode.setEnabled(true);
				}
				mBtnGetSignCode.setText(getString(R.string.login_get_signcode_again));
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_get_signcode:
				phone = mInputPhone.getText().toString();
				if (checkPhoneNum(phone)) {
					countDownTimer.start();
					getSignCode();
					mInputSignCode.requestFocus();
				}
				break;
			case R.id.btn_login:
				phone = mInputPhone.getText().toString();
				if (checkPhoneNum(phone)) {
					login();
				}
//                copyDBFile();//发布版本之前复制数据库文件时使用
				break;
			case R.id.btn_login_change_phone:
				startActivity(new Intent(this, InputOldPhoneActivity.class));
				break;
			case R.id.right_button:
				finish();
				break;
		}
	}

	//用于发布版本之前复制数据库文件使用
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == 1) {
				ToastUtil.shortShow(LoginActivity.this, "copy DB Success!");
			} else {
				ToastUtil.shortShow(LoginActivity.this, "copy DB Failure!");
			}
		}
	};

	/**
	 * 用于发布版本之前复制数据库文件到sd卡。
	 * 1.程序运行后，点击左上角城市切换（北京），展示城市菜单。
	 * 2.到登录界面，点击登录按钮，将数据库复制到手机存储
	 * 3.将数据库文件拷贝到asset目录
	 * 4.更改city.properties中area_version值为服务端最新的版本号
	 */
	private void copyDBFile() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileInputStream fis = null;
				try {
					File file = new File(LoginActivity.this.getDir(CityDBHelper.DB_DIR_NAME, 0).getAbsolutePath() + File.separator + CityDBHelper.DATABASE_NAME);
					fis = new FileInputStream(file);
					String path = Environment.getExternalStorageDirectory() + File.separator + "wjk";
					if (StreamUtil.saveStreamToFile(fis, path + File.separator + "wjk.db")) {
						mHandler.sendEmptyMessage(1);
					} else {
						mHandler.sendEmptyMessage(2);
					}
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(2);
				} finally {
					StreamUtil.closeStream(fis);
				}
			}
		}).start();
	}

	private boolean checkPhoneNum(String phone) {
		if (StringUtil.isEmpty(phone)) {
			ToastUtil.shortShow(this, getString(R.string.login_please_input_phone));
			return false;
		} else if (!"1".equals(phone.trim().substring(0, 1))) {
			ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
			return false;
		}
		if (FROM_SETTING_ALTER_PHONE == from && UserCenter.getUserPhone(this).equals(phone)) {
			ToastUtil.shortShow(this, getString(R.string.login_no_change_again));
			return false;
		}
		if (phone.trim().length() != 11) {
			ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
			return false;
		}
		return true;
	}

	private void getSignCode() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("phone", phone);
		params.put("token", UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_GET_LOGIN_SIGN_CODE, REQUEST_GET_SIGN_CODE, FProtocol.HttpMethod.POST, params);
	}

	private void login() {
		String inputPhone = mInputPhone.getText().toString();
		String signCode = mInputSignCode.getText().toString();
		if (!NetWorkUtil.isConnect(this)) {
//            ToastUtil.shortShow(this, "当前网络不可用，请检查网络！");
			return;
		}
		if (StringUtil.isEmpty(inputPhone)) {
			ToastUtil.shortShow(this, getString(R.string.login_please_input_phone));
			return;
		}
		if (StringUtil.isEmpty(signCode)) {
			ToastUtil.shortShow(this, getString(R.string.logon_please_input_code));
			return;
		}
//        initSignState();
		phone = inputPhone.trim();
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		if (FROM_SETTING_ALTER_PHONE == from || FROM_INPUT_OLD_PHONE == from) {
			params.put("phone", phone);
			params.put("verificationCode", signCode.trim());
			if (FROM_SETTING_ALTER_PHONE == from) {
				params.put("token", UserCenter.getToken(this));
			} else {
				params.put("token", getIntent().getStringExtra("token"));
			}
			params.put("userPushID", JPushInterface.getRegistrationID(this.getApplicationContext()));
			requestHttpData(Constants.Urls.URL_POST_CHANGE_PHONE, REQUEST_VERIFY_PHONE, FProtocol.HttpMethod.POST, params);
		} else {
			params.put("phone", phone);
			params.put("token", "");
			params.put("verificationCode", signCode.trim());
			params.put("userPushID", JPushInterface.getRegistrationID(this.getApplicationContext()));
			params.put("appChannel", ClientApplication.getChannel(this));
			requestHttpData(Constants.Urls.URL_LOGIN, REQUEST_LOGIN, FProtocol.HttpMethod.POST, params);
		}
	}

	private void initSignState() {
		countDownTimer.cancel();
		mBtnGetSignCode.setClickable(true);
		mBtnGetSignCode.setText("获取验证码");
		InputMethodUtil.closeInputMethod(this);
	}

	@Override
	public void success(int requestCode, String data) {
		InputMethodUtil.closeInputMethod(this);
		closeProgressDialog();
		Entity entity = Parsers.getResponseSatus(data);
		if (RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
			switch (requestCode) {
				case REQUEST_GET_SIGN_CODE: {
					break;
				}
				case REQUEST_VERIFY_PHONE:
					if (FROM_SETTING_ALTER_PHONE == from) {
						setResult(RESULT_OK, new Intent().putExtra("phone", phone));
						ToastUtil.shortShow(this, "手机号更改成功");
						UserEntity user = Parsers.getUserInfo(data);
						UserCenter.saveLoginInfo(this, user);
					} else {
						UserEntity user = Parsers.getUserInfo(data);
						String paypwd = getIntent().getStringExtra("paypwd");
						if (user != null && !TextUtils.isEmpty(paypwd)) {
							String payPwdSalt = user.getPayPwdSalt();
							UserCenter.setUserPaypwdSalt(this, payPwdSalt);
							UserCenter.setUserPaypwd(this, EnCryptionUtils.SHA1(paypwd, payPwdSalt));
							user.setPhone(phone);
							UserCenter.saveLoginInfo(this, user);
							Intent intent = new Intent(this, MainActivity.class);
							startActivity(intent);
						} else {
							ToastUtil.shortShow(this, "手机号更换失败");
						}
					}
					ExitManager.instance.closeChangePhoneAct();
					break;
				case REQUEST_LOGIN: {
					if (data != null) {
						UserEntity user = Parsers.getUserInfo(data);
						if (user != null) {
							user.setPhone(phone);
							UserCenter.saveLoginInfo(this, user);

							if (user.isSetPayPassword()) { //已设置支付密码，跳到验证支付密码界面
								Intent intent = new Intent(this, PayVerifyPWDActivity.class);
								intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, from);
								intent.putExtra(PayVerifyPWDActivity.IDENTITY_EXTRA, user.getIdentity());
								startActivityForResult(intent, REQUEST_FOR_PERSON_CENTER);
							} else {//没设置支付密码，跳到设置支付密码界面
								Intent intent = new Intent(this, PayPasswordActivity.class);
								intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, from);
								intent.putExtra(PayPasswordActivity.IDENTITY_EXTRA, user.getIdentity());
								startActivityForResult(intent, REQUEST_FOR_PERSON_CENTER);
							}
						} else {
							ToastUtil.shortShow(this, getString(R.string.login_failed));
						}
					}
					break;
				}
			}
		} else if (RESPONSE_USER_LOCKED.equals(entity.getResultCode())) {
			if (data != null) {
				UserEntity user = Parsers.getUserInfo(data);
				if (user.isLockedStatus()) {
					initSignState();
					mBtnGetSignCode.setEnabled(true);
					showVerifyFailedDialog(getString(R.string.title_account_locked), entity.getResultMsg(), R.string.pay_verify_pay_back_password, user);
				}
			}
		} else {
			super.success(requestCode, data);
		}
	}

	/**
	 * @param msg message
	 * @param okTextResId 再试一次R.string.person_pay_setting_confirm_pwd; 找回支付密码R.string.pay_verify_pay_back_password
	 */
	private void showVerifyFailedDialog(String title, String msg, int okTextResId, final UserEntity user) {
		closeProgressDialog();
		showAlertDialog(title,
				msg,
				res.getString(R.string.verify_safe_cancel),
				res.getString(okTextResId),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv = (TextView) v;
						if (tv.getText().equals(res.getString(R.string.pay_verify_pay_back_password))) {
							showMenu(user.isSetSecurity(), user.getAuthentication() == 1, user.getAppealStatus());
						}
						closeDialog();
					}
				}
		);
	}

	private void showMenu(boolean isShowSecurity, boolean isAuthencation, final int appealStatus) {
		final Dialog dialog = new Dialog(LoginActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(R.layout.chooseway_dialog);
		View tvSecurity = dialog.findViewById(R.id.tv_security_find);
		TextView tvAuthenticaion = (TextView) dialog.findViewById(R.id.tv_authentication_find);
		View vLine1 = dialog.findViewById(R.id.v_Line1);
		View vLine2 = dialog.findViewById(R.id.v_Line2);
		if (isAuthencation) {
			tvAuthenticaion.setVisibility(View.VISIBLE);
			vLine1.setVisibility(View.VISIBLE);
		} else {
			tvAuthenticaion.setVisibility(View.GONE);
			vLine1.setVisibility(View.GONE);
		}
		if (isShowSecurity) {
			tvSecurity.setVisibility(View.VISIBLE);
			vLine2.setVisibility(View.VISIBLE);
		} else {
			tvSecurity.setVisibility(View.GONE);
			vLine2.setVisibility(View.GONE);
		}
		//安全问题找回
		tvSecurity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent securityIntent = new Intent(LoginActivity.this, VerifySafeQuestionActivity.class);
				securityIntent.putExtra(VerifySafeQuestionActivity.FROM_EXTRA, VerifySafeQuestionActivity.MODIFY);
				startActivity(securityIntent);
				dialog.dismiss();
			}
		});
		//实名找回
		tvAuthenticaion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, FindPassByAuthActivity.class));
				dialog.dismiss();
			}
		});
		//申诉找回
		dialog.findViewById(R.id.tv_complain_find).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (10 != appealStatus) {
					startActivity(new Intent(LoginActivity.this, ComplainMessageFirstActivity.class));
				} else {
					ToastUtil.longShow(getApplicationContext(), "您的账号正在申诉中");
				}
			}
		});
		//取消
		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Window window = dialog.getWindow();
		if (window != null) {
			window.setGravity(Gravity.BOTTOM);
			//window.setWindowAnimations(R.style.dialogAnimation);
			// 可以在此设置显示动画
			WindowManager.LayoutParams wl = window.getAttributes();
			// 以下这两句是为了保证按钮可以水平满屏
			wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.onWindowAttributesChanged(wl);
		}
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_FOR_PERSON_CENTER:
					setResult(RESULT_OK);
					finish();
					break;
			}
		}
	}

	@Override
	public void onDestroy() {
		initSignState();
		InputMethodUtil.closeInputMethod(this);
		super.onDestroy();
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		if (from == FROM_SETTING_ALTER_PHONE) {
			finish();
		}
	}
}
