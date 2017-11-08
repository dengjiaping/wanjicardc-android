package com.wjika.client.market.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.ECardOrderEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.ECardBuyActivity;
import com.wjika.client.pay.controller.ECardPayResultActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.person.controller.PayDialogActivity;
import com.wjika.client.person.controller.RechargeDialogActivity;
import com.wjika.client.utils.CommonShareUtil;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_Zhichao on 2016/8/17 14:36.
 * 电子卡详情
 */
public class ECardDetailActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_ECARD_ID = "extra_ecard_id";
	public static final String EXTRA_FLAG = "extra_flag";
	public static final String EXTRA_BUY = "extra_buy";
	public static final String EXTRA_NUM = "extra_num";
	private static final int REQUEST_NET_ECARD_DEATIL = 10;
	private static final int REQUEST_NET_ECARD_ORDER = 20;
	private static final int REQUEST_NET_ECARD_PAY = 30;
	private static final int REQUEST_ACT_PAY = 40;
	private static final int DEFAULT_BUY_NUM = 1;
	private static final int MAX_BUY_NUM = 200;
	public static final int FLAG_CARD_STORE = 0x1;//卡商城
	public static int flag;

	@ViewInject(R.id.ecard_detail_card)
	private CardView ecardDetailCard;
	@ViewInject(R.id.ecard_detail_logo)
	private SimpleDraweeView ecardDetailLogo;
	@ViewInject(R.id.ecard_detail_tag)
	private View ecardDetailTag;
	@ViewInject(R.id.ecard_detail_value)
	private TextView ecardDetailValue;
	@ViewInject(R.id.ecard_detail_name)
	private TextView ecardDetailName;
	@ViewInject(R.id.ecard_detail_ad)
	private TextView ecardDetailAd;
	@ViewInject(R.id.ecard_detail_price)
	private TextView ecardDetailPrice;
	@ViewInject(R.id.ecard_detail_discount)
	private TextView ecardDetailDiscount;
	@ViewInject(R.id.ecard_detail_limit)
	private TextView ecardDetailLimit;
	@ViewInject(R.id.ecard_detail_introduce)
	private WebView ecardDetailIntroduce;
	@ViewInject(R.id.ecard_detail_minus)
	private View ecardDetailMinus;
	@ViewInject(R.id.ecard_detail_buy_num)
	private EditText ecardDetailBuyNum;
	@ViewInject(R.id.ecard_detail_plus)
	private View ecardDetailPlus;
	@ViewInject(R.id.ecard_detail_buy)
	private TextView ecardDetailBuy;

	private String eCardId;
	private ECardEntity eCard;
	private int num = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_ecard_detail);
		ExitManager.instance.addECardActivity(this);
		ViewInjectUtils.inject(this);
		initView();
		initLoadingView(this);
	}

	private void initView() {
		String title = getIntent().getStringExtra(EXTRA_TITLE);
		eCardId = getIntent().getStringExtra(EXTRA_ECARD_ID);
		if (TextUtils.isEmpty(title)) {
			setLeftTitle(getString(R.string.ecard_detail_title));
		} else {
			setLeftTitle(title);
		}
		flag = getIntent().getIntExtra(EXTRA_FLAG, 0);
		ecardDetailBuyNum.setSelection(ecardDetailBuyNum.length());
		ecardDetailValue.getPaint().setFakeBoldText(true);
		ecardDetailIntroduce.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("wjika.com")) {
					ecardDetailIntroduce.loadUrl(url);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		mBtnTitleRight.setVisibility(View.VISIBLE);
		mBtnTitleRight.setOnClickListener(this);
		ecardDetailMinus.setOnClickListener(this);
		ecardDetailPlus.setOnClickListener(this);
		ecardDetailBuy.setOnClickListener(this);
	}

	private void loadData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("thirdCardId", eCardId);
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_POST_ECARD_DETAIL, REQUEST_NET_ECARD_DEATIL, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_EcardDetail");
		setLoadingStatus(LoadingStatus.LOADING);
		loadData();
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_NET_ECARD_DEATIL:
				eCard = Parsers.getECard(data);
				if (eCard != null) {
					if (!StringUtil.isEmpty(eCard.getBgcolor())) {
						ecardDetailCard.setCardBackgroundColor(Color.parseColor(eCard.getBgcolor()));
					} else {
						ecardDetailCard.setCardBackgroundColor(Color.parseColor("#487AE0"));
					}
					ImageUtils.setSmallImg(ecardDetailLogo, eCard.getLogoUrl());
					ecardDetailValue.setText(getString(R.string.money, String.valueOf(eCard.getFacePrice())));
					ecardDetailName.setText(eCard.getName());
					ecardDetailAd.setText(eCard.getAd());
					if (FLAG_CARD_STORE == flag) {
						ecardDetailPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
						ecardDetailPrice.setText(getString(R.string.person_order_detail_buy_amount, NumberFormatUtil.formatMoney(eCard.getRMBSalePrice())));
						ecardDetailPrice.setTextColor(getResources().getColor(R.color.card_store_price));
						ecardDetailDiscount.setText(eCard.getDiscount());
					} else {
						ecardDetailPrice.setText(NumberFormatUtil.formatBun(eCard.getSalePrice()));
					}
					if (ECardEntity.ACTIVITY_TYPE_LIMIT == eCard.getActivityType()) {
						ecardDetailTag.setVisibility(View.VISIBLE);
						ecardDetailLimit.setText(getString(R.string.ecard_limit, String.valueOf(eCard.getLimitCount())));
					}
					ecardDetailIntroduce.loadUrl(eCard.getUseRule());
					if (eCard.getStock() <= 0) {
						ecardDetailBuy.setText(getString(R.string.ecard_detail_none));
						ecardDetailBuy.setEnabled(false);
						ecardDetailMinus.setEnabled(false);
						ecardDetailBuyNum.setEnabled(false);
						ecardDetailPlus.setEnabled(false);
					} else {
						ecardDetailBuy.setEnabled(true);
						ecardDetailMinus.setEnabled(true);
						ecardDetailBuyNum.setEnabled(true);
						ecardDetailPlus.setEnabled(true);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			case REQUEST_NET_ECARD_ORDER:
				ECardOrderEntity eCardOrder = Parsers.getECardOrder(data);
				if (eCardOrder != null) {
					String orderNo = eCardOrder.getOrderNo();
					if (num * eCard.getSalePrice() > eCardOrder.getBalance()) {
						Intent rechargeIntent = new Intent(this, RechargeDialogActivity.class);
						rechargeIntent.putExtra("from", "eCardDetail");
						rechargeIntent.putExtra("eCard", eCard);
						rechargeIntent.putExtra("eCardNo", orderNo);
						rechargeIntent.putExtra("eCardNum", num);
						rechargeIntent.putExtra("walletCount", eCardOrder.getBalance());
						startActivity(rechargeIntent);
					} else {
						Intent payIntent = new Intent(this, PayDialogActivity.class);
						payIntent.putExtra("from", "eCardDetail");
						payIntent.putExtra("eCard", eCard);
						payIntent.putExtra("eCardNo", orderNo);
						payIntent.putExtra("eCardNum", num);
						payIntent.putExtra("walletCount", eCardOrder.getBalance());
						startActivity(payIntent);
					}
				}
				break;
			case REQUEST_NET_ECARD_PAY: {
				Intent intent = new Intent(this, ECardPayResultActivity.class);
				intent.putExtra(ECardPayResultActivity.EXTRA_ECARD, eCard);
				intent.putExtra(ECardPayResultActivity.EXTRA_NUM, num);
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (ecardDialogPwd != null) {
			ecardDialogPwd.getText().clear();
		}
		switch (requestCode) {
			case REQUEST_NET_ECARD_DEATIL:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.right_button://分享
				if (eCard != null) {
					CommonShareUtil.share(this, eCard.getDesc(), eCard.getTitle(), eCard.getLogoUrl(), eCard.getUrl());
				}
				break;
			case R.id.ecard_detail_minus: {
				String content = ecardDetailBuyNum.getText().toString();
				if (TextUtils.isEmpty(content)) {
					reset();
					return;
				}
				int buyNum = Integer.valueOf(ecardDetailBuyNum.getText().toString());
				if (buyNum <= DEFAULT_BUY_NUM) {
					ToastUtil.shortShow(this, getString(R.string.ecard_detail_min_buy, String.valueOf(DEFAULT_BUY_NUM)));
					return;
				}
				num = --buyNum;
				ecardDetailBuyNum.setText(String.valueOf(num));
				ecardDetailBuyNum.setSelection(ecardDetailBuyNum.length());
				break;
			}
			case R.id.ecard_detail_plus: {
				String content = ecardDetailBuyNum.getText().toString();
				if (TextUtils.isEmpty(content)) {
					reset();
					return;
				}
				int buyNum = Integer.valueOf(content);
				if (buyNum >= MAX_BUY_NUM) {
					ToastUtil.shortShow(this, getString(R.string.ecard_detail_max_buy, String.valueOf(MAX_BUY_NUM)));
					return;
				}
				if (eCard != null) {
					if (buyNum >= eCard.getStock()) {
						ToastUtil.shortShow(this, getString(R.string.ecard_detail_stock_none));
						return;
					}
					if (ECardEntity.ACTIVITY_TYPE_LIMIT == eCard.getActivityType() && buyNum >= eCard.getCanBuyNum()) {
						ToastUtil.shortShow(this, getString(R.string.ecard_limit_hint, String.valueOf(eCard.getCanBuyNum())));
						return;
					}
				}
				num = ++buyNum;
				ecardDetailBuyNum.setText(String.valueOf(num));
				ecardDetailBuyNum.setSelection(ecardDetailBuyNum.length());
				break;
			}
			case R.id.ecard_detail_buy: {
				if (UserCenter.isLogin(this)) {
					if (TextUtils.isEmpty(ecardDetailBuyNum.getText().toString())) {
						reset();
					}
					Integer buyNum = Integer.valueOf(ecardDetailBuyNum.getText().toString());
					if (buyNum < DEFAULT_BUY_NUM) {
						ToastUtil.shortShow(this, getString(R.string.ecard_detail_not_zero));
						return;
					}
					if (buyNum > MAX_BUY_NUM) {
						ToastUtil.shortShow(this, getString(R.string.ecard_detail_max_buy, String.valueOf(MAX_BUY_NUM)));
						return;
					}
					if (eCard != null) {
						if (buyNum > eCard.getStock()) {
							ToastUtil.shortShow(this, getString(R.string.ecard_detail_no_stock));
							return;
						}
						if (ECardEntity.ACTIVITY_TYPE_LIMIT == eCard.getActivityType() && buyNum > eCard.getCanBuyNum()) {
							ToastUtil.shortShow(this, getString(R.string.ecard_limit_hint, String.valueOf(eCard.getCanBuyNum())));
							return;
						}
					}
					num = buyNum;
					if (FLAG_CARD_STORE == flag) {
						if (eCard != null) {
							Intent intent = new Intent(this, ECardBuyActivity.class);
							intent.putExtra(EXTRA_FLAG, flag);
							intent.putExtra(EXTRA_BUY, eCard);
							intent.putExtra(EXTRA_NUM, num);
							startActivityForResult(intent, REQUEST_ACT_PAY);
						}
					} else {
						if (UserCenter.isAuthencaiton(this)) {
							showProgressDialog();
							IdentityHashMap<String, String> params = new IdentityHashMap<>();
							params.put("thirdCardId", eCardId);
							params.put("purchaseNum", buyNum.toString());
							params.put("channelId", ECardBuyActivity.BAOZI_CHANNEL);
							params.put("orderNo", "");
							params.put(Constants.TOKEN, UserCenter.getToken(this));
							requestHttpData(Constants.Urls.URL_POST_CREATE_ECARD_ORDER, REQUEST_NET_ECARD_ORDER, FProtocol.HttpMethod.POST, params);
						} else {
							startActivity(new Intent(this, AuthenticationActivity.class));
						}
					}
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				break;
			}
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData();
				break;
		}
	}

	private void reset() {
		String defaultNum = String.valueOf(DEFAULT_BUY_NUM);
		ecardDetailBuyNum.setText(defaultNum);
		ecardDetailBuyNum.setSelection(defaultNum.length());
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		closeDialog();
		closeProgressDialog();
		ExitManager.instance.closeECardActivity();
	}
}
