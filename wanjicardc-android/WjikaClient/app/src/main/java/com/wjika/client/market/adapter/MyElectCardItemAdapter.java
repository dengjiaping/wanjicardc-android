package com.wjika.client.market.adapter;

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
import com.wjika.client.network.entities.MyElectCardItemEntity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

import static com.wjika.cardagent.client.R.id.mycard_item_bg;

/**
 * Created by ZHXIA on 2016/8/22
 */
public class MyElectCardItemAdapter extends BaseAdapterNew<MyElectCardItemEntity> {

	public static final String EXTRA_FROM = "extra_from";
	private View.OnClickListener listener;
	private Context context;

	public MyElectCardItemAdapter(Context context, List<MyElectCardItemEntity> mDatas, View.OnClickListener listener) {
		super(context, mDatas);
		this.listener = listener;
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.act_my_electcard_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MyElectCardItemEntity cardOrderItemEntity = getItem(position);
		if (cardOrderItemEntity != null) {
			View viewDate = ViewHolder.get(convertView, R.id.layout_date);
			TextView tvMyElectOrderTime = ViewHolder.get(convertView, R.id.tv_my_electcard_order_time);
			SimpleDraweeView imageMycardLogo = ViewHolder.get(convertView, R.id.mycard_logo);
			TextView imageMycardfaceValue = ViewHolder.get(convertView, R.id.mycard_face_value);
			CardView mycardItemBg = ViewHolder.get(convertView, mycard_item_bg);
			TextView txtMycardName = ViewHolder.get(convertView, R.id.tv_mycard_name);
			TextView txtMycardNum = ViewHolder.get(convertView, R.id.tv_mycard_num);
			TextView btnMycardExtract = ViewHolder.get(convertView, R.id.bt_mycard_extract);

        String myCardLogoUrl = cardOrderItemEntity.getMyCardlogoUrl();
        if (!StringUtil.isEmpty(cardOrderItemEntity.getBgcolor())) {
            mycardItemBg.setCardBackgroundColor(Color.parseColor(cardOrderItemEntity.getBgcolor()));
        } else {
            mycardItemBg.setCardBackgroundColor(Color.parseColor("#487AE0"));
        }

			if (StringUtil.isEmpty(cardOrderItemEntity.getDate())) {
				viewDate.setVisibility(View.GONE);
			} else {
				viewDate.setVisibility(View.VISIBLE);
				tvMyElectOrderTime.setText(cardOrderItemEntity.getDate());
			}

			ImageUtils.setSmallImg(imageMycardLogo, myCardLogoUrl);
			txtMycardName.setText(cardOrderItemEntity.getMyCardName());
			txtMycardNum.setText("数量：" + cardOrderItemEntity.getMyCardCount());
			imageMycardfaceValue.setText(context.getString(R.string.money, NumberFormatUtil.formatBun(cardOrderItemEntity.getMyCardFacePrice())));
			//提取
			btnMycardExtract.setTag(cardOrderItemEntity);
			btnMycardExtract.setOnClickListener(listener);
		}
	}
}