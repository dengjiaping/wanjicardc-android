package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.AuthBankSupportActivity;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/11/23.
 */

public class AddBankActivity extends BaseDJActivity implements View.OnClickListener {

	private static final int REQUEST_NET_GETCODE = 0x01;
	private static final int REQUEST_NET_BINDBANK = 0x02;

	@ViewInject(R.id.djpay_addbank_name)
	private EditText addBankName;
	@ViewInject(R.id.djpay_addbank_banknum)
	private EditText addBankBankNum;
	@ViewInject(R.id.djpay_addbank_idcard)
	private EditText addBankBankidcard;
	@ViewInject(R.id.djpay_addbank_phone)
	private EditText addBankPhone;
	@ViewInject(R.id.djpay_addbank_code)
	private EditText addBankCode;
	@ViewInject(R.id.djpay_addbank_getcode)
	private TextView addBankGetcode;
	@ViewInject(R.id.djpay_addbank_bind)
	private TextView addBankBind;
	private CountDownTimer countDownTimer;

	private int from;
	private double amount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.djpay_bank_add_act);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle("添加银行卡");
		addBankGetcode.setOnClickListener(this);
		addBankBind.setOnClickListener(this);
		selectBtn(BTN_CODE_ACCOUNT);
		InputUtil.editIsEmpty(addBankBind, addBankName, addBankBankNum, addBankBankidcard, addBankPhone);
		initCountDownTimer();
		from = getIntent().getIntExtra("from", 0);
		amount = getIntent().getDoubleExtra(SelectChannelActivity.EXTRA_AMOUNT,0);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			/*case R.id.djpay_addbank_getcode:
				if (!StringUtil.isEmpty(addBankPhone.getText().toString())) {
					getCode();
					countDownTimer.start();
				} else {
					ToastUtil.shortShow(this, "请输入手机号");
				}
				break;*/
			case R.id.djpay_addbank_bind:
				bindBank();
				break;
		}
	}

	private void bindBank() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", DJUserCenter.getToken(this));
		params.put("dj", "");
		params.put("phone", addBankPhone.getText().toString());
		params.put("cardNo", addBankBankNum.getText().toString());
		params.put("idNo", addBankBankidcard.getText().toString());
		params.put("messageCode", "");
		params.put("trueName", addBankName.getText().toString());
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BINDCARD , REQUEST_NET_BINDBANK, FProtocol.HttpMethod.POST, params);
	}

	private void initCountDownTimer() {
		countDownTimer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				long time = millisUntilFinished / 1000;
				addBankGetcode.setEnabled(false);
				addBankGetcode.setText(String.format(getString(R.string.person_verification_code_time), String.valueOf(time)));
			}

			@Override
			public void onFinish() {
				addBankGetcode.setEnabled(true);
				addBankGetcode.setText(getString(R.string.login_get_signcode_again));
			}
		};
	}

	public void getCode() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", DJUserCenter.getToken(this));
		params.put("dj", "");
		params.put("userPhone", addBankPhone.getText().toString());
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_GET_VERIFICATION_CODE , REQUEST_NET_GETCODE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		closeProgressDialog();
		switch (requestCode) {
			case REQUEST_NET_GETCODE:
				ToastUtil.shortShow(this,"验证码已发送，请注意查收");
				break;
			case REQUEST_NET_BINDBANK:
				Entity entity = Parsers.getResponseSatus(data);
				String rspCode = entity.getResultCode();
				if (!"000".equals(rspCode)) {
					showAlertDialog("", entity.getResultMsg(), "确定", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							closeDialog();
						}
					});
				} else {
					DJUserCenter.setBankCard(this,true);
					if (1 == from) {
						startActivity(new Intent(AddBankActivity.this, BankCardActivity.class));
					} else if (2 == from) {
						setResult(RESULT_OK);
						finish();
					} else {
						startActivity(new Intent(AddBankActivity.this, SelectChannelActivity.class).putExtra(SelectChannelActivity.EXTRA_AMOUNT,amount));
					}
					setResult(RESULT_OK);
					finish();
				}
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
	}
}
