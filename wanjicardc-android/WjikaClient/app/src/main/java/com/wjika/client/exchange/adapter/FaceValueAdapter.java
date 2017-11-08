package com.wjika.client.exchange.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.widget.Utils;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/12/1.
 */

public class FaceValueAdapter extends BaseAdapterNew<ExchangeFacevalueEntity> {
	private Context context;
	private Resources res;

	public FaceValueAdapter(Context context, List<ExchangeFacevalueEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
		res = context.getResources();
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.exchange_facevalue_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ExchangeFacevalueEntity entity = getItem(position);
		LinearLayout gridItem = ViewHolder.get(convertView, R.id.exchange_griditem);
		TextView faceValue = ViewHolder.get(convertView, R.id.exchange_face_facevalue);
		TextView bunNum = ViewHolder.get(convertView, R.id.exchange_face_bunnum);

		faceValue.setText(context.getString(R.string.money, NumberFormatUtil.formatBun(entity.getFaceValue())) );
		bunNum.setText(context.getString(R.string.exchange_can_exchange_baozi, NumberFormatUtil.formatBun(entity.getBunNum())));
		if (entity.isChecked()) {
			gridItem.setBackgroundResource(R.drawable.exchange_griditem_checked);
			faceValue.setTextColor(res.getColor(R.color.wjika_client_primary_visual_blue));
			bunNum.setTextColor(res.getColor(R.color.wjika_client_primary_visual_blue));
		} else {
			gridItem.setBackgroundResource(R.drawable.exchange_griditem_normal);
			faceValue.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
			bunNum.setTextColor(res.getColor(R.color.wjika_client_middle_gray));
		}
	}
}
