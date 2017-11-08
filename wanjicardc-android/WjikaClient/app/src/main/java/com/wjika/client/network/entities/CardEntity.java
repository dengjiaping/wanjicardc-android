package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/8/30.
 * 卡列表item
 */
public class CardEntity extends Entity {

	@SerializedName("cardId")
	private String id;
	@SerializedName("cardName")
	private String name;
	@SerializedName("cardLogo")
	private String imgPath;
	@SerializedName("cardColor")
	private int imgType;
	@SerializedName("balance")
	private String balance;
	@SerializedName("merchantId")
	private String merId;

	private String faceValue;
	private String salePrice;
	private int saledNum;
	private int status;
	private String storeName;
	private boolean isChose;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(String faceValue) {
		this.faceValue = faceValue;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getSaledNum() {
		return saledNum;
	}

	public void setSaledNum(int saledNum) {
		this.saledNum = saledNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isChose() {
		return isChose;
	}

	public void setIsChose(boolean isChose) {
		this.isChose = isChose;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public CardBGType getImgType() {
		//卡背景状态： 1 红色，2黄色，3蓝色 4绿色
		switch (imgType) {
			case 3:
				return CardEntity.CardBGType.BLUE;
			case 1:
				return CardEntity.CardBGType.RED;
			case 2:
				return CardEntity.CardBGType.ORANGE;
			case 4:
				return CardEntity.CardBGType.GREEN;
			default:
				return CardEntity.CardBGType.BLUE;
		}
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public enum CardBGType {
		BLUE,
		RED,
		ORANGE,
		GREEN,
	}
}
