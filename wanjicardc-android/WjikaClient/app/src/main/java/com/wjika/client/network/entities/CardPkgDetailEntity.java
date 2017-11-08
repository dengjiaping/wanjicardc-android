package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/9/10.
 * 卡包卡详情
 */
public class CardPkgDetailEntity extends Entity {

	@SerializedName("cardName")
	private String name;
	@SerializedName("cardColor")
	private int imgType;
	@SerializedName("cardLogo")
	private String imgPath;
	@SerializedName("balance")
	private String amount;
	@SerializedName("merchantId")
	private String merId;
	@SerializedName("ad")
	private String adDesc;
	@SerializedName("privilege")
	private ArrayList<PrivilegeEntity> privilegeEntityList;

	private String cardId;
	private String mainMerId;
	private String faceValue;
	private String introduce;
	private String promotion;
	private int SaleNum;
	private String salePrice;
	private String useExplain;
	private int supportStoreNum;
	private List<StoreEntity> mSupportStoreList;
	private int privilegeNum;
	private Share shareInfos;
	private String storeName;

	public String getId() {
		return cardId;
	}

	public void setId(String id) {
		this.cardId = id;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getMainMerId() {
		return mainMerId;
	}

	public void setMainMerId(String mainMerId) {
		this.mainMerId = mainMerId;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUseExplain() {
		return useExplain;
	}

	public void setUseExplain(String useExplain) {
		this.useExplain = useExplain;
	}

	public int getSupportStoreNum() {
		return supportStoreNum;
	}

	public void setSupportStoreNum(int supportStoreNum) {
		this.supportStoreNum = supportStoreNum;
	}

	public List<StoreEntity> getmSupportStoreList() {
		return mSupportStoreList;
	}

	public void setmSupportStoreList(List<StoreEntity> mSupportStoreList) {
		this.mSupportStoreList = mSupportStoreList;
	}

	public ArrayList<PrivilegeEntity> getPrivilegeEntityList() {
		return privilegeEntityList;
	}

	public void setPrivilegeEntityList(ArrayList<PrivilegeEntity> privilegeEntityList) {
		this.privilegeEntityList = privilegeEntityList;
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

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public int getSaleNum() {
		return SaleNum;
	}

	public void setSaleNum(int saleNum) {
		SaleNum = saleNum;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public int getPrivilegeNum() {
		return privilegeNum;
	}

	public void setPrivilegeNum(int privilegeNum) {
		this.privilegeNum = privilegeNum;
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

	public String getAdDesc() {
		return adDesc;
	}

	public void setAdDesc(String adDesc) {
		this.adDesc = adDesc;
	}

	public Share getShareInfos() {
		return shareInfos;
	}

	public void setShareInfos(Share shareInfos) {
		this.shareInfos = shareInfos;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	private static class Share {

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
