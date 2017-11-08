package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/12.
 * 城市
 */
public class CityEntity extends Entity {

	@SerializedName("cityId")
	private String id;
	@SerializedName("name")
	private String name;
	@SerializedName("cityAbbreviation")
	private String pinYinShort;
	@SerializedName("available")
	private int isAvailable;
	@SerializedName("popular")
	private String isPopular;
	@SerializedName("parentId")
	private String parentId;
	@SerializedName("level")
	private int level;
	@SerializedName("operationType")
	private int operationType;

	private boolean isSelected;

	private boolean isCurrentCity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinYinShort() {
		return pinYinShort;
	}

	public void setPinYinShort(String pinYinShort) {
		this.pinYinShort = pinYinShort;
	}

	public boolean isAvailable() {
		return isAvailable == 1;
	}

	public void setIsAvailable(int isAvailable) {
		this.isAvailable = isAvailable;
	}

	public boolean isPopular() {
		return "true".equalsIgnoreCase(isPopular);
	}

	public void setIsPopular(String isPopular) {
		this.isPopular = isPopular;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isCurrentCity() {
		return isCurrentCity;
	}

	public void setIsCurrentCity(boolean isCurrentCity) {
		this.isCurrentCity = isCurrentCity;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public CityOperationType getOperationType() {
		//卡背景状态： 1 红色，2黄色，3蓝色 4绿色
		switch (operationType) {
			case 1:
				return CityOperationType.INSERT;
			case 3:
				return CityOperationType.UPDATE;
			case 2:
				return CityOperationType.DELETE;
			default:
				return CityOperationType.NONE;
		}
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public enum CityOperationType {
		NONE,
		INSERT,
		DELETE,
		UPDATE,
	}
}
