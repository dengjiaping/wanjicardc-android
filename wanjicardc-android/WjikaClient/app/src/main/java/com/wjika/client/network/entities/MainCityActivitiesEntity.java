package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZHXIA on 2016/6/30
 * <p/>
 * 首页城市活动
 */

public class MainCityActivitiesEntity {

	@SerializedName("category")
	private String cityActivitiesCategory;
	@SerializedName("id")
	private String cityActivitiesId;
	//图片的url
	@SerializedName("img")
	private String cityActivitiesImg;
	//图片对应的链接url
	@SerializedName("url")
	private String cityActivitiesContent;
	@SerializedName("type")
	private String cityActivitType;
	@SerializedName("merchantName")
	private String mainCityActiveMerchantName;
	@SerializedName("merchantAccountId")
	private String cityActionMerchantAccountId;
	@SerializedName("brandName")
	private String cityActionbrandName;
	@SerializedName("merchantId")
	private String cardMerchantId;
	@SerializedName("isLogin")
	private boolean isNeedLogin;
	@SerializedName("merchantBranchId")
	private String brandId;

	public String getCardMerchantId() {
		return cardMerchantId;
	}

	public void setCardMerchantId(String cardMerchantId) {
		this.cardMerchantId = cardMerchantId;
	}

	public String getCityActionbrandName() {
		return cityActionbrandName;
	}

	public void setCityActionbrandName(String cityActionbrandName) {
		this.cityActionbrandName = cityActionbrandName;
	}

	public String getCityActionMerchantAccountId() {
		return cityActionMerchantAccountId;
	}

	public void setCityActionMerchantAccountId(String cityActionMerchantAccountId) {
		this.cityActionMerchantAccountId = cityActionMerchantAccountId;
	}

	public String getMainCityActiveMerchantName() {
		return mainCityActiveMerchantName;
	}

	public void setMainCityActiveMerchantName(String mainCityActiveMerchantName) {
		this.mainCityActiveMerchantName = mainCityActiveMerchantName;
	}

	public String getCityActivitType() {
		return cityActivitType;
	}

	public void setCityActivitType(String cityActivitType) {
		this.cityActivitType = cityActivitType;
	}

	public String getCityActivitiesCategory() {
		return cityActivitiesCategory;
	}

	public void setCityActivitiesCategory(String cityActivitiesCategory) {
		this.cityActivitiesCategory = cityActivitiesCategory;
	}

	public String getCityActivitiesId() {
		return cityActivitiesId;
	}

	public void setCityActivitiesId(String cityActivitiesId) {
		this.cityActivitiesId = cityActivitiesId;
	}

	public String getCityActivitiesImg() {
		return cityActivitiesImg;
	}

	public void setCityActivitiesImg(String cityActivitiesImg) {
		this.cityActivitiesImg = cityActivitiesImg;
	}

	public String getCityActivitiesContent() {
		return cityActivitiesContent;
	}

	public void setCityActivitiesContent(String cityActivitiesContent) {
		this.cityActivitiesContent = cityActivitiesContent;
	}

	public boolean isNeedLogin() {
		return isNeedLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		isNeedLogin = needLogin;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
}
