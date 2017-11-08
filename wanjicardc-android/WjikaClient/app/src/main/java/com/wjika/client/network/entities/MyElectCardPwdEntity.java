package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZHXIA on 2016/8/23
 */
public class MyElectCardPwdEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("pages")
	private int pages;
	@SerializedName("total")
	private int total;
	@SerializedName("result")
	private List<MyElectCardPwdItemEntity> myElectCardPwdItemEntity;

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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<MyElectCardPwdItemEntity> getMyElectCardPwdItemEntity() {
		return myElectCardPwdItemEntity;
	}

	public void setMyElectCardPwdItemEntity(List<MyElectCardPwdItemEntity> myElectCardPwdItemEntity) {
		this.myElectCardPwdItemEntity = myElectCardPwdItemEntity;
	}
}
