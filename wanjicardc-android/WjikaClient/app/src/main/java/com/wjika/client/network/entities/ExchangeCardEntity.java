package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhaoweiwei on 2016/11/30.
 */

public class ExchangeCardEntity implements Parcelable{
	@SerializedName("id")
	private String id;
	@SerializedName("cardColorValue")
	private String cardColorValue;
	@SerializedName("thirdCardSortId")
	private int thirdCardSortId;
	@SerializedName("newCardName")
	private String cardName;
	@SerializedName("logoUrl")
	private String logoUrl;

	protected ExchangeCardEntity(Parcel in) {
		id = in.readString();
		cardColorValue = in.readString();
		thirdCardSortId = in.readInt();
		cardName = in.readString();
		logoUrl = in.readString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCardColorValue() {
		return cardColorValue;
	}

	public void setCardColorValue(String cardColorValue) {
		this.cardColorValue = cardColorValue;
	}

	public int getThirdCardSortId() {
		return thirdCardSortId;
	}

	public void setThirdCardSortId(int thirdCardSortId) {
		this.thirdCardSortId = thirdCardSortId;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public static Creator<ExchangeCardEntity> getCREATOR() {
		return CREATOR;
	}

	public static final Creator<ExchangeCardEntity> CREATOR = new Creator<ExchangeCardEntity>() {
		@Override
		public ExchangeCardEntity createFromParcel(Parcel in) {
			return new ExchangeCardEntity(in);
		}

		@Override
		public ExchangeCardEntity[] newArray(int size) {
			return new ExchangeCardEntity[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(id);
		parcel.writeString(cardColorValue);
		parcel.writeInt(thirdCardSortId);
		parcel.writeString(cardName);
		parcel.writeString(logoUrl);
	}
}
