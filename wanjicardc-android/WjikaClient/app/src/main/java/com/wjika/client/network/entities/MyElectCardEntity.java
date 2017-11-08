package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZHXIA on 2016/8/18
 * 我的电子卡
 */
public class MyElectCardEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("pages")
	private int pages;
	@SerializedName("result")
	private List<MyElectResultEntity> myCardResultList;
	@SerializedName("total")
	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public List<MyElectResultEntity> getMyCardResultList() {
		return myCardResultList;
	}

	public void setMyCardResultList(List<MyElectResultEntity> myCardResultList) {
		this.myCardResultList = myCardResultList;
	}
}
