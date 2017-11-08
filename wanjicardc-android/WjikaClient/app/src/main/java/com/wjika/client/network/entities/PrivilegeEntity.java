package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/8.
 * 特权
 */
public class PrivilegeEntity extends Entity implements Parcelable {

	@SerializedName("privilegeId")
	private String privilegeId;
	@SerializedName("merchantPrivilegeDes")
	private String details;
	@SerializedName("privilegeName")
	private String name;
	@SerializedName("privilegePic")
	private String imgPath;
	@SerializedName("inUse")
	private boolean isHave;
	@SerializedName("privilegeType")
	private int privilegeType; // 5是特权，10是服务

	protected PrivilegeEntity(Parcel in) {
		privilegeId = in.readString();
		details = in.readString();
		name = in.readString();
		imgPath = in.readString();
		privilegeType = in.readInt();
		isHave = (in.readByte() == 1);
	}

	public static final Creator<PrivilegeEntity> CREATOR = new Creator<PrivilegeEntity>() {
		@Override
		public PrivilegeEntity createFromParcel(Parcel in) {
			return new PrivilegeEntity(in);
		}

		@Override
		public PrivilegeEntity[] newArray(int size) {
			return new PrivilegeEntity[size];
		}
	};

	public boolean isHave() {
		return isHave;
	}

	public void setIsHave(boolean isHave) {
		this.isHave = isHave;
	}

	public int getType() {
		return privilegeType;
	}

	public void setType(int type) {
		this.privilegeType = type;
	}

	public String getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		dest.writeString(privilegeId);
		dest.writeString(details);
		dest.writeString(name);
		dest.writeString(imgPath);
		dest.writeInt(privilegeType);
		dest.writeByte(isHave ? (byte) 1 : 0);
	}
}
