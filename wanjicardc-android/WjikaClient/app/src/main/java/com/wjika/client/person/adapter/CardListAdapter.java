package com.wjika.client.person.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CardEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/6 18:33.
 * 充值卡列表
 */
public class CardListAdapter extends BaseAdapterNew<CardEntity> {

	private View.OnClickListener onClickListener;
	private Resources res;

	public CardListAdapter(Context context, List<CardEntity> mDatas, View.OnClickListener onClickListener) {
		super(context, mDatas);
		res = context.getResources();
		this.onClickListener = onClickListener;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.person_card_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CardEntity item = getItem(position);
		CardView imgIconBG = ViewHolder.get(convertView, R.id.card_img_bg);
		ImageView imgCardLogo = ViewHolder.get(convertView, R.id.card_img_cover);
		TextView cardTxtName = ViewHolder.get(convertView, R.id.card_txt_name);
		TextView cardTxtStoreName = ViewHolder.get(convertView, R.id.card_txt_store_name);

		TextView personCardName = (TextView) convertView.findViewById(R.id.person_card_name);
		TextView personCardBalance = (TextView) convertView.findViewById(R.id.person_card_balance);
		TextView personCardPay = (TextView) convertView.findViewById(R.id.person_card_pay);

		switch (item.getImgType()){
			case BLUE:
				imgIconBG.setCardBackgroundColor((res.getColor(R.color.wjika_client_card_red)));
				break;
			case RED:
				imgIconBG.setCardBackgroundColor((res.getColor(R.color.wjika_client_card_yellow)));
				break;
			case GREEN:
				imgIconBG.setCardBackgroundColor((res.getColor(R.color.wjika_client_card_blue)));
				break;
			case ORANGE:
				imgIconBG.setCardBackgroundColor((res.getColor(R.color.wjika_client_card_green)));
				break;
		}
        String url = item.getImgPath();
        if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(imgCardLogo,url);
        }

		cardTxtName.setText(item.getName());
		cardTxtStoreName.setText(String.format(res.getString(R.string.person_recharge_balance),item.getBalance()));

		personCardName.setText(item.getName());
		SpannableString spannableString = new SpannableString(String.format(res.getString(R.string.person_recharge_balance),item.getBalance()));
		spannableString.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_price_red)),
				3, spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//flags前面包括后面不包括
		spannableString.setSpan(new RelativeSizeSpan(1.5f), 3, spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		personCardBalance.setText(spannableString);
		personCardPay.setTag(item);
		personCardPay.setOnClickListener(onClickListener);
	}
}
