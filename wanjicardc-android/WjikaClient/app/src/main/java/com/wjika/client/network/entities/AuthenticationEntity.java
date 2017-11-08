package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/6/29.
 * 实名认证获取验证码返回结果
 */
public class AuthenticationEntity extends Entity {

	@SerializedName("certificate")
	private String certificate;
	@SerializedName("status")
	private boolean status;
	@SerializedName("isAuthentication")
	private boolean isAuthentication;
	@SerializedName("isUnionVerify")
	private boolean isUnionVerify;//判断是否支持短信验证码验证

	public boolean isAuthentication() {
		return isAuthentication;
	}

	public void setAuthentication(boolean authentication) {
		isAuthentication = authentication;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isUnionVerify() {
		return isUnionVerify;
	}

	public void setUnionVerify(boolean unionVerify) {
		isUnionVerify = unionVerify;
	}
}
