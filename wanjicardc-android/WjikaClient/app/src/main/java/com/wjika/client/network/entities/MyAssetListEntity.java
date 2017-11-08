package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bob on 16/5/24.
 * 我的资产消息
 */
public class MyAssetListEntity extends Entity {

	@SerializedName("nextPage")
	private String nextPage;
	@SerializedName("pages")
	private int pages;
	@SerializedName("result")
	private List<MyAssetEntity> list;

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public List<MyAssetEntity> getList() {
		return list;
	}

	public void setList(List<MyAssetEntity> list) {
		this.list = list;
	}
}
