package com.wjika.client.pay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.common.utils.AnalysisUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by bob on 2016/8/26.
 * 包子支付结果界面
 */
public class BaoziPayResultActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_REAL_PAY = "extra_real_pay";
	public static final String EXTRA_BAOZI_DESCRIBE = "extra_baozi_describe";
	public static final String EXTRA_NEW_HINT_INFO = "extra_new_hint_info";

	@ViewInject(R.id.pay_result_title)
	private TextView payResultTitle;
	@ViewInject(R.id.tv_baozi_describe)
	private TextView tvBaoziDescribe;
	@ViewInject(R.id.tv_real_pay)
	private TextView tvRealPay;
	@ViewInject(R.id.tv_hintinfo)
	private TextView tvHintInfo;
	@ViewInject(R.id.pay_result_left_btn)
	private Button payResultLeftBtn;
	@ViewInject(R.id.pay_result_right_btn)
	private Button payResultRightBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_baozi_pay_result);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.card_charge_sucess));
		Intent extraIntent = getIntent();
		String describe = extraIntent.getStringExtra(EXTRA_BAOZI_DESCRIBE);
		String realPay = extraIntent.getStringExtra(EXTRA_REAL_PAY);
		String hintInfo = extraIntent.getStringExtra(EXTRA_NEW_HINT_INFO);
		payResultTitle.setText(getString(R.string.card_charge_sucess));
		tvRealPay.setText("实付：￥" + NumberFormatUtil.formatMoney(realPay));
		tvBaoziDescribe.setText("到账：" + describe);

		//新老用户是否赠送优惠提示信息
		if (StringUtil.isEmpty(hintInfo)) {
			tvHintInfo.setVisibility(View.GONE);
		} else {
			tvHintInfo.setText(hintInfo);
			tvHintInfo.setVisibility(View.VISIBLE);
		}

		payResultLeftBtn.setOnClickListener(this);
		payResultRightBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pay_result_left_btn:
				AnalysisUtil.onEvent(this, "Android_act_baozi_recharge");
				//去包子商城，跳到包子商城tab
				Intent marketIntent = new Intent(this, MainActivity.class);
				marketIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_MARKET);
				startActivity(marketIntent);
				break;
			case R.id.pay_result_right_btn:
				finish();
				break;
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
