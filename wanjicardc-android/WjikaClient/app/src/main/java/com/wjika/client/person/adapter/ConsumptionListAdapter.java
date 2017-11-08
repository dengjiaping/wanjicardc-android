package com.wjika.client.person.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;

import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ConsumptionPageEntity;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/6 15:37.
 * 消费记录adapter
 */
public class ConsumptionListAdapter extends BaseAdapterNew<ConsumptionPageEntity.ConsumptionEntity> {

	private Resources res;

	public ConsumptionListAdapter(Context context, List<ConsumptionPageEntity.ConsumptionEntity> mDatas) {
		super(context, mDatas);
		res = context.getResources();
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.person_consumption_list_item;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ConsumptionPageEntity.ConsumptionEntity item = getItem(position);
		TextView personConsumptionDate =  ViewHolder.get(convertView, R.id.person_consumption_date);
		TextView personConsumptionAmount =  ViewHolder.get(convertView, R.id.person_consumption_amount);
		TextView personConsumptionNo =  ViewHolder.get(convertView, R.id.person_consumption_no);
//        TextView personConsumptionStatus = ViewHolder.get(convertView, R.id.person_consumption_status);
		TextView personConsumptionName =  ViewHolder.get(convertView, R.id.person_consumption_name);

//		long time = TimeUtil.parseTime(item.getDate(), TimeUtil.TIME_FORMAT_16);
		personConsumptionDate.setText(item.getDate());
		personConsumptionAmount.setText("-" + String.format(res.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(item.getAmount())));
		personConsumptionNo.setText(String.format(res.getString(R.string.person_consume_no),item.getPaymentNo()));
		personConsumptionName.setText(item.getMerName());
	}
}
