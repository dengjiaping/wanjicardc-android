package com.wjika.client.network.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/1.
 * 用户实体
 */
public class UserEntity extends Entity implements Parcelable {

	private String id;
	@SerializedName("userName")
	private String name;
	@SerializedName("userRealname")
	private String userRealName;
	@SerializedName("userPhone")
	private String phone;
	@SerializedName("token")
	private String Token;
	@SerializedName("isNewUser")
	private boolean isNewUser;
	@SerializedName("isSetpaypwd")
	private boolean isSetPayPassword;//是否设置支付密码
	@SerializedName("isSetsecurity")
	private boolean isSetSecurity;//是否设置安全问题
	@SerializedName("userSex")
	private int gender;//true-男 false-女
	@SerializedName("address")
	private String address;
	@SerializedName("userIdcard")
	private String idNo;//身份证
	@SerializedName("userBirthday")
	private String birthDay;
	@SerializedName("userPhoto")
	private String headImg;//头像地址
	@SerializedName("isAuthentication")
	private int authentication;//实名认证,0未认证,1审核中,2已认证
	@SerializedName("appealStatus")
	private int appealStatus;//申诉状态，10申诉中，30申诉成功
	@SerializedName("userLock")
	private boolean isLocked;
	@SerializedName("userPasswordSalt")
	private String payPwdSalt;
	@SerializedName("couponCount")
	private int couponNum;

	@SerializedName("identity")
	private String identity;
	@SerializedName("isOpennopay")
	private String isOpennopay;
	@SerializedName("lockedStatus")
	private boolean lockedStatus;
	@SerializedName("lockedMessage")
	private String lockedMessage;
	@SerializedName("cardCount")//商家卡数量
	private int cardCount;
	@SerializedName("thirdCardCount")//电子卡数量
	private int thirdCardCount;
	@SerializedName("walletCount")//包子数量
	private double walletCount;
	@SerializedName("ifRecharge")//包子是否可以充值
	private boolean ifRecharge;

	private UserEntity(Parcel in) {
		id = in.readString();
		name = in.readString();
		userRealName = in.readString();
		phone = in.readString();
		Token = in.readString();
		isNewUser = in.readByte() != 0;
		isSetPayPassword = in.readByte() != 0;
		isSetSecurity = in.readByte() != 0;
		gender = in.readInt();
		address = in.readString();
		idNo = in.readString();
		birthDay = in.readString();
		headImg = in.readString();
		authentication = in.readInt();
		appealStatus = in.readInt();
		isLocked = in.readByte() != 0;
		payPwdSalt = in.readString();
		couponNum = in.readInt();
		identity = in.readString();
		isOpennopay = in.readString();
		lockedStatus = in.readByte() != 0;
		lockedMessage = in.readString();
		cardCount = in.readInt();
		thirdCardCount = in.readInt();
		walletCount = in.readDouble();
		ifRecharge = in.readByte() != 0;
	}

	public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
		@Override
		public UserEntity createFromParcel(Parcel in) {
			return new UserEntity(in);
		}

		@Override
		public UserEntity[] newArray(int size) {
			return new UserEntity[size];
		}
	};

	public int getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isSetPayPassword() {
		return isSetPayPassword;
	}

	public void setIsSetPayPassword(boolean isSetPayPassword) {
		this.isSetPayPassword = isSetPayPassword;
	}

	public boolean isSetSecurity() {
		return isSetSecurity;
	}

	public void setSetSecurity(boolean setSecurity) {
		isSetSecurity = setSecurity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setIsNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getIsOpennopay() {
		return isOpennopay;
	}

	public void setIsOpennopay(String isOpennopay) {
		this.isOpennopay = isOpennopay;
	}

	public boolean isLockedStatus() {
		return lockedStatus;
	}

	public void setLockedStatus(boolean lockedStatus) {
		this.lockedStatus = lockedStatus;
	}

	public String getLockedMessage() {
		return lockedMessage;
	}

	public void setLockedMessage(String lockedMessage) {
		this.lockedMessage = lockedMessage;
	}

	public String isGender() {
		if (gender == 1) {
			return "男";
		} else if (gender == 2) {
			return "女";
		} else {
			return "未设置";
		}
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public int getAuthentication() {
		return authentication;
	}

	public void setAuthentication(int authentication) {
		this.authentication = authentication;
	}

	public int getAppealStatus() {
		return appealStatus;
	}

	public void setAppealStatus(int appealStatus) {
		this.appealStatus = appealStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getPayPwdSalt() {
		return payPwdSalt;
	}

	public void setPayPwdSalt(String payPwdSalt) {
		this.payPwdSalt = payPwdSalt;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public int getThirdCardCount() {
		return thirdCardCount;
	}

	public void setThirdCardCount(int thirdCardCount) {
		this.thirdCardCount = thirdCardCount;
	}

	public double getWalletCount() {
		return walletCount;
	}

	public void setWalletCount(double walletCount) {
		this.walletCount = walletCount;
	}

	public boolean isIfRecharge() {
		return ifRecharge;
	}

	public void setIfRecharge(boolean ifRecharge) {
		this.ifRecharge = ifRecharge;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(userRealName);
		dest.writeString(phone);
		dest.writeString(Token);
		dest.writeByte((byte) (isNewUser ? 1 : 0));
		dest.writeByte((byte) (isSetPayPassword ? 1 : 0));
		dest.writeByte((byte) (isSetSecurity ? 1 : 0));
		dest.writeInt(gender);
		dest.writeString(address);
		dest.writeString(idNo);
		dest.writeString(birthDay);
		dest.writeString(headImg);
		dest.writeInt(authentication);
		dest.writeInt(appealStatus);
		dest.writeByte((byte) (isLocked ? 1 : 0));
		dest.writeString(payPwdSalt);
		dest.writeInt(couponNum);
		dest.writeString(identity);
		dest.writeString(isOpennopay);
		dest.writeByte((byte) (lockedStatus ? 1 : 0));
		dest.writeString(lockedMessage);
		dest.writeInt(cardCount);
		dest.writeInt(thirdCardCount);
		dest.writeDouble(walletCount);
		dest.writeByte((byte) (ifRecharge ? 1 : 0));
	}
}
