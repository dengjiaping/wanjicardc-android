package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/5/24.
 * 我的资产消息
 */
public class MyAssetEntity extends Entity {

	@SerializedName("type")
	private String type;
	@SerializedName("theme")
	private String theme;
	@SerializedName("pcUrl")
	private String pcUrl;
	@SerializedName("word")
	private String word;
	@SerializedName("activityUrl")
	private String activityUrl;
	@SerializedName("content")
	private String content;
	@SerializedName("time")
	private String time;
	@SerializedName("date")
	private String date;
	@SerializedName("reqParam")
	private String reqParam;
	@SerializedName("money")
	private String money;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getPcUrl() {
		return pcUrl;
	}

	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getActivityUrl() {
		return activityUrl;
	}

	public void setActivityUrl(String activityUrl) {
		this.activityUrl = activityUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReqParam() {
		return reqParam;
	}

	public void setReqParam(String reqParam) {
		this.reqParam = reqParam;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}
}
