package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;
import com.wjika.cardagent.client.R;

/**
 * Created by bob on 16/8/17.
 * 包子订单
 */
public class BaoziTransRecordsEntity extends Entity {

	private static final String ORDER_TYPE_BAOZI_RECHARGE = "3";//充值包子
	private static final String ORDER_TYPE_BAOZI_BUY_ECARD = "4";//购买电子卡

	@SerializedName("days")
	private String days;
	@SerializedName("bun")
	private String bun;
	@SerializedName("typeStr")
	private String typeStr;
	@SerializedName("git")
	private String git;
	@SerializedName("type")
	private String type;
	@SerializedName("dayStr")
	private String dayStr;
	@SerializedName("orderNo")
	private String orderNo;

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getBun() {
		return bun;
	}

	public void setBun(String bun) {
		this.bun = bun;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getIconByType() {
		if (ORDER_TYPE_BAOZI_RECHARGE.equals(type)) { //充值包子
			return R.drawable.mybaozi_ic_paybaozi;
		} else if (ORDER_TYPE_BAOZI_BUY_ECARD.equals(type)) { //购买电子卡
			return R.drawable.mybaozi_ic_card;
		} else {
			return R.drawable.mybaozi_ic_card;
		}
	}

	public int getBaoziTextColorResId() {
		if (ORDER_TYPE_BAOZI_RECHARGE.equals(type)) { //充值包子
			return R.color.wjika_client_title_bg;
		} else if (ORDER_TYPE_BAOZI_BUY_ECARD.equals(type)) { //购买电子卡
			return R.color.wjika_client_card_red;
		} else {
			return R.color.wjika_client_card_red;
		}
	}
}
