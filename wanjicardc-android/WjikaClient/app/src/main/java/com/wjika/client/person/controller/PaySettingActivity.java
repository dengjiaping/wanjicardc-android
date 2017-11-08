package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.pay.controller.PayVerifyPWDActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by Liu_ZhiChao on 2015/9/7 20:37.
 * 安全设置
 */
public class PaySettingActivity extends ToolBarActivity implements View.OnClickListener {

	private static final int REQUEST_VERIFYPEW_CODE = 0x1;
	private static final int REQUEST_VERIFY_PWD_CODE = 0x2;
	private static final int REQUEST_ALTER_PHONE_CODE = 0x3;
	private static final int REQUEST_LOGIN_CODE = 0x4;
	private static final int REQUEST_SET_QUESTION_CODE = 0x5;

	@ViewInject(R.id.person_pay_setting_nopwd_selector)
	private TextView personPaySettingNopwdSelector;
	@ViewInject(R.id.setting_alter_find_pay_pwd)
	private LinearLayout settingAlterFindPayPwd;
	@ViewInject(R.id.person_pay_setting_account_question)
	private LinearLayout personPaySettingAccountQuestion;
	@ViewInject(R.id.person_pay_setting_binding_phone)
	private LinearLayout personPaySettingBindingPhone;
	@ViewInject(R.id.person_pay_setting_phone)
	private TextView personPaySettingPhone;
	@ViewInject(R.id.person_pay_setting_question)
	private TextView personPaySettingQuestion;

	private UserEntity userEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitManager.instance.addPayPwdActivity(this);
		setContentView(R.layout.person_act_pay_setting);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.person_pay_pwd_setting));
		userEntity = getIntent().getParcelableExtra(PersonalFragment.USER_INFO);
		if (userEntity != null) {
			hidePhone(userEntity.getPhone());
		}
		personPaySettingNopwdSelector.setOnClickListener(this);
		personPaySettingNopwdSelector.setSelected(UserCenter.issetNopwd(this));
		settingAlterFindPayPwd.setOnClickListener(this);
		personPaySettingAccountQuestion.setOnClickListener(this);
		personPaySettingBindingPhone.setOnClickListener(this);
	}

	public void setLeftTitle(String title) {
		mTitleLeft.setVisibility(View.VISIBLE);
		mTitleLeft.setText(title);
		toolbarTitleCenter.setVisibility(View.GONE);
		mBtnTitleLeft.setVisibility(View.VISIBLE);
		mBtnTitleLeft.setOnClickListener(this);
	}

	private void hidePhone(String userPhone) {
		if (!TextUtils.isEmpty(userPhone) && 11 == userPhone.length()) {
			String head = userPhone.substring(0, 3);
			String end = userPhone.substring(7);
			personPaySettingPhone.setText(head + "****" + end);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (UserCenter.isSetSecurity(this)) {
			personPaySettingQuestion.setText(res.getString(R.string.pay_setting_has_been_set));
			personPaySettingQuestion.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else {
			personPaySettingQuestion.setText(res.getString(R.string.person_info_is_not_set));
			personPaySettingQuestion.setTextColor(res.getColor(R.color.wjika_client_price_red));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_button:
				setResult(RESULT_OK, new Intent().putExtra(PersonalFragment.USER_INFO, userEntity));
				finish();
				break;
			case R.id.person_pay_setting_nopwd_selector:
				if (personPaySettingNopwdSelector.isSelected()) {
					showAlertDialog(null,
							res.getString(R.string.pay_setting_shut_down_no_pay),
							res.getString(R.string.wjika_cancel),
							res.getString(R.string.person_confirm),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									closeDialog();
								}
							},
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									UserCenter.setUserSetNopwd(PaySettingActivity.this, false);
									personPaySettingNopwdSelector.setSelected(false);
									ToastUtil.shortShow(PaySettingActivity.this, getString(R.string.person_pay_nopwd_closed));
									closeDialog();
								}
							});
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setMessage(res.getString(R.string.pay_setting_shut_down_no_pay));
//					builder.setNegativeButton(res.getString(R.string.verify_safe_cancel), null);
//					builder.setPositiveButton(res.getString(R.string.person_confirm), new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//
//							dialog.dismiss();
//						}
//					});
//					AlertDialog alertDialog = builder.create();
//					alertDialog.show();
				} else {
					Intent i = new Intent(this, PayVerifyPWDActivity.class);
					i.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.ALTER_PAY_PWD);
					startActivityForResult(i, REQUEST_VERIFYPEW_CODE);
				}
				break;
			case R.id.setting_alter_find_pay_pwd:
				Intent i = new Intent(this, PayVerifyPWDActivity.class);
				i.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.ALTER_PAY_PWD);
				startActivityForResult(i, REQUEST_VERIFY_PWD_CODE);
				break;
			case R.id.person_pay_setting_account_question:
				Intent questionIntent = new Intent(this, PayVerifyPWDActivity.class);
				questionIntent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.ALTER_PAY_PWD);
				startActivityForResult(questionIntent, REQUEST_SET_QUESTION_CODE);
				break;
			case R.id.person_pay_setting_binding_phone:
				Intent intent = new Intent(this, PayVerifyPWDActivity.class);
				intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.SETTING);
				startActivityForResult(intent, REQUEST_ALTER_PHONE_CODE);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_VERIFYPEW_CODE: {
					UserCenter.setUserSetNopwd(this, true);
					personPaySettingNopwdSelector.setSelected(true);
					ToastUtil.shortShow(this, res.getString(R.string.pay_setting_payment_function_open));
				}
				break;
				case REQUEST_VERIFY_PWD_CODE: {
					Intent intent = new Intent(this, PayPasswordActivity.class);
					intent.putExtra(PayPasswordActivity.FROM_EXTRA, PayPasswordActivity.FIND_PASSWORD_RESET);
					startActivity(intent);
				}
				break;
				case REQUEST_ALTER_PHONE_CODE:
					Intent loginIntent = new Intent(this, LoginActivity.class);
					loginIntent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_SETTING_ALTER_PHONE);
					startActivityForResult(loginIntent, REQUEST_LOGIN_CODE);
					break;
				case REQUEST_LOGIN_CODE:
					String phone = data.getStringExtra("phone");
					if (userEntity != null && !TextUtils.isEmpty(phone)) {
						userEntity.setPhone(phone);
						hidePhone(phone);
					}
					break;
				case REQUEST_SET_QUESTION_CODE:
					Intent accountQuestionIntent = new Intent(this, AccountSafeQuestionActivity.class);
					accountQuestionIntent.putExtra("isSetQuestion", UserCenter.isSetSecurity(this));
					startActivity(accountQuestionIntent);
					break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, new Intent().putExtra(PersonalFragment.USER_INFO, userEntity));
		finish();
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
