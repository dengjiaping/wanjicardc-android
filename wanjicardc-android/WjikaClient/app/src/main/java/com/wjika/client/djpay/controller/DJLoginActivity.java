package com.wjika.client.djpay.controller;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DJUserEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by liuzhichao on 2016/11/24.
 * 斗金-登录
 */
public class DJLoginActivity extends ToolBarActivity implements View.OnClickListener {

	private static final int REQUEST_NET_CODE = 1;
	private static final int REQUEST_NET_LOGIN = 2;

	@ViewInject(R.id.input_phone)
	private EditText inputPhone;
	@ViewInject(R.id.input_signcode)
	private EditText inputSigncode;
	@ViewInject(R.id.input_invite_code)
	private EditText inputInviteCode;
	@ViewInject(R.id.btn_get_signcode)
	private TextView btnGetSigncode;
	@ViewInject(R.id.btn_login)
	private View btnLogin;

	private CountDownTimer countDownTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login_dj);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setCenterTitleAndLeftText("登录");
		mTxtLeft.setText("取消");
		btnGetSigncode.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		InputUtil.editIsEmpty(btnLogin, inputPhone, inputSigncode);
		countDownTimer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				long time = millisUntilFinished / 1000;
				btnGetSigncode.setEnabled(false);
				btnGetSigncode.setText(String.format(getString(R.string.person_verification_code_time), String.valueOf(time)));
			}

			@Override
			public void onFinish() {
				btnGetSigncode.setEnabled(true);
				btnGetSigncode.setText(getString(R.string.login_get_signcode_again));
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_get_signcode:
				String phone = inputPhone.getText().toString();
				if (checkPhoneNum(phone)) {
					countDownTimer.start();
					getSignCode(phone);
					inputSigncode.requestFocus();
				}
				break;
			case R.id.btn_login:
				String userPhone = inputPhone.getText().toString();
				String signCode = inputSigncode.getText().toString();
				String inviteCode = inputInviteCode.getText().toString();
				if (checkPhoneNum(userPhone)) {
					if (TextUtils.isEmpty(signCode)) {
						ToastUtil.shortShow(this, "请输入验证码");
						return;
					}
					showProgressDialog();
					IdentityHashMap<String, String> params = new IdentityHashMap<>();
					params.put("userPhone", userPhone);
					params.put("code", signCode);
					params.put(Constants.TOKEN, "");
					params.put("dj", "");
					params.put("inviteCode", inviteCode);
					requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_LOGIN, REQUEST_NET_LOGIN, FProtocol.HttpMethod.POST, params);
				}
				break;
		}
	}

	private boolean checkPhoneNum(String phone) {
		if (StringUtil.isEmpty(phone)) {
			ToastUtil.shortShow(this, getString(R.string.login_please_input_phone));
			return false;
		} else if (!"1".equals(phone.trim().substring(0, 1))) {
			ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
			return false;
		}
		if (phone.trim().length() != 11) {
			ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
			return false;
		}
		return true;
	}

	private void getSignCode(String phone) {
		showProgressDialog();
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_GET_VERIFICATION_CODE + "?userPhone=" + phone + "&" + Constants.TOKEN + "=&dj=", REQUEST_NET_CODE);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		if (REQUEST_NET_CODE == requestCode) {
			ToastUtil.shortShow(this, "验证码已经发送成功，请注意查收");
		} else if (REQUEST_NET_LOGIN == requestCode) {
			DJUserEntity djUser = Parsers.getDJUser(data);
			if (djUser != null) {
				DJUserCenter.setToken(this, djUser.getToken());
				DJUserCenter.setBankCard(this, djUser.getCardNum() > 0);
				DJUserCenter.setPhone(this, djUser.getPhone());
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	private void initSignState() {
		countDownTimer.cancel();
		btnGetSigncode.setClickable(true);
		btnGetSigncode.setText("获取验证码");
		InputMethodUtil.closeInputMethod(this);
	}

	@Override
	public void onDestroy() {
		initSignState();
		InputMethodUtil.closeInputMethod(this);
		super.onDestroy();
	}
}
