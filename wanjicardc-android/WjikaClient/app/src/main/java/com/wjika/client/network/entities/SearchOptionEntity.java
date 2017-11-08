package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jacktian on 15/9/7.
 * 搜索
 */
public class SearchOptionEntity extends Entity {

	@SerializedName("Category")
	private List<CategoryEntity> categorys;
	@SerializedName("KeyOrder")
	private List<OrderByEntity> orderBys;

	public List<CategoryEntity> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<CategoryEntity> categorys) {
		this.categorys = categorys;
	}

	public List<OrderByEntity> getOrderBys() {
		return orderBys;
	}

	public void setOrderBys(List<OrderByEntity> orderBys) {
		this.orderBys = orderBys;
	}
}
