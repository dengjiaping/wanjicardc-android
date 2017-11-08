package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bob on 2016/7/11.
 * 充值时确认订单接口实体
 */
public class ChargeEntity extends Entity {

	@SerializedName("coupon")
	private List<CouponEntity> coupon;
	@SerializedName("cards")
	private List<ChargeCardEntity> cards;
	@SerializedName("discount")
	private double discount;
	@SerializedName("ecoEnable")
	private int ecoEnable;//是否启用易联支付

	public List<CouponEntity> getCoupon() {
		return coupon;
	}

	public void setCoupon(List<CouponEntity> coupon) {
		this.coupon = coupon;
	}

	public List<ChargeCardEntity> getCards() {
		return cards;
	}

	public void setCards(List<ChargeCardEntity> cards) {
		this.cards = cards;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getEcoEnable() {
		return ecoEnable;
	}

	public void setEcoEnable(int ecoEnable) {
		this.ecoEnable = ecoEnable;
	}
}
