package com.wjika.client.person.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.SupportBankEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/7/19.
 * 支持银行列表
 */
public class BankSupportAdapter extends BaseAdapterNew<SupportBankEntity> {

	public BankSupportAdapter(Context context, List<SupportBankEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.person_auth_bank_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		SupportBankEntity item = getItem(position);
		TextView bankSupportTxt = ViewHolder.get(convertView, R.id.bank_support_txt);
		ImageView bankSupportImg = ViewHolder.get(convertView,R.id.bank_support_img);
		bankSupportTxt.setText(item.getName());
		String url = item.getLogoImg();
		if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(bankSupportImg,url);
		}
	}
}