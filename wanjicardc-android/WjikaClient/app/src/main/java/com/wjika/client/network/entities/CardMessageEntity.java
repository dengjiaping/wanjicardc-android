package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Leo_Zhang on 2016/7/7.
 * 卡消息
 */
public class CardMessageEntity {

	@SerializedName("activityId")
	private String activityId;
	@SerializedName("ad")
	private String ad;
	@SerializedName("cardColor")
	private int cardColor;
	@SerializedName("cardId")
	private String cardId;
	@SerializedName("cardLogo")
	private String cardLogo;
	@SerializedName("cardName")
	private String cardName;
	@SerializedName("currentFlag")
	private String currentFlag;
	@SerializedName("faceValue")
	private String faceValue;
	@SerializedName("introduce")
	private String introduce;

	@SerializedName("isLimitForSale")//1：有活动，有返回。0：没有活动，有返回，是""。
	private String isLimitForSale;
	@SerializedName("merchantName")
	private String merchantName;
	@SerializedName("price")//活动价格。
	private String price;
	@SerializedName("privilege")
	private ArrayList<PrivilegeEntity> privilegeEntityList;
	@SerializedName("privilegeNum")
	private int privilegeNum;
	@SerializedName("saleValue")
	private String saleValue;
	@SerializedName("totalSale")
	private String totalSale;
	@SerializedName("useExplain")
	private String useExplain;

	public String getIsLimitForSale() {
		return isLimitForSale;
	}

	public void setIsLimitForSale(String isLimitForSale) {
		this.isLimitForSale = isLimitForSale;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getAd() {
		return ad;
	}

	public void setAd(String ad) {
		this.ad = ad;
	}

	public CardEntity.CardBGType getCardColor() {
		switch (cardColor) {
			case 1:
				return CardEntity.CardBGType.RED;
			case 2:
				return CardEntity.CardBGType.ORANGE;
			case 3:
				return CardEntity.CardBGType.BLUE;
			case 4:
				return CardEntity.CardBGType.GREEN;
			default:
				return CardEntity.CardBGType.BLUE;
		}
	}

	public void setCardColor(int cardColor) {
		this.cardColor = cardColor;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardLogo() {
		return cardLogo;
	}

	public void setCardLogo(String cardLogo) {
		this.cardLogo = cardLogo;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCurrentFlag() {
		return currentFlag;
	}

	public void setCurrentFlag(String currentFlag) {
		this.currentFlag = currentFlag;
	}

	public String getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(String faceValue) {
		this.faceValue = faceValue;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public ArrayList<PrivilegeEntity> getPrivilegeEntityList() {
		return privilegeEntityList;
	}

	public void setPrivilegeEntityList(ArrayList<PrivilegeEntity> privilegeEntityList) {
		this.privilegeEntityList = privilegeEntityList;
	}

	public int getPrivilegeNum() {
		return privilegeNum;
	}

	public void setPrivilegeNum(int privilegeNum) {
		this.privilegeNum = privilegeNum;
	}

	public String getSaleValue() {
		return saleValue;
	}

	public void setSaleValue(String saleValue) {
		this.saleValue = saleValue;
	}

	public String getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(String totalSale) {
		this.totalSale = totalSale;
	}

	public String getUseExplain() {
		return useExplain;
	}

	public void setUseExplain(String useExplain) {
		this.useExplain = useExplain;
	}
}
