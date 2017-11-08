package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jacktian on 15/9/8.
 * 商品
 */
public class ProductEntity implements Serializable {

	@SerializedName("cardId")
	private String id;
	@SerializedName("cardName")
	private String name;
	@SerializedName("faceValue")
	private String facevalue;
	@SerializedName("saleValue")
	private String saleprice;
	@SerializedName("cardLogo")
	private String imgPath;
	@SerializedName("totalSale")
	private int SaledNum;
	@SerializedName("Status")
	private int Status;
	@SerializedName("cardColor")
	private int imgType;
	@SerializedName("merchantName")
	private String storeName;
	@SerializedName("price")
	private String price;//活动价
	@SerializedName("isLimitForSale")
	private boolean isLimitForSale;//限购
	@SerializedName("salePercent")
	private String salePercent;//已售占比

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public boolean isLimitForSale() {
		return isLimitForSale;
	}

	public void setLimitForSale(boolean limitForSale) {
		isLimitForSale = limitForSale;
	}

	public String getSalePercent() {
		return salePercent;
	}

	public void setSalePercent(String salePercent) {
		this.salePercent = salePercent;
	}

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

	public String getFacevalue() {
		return facevalue;
	}

	public void setFacevalue(String facevalue) {
		this.facevalue = facevalue;
	}

	public String getSaleprice() {
		return saleprice;
	}

	public void setSaleprice(String saleprice) {
		this.saleprice = saleprice;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getSaledNum() {
		return SaledNum;
	}

	public void setSaledNum(int saledNum) {
		SaledNum = saledNum;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public CardEntity.CardBGType getImgType() {
		//卡背景状态： 1 红色，2黄色，3蓝色 4绿色
		switch (imgType) {
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

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}
