package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_ZhiChao on 2015/9/12 17:28.
 * 订单支付
 */
public class OrderPayEntity extends Entity {

	@SerializedName("cardOrderNo")
	private String orderNo;
	@SerializedName("cardOrderStatus")
	private String orderState;
	@SerializedName("charge")
	private String charge;
	@SerializedName("cardOrderAmount")
	private String orderAmount;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
}
