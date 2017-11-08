package com.wjika.client.pay.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.ECardOrderEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.AuthCodeActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.person.controller.OrderDetailActivity;
import com.wjika.client.person.controller.PayDialogActivity;
import com.wjika.client.person.controller.RechargeDialogActivity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/11/4.
 */

public class ECardBuyActivity extends ToolBarActivity implements View.OnClickListener {
	public static final String ALIPAY_CHANNEL = "pingapp";
	public static final String YILIAN_CHANNEL = "payeco";
	public static final String BAOZI_CHANNEL = "wjkbun";

	private static final int REQUEST_NET_ECARD_ORDER = 0x01;
	public static final int FROM_ECARD_BUY = 0x02;
	private static final int REQUEST_BAOZI_PAY_SECCESS = 0x03;
	private final static String BROADCAST_PAY_END = "com.wjika.client.broadcast";

	@ViewInject(R.id.electron_img_bg)
	private CardView cardView;
	@ViewInject(R.id.electron_card_img_cover)
	private SimpleDraweeView cardImg;
	@ViewInject(R.id.electron_order_item_name)
	private TextView cardName;
	@ViewInject(R.id.electron_order_item_facevalue)
	private TextView cardNum;
	@ViewInject(R.id.electron_order_item_price)
	private TextView cardPrice;

	@ViewInject(R.id.person_recharge_baozipay)
	private RelativeLayout baoziLayout;
	@ViewInject(R.id.person_baozi_info)
	private TextView baoziInfo;
	@ViewInject(R.id.vline_recharge_alipay)
	private View lineAli;
	@ViewInject(R.id.person_recharge_alipay)
	private RelativeLayout aliLayout;
	@ViewInject(R.id.person_recharge_yeepay)
	private RelativeLayout yeeLayout;

	@ViewInject(R.id.person_recharge_baozi)
	private RadioButton radioButtonBaozi;
	@ViewInject(R.id.person_recharge_ali)
	private RadioButton radioButtonAli;
	@ViewInject(R.id.person_recharge_yee)
	private RadioButton radioButtonYee;

	@ViewInject(R.id.order_detail_recharge_price)
	private TextView priceInfo;
	@ViewInject(R.id.order_detail_num)
	private TextView numInfo;
	@ViewInject(R.id.ll_order_detail_account)
	private LinearLayout couponLayout;
	@ViewInject(R.id.order_detail_account)
	private TextView couponInfo;
	@ViewInject(R.id.person_order_amount)
	private TextView price;
	@ViewInject(R.id.person_order_special_amount)
	private TextView num;
	@ViewInject(R.id.order_account_detail)
	private TextView coupon;
	@ViewInject(R.id.person_order_pay_amount_layout)
	private LinearLayout payAmountLayout;

	@ViewInject(R.id.pay_ecard_all)
	private TextView orderAll;
	@ViewInject(R.id.person_ecard_pay)
	private TextView orderPay;

	@ViewInject(R.id.person_order_item_no)
	private TextView merchantInfo;

	private String payChannel = ALIPAY_CHANNEL;//支付渠道
	private ECardEntity eCardEntity;
	private String discount;//折扣
	private int buyNum;
	private String orderNo = "";
	private int flag;

