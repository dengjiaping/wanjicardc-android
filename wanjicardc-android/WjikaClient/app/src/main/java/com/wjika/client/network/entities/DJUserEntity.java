package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2016/11/25.
 */

public class DJUserEntity {

	@SerializedName("token")
	private String token;
	@SerializedName("userPhone")
	private String phone;
	@SerializedName("bankAmount")
	private int cardNum;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getCardNum() {
		return cardNum;
	}

	public void setCardNum(int cardNum) {
		this.cardNum = cardNum;
	}
}
