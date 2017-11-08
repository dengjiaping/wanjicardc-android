package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bob on 16/5/24.
 * 包子商城电子卡列表实体类
 */
public class MarketECardEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("pages")
	private int pages;
	@SerializedName("result")
	private List<ECardEntity> eCardList;

	public List<ECardEntity> geteCardList() {
		return eCardList;
	}

	public void seteCardList(List<ECardEntity> eCardList) {
		this.eCardList = eCardList;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getPages() {
		return pages;
	}
}
