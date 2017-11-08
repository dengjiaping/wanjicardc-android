package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jacktian on 15/8/30.
 * 包子充值
 */
public class BaoziChargeEntity extends Entity {

	@SerializedName("bunList")
	private List<BaoziCardEntity> baoziList;
	@SerializedName("ecoEnable")
	private int ecoEnable;//是否启用易联支付
	@SerializedName("rechargeAgreement")
	private String rechargeAgreement;

	public List<BaoziCardEntity> getBaoziList() {
		return baoziList;
	}

	public void setBaoziList(List<BaoziCardEntity> baoziList) {
		this.baoziList = baoziList;
	}

	public int getEcoEnable() {
		return ecoEnable;
	}

	public void setEcoEnable(int ecoEnable) {
		this.ecoEnable = ecoEnable;
	}

	public String getRechargeAgreement() {
		return rechargeAgreement;
	}

	public void setRechargeAgreement(String rechargeAgreement) {
		this.rechargeAgreement = rechargeAgreement;
	}
}
