package com.wjika.client.pay.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.payeco.android.plugin.PayecoPluginLoadingActivity;
import com.pingplusplus.android.PaymentActivity;
import com.wjika.cardagent.client.R;
import com.wjika.client.ClientApplication;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ChargeCardEntity;
import com.wjika.client.network.entities.ChargeEntity;
import com.wjika.client.network.entities.CouponEntity;
import com.wjika.client.network.entities.DisCountCouponEntity;
import com.wjika.client.network.entities.OrderPayEntity;
import com.wjika.client.network.entities.PrivilegeEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/12 12:17.
 * 卡包充值
 */
public class CardRechargeActivity extends ToolBarActivity implements View.OnClickListener {

    private static final int REQUEST_SUBMIT_ORDER = 200;
    public static final int REQUEST_START_PAY = 300;
    public static final int REQUEST_CHANNEL_PAY = 500;
    public static final int REQUEST_WEB_PAY = 600;
    public static final int REQUEST_REAL_PAY_AMOUNT = 700;
    public static final int REQUEST_REAL_PAY_AMOUNT_BUYCARD = 800;
    public static final int ALIPAY_CHANNEL_CODE = 40;//ping++支付
    public static final int COUPON_USE_ACTIVITY_REQUEST_CODE = 6;
    public static final String ALIPAY_CHANNEL = "pingapp";
    public static final String YILIAN_CHANNEL = "payeco";

    public static final String EXTRA_CARD_NAME = "name";
    public static final String EXTRA_CARD_FACE_VALUE = "extra_card_face_value";
    public static final String EXTRA_CARD_SALE_VALUE = "extra_card_sale_value";
    public static final String EXTRA_CARD_ACTIVITY_VALUE = "extra_card_activity_value";
    public static final String EXTRA_CARD_PRIVILEGE = "extra_card_privilege";
    public static final String EXTRA_CARD_ID = "extra_cardId";
    public static final String EXTRA_ACTIVITY_ID = "extra_activity_id";
    public static final String EXTRA_ISLIMITFORSALE = "extra_isLimitForSale";

    public static final String EXTRA_MER_ID = "merId";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String TYPE_CHARGE = "2";//和接口保持一致充值为2
    public static final String TYPE_BUY_CARD = "1";//和接口保持一致购卡为1

    @ViewInject(R.id.ll_person_recharge_amount)
    private LinearLayout llPersonRechargeAmount;
    @ViewInject(R.id.person_recharge_hint)
    private LinearLayout personRechargeHint;

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
    @ViewInject(R.id.person_recharge_money)
    private TextView personRechargeMoney;
    @ViewInject(R.id.person_recharge_submit)
    private TextView personRechargeSubmit;
    @ViewInject(R.id.layout_buy_card)
    private View mLayoutBuyCard;
    @ViewInject(R.id.tv_face_value)
    private TextView tvFaceValue;

    @ViewInject(R.id.v_Line_pay)
    private View vLinePay;
    @ViewInject(R.id.ll_pay_btn)
    private View llPayBtn;
    @ViewInject(R.id.sv_parentview)
    private View svParentView;
    @ViewInject(R.id.tv_coupon_count)
    private TextView tvCouponCount;
    @ViewInject(R.id.tv_coupon_amount)
    private TextView tvCouponAmount;
    @ViewInject(R.id.tv_card_name)
    private TextView tvCardName;
    @ViewInject(R.id.tv_card_amount)
    private TextView tvCardAmount;
    @ViewInject(R.id.vLinePrivilege)
    private View vLinePrivilege;
    @ViewInject(R.id.rl_privilege)
    private View rlPrivilege;

    @ViewInject(R.id.ll_recharge)
    private View ll_recharge;
    @ViewInject(R.id.rl_action_benefit)
    private View rlActionBenefit; //活动优惠：购卡时显示，充值时隐藏
    @ViewInject(R.id.tv_order_amount)
    private TextView tvOrderAmount;//清单：商品金额
    @ViewInject(R.id.tv_order_coupon_amount)
    private TextView tvOrderCouponAmount;//清单：优惠券
    @ViewInject(R.id.tv_Action_benefit)
    private TextView tvActionBenefit;//清单：活动优惠
    @ViewInject(R.id.tv_real_pay)
    private TextView tvRealPay;//清单：实付金额

