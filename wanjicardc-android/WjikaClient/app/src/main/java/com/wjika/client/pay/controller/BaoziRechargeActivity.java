package com.wjika.client.pay.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.controller.BaoziHelpActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.BaoziCardEntity;
import com.wjika.client.network.entities.BaoziChargeEntity;
import com.wjika.client.network.entities.BaoziPayEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by bob on 2016/8/19.
 * 包子充值
 */
public class BaoziRechargeActivity extends ToolBarActivity implements View.OnClickListener {

    private static final int REQUEST_SUBMIT_ORDER = 200;
    public static final int REQUEST_START_PAY = 300;
    public static final int REQUEST_CHANNEL_PAY = 500;
    public static final int REQUEST_WEB_PAY = 600;
    public static final int REQUEST_REAL_PAY_AMOUNT = 700;
    public static final int REQUEST_REAL_PAY_AMOUNT_BUYCARD = 800;
    public static final int COUPON_USE_ACTIVITY_REQUEST_CODE = 6;
    public static final String ALIPAY_CHANNEL = "pingapp";
    public static final String YILIAN_CHANNEL = "payeco";

    @ViewInject(R.id.ll_person_recharge_amount)
    private LinearLayout llPersonRechargeAmount;

    @ViewInject(R.id.person_recharge_alipay)
    private RelativeLayout personRechargeAlipay;
    @ViewInject(R.id.person_recharge_ali)
    private RadioButton personRechargeAli;
    @ViewInject(R.id.person_recharge_yeepay)
    private RelativeLayout personRechargeYeepay;
    @ViewInject(R.id.vline_recharge_yeepay)
    private View vLineRechargeYeepay;
    @ViewInject(R.id.person_recharge_yee)
    private RadioButton personRechargeYee;
    @ViewInject(R.id.cb_charge_agree)
    private CheckBox cbChargeAgree;
    @ViewInject(R.id.person_recharge_money)
    private TextView personRechargeMoney;
    @ViewInject(R.id.person_recharge_submit)
    private TextView personRechargeSubmit;
    @ViewInject(R.id.tv_agree)
    private TextView tvAgree;
    @ViewInject(R.id.iv_arrow)
    private ImageView ivArrow;

    @ViewInject(R.id.v_Line_pay)
    private View vLinePay;
    @ViewInject(R.id.ll_pay_btn)
    private View llPayBtn;
    @ViewInject(R.id.sv_parentview)
    private View svParentView;

    @ViewInject(R.id.ll_recharge)
    private View ll_recharge;
    @ViewInject(R.id.rl_charge_agree)
    private View vCharegAgree;

    private String payChannel = ALIPAY_CHANNEL;//支付渠道
    private BaoziPayEntity channelPay;
    private BaoziChargeEntity baoziChargeEntity;
    private List<RadioButton> listRadioButton;
    private BaoziCardEntity selectChargeCard;
    private String rechargeAgreementUrl;

    //广播地址，用于接收易联支付插件支付完成之后回调客户端
    private final static String BROADCAST_PAY_END = "com.wjika.client.broadcast";

