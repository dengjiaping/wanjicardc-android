package com.wjika.client.pay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.person.controller.OrderListActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by Liu_Zhichao on 2016/8/19 16:24.
 * 电子卡支付成功界面
 */
public class ECardPayResultActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_ECARD = "extra_ecard";
	public static final String EXTRA_NUM = "extra_num";
	public static final String EXTRA_ECARD_NAME = "ecard_name";
	public static final String EXTRA_ECARD_FACE_VALUE = "ecard_face_value";
	public static final String EXTRA_ECARD_SALE_VALE = "ecard_sale_value";
	public static final String EXTRA_FROM = "extra_from";
	public static final String EXTRA_PAYCHANNEL = "extra_paychannel";

	@ViewInject(R.id.ecard_pay_name)
	private TextView ecardPayName;
	@ViewInject(R.id.ecard_pay_value)
	private TextView ecardPayValue;
	@ViewInject(R.id.ecard_pay_num)
	private TextView ecardPayNum;
	@ViewInject(R.id.ecard_pay_price)
	private TextView ecardPayPrice;
	@ViewInject(R.id.ecard_pay_goto_market)
	private View ecardPayGotoMarket;
	@ViewInject(R.id.ecard_pay_baozi_img)
	private ImageView baoziImg;

	private int flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_ecard_pay_result);
		ExitManager.instance.addECardActivity(this);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_order_pay_success));
		int from = getIntent().getIntExtra(EXTRA_FROM, 0);
		flag = getIntent().getIntExtra(ECardDetailActivity.EXTRA_FLAG, 0);
		ECardEntity eCard = getIntent().getParcelableExtra(EXTRA_ECARD);
		int num = getIntent().getIntExtra(EXTRA_NUM, 1);
		if (from == OrderListActivity.FROM_ORDER_LIST) {
			if (getIntent() != null) {
				String orderListFaceValue = getIntent().getStringExtra(EXTRA_ECARD_FACE_VALUE);
				String orderListName = getIntent().getStringExtra(EXTRA_ECARD_NAME);
				String orderListSaleValue = getIntent().getStringExtra(EXTRA_ECARD_SALE_VALE);
				ecardPayName.setText(orderListName);
				baoziImg.setVisibility(View.VISIBLE);
				ecardPayValue.setText(getString(R.string.buy_card_face_value, orderListFaceValue));
				ecardPayPrice.setTextColor(getResources().getColor(R.color.person_main_baozi_num));
				ecardPayPrice.setText(NumberFormatUtil.formatBun(Double.parseDouble(orderListSaleValue) * num));
			}
		} else if (from == ECardBuyActivity.FROM_ECARD_BUY) {
			if (eCard != null) {
				String payChannel = getIntent().getStringExtra(ECardPayResultActivity.EXTRA_PAYCHANNEL);
				if (ECardBuyActivity.BAOZI_CHANNEL.equals(payChannel)) {
					ecardPayName.setText(eCard.getName());
					baoziImg.setVisibility(View.VISIBLE);
					ecardPayValue.setVisibility(View.VISIBLE);
					ecardPayValue.setText(getString(R.string.wjika_client_selling_price2, String.valueOf(eCard.getRMBSalePrice())));
					ecardPayPrice.setTextColor(getResources().getColor(R.color.person_main_baozi_num));
					ecardPayPrice.setText(NumberFormatUtil.formatBun(eCard.getSalePrice() * num));
				} else {
					ecardPayName.setText(eCard.getName());
					baoziImg.setVisibility(View.GONE);
					ecardPayValue.setVisibility(View.GONE);
					ecardPayPrice.setTextColor(getResources().getColor(R.color.card_store_price));
					ecardPayPrice.setText(getString(R.string.person_order_detail_buy_amount,String.valueOf(eCard.getRMBSalePrice())));
				}
			}
		} else {//包子支付页面跳转
			if (eCard != null) {
				ecardPayName.setText(eCard.getName());
				baoziImg.setVisibility(View.VISIBLE);
				ecardPayValue.setVisibility(View.VISIBLE);
				ecardPayValue.setText(getString(R.string.wjika_client_selling_price2, String.valueOf(eCard.getRMBSalePrice())));
				ecardPayPrice.setTextColor(getResources().getColor(R.color.person_main_baozi_num));
				ecardPayPrice.setText(NumberFormatUtil.formatBun(eCard.getSalePrice() * num));
			}
		}
		ecardPayNum.setText(getString(R.string.person_order_detail_buy_num, String.valueOf(num)));

		ecardPayGotoMarket.setOnClickListener(this);

		mBtnTitleLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ecard_pay_goto_market:
				if (ECardDetailActivity.FLAG_CARD_STORE == flag){
					//去包子商城，跳到包子商城tab
					Intent cardIntent = new Intent(this, MainActivity.class);
					cardIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_CARD);
					startActivity(cardIntent);
				} else {
					//去包子商城，跳到包子商城tab
					Intent marketIntent = new Intent(this, MainActivity.class);
					marketIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_MARKET);
					startActivity(marketIntent);
				}
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_OK);
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		ExitManager.instance.toActivity(MainActivity.class);
	}
}
