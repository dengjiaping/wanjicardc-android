package com.wjika.client.person.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.OrderDetailEntity;
import com.wjika.client.network.entities.OrderPageEntity;
import com.wjika.client.network.entities.PayCertificateEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.BaoziPayResultActivity;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.pay.controller.CardRechargeActivity;
import com.wjika.client.pay.controller.ECardBuyActivity;
import com.wjika.client.pay.controller.PayResultActivity;
import com.wjika.client.store.controller.CorrelationStoreActivity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.TimeUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/9/7 11:47.
 * 订单详情
 */
public class OrderDetailActivity extends ToolBarActivity implements View.OnClickListener {
    public static final int ELECTRON = 4;// 电子卡
    public static final int BAOZI = 3;// 包子充值
    static final int REQUEST_PERSON_ORDER_DETAIL_CODE = 100;
    private static final int REQUEST_PERSON_CANCEL_ORDER_CODE = 101;
    private static final int REQUEST_DETAIL_PAY_SUCCESS = 102;
    public static final int REQUEST_ELECTRON_BAOZI_ORDER_DETAIL = 0x1;
    public static final int REQUEST_CODE_PAY_CERTIFICATE = 0x3;
    private final static String BROADCAST_PAY_END = "com.wjika.client.broadcast";

    @ViewInject(R.id.person_order_info_title)
    private TextView personOrderInfoTitle;
    @ViewInject(R.id.person_order_info_address)
    private TextView personOrderInfoAddress;
    @ViewInject(R.id.person_order_info_store)
    private TextView personOrderInfoStore;
    @ViewInject(R.id.person_order_amount)
    private TextView personOrderAmount;
    @ViewInject(R.id.person_order_special_amount)
    private TextView personOrderSpecialAmount;
    @ViewInject(R.id.person_order_pay_amount)
    private TextView personOrderPayAmount;
    @ViewInject(R.id.person_order_info_time)
    private TextView personOrderInfoTime;
    @ViewInject(R.id.person_order_info_foot)
    private LinearLayout personOrderInfoFoot;
    @ViewInject(R.id.person_order_info_total_amount)
    private TextView personOrderInfoTotalAmount;
    @ViewInject(R.id.person_order_info_pay)
    private TextView personOrderInfoPay;
    @ViewInject(R.id.same_footer_view)
    private TextView viewSameFooter;
    @ViewInject(R.id.order_detail_shop_message)
    private LinearLayout orderDetailShopMessage;
    @ViewInject(R.id.person_order_info_cancel)
    private TextView personOrderCancel;
    @ViewInject(R.id.order_detail_recharge_price)
    private TextView orderDetailRechargePrice;
    @ViewInject(R.id.order_detail_account)
    private TextView orderDetailAccount;
    @ViewInject(R.id.order_account_detail)
    private TextView orderAccountDetail;
    @ViewInject(R.id.ll_order_detail_account)
    private LinearLayout llOrderDetailAccount;
    @ViewInject(R.id.person_orderdetail_split1)
    private View orderdetailSplit1;
    @ViewInject(R.id.person_orderdetail_split2)
    private View orderdetailSplit2;
    @ViewInject(R.id.order_detail_card_item)
    private LinearLayout orderDetailCardItem;
    @ViewInject(R.id.person_orderdetail_payway)
    private TextView orderPayWay;

    private int type;
    private String cardOrderNo;
    private int position;
    //广播地址，用于接收易联支付插件支付完成之后回调客户端
    private BroadcastReceiver payecoPayBroadcastReceiver;
    private OrderDetailEntity orderDetail;
    private LayoutInflater lf;

