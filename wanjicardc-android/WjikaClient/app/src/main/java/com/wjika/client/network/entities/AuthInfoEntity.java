package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/6/29.
 * 认证信息
 */
public class AuthInfoEntity extends Entity {

	@SerializedName("custName")
	private String custName;
	@SerializedName("idCode")
	private String idCode;

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

}
