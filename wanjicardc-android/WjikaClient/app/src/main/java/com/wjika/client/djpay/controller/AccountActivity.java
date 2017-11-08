package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by zhaoweiwei on 2016/11/22.
 * 斗金-账户
 */
public class AccountActivity extends BaseDJActivity implements View.OnClickListener {

	private static final int EXTRA_FROM = 1;

	@ViewInject(R.id.djpay_account_bankcard)
	private TextView accountBank;
	@ViewInject(R.id.djpay_account_bill)
	private TextView accountBill;
	@ViewInject(R.id.djpay_account_layout)
	private RelativeLayout accountLayout;
	@ViewInject(R.id.djpay_account_name)
	private TextView accountName;
	@ViewInject(R.id.exchange_login_out)
	private TextView accountLoginOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.djpay_account_act);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle("账户");
		accountBank.setOnClickListener(this);
		accountBill.setOnClickListener(this);
		accountLayout.setOnClickListener(this);
		accountLoginOut.setOnClickListener(this);
		selectBtn(BTN_CODE_ACCOUNT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DJUserCenter.isLogin(this)) {
			String tempPhone = DJUserCenter.getPhone(this);
			if (11 == tempPhone.length()) {
				String phone = tempPhone.substring(0,4) + "****" + tempPhone.substring(7);
				accountName.setText(phone);
			} else {
				accountName.setText(tempPhone);
			}
			accountLoginOut.setVisibility(View.VISIBLE);
		} else {
			accountName.setText("尚未登录");
			accountLoginOut.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		if (DJUserCenter.isLogin(this)) {
			switch (view.getId()) {
				case R.id.djpay_account_layout:
					break;
				case R.id.djpay_account_bankcard:
					if (DJUserCenter.haveBankCard(this)) {
						startActivity(new Intent(AccountActivity.this, BankCardActivity.class));
					} else {
						Intent intent = new Intent(AccountActivity.this, NotBindingActivity.class);
						intent.putExtra("from", EXTRA_FROM);
						startActivity(intent);
					}
					break;
				case R.id.djpay_account_bill:
					startActivity(new Intent(AccountActivity.this, BillActivity.class));
					break;
				case R.id.exchange_login_out:
					accountName.setText("尚未登录");
					accountLoginOut.setVisibility(View.GONE);
					DJUserCenter.cleanLoginInfo(this);
					break;
			}
		} else {
			startActivity(new Intent(AccountActivity.this, DJLoginActivity.class));
		}
	}
}
