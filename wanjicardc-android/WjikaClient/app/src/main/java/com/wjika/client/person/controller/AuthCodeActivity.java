package com.wjika.client.person.controller;

/**
 * Created by zhaoweiwei on 2016/6/29.
 * 实名验证码
 */

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.EnCryptionUtils;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.AuthInfoEntity;
import com.wjika.client.network.entities.AuthenticationEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * 实名认证-验证码
 */
public class AuthCodeActivity extends ToolBarActivity implements View.OnClickListener {

	private static final int REQUEST_NET_SIGN_CODE = 100;
	private static final int REQUEST_NET_AUTHENTICATION_CODE = 200;

	@ViewInject(R.id.person_verify_code)
	private EditText personVerifyCode;
	@ViewInject(R.id.person_verify_get_code)
	private TextView personVerifyGetCode;
	@ViewInject(R.id.person_auth_btn)
	private TextView personAuthBtn;
	@ViewInject(R.id.person_verify_info)
	private TextView personVerifyInfo;
	@ViewInject(R.id.person_auth_moneyverify_info)
	private TextView personMoneyInfo;
	@ViewInject(R.id.person_authcode_info)
	private TextView personAuthCodeInfo;

	private CountDownTimer countDownTimer;
	private String name;
	private String idNo;
	private String bankNo;
	private String phone;
	private String tempPhone;
	private boolean isUnionVerify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_auth_code);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_info_auth));
		boolean isSet = getIntent().getBooleanExtra("isset",false);
		if (isSet) {//如果已经实名验证过小额鉴权
			bankNo = UserCenter.getAuthBankCard(this);
			setMoneyData();
		} else {
			name = getIntent().getStringExtra(AuthenticationActivity.CUS_NAME);
			idNo = getIntent().getStringExtra(AuthenticationActivity.ID_CODE);
			if (StringUtil.isEmpty(UserCenter.getAuthBankCard(this))) {
				bankNo = getIntent().getStringExtra(AuthenticationActivity.ACCT_ID);
			} else {
				bankNo = UserCenter.getAuthBankCard(this);
			}
			phone = getIntent().getStringExtra(AuthenticationActivity.MOBILE_PHONE);
			if (11 == phone.length()) {
				tempPhone = phone.substring(0, 3) + "****" + phone.substring(7);
			}
			isUnionVerify = getIntent().getBooleanExtra(AuthInfoActivity.IS_UNIONVERIFY,true);
			countDownTimer = new CountDownTimer(120000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					long time = millisUntilFinished / 1000;
					personVerifyGetCode.setEnabled(false);
					personVerifyGetCode.setText(String.format(getString(R.string.person_verification_code_time), time));
				}

				@Override
				public void onFinish() {
					personVerifyGetCode.setEnabled(true);
					personVerifyGetCode.setText(getString(R.string.login_get_signcode));
				}
			};
			if (isUnionVerify) {
				setSmsData();//设置获取短信验证码的显示
				countDownTimer.start();
				personVerifyGetCode.setOnClickListener(this);
			} else {
				setMoneyData();//设置获取小额鉴权的显示
				UserCenter.setAuthBankCard(this,bankNo);
				UserCenter.setAuthMoney(this,true);
			}
		}

		InputUtil.editIsEmpty(personAuthBtn, personVerifyCode);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(AuthCodeActivity.this, personVerifyCode);
			}
		}, 100);
		personAuthBtn.setOnClickListener(this);
	}

	private void setSmsData() {
		SpannableString spannableString = new SpannableString(String.format(res.getString(R.string.person_auth_sms_info),tempPhone));
		spannableString.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_title_bg)),16,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		personAuthCodeInfo.setText(spannableString);
		personVerifyInfo.setText(getString(R.string.login_input_signcode));
		personVerifyCode.setHint(getString(R.string.person_auth_input_sms));
		personVerifyGetCode.setVisibility(View.VISIBLE);
		personMoneyInfo.setVisibility(View.GONE);
	}

	private void setMoneyData() {
		personVerifyGetCode.setVisibility(View.GONE);
		personMoneyInfo.setVisibility(View.VISIBLE);
		if (!StringUtil.isEmpty(bankNo)) {
			String tempBankNum = bankNo.substring(bankNo.length()-4,bankNo.length());
			SpannableStringBuilder spannableString = new SpannableStringBuilder(String.format(res.getString(R.string.person_auth_money_info),bankNo));
			spannableString.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_title_bg)),8,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			personAuthCodeInfo.setText(spannableString);
		}
		personVerifyInfo.setText(getString(R.string.person_auth_get_money));
		personVerifyCode.setHint(getString(R.string.person_auth_input_money));
		SpannableStringBuilder str = new SpannableStringBuilder(getString(R.string.person_auth_money_prompt_info));
		str.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_title_bg)),24,26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		personMoneyInfo.setText(str);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.person_verify_get_code:
				showProgressDialog();
				IdentityHashMap<String, String> param = new IdentityHashMap<>();
				param.put("custName", name);
				param.put("idCode", idNo);
				param.put("acctId", bankNo);
				param.put("mobilePhone", phone);
				param.put("token", UserCenter.getToken(this));
				requestHttpData(Constants.Urls.URL_POST_OBTAIN_CODE, REQUEST_NET_SIGN_CODE, FProtocol.HttpMethod.POST, param);
				break;
			case R.id.person_auth_btn:
				showProgressDialog();
				String code = personVerifyCode.getText().toString().trim();
				IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
				identityHashMap.put("token", UserCenter.getToken(this));
				if (isUnionVerify) {
					identityHashMap.put("messageCode", code);
					requestHttpData(Constants.Urls.URL_POST_AUTH_VERIFY_CODE, REQUEST_NET_AUTHENTICATION_CODE, FProtocol.HttpMethod.POST, identityHashMap);
				} else {
					identityHashMap.put("amount", code);
					requestHttpData(Constants.Urls.URL_POST_AUTH_VERIFY_MONEY, REQUEST_NET_AUTHENTICATION_CODE, FProtocol.HttpMethod.POST, identityHashMap);
				}
				break;
		}
	}

	private void initSignState() {
		countDownTimer.cancel();
		personVerifyGetCode.setClickable(true);
		personVerifyGetCode.setText(getString(R.string.login_get_signcode));
		InputMethodUtil.closeInputMethod(this);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		switch (requestCode) {
			case REQUEST_NET_SIGN_CODE:
				AuthenticationEntity authCode = Parsers.getAuthCode(data);
				if (authCode != null && !TextUtils.isEmpty(authCode.getCertificate()) &&
						EnCryptionUtils.SHA1(UserCenter.getToken(this)).equals(authCode.getCertificate())) {
					countDownTimer.start();
					personVerifyCode.requestFocus();
				} else {
					ToastUtil.shortShow(this, getString(R.string.verify_safe_validation_fails));
				}
				break;
			case REQUEST_NET_AUTHENTICATION_CODE:
				AuthInfoEntity authInfo = Parsers.getAuthInfo(data);
				if (authInfo != null) {
					ToastUtil.shortShow(this,getString(R.string.person_auth_toast_authenticated));
					UserCenter.setAuthenticatiton(this, true);
					setResult(RESULT_OK);
					finish();
				}
				break;
		}
	}

	@Override
	public void setLeftTitle(String title) {
		mTitleLeft.setVisibility(View.VISIBLE);
		mTitleLeft.setText(title);
		toolbarTitleCenter.setVisibility(View.GONE);
		mBtnTitleLeft.setVisibility(View.VISIBLE);
		mBtnTitleLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCancleHint();
			}
		});
	}

	@Override
	public void onBackPressed() {
		showCancleHint();
	}

	private void showCancleHint() {
		showAlertDialog("", getString(R.string.person_auth_giveup_to_auth), getString(R.string.person_give_up), getString(R.string.person_continue), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				UserCenter.setAuthenticatiton(AuthCodeActivity.this, false);
				UserCenter.setAuthMoney(AuthCodeActivity.this,false);
				UserCenter.setAuthBankCard(AuthCodeActivity.this,"");
				setResult(RESULT_CANCELED);
				finish();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});
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
		finish();
	}
}
