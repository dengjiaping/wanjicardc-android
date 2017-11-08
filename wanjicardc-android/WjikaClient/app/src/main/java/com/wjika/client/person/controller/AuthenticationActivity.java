package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.EnCryptionUtils;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.AuthenticationEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.VerifyUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/8/31 14:09.
 * 实名认证
 */
public class AuthenticationActivity extends ToolBarActivity implements View.OnClickListener {

	public static final int FROM_PERSON = 20;
	public static final int FROM_BUY_CARD = 30;
	public static final int FROM_PERSON_INFO = 40;
	public static final int REQUEST_NET_SIGN_CODE = 100;
	public static final int REQUEST_ACT_AUTH_CODE = 200;
	private static final int REQUEST_CHOOSE_BANK = 300;

	public static final String CUS_NAME = "cus_name";
	public static final String ID_CODE = "id_code";
	public static final String ACCT_ID = "acct_id";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final int BUY_CARD_DETAIL = 0x1;

	@ViewInject(R.id.person_authentication_name)
	private EditText personAuthenticationName;
	@ViewInject(R.id.person_authentication_identity)
	private EditText personAuthenticationIdentity;
	@ViewInject(R.id.person_authentication_card)
	private EditText personAuthenticationBankCard;
	@ViewInject(R.id.person_authentication_phone)
	private EditText personAuthenticationPhone;
	@ViewInject(R.id.person_authentication_submit)
	private TextView personAuthenticationSubmit;
	@ViewInject(R.id.authentication_image)
	private ImageView authenticationImage;
	@ViewInject(R.id.authentication_info)
	private TextView authenticationInfo;
	@ViewInject(R.id.person_auth_bank_list)
	private ImageView personAuthBankList;
	@ViewInject(R.id.authentication_choose_bank)
	private LinearLayout authenticationChooseBank;
	@ViewInject(R.id.authentication_bank_name)
	private TextView authticationBankName;

	private String name;
	private String idNo;
	private String bankNo;
	private String phone;

	private int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (UserCenter.getAuthMoney(this)) {
			Intent intent = new Intent(this,AuthCodeActivity.class);
			intent.putExtra("isset",true);
			startActivity(intent);
			finish();
		} else {
			setContentView(R.layout.person_act_authentication);
			ViewInjectUtils.inject(this);
			initView();
		}
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.person_info_auth));
		from = getIntent().getIntExtra("from",0);
		InputUtil.editIsEmpty(personAuthenticationSubmit, personAuthenticationName,
				personAuthenticationIdentity, personAuthenticationBankCard, personAuthenticationPhone);
		authenticationImage.setImageResource(R.drawable.person_auth_commit);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(AuthenticationActivity.this, personAuthenticationName);
			}
		}, 100);
		personAuthenticationSubmit.setOnClickListener(AuthenticationActivity.this);
		authenticationInfo.setText(getString(R.string.person_auth_hint));
		personAuthBankList.setOnClickListener(this);
		authenticationChooseBank.setOnClickListener(this);
		personAuthenticationIdentity.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains("x")) {
					personAuthenticationIdentity.setText(s.toString().toUpperCase());
					personAuthenticationIdentity.setSelection(s.length());
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_certification");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.person_authentication_submit:
				name = personAuthenticationName.getText().toString().trim();
				idNo = personAuthenticationIdentity.getText().toString().trim();
				bankNo = personAuthenticationBankCard.getText().toString().trim();
				phone = personAuthenticationPhone.getText().toString().trim();
				if (!TextUtils.isEmpty(name) && VerifyUtils.verifyIdentityCard(idNo)) {
					if (TextUtils.isEmpty(phone) || phone.length() != 11 || !phone.startsWith("1")) {
						ToastUtil.shortShow(this, getString(R.string.login_please_right_phone));
					} else if (bankNo.length() < 13) {
						ToastUtil.shortShow(this, getString(R.string.input_correct_bank_number));
					} else {
						showProgressDialog();
						IdentityHashMap<String, String> param = new IdentityHashMap<>();
						param.put("custName", name);
						param.put("idCode", idNo);
						param.put("acctId", bankNo);
						param.put("mobilePhone", phone);
						param.put("token", UserCenter.getToken(this));
						requestHttpData(Constants.Urls.URL_POST_OBTAIN_CODE, REQUEST_NET_SIGN_CODE, FProtocol.HttpMethod.POST, param);
					}
				} else {
					ToastUtil.shortShow(this, res.getString(R.string.person_auth_toast));
				}
				break;
			case R.id.authentication_choose_bank:
			case R.id.person_auth_bank_list:
				startActivityForResult(new Intent(this, AuthBankSupportActivity.class), REQUEST_CHOOSE_BANK);
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		if (REQUEST_NET_SIGN_CODE == requestCode) {
			AuthenticationEntity authenticationEntity = Parsers.getAuthCode(data);
			boolean isUnionVerify = authenticationEntity.isUnionVerify();
			if (authenticationEntity != null && !TextUtils.isEmpty(authenticationEntity.getCertificate()) &&
					EnCryptionUtils.SHA1(UserCenter.getToken(this)).equals(authenticationEntity.getCertificate())) {
				if (authenticationEntity.isAuthentication()) {
					ToastUtil.shortShow(this, getString(R.string.person_auth_toast_authenticated));
					UserCenter.setAuthenticatiton(this, true);
					setResult(RESULT_OK);
					finish();
					return;
				}
				Intent intent = new Intent(this, AuthCodeActivity.class);
				intent.putExtra("from", from);
				intent.putExtra("isset",false);
				intent.putExtra(AuthInfoActivity.CUS_NAME, name);
				intent.putExtra(AuthInfoActivity.ID_CODE, idNo);
				intent.putExtra(AuthInfoActivity.ACCT_ID, bankNo);
				intent.putExtra(AuthInfoActivity.MOBILE_PHONE, phone);
				intent.putExtra(AuthInfoActivity.IS_UNIONVERIFY,isUnionVerify);
				if (FROM_BUY_CARD == from || FROM_PERSON == from) {
					startActivityForResult(intent, REQUEST_ACT_AUTH_CODE);
				} else {
					startActivity(intent);
					finish();
				}
			} else {
				ToastUtil.shortShow(this, getString(R.string.verify_safe_validation_fails));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case REQUEST_ACT_AUTH_CODE:
					setResult(RESULT_OK, data);
					finish();
					break;
				case REQUEST_CHOOSE_BANK:
					authticationBankName.setText(data.getStringExtra(AuthBankSupportActivity.INTENT_BANK_NAME));
					authticationBankName.setTextColor(getResources().getColor(R.color.wjika_client_dark_grey));
					break;
			}
		} else {
			switch (requestCode) {
				case REQUEST_ACT_AUTH_CODE:
					finish();
					break;
				case REQUEST_CHOOSE_BANK:
					break;
			}
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
