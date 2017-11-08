package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.market.controller.BaoziHelpActivity;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by kkkkk on 2016/9/1.
 */
public class RechargeDialogActivity extends BaseActivity implements View.OnClickListener{

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
    @ViewInject(R.id.ecard_dialog_recharge)
    private TextView recharge;
    @ViewInject(R.id.ecard_dialog_cancel)
    private TextView cancel;

    private String orderName;
    private String facePrice;
    private String orderNo;
    private Double payAmount;
    private int buyNum;
    private Double walletCount;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.dialog_ecard_recharge);
        ViewInjectUtils.inject(this);
        getData();
        initViews();
    }

    private void getData() {
        Intent payIntent = getIntent();
        from = payIntent.getStringExtra("from");
        if (("eCardDetail").equals(from)) {
            ECardEntity eCardEntity = payIntent.getParcelableExtra("eCard");
            orderName = eCardEntity.getName();
            facePrice = eCardEntity.getFacePrice();
            Double salePrice = eCardEntity.getSalePrice();
            orderNo = payIntent.getStringExtra("eCardNo");
            buyNum = payIntent.getIntExtra("eCardNum",0);
            payAmount = salePrice * buyNum;
            walletCount = payIntent.getDoubleExtra("walletCount",0.0);
        } else {
            orderName = payIntent.getStringExtra("orderName");
            facePrice = payIntent.getStringExtra("facePrice");
            orderNo = payIntent.getStringExtra("orderNo");
            payAmount = payIntent.getDoubleExtra("payAmount",0.0);
            buyNum = payIntent.getIntExtra("buyNum",0);
            walletCount = payIntent.getDoubleExtra("walletCount",0.0);
        }
    }

    private void initViews() {
        close.setOnClickListener(this);
        help.setOnClickListener(this);
        recharge.setOnClickListener(this);
        cancel.setOnClickListener(this);
        message.setText(orderName + " 面值" + facePrice + "元 数量X" + buyNum);
        amount.setText(NumberFormatUtil.formatBun(payAmount));
        balance.setText("可用包子数：" + NumberFormatUtil.formatBun(walletCount) + "个");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ecard_dialog_cancel:
            case R.id.ecard_dialog_close:
                finish();
                break;
            case R.id.ecard_dialog_help:
                startActivity(new Intent(this, BaoziHelpActivity.class));
                break;
            case R.id.ecard_dialog_recharge:
                startActivity(new Intent(this, BaoziRechargeActivity.class));
                finish();
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
