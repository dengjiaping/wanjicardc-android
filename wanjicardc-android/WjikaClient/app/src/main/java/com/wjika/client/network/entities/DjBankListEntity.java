package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/11/23.
 */

public class DjBankListEntity {

	@SerializedName("id")
	private String id;
	@SerializedName("bankName")
	private String bankName;
	@SerializedName("cardNo")
	private String bankNum;
	@SerializedName("cardType")
	private String bankType;
	private String bankState;
	@SerializedName("bankLogo")
	private String bankImg;
	@SerializedName("default")
	private boolean isChecked;

	public DjBankListEntity(String bankName, String bankNum, String bankType, String bankState, String bankImg, boolean isChecked) {
		this.bankName = bankName;
		this.bankNum = bankNum;
		this.bankType = bankType;
		this.bankState = bankState;
		this.bankImg = bankImg;
		this.isChecked = isChecked;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankNum() {
		return bankNum;
	}

	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getBankState() {
		return bankState;
	}

	public void setBankState(String bankState) {
		this.bankState = bankState;
	}

	public String getBankImg() {
		return bankImg;
	}

	public void setBankImg(String bankImg) {
		this.bankImg = bankImg;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}
}
