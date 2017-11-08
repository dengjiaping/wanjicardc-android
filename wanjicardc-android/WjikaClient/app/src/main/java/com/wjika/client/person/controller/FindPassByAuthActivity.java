package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.VerifyUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/7/1.
 * 身份验证找回支付密码
 */
public class FindPassByAuthActivity extends ToolBarActivity implements View.OnClickListener{
	private static final int REQUEST_FIND_BYAUTHENCATION = 100;

	@ViewInject(R.id.person_authentication_name)
	private EditText authName;
	@ViewInject(R.id.person_authentication_identity)
	private EditText authIdCard;
	@ViewInject(R.id.person_authentication_bankcard)
	private EditText authBankNo;
	@ViewInject(R.id.person_authentication_phone)
	private EditText authPhone;
	@ViewInject(R.id.person_authentication_submit)
	private TextView authSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_authencation_find);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.findpass_authen));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(FindPassByAuthActivity.this, authName);
			}
		}, 100);
		InputUtil.editIsEmpty(authSubmit,authName,authIdCard,authBankNo,authPhone);
		authSubmit.setOnClickListener(this);
		String userName = UserCenter.getUserRelName(this);
		if (!TextUtils.isEmpty(userName)) {
			String nameMiddle = userName.substring(1,userName.length()-1);
			if (userName.length() == 2) {
				userName = userName.substring(0,1) +"*";
			} else if (userName.length() >2){
				String str = "";
				for (int i = 0; i < nameMiddle.length(); i++) {
					str += "*";
				}
				userName = userName.substring(0,1) + str + userName.substring(userName.length() - 1);
			}
			authName.setText(userName);
			authName.setFocusable(false);
			authName.setFocusableInTouchMode(false);
		}
		authIdCard.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains("x")) {
					authIdCard.setText(s.toString().toUpperCase());
					authIdCard.setSelection(s.length());
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.person_authentication_submit:
				String name = UserCenter.getUserRelName(this);
				String idNo = authIdCard.getText().toString().trim();
				String bankNo = authBankNo.getText().toString().trim();
				String phone = authPhone.getText().toString().trim();
				if (!TextUtils.isEmpty(name) && VerifyUtils.verifyIdentityCard(idNo)) {
					if (TextUtils.isEmpty(phone) || phone.length() != 11 || !phone.startsWith("1")) {
						ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
					} else if (bankNo.length() < 13) {
						ToastUtil.shortShow(this, getString(R.string.input_correct_bank_number));
					} else {
						showProgressDialog();
						IdentityHashMap<String, String> param = new IdentityHashMap<>();
						param.put("trueName", name);
						param.put("idNo", idNo);
						param.put("cardNo", bankNo);
						param.put("phone", phone);
						param.put("token", "");
						requestHttpData(Constants.Urls.URL_FINDPASS_BYAUTH, REQUEST_FIND_BYAUTHENCATION, FProtocol.HttpMethod.POST, param);
					}
				} else {
					ToastUtil.shortShow(this, res.getString(R.string.person_auth_toast));
				}
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		if (requestCode == REQUEST_FIND_BYAUTHENCATION) {
			closeProgressDialog();
			startActivity(new Intent(this, PayPasswordActivity.class).putExtra(PayPasswordActivity.FROM_EXTRA, PayPasswordActivity.FIND_PASSWORD_RESET));
			finish();
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		closeProgressDialog();
		ToastUtil.shortShow(this,errorMessage);
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