    @ViewInject(R.id.ll_privilege)
    private LinearLayout llPrivilege;//特权数组View容器

    private String merId;
    private String payChannel = ALIPAY_CHANNEL;//支付渠道
    private OrderPayEntity channelPay;
    private String type;
    private String cardId;
    private double discount = 1;//折扣

    private double amount;//卡的销售金额，从上个界面传过来
    private String name;//购买卡的名字 从上个界面传过来
    private double couponPrice;//优惠后的金额,从上个界面传过来
    private String faceValue;//从上个页面取
    private String activityId;//从上个界面传过来
    private String isLimitForSale;//从上个界面传过来
    private ArrayList<PrivilegeEntity> privilegeEntityList;//从上个页面取

    private double realPay;

    private List<CouponEntity> couponEntityList;
    private DisCountCouponEntity disCountCouponEntity;
    private ChargeEntity chargeEntity;

    private CouponEntity couponEntity = null;
    private List<RadioButton> listRadioButton;
    private ChargeCardEntity selectChargeCard;

    //广播地址，用于接收易联支付插件支付完成之后回调客户端
    private final static String BROADCAST_PAY_END = "com.wjika.client.broadcast";

    /**
     * @Fields payecoPayBroadcastReceiver : 易联支付插件广播
     */
    private BroadcastReceiver payecoPayBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitManager.instance.addPayActivity(this);
        setContentView(R.layout.person_act_recharge);
        ViewInjectUtils.inject(this);

        Intent extraIntent = getIntent();
        merId = extraIntent.getStringExtra(EXTRA_MER_ID);
        type = extraIntent.getStringExtra(EXTRA_TYPE);

        name = extraIntent.getStringExtra(EXTRA_CARD_NAME);

        String saleValue = extraIntent.getStringExtra(EXTRA_CARD_SALE_VALUE);
        if (!StringUtil.isEmpty(saleValue)) {
            amount = Double.valueOf(saleValue);
        }

        String actionPrice = extraIntent.getStringExtra(EXTRA_CARD_ACTIVITY_VALUE);
        if (!StringUtil.isEmpty(actionPrice)) {
            couponPrice = Double.valueOf(actionPrice);
        }

        faceValue = extraIntent.getStringExtra(EXTRA_CARD_FACE_VALUE);
        cardId = extraIntent.getStringExtra(EXTRA_CARD_ID);
        activityId = extraIntent.getStringExtra(EXTRA_ACTIVITY_ID);
        isLimitForSale = extraIntent.getStringExtra(EXTRA_ISLIMITFORSALE);
        if("0".equals(isLimitForSale) || "false".equals(isLimitForSale)) {
            isLimitForSale = String.valueOf(false);
            couponPrice = amount;
        } else {
            isLimitForSale = String.valueOf(true);
        }
        privilegeEntityList = extraIntent.getParcelableArrayListExtra(EXTRA_CARD_PRIVILEGE);

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
        personRechargeSubmit.setText(this.getResources().getString(R.string.card_current_charge));

        personRechargeHint.setOnClickListener(this);

