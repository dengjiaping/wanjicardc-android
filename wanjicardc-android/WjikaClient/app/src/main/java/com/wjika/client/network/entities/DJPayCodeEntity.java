package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2016/11/30.
 */

public class DJPayCodeEntity {

	@SerializedName("code_img_url")
	private String qrImg;
	@SerializedName("payRemender")
	private String desc;
	@SerializedName("total_fee")
	private String amount;
	@SerializedName("warmPrompt")
	private String hint;

	public String getQrImg() {
		return qrImg;
	}

	public void setQrImg(String qrImg) {
		this.qrImg = qrImg;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
}
