package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/24.
 * 版本升级实体
 */
public class UpdateVersionEntity extends Entity implements Parcelable{

    @SerializedName("version")
    private String version;
    @SerializedName("updateType")
    private int type; //0 无更新  1 提醒更新  2 强制更新
    @SerializedName("describe")
    private String desc;
    @SerializedName("url")
    private String downloadUrl;

    private UpdateVersionEntity(Parcel in) {
        version = in.readString();
        type = in.readInt();
        desc = in.readString();
        downloadUrl = in.readString();
    }

    public static final Creator<UpdateVersionEntity> CREATOR = new Creator<UpdateVersionEntity>() {
        @Override
        public UpdateVersionEntity createFromParcel(Parcel in) {
            return new UpdateVersionEntity(in);
        }

        @Override
        public UpdateVersionEntity[] newArray(int size) {
            return new UpdateVersionEntity[size];
        }
    };

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeInt(type);
        dest.writeString(desc);
        dest.writeString(downloadUrl);
    }
}
