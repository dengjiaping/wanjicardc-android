package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jacktian on 15/8/30.
 * 充值
 */
public class ChargeCardEntity extends Entity {

	@SerializedName("cardFacePrice")
	private String cardFacePrice;
	@SerializedName("merchantCardName")
	private String merchantCardName;
	@SerializedName("merchantCardPrivilege")
	private List<PrivilegeEntity> merchantCardPrivilege;
	@SerializedName("cardId")
	private String cardId;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardFacePrice() {
		return cardFacePrice;
	}

	public void setCardFacePrice(String cardFacePrice) {
		this.cardFacePrice = cardFacePrice;
	}

	public String getMerchantCardName() {
		return merchantCardName;
	}

	public void setMerchantCardName(String merchantCardName) {
		this.merchantCardName = merchantCardName;
	}

	public List<PrivilegeEntity> getMerchantCardPrivilege() {
		return merchantCardPrivilege;
	}

	public void setMerchantCardPrivilege(List<PrivilegeEntity> merchantCardPrivilege) {
		this.merchantCardPrivilege = merchantCardPrivilege;
	}
}
