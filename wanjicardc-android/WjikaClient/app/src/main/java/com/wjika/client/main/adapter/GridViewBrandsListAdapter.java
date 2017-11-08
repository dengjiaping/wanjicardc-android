package com.wjika.client.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.MainBrandsEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by ZHXIA on 2016/7/4
 * 品牌墙列表
 */
public class GridViewBrandsListAdapter extends BaseAdapterNew<MainBrandsEntity> {

	private boolean isMore;
	private Context context;

	public GridViewBrandsListAdapter(Context context, List<MainBrandsEntity> mDatas, boolean isMore) {
		super(context, mDatas);
		this.isMore = isMore;
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		if (isMore) {
			return R.layout.home_brands_more_list_item;
		} else {
			return R.layout.home_brands_list_item;
		}
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MainBrandsEntity itemEntity = getItem(position);
		if (itemEntity != null) {
			CardView cvBrandEcardBg = ViewHolder.get(convertView, R.id.cv_brand_ecard_bg);
			SimpleDraweeView dvBgSurfaceLogo = ViewHolder.get(convertView, R.id.dv_brand_item_surface_logo);
			TextView tvBrandName = ViewHolder.get(convertView, R.id.tv_brand_item_name);

			if (MainBrandsEntity.TYPE_OF_STORE == itemEntity.getType()) {
				int random = new Random().nextInt(12);
				switch (random) {
					case 0:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_red1)));
						break;
					case 1:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_red2)));
						break;
					case 2:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_red3)));
						break;
					case 3:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_green1)));
						break;
					case 4:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_green2)));
						break;
					case 5:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_green3)));
						break;
					case 6:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_pink)));
						break;
					case 7:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_purple)));
						break;
					case 8:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_yellow)));
						break;
					case 9:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_orange)));
						break;
					case 10:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_black)));
						break;
					case 11:
						cvBrandEcardBg.setCardBackgroundColor((context.getResources().getColor(R.color.wjika_client_brand_blue)));
						break;
				}
			} else if (MainBrandsEntity.TYPE_OF_ECARD == itemEntity.getType()) {
				if (!StringUtil.isEmpty(itemEntity.getBgcolor())) {
					cvBrandEcardBg.setCardBackgroundColor(Color.parseColor(itemEntity.getBgcolor()));
				} else {
					cvBrandEcardBg.setCardBackgroundColor(context.getResources().getColor(R.color.wjika_client_card_blue));
				}
			}

			String logoUrl = itemEntity.getLogoImg();
			String brandName = itemEntity.getBrandName();
			ImageUtils.setSmallImg(dvBgSurfaceLogo, logoUrl);
			tvBrandName.setText(brandName);
		}
	}
}