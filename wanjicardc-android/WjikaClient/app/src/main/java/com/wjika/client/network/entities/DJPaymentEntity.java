package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2016/11/25.
 */

public class DJPaymentEntity {

	@SerializedName("minLimits")
	private double minQuota;
	@SerializedName("maxLimits")
	private double maxQuota;
	@SerializedName("gatheringRemender")
	private String hint;
	@SerializedName("businessRemender")
	private String declaration;

	public double getMinQuota() {
		return minQuota;
	}

	public void setMinQuota(double minQuota) {
		this.minQuota = minQuota;
	}

	public double getMaxQuota() {
		return maxQuota;
	}

	public void setMaxQuota(double maxQuota) {
		this.maxQuota = maxQuota;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}
}
