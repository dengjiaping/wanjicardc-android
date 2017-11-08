package com.wjika.client.djpay.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.DjBankListEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/23.
 */

public class DjBankListAdapter extends BaseAdapterNew<DjBankListEntity> {

	private View.OnClickListener onClickListener;
	private Context context;

	public DjBankListAdapter(Context context, List<DjBankListEntity> mDatas, View.OnClickListener onClickListener) {
		super(context, mDatas);
		this.onClickListener = onClickListener;
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.djpay_banklist_item;
	}

	@Override
	protected void setViewData(final View convertView, int position) {
		DjBankListEntity djBankListEntity = getItem(position);
		SimpleDraweeView bankImg = ViewHolder.get(convertView, R.id.djpay_bank_img);
		TextView bankName = ViewHolder.get(convertView, R.id.djpay_bank_name);
		TextView bankNum = ViewHolder.get(convertView, R.id.djpay_bank_num);
		TextView bankType = ViewHolder.get(convertView, R.id.djpay_bank_type);
		TextView bankState = ViewHolder.get(convertView, R.id.djpay_bank_state);
		TextView bankSet = ViewHolder.get(convertView, R.id.djpay_bank_set);
		ImageView bankDelete = ViewHolder.get(convertView, R.id.djpay_bank_delete);

		String url = djBankListEntity.getBankImg();
		if (null != url) {
			ImageUtils.setSmallImg(bankImg, url);
		}
		bankName.setText(djBankListEntity.getBankName());
		String cardNum = djBankListEntity.getBankNum();
		if (cardNum.length() > 4) {
			String tempCardNum = cardNum.substring(0, 4);
			String tempCardNum2 = cardNum.substring(cardNum.length() - 4, cardNum.length());
			cardNum = tempCardNum + "**** ****" + tempCardNum2;
			bankNum.setText(cardNum);
		} else {
			bankNum.setText("****");
		}
		bankType.setText(djBankListEntity.getBankType());
//		bankState.setText(djBankListEntity.getBankState());
		bankState.setText("已绑定");

		if (djBankListEntity.isChecked()) {
			bankSet.setText("收款卡");
			bankSet.setTextColor(context.getResources().getColor(R.color.white));
			bankSet.setBackgroundResource(R.drawable.djpay_bank_setted_bg);
		} else {
			bankSet.setText("设为收款卡");
			bankSet.setTextColor(context.getResources().getColor(R.color.wjika_client_title_bg));
			bankSet.setBackgroundResource(R.drawable.djpay_bank_setting_bg);
		}

		bankSet.setOnClickListener(onClickListener);
		bankSet.setTag(djBankListEntity);
		bankDelete.setTag(djBankListEntity);
		bankDelete.setOnClickListener(onClickListener);
	}
}
