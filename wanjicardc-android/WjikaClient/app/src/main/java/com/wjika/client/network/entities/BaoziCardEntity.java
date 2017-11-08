package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/8/30.
 * 包子
 */
public class BaoziCardEntity extends Entity {

	@SerializedName("id")
	private String id;
	@SerializedName("rechargeAmount")
	private String rechargeAmount;
	@SerializedName("describe")
	private String describe;
	@SerializedName("baoziAmount")
	private int baoziAmount;
	@SerializedName("newdescribe")
	private String newUserGift;
	@SerializedName("successdescribe")
	private String hintInfo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getBaoziAmount() {
		return baoziAmount;
	}

	public void setBaoziAmount(int baoziAmount) {
		this.baoziAmount = baoziAmount;
	}

	public String getNewUserGift() {
		return newUserGift;
	}

	public void setNewUserGift(String newUserGift) {
		this.newUserGift = newUserGift;
	}

	public String getHintInfo() {
		return hintInfo;
	}

	public void setHintInfo(String hintInfo) {
		this.hintInfo = hintInfo;
	}
}
