package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/9.
 * 搜索热词
 */
public class HotKeyEntity extends Entity {

	@SerializedName("hotwordsName")
	private String name;
	@SerializedName("Count")
	private int count;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
