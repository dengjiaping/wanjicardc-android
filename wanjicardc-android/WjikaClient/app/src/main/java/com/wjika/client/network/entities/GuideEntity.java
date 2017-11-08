package com.wjika.client.network.entities;

/**
 * Created by Liu_Zhichao on 2016/7/18 14:37.
 * 引导页实体
 */
public class GuideEntity {

	private int imgRes;
	private String title;
	private String desc;

	public GuideEntity(int imgRes, String title, String desc) {
		this.imgRes = imgRes;
		this.title = title;
		this.desc = desc;
	}

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
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
}
