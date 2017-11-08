package com.wjika.client.djpay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.DjpayRateChannelEntity;

import java.util.List;

/**
 * Created by liuzhichao on 2016/11/29.
 */

public class DjChannleAdapter extends BaseAdapterNew<DjpayRateChannelEntity> {

	public DjChannleAdapter(Context context, List<DjpayRateChannelEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.item_dj_channle;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		DjpayRateChannelEntity channelEntity = getItem(position);
		SimpleDraweeView itemChannelLogo = ViewHolder.get(convertView, R.id.item_channel_logo);
		TextView itemChannelName = ViewHolder.get(convertView, R.id.item_channel_name);
		TextView itemChannelDesc = ViewHolder.get(convertView, R.id.item_channel_desc);
		View itemChannelNow = ViewHolder.get(convertView, R.id.item_channel_now);

		if (channelEntity != null) {
			if (!StringUtil.isEmpty(channelEntity.getLogo())) {
				itemChannelLogo.setImageURI(Uri.parse(channelEntity.getLogo()));
			}
			itemChannelName.setText(channelEntity.getChannelName());
			itemChannelDesc.setText(channelEntity.getDesc());

			if (channelEntity.getDjpayRateEntity0() != null) {
				itemChannelNow.setVisibility(View.VISIBLE);
			} else {
				itemChannelNow.setVisibility(View.GONE);
			}
		}
	}
}
