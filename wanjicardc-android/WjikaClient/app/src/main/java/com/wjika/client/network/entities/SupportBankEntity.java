package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kkkkk on 2016/8/22.
 * 实名认证支持银行
 */
public class SupportBankEntity {

	@SerializedName("id")
	private int id;
	@SerializedName("name")
	private String name;
	@SerializedName("logoImg")
	private String logoImg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoImg() {
		return logoImg;
	}

	public void setLogoImg(String logoImg) {
		this.logoImg = logoImg;
	}
}
