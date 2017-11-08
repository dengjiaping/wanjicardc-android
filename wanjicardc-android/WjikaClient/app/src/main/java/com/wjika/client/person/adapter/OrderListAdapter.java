package com.wjika.client.person.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.OrderPageEntity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/8/31 18:25.
 * 订单列表adapter
 */
public class OrderListAdapter extends BaseAdapterNew<OrderPageEntity.OrderEntity> {

	private static final int CARD = 1;
	private static final int ELECTRON = 4;// 电子卡
	private static final int BAOZI = 3;// 包子充值

	private View.OnClickListener onClickListener;
	private Resources res;
	private int type;
	private OrderPageEntity.OrderEntity orderEntity;

	public OrderListAdapter(Context context, List<OrderPageEntity.OrderEntity> mDatas, View.OnClickListener onClickListener) {
		super(context, mDatas);
		res = context.getResources();
		this.onClickListener = onClickListener;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		type = getItemViewType(position);
		if(convertView == null || position > 0 && type != getItem(position -1).getType()) {
			if(type == CARD) {
				convertView = View.inflate(getContext(),R.layout.person_order_list_item_card,null);
			}else if(type == ELECTRON){
				convertView = View.inflate(getContext(),R.layout.person_order_list_item_electron,null);
			}else if(type == BAOZI){
				convertView = View.inflate(getContext(),R.layout.person_order_list_item_baozi,null);
			}else{
				convertView = View.inflate(getContext(),R.layout.person_order_list_item_card,null);
			}
		}
		try {
			setViewData(convertView, position);
		} catch (Exception e) {
			LogUtil.e("adapter setViewData error "+this.getClass().getSimpleName(),e.toString());
		}

		return convertView;
	}

	@Override
	protected int getResourceId(int Position) {
		return -1;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		orderEntity = getItem(position);
		if(type == CARD){
			setCardData(convertView);
		}else if(type == ELECTRON){
			setElectronData(convertView);
		} else if(type == BAOZI){
			setBaoziData(convertView);
		}else {
			setCardData(convertView);
		}
	}

