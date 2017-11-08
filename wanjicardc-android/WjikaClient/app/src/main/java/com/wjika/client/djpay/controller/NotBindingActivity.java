package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.base.BaseDJActivity;

/**
 * Created by liuzhichao on 2016/11/23.
 * 斗金-未绑卡界面
 */
public class NotBindingActivity extends BaseDJActivity {

	private static final int RESPONSE_TOADD = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_not_binding);
		initView();
	}

	private void initView() {
		final int from = getIntent().getIntExtra("from", 0);
		if (1 == from) {
			setLeftTitle("我的银行卡");
			selectBtn(BTN_CODE_ACCOUNT);
		} else {
			setLeftTitle("收款");
			selectBtn(BTN_CODE_PAYMENT);
		}
		findViewById(R.id.goto_binding).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NotBindingActivity.this, AddBankActivity.class);
				intent.putExtra("from", from);
				intent.putExtra(SelectChannelActivity.EXTRA_AMOUNT, getIntent().getDoubleExtra(SelectChannelActivity.EXTRA_AMOUNT, 0));
				startActivityForResult(intent, RESPONSE_TOADD);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case RESPONSE_TOADD:
					finish();
					break;
			}
		}
	}
}
