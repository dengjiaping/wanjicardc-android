package com.wjika.client.store.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.OptionEntity;

import java.util.List;

public class LeftAdapter extends BaseAdapterNew<OptionEntity> {

	public LeftAdapter(Context context, List<OptionEntity> mDatas) {
		super(context, mDatas);
	}


	@Override
	protected int getResourceId(int Position) {
		return R.layout.distance_spinner_left_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		OptionEntity entity = this.getItem(position);
		if (entity != null) {
			TextView tv = ViewHolder.get(convertView, R.id.tv);
			tv.setText(entity.getName());
			if (entity.isSelected()) {
				tv.setTextColor(this.getContext().getResources().getColor(R.color.wjika_client_title_bg));
			} else {
				tv.setTextColor(this.getContext().getResources().getColor(android.R.color.black));
			}
		}
	}

	public void setSelectedPosition(int pos) {
		for (int i = 0; i < getCount(); i++) {
			OptionEntity item = getItem(i);
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
