package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.pay.controller.PayVerifyPWDActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by Liu_Zhichao on 2015/12/8 15:20.
 * 输入原手机号
 */
public class InputOldPhoneActivity extends ToolBarActivity implements View.OnClickListener{

	@ViewInject(R.id.person_phone_input)
	private EditText personPhoneInput;
	@ViewInject(R.id.person_phone_next)
	private TextView personPhoneNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_old_phone_act);
		ExitManager.instance.addChangePhoneAct(this);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.input_old_phone_num));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(InputOldPhoneActivity.this, personPhoneInput);
			}
		}, 100);
		InputUtil.editIsEmpty(personPhoneNext,personPhoneInput);
		personPhoneNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.person_phone_next:
				String mobile = personPhoneInput.getText().toString();
				if (TextUtils.isEmpty(mobile)){
					ToastUtil.shortShow(this, res.getString(R.string.input_old_phone_num));
					return;
				}else if (11 != mobile.length() || !mobile.startsWith("1")){
					ToastUtil.shortShow(this, res.getString(R.string.login_please_right_phone));
					return;
				} else {
					Intent intent = new Intent(this, PayVerifyPWDActivity.class);
					intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.INPUT_PHONE);
					intent.putExtra("mobile", mobile);
					startActivity(intent);
				}
				break;
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
