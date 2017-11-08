package com.wjika.client.djpay.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.utils.DeviceUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.djpay.controller.AccountActivity;
import com.wjika.client.djpay.controller.PaymentActivity;
import com.wjika.client.djpay.controller.RateActivity;
import com.wjika.client.utils.ExitManager;

/**
 * Created by liuzhichao on 2016/11/22.
 */

public class BaseDJActivity extends ToolBarActivity {

	public static final int BTN_CODE_PAYMENT = 1;
	public static final int BTN_CODE_RATE = 2;
	public static final int BTN_CODE_ACCOUNT = 3;

	private ViewGroup root;
	protected View tabbar;
	private TextView tvTabPayment;
	private TextView tvTabRate;
	private TextView tvTabAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitManager.instance.addDJActivity(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(R.layout.base_tabbar_frame);
		root = (ViewGroup) findViewById(R.id.frame_container);
		View.inflate(this, layoutResID, root);
		initTabbar();
	}

	private void initTabbar() {
		tabbar = findViewById(R.id.frame_tabbar);
		tvTabPayment = (TextView) findViewById(R.id.tv_tab_payment);
		tvTabRate = (TextView) findViewById(R.id.tv_tab_rate);
		tvTabAccount = (TextView) findViewById(R.id.tv_tab_account);
		BTNOnClickListener btnOnClickListener = new BTNOnClickListener();
		tvTabPayment.setOnClickListener(btnOnClickListener);
		tvTabRate.setOnClickListener(btnOnClickListener);
		tvTabAccount.setOnClickListener(btnOnClickListener);
	}

	@Override
	public void setLeftTitle(String title) {
		super.setLeftTitle(title);
		showRightBtn();
	}

	public void setClickCenterTitle(String title) {
		mBtnTitleLeft.setVisibility(View.VISIBLE);
		mBtnTitleLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTitleLeft.setVisibility(View.GONE);
		toolbarTitleCenter.setVisibility(View.VISIBLE);
		toolbarTitleCenter.setText(title);
		toolbarTitleCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.home_btn_location, 0);
		toolbarTitleCenter.setCompoundDrawablePadding(DeviceUtil.dp_to_px(this, 10));
		showRightBtn();
	}

	private void showRightBtn() {
		mBtnTitleRight.setVisibility(View.VISIBLE);
		mBtnTitleRight.setImageResource(R.drawable.djpay_close);
		mBtnTitleRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ExitManager.instance.closeDJActivity();
			}
		});
	}

	public void showBtn(boolean isShow) {
		if (isShow) {
			tabbar.setVisibility(View.VISIBLE);
		} else {
			tabbar.setVisibility(View.GONE);
		}
	}

	public void selectBtn(int btnCode) {
		switch (btnCode) {
			case BTN_CODE_PAYMENT:
				tvTabPayment.setOnClickListener(null);
				tvTabPayment.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
				break;
			case BTN_CODE_RATE:
				tvTabRate.setOnClickListener(null);
				tvTabRate.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
				break;
			case BTN_CODE_ACCOUNT:
				tvTabAccount.setOnClickListener(null);
				tvTabAccount.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
				break;
		}
	}

	private class BTNOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.tv_tab_payment:
					startActivity(new Intent(BaseDJActivity.this, PaymentActivity.class));
					break;
				case R.id.tv_tab_rate:
					startActivity(new Intent(BaseDJActivity.this, RateActivity.class));
					break;
				case R.id.tv_tab_account:
					startActivity(new Intent(BaseDJActivity.this, AccountActivity.class));
					break;
			}
		}
	}
}
