package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bob on 16/8/23.
 * 我的包子
 */
public class MyBaoziEntity extends Entity {

	@SerializedName("walletCount")
	private String walletCount;
	@SerializedName("availableWalletCount")
	private String availableWalletCount;
	@SerializedName("result")
	private List<BaoziTransRecordsEntity> transRecord;
	@SerializedName("totalPage")
	private int totalPage;
	@SerializedName("ifRecharge")
	private boolean ifRecharge;

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public String getWalletCount() {
		return walletCount;
	}

	public void setWalletCount(String walletCount) {
		this.walletCount = walletCount;
	}

	public String getAvailableWalletCount() {
		return availableWalletCount;
	}

	public void setAvailableWalletCount(String availableWalletCount) {
		this.availableWalletCount = availableWalletCount;
	}

	public List<BaoziTransRecordsEntity> getTransRecord() {
		return transRecord;
	}

	public void setTransRecord(List<BaoziTransRecordsEntity> transRecord) {
		this.transRecord = transRecord;
	}

	public boolean isIfRecharge() {
		return ifRecharge;
	}

	public void setIfRecharge(boolean ifRecharge) {
		this.ifRecharge = ifRecharge;
	}
}
