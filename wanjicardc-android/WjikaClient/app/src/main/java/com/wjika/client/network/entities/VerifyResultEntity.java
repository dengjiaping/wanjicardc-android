package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_Zhichao on 2015/12/24 11:35.
 * 手机号支付密码校验结果
 */
public class VerifyResultEntity extends Entity {

	private boolean result;
	@SerializedName("appealStatus")
	private int appealStatus;
	@SerializedName("token")
	private String token;
	@SerializedName("lockTimes")
	private int lockTimes;
	@SerializedName("certificate")
	private String certificate;//token  sha1之后的值

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getAppealStatus() {
		return appealStatus;
	}

	public void setAppealStatus(int appealStatus) {
		this.appealStatus = appealStatus;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getLockTimes() {
		return lockTimes;
	}

	public void setLockTimes(int lockTimes) {
		this.lockTimes = lockTimes;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
}
