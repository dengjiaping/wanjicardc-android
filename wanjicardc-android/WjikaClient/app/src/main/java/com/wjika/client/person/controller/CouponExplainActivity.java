package com.wjika.client.person.controller;

import android.os.Bundle;

import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;

/**
 * Created by kkkkk on 2016/6/2.
 * 优惠券说明页
 */
public class CouponExplainActivity extends ToolBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_coupon_note);
		setLeftTitle(getString(R.string.person_coupon_explain));
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