	//广播地址，用于接收易联支付插件支付完成之后回调客户端
	private BroadcastReceiver payecoPayBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_ecard);
		ViewInjectUtils.inject(this);
		setLeftTitle(getString(R.string.pay_ensure));
		initView();
		//初始化支付结果广播接收器
		initPayecoPayBroadcastReceiver();
		//注册支付结果广播接收器
		registerPayecoPayBroadcastReceiver();
	}

	private void initView() {

		merchantInfo.setText("商品信息");
		merchantInfo.setTextSize(15);
		merchantInfo.setTextColor(getResources().getColor(R.color.wjika_client_middle_gray));
		priceInfo.setText(getString(R.string.person_orderdetail_amount));

		payAmountLayout.setVisibility(View.GONE);

		flag = getIntent().getIntExtra(ECardDetailActivity.EXTRA_FLAG, 0);
		eCardEntity = getIntent().getParcelableExtra(ECardDetailActivity.EXTRA_BUY);
		buyNum = getIntent().getIntExtra(ECardDetailActivity.EXTRA_NUM, 1);
		if (eCardEntity != null) {
			ImageUtils.setSmallImg(cardImg, eCardEntity.getLogoUrl());
			cardName.setText(eCardEntity.getName());
			cardNum.setText(String.format(getString(R.string.person_order_detail_buy_num), String.valueOf(buyNum)));
			cardPrice.setText(String.format(getString(R.string.person_order_detail_buy_amount), String.valueOf(eCardEntity.getRMBSalePrice())));
			discount = NumberFormatUtil.formatBun((eCardEntity.getRMBSalePrice() - eCardEntity.getSalePrice()) * buyNum);
			cardView.setCardBackgroundColor(Color.parseColor(eCardEntity.getBgcolor()));
		}
		if (Double.parseDouble(discount) > 0){
			baoziInfo.setText(String.format(getString(R.string.ecard_buy_baozi_minu), discount));
		}
		//显示包子支付
		baoziLayout.setVisibility(View.VISIBLE);
		lineAli.setVisibility(View.VISIBLE);
		radioButtonBaozi.setChecked(false);
		radioButtonAli.setChecked(true);
		radioButtonYee.setChecked(false);

		payByRMB();

		baoziLayout.setOnClickListener(this);
		aliLayout.setOnClickListener(this);
		yeeLayout.setOnClickListener(this);
		orderPay.setOnClickListener(this);//立即支付按钮

		mBtnTitleLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showCancleHint();
			}
		});

	}

	private void payByBaozi() {
		price.setText(String.format(getString(R.string.person_baozi_num), NumberFormatUtil.formatBun(eCardEntity.getSalePrice() * buyNum)));
		num.setText(String.format(getString(R.string.person_order_detail_num), String.valueOf(buyNum)));
		couponInfo.setVisibility(View.VISIBLE);
		coupon.setVisibility(View.VISIBLE);
		if (Double.parseDouble(discount) > 0) {
			couponLayout.setVisibility(View.VISIBLE);
			couponInfo.setText(getString(R.string.wjika_client_coupon));
			coupon.setText(String.format(getString(R.string.minus_baozi), discount));
		} else {
			couponLayout.setVisibility(View.GONE);
		}
		orderAll.setText(String.format(getString(R.string.person_baozi_num), NumberFormatUtil.formatBun(eCardEntity.getSalePrice() * buyNum)));
		orderAll.setTextSize(14);
		orderAll.setTextColor(getResources().getColor(R.color.person_main_baozi_num));
	}

	private void payByRMB() {
		String money = String.format(getString(R.string.person_order_detail_buy_amount), String.valueOf(eCardEntity.getRMBSalePrice() * buyNum));
		price.setText(money);
		num.setText(String.format(getString(R.string.person_order_detail_num), String.valueOf(buyNum)));
		couponInfo.setVisibility(View.GONE);
		coupon.setVisibility(View.GONE);
		orderAll.setText(money);
		orderAll.setTextSize(14);
		orderAll.setTextColor(getResources().getColor(R.color.card_store_price));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.person_recharge_baozipay:
				radioButtonBaozi.setChecked(true);
				radioButtonAli.setChecked(false);
				radioButtonYee.setChecked(false);
				payChannel = BAOZI_CHANNEL;
				payByBaozi();
				break;
			case R.id.person_recharge_alipay:
				radioButtonBaozi.setChecked(false);
				radioButtonAli.setChecked(true);
				radioButtonYee.setChecked(false);
				payChannel = ALIPAY_CHANNEL;
				payByRMB();
				break;
			case R.id.person_recharge_yeepay:
				radioButtonBaozi.setChecked(false);
				radioButtonAli.setChecked(false);
				radioButtonYee.setChecked(true);
				payChannel = YILIAN_CHANNEL;
				payByRMB();
				break;
			case R.id.person_ecard_pay:
				if (UserCenter.isLogin(this)) {
					showProgressDialog();
					IdentityHashMap<String, String> params = new IdentityHashMap<>();
					params.put("thirdCardId", eCardEntity.getId());
					params.put("purchaseNum", String.valueOf(buyNum));
					params.put("channelId", payChannel);
					params.put("orderNo", orderNo);
					params.put(Constants.TOKEN, UserCenter.getToken(this));
					requestHttpData(Constants.Urls.URL_POST_CREATE_ECARD_ORDER, REQUEST_NET_ECARD_ORDER, FProtocol.HttpMethod.POST, params);
				} else {
					startActivity(new Intent(ECardBuyActivity.this, LoginActivity.class));
				}
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_NET_ECARD_ORDER:
				if (null != data) {
					ECardOrderEntity eCardOrder = Parsers.getECardOrder(data);
					orderNo = eCardOrder.getOrderNo();
					if (BAOZI_CHANNEL.equals(payChannel)) {
						if (eCardOrder != null) {
							if (UserCenter.isAuthencaiton(this)) {
								if (buyNum * eCardEntity.getSalePrice() > eCardOrder.getBalance()) {
									Intent rechargeIntent = new Intent(this, RechargeDialogActivity.class);
									rechargeIntent.putExtra("from", "eCardDetail");
									rechargeIntent.putExtra("eCard", eCardEntity);
									rechargeIntent.putExtra("eCardNo", orderNo);
									rechargeIntent.putExtra("eCardNum", buyNum);
									rechargeIntent.putExtra("walletCount", eCardOrder.getBalance());
									startActivity(rechargeIntent);
								} else {
									Intent payIntent = new Intent(this, PayDialogActivity.class);
									payIntent.putExtra("from", "eCardDetail");
									payIntent.putExtra("eCard", eCardEntity);
									payIntent.putExtra("eCardNo", orderNo);
									payIntent.putExtra("eCardNum", buyNum);
									payIntent.putExtra("walletCount", eCardOrder.getBalance());
									payIntent.putExtra(ECardDetailActivity.EXTRA_FLAG, flag);
									startActivityForResult(payIntent, REQUEST_BAOZI_PAY_SECCESS);
								}
							} else {
								showAlertDialog("", getString(R.string.person_auth_buy_baozi), getString(R.string.person_not_now), getString(R.string.person_auth_now), new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										closeDialog();
									}
								}, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										startActivity(new Intent(ECardBuyActivity.this, AuthenticationActivity.class));
										closeDialog();
									}
								});
							}
						}
					} else {
						String charge = eCardOrder.getCharge();
						CardRechargeActivity.pay(this, payChannel, charge);
					}
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case CardRechargeActivity.REQUEST_CHANNEL_PAY:
					String result = data.getExtras().getString("pay_result");//返回值
