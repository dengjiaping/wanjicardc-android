package com.wjika.client.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.utils.CityUtils;

import java.util.List;

/**
 * Created by jacktian on 15/9/23.
 * 城市
 */
public class CityAdapter extends BaseAdapterNew<CityEntity> {

	public CityAdapter(Context context, List<CityEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.store_city_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CityEntity entity = getItem(position);
		if (entity != null) {
			TextView txtCityName = ViewHolder.get(convertView, R.id.txt_city_name);
			txtCityName.setText(CityUtils.getCityShortName(entity.getName()));
		}
	}
}
