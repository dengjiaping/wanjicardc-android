package com.wjika.client.message.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.MyAssetEntity;

import java.util.List;

/**
 * 我的资产列表适配器
 * Created by bob on 16/5/23.
 */
public class MyAssetListAdapter extends BaseAdapterNew<MyAssetEntity> {

	public MyAssetListAdapter(Context context, List<MyAssetEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.system_message_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MyAssetEntity entity = getItem(position);
		if (entity != null) {
			TextView tvContent = ViewHolder.get(convertView, R.id.content_tv);
			TextView tvContentDate = ViewHolder.get(convertView, R.id.content_date_tv);

			tvContent.setText(entity.getReqParam());
			tvContentDate.setText(entity.getDate() + "      " + entity.getTime());
		}
	}
}
