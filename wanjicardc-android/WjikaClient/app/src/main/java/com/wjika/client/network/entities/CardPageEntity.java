package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/7/9 11:47.
 * 卡列表
 */
public class CardPageEntity extends Entity {

	@SerializedName("totalAssets")
	private String totalAssets;//总资产
	@SerializedName("totalPage")
	private int totalPage;
	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("result")
	private List<CardEntity> cardEntities;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(String totalAssets) {
		this.totalAssets = totalAssets;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<CardEntity> getCardEntities() {
		return cardEntities;
	}

	public void setCardEntities(List<CardEntity> cardEntities) {
		this.cardEntities = cardEntities;
	}
}
