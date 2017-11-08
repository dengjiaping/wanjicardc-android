package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DJPaymentEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.MoneyFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by liuzhichao on 2016/11/22.
 * 斗金-收款
 */
public class PaymentActivity extends BaseDJActivity implements View.OnClickListener {

	private static final int REQUSET_ACT_LOGIN = 1;
	private static final int REQUEST_NET_PAYMENT = 2;

	@ViewInject(R.id.payment_amount)
	private EditText paymentAmount;
	@ViewInject(R.id.payment_hint)
	private TextView paymentHint;
	@ViewInject(R.id.payment_confirm)
	private TextView paymentConfirm;
	@ViewInject(R.id.payment_declaration)
	private TextView paymentDeclaration;

	private double minQuota;
	private double maxQuota;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_payment);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle(getString(R.string.payment_title));
		selectBtn(BTN_CODE_PAYMENT);
		MoneyFormatUtil.addTextWatcher(paymentAmount);
		InputUtil.editIsEmpty(paymentConfirm, paymentAmount);
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("dj", "");
		params.put("token", "");
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_PAYMENT, REQUEST_NET_PAYMENT, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		DJPaymentEntity djPayment = Parsers.getDJPayment(data);
		if (djPayment != null) {
			paymentHint.setText(djPayment.getHint());
			paymentDeclaration.setText(djPayment.getDeclaration());
			minQuota = djPayment.getMinQuota();
			maxQuota = djPayment.getMaxQuota();
			paymentConfirm.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.payment_confirm:
				if (DJUserCenter.isLogin(this)) {
					String txt = paymentAmount.getText().toString();
					double amount;
					if (!TextUtils.isEmpty(txt)) {
						amount = Double.parseDouble(txt);
					} else {
						amount = 0;
					}
					if (amount < minQuota) {
						ToastUtil.shortShow(this, "收款金额不能小于最低限额");
						return;
					}
					if (amount > maxQuota) {
						ToastUtil.shortShow(this, "收款金额不能大于最高限额");
						return;
					}
					if (DJUserCenter.haveBankCard(this)) {
						startActivity(new Intent(this, SelectChannelActivity.class).putExtra(SelectChannelActivity.EXTRA_AMOUNT, amount));
					} else {
						startActivity(new Intent(this, NotBindingActivity.class).putExtra(SelectChannelActivity.EXTRA_AMOUNT, amount));
					}
				} else {
					startActivityForResult(new Intent(this, DJLoginActivity.class), REQUSET_ACT_LOGIN);
				}
				break;
		}
	}
}
