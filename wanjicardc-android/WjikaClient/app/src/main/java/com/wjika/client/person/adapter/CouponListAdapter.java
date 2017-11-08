package com.wjika.client.person.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CouponEntity;

import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/7 18:45.
 * 优惠券
 */
public class CouponListAdapter extends BaseAdapterNew<CouponEntity> {

	private Resources res;
	private List<CouponEntity> mDatas;

	public CouponListAdapter(Context context, List<CouponEntity> mDatas) {
		super(context, mDatas);
		res = context.getResources();
		this.mDatas = mDatas;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.person_coupon_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CouponEntity item = getItem(position);
		View viewSplit = convertView.findViewById(R.id.person_coupon_split);
		RelativeLayout personCouponContent = (RelativeLayout) convertView.findViewById(R.id.person_coupon_content);
		TextView personCouponName = (TextView) convertView.findViewById(R.id.person_coupon_name);
		TextView personCouponScope = (TextView) convertView.findViewById(R.id.person_coupon_scope);
		TextView personCouponValue = (TextView) convertView.findViewById(R.id.person_coupon_value);
		TextView personCouponValidity = (TextView) convertView.findViewById(R.id.person_coupon_validity);
		ImageView personCouponExpired = (ImageView) convertView.findViewById(R.id.person_coupon_expired);

		if ((mDatas.size() - 1 ) == position) {
			viewSplit.setVisibility(View.GONE);
		} else {
			viewSplit.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(item.getName()))
			personCouponName.setText(item.getName());
		if (!TextUtils.isEmpty(item.getDesc()))
			personCouponScope.setText(item.getDesc());

		SpannableStringBuilder ssb = new SpannableStringBuilder(String.format(res.getString(R.string.person_order_detail_buy_amount), item.getAmount()));
		ssb.setSpan(new RelativeSizeSpan(0.4f), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		personCouponValue.setText(ssb);
		if (!TextUtils.isEmpty(item.getValidTime())){
//			long time = TimeUtil.parseTime(item.getValidTime(), TimeUtil.TIME_FORMAT_ONE);
			personCouponValidity.setText(String.format(res.getString(R.string.person_coupon_validity), item.getValidTime()));
		}

		/*if (item.isExpiredType()){
			personCouponRift.setVisibility(View.GONE);
//			personCouponContent.setBackgroundResource(R.drawable.coupon_normal_bg);
			personCouponName.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
			personCouponValue.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
		}else {
			personCouponRift.setVisibility(View.VISIBLE);
//			personCouponContent.setBackgroundResource(R.drawable.coupon_rift_bg);
			personCouponName.setTextColor(res.getColor(R.color.wjika_client_gray));
			personCouponValue.setTextColor(res.getColor(R.color.wjika_client_gray));
		}*/
		if ("2".equals(item.isCurrentStatus()) || "3".equals(item.isCurrentStatus())) {
			personCouponContent.setBackgroundResource(R.drawable.coupon_rift_bg);
			personCouponName.setTextColor(res.getColor(R.color.wjika_client_gray));
			personCouponValue.setTextColor(res.getColor(R.color.wjika_client_gray));
			personCouponExpired.setVisibility(View.VISIBLE);
			personCouponExpired.setImageResource(R.drawable.coupon_used);
		} else if ("1".equals(item.isCurrentStatus())){
			if ("0".equals(item.isExpiredType())) {
				personCouponContent.setBackgroundResource(R.drawable.coupon_normal_bg);
				personCouponName.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
				personCouponValue.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
				personCouponExpired.setVisibility(View.GONE);
			} else if ("1".equals(item.isExpiredType())) {
				personCouponContent.setBackgroundResource(R.drawable.coupon_normal_bg);
				personCouponName.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
				personCouponValue.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
				personCouponExpired.setVisibility(View.VISIBLE);
				personCouponExpired.setImageResource(R.drawable.coupon_expiring);
			} else if ("2".equals(item.isExpiredType())){
				personCouponContent.setBackgroundResource(R.drawable.coupon_rift_bg);
				personCouponName.setTextColor(res.getColor(R.color.wjika_client_gray));
				personCouponValue.setTextColor(res.getColor(R.color.wjika_client_gray));
				personCouponExpired.setVisibility(View.VISIBLE);
				personCouponExpired.setImageResource(R.drawable.coupon_expired);
			}
		}
	}
}
