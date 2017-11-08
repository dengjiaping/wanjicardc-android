package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jacktian on 15/9/7.
 * 搜索店铺
 */
public class SearchStoreEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNumber;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("pages")
	private int totalPage;
	@SerializedName("total")
	private int totalSize;
	@SerializedName("result")
	private List<StoreEntity> storeEntityList;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public List<StoreEntity> getStoreEntityList() {
		return storeEntityList;
	}

	public void setStoreEntityList(List<StoreEntity> storeEntityList) {
		this.storeEntityList = storeEntityList;
	}
}
