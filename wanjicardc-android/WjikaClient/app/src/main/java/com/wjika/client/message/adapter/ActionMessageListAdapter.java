package com.wjika.client.message.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.MyAssetEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by bob on 16/5/23.
 * 活动消息列表适配器
 */
public class ActionMessageListAdapter extends BaseAdapterNew<MyAssetEntity> {

	public ActionMessageListAdapter(Context context, List<MyAssetEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.action_message_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MyAssetEntity entity = getItem(position);
		if (entity != null) {
			ImageView imgAction = ViewHolder.get(convertView, R.id.action_message_image);
			TextView tvActionDate = ViewHolder.get(convertView, R.id.message_date_tv);
			TextView tvActionTime = ViewHolder.get(convertView, R.id.message_time_tv);
			String url = entity.getPcUrl();
			if (!TextUtils.isEmpty(url) && !url.contains("?")) {
				ImageUtils.setSmallImg(imgAction,url);
			}
			tvActionDate.setText(entity.getDate());
			tvActionTime.setText(entity.getTime());
		}
	}
}
