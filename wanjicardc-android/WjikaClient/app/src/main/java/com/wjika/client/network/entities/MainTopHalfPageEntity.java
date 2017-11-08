package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZHXIA on 2016/6/13
 * 首页上半部分
 */
public class MainTopHalfPageEntity extends Entity {

	//活动
	@SerializedName("activities")
	private List<MainActionsEntity> activitiesList;
	//品牌
	@SerializedName("brands")
	private List<MainBrandsEntity> brandsList;
	//城市活动
	@SerializedName("cityActivity")
	private List<MainCityActivitiesEntity> cityActivityList;

	public List<MainCityActivitiesEntity> getCityActivityList() {
		return cityActivityList;
	}

	public void setCityActivityList(List<MainCityActivitiesEntity> cityActivityList) {
		this.cityActivityList = cityActivityList;
	}

	public List<MainActionsEntity> getActivitiesList() {
		return activitiesList;
	}

	public void setActivitiesList(List<MainActionsEntity> activitiesList) {
		this.activitiesList = activitiesList;
	}

	public List<MainBrandsEntity> getBrandsList() {
		return brandsList;
	}

	public void setBrandsList(List<MainBrandsEntity> brandsList) {
		this.brandsList = brandsList;
	}
}
