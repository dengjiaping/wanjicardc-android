package com.wjika.client.store.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CityEntity;

import java.util.List;

public class RightAdapter extends BaseAdapterNew<CityEntity> {

	private int pos;
	private Context context;

	public RightAdapter(Context context, List<CityEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.distance_spinner_right_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CityEntity entity = this.getItem(position);
		if (entity != null) {
			TextView tv = ViewHolder.get(convertView, R.id.tv);
			if (position == 0) {
				tv.setText(String.format(context.getResources().getString(R.string.all), entity.getName()));
			} else {
				tv.setText(entity.getName());
			}

			if (pos == position) {
				tv.setTextColor(this.getContext().getResources().getColor(R.color.wjika_client_title_bg));
			} else {
				tv.setTextColor(this.getContext().getResources().getColor(android.R.color.black));
			}
		}
	}

	public void setSelectedPosition(int pos) {
		this.pos = pos;
	}
}
