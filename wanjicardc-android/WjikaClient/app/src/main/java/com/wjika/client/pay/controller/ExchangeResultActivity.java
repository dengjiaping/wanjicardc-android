package com.wjika.client.pay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.market.controller.MyBaoziActivity;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import static com.wjika.client.exchange.controller.ExchangeActivity.EXTRA_FACE_ENTITY;
import static com.wjika.client.exchange.controller.ExchangeActivity.EXTRA_LIST_ENTITY;

/**
 * Created by liuzhichao on 2016/11/29.
 * 卡兑换结果页
 */
public class ExchangeResultActivity extends ToolBarActivity implements View.OnClickListener {

	@ViewInject(R.id.exchange_result_ecard)
	private TextView exchangeResultEcard;
	@ViewInject(R.id.exchange_result_bun)
	private TextView exchangeResultBun;
	@ViewInject(R.id.exchange_result_show)
	private Button exchangeResultShow;
	@ViewInject(R.id.exchange_result_continue)
	private Button exchangeResultContinue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_exchange_result);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle("兑换成功");
		ExchangeCardEntity cardEntity = getIntent().getParcelableExtra(EXTRA_LIST_ENTITY);
		ExchangeFacevalueEntity facevalueEntity = getIntent().getParcelableExtra(EXTRA_FACE_ENTITY);
		if (cardEntity != null && facevalueEntity != null) {
			SpannableString card = new SpannableString("我用   " + cardEntity.getCardName() + NumberFormatUtil.formatBun(facevalueEntity.getFaceValue()) + "元");
			card.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.wjika_client_dark_grey)), 2, card.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			card.setSpan(new AbsoluteSizeSpan(15, true), 2, card.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			exchangeResultEcard.setText(card);
			String bunNum = NumberFormatUtil.formatBun(facevalueEntity.getBunNum());
			SpannableString bun = new SpannableString("兑换：" + bunNum + "个包子  (1元=1包子)");
			bun.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.person_main_baozi_num)), 3, bunNum.length() + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			bun.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.wjika_client_dark_grey)), bun.toString().indexOf("("), bun.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			bun.setSpan(new AbsoluteSizeSpan(15, true), 3, bun.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			exchangeResultBun.setText(bun);
		}
		exchangeResultShow.setOnClickListener(this);
		exchangeResultContinue.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.exchange_result_show:
				startActivity(new Intent(this, MyBaoziActivity.class));
				finish();
				break;
			case R.id.exchange_result_continue:
				finish();
				break;
		}
	}
}
