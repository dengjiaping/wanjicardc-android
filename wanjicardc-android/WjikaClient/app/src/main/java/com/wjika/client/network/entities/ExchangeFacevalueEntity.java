package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/12/1.
 */

public class ExchangeFacevalueEntity implements Parcelable{
	@SerializedName("id")
	private String id;
	@SerializedName("thirdCardFacePrice")
	private String faceValue;
	@SerializedName("bunNum")
	private String bunNum;
	private boolean isChecked;

	protected ExchangeFacevalueEntity(Parcel in) {
		id = in.readString();
		faceValue = in.readString();
		bunNum = in.readString();
		isChecked = in.readByte() != 0;
	}

	public static final Creator<ExchangeFacevalueEntity> CREATOR = new Creator<ExchangeFacevalueEntity>() {
		@Override
		public ExchangeFacevalueEntity createFromParcel(Parcel in) {
			return new ExchangeFacevalueEntity(in);
		}

		@Override
		public ExchangeFacevalueEntity[] newArray(int size) {
			return new ExchangeFacevalueEntity[size];
		}
	};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(String faceValue) {
		this.faceValue = faceValue;
	}

	public String getBunNum() {
		return bunNum;
	}

	public void setBunNum(String bunNum) {
		this.bunNum = bunNum;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(id);
		parcel.writeString(faceValue);
		parcel.writeString(bunNum);
		parcel.writeByte((byte) (isChecked ? 1 : 0));
	}
}
