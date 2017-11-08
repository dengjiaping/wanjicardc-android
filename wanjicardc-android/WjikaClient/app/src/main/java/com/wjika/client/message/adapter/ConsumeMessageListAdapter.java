package com.wjika.client.message.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.MyAssetEntity;

import java.util.List;

/**
 * 消费信息列表适配器
 * Created by bob on 16/5/23.
 */
public class ConsumeMessageListAdapter extends BaseAdapterNew<MyAssetEntity> {

	public ConsumeMessageListAdapter(Context context, List<MyAssetEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.consume_message_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MyAssetEntity entity = getItem(position);
		if (entity != null) {
			TextView tvConsumeStore = ViewHolder.get(convertView, R.id.consume_store_name_tv);
			TextView tvConsumeAmount = ViewHolder.get(convertView, R.id.consume_amount_tv);
			TextView tvDate = ViewHolder.get(convertView, R.id.content_date_tv);

			tvConsumeStore.setText(entity.getReqParam());
			tvConsumeAmount.setText("￥ " + entity.getMoney());
			tvDate.setText(entity.getDate() + "      " + entity.getTime());
		}
	}
}
