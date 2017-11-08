package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 2015/9/12 17:28.
 * 包子订单支付
 */
public class BaoziPayEntity extends Entity {

	@SerializedName("charge")
	private String charge;
	@SerializedName("rechargeAmount")
	private String rechargeAmount;

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
}