        if (TYPE_BUY_CARD.equals(type)) {
            setLeftTitle("确认订单");
            mLayoutBuyCard.setVisibility(View.VISIBLE);
            ll_recharge.setVisibility(View.GONE);
            rlActionBenefit.setVisibility(View.VISIBLE);
            personRechargeSubmit.setText(this.getResources().getString(R.string.pay_click_label));
        } else if (TYPE_CHARGE.equals(type)) {
            setLeftTitle(res.getString(R.string.person_recharge_text));
            personRechargeSubmit.setText(this.getResources().getString(R.string.card_current_charge));
            mLayoutBuyCard.setVisibility(View.GONE);
            ll_recharge.setVisibility(View.VISIBLE);
            rlActionBenefit.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put(Constants.TOKEN, UserCenter.getToken(this));

        if (TYPE_CHARGE.equals(type)) {//充值
            params.put("merchantAccountId", merId);
            requestHttpData(Constants.Urls.URL_CARD_RECHARGE_LISTERS, REQUEST_REAL_PAY_AMOUNT, FProtocol.HttpMethod.POST, params);
        } else {//购卡
            params.put("branchMerchId", merId);
            params.put("orderType", "1");
            params.put("activityId", activityId);
            params.put("merchantCardId", cardId);
            params.put("isLimitForSale", isLimitForSale);
            requestHttpData(Constants.Urls.URL_GET_CHARGE_AMOUNT, REQUEST_REAL_PAY_AMOUNT_BUYCARD, FProtocol.HttpMethod.POST, params);
        }
    }

