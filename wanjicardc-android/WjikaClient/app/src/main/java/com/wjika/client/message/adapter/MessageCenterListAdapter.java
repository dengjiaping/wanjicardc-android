package com.wjika.client.message.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.StringUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.MessageCenterEntity;

import java.util.List;

/**
 * Created by bob on 16/5/23.
 * 消息中心列表
 */
public class MessageCenterListAdapter extends BaseAdapterNew<MessageCenterEntity> {

	public MessageCenterListAdapter(Context context, List<MessageCenterEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.message_center_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MessageCenterEntity entity = getItem(position);
		if (entity != null) {
			TextView tvMsgTitle = ViewHolder.get(convertView, R.id.message_title_tv);
			TextView tvMsgContent = ViewHolder.get(convertView, R.id.message_content_tv);
			ImageView ivMsgIcon = ViewHolder.get(convertView, R.id.message_icon);
			TextView tvRedCircle = ViewHolder.get(convertView, R.id.tvRedCircle);
			TextView tvMoney = ViewHolder.get(convertView, R.id.tv_money);

			ivMsgIcon.setBackgroundResource(entity.getIconResId());
			tvMsgTitle.setText(getContext().getResources().getString(entity.getTitleResIdByType()));
			if (!StringUtil.isEmpty(entity.getReqParam())) {
				tvMsgContent.setText(entity.getReqParam());
			} else {
				tvMsgContent.setText(getContext().getResources().getString(R.string.message_center_no_message));
			}

			if ("0".equals(entity.getIsRead())) {
				tvRedCircle.setVisibility(View.VISIBLE);
			} else {
				tvRedCircle.setVisibility(View.GONE);
			}

			if (MessageCenterEntity.TYPE_CONSUME_MESSAGE.equals(entity.getType()) && !StringUtil.isEmpty(entity.getReqParam())) {
				tvMoney.setVisibility(View.VISIBLE);
				tvMoney.setText("￥ " + entity.getMoney());
			} else {
				tvMoney.setVisibility(View.GONE);
			}
		}
	}
}
