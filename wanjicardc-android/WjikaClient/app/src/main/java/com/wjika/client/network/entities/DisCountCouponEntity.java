package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jacktian on 16/3/18.
 * 可用优惠券
 */
public class DisCountCouponEntity extends Entity {

	public static final String ACTIVITY_PAUSE = "50008056";//活动暂停，显示为活动结束
	public static final String ACTIVITY_FINISH = "50008057";//活动结束
	public static final String ACTIVITY_NO_QUALIFICATION = "50008058";//不满足条件
	public static final String ACTIVITY_NO_INVENTORY = "50008059";//库存已售尽

	@SerializedName("discount")
	private double discount;//折扣
	@SerializedName("userCoupons")
	private List<CouponEntity> couponList;
	@SerializedName("ecoEnable")
	private int ecoEnable;//是否启用易联支付
	@SerializedName("canBuyNum")
	private int maxNum;//最大可购买数
	@SerializedName("code")
	private String activityCondition;//购买条件

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public List<CouponEntity> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<CouponEntity> couponList) {
		couponList = couponList;
	}

	public int getEcoEnable() {
		return ecoEnable;
	}

	public void setEcoEnable(int ecoEnable) {
		this.ecoEnable = ecoEnable;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public String getActivityCondition() {
		return activityCondition;
	}

	public void setActivityCondition(String activityCondition) {
		this.activityCondition = activityCondition;
	}

	public boolean isActivityCanBuy() {
		return !(ACTIVITY_PAUSE.equals(activityCondition) || ACTIVITY_FINISH.equals(activityCondition) || ACTIVITY_NO_QUALIFICATION.equals(activityCondition) || ACTIVITY_NO_INVENTORY.equals(activityCondition));
	}
}
