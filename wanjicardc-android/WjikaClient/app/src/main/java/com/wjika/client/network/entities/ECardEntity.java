package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.wjika.cardagent.client.R;

/**
 * Created by Liu_Zhichao on 2016/8/18 16:30.
 * 卡详情
 */
public class ECardEntity implements Parcelable {

	public static final int ACTIVITY_TYPE_LIMIT = 1;//限购活动

	@SerializedName("id")
	private String id;
	@SerializedName("cardColor")
	private int cardColor;
	@SerializedName("cardColorValue")
	private String bgcolor;
	@SerializedName("commodityName")
	private String name;
	@SerializedName("logoUrl")
	private String logoUrl;
	@SerializedName("facePrice")
	private String facePrice;
	@SerializedName("salePrice")
	private double salePrice;//包子售价
	@SerializedName("salePriceRmb")
	private double RMBSalePrice;//人民币售价
	@SerializedName("adSale")
	private String discount;//优惠提示信息

	@SerializedName("soldCount")
	private String soldCount;
	@SerializedName("allowBuyCount")
	private int canBuyNum;//限购数量中剩余可购买数量
	@SerializedName("limitCount")
	private int limitCount;//限购数量
	@SerializedName("activityType")
	private int activityType;//活动类型

	@SerializedName("stock")
	private int stock;//库存
	@SerializedName("ad")
	private String ad;
	@SerializedName("useRule")
	private String useRule;

	@SerializedName("Url")
	private String url;
	@SerializedName("Desc")
	private String desc;
	@SerializedName("Title")
	private String title;

	public ECardEntity(String name, String facePrice, double salePrice, double RMBSalePrice) {
		this.name = name;
		this.facePrice = facePrice;
		this.salePrice = salePrice;
		this.RMBSalePrice = RMBSalePrice;
	}

	public ECardEntity(String id, String logoUrl, String name, double salePrice, double RMBSalePrice, String bgcolor) {
		this.id = id;
		this.logoUrl = logoUrl;
		this.name = name;
		this.salePrice = salePrice;
		this.RMBSalePrice = RMBSalePrice;
		this.bgcolor = bgcolor;
	}

	protected ECardEntity(Parcel in) {
		id = in.readString();
		cardColor = in.readInt();
		bgcolor = in.readString();
		name = in.readString();
		logoUrl = in.readString();
		facePrice = in.readString();
		salePrice = in.readDouble();
		RMBSalePrice = in.readDouble();
		discount = in.readString();
		soldCount = in.readString();
		canBuyNum = in.readInt();
		limitCount = in.readInt();
		activityType = in.readInt();
		stock = in.readInt();
		ad = in.readString();
		useRule = in.readString();
		url = in.readString();
		desc = in.readString();
		title = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(cardColor);
		dest.writeString(bgcolor);
		dest.writeString(name);
		dest.writeString(logoUrl);
		dest.writeString(facePrice);
		dest.writeDouble(salePrice);
		dest.writeDouble(RMBSalePrice);
		dest.writeString(discount);
		dest.writeString(soldCount);
		dest.writeInt(canBuyNum);
		dest.writeInt(limitCount);
		dest.writeInt(activityType);
		dest.writeInt(stock);
		dest.writeString(ad);
		dest.writeString(useRule);
		dest.writeString(url);
		dest.writeString(desc);
		dest.writeString(title);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ECardEntity> CREATOR = new Creator<ECardEntity>() {
		@Override
		public ECardEntity createFromParcel(Parcel in) {
			return new ECardEntity(in);
		}

		@Override
		public ECardEntity[] newArray(int size) {
			return new ECardEntity[size];
		}
	};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getColorResId() {
		//卡背景状态： 1 红色，2黄色，3蓝色 4绿色
		switch (cardColor) {
			case 3:
				return R.color.wjika_client_card_blue;
			case 1:
				return R.color.wjika_client_card_red;
			case 2:
				return R.color.wjika_client_card_yellow;
			case 4:
				return R.color.wjika_client_card_green;
			default:
				return R.color.wjika_client_card_blue;
		}
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public void setCardColor(int cardColor) {
		this.cardColor = cardColor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getFacePrice() {
		return facePrice;
	}

	public void setFacePrice(String facePrice) {
		this.facePrice = facePrice;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public String getSoldCount() {
		return soldCount;
	}

	public void setSoldCount(String soldCount) {
		this.soldCount = soldCount;
	}

	public int getCanBuyNum() {
		return canBuyNum;
	}

	public void setCanBuyNum(int canBuyNum) {
		this.canBuyNum = canBuyNum;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public int getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getAd() {
		return ad;
	}

	public void setAd(String ad) {
		this.ad = ad;
	}

	public String getUseRule() {
		return useRule;
	}

	public void setUseRule(String useRule) {
		this.useRule = useRule;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public double getRMBSalePrice() {
		return RMBSalePrice;
	}

	public void setRMBSalePrice(double RMBSalePrice) {
		this.RMBSalePrice = RMBSalePrice;
	}
}
