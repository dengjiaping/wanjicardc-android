package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_ZhiChao on 2015/9/7 18:45.
 * 优惠券
 */
public class CouponEntity implements Parcelable {

	@SerializedName("userCouponId")
	private String id;
	@SerializedName("couponName")
	private String name;//优惠券名字
	@SerializedName("couponValue")
	private String amount;//优惠券金额
	@SerializedName("couponIntroduce")
	private String desc;//说明
	@SerializedName("couponEndDate")
	private String validTime;//有效期
	@SerializedName("couponCode")
	private String couponCode;//优惠券code用于支付订单使用
	@SerializedName("currentStatus")
	private String currentStatus;//1 未使用 2 已使用 3 已占用（待支付订单）
	@SerializedName("expiredType")
	private String expiredType;//0 未过期 1 将要过期（3天） 2 已过期

	private boolean isChecked;//支付时判断是否选中使用

	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}


	private CouponEntity(Parcel in) {
		id = in.readString();
		name = in.readString();
		amount = in.readString();
		desc = in.readString();
		validTime = in.readString();
		couponCode = in.readString();
		expiredType = in.readString();
		isChecked = (in.readByte() == 1);
	}

	public static final Creator<CouponEntity> CREATOR = new Creator<CouponEntity>() {
		@Override
		public CouponEntity createFromParcel(Parcel in) {
			return new CouponEntity(in);
		}

		@Override
		public CouponEntity[] newArray(int size) {
			return new CouponEntity[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	public String isCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String isExpiredType() {
		return expiredType;
	}

	public void setExpiredType(String expiredType) {
		this.expiredType = expiredType;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(amount);
		dest.writeString(desc);
		dest.writeString(validTime);
		dest.writeString(couponCode);
		dest.writeString(expiredType);
		dest.writeByte(isChecked ? (byte)1 : 0);
	}
}
