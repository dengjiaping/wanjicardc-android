package com.wjika.client.pay.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CouponEntity;

import java.util.List;

/**
 * Created by bob on 2016/5/31 0031.
 * 支付使用优惠券适配器
 */
public class CouponUseListAdapter extends BaseAdapterNew<CouponEntity> {

	private Resources res;

	public CouponUseListAdapter(Context context, List<CouponEntity> mDatas) {
		super(context, mDatas);
		res = context.getResources();
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.pay_use_coupon_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CouponEntity item = getItem(position);
		if (item != null) {
			RadioButton rbCouponSelect = ViewHolder.get(convertView, R.id.use_coupon_select_rb);
			TextView personCouponName = ViewHolder.get(convertView, R.id.person_coupon_name);
			TextView personCouponScope = ViewHolder.get(convertView, R.id.person_coupon_scope);
			TextView personCouponValue = ViewHolder.get(convertView, R.id.person_coupon_value);
			TextView personCouponValidity = ViewHolder.get(convertView, R.id.person_coupon_validity);
			ImageView personCouponExpired = (ImageView) convertView.findViewById(R.id.person_coupon_expired);

			rbCouponSelect.setChecked(item.isChecked());

			if (!TextUtils.isEmpty(item.getName()))
				personCouponName.setText(item.getName());
			if (!TextUtils.isEmpty(item.getDesc()))
				personCouponScope.setText(item.getDesc());

			SpannableStringBuilder ssb = new SpannableStringBuilder(String.format(res.getString(R.string.person_order_detail_buy_amount), item.getAmount()));
			ssb.setSpan(new RelativeSizeSpan(0.4f), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			personCouponValue.setText(ssb);
			if (!TextUtils.isEmpty(item.getValidTime())) {
				personCouponValidity.setText(String.format(res.getString(R.string.person_coupon_validity), item.getValidTime()));
			}
			if ("0".equals(item.isExpiredType())) {
				personCouponExpired.setVisibility(View.GONE);
			} else if ("1".equals(item.isExpiredType())) {
				personCouponExpired.setVisibility(View.VISIBLE);
				personCouponExpired.setImageResource(R.drawable.coupon_expiring);
			}
		}
	}
}
