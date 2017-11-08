package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/15.
 * 店铺图片
 */
public class StoreImgEntity implements Parcelable {

	@SerializedName("merchantPhotoName")
	private String desc;
	@SerializedName("Id")
	private String id;
	@SerializedName("photoType")
	private int type;
	@SerializedName("merchantPhotoAppsrc")
	private String imgPath;

	private StoreImgEntity(Parcel in) {
		desc = in.readString();
		id = in.readString();
		type = in.readInt();
		imgPath = in.readString();
	}

	public static final Creator<StoreImgEntity> CREATOR = new Creator<StoreImgEntity>() {
		@Override
		public StoreImgEntity createFromParcel(Parcel in) {
			return new StoreImgEntity(in);
		}

		@Override
		public StoreImgEntity[] newArray(int size) {
			return new StoreImgEntity[size];
		}
	};

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(desc);
		dest.writeString(id);
		dest.writeInt(type);
		dest.writeString(imgPath);
	}
}
