package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/7/20.
 * 优惠券
 */
public class CouponPageEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNum;//当前页
	@SerializedName("pageSize")
	private int pageSize;//每页数量
	@SerializedName("pages")
	private int pages;//总页数
	@SerializedName("useableNum")
	private int useableNum;//可用优惠券数量
	@SerializedName("total")
	private int total;//总条数
	@SerializedName("result")
	private List<CouponEntity> couponEntities;//总条数

	public List<CouponEntity> getCouponEntities() {
		return couponEntities;
	}

	public void setCouponEntities(List<CouponEntity> couponEntities) {
		this.couponEntities = couponEntities;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getUseableNum() {
		return useableNum;
	}

	public void setUseableNum(int useableNum) {
		this.useableNum = useableNum;
	}
}
