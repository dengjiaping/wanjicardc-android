package com.wjika.client.store.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;


/**
 * Created by jacktian on 15/9/11.
 * 支持店铺
 */
public class SupportStoreAdapter extends BaseAdapterNew<StoreEntity> {

	private Context context;
	private String latitude;
	private String longitude;

	public SupportStoreAdapter(Context context, List<StoreEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
	}

	public void setLocation(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.buy_support_store_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		StoreEntity entity = getItem(position);
		SimpleDraweeView storeIconSupport = ViewHolder.get(convertView, R.id.store_icon_support);
		TextView storeNameSupport = ViewHolder.get(convertView, R.id.store_name_support);
		TextView supportAddress = ViewHolder.get(convertView, R.id.support_address);
		TextView storeOutOfSupport = ViewHolder.get(convertView, R.id.store_out_of_support);
		TextView storeDistanceSupport = ViewHolder.get(convertView, R.id.store_distance_support);

		storeNameSupport.setText(entity.getName());
		supportAddress.setText(context.getResources().getString(R.string.store_address, entity.getAddress()));
		storeOutOfSupport.setText(context.getResources().getString(R.string.card_details_saled_label, String.valueOf(entity.getTotalSale())));
		if (!latitude.equals(Double.toString(Double.MIN_VALUE)) && !longitude.equals(Double.toString(Double.MIN_VALUE))) {
			storeDistanceSupport.setText(entity.getDistanceStr());
		} else {
			storeDistanceSupport.setVisibility(View.GONE);
		}
		storeIconSupport.setImageBitmap(null);
		String url = entity.getImgPath();
		if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(storeIconSupport,url);
		}
	}
}
