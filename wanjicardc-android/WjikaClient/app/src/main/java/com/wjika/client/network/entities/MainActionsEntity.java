package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZHXIA on 2016/6/21
 * <p/>
 * 首页活动
 */
public class MainActionsEntity {

	@SerializedName("activityName")
	private String activityName;
	@SerializedName("imagePosition")
	private String activityImagePosition;
	@SerializedName("img")
	private String activityImg;
	@SerializedName("url")
	private String mainActionContent;
	@SerializedName("type")
	private String mainActionType;
	@SerializedName("merchantName")
	private String mainActionMerchantName;
	@SerializedName("merchantAccountId")
	private String actionMerchantAccountId;
	@SerializedName("brandName")
	private String mainActionBrandName;
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

	public String getMainActionBrandName() {
		return mainActionBrandName;
	}

	public void setMainActionBrandName(String mainActionBrandName) {
		this.mainActionBrandName = mainActionBrandName;
	}

	public String getActionMerchantAccountId() {
		return actionMerchantAccountId;
	}

	public void setActionMerchantAccountId(String actionMerchantAccountId) {
		this.actionMerchantAccountId = actionMerchantAccountId;
	}

	public String getMainActionMerchantName() {
		return mainActionMerchantName;
	}

	public void setMainActionMerchantName(String mainActionMerchantName) {
		this.mainActionMerchantName = mainActionMerchantName;
	}

	public String getMainActionType() {
		return mainActionType;
	}

	public void setMainActionType(String mainActionType) {
		this.mainActionType = mainActionType;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityImagePosition() {
		return activityImagePosition;
	}

	public void setActivityImagePosition(String activityImagePosition) {
		this.activityImagePosition = activityImagePosition;
	}

	public String getActivityImg() {
		return activityImg;
	}

	public void setActivityImg(String activityImg) {
		this.activityImg = activityImg;
	}

	public String getMainActionContent() {
		return mainActionContent;
	}

	public void setMainActionContent(String mainActionContent) {
		this.mainActionContent = mainActionContent;
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
