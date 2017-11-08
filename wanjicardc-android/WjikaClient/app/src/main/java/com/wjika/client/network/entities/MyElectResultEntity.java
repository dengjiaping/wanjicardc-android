package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZHXIA on 2016/8/22
 */
public class MyElectResultEntity extends Entity {

	@SerializedName("purchaseDate")
	private String orderCardItemDate;
	@SerializedName("thirdCard")
	private List<MyElectCardItemEntity> myElectCardItemEntity;

	public String getOrderCardItemDate() {
		return orderCardItemDate;
	}

	public void setOrderCardItemDate(String orderCardItemDate) {
		this.orderCardItemDate = orderCardItemDate;
	}

	public List<MyElectCardItemEntity> getMyElectCardItemEntity() {
		return myElectCardItemEntity;
	}

	public void setMyElectCardItemEntity(List<MyElectCardItemEntity> myElectCardItemEntity) {
		this.myElectCardItemEntity = myElectCardItemEntity;
	}
}
