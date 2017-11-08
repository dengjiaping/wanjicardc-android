package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/7.
 * 店铺实体
 */
public class StoreEntity extends Entity {

	@SerializedName("merchantId")
	private String id;
	@SerializedName("merchantName")
	private String name;
	@SerializedName("merchantAddress")
	private String address;
	@SerializedName("merchantCategory")
	private String category;
	@SerializedName("totalSale")
	private int totalSale;
	@SerializedName("Longitude")
	private double longitude;
	@SerializedName("Latitude")
	private double latitude;
	@SerializedName("merchantPic")
	private String imgPath;
	@SerializedName("BusinessTime")
	private String businessTime;
	@SerializedName("merchantPhone")
	private String phone;
	@SerializedName("distance")
	private String distanceStr;
	@SerializedName("isLimitForSale")
	private boolean isLimitForSale;//是否限购
	@SerializedName("merchantLatitude")
	private String merchantLatitude;
	@SerializedName("merchantLongitude")
	private String merchantLongitude;

	public String getMerchantLatitude() {
		return merchantLatitude;
	}

	public void setMerchantLatitude(String merchantLatitude) {
		this.merchantLatitude = merchantLatitude;
	}

	public String getMerchantLongitude() {
		return merchantLongitude;
	}

	public void setMerchantLongitude(String merchantLongitude) {
		this.merchantLongitude = merchantLongitude;
	}

	public boolean isLimitForSale() {
		return isLimitForSale;
	}

	public void setLimitForSale(boolean limitForSale) {
		isLimitForSale = limitForSale;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(int totalSale) {
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

	public String getDistanceStr() {
		return distanceStr;
	}

	public void setDistanceStr(String distanceStr) {
		this.distanceStr = distanceStr;
	}
}
