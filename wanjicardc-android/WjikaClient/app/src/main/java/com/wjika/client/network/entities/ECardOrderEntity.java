package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_Zhichao on 2016/8/31 11:24.
 * 电子卡生成订单后返回的数据
 */
public class ECardOrderEntity extends Entity {

	@SerializedName("charge")
	private String charge;
	@SerializedName("orderNo")
	private String orderNo;
	@SerializedName("totalBunNum")
	private double balance;

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
