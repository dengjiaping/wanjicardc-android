package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/11/23.
 */

public class DjBillEntity {
	@SerializedName("orderNo")
	private String orderNum;
	@SerializedName("status")
	private String orderState;
	@SerializedName("accountMoney")
	private String orderMoney;
	@SerializedName("channelName")
	private String orderChannel;
	@SerializedName("charge")
	private String orderFee;
	@SerializedName("crtTime")
	private String orderTime;

	public DjBillEntity(String orderNum, String orderState, String orderMoney, String orderChannel, String orderFee, String orderTime, String detailTime) {
		this.orderNum = orderNum;
		this.orderState = orderState;
		this.orderMoney = orderMoney;
		this.orderChannel = orderChannel;
		this.orderFee = orderFee;
		this.orderTime = orderTime;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(String orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getOrderChannel() {
		return orderChannel;
	}

	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}

	public String getOrderFee() {
		return orderFee;
	}

	public void setOrderFee(String orderFee) {
		this.orderFee = orderFee;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

}
