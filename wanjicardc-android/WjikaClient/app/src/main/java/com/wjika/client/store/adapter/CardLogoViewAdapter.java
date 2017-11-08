package com.wjika.client.store.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CardMessageEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * 卡详情viewpager
 * Created by Leo_Zhang on 2016/7/7.
 */
public class CardLogoViewAdapter extends PagerAdapter {

	private List<View> viewList;
	private List<CardMessageEntity> cardMessageEntities;
	private Context context;

	public CardLogoViewAdapter(List<View> viewList, List<CardMessageEntity> cardMessageEntities, Context context) {
		this.viewList = viewList;
		this.cardMessageEntities = cardMessageEntities;
		this.context = context;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		CardMessageEntity cardMessageEntity = cardMessageEntities.get(position);
		View view = viewList.get(position);
		CardView cardBg = (CardView) view.findViewById(R.id.card_bg);
		SimpleDraweeView cardStore = (SimpleDraweeView) view.findViewById(R.id.card_store);
		TextView cardName = (TextView) view.findViewById(R.id.card_name);
		TextView cardFaceValue = (TextView) view.findViewById(R.id.card_face_value);
		if(cardMessageEntity != null){
			if(cardMessageEntity.getCardName()!=null){
				cardName.setText(cardMessageEntity.getCardName());
			}
			String url = cardMessageEntity.getCardLogo();
			if (!TextUtils.isEmpty(url) && !url.contains("?")) {
				ImageUtils.setSmallImg(cardStore,url);
			}
			if(cardMessageEntity.getFaceValue()!=null){
				cardFaceValue.setText(String.format(context.getResources().getString(R.string.money),cardMessageEntity.getFaceValue()));
			}
			switch (cardMessageEntity.getCardColor()){
				case BLUE:
					cardBg.setCardBackgroundColor(context.getResources().getColor(R.color.wjika_client_card_blue));
					break;
				case RED:
					cardBg.setCardBackgroundColor(context.getResources().getColor(R.color.wjika_client_card_red));
					break;
				case GREEN:
					cardBg.setCardBackgroundColor(context.getResources().getColor(R.color.wjika_client_card_green));
					break;
				case ORANGE:
					cardBg.setCardBackgroundColor(context.getResources().getColor(R.color.wjika_client_card_yellow));
					break;
			}
		}
		container.addView(viewList.get(position));
		return viewList.get(position);
	}
}
