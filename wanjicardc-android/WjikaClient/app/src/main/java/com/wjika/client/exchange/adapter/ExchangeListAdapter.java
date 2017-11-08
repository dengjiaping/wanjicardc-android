package com.wjika.client.exchange.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.StoreImgEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/30.
 */

public class ExchangeListAdapter extends BaseAdapterNew<ExchangeCardEntity> {

	private View.OnClickListener onClickListener;
	public ExchangeListAdapter(Context context, List<ExchangeCardEntity> mDatas,View.OnClickListener onClickListener) {
		super(context, mDatas);
		this.onClickListener = onClickListener;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.exchange_cardlist_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ExchangeCardEntity entity = getItem(position);
		CardView cardView = ViewHolder.get(convertView,R.id.exchange_carditem_cardview);
		SimpleDraweeView logo = ViewHolder.get(convertView,R.id.exchange_carditem_logo);
		TextView cardName = ViewHolder.get(convertView,R.id.exchange_carditem_name);
		TextView exchange = ViewHolder.get(convertView,R.id.exchange_carditem_exchange);

		String url = entity.getLogoUrl();
		if (null != url) {
			ImageUtils.setSmallImg(logo,url);
		}
		cardName.setText(entity.getCardName());
		exchange.setTag(entity);
		exchange.setOnClickListener(onClickListener);

		String color = entity.getCardColorValue();
		if (null != color) {
			cardView.setCardBackgroundColor(Color.parseColor(color));
		}
	}
}
