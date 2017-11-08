package com.wjika.client.pay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by jacktian on 15/9/21.
 * 消费结果页
 */
public class ConsumeResultActivity extends ToolBarActivity implements View.OnClickListener {

	public final static String EXTRA_ORDERNO = "extra_orderno";
	public final static String EXTRA_AMOUNT = "extra_amount";
	public final static String EXTRA_FROM = "extra_from";
	public final static int FROM_PUSH = 1;

	@ViewInject(R.id.pay_result_business)
	private View payResultBusiness;
	@ViewInject(R.id.pay_result_amount)
	private View payResultAmount;
	@ViewInject(R.id.consume_result)
	private TextView mConsumeResult;
	@ViewInject(R.id.pay_result_orderno)
	private TextView mResultOrderNo;
	@ViewInject(R.id.pay_result_consume_amount)
	private TextView mResultConsumeAmount;
	@ViewInject(R.id.pay_result_close)
	private Button mResultClose;

	private int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_consume_result_act);
		from = getIntent().getIntExtra(EXTRA_FROM, 0);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		mBtnTitleLeft.setVisibility(View.INVISIBLE);
		setCenterTitle("交易结果");
	}

	private void loadData() {
		String orderNo = getIntent().getStringExtra(EXTRA_ORDERNO);
		String amount = getIntent().getStringExtra(EXTRA_AMOUNT);
		if (orderNo == null || amount == null) {
			mConsumeResult.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_failure, 0, 0, 0);
			mConsumeResult.setText("支付失败");
			payResultBusiness.setVisibility(View.GONE);
			payResultAmount.setVisibility(View.GONE);
		} else {
			mResultOrderNo.setText(orderNo);
			mResultConsumeAmount.setText(getString(R.string.person_order_detail_buy_amount, NumberFormatUtil.formatMoney(amount)));
		}

		mResultClose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

	@Override
	public void finish() {
		if (FROM_PUSH == from)
			startActivity(new Intent(this, MainActivity.class));
		super.finish();
	}
}