    private TextView orderDetailNo;
    private TextView orderDetailCountTime;
    private TextView orderDetailState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_act_order_detail);
        ViewInjectUtils.inject(this);
        cardOrderNo = getIntent().getStringExtra("cardOrderNo");
        type = getIntent().getIntExtra("type", 0);
        position = getIntent().getIntExtra("orderPosition", 0);
        initView();
        initLoadingView(this);
        loadData();
        //初始化支付结果广播接收器
        initPayecoPayBroadcastReceiver();
        //注册支付结果广播接收器
        registerPayecoPayBroadcastReceiver();
    }

    private void loadData() {
        showProgressDialog();
        if (type == ELECTRON || type == BAOZI) {
            loadElectronAndBaozi();
        } else {
            loadMearchantData();
        }
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.person_order_detail_title));
        mBtnTitleLeft.setOnClickListener(this);
        personOrderCancel.setOnClickListener(this);
        personOrderInfoStore.setOnClickListener(this);
        personOrderInfoPay.setOnClickListener(this);
    }

    /*获取电子卡和包子订单详情*/
    private void loadElectronAndBaozi() {
        setLoadingStatus(LoadingStatus.LOADING);
        if (!TextUtils.isEmpty(cardOrderNo)) {
            IdentityHashMap<String, String> params = new IdentityHashMap<>();
            params.put("orderNo", cardOrderNo);
            params.put("orderType", type + "");
            params.put(Constants.TOKEN, UserCenter.getToken(this));
            requestHttpData(Constants.Urls.URL_POST_ORDER_DETAIL, REQUEST_ELECTRON_BAOZI_ORDER_DETAIL, FProtocol.HttpMethod.POST, params);
        }
    }

    /*获取商品卡订单详情*/
    private void loadMearchantData() {
        setLoadingStatus(LoadingStatus.LOADING);
        if (!TextUtils.isEmpty(cardOrderNo)) {
            IdentityHashMap<String, String> params = new IdentityHashMap<>();
            params.put("cardOrderNo", cardOrderNo);
            params.put(Constants.TOKEN, UserCenter.getToken(this));
            requestHttpData(Constants.Urls.URL_GET_ORDER_DETAIL, REQUEST_PERSON_ORDER_DETAIL_CODE, FProtocol.HttpMethod.POST, params);
        }
    }

    /*取消订单*/
    private void cancelOrder() {
        if (orderDetail != null) {
            showProgressDialog();
            IdentityHashMap<String, String> params = new IdentityHashMap<>();
            params.put("orderNo", cardOrderNo);
            params.put("orderType", String.valueOf(type));
            params.put(Constants.TOKEN, UserCenter.getToken(this));
            requestHttpData(Constants.Urls.URL_GET_CANCEL_ORDER, REQUEST_PERSON_CANCEL_ORDER_CODE, FProtocol.HttpMethod.POST, params);
        }
    }


    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        closeProgressDialog();
        setLoadingStatus(LoadingStatus.GONE);
        switch (requestCode) {
            case REQUEST_PERSON_ORDER_DETAIL_CODE://商品卡详情
                orderDetail = Parsers.getOrderDetail(data);
                if (orderDetail != null) {
                    lf = LayoutInflater.from(this);
                    setMerchantData();
                    setData();
                }
                break;
            case REQUEST_ELECTRON_BAOZI_ORDER_DETAIL://包子和电子卡详情
                orderDetail = Parsers.getOrderDetail(data);
                if (orderDetail != null) {
                    lf = LayoutInflater.from(this);
                    if (type == ELECTRON) {//电子卡
                        setElectronData();
                        setData();
                    } else if (type == BAOZI) {//包子
                        setBaoziData();
                        setData();
                    }
                }
                break;
            case REQUEST_CODE_PAY_CERTIFICATE://商品卡包子立即支付
                PayCertificateEntity payCertificate = Parsers.getPayCertificate(data);
                String charge = payCertificate.getCharge();
                String payChannel = payCertificate.getPayChannel();
                CardRechargeActivity.pay(this, payChannel, charge);
                break;
            case REQUEST_PERSON_CANCEL_ORDER_CODE://取消订单
                Entity entity = Parsers.getResponseSatus(data);
                if (entity != null) {
                    ToastUtil.shortShow(this, res.getString(R.string.order_cancel_success));
                    cancelToUpdate();
                } else {
                    ToastUtil.shortShow(this, res.getString(R.string.order_cancel_fail));
                }
                break;
        }
    }

    /*取消订单,更新界面*/
    private void cancelToUpdate() {
        personOrderInfoFoot.setVisibility(View.GONE);
        orderDetailCountTime.setVisibility(View.GONE);
        orderDetail.setStatus(2);
        orderDetailState.setText(orderDetail.getStatusName());
        orderDetailState.setTextColor(getResources().getColor(R.color.wjika_client_introduce_words));
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        super.mistake(requestCode, status, errorMessage);
        setLoadingStatus(LoadingStatus.RETRY);
        switch (requestCode) {
            case REQUEST_PERSON_CANCEL_ORDER_CODE:
                ToastUtil.shortShow(this, res.getString(R.string.order_cancel_fail));
                break;
        }
    }

    /*设置倒计时、订单号、状态等信息*/
    private void setData() {
        orderDetailNo.setText(String.format(res.getString(R.string.person_order_detail_no), orderDetail.getOrderNo()));
        orderDetailState.setText(orderDetail.getStatusName());
        if (OrderPageEntity.OrderStatus.UNPAY == orderDetail.getStatus()) {
            personOrderInfoFoot.setVisibility(View.VISIBLE);
            orderDetailState.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
            //增加倒计时
            if (orderDetail.getCountTime() > 0) {
                orderDetailCountTime.setVisibility(View.VISIBLE);
                new CountDownTimer(orderDetail.getCountTime() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        orderDetailCountTime.setText(TimeUtil.getFormatTime2(millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        cancelToUpdate();
                        cancel();
                    }
                }.start();
            }
        } else {
            personOrderInfoFoot.setVisibility(View.GONE);
        }
    }

    /*设置商家卡信息*/
    private void setMerchantData() {
        orderDetailCardItem.removeAllViews();
        View view = lf.inflate(R.layout.person_order_list_item_card, null);
        orderDetailCardItem.addView(view);
        orderDetailNo = (TextView) view.findViewById(R.id.person_order_item_no);
        orderDetailCountTime = (TextView) view.findViewById(R.id.person_order_item_time);
        orderDetailState = (TextView) view.findViewById(R.id.person_order_item_status);
        CardView cardImgBg = (CardView) view.findViewById(R.id.card_img_bg);
        ImageView cardImgCover = (ImageView) view.findViewById(R.id.card_img_cover);
        TextView cardTxtName = (TextView) view.findViewById(R.id.card_txt_name);
        TextView cardTxtStoreName = (TextView) view.findViewById(R.id.card_txt_store_name);
        TextView personOrderItemName = (TextView) view.findViewById(R.id.person_order_item_name);
        TextView personOrderItemFacevalue = (TextView) view.findViewById(R.id.person_order_item_facevalue);
        TextView personOrderItemPrice = (TextView) view.findViewById(R.id.person_order_item_price);

        //卡颜色(1,红色 2 黄色，3 蓝色 4 绿色)
        switch (orderDetail.getcType()) {
            case RED:
                cardImgBg.setCardBackgroundColor((getResources().getColor(R.color.wjika_client_card_red)));
                break;
            case ORANGE:
                cardImgBg.setCardBackgroundColor((getResources().getColor(R.color.wjika_client_card_yellow)));
                break;
            case BLUE:
                cardImgBg.setCardBackgroundColor((getResources().getColor(R.color.wjika_client_card_blue)));
                break;
            case GREEN:
                cardImgBg.setCardBackgroundColor((getResources().getColor(R.color.wjika_client_card_green)));
                break;
        }
        String url = orderDetail.getCover();
        if (!TextUtils.isEmpty(url) && !url.contains("?")) {
            ImageUtils.setSmallImg(cardImgCover,url);
        }
        cardTxtName.setText(orderDetail.getMerName());
        cardTxtStoreName.setText(res.getString(R.string.buy_card_face_value, NumberFormatUtil.formatBun(orderDetail.getCardFacePrice())));
        personOrderItemName.setText(orderDetail.getName());
        personOrderItemFacevalue.setText(String.format(res.getString(R.string.buy_card_face_value), String.valueOf(orderDetail.getCardFacePrice())));
        personOrderItemPrice.setText(String.format(res.getString(R.string.person_order_detail_buy_amount), String.valueOf(orderDetail.getOrderAmount())));

        SpannableStringBuilder amount = new SpannableStringBuilder(String.format(res.getString(R.string.person_order_detail_amount), NumberFormatUtil.formatMoney(orderDetail.getPayAmount())));
        amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_dark_grey)), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        viewSameFooter.setVisibility(View.GONE);
        orderPayWay.setText(getString(R.string.person_order_detail_payway_online));
        personOrderInfoTotalAmount.setText(amount);
        personOrderInfoTitle.setText(orderDetail.getMerName());
        personOrderInfoAddress.setText(orderDetail.getMerAddress());
        if (orderDetail.getSupportStoreNum() <= 0) {
            personOrderInfoStore.setVisibility(View.GONE);
        } else {
            personOrderInfoStore.setVisibility(View.VISIBLE);
            personOrderInfoStore.setText(String.format(res.getString(R.string.person_order_detail_store_num), String.valueOf(orderDetail.getSupportStoreNum())));
        }
        personOrderAmount.setText(String.format(getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(orderDetail.getOrderAmount())));
        personOrderSpecialAmount.setText(String.format(res.getString(R.string.person_order_detail_num), String.valueOf(orderDetail.getBuyNum())));
        orderAccountDetail.setText(String.format(res.getString(R.string.minus_money), orderDetail.getSpecialAmount()));
        orderDetailAccount.setText(R.string.person_special_amount);
        SpannableStringBuilder ssbTime = new SpannableStringBuilder(String.format(res.getString(R.string.person_order_detail_time), orderDetail.getDate()));
        ssbTime.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_gray)), 0, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        personOrderInfoTime.setText(ssbTime);
        personOrderPayAmount.setText(String.format(getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(orderDetail.getPayAmount())));
    }

    /*设置包子信息*/
    private void setBaoziData() {
        orderDetailShopMessage.setVisibility(View.GONE);
        orderdetailSplit1.setVisibility(View.GONE);
        orderdetailSplit2.setVisibility(View.GONE);
        orderDetailCardItem.removeAllViews();
        View view = lf.inflate(R.layout.person_order_list_item_baozi, null);
        orderDetailCardItem.addView(view);
        orderDetailNo = (TextView) view.findViewById(R.id.person_order_item_no);
        orderDetailState = (TextView) view.findViewById(R.id.person_order_item_status);
        orderDetailCountTime = (TextView) view.findViewById(R.id.person_order_item_time);
        TextView baoziOrderItemName = (TextView) view.findViewById(R.id.baozi_order_item_name);
        TextView baoziOrderItemPrice = (TextView) view.findViewById(R.id.baozi_order_item_price);
        TextView baoziOrderFacevalue1 = (TextView) view.findViewById(R.id.baozi_order_item_facevalue);
        View baoziOrderFacevalue2 = view.findViewById(R.id.baozi_order_item_facevalue2);

        baoziOrderFacevalue1.setVisibility(View.GONE);
        baoziOrderFacevalue2.setVisibility(View.VISIBLE);
        baoziOrderItemName.setText(orderDetail.getName());
        String priceStr = String.format(res.getString(R.string.person_order_detail_charge), String.valueOf(orderDetail.getOrderAmount()));
        SpannableStringBuilder ssb = new SpannableStringBuilder(priceStr);
        ssb.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_dark_grey)), 0, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        baoziOrderItemPrice.setText(ssb);
        SpannableStringBuilder amount = new SpannableStringBuilder(String.format(res.getString(R.string.person_order_detail_amount), NumberFormatUtil.formatMoney(orderDetail.getPayAmount())));
        amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_dark_grey)), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        viewSameFooter.setVisibility(View.GONE);
        orderPayWay.setText(getString(R.string.person_order_detail_payway_online));
        personOrderInfoTotalAmount.setText(amount);
        orderDetailRechargePrice.setText(R.string.person_recharge_text);
        personOrderAmount.setText(String.format(getString(R.string.person_order_detail_buy_amount),NumberFormatUtil.formatMoney(orderDetail.getOrderAmount())));
        personOrderSpecialAmount.setText(String.format(res.getString(R.string.person_order_detail_num), String.valueOf(orderDetail.getBuyNum())));
        orderDetailAccount.setText(R.string.person_order_detail_toaccount);
        orderAccountDetail.setText(orderDetail.getAccoutDetail());
        personOrderPayAmount.setText(String.format(res.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(orderDetail.getOrderAmount())));
        personOrderInfoTime.setText(String.format(res.getString(R.string.person_order_detail_time), orderDetail.getDate()));
    }

    /*设置电子卡信息*/
    private void setElectronData() {
        orderDetailShopMessage.setVisibility(View.GONE);
        orderdetailSplit1.setVisibility(View.GONE);
        orderdetailSplit2.setVisibility(View.GONE);

        orderDetailCardItem.removeAllViews();
        View view = lf.inflate(R.layout.person_order_list_item_electron, null);
        orderDetailCardItem.addView(view);
        orderDetailNo = (TextView) view.findViewById(R.id.person_order_item_no);
        orderDetailState = (TextView) view.findViewById(R.id.person_order_item_status);
        orderDetailCountTime = (TextView) view.findViewById(R.id.person_order_item_time);
        ImageView electronCardImgCover = (ImageView) view.findViewById(R.id.electron_card_img_cover);
        TextView electronFaceValue = (TextView) view.findViewById(R.id.electron_face_value);
        TextView electronOrderItemName = (TextView) view.findViewById(R.id.electron_order_item_name);
        TextView electronOrderItemPrice = (TextView) view.findViewById(R.id.electron_order_item_price);
        CardView electronImgBg = (CardView) view.findViewById(R.id.electron_img_bg);
        TextView electronOrderFacevalue1 = (TextView) view.findViewById(R.id.electron_order_item_facevalue);
        View electronOrderFacevalue2 = view.findViewById(R.id.electron_order_item_facevalue2);

        electronOrderFacevalue1.setVisibility(View.VISIBLE);
        electronOrderFacevalue2.setVisibility(View.GONE);
	    electronOrderFacevalue1.setText(getString(R.string.person_order_detail_buy_num, String.valueOf(orderDetail.getBuyNum())));
        if (!StringUtil.isEmpty(orderDetail.getBgcolor())) {
            electronImgBg.setCardBackgroundColor(Color.parseColor(orderDetail.getBgcolor()));
        } else {
            electronImgBg.setCardBackgroundColor(res.getColor(R.color.wjika_client_card_blue));
        }
        String url = orderDetail.getCover();
        if (!TextUtils.isEmpty(url) && !url.contains("?")) {
            ImageUtils.setSmallImg(electronCardImgCover,url);
        }
        electronFaceValue.getPaint().setFakeBoldText(true);
        electronFaceValue.setText(res.getString(R.string.money, NumberFormatUtil.formatBun(String.valueOf(orderDetail.getCardFacePrice()))));
        electronOrderItemName.setText(orderDetail.getName());
        electronOrderItemPrice.setText(getString(R.string.person_order_detail_buy_amount, NumberFormatUtil.formatMoney(orderDetail.getCardOrignalRmb())));
//        electronOrderItemPrice.setTextColor(getResources().getColor(R.color.person_main_baozi_num));

        //支付方式 0:易联；1:ping++；2:包子
        String payWay = orderDetail.getPayWay();
        if ("2".equals(payWay)) {
            orderPayWay.setText(getString(R.string.person_order_detail_payway_baozi));
            String str = String.format(res.getString(R.string.person_baozi_num2), NumberFormatUtil.formatBun(orderDetail.getPayAmount()));
            SpannableStringBuilder amount = new SpannableStringBuilder(str);
            amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_dark_grey)), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.person_main_baozi_num)), 3, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            personOrderInfoTotalAmount.setText(amount);
            viewSameFooter.setVisibility(View.VISIBLE);
	        personOrderPayAmount.setText(String.format(res.getString(R.string.person_baozi_num), NumberFormatUtil.formatBun(orderDetail.getPayAmount())));//总价
	        personOrderPayAmount.setTextColor(getResources().getColor(R.color.person_main_baozi_num));
            String discount = NumberFormatUtil.formatBun(orderDetail.getBuyNum() * (orderDetail.getCardOrignalRmb() - orderDetail.getCardSalePrice()));
            if (Double.parseDouble(discount) > 0) {
                llOrderDetailAccount.setVisibility(View.VISIBLE);
                orderDetailAccount.setText(getString(R.string.wjika_client_coupon));
                orderAccountDetail.setText(getString(R.string.minus_baozi, discount));
            } else {
                llOrderDetailAccount.setVisibility(View.GONE);
            }
            personOrderAmount.setText(String.format(res.getString(R.string.person_baozi_num), NumberFormatUtil.formatBun(orderDetail.getPayAmount())));//售价
        } else {
            orderPayWay.setText(getString(R.string.person_order_detail_payway_online));
            String str = String.format(res.getString(R.string.person_order_detail_amount), NumberFormatUtil.formatMoney(orderDetail.getPayAmount()));
            SpannableStringBuilder amount = new SpannableStringBuilder(str);
            amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_dark_grey)), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            amount.setSpan(new ForegroundColorSpan(res.getColor(R.color.card_store_price)), 3, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            personOrderInfoTotalAmount.setText(amount);
            viewSameFooter.setVisibility(View.GONE);
	        personOrderPayAmount.setText(String.format(res.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatBun(orderDetail.getPayAmount())));//总价
	        personOrderPayAmount.setTextColor(getResources().getColor(R.color.card_store_price));
            llOrderDetailAccount.setVisibility(View.GONE);
            personOrderAmount.setText(String.format(res.getString(R.string.person_order_detail_buy_amount), String.valueOf(orderDetail.getCardOrignalRmb())));//售价
        }
        orderDetailRechargePrice.setText(R.string.person_orderdetail_amount);
        personOrderSpecialAmount.setText(String.format(res.getString(R.string.person_order_detail_num), String.valueOf(orderDetail.getBuyNum())));
        personOrderInfoTime.setText(String.format(res.getString(R.string.person_order_detail_time), orderDetail.getDate()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_order_info_store:
                Intent intent = new Intent(this, CorrelationStoreActivity.class);
                intent.putExtra(CorrelationStoreActivity.EXTRA_MERID, orderDetail.getMerId());
                startActivity(intent);
                break;
            case R.id.person_order_info_pay:
                if (type == ELECTRON) {//电子卡充值
                    //支付方式 0:易联；1:ping++；2:包子
                    String payWay = orderDetail.getPayWay();
                    if ("2".equals(payWay)) {
                        electricPay();
                    } else {
                        ECardEntity eCard = new ECardEntity(orderDetail.getCardId(),orderDetail.getCover(),orderDetail.getName(),orderDetail.getPayAmount(),orderDetail.getCardOrignalRmb(),orderDetail.getBgcolor());
                        Intent Changentent = new Intent(this, ECardBuyActivity.class);
                        Changentent.putExtra(ECardDetailActivity.EXTRA_FLAG, ECardDetailActivity.flag);
                        Changentent.putExtra(ECardDetailActivity.EXTRA_BUY, eCard);
                        Changentent.putExtra(ECardDetailActivity.EXTRA_NUM, orderDetail.getBuyNum());
                        startActivity(Changentent);
                    }
                } else if (type == BAOZI){//包子充值
                    if ((orderDetail.isRechargeMoney() + orderDetail.getPayAmount()) > 2000) {
                        showAlertDialog(null, getString(R.string.person_baozipay_toomany),
                                getString(R.string.button_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        closeDialog();
                                    }
                                });
                    } else {
                        mearchantPay();
                    }
                } else {//商家卡充值
                    mearchantPay();
                }
                break;
            case R.id.person_order_info_cancel:
                cancelOrder();
                break;
            case R.id.left_button:
                setIntentData();
                finish();
                break;
            case R.id.loading_layout:
                loadData();
                break;
            case R.id.ecard_dialog_recharge:
                startActivity(new Intent(this, BaoziRechargeActivity.class));
                break;
        }
    }

    private void electricPay() {
        if (orderDetail.getWalletCount() < orderDetail.getPayAmount()) {
            Intent rechargeIntent = new Intent(this, RechargeDialogActivity.class);
            rechargeIntent.putExtra("orderName", orderDetail.getName());
            rechargeIntent.putExtra("facePrice", orderDetail.getCardFacePrice());
            rechargeIntent.putExtra("orderNo", orderDetail.getOrderNo());
            rechargeIntent.putExtra("payAmount", orderDetail.getPayAmount());
            rechargeIntent.putExtra("buyNum", orderDetail.getBuyNum());
            rechargeIntent.putExtra("walletCount", orderDetail.getWalletCount());
            startActivity(rechargeIntent);
        } else {
            Intent payIntent = new Intent(this, PayDialogActivity.class);
            payIntent.putExtra("orderName", orderDetail.getName());
            payIntent.putExtra("facePrice", orderDetail.getCardFacePrice());
            payIntent.putExtra("orderNo", orderDetail.getOrderNo());
            payIntent.putExtra("salePrice", orderDetail.getOrderAmount());
            payIntent.putExtra("buyNum", orderDetail.getBuyNum());
            payIntent.putExtra("walletCount", orderDetail.getWalletCount());
            startActivityForResult(payIntent,REQUEST_DETAIL_PAY_SUCCESS);
        }
    }

    private void mearchantPay() {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put(Constants.TOKEN, UserCenter.getToken(this));
        params.put("orderType", type + "");
        params.put("cardOrderNo", orderDetail.getOrderNo());
        requestHttpData(Constants.Urls.URL_POST_PAY_CERTIFICATE, REQUEST_CODE_PAY_CERTIFICATE, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void onDestroy() {
        try {
            unRegisterPayecoPayBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        if (type == BAOZI) {
                            doPayBaoziSucess();
                        } else {
                            doPayMerchantSucess();
                        }
                        loadData();
                    } else if ("cancel".equalsIgnoreCase(result)) {
                        //支付取消，未修改允许继续支付，不生成新订单
                        ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_cancel));
                    } else {
                        //支付失败，未修改允许继续支付，不生成新订单
                        ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_failed));
                    }
                    break;
                case REQUEST_DETAIL_PAY_SUCCESS:
                    loadData();
                    break;
            }
        }
    }

    private void doPayMerchantSucess() {
        ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_success));
        Intent payResult = new Intent(this, PayResultActivity.class);
        payResult.putExtra(CardRechargeActivity.EXTRA_TYPE, type);
        payResult.putExtra(PayResultActivity.EXTRA_NAME, orderDetail.getName());
        if(CardRechargeActivity.ALIPAY_CHANNEL.equals(orderDetail.getPayChannel())) {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_alipay));
        } else {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_bankpay));
        }

        payResult.putExtra(PayResultActivity.EXTRA_REAL_PAY, orderDetail.getPayAmount());
        payResult.putExtra(PayResultActivity.EXTRA_FACE_VALUE, String.valueOf(orderDetail.getCardFacePrice()));

        payResult.putParcelableArrayListExtra(PayResultActivity.EXTRA_PRIVILEGE_LIST, orderDetail.getPrivilege());
        startActivity(payResult);
        finish();
    }

    private void doPayBaoziSucess() {
        ToastUtil.shortShow(this, res.getString(R.string.person_order_pay_success));
        Intent payResult = new Intent(this, BaoziPayResultActivity.class);
        if (BaoziRechargeActivity.ALIPAY_CHANNEL.equals(orderDetail.getPayChannel())) {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_alipay));
        } else {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_bankpay));
        }
        payResult.putExtra(BaoziPayResultActivity.EXTRA_REAL_PAY, String.valueOf(orderDetail.getPayAmount()));//string
        payResult.putExtra(BaoziPayResultActivity.EXTRA_BAOZI_DESCRIBE,orderDetail.getAccoutDetail());
        payResult.putExtra(BaoziPayResultActivity.EXTRA_NEW_HINT_INFO, orderDetail.getHintInfo());
        startActivity(payResult);
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
                                if (type == BAOZI) {
                                    doPayBaoziSucess();
                                } else {
                                    doPayMerchantSucess();
                                }
                                loadData();
                                break;
                            case "01":

                            default:
                                //取消支付
                                ToastUtil.shortShow(OrderDetailActivity.this, res.getString(R.string.person_order_pay_cancel));
                                break;

                        }
                    }
                }

            }
        };
    }

    @Override
    public void onBackPressed() {
        setIntentData();
        super.onBackPressed();
    }

    /*返回订单列表时传递数据*/
    private void setIntentData() {
        if (null != orderDetail) {
            Intent intent = getIntent();
            intent.putExtra("orderPosition", position);
            intent.putExtra("orderState", orderDetail.getStatus0());
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    protected void receiverLogout(String data) {
        super.receiverLogout(data);
        finish();
    }
}
