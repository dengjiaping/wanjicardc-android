package com.wjika.client.djpay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.DjBillEntity;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/23.
 */

public class DjBillListAdapter extends BaseAdapterNew<DjBillEntity> {
	public DjBillListAdapter(Context context, List<DjBillEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.djpay_billlist_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		DjBillEntity entity = getItem(position);
		TextView orderNum = ViewHolder.get(convertView,R.id.djpay_bill_ordernum);
		TextView orderState = ViewHolder.get(convertView,R.id.djpay_bill_orderstate);
		TextView orderMoney = ViewHolder.get(convertView,R.id.djpay_bill_orderget);
		TextView orderChannel = ViewHolder.get(convertView,R.id.djpay_bill_orderchannel);
		TextView orderFee = ViewHolder.get(convertView,R.id.djpay_bill_orderfee);
		TextView detailTime = ViewHolder.get(convertView,R.id.djpay_billitem_detailtime);

		orderNum.setText("订单编号：" + entity.getOrderNum());
		//订单状态（0：待支付，1：交易成功，2：交易关闭）
		if ("0".equals(entity.getOrderState())) {
			orderState.setText("待支付");
			orderState.setTextColor(getContext().getResources().getColor(R.color.wjika_client_title_bg));
		} else if ("1".equals(entity.getOrderState())) {
			orderState.setText("交易成功");
			orderState.setTextColor(getContext().getResources().getColor(R.color.wjika_client_introduce_words));
		} else {
			orderState.setText("交易关闭");
			orderState.setTextColor(getContext().getResources().getColor(R.color.wjika_client_introduce_words));
		}

		orderMoney.setText("收款：￥" + NumberFormatUtil.formatMoney(entity.getOrderMoney()));
		orderChannel.setText("通道：" + entity.getOrderChannel());
		orderFee.setText("手续费：￥" + NumberFormatUtil.formatMoney(entity.getOrderFee()));
		detailTime.setText(entity.getOrderTime());
	}
}
