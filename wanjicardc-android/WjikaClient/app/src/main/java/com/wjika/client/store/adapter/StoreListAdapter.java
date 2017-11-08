package com.wjika.client.store.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by Leo_Zhang on 2016/5/18.
 * 店铺列表
 */
public class StoreListAdapter extends BaseAdapterNew<StoreEntity> {

	private String latitude;
	private String longitude;

	public void setIsLocation(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public StoreListAdapter(Context context, List<StoreEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.store_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		StoreEntity entity = getItem(position);
		SimpleDraweeView storeIcon = ViewHolder.get(convertView, R.id.store_icon);
		TextView storeName = ViewHolder.get(convertView, R.id.store_name);
		TextView storeCategroy = ViewHolder.get(convertView, R.id.store_category);
		CardView storeActivities = ViewHolder.get(convertView, R.id.store_activities);
		TextView storeOutOf = ViewHolder.get(convertView, R.id.store_out_of);
		TextView storeDistance = ViewHolder.get(convertView, R.id.store_distance);

		storeIcon.setImageBitmap(null);
		storeName.setText("");
		storeCategroy.setText("");
		storeDistance.setText("");
		storeOutOf.setText("");

		String url = entity.getImgPath();
		if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(storeIcon,url);
		}
		if (entity.isLimitForSale()) {
			storeActivities.setVisibility(View.VISIBLE);
		} else {
			storeActivities.setVisibility(View.GONE);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) storeCategroy.getLayoutParams();
			layoutParams.setMargins(0, 0, 0, 0);
			storeCategroy.setLayoutParams(layoutParams);
		}
		storeName.setText(entity.getName());
		storeCategroy.setText(entity.getCategory());
		storeOutOf.setText(getContext().getString(R.string.store_list_out_of, String.valueOf(entity.getTotalSale())));
		if (!latitude.equals(Double.toString(Double.MIN_VALUE)) && !longitude.equals(Double.toString(Double.MIN_VALUE))) {
			storeDistance.setText(entity.getDistanceStr());
		} else {
			storeDistance.setVisibility(View.GONE);
		}
	}
}
