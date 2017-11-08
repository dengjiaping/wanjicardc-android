package com.wjika.client.cardpackage.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CardEntity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/5/5 11:31.
 * 卡列表
 */
public class CardAdapter extends BaseAdapterNew<CardEntity> {

	public CardAdapter(Context context, List<CardEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.card_pkg_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CardEntity cardEntity = getItem(position);
		SimpleDraweeView cardItemLogo = ViewHolder.get(convertView, R.id.card_item_logo);
		TextView cardItemName = ViewHolder.get(convertView, R.id.card_item_name);
		TextView cardItemBalance = ViewHolder.get(convertView, R.id.card_item_balance);

		switch (cardEntity.getImgType()) {
			case BLUE:
				convertView.setBackgroundResource(R.drawable.card_pkg_blue_bg);
				break;
			case RED:
				convertView.setBackgroundResource(R.drawable.card_pkg_red_bg);
				break;
			case ORANGE:
				convertView.setBackgroundResource(R.drawable.card_pkg_yellow_bg);
				break;
			case GREEN:
				convertView.setBackgroundResource(R.drawable.card_pkg_green_bg);
				break;
		}
		ImageUtils.setSmallImg(cardItemLogo, cardEntity.getImgPath());
		if (!TextUtils.isEmpty(cardEntity.getName())) {
			cardItemName.setText(cardEntity.getName());
		}
		if (!TextUtils.isEmpty(cardEntity.getBalance())) {
			cardItemBalance.setText(getContext().getString(R.string.card_pkg_balance, NumberFormatUtil.formatMoney(cardEntity.getBalance())));
		}
	}
}
