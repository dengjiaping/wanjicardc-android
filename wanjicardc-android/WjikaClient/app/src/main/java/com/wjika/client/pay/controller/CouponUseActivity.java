package com.wjika.client.pay.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.network.entities.CouponEntity;
import com.wjika.client.pay.adapter.CouponUseListAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.List;

/**
 * Created by bob on 2016/5/31 12:17.
 * 支付时，选择可用优惠券界面
 */
public class CouponUseActivity extends ToolBarActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    public static final String EXTRA_COUPONLIST = "extra_couponlist";
    public static final String EXTRA_RESULT_COUPON = "extra_result_coupon";

    @ViewInject(R.id.person_coupon_list)
    private FootLoadingListView listView;

    private CouponUseListAdapter adapter;
    private List<CouponEntity> listCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_use_coupon);
        ViewInjectUtils.inject(this);

        Intent intent = getIntent();
        listCoupon = intent.getParcelableArrayListExtra(EXTRA_COUPONLIST);
        initView();
    }

    private void initView() {
        setLeftTitle(getString(R.string.person_usable_coupon));
        rightText.setText(getString(R.string.card_details_use_explian));
        rightText.setVisibility(View.VISIBLE);
        rightText.setOnClickListener(this);
        mBtnTitleLeft.setOnClickListener(this);

        listView.setEnabled(true);
        listView.setOnItemClickListener(this);
        adapter = new CouponUseListAdapter(this, listCoupon);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rigth_text:
                showAlertDialog(null,
                        res.getString(R.string.person_coupon_info),
                        res.getString(R.string.wjk_alert_know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeDialog();
                            }
                        });
                break;
            case R.id.left_button:
                setResultCancel();
                break;
        }
    }

    private void setResultCancel() {
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CouponEntity coupon = adapter.getItem(position);

        boolean isCheck = !coupon.isChecked();
        for(int i=0; i<listCoupon.size(); i++) {
            coupon.setIsChecked(false);
        }
        coupon.setIsChecked(isCheck);

        adapter.notifyDataSetChanged();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_COUPON, coupon);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }
}
