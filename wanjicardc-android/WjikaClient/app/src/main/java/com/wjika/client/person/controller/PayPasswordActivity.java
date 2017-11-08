package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.wjika.client.cardpackage.controller.CardPackageActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.SetPayPwdEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Liu_ZhiChao on 2015/9/9 17:52.
 * 支付密码
 * 修改/找回支付密码，设置新的支付密码
 */
public class PayPasswordActivity extends ToolBarActivity implements View.OnClickListener{

	public static final String FROM_EXTRA = "extra_from";
	public static final String IDENTITY_EXTRA = "identity_extra";
	public static final int FIND_PASSWORD_RESET = 100;
	private static final int REQUEST_PAY_PASSWORD_CODE = 500;

	@ViewInject(R.id.btn_set_pay_pwd_ok)
	private TextView setPayPwdOk;
	@ViewInject(R.id.et_set_pay_pwd)
	private EditText etSetPayPwd;
	@ViewInject(R.id.et_set_pay_pwd_confirm)
	private EditText etSetPayPwdConfirm;

	private int from;
	private String pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_pay_password);
		ViewInjectUtils.inject(this);
		ExitManager.instance.addLoginActivity(this);
		from = getIntent().getIntExtra(FROM_EXTRA, 0);
		initView();
	}

	private void initView() {
		if (from == FIND_PASSWORD_RESET) {
			setLeftTitle("修改支付密码");
		} else {
			setLeftTitle(getString(R.string.set_pay_pwd_title));
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(PayPasswordActivity.this, etSetPayPwd);
			}
		}, 100);
		setPayPwdOk.setOnClickListener(this);
		InputUtil.editIsEmpty(setPayPwdOk,etSetPayPwd,etSetPayPwdConfirm);
		etSetPayPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String password = etSetPayPwd.getText().toString().trim();
				if (6 == password.length()) {
					etSetPayPwdConfirm.requestFocus();
				}
			}
		});
	}

	private void creatOrSetPayPwd() {
		showProgressDialog();
		IdentityHashMap<String, String> paramHashMap = new IdentityHashMap<>();
		paramHashMap.put("payPassword", pwd);
		paramHashMap.put("userPushID", JPushInterface.getRegistrationID(this.getApplicationContext()));
		paramHashMap.put("token", UserCenter.getToken(this));
		paramHashMap.put("identity",UserCenter.getUserIdentity(this));
		requestHttpData(Constants.Urls.URL_POST_FIRST_SET_PWD, REQUEST_PAY_PASSWORD_CODE,
				FProtocol.HttpMethod.POST, paramHashMap);
	}

	@Override
	protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
		if(REQUEST_PAY_PASSWORD_CODE == requestCode) {
			SetPayPwdEntity entity = Parsers.getSetPayPwdEntity(data);
			String paySalt = entity.getSalt();
			UserCenter.setUserPaypwdSalt(this, paySalt);
			UserCenter.setUserPaypwd(this, EnCryptionUtils.SHA1(pwd, paySalt));
			UserCenter.setToken(this,entity.getToken());
			if (from == FIND_PASSWORD_RESET){
				ToastUtil.shortShow(this, res.getString(R.string.pay_password_changed));
			} else if (from == LoginActivity.FROM_PERSON_CENTER || from == LoginActivity.FROM_MAIN_ACTIVITY) {
				setResult(RESULT_OK);
			} else{
				ToastUtil.shortShow(this, getString(R.string.person_pay_setting_success));
				UserCenter.setUserSetNopwd(this, true);//默认开启无密支付
				ExitManager.instance.closeLoginActivity();
				if (from == LoginActivity.FROM_MAIN_CARD_PKG) {
					startActivity(new Intent(this, CardPackageActivity.class));
				}
			}
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(R.id.btn_set_pay_pwd_ok == id) {
			pwd = etSetPayPwd.getText().toString();
			String pwdConfirm = etSetPayPwdConfirm.getText().toString();
			if(!StringUtil.isEmpty(pwd)) {
				if(6 != pwd.length()) {
					ToastUtil.shortShow(this,getString(R.string.pay_input_six_password));
				} else if(pwd.equals(pwdConfirm)){
					creatOrSetPayPwd();
				} else {
					etSetPayPwd.setText("");
					etSetPayPwdConfirm.setText("");
					ToastUtil.shortShow(this, getString(R.string.pay_password_different_input_password));
					etSetPayPwd.requestFocus();
				}
			} else {
				ToastUtil.shortShow(this, getString(R.string.pay_password_set_up_payment));
			}
		} else if(R.id.left_button == id) {
			this.finish();
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		if (from == FIND_PASSWORD_RESET) {
			finish();
		}
	}
}
