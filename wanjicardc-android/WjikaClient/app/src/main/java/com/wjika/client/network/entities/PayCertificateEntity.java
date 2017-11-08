package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 张家洛 on 2016/8/29.
 * 支付凭证
 */
public class PayCertificateEntity extends Entity {

	@SerializedName("charge")
	private String charge;
	@SerializedName("payChannel")
	private String payChannel;

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
}