    /**
     * payecoPayBroadcastReceiver : 易联支付插件广播
     */
    private BroadcastReceiver payecoPayBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitManager.instance.addPayActivity(this);
        setContentView(R.layout.act_baozi_recharge);
        ViewInjectUtils.inject(this);
        initView();
        initLoadingView(this);
        setDataLoadingUI(false, LoadingStatus.LOADING);
        loadData();
        //初始化支付结果广播接收器
        initPayecoPayBroadcastReceiver();
        //注册支付结果广播接收器
        registerPayecoPayBroadcastReceiver();
    }

    /**
     * 设置数据加载时的UI界面
     *
     * @param loadOk 数据是否加载完成：未完成（false），完成（true）
     * @param status 加载状态
     */
    private void setDataLoadingUI(boolean loadOk, LoadingStatus status) {
        if (loadOk) {
            setLoadingStatus(status);
            vLinePay.setVisibility(View.VISIBLE);
            llPayBtn.setVisibility(View.VISIBLE);
            svParentView.setVisibility(View.VISIBLE);
        } else {
            setLoadingStatus(status);
            vLinePay.setVisibility(View.GONE);
            llPayBtn.setVisibility(View.GONE);
            svParentView.setVisibility(View.GONE);
        }
    }

    private void initView() {
        updateUiByEco(false);
        personRechargeAlipay.setOnClickListener(this);
        personRechargeYeepay.setOnClickListener(this);
        personRechargeSubmit.setOnClickListener(this);
        tvAgree.setOnClickListener(this);
        ivArrow.setOnClickListener(this);

        setLeftTitle(res.getString(R.string.person_recharge_text));
        mBtnTitleRight.setVisibility(View.VISIBLE);
        mBtnTitleRight.setImageResource(R.drawable.baozi_btn_help_bg_selector);
        mBtnTitleRight.setOnClickListener(this);
        personRechargeSubmit.setText(this.getResources().getString(R.string.card_current_charge));
        ll_recharge.setVisibility(View.VISIBLE);

        cbChargeAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean haveCard = baoziChargeEntity.getBaoziList() != null && baoziChargeEntity.getBaoziList().size() > 0;
                personRechargeSubmit.setEnabled(isChecked && haveCard);

            }
        });
    }

    private void loadData() {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put(Constants.TOKEN, UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_POST_BAOZI_CARD_LIST, REQUEST_REAL_PAY_AMOUNT, FProtocol.HttpMethod.POST, params);
    }

    @Override
    protected void receiverLogout(String data) {
        super.receiverLogout(data);
        ExitManager.instance.toActivity(MainActivity.class);
    }

    @Override
    public void success(int requestCode, String data) {
        super.success(requestCode, data);
        switch (requestCode) {
            case REQUEST_SUBMIT_ORDER:
                personRechargeSubmit.setClickable(true);
                break;
        }
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        switch (requestCode) {
            case REQUEST_SUBMIT_ORDER:
                channelPay = Parsers.getBaoziPay(data);
                if (channelPay != null) {
                    CardRechargeActivity.pay(this, payChannel, channelPay.getCharge());
                } else {
                    ToastUtil.shortShow(this, "创建订单失败");
                }
                break;
            case REQUEST_REAL_PAY_AMOUNT://充值请求
                doChargeCardData(data);
                break;
        }
    }

    /**
     * 充值时获取数据后处理
     *
     * @param data 接口返回
     */
    private void doChargeCardData(String data) {
        if (!StringUtil.isEmpty(data)) {
            baoziChargeEntity = Parsers.getBaoziChargeEntity(data);
            if (baoziChargeEntity != null) {
                setDataLoadingUI(true, LoadingStatus.GONE);
                //易联支付是否显示
                updateUiByEco(baoziChargeEntity.getEcoEnable() == 1);
                rechargeAgreementUrl = baoziChargeEntity.getRechargeAgreement();

                //充值卡面额列表
                llPersonRechargeAmount.removeAllViews();
                if (baoziChargeEntity.getBaoziList() != null && baoziChargeEntity.getBaoziList().size() > 0) {
                    //有可用充值卡
                    personRechargeSubmit.setEnabled(true);
                    if (listRadioButton != null) {
                        listRadioButton.clear();
                    }
                    for (int i = 0; i < baoziChargeEntity.getBaoziList().size(); i++) {
                        BaoziCardEntity baoziCardEntity = baoziChargeEntity.getBaoziList().get(i);
                        View view = LayoutInflater.from(this).inflate(R.layout.charge_baozi_item, null);
                        RadioButton rbPersonRecharge = (RadioButton) view.findViewById(R.id.rb_person_recharge);
                        TextView tvBaoziPrice = (TextView) view.findViewById(R.id.tv_baozi_value);
                        TextView tvGiftInfo = (TextView) view.findViewById(R.id.tv_gift_info);
                        TextView tvNewUserGiftInfo = (TextView) view.findViewById(R.id.tv_newuser_gift_info);
                        View rlItem = view.findViewById(R.id.rl_item);
                        if (listRadioButton == null) {
                            listRadioButton = new ArrayList<>();
                        }
                        listRadioButton.add(rbPersonRecharge);
                        View vLine = view.findViewById(R.id.vRechargeLine);

                        String sAmount = "￥" + NumberFormatUtil.formatMoney(baoziCardEntity.getRechargeAmount());
                        tvBaoziPrice.setText(sAmount);

                        //所有用户充值赠送信息，没有时gone以保证面值垂直居中显示
                        if (StringUtil.isEmpty(baoziCardEntity.getDescribe())) {
                            tvGiftInfo.setText("");
                            tvGiftInfo.setVisibility(View.GONE);
                        } else {
                            tvGiftInfo.setText(baoziCardEntity.getDescribe());
                            tvGiftInfo.setVisibility(View.VISIBLE);
                        }

                        //新用户充值送包子信息
                        if (StringUtil.isEmpty(baoziCardEntity.getNewUserGift())) {
                            tvNewUserGiftInfo.setText("");
                        } else {
                            tvNewUserGiftInfo.setText(baoziCardEntity.getNewUserGift());
                        }

                        rlItem.setTag(i);
                        rlItem.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int pos = (int) v.getTag();
                                for (int j = 0; j < listRadioButton.size(); j++) {
                                    RadioButton rb = listRadioButton.get(j);
                                    if (j == pos) {
                                        rb.setChecked(true);
                                    } else {
                                        rb.setChecked(false);
                                    }
                                }
                                selectChargeCard = baoziChargeEntity.getBaoziList().get(pos);
                                setRealPrice(NumberFormatUtil.formatMoney(selectChargeCard.getRechargeAmount()));
                            }
                        });

                        if (i == baoziChargeEntity.getBaoziList().size() - 1) {
                            vLine.setVisibility(View.GONE);
                        }

                        if (i == 0) {//默认选择第一个
                            selectChargeCard = baoziChargeEntity.getBaoziList().get(i);
                            rbPersonRecharge.setChecked(true);
                        } else {
                            rbPersonRecharge.setChecked(false);
                        }

                        llPersonRechargeAmount.addView(view);
                    }
                    setRealPrice(NumberFormatUtil.formatMoney(selectChargeCard.getRechargeAmount()));

                } else {
                    //没有可用充值卡
                    personRechargeSubmit.setEnabled(false);
                    setRealPrice(NumberFormatUtil.formatMoney(0));
                }
            } else {
                setDataLoadingUI(false, LoadingStatus.RETRY);
            }
        } else {
            setDataLoadingUI(false, LoadingStatus.RETRY);
        }
    }

    private void updateUiByEco(boolean isEcoEnable) {
        if (isEcoEnable) {//银联卡支付可用
            personRechargeYeepay.setVisibility(View.VISIBLE);
            vLineRechargeYeepay.setVisibility(View.VISIBLE);
        } else {
            personRechargeYeepay.setVisibility(View.GONE);
            vLineRechargeYeepay.setVisibility(View.GONE);
        }

    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        super.mistake(requestCode, status, errorMessage);

        switch (requestCode) {
            case REQUEST_SUBMIT_ORDER:
            case REQUEST_START_PAY:
                personRechargeSubmit.setClickable(true);
                break;
            case REQUEST_REAL_PAY_AMOUNT:
            case REQUEST_REAL_PAY_AMOUNT_BUYCARD:
                setDataLoadingUI(false, LoadingStatus.RETRY);
                break;
        }
    }

    @Override
    public void onDestroy() {
        try {
            unRegisterPayecoPayBroadcastReceiver();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_recharge_submit:
	            if (UserCenter.isAuthencaiton(this)) {
		            //防止重复点击
		            personRechargeSubmit.setClickable(false);

		            //如果订单信息不变，则继续支付原有订单，否则生成新订单
		            if (channelPay == null) {
			            showProgressDialog();
			            IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();

			            if (selectChargeCard != null) {
				            identityHashMap.put("rechargeMoneyId", selectChargeCard.getId());
			            } else {
				            identityHashMap.put("rechargeMoneyId", "");
			            }

			            identityHashMap.put("channelId", payChannel);
			            identityHashMap.put("token", UserCenter.getToken(this));
			            requestHttpData(Constants.Urls.URL_POST_BAOZI_CHARGE_ORDER, REQUEST_SUBMIT_ORDER,
					            FProtocol.HttpMethod.POST, identityHashMap);
		            } else {
			            CardRechargeActivity.pay(this, payChannel, channelPay.getCharge());
		            }
	            } else {
		            startActivity(new Intent(this, AuthenticationActivity.class));
	            }
                break;
            case R.id.person_recharge_alipay:
                personRechargeAli.setChecked(true);
                personRechargeYee.setChecked(false);
                if (!personRechargeSubmit.isClickable()) {
                    personRechargeSubmit.setClickable(true);
                }
                payChannel = ALIPAY_CHANNEL;
                break;
            case R.id.person_recharge_yeepay:
                personRechargeAli.setChecked(false);
                personRechargeYee.setChecked(true);
                payChannel = YILIAN_CHANNEL;//原有易宝支付修改为易联支付
                if (!personRechargeSubmit.isClickable()) {
                    personRechargeSubmit.setClickable(true);
                }
                break;
            case R.id.loading_layout:
                setDataLoadingUI(false, LoadingStatus.LOADING);
                loadData();
                break;
            case R.id.person_recharge_hint:
                Intent useCouponIntent = new Intent(this, CouponUseActivity.class);
                startActivityForResult(useCouponIntent, COUPON_USE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.tv_agree:
            case R.id.iv_arrow:
                Intent chargeAgreeIntent = new Intent(this, WebViewActivity.class);
                chargeAgreeIntent.putExtra(WebViewActivity.EXTRA_URL, rechargeAgreementUrl);
                chargeAgreeIntent.putExtra(WebViewActivity.EXTRA_TITLE, "充值协议");
                startActivity(chargeAgreeIntent);
                break;
            case R.id.right_button:
                Intent intent = new Intent(this, BaoziHelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setRealPrice(String price) {
        String priceStr = String.format(res.getString(R.string.person_recharge_need_pay), NumberFormatUtil.formatMoney(price));
        SpannableStringBuilder ssbNum = new SpannableStringBuilder(priceStr);
        ssbNum.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_price_red)), 5, priceStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ssbNum.setSpan(new AbsoluteSizeSpan(DeviceUtil.dp_to_px(this, 18)), 5, priceStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        personRechargeMoney.setText(ssbNum);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        personRechargeSubmit.setClickable(true);
        if (REQUEST_CHANNEL_PAY == requestCode && Activity.RESULT_OK == resultCode) {
            String result = data.getExtras().getString("pay_result");//返回值
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
            finish();
        } else if (REQUEST_WEB_PAY == requestCode) {
            switch (resultCode) {
                case RESULT_OK:
                    //支付成功
                    doPaySuccess();
                    break;
                case RESULT_CANCELED:
                    //取消支付
                    ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_cancel));
                    finish();
                    break;
            }
        }
    }

    /**
     * 注册广播接收器
     */
    private void registerPayecoPayBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_PAY_END);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(payecoPayBroadcastReceiver, filter);
    }

    /**
     * 注销广播接收器
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
                }
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
                            ToastUtil.shortShow(BaoziRechargeActivity.this, res.getString(R.string.person_order_pay_cancel));
                            finish();
                            break;
                    }
                }
                //客户端自行根据返回结果进行验证和界面跳转
            }
        };
    }

    private void doPaySuccess() {
        ToastUtil.shortShow(BaoziRechargeActivity.this, res.getString(R.string.person_order_pay_success));
        Intent payResult = new Intent(BaoziRechargeActivity.this, BaoziPayResultActivity.class);
        if (ALIPAY_CHANNEL.equals(payChannel)) {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_alipay));
        } else {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_bankpay));
        }

        payResult.putExtra(BaoziPayResultActivity.EXTRA_REAL_PAY, channelPay.getRechargeAmount());
        payResult.putExtra(BaoziPayResultActivity.EXTRA_BAOZI_DESCRIBE, selectChargeCard.getDescribe());
        payResult.putExtra(BaoziPayResultActivity.EXTRA_NEW_HINT_INFO, selectChargeCard.getHintInfo());
        startActivity(payResult);
        finish();
    }
}