	private void setBaoziData(View convertView) {
		TextView baoziOrderItemNo = ViewHolder.get(convertView,R.id.person_order_item_no);
		TextView baoziOrderItemStatus = ViewHolder.get(convertView,R.id.person_order_item_status);
		View baoziLine = ViewHolder.get(convertView, R.id.baozi_line);

		TextView baoziOrderItemName = ViewHolder.get(convertView,R.id.baozi_order_item_name);
		TextView baoziOrderItemFacevalue = ViewHolder.get(convertView,R.id.baozi_order_item_facevalue);
		TextView baoziOrderItemPrice = ViewHolder.get(convertView,R.id.baozi_order_item_price);

		baoziOrderItemNo.setText(String.format(res.getString(R.string.order_list_order_number),orderEntity.getOrderNo()));

		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		//支付中，待支付，已完成，已关闭，已退款
		if (OrderPageEntity.OrderStatus.PAYING == orderEntity.getStatus()) {
			baoziOrderItemStatus.setText(res.getString(R.string.person_order_paying));
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.FINISH == orderEntity.getStatus()) {
			baoziOrderItemStatus.setText(res.getString(R.string.person_order_pay_success));
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.CLOSED == orderEntity.getStatus()) {
			baoziOrderItemStatus.setText(res.getString(R.string.person_order_closed));
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			baoziOrderItemStatus.setText(res.getString(R.string.person_order_wait));
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else {
			baoziOrderItemStatus.setText(res.getString(R.string.person_order_refund));
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		}
		baoziLine.setVisibility(View.GONE);
		baoziOrderItemName.setText(orderEntity.getName());
		baoziOrderItemFacevalue.setText(String.format(res.getString(R.string.person_order_detail_buy_num), String.valueOf(orderEntity.getBuyNum())));
		baoziOrderItemPrice.setText(String.format(res.getString(R.string.person_order_detail_buy_amount),
				NumberFormatUtil.formatMoney(Double.parseDouble(orderEntity.getCardOrderValue()))));

		if (OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else {
			baoziOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		}
	}

	private void setElectronData(View convertView) {
		TextView electronOrderItemNo = ViewHolder.get(convertView,R.id.person_order_item_no);
		TextView electronOrderItemStatus = ViewHolder.get(convertView,R.id.person_order_item_status);
		TextView electronOrderItemName = ViewHolder.get(convertView,R.id.electron_order_item_name);
		TextView electronOrderItemFacevalue = ViewHolder.get(convertView,R.id.electron_order_item_facevalue);
		TextView electronOrderItemPrice = ViewHolder.get(convertView,R.id.electron_order_item_price);
		TextView electronOrderItemPay = ViewHolder.get(convertView,R.id.electron_order_item_pay);
		View electronLine = ViewHolder.get(convertView, R.id.electron_line);

		CardView electronImgBg = ViewHolder.get(convertView,R.id.electron_img_bg);
		ImageView electronCardImgCover = ViewHolder.get(convertView,R.id.electron_card_img_cover);
		TextView electronFaceValue = ViewHolder.get(convertView,R.id.electron_face_value);

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//低于5.0的SDK版本，CardView会自己设置圆角padding，所以给高度加个值
			ViewGroup.LayoutParams layoutParams = electronImgBg.getLayoutParams();
			layoutParams.height = CommonTools.dp2px(getContext(), 70);
			electronImgBg.setLayoutParams(layoutParams);
		}

		if (!StringUtil.isEmpty(orderEntity.getBgcolor())) {
			electronImgBg.setCardBackgroundColor(Color.parseColor(orderEntity.getBgcolor()));
		} else {
			electronImgBg.setCardBackgroundColor(res.getColor(R.color.wjika_client_card_blue));
		}
		electronLine.setVisibility(View.GONE);
		String url = orderEntity.getCover();
		if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(electronCardImgCover,url);
		}
		electronFaceValue.getPaint().setFakeBoldText(true);
		electronFaceValue.setText(res.getString(R.string.money, NumberFormatUtil.formatBun(orderEntity.getFacevalue())));

		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		//支付中，待支付，已完成，已关闭，已退款
		if (OrderPageEntity.OrderStatus.PAYING == orderEntity.getStatus()) {
			electronOrderItemStatus.setText(res.getString(R.string.person_order_paying));
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else if (OrderPageEntity.OrderStatus.FINISH == orderEntity.getStatus()) {
			electronOrderItemStatus.setText(res.getString(R.string.person_order_pay_success));
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.CLOSED == orderEntity.getStatus()) {
			electronOrderItemStatus.setText(res.getString(R.string.person_order_closed));
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			electronOrderItemStatus.setText(res.getString(R.string.person_order_wait));
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else {
			electronOrderItemStatus.setText(res.getString(R.string.person_order_refund));
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		}

		electronOrderItemNo.setText(String.format(res.getString(R.string.order_list_order_number),orderEntity.getOrderNo()));
		electronOrderItemName.setText(orderEntity.getName());
		electronOrderItemFacevalue.setText(String.format(res.getString(R.string.person_order_detail_buy_num), String.valueOf(orderEntity.getBuyNum())));
		if ("2".equals(orderEntity.getPayWay())) {
			electronOrderItemPrice.setText(String.format(res.getString(R.string.person_baozi_num),NumberFormatUtil.formatBun(Double.parseDouble(orderEntity.getOrderValue()))));
			electronOrderItemPrice.setTextColor(res.getColor(R.color.person_main_baozi_num));
		} else {
			electronOrderItemPrice.setText(String.format(res.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatBun(orderEntity.getOrderValue())));//总价
			electronOrderItemPrice.setTextColor(res.getColor(R.color.card_store_price));
		}

		//getIfBuy 字段为1 ，有库存。为0，没有。
		if (OrderPageEntity.OrderStatus.FINISH == orderEntity.getStatus() && orderEntity.getIfBuy() == 1){
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
			electronOrderItemPay.setVisibility(View.GONE);
			electronOrderItemPay.setTag(orderEntity);
			electronOrderItemPay.setOnClickListener(onClickListener);
		} else if(OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
			electronOrderItemPay.setVisibility(View.GONE);
		} else {
			electronOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
			electronOrderItemPay.setVisibility(View.GONE);
		}
	}

	private void setCardData(View convertView) {
		CardView imgIconBG = ViewHolder.get(convertView, R.id.card_img_bg);
		ImageView imgCardLogo = ViewHolder.get(convertView, R.id.card_img_cover);
		TextView cardTxtName = ViewHolder.get(convertView, R.id.card_txt_name);
		TextView cardTxtStoreName = ViewHolder.get(convertView, R.id.card_txt_store_name);
		View cardLine = ViewHolder.get(convertView, R.id.card_line);

		TextView personOrderItemStatus = ViewHolder.get(convertView,R.id.person_order_item_status);
		TextView personOrderItemNo = ViewHolder.get(convertView,R.id.person_order_item_no);
		TextView personOrderItemName = ViewHolder.get(convertView,R.id.person_order_item_name);
		TextView personOrderItemFacevalue = ViewHolder.get(convertView,R.id.person_order_item_facevalue);
		TextView personOrderItemPrice = ViewHolder.get(convertView,R.id.person_order_item_price);

		//卡颜色(1,红色 2 黄色，3 蓝色 4 绿色)
		if(String.valueOf(1).equals(orderEntity.getcType())){
			imgIconBG.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_red));
		}else if(String.valueOf(2).equals(orderEntity.getcType())){
			imgIconBG.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_yellow));
		}else if(String.valueOf(3).equals(orderEntity.getcType())){
			imgIconBG.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_blue));
		}else if(String.valueOf(4).equals(orderEntity.getcType())){
			imgIconBG.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_green));
		}else {
			imgIconBG.setCardBackgroundColor(getContext().getResources().getColor(R.color.wjika_client_card_blue));
		}

		String url = orderEntity.getCover();
		if (!TextUtils.isEmpty(url) && !url.contains("?")) {
			ImageUtils.setSmallImg(imgCardLogo,url);
		}
		cardLine.setVisibility(View.GONE);
		cardTxtName.setText(orderEntity.getName());
		cardTxtStoreName.setText(res.getString(R.string.buy_card_face_value,NumberFormatUtil.formatBun(orderEntity.getFacevalue())));

		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		//支付中，待支付，已完成，已关闭，已退款
		if (OrderPageEntity.OrderStatus.PAYING == orderEntity.getStatus()) {
			personOrderItemStatus.setText(res.getString(R.string.person_order_paying));
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.FINISH == orderEntity.getStatus()) {
			personOrderItemStatus.setText(res.getString(R.string.person_order_pay_success));
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.CLOSED == orderEntity.getStatus()) {
			personOrderItemStatus.setText(res.getString(R.string.person_order_closed));
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		} else if (OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			personOrderItemStatus.setText(res.getString(R.string.person_order_wait));
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else {
			personOrderItemStatus.setText(res.getString(R.string.person_order_refund));
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		}

		personOrderItemName.setText(orderEntity.getName());
		personOrderItemNo.setText(String.format(res.getString(R.string.person_order_detail_no), orderEntity.getOrderNo()));
		personOrderItemFacevalue.setText(String.format(res.getString(R.string.person_order_detail_buy_num), String.valueOf(orderEntity.getBuyNum())));
		personOrderItemPrice.setText(String.format(res.getString(R.string.person_order_detail_buy_amount),
				NumberFormatUtil.formatMoney(Double.parseDouble(orderEntity.getOrderValue()))));
		if (OrderPageEntity.OrderStatus.UNPAY == orderEntity.getStatus()){
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_title_bg));
		} else {
			personOrderItemStatus.setTextColor(res.getColor(R.color.wjika_client_gray));
		}
	}
}
