package com.wjika.client.store.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ProductEntity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by Leo_Zhang on 2016/6/6.
 * 店铺
 */
public class StoreShowcaseAdapter extends BaseAdapterNew<ProductEntity> {

	public StoreShowcaseAdapter(Context context, List<ProductEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.buy_store_product_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ProductEntity entity = getItem(position);
		ImageView cardImgCover = ViewHolder.get(convertView, R.id.card_img_cover);
		CardView cardImgBg = ViewHolder.get(convertView, R.id.card_img_bg);
		TextView cardTxtName = ViewHolder.get(convertView, R.id.card_txt_name);
		TextView cardTxtStoreName = ViewHolder.get(convertView, R.id.card_txt_store_name);
		TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
		CardView storeLimitShop = ViewHolder.get(convertView, R.id.store_limit_shop);
		TextView txtCateGory = ViewHolder.get(convertView, R.id.store_face_value);
		TextView txtTotalSale = ViewHolder.get(convertView, R.id.txt_total_sale);
		TextView storeOriginalPrice = ViewHolder.get(convertView, R.id.store_original_price);
		TextView outOfCount = ViewHolder.get(convertView, R.id.out_of_count);
		LinearLayout llSaleProgress = ViewHolder.get(convertView, R.id.ll_sale_progress);
		TextView storeSaleProgress = ViewHolder.get(convertView, R.id.store_sale_progress);
		ProgressBar storeProgressSale = ViewHolder.get(convertView, R.id.store_progress_sale);

		cardTxtName.setText(entity.getName());
		cardTxtStoreName.setText(String.format(getContext().getString(R.string.buy_card_face_value), (int) NumberFormatUtil.string2Double(entity.getFacevalue())));
		txtName.setText(entity.getName());
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtCateGory.getLayoutParams();
		params.setMargins(0, 0, 0, 0);
		txtCateGory.setText(String.format(getContext().getString(R.string.buy_card_face_value), (int) NumberFormatUtil.string2Double(entity.getFacevalue())));

		String imgPath = entity.getImgPath();
		if (!TextUtils.isEmpty(imgPath) && !imgPath.contains("?")) {
			ImageUtils.setSmallImg(cardImgCover,imgPath);
		}
		if (entity.isLimitForSale()) {
			int progress = (int) Double.parseDouble(entity.getSalePercent());
			storeProgressSale.setProgress(progress);
			txtTotalSale.setText(String.format(getContext().getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(entity.getPrice())));
			storeOriginalPrice.setText(String.format(getContext().getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(entity.getSaleprice())));
			storeSaleProgress.setText(String.format(getContext().getString(R.string.store_action_progress_bar), progress));
			storeOriginalPrice.setVisibility(View.VISIBLE);
			outOfCount.setVisibility(View.GONE);
			storeLimitShop.setVisibility(View.VISIBLE);
			llSaleProgress.setVisibility(View.VISIBLE);
			storeOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			outOfCount.setText(String.format(getContext().getString(R.string.card_details_saled_label), entity.getSaledNum()));
			txtTotalSale.setText(String.format(getContext().getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(entity.getSaleprice())));
			storeOriginalPrice.setVisibility(View.GONE);
			outOfCount.setVisibility(View.VISIBLE);
			storeLimitShop.setVisibility(View.GONE);
			llSaleProgress.setVisibility(View.GONE);
		}

		switch (entity.getImgType()) {
			case BLUE:
				cardImgBg.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_blue));
				break;
			case RED:
				cardImgBg.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_red));
				break;
			case GREEN:
				cardImgBg.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_green));
				break;
			case ORANGE:
				cardImgBg.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_yellow));
				break;
		}
		storeOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
	}
}
