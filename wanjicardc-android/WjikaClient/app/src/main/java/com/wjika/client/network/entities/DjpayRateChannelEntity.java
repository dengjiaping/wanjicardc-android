package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhaoweiwei on 2016/11/25.
 */

public class DjpayRateChannelEntity {

	@SerializedName("channelName")
	private String channelName;//支付通道
	@SerializedName("id")
	private String id;
	@SerializedName("tipCopy")
	private String desc;
	@SerializedName("url")
	private String logo;
	@SerializedName("type_0")
	private DjpayRateEntity djpayRateEntity0;//T+0
	@SerializedName("type_1")
	private DjpayRateEntity djpayRateEntity1;//T+1

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public DjpayRateEntity getDjpayRateEntity0() {
		return djpayRateEntity0;
	}

	public void setDjpayRateEntity0(DjpayRateEntity djpayRateEntity0) {
		this.djpayRateEntity0 = djpayRateEntity0;
	}

	public DjpayRateEntity getDjpayRateEntity1() {
		return djpayRateEntity1;
	}

	public void setDjpayRateEntity1(DjpayRateEntity djpayRateEntity1) {
		this.djpayRateEntity1 = djpayRateEntity1;
	}
}
