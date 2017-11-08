package com.wjika.client.network.entities;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_ZhiChao on 2015/9/18 18:12.
 * 第三方分享
 */
public class ShareEntity {

	@SerializedName("Title")
	private String title;
	@SerializedName("Desc")
	private String desc;
	@SerializedName("Url")
	private String url;

	private String name;
	private Drawable logo;

	public ShareEntity(String name, Drawable logo) {
		this.name = name;
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getLogo() {
		return logo;
	}

	public void setLogo(Drawable logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