//            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
            /* 处理返回值
             * "success" - payment succeed
             * "fail"    - payment failed
             * "cancel"  - user canceld
             * "invalid" - payment plugin not installed
             * 如果是银联渠道返回 invalid，调用 UPPayAssistEx.installUPPayPlugin(this); 安装银联安全支付控件。
             */
					if ("success".equalsIgnoreCase(result)) {
						doPaySuccess();
					} else if ("cancel".equalsIgnoreCase(result)) {
						//支付取消，未修改允许继续支付，不生成新订单
						ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_cancel));
					} else {
						//支付失败，未修改允许继续支付，不生成新订单
						ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_failed));
					}
					break;
				case REQUEST_BAOZI_PAY_SECCESS:
					finish();
					break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterPayecoPayBroadcastReceiver();
	}

	@Override
	public void onBackPressed() {
		showCancleHint();
	}

	private void showCancleHint() {
		showAlertDialog("", getString(R.string.person_giveup_pay), getString(R.string.person_give_up), getString(R.string.person_think_again), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				finish();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});
	}

	private void doPaySuccess() {//支付宝支付和银行卡支付成功跳转
		ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_success));
		Intent payResult = new Intent(this, ECardPayResultActivity.class);
		payResult.putExtra(ECardPayResultActivity.EXTRA_ECARD, eCardEntity);
		payResult.putExtra(ECardPayResultActivity.EXTRA_FROM, FROM_ECARD_BUY);
		payResult.putExtra(ECardPayResultActivity.EXTRA_PAYCHANNEL, payChannel);
		payResult.putExtra(ECardPayResultActivity.EXTRA_NUM, buyNum);
		payResult.putExtra(ECardDetailActivity.EXTRA_FLAG, flag);
		startActivity(payResult);
		finish();
	}

	/**
	 * Title registerPayecoPayBroadcastReceiver
	 * Description 注册广播接收器
	 */
	private void registerPayecoPayBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_PAY_END);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(payecoPayBroadcastReceiver, filter);
	}

	/**
	 * Title unRegisterPayecoPayBroadcastReceiver
	 * Description 注销广播接收器
	 */
	private void unRegisterPayecoPayBroadcastReceiver() {
		if (payecoPayBroadcastReceiver != null) {
			unregisterReceiver(payecoPayBroadcastReceiver);
			payecoPayBroadcastReceiver = null;
		}
	}


	//初始化支付结果广播接收器
	private void initPayecoPayBroadcastReceiver() {
		payecoPayBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//接收易联支付插件的广播回调
				String action = intent.getAction();
				if (!BROADCAST_PAY_END.equals(action)) {
					return;
				} else {
//获取支付结果，result为手机支付返回的json数据
					String result = intent.getExtras().getString("upPay.Rsp");
					if (!StringUtil.isEmpty(result)) {
						String status = Parsers.getYilianPayResult(result);
						//01:未支付，02:已支付，03:已退款，04:已过期，05:已作废
						switch (status) {
							case "02":
								//支付成功
								doPaySuccess();
								break;
							case "01":

							default:
								//取消支付
								ToastUtil.shortShow(ECardBuyActivity.this, res.getString(R.string.person_order_pay_cancel));
								break;

						}
					}
				}

			}
		};
	}
}
