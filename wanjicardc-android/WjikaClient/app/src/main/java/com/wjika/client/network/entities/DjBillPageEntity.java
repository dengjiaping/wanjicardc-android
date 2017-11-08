package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/28.
 */

public class DjBillPageEntity {
	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("pageSize")
	private String pageSize;
	@SerializedName("pages")
	private int pages;
	@SerializedName("total")
	private String total;
	@SerializedName("result")
	private List<DjBillEntity> djBillEntity;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<DjBillEntity> getDjBillEntity() {
		return djBillEntity;
	}

	public void setDjBillEntity(List<DjBillEntity> djBillEntity) {
		this.djBillEntity = djBillEntity;
	}
}