    @Override
    protected void receiverLogout(String data) {
        super.receiverLogout(data);
        this.finish();
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
                channelPay = Parsers.getOrderPay(data);
                if (channelPay != null) {
                    try {
                        couponPrice = Double.valueOf(channelPay.getOrderAmount());
                        String priceStr = String.format(res.getString(R.string.person_recharge_need_pay), NumberFormatUtil.formatMoney(couponPrice));
                        setRealPrice(priceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pay(this, payChannel, channelPay.getCharge());
                }
                break;
            case REQUEST_REAL_PAY_AMOUNT://充值请求
                doChargeCardData(data);
                break;
            case REQUEST_REAL_PAY_AMOUNT_BUYCARD://购卡请求
                doBuyCardData(data);
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
            chargeEntity = Parsers.getChargeEntity(data);
            if (chargeEntity != null) {
                setDataLoadingUI(true, LoadingStatus.GONE);

                //易联支付是否显示
                updateUiByEco(chargeEntity.getEcoEnable() == 1);

                //充值时有折扣
                discount = chargeEntity.getDiscount();

                //充值卡面额列表
                llPersonRechargeAmount.removeAllViews();
                if (chargeEntity.getCards() != null && chargeEntity.getCards().size() > 0) {
                    //有可用充值卡
                    personRechargeSubmit.setEnabled(true);
                    for (int i = 0; i < chargeEntity.getCards().size(); i++) {
                        ChargeCardEntity chargeCardEntity = chargeEntity.getCards().get(i);
                        View view = LayoutInflater.from(this).inflate(R.layout.charge_card_item, null);
                        RadioButton rbPersonRecharge = (RadioButton) view.findViewById(R.id.rb_person_recharge);
                        if (listRadioButton == null) {
                            listRadioButton = new ArrayList<RadioButton>();
                        }
                        listRadioButton.add(rbPersonRecharge);
                        View vLine = view.findViewById(R.id.vRechargeLine);

                        String sAmount = "￥" + NumberFormatUtil.formatMoney(chargeCardEntity.getCardFacePrice());
                        rbPersonRecharge.setText(sAmount);

                        rbPersonRecharge.setTag(i);
                        rbPersonRecharge.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                for (int j = 0; j < listRadioButton.size(); j++) {
                                    RadioButton rb = listRadioButton.get(j);
                                    rb.setChecked(false);
                                }
                                ((RadioButton) v).setChecked(true);
                                int pos = (int) v.getTag();
                                selectChargeCard = chargeEntity.getCards().get(pos);
                                privilegeEntityList = (ArrayList<PrivilegeEntity>)selectChargeCard.getMerchantCardPrivilege();
                                name = selectChargeCard.getMerchantCardName();
                                amount = Double.valueOf(selectChargeCard.getCardFacePrice());
                                faceValue = selectChargeCard.getCardFacePrice();
                                if (couponEntity == null) {
                                    updateUIByCoupon(false);
                                } else {
                                    updateUIByCoupon(couponEntity.isChecked());
                                }
                            }
                        });

                        if (i == chargeEntity.getCards().size() - 1) {
                            vLine.setVisibility(View.GONE);
                        }
                        llPersonRechargeAmount.addView(view);
                    }
                    selectChargeCard = chargeEntity.getCards().get(0);
                    privilegeEntityList = (ArrayList<PrivilegeEntity>)selectChargeCard.getMerchantCardPrivilege();
                    amount = Double.valueOf(selectChargeCard.getCardFacePrice());
                    name = selectChargeCard.getMerchantCardName();
                    faceValue = selectChargeCard.getCardFacePrice();
                    for (int j = 0; j < listRadioButton.size(); j++) {
                        RadioButton rb = listRadioButton.get(j);
                        if (j == 0) {
                            rb.setChecked(true);
                        } else {
                            rb.setChecked(false);
                        }
                    }

                } else {
                    //没有可用充值卡
                    personRechargeSubmit.setEnabled(false);
                    amount = 0;
                    faceValue = String.valueOf(0);
                }

                //优惠券
                couponEntityList = chargeEntity.getCoupon();
                setCouponUI(couponEntityList);
            } else {
                setDataLoadingUI(false, LoadingStatus.RETRY);
            }
        } else {
            setDataLoadingUI(false, LoadingStatus.RETRY);
        }
    }

    /**
     * 设置特权UI
     */
    private void setPrivilegeView() {
        if (privilegeEntityList != null && privilegeEntityList.size() > 0) {
            rlPrivilege.setVisibility(View.VISIBLE);
            vLinePrivilege.setVisibility(View.VISIBLE);
            llPrivilege.removeAllViews();
            int defaultPadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_comment_marginright) * 2;
            int width = DeviceUtil.getWidth(this) - CommonTools.dp2px(this,44) - defaultPadding;
            int privilegeSize = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_size);
            int privilegePadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_padding);
            int columsNum = width / (privilegeSize + privilegePadding);
            if (columsNum > privilegeEntityList.size()) {
                columsNum = privilegeEntityList.size();
            }

            for (int i = 0; i < columsNum; i++) {
                SimpleDraweeView view = new SimpleDraweeView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(privilegeSize, privilegeSize);
                view.setLayoutParams(imgParams);
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setFadeDuration(300)
                        .setPlaceholderImage(this.getResources().getDrawable(R.drawable.def_privilege_img))
                        .build();
                view.setHierarchy(hierarchy);
                view.setId(i);
                View blankView = new View(this);
                blankView.setId(i + 1000);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(privilegePadding, privilegePadding);
                blankView.setLayoutParams(layoutParams);
                String url = privilegeEntityList.get(i).getImgPath();
                if (!TextUtils.isEmpty(url) && !url.contains("?")) {
                    ImageUtils.setSmallImg(view,url);
                }
                llPrivilege.addView(view);
                llPrivilege.addView(blankView);
            }
        } else {
            rlPrivilege.setVisibility(View.GONE);
            vLinePrivilege.setVisibility(View.GONE);
        }
    }

    /**
     * 充值或购卡时，获取到优惠券列表后更新优惠券UI
     *
     * @param listCoupon
     */
    private void setCouponUI(List<CouponEntity> listCoupon) {
        int couponCount = 0;
        boolean couponEnable = false;
        if (listCoupon != null && listCoupon.size() > 0) {
            //有优惠券可用
            couponCount = listCoupon.size();

            if (couponEntity == null) {//第一次进入页面时，未初始化优惠券，默认选择使用list第一个券
                couponEntity = listCoupon.get(0);
                couponEntity.setIsChecked(true);
            }
            couponEnable = couponEntity.isChecked();
        }
        if (couponCount > 0) {
            personRechargeHint.setEnabled(true);
            tvCouponAmount.setVisibility(View.VISIBLE);
        } else {
            personRechargeHint.setEnabled(false);
            tvCouponAmount.setVisibility(View.INVISIBLE);
        }

        updateUIByCoupon(couponEnable);
        tvCouponCount.setText(String.format(res.getString(R.string.can_use_coupon_count), couponCount));
    }

    /**
     * 购卡请求查询优惠券后获取到数据后的处理
     *
     * @param data
     */
    private void doBuyCardData(String data) {
        if (!StringUtil.isEmpty(data)) {
            disCountCouponEntity = Parsers.getDiscount(data);
            if (disCountCouponEntity != null) {
                setDataLoadingUI(true, LoadingStatus.GONE);

                //如果有活动，根据活动条件，弹框提示用户
                if("true".equals(isLimitForSale)) {
                    showHintDialog(disCountCouponEntity.getActivityCondition());
                }
                updateBuyCardUi();
            } else {
                setDataLoadingUI(false, LoadingStatus.RETRY);
            }
        } else {
            setDataLoadingUI(false, LoadingStatus.RETRY);
        }
    }

    /**
     * 获取到数据或数据发生变化时（活动价恢复到原价）后更新下购卡的UI
     */
    private void updateBuyCardUi() {
        //特权
        setPrivilegeView();

        //易联支付是否可用
        updateUiByEco(disCountCouponEntity.getEcoEnable() == 1);

        //卡的面值，金额，名字
        tvCardName.setText(name);
        tvCardAmount.setText("￥" + NumberFormatUtil.formatMoney(getActionBuyAmount()));
        String sBuyCardFaceValue = String.format(res.getString(R.string.buy_card_face_value), faceValue);
        tvFaceValue.setText(sBuyCardFaceValue);
        //商品金额
        tvOrderAmount.setText("￥" + NumberFormatUtil.formatMoney(amount));

        //活动优惠
        double benefitAmount = amount - getActionBuyAmount();
        String benefit = String.format(res.getString(R.string.minus_money), NumberFormatUtil.formatMoney(benefitAmount));
        tvActionBenefit.setText(benefit);

        //购卡时没有折扣此时后台返回discount应为1
        discount = disCountCouponEntity.getDiscount();

        //优惠券
        couponEntityList = disCountCouponEntity.getCouponList();
        setCouponUI(couponEntityList);
    }

    private void showHintDialog(String type) {
        switch (type) {
            case DisCountCouponEntity.ACTIVITY_PAUSE:
            case DisCountCouponEntity.ACTIVITY_FINISH:
                showAlertDialog("", getString(R.string.order_cfm_action_end),false, getString(R.string.button_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLimitForSale = String.valueOf(false);
                        updateBuyCardUi();
                        closeDialog();
                    }
                });

                break;
            case DisCountCouponEntity.ACTIVITY_NO_QUALIFICATION:
                showAlertDialog("", getString(R.string.order_cfm_action_limit),false, getString(R.string.button_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLimitForSale = String.valueOf(false);
                        updateBuyCardUi();
                        closeDialog();
                    }
                });

                break;
            case DisCountCouponEntity.ACTIVITY_NO_INVENTORY:
                showAlertDialog("", getString(R.string.order_cfm_action_no_inventory),false, getString(R.string.order_cfm_later_on_try), getString(R.string.order_cfm_origin_buy), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        closeDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLimitForSale = String.valueOf(false);
                        updateBuyCardUi();
                        closeDialog();
                    }
                });

                break;
        }
    }

    private double getActionBuyAmount() {
        if (isLimitForSale.equals("true")) {//有活动优惠
            return couponPrice;
        } else {//无活动优惠
            return amount;
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

    /**
     * 选择优惠券后实际支付金额改变，该方法更新UI
     *
     * @param isUseCoupon false:没有使用优惠券    true:使用优惠券
     */
    private void updateUIByCoupon(boolean isUseCoupon) {
        String sCoupon = "0";
        String sCouponAmount = "";
        if (isUseCoupon) {
            sCoupon = couponEntity.getAmount();
            sCouponAmount = String.format(res.getString(R.string.coupon_amount), NumberFormatUtil.formatMoney(sCoupon, "###"));
        }
        tvCouponAmount.setText(sCouponAmount);//已抵用XX元
        tvOrderCouponAmount.setText(String.format(res.getString(R.string.minus_money), NumberFormatUtil.formatMoney(sCoupon)));//清单：优惠券

        if (TYPE_BUY_CARD.equals(type)) {//购卡
            realPay = getActionBuyAmount() - Double.valueOf(sCoupon);
        } else { //充值
            realPay = amount * discount - Double.valueOf(sCoupon);
            tvOrderAmount.setText("￥" + NumberFormatUtil.formatMoney(amount * discount));//清单：商品金额
        }
        if (realPay < 0) {
            realPay = 0;
        }

        tvRealPay.setText("￥" + NumberFormatUtil.formatMoney(realPay));

        String priceStr = String.format(res.getString(R.string.person_recharge_need_pay), NumberFormatUtil.formatMoney(realPay));
        setRealPrice(priceStr);
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


    /**
     * 作为一个公共支付的入口
     */
    public static void pay(Activity activity, String payChannel, String charge) {
        if (activity == null || StringUtil.isEmpty(payChannel) || StringUtil.isEmpty(charge)) {
            return;
        }

        switch (payChannel) {

            case YILIAN_CHANNEL:
                //设置Intent指向
                Intent intent = new Intent(activity, PayecoPluginLoadingActivity.class);
                // 将封装好的xml报文传入bundle
                intent.putExtra("upPay.Req", charge);
                // 设置广播接收地址
                intent.putExtra("Broadcast", BROADCAST_PAY_END);
                // 设置支付插件访问的环境： 00: 测试环境, 01: 生产环境
                if (ClientApplication.test) {
                    intent.putExtra("Environment", "00");
                } else {
                    intent.putExtra("Environment", "01");
                }
                // 使用intent跳转至手机在线支付
                activity.startActivity(intent);
                break;
            case ALIPAY_CHANNEL:
            default:
                //客户端支付
                Intent pingPP = new Intent();
                String packageName = activity.getPackageName();
                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                pingPP.setComponent(componentName);
                pingPP.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                activity.startActivityForResult(pingPP, REQUEST_CHANNEL_PAY);
                break;
        }

    }

    /*public static void pay(Fragment fragment, String payChannel, String charge) {
        Context context = ClientApplication.getContext();
        if (fragment == null || StringUtil.isEmpty(payChannel) || StringUtil.isEmpty(charge)) {
            return;
        }
        switch (payChannel) {

            case YILIAN_CHANNEL:
                //设置Intent指向
                Intent intent = new Intent(context, PayecoPluginLoadingActivity.class);
                // 将封装好的xml报文传入bundle
                intent.putExtra("upPay.Req", charge);
                // 设置广播接收地址
                intent.putExtra("Broadcast", BROADCAST_PAY_END);
                // 设置支付插件访问的环境： 00: 测试环境, 01: 生产环境
                if (ClientApplication.test) {

                    intent.putExtra("Environment", "00");
                } else {
                    intent.putExtra("Environment", "01");
                }
                // 使用intent跳转至手机在线支付
                fragment.startActivity(intent);

                break;
            case ALIPAY_CHANNEL:
            default:
                //客户端支付
                Intent pingPP = new Intent();
                String packageName = context.getPackageName();
                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                pingPP.setComponent(componentName);
                pingPP.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                fragment.startActivityForResult(pingPP, REQUEST_CHANNEL_PAY);
                break;
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_recharge_submit:
                //防止重复点击
                personRechargeSubmit.setClickable(false);

                //如果订单信息不变，则继续支付原有订单，否则生成新订单
                if (channelPay == null) {
                    showProgressDialog();
                    IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
                    identityHashMap.put("branchMerchId", merId);
                    identityHashMap.put("cardOrderAmount", String.valueOf(realPay));

                    identityHashMap.put("orderType", type);
                    if (TYPE_CHARGE.equals(type)) {//充值 2
                        identityHashMap.put("activityId", "");
                        identityHashMap.put("isLimitForSale", String.valueOf(false));

                        if (selectChargeCard != null) {
                            identityHashMap.put("merchantCardId", selectChargeCard.getCardId());
                        } else {
                            identityHashMap.put("merchantCardId", "");
                        }
                    } else {//购卡 1
                        identityHashMap.put("merchantCardId", cardId);
                        identityHashMap.put("activityId", activityId);
                        identityHashMap.put("isLimitForSale", isLimitForSale);
                    }
                    if (couponEntity != null && couponEntity.isChecked()) {
                        identityHashMap.put("userCouponCode", couponEntity.getCouponCode());
                    } else {
                        identityHashMap.put("userCouponCode", "");
                    }
                    identityHashMap.put("channelId", payChannel);
                    identityHashMap.put("token", UserCenter.getToken(this));
                    requestHttpData(Constants.Urls.URL_POST_CARD_CHARGE, REQUEST_SUBMIT_ORDER,
                            FProtocol.HttpMethod.POST, identityHashMap);
                } else {
                    pay(this, payChannel, channelPay.getCharge());
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
//				payChannel = YEEPAY_CHANNEL;
                payChannel = YILIAN_CHANNEL;//原有易宝支付修改为易联支付
                if (!personRechargeSubmit.isClickable()) {
                    personRechargeSubmit.setClickable(true);
                }
                break;
            case R.id.loading_layout:
                loadData();
                break;
            case R.id.person_recharge_hint:
                Intent useCouponIntent = new Intent(this, CouponUseActivity.class);
                useCouponIntent.putParcelableArrayListExtra(CouponUseActivity.EXTRA_COUPONLIST, ((ArrayList<CouponEntity>) couponEntityList));
                startActivityForResult(useCouponIntent, COUPON_USE_ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    private void setRealPrice(String priceStr) {
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
            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
            String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
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
        } else if (COUPON_USE_ACTIVITY_REQUEST_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                couponEntity = data.getParcelableExtra(CouponUseActivity.EXTRA_RESULT_COUPON);
                updateUIByCoupon(couponEntity.isChecked());
                for (int i = 0; i < couponEntityList.size(); i++) {
                    if (couponEntityList.get(i).getCouponCode().equals(couponEntity.getCouponCode())) {
                        couponEntityList.get(i).setIsChecked(couponEntity.isChecked());
                    } else {
                        couponEntityList.get(i).setIsChecked(false);
                    }
                }
            }
        }
    }

    /**
     * @Title registerPayecoPayBroadcastReceiver
     * @Description 注册广播接收器
     */
    private void registerPayecoPayBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_PAY_END);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(payecoPayBroadcastReceiver, filter);
    }

    /**
     * @Title unRegisterPayecoPayBroadcastReceiver
     * @Description 注销广播接收器
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
                            ToastUtil.shortShow(CardRechargeActivity.this, res.getString(R.string.person_order_pay_cancel));
                            finish();
                            break;
                    }
                }
                //客户端自行根据返回结果进行验证和界面跳转
            }
        };
    }

    private void doPaySuccess() {
        ToastUtil.shortShow(CardRechargeActivity.this, res.getString(R.string.person_order_pay_success));
        Intent payResult = new Intent(CardRechargeActivity.this, PayResultActivity.class);
        payResult.putExtra(EXTRA_TYPE, type);
        payResult.putExtra(PayResultActivity.EXTRA_NAME, name);
        if(ALIPAY_CHANNEL.equals(payChannel)) {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_alipay));
        } else {
            payResult.putExtra(PayResultActivity.EXTRA_CHANNEL, res.getString(R.string.person_order_bankpay));
        }

        payResult.putExtra(PayResultActivity.EXTRA_REAL_PAY, realPay);
        payResult.putExtra(PayResultActivity.EXTRA_FACE_VALUE, faceValue);

        payResult.putParcelableArrayListExtra(PayResultActivity.EXTRA_PRIVILEGE_LIST, privilegeEntityList);
        startActivity(payResult);
        finish();
    }
}
