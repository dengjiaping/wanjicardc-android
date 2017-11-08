package com.wjika.client.person.controller;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.EnCryptionUtils;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.viewinject.util.core.DoubleKeyValueMap;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.controller.BaoziHelpActivity;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.PayVerifyPwdEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.ECardPayResultActivity;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;
import com.wjika.client.widget.PasswordInputView;

import java.util.IdentityHashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by kkkkk on 2016/8/31.
 */
public class PayDialogActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_NET_ECARD_PAY = 30;

    @ViewInject(R.id.ecard_dialog_close)
    private ImageView close;
    @ViewInject(R.id.ecard_dialog_help)
    private ImageView help;
    @ViewInject(R.id.ecard_dialog_message)
    private TextView message;
    @ViewInject(R.id.ecard_dialog_amount)
    private TextView amount;
    @ViewInject(R.id.ecard_dialog_balance)
    private TextView balance;
    @ViewInject(R.id.ecard_dialog_pwd)
    private PasswordInputView pwd;
    @ViewInject(R.id.ecard_dialog_forget)
    private TextView forget;

    private String orderName;
    private String facePrice;
    private String orderNo;
    private Double payAmount;
    private Double salePrice;
    private Double moneySalePrice;
    private int buyNum;
    private Double walletCount;
    private String from;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.dialog_ecard_verify_pwd);
        ViewInjectUtils.inject(this);
        getData();
        initViews();
    }

    private void getData() {
        Intent payIntent = getIntent();
        from = payIntent.getStringExtra("from");
        flag = payIntent.getIntExtra(ECardDetailActivity.EXTRA_FLAG, 0);
        if (("eCardDetail").equals(from)) {
            ECardEntity eCardEntity = payIntent.getParcelableExtra("eCard");
            moneySalePrice = eCardEntity.getRMBSalePrice();
            orderName = eCardEntity.getName();
            facePrice = eCardEntity.getFacePrice();
            orderNo = payIntent.getStringExtra("eCardNo");
            buyNum = payIntent.getIntExtra("eCardNum",0);
            salePrice = eCardEntity.getSalePrice();
            payAmount = salePrice * buyNum;
            walletCount = payIntent.getDoubleExtra("walletCount",0.0);
        } else {
            orderName = payIntent.getStringExtra("orderName");
            facePrice = payIntent.getStringExtra("facePrice");
            orderNo = payIntent.getStringExtra("orderNo");
            salePrice = payIntent.getDoubleExtra("salePrice",0.0);
            buyNum = payIntent.getIntExtra("buyNum",0);
            payAmount = salePrice * buyNum;
            walletCount = payIntent.getDoubleExtra("walletCount",0.0);
        }
    }

    private void initViews() {
        message.setText(orderName + " 面值" + facePrice + "元 数量X" + buyNum);
        amount.setText(NumberFormatUtil.formatBun(payAmount));
        balance.setText("可用包子数：" + NumberFormatUtil.formatBun(walletCount) + "个");
        close.setOnClickListener(this);
        help.setOnClickListener(this);
        forget.setOnClickListener(this);
        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (6 == s.length()) {
                    showProgressDialog();
                    paypwd = s.toString();
                    loadPaypwdSalt();
                }
            }
        });
    }

    private void loadPaypwdSalt() {
        String paypwdSalt = UserCenter.getUserPaypwdSalt(this);
        if (StringUtil.isEmpty(paypwdSalt)) {
            requestHttpData(String.format(Constants.Urls.URL_PAY_PWD_SHA_KEY, UserCenter.getToken(this)), REQUEST_NET_PAYPWD_SALT);
        } else {
            verifyPaypwd();
        }
    }

    private void verifyPaypwd() {
        if (!TextUtils.isEmpty(paypwd)) {
            IdentityHashMap<String, String> params = new IdentityHashMap<>();
            paypwd = EnCryptionUtils.SHA1(paypwd, UserCenter.getUserPaypwdSalt(this));
            params.put("payPassword", paypwd);
            params.put("token", UserCenter.getToken(this));
            params.put("userPushID", JPushInterface.getRegistrationID(this.getApplicationContext()));
            params.put("identity", UserCenter.getUserIdentity(this));
            requestHttpData(Constants.Urls.URL_VERIFY_PAY_PWD, REQUEST_NET_VERIFY_PAYPWD, FProtocol.HttpMethod.POST, params);
        }
    }

    @Override
    public void success(int requestCode, String data) {
//        super.success(requestCode, data);
        switch (requestCode) {
            case REQUEST_NET_VERIFY_PAYPWD:
                closeProgressDialog();
                PayVerifyPwdEntity entity = Parsers.getPayVerifyPwdEntity(data);
                if (entity != null) {
                    if (RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
                        closeDialog();
                        if (paypwd != null) {
                            UserCenter.setUserPaypwd(this, paypwd);
                        }
                        if (ecardDialogPwd != null) {
                            ecardDialogPwd.getText().clear();
                        }
                        UserCenter.setToken(this, entity.getToken());

                        if (!TextUtils.isEmpty(orderNo)) {
                            IdentityHashMap<String, String> params = new IdentityHashMap<>();
                            params.put("orderNo", orderNo);
                            params.put(Constants.TOKEN, UserCenter.getToken(this));
                            requestHttpData(Constants.Urls.URL_POST_ECARD_ORDER_PAY, REQUEST_NET_ECARD_PAY, FProtocol.HttpMethod.POST, params);
                        }
                    } else {
                        if (ecardDialogPwd != null) {
                            ecardDialogPwd.getText().clear();
                        }
                        if (entity.isLockedStatus()) {
                            showVerifyFailedDialog(getString(R.string.title_account_locked), entity.getResultMsg(), R.string.pay_verify_pay_back_password);
                        } else {
                            showVerifyFailedDialog(getString(R.string.verify_safe_validation_fails), entity.getResultMsg(), R.string.person_pay_setting_confirm_pwd);
                            pwd.setText("");
                        }
                    }
                }
                break;
            case REQUEST_NET_PAYPWD_SALT:{
                String paypwdSalt = Parsers.getPaypwdSalt(data);
                if (!StringUtil.isEmpty(paypwdSalt)){
                    UserCenter.setUserPaypwdSalt(this, paypwdSalt);
                    verifyPaypwd();
                }
                break;
            }
            default:
                super.success(requestCode, data);
                break;
        }
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        if (REQUEST_NET_ECARD_PAY == requestCode) {
            //包子不够弹充值框
            //支付失败需要弹窗
            setResult(RESULT_OK);
            finish();
            ECardEntity eCard = new ECardEntity(orderName,facePrice,salePrice,moneySalePrice);
            Intent intent = new Intent(this, ECardPayResultActivity.class);
            intent.putExtra(ECardDetailActivity.EXTRA_FLAG, flag);
            intent.putExtra(ECardPayResultActivity.EXTRA_ECARD, eCard);
            intent.putExtra(ECardPayResultActivity.EXTRA_NUM, buyNum);
            startActivity(intent);
        }
    }

    private void showVerifyFailedDialog(String title, String msg, int okTextResId) {
        closeProgressDialog();
        showAlertDialog(title,
                msg,
                res.getString(R.string.verify_safe_cancel),
                res.getString(okTextResId),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDialog();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView) v;
                        if (tv.getText().equals(res.getString(R.string.pay_verify_pay_back_password))) {
                            showMenu(UserCenter.isSetSecurity(getApplicationContext()), UserCenter.isAuthencaiton(getApplicationContext()));
                        }
                        closeDialog();
                    }
                });
    }

    private void showMenu(boolean isShowSecurity, boolean isAuthencation) {
        final Dialog dialog = new Dialog(PayDialogActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(R.layout.chooseway_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //window.setWindowAnimations(R.style.dialogAnimation);
        // 可以在此设置显示动画
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        View tvSecurity = dialog.findViewById(R.id.tv_security_find);
        TextView tvAuthenticaion = (TextView) dialog.findViewById(R.id.tv_authentication_find);
        View vLine1 = dialog.findViewById(R.id.v_Line1);
        View vLine2 = dialog.findViewById(R.id.v_Line2);
        if (isAuthencation) {
            tvAuthenticaion.setVisibility(View.VISIBLE);
            vLine1.setVisibility(View.VISIBLE);
        } else {
            tvAuthenticaion.setVisibility(View.GONE);
            vLine1.setVisibility(View.GONE);
        }
        if (isShowSecurity) {
            tvSecurity.setVisibility(View.VISIBLE);
            vLine2.setVisibility(View.VISIBLE);
        } else {
            tvSecurity.setVisibility(View.GONE);
            vLine2.setVisibility(View.GONE);
        }
        //安全问题找回
        tvSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent securityIntent = new Intent(PayDialogActivity.this, VerifySafeQuestionActivity.class);
                securityIntent.putExtra(VerifySafeQuestionActivity.FROM_EXTRA, VerifySafeQuestionActivity.MODIFY);
                startActivity(securityIntent);
                dialog.dismiss();
            }
        });
        //实名找回
        tvAuthenticaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayDialogActivity.this, FindPassByAuthActivity.class));
                dialog.dismiss();
            }
        });
        //申诉找回
        dialog.findViewById(R.id.tv_complain_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (10 != UserCenter.getAppealStatus(PayDialogActivity.this)) {
                    startActivity(new Intent(PayDialogActivity.this, ComplainMessageFirstActivity.class));
                } else {
                    ToastUtil.longShow(getApplicationContext(), "您的账号正在申诉中");
                }
            }
        });
        //取消
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.onWindowAttributesChanged(wl);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ecard_dialog_close:
                finish();
                break;
            case R.id.ecard_dialog_help:
                startActivity(new Intent(this, BaoziHelpActivity.class));
                break;
            case R.id.ecard_dialog_forget:
                showMenu(UserCenter.isSetSecurity(getApplicationContext()),UserCenter.isAuthencaiton(this));
                break;
        }
    }

    @Override
    public void finish() {
        if ("orderList".equals(from)) {
            setResult(RESULT_OK);
        }
        super.finish();
        overridePendingTransition(0, R.anim.abc_popup_exit);
    }
}
