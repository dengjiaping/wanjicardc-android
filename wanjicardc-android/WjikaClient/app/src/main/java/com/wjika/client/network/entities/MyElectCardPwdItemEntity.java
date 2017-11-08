package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZHXIA on 2016/8/23
 * 电子卡卡密
 */
public class MyElectCardPwdItemEntity {

	@SerializedName("cardNum")
	private String myElectCardNum;
	@SerializedName("cardPassword")
	private String myElectCardPwd;

	public String getMyElectCardNum() {
		return myElectCardNum;
	}

	public void setMyElectCardNum(String myElectCardNum) {
		this.myElectCardNum = myElectCardNum;
	}

	public String getMyElectCardPwd() {
		return myElectCardPwd;
	}

	public void setMyElectCardPwd(String myElectCardPwd) {
		this.myElectCardPwd = myElectCardPwd;
	}
}
