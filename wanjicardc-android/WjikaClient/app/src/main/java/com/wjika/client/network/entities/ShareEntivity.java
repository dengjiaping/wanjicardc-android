package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu_ZhiChao on 2015/9/16 19:38.
 * 邀请分享
 */
public class ShareEntivity extends Entity{

	@SerializedName("title")
	private String title;
	@SerializedName("content")
	private String content;
	@SerializedName("imgUrl")
	private String imgUrl;
	@SerializedName("shareContent")
	private String shareContent;
	@SerializedName("shareImgUrl")
	private String shareImgUrl;
	@SerializedName("titleUrl")
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getShareContent() {
		return shareContent;
	}

	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}

	public String getShareImgUrl() {
		return shareImgUrl;
	}

	public void setShareImgUrl(String shareImgUrl) {
		this.shareImgUrl = shareImgUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
