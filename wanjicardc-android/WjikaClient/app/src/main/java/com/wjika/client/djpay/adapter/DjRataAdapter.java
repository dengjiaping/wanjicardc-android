package com.wjika.client.djpay.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.client.result.VINParsedResult;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.DjpayRateChannelEntity;
import com.wjika.client.network.entities.DjpayRateEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/25.
 */

public class DjRataAdapter extends BaseAdapterNew<DjpayRateChannelEntity> {
	public DjRataAdapter(Context context, List<DjpayRateChannelEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.djpay_rate_weixin;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		DjpayRateChannelEntity djpayRateChannelEntity = getItem(position);

		SimpleDraweeView rateImg = ViewHolder.get(convertView,R.id.djpay_rate_img);

		TextView rate0 = ViewHolder.get(convertView, R.id.djpay_rate_rate);
		TextView charge0 = ViewHolder.get(convertView, R.id.djpay_rate_charge);
		TextView limit0 = ViewHolder.get(convertView, R.id.djpay_rate_limit);
/*
		TextView rate1 = ViewHolder.get(convertView, R.id.djpay_rate_rate1);
		TextView charge1 = ViewHolder.get(convertView, R.id.djpay_rate_charge1);
		TextView limit1 = ViewHolder.get(convertView, R.id.djpay_rate_limit1);*/

		String url = djpayRateChannelEntity.getLogo();
		if (!StringUtil.isEmpty(url)) {
			ImageUtils.setSmallImg(rateImg,url);
		}
		String rate = djpayRateChannelEntity.getDjpayRateEntity0().getRate();
		String charge = djpayRateChannelEntity.getDjpayRateEntity0().getCharge();
		String limits = djpayRateChannelEntity.getDjpayRateEntity0().getLimits();
		if (StringUtil.isEmpty(rate)) {
			rate0.setText("——");
		}else {
			rate0.setText(rate + "%");
		}

		if (StringUtil.isEmpty(charge)) {
			charge0.setText("——");
		}else {
			charge0.setText(charge + "元/笔");
		}

		if (StringUtil.isEmpty(limits)) {
			limit0.setText("——");
		}else {
			limit0.setText(limits + "元/笔");
		}




		/*rate1.setText(djpayRateChannelEntity.getDjpayRateEntity1().getRate()+"%");
		charge1.setText(djpayRateChannelEntity.getDjpayRateEntity1().getCharge()+"元/笔");
		limit1.setText(djpayRateChannelEntity.getDjpayRateEntity1().getLimits()+"元/笔");*/
	}
}
