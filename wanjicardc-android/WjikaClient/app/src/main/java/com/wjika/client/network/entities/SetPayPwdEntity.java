package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/7/6.
 * 设置支付密码返回数据实体
 */
public class SetPayPwdEntity extends Entity {

	@SerializedName("token")
	private String token;
	@SerializedName("salt")
	private String salt;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
}
