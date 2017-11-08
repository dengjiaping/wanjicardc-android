package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/8/18 16:29.
 * 卡列表分页实体
 */
public class ECardPageEntity extends Entity {

	@SerializedName("pages")
	private int totalPage;
	@SerializedName("result")
	private List<ECardEntity> eCardEntities;

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<ECardEntity> geteCardEntities() {
		return eCardEntities;
	}

	public void seteCardEntities(List<ECardEntity> eCardEntities) {
		this.eCardEntities = eCardEntities;
	}
}
