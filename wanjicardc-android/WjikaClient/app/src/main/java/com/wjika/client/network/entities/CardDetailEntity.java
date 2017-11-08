package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Leo_Zhang on 2016/7/7.
 * 商家卡详情
 */
public class CardDetailEntity extends Entity {

	@SerializedName("ShareInfo")
	private Share shareInfos;
	@SerializedName("branch")
	private List<StoreEntity> mSupportStoreList;
	@SerializedName("branchNum")
	private int branchNum;
	@SerializedName("cardList")
	private List<CardMessageEntity> cardMessageEntityList;
	@SerializedName("mainMerchantId")
	private String mainMerchantId;
	@SerializedName("merchantId")
	private String merchantId;
	@SerializedName("merchantName")
	private String merchantName;
	@SerializedName("isMyCard")
	private String isMyCard;

	public String getIsMyCard() {
		return isMyCard;
	}

	public void setIsMyCard(String isMyCard) {
		this.isMyCard = isMyCard;
	}

	public Share getShareInfos() {
		return shareInfos;
	}

	public void setShareInfos(Share shareInfos) {
		this.shareInfos = shareInfos;
	}

	public List<StoreEntity> getmSupportStoreList() {
		return mSupportStoreList;
	}

	public void setmSupportStoreList(List<StoreEntity> mSupportStoreList) {
		this.mSupportStoreList = mSupportStoreList;
	}

	public int getBranchNum() {
		return branchNum;
	}

	public void setBranchNum(int branchNum) {
		this.branchNum = branchNum;
	}

	public List<CardMessageEntity> getCardMessageEntityList() {
		return cardMessageEntityList;
	}

	public void setCardMessageEntityList(List<CardMessageEntity> cardMessageEntityList) {
		this.cardMessageEntityList = cardMessageEntityList;
	}

	public String getMainMerchantId() {
		return mainMerchantId;
	}

	public void setMainMerchantId(String mainMerchantId) {
		this.mainMerchantId = mainMerchantId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public static class Share{

		@SerializedName("Title")
		private String title;
		@SerializedName("Desc")
		private String desc;
		@SerializedName("Url")
		private String url;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
