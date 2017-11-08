package com.wjika.client.market.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.widget.BaseRecycleAdapter;
import com.common.widget.RecyclerViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/8/25 16:05.
 * 电子卡列表
 */
public class ECardAdapter extends BaseRecycleAdapter<ECardEntity>{

	public static final int FLAG_CARD_STORE = 0x1;//卡商城

	private View.OnClickListener onClickListener;
	private int flag;

	public ECardAdapter(List<ECardEntity> datas, View.OnClickListener onClickListener) {
		super(datas);
		this.onClickListener = onClickListener;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new RecyclerViewHolder(parent, R.layout.item_ecard);
	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
		ECardEntity eCardEntity = getItemData(position);

		CardView ecardItemEcard = holder.getView(R.id.ecard_item_ecard);
		if (!StringUtil.isEmpty(eCardEntity.getBgcolor())) {
			ecardItemEcard.setCardBackgroundColor(Color.parseColor(eCardEntity.getBgcolor()));
		} else {
			ecardItemEcard.setCardBackgroundColor(Color.parseColor("#487AE0"));
		}
		Context context = ecardItemEcard.getContext();
		int width = (DeviceUtil.getWidth(context) - DeviceUtil.dp_to_px(context, 12 * 3)) / 2;

		TextView ecardItemValue = holder.getView(R.id.ecard_item_value);
		ecardItemValue.setText(context.getString(R.string.money, NumberFormatUtil.formatBun(eCardEntity.getFacePrice())));
		ecardItemValue.getPaint().setFakeBoldText(true);

		int off = 0;
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//低于5.0的SDK版本，CardView会自己设置圆角padding，所以给高度加个值
			off = (int)((1-Math.cos(Math.PI/4))* CommonTools.dp2px(context,5)*4 + 0.5);
		}
		int height = width / 16 * 9 + off;
		ViewGroup.LayoutParams layoutParams = ecardItemEcard.getLayoutParams();
		layoutParams.height = height;
		ecardItemEcard.setLayoutParams(layoutParams);

		ImageUtils.setSmallImg((ImageView) holder.getView(R.id.ecard_item_logo), eCardEntity.getLogoUrl());

		holder.setText(R.id.ecard_item_name, eCardEntity.getName());

		TextView ecardItemPrice = holder.getView(R.id.ecard_item_price);
		if (FLAG_CARD_STORE == flag) {
			ecardItemPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			ecardItemPrice.setTextColor(context.getResources().getColor(R.color.card_store_price));
			holder.setTextColorRes(R.id.ecard_item_limit, R.color.person_main_baozi_num);
			ecardItemPrice.setText(context.getString(R.string.person_order_detail_buy_amount, NumberFormatUtil.formatMoney(eCardEntity.getRMBSalePrice())));
			holder.setText(R.id.ecard_item_limit, eCardEntity.getDiscount());
			if (ECardEntity.ACTIVITY_TYPE_LIMIT == eCardEntity.getActivityType()) {
				holder.setVisible(R.id.ecard_item_tag, View.VISIBLE);
			} else {
				holder.setVisible(R.id.ecard_item_tag, View.INVISIBLE);
			}
		} else {
			ecardItemPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ecard_wallet_ic, 0, 0, 0);
			ecardItemPrice.setTextColor(context.getResources().getColor(R.color.person_main_baozi_num));
			holder.setTextColorRes(R.id.ecard_item_limit, R.color.ecard_limit_text);
			ecardItemPrice.setText(NumberFormatUtil.formatBun(eCardEntity.getSalePrice()));
			if (ECardEntity.ACTIVITY_TYPE_LIMIT == eCardEntity.getActivityType()) {
				holder.setVisible(R.id.ecard_item_limit, View.VISIBLE);
				holder.setVisible(R.id.ecard_item_tag, View.VISIBLE);
				holder.setText(R.id.ecard_item_limit, context.getString(R.string.ecard_limit, String.valueOf(eCardEntity.getLimitCount())));
			} else {
				holder.setVisible(R.id.ecard_item_limit, View.INVISIBLE);
				holder.setVisible(R.id.ecard_item_tag, View.INVISIBLE);
			}
		}

		holder.setTag(R.id.ecard_item_layout, eCardEntity);
		holder.setOnClickListener(R.id.ecard_item_layout, onClickListener);
	}
}
