package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZHXIA on 2016/8/18
 * 我的电子卡条目
 */
public class MyElectCardItemEntity implements Parcelable {

	@SerializedName("cardColorValue")
	private String bgcolor;
	@SerializedName("cardCount")
	private int myCardCount;
	@SerializedName("commodityName")
	private String myCardName;
	@SerializedName("faceUrl")
	private String myCardfaceUrl;
	@SerializedName("logoUrl")
	private String myCardlogoUrl;
	@SerializedName("thirdCardId")
	private String myCardItemId;
	@SerializedName("facePrice")
	private String myCardFacePrice;
	@SerializedName("orderNo")
	private String orderNo;
	private String date;

	private MyElectCardItemEntity(Parcel in) {
		bgcolor = in.readString();
		myCardCount = in.readInt();
		myCardName = in.readString();
		myCardfaceUrl = in.readString();
		myCardlogoUrl = in.readString();
		myCardItemId = in.readString();
		myCardFacePrice = in.readString();
		orderNo = in.readString();
		date = in.readString();
	}

	public static final Creator<MyElectCardItemEntity> CREATOR = new Creator<MyElectCardItemEntity>() {
		@Override
		public MyElectCardItemEntity createFromParcel(Parcel in) {
			return new MyElectCardItemEntity(in);
		}

		@Override
		public MyElectCardItemEntity[] newArray(int size) {
			return new MyElectCardItemEntity[size];
		}
	};

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public int getMyCardCount() {
		return myCardCount;
	}

	public void setMyCardCount(int myCardCount) {
		this.myCardCount = myCardCount;
	}

	public String getMyCardName() {
		return myCardName;
	}

	public void setMyCardName(String myCardName) {
		this.myCardName = myCardName;
	}

	public String getMyCardfaceUrl() {
		return myCardfaceUrl;
	}

	public void setMyCardfaceUrl(String myCardfaceUrl) {
		this.myCardfaceUrl = myCardfaceUrl;
	}

	public String getMyCardlogoUrl() {
		return myCardlogoUrl;
	}

	public void setMyCardlogoUrl(String myCardlogoUrl) {
		this.myCardlogoUrl = myCardlogoUrl;
	}

	public String getMyCardItemId() {
		return myCardItemId;
	}

	public void setMyCardItemId(String myCardItemId) {
		this.myCardItemId = myCardItemId;
	}

	public String getMyCardFacePrice() {
		return myCardFacePrice;
	}

	public void setMyCardFacePrice(String myCardFacePrice) {
		this.myCardFacePrice = myCardFacePrice;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bgcolor);
		dest.writeInt(myCardCount);
		dest.writeString(myCardName);
		dest.writeString(myCardfaceUrl);
		dest.writeString(myCardlogoUrl);
		dest.writeString(myCardItemId);
		dest.writeString(myCardFacePrice);
		dest.writeString(orderNo);
		dest.writeString(date);
	}
}
