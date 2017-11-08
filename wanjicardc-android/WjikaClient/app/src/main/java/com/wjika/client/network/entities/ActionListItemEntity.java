package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/5/24.
 * 活动列表实体类
 */
public class ActionListItemEntity extends Entity {

	@SerializedName("id")
	private String id;
	@SerializedName("linkUrl")
	private String linkUrl;
	@SerializedName("name")
	private String name;
	@SerializedName("state")
	private String state;
	@SerializedName("word")
	private String word;
	@SerializedName("pcUrl")
	private String pcUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPcUrl() {
		return pcUrl;
	}

	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}
}
