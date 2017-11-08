package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/11/25.
 */

public class DjpayRateEntity {

	@SerializedName("charge")
	private String charge;//结算金额
	@SerializedName("id")
	private String id;//
	@SerializedName("limits")
	private String limits;//额度
	@SerializedName("payType")
	private String payType;//表示到账类型（T+0,T+1）
	@SerializedName("payWayId")
	private String payWayId;//支付方式（微信，支付宝）
	@SerializedName("rate")
	private String rate;//费率

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLimits() {
		return limits;
	}

	public void setLimits(String limits) {
		this.limits = limits;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayWayId() {
		return payWayId;
	}

	public void setPayWayId(String payWayId) {
		this.payWayId = payWayId;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
}
