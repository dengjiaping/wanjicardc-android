package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZHXIA on 2016/7/5
 */
public class MainBrandsEntity {

	public static final int TYPE_OF_STORE = 1;//商家
	public static final int TYPE_OF_ECARD = 2;//电子卡

	@SerializedName("merchantBranchId")
	private String brandId;
	@SerializedName("name")
	private String brandName;
	@SerializedName("hotNum")
	private int brandsId;
	@SerializedName("brandPhotoUrl")
	private String brandsImg;
	@SerializedName("merchantAccountId")
	private String merchantAccountId;
	@SerializedName("logoUrl")
	private String logoImg;
	@SerializedName("type")
	private int type;//1:商家；2：电子卡
	@SerializedName("colour")
	private String bgcolor;

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getBrandsId() {
		return brandsId;
	}

	public void setBrandsId(int brandsId) {
		this.brandsId = brandsId;
	}

	public String getBrandsImg() {
		return brandsImg;
	}

	public void setBrandsImg(String brandsImg) {
		this.brandsImg = brandsImg;
	}

	public String getMerchantAccountId() {
		return merchantAccountId;
	}

	public void setMerchantAccountId(String merchantAccountId) {
		this.merchantAccountId = merchantAccountId;
	}

	public String getLogoImg() {
		return logoImg;
	}

	public void setLogoImg(String logoImg) {
		this.logoImg = logoImg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
}
