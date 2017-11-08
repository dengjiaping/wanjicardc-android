package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/9/7.
 * 店铺详情
 */
public class StoreDetailsEntity extends Entity {

	@SerializedName("merchantId")
	private String id;
	@SerializedName("mainMerchantId")
	private String mainMerId;
	@SerializedName("merchantName")
	private String name;
	@SerializedName("merchantAddress")
	private String address;
	@SerializedName("totalSale")
	private String totalSale;
	@SerializedName("merchantLongitude")
	private double longitude;
	@SerializedName("merchantLatitude")
	private double latitude;
	@SerializedName("Cover")
	private String imgPath;
	@SerializedName("merchantBusinesshours")
	private String businessTime;
	@SerializedName("merchantPhone")
	private String phone;
	@SerializedName("ProductNum")
	private int productNum;
	@SerializedName("branchNum")
	private int branchNum;
	@SerializedName("photoes")
	private ArrayList<StoreImgEntity> storeImgEntities;
	@SerializedName("branch")
	private List<StoreEntity> branchs;
	@SerializedName("merchantCards")
	private List<ProductEntity> productEntityList;
	@SerializedName("Promotion")
	private String promotion;
	@SerializedName("privilege")
	private ArrayList<PrivilegeEntity> privilegeEntityList;
	@SerializedName("photoesNum")
	private int galleryNum;
	@SerializedName("activity")
	private List<String> activityEntities;
	@SerializedName("activityNum")
	private int activityNum;

	public List<String> getActivityEntities() {
		return activityEntities;
	}

	public void setActivityEntities(List<String> activityEntities) {
		this.activityEntities = activityEntities;
	}

	public int getActivityNum() {
		return activityNum;
	}

	public void setActivityNum(int activityNum) {
		this.activityNum = activityNum;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@SerializedName("privilegeNum")
	private int privilegesNum;
	@SerializedName("merchantIntroduce")
	private String introductionUrl;
	@SerializedName("Desc")
	private String desc;
	@SerializedName("Title")
	private String title;
	@SerializedName("Url")
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMainMerId() {
		return mainMerId;
	}

	public void setMainMerId(String mainMerId) {
		this.mainMerId = mainMerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(String totalSale) {
		this.totalSale = totalSale;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getImgPath() {
		return imgPath;
	}

	public ArrayList<StoreImgEntity> getStoreImgEntities() {
		return storeImgEntities;
	}

	public void setStoreImgEntities(ArrayList<StoreImgEntity> storeImgEntities) {
		this.storeImgEntities = storeImgEntities;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getBusinessTime() {
		return businessTime;
	}

	public void setBusinessTime(String businessTime) {
		this.businessTime = businessTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getProductNum() {
		return productNum;
	}

	public void setProductNum(int productNum) {
		this.productNum = productNum;
	}

	public int getBranchNum() {
		return branchNum;
	}

	public void setBranchNum(int branchNum) {
		this.branchNum = branchNum;
	}

	public List<StoreEntity> getBranchs() {
		return branchs;
	}

	public void setBranchs(List<StoreEntity> branchs) {
		this.branchs = branchs;
	}

	public List<ProductEntity> getProductEntityList() {
		return productEntityList;
	}

	public void setProductEntityList(List<ProductEntity> productEntityList) {
		this.productEntityList = productEntityList;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public ArrayList<PrivilegeEntity> getPrivilegeEntityList() {
		return privilegeEntityList;
	}

	public void setPrivilegeEntityList(ArrayList<PrivilegeEntity> privilegeEntityList) {
		this.privilegeEntityList = privilegeEntityList;
	}

	public int getGalleryNum() {
		return galleryNum;
	}

	public void setGalleryNum(int galleryNum) {
		this.galleryNum = galleryNum;
	}

	public int getPrivilegesNum() {
		return privilegesNum;
	}

	public void setPrivilegesNum(int privilegesNum) {
		this.privilegesNum = privilegesNum;
	}

	public String getIntroductionUrl() {
		return introductionUrl;
	}

	public void setIntroductionUrl(String introductionUrl) {
		this.introductionUrl = introductionUrl;
	}
}
