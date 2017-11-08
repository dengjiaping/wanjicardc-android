package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/7/6.
 * 验证支付密码返回数据实体
 */
public class PayVerifyPwdEntity extends Entity {

	@SerializedName("lockedStatus")
	private boolean lockedStatus;
	@SerializedName("token")
	private String token;
	@SerializedName("lockTimes")
	private int lockTimes;

	public boolean isLockedStatus() {
		return lockedStatus;
	}

	public void setLockedStatus(boolean lockedStatus) {
		this.lockedStatus = lockedStatus;
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
}
