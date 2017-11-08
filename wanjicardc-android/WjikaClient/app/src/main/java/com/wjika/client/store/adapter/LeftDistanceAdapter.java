package com.wjika.client.store.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CityEntity;

import java.util.List;

/**
 * Created by jacktian on 15/9/14.
 * 城市选择
 */
public class LeftDistanceAdapter extends BaseAdapterNew<CityEntity> {

	private Context context;

	public LeftDistanceAdapter(Context context, List<CityEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.distance_spinner_left_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CityEntity entity = this.getItem(position);
		if (entity != null) {
			TextView tv = ViewHolder.get(convertView, R.id.tv);
			View view = ViewHolder.get(convertView, R.id.left_view);
			View view1 = ViewHolder.get(convertView, R.id.right_view);
			if (position == 0) {
				tv.setText(String.format(context.getResources().getString(R.string.all), entity.getName()));
			} else {
				tv.setText(entity.getName());
			}

			if (entity.isSelected()) {
				view.setVisibility(View.VISIBLE);
				view1.setVisibility(View.INVISIBLE);
				tv.setTextColor(this.getContext().getResources().getColor(R.color.wjika_client_title_bg));
				convertView.setBackgroundColor(this.getContext().getResources().getColor(R.color.main_back));
			} else {
				view.setVisibility(View.INVISIBLE);
				view1.setVisibility(View.VISIBLE);
				tv.setTextColor(this.getContext().getResources().getColor(android.R.color.black));
				convertView.setBackgroundColor(this.getContext().getResources().getColor(R.color.white));
			}
		}
	}

	public void setSelectedPosition(int pos) {
		for (int i = 0; i < getCount(); i++) {
			CityEntity item = getItem(i);
			if (i == pos) {
				if (item != null) {
					item.setIsSelected(true);
				}
			} else {
				if (item != null) {
					item.setIsSelected(false);
				}
			}
		}
		notifyDataSetChanged();
	}
}
