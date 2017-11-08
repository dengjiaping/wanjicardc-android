package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/7.
 * 订单
 */
public class OrderByEntity extends OptionEntity{

	@SerializedName("key")
	private String key;
	@SerializedName("value")
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return this.value;
	}

	public String getId() {
		return this.key;
	}
}
