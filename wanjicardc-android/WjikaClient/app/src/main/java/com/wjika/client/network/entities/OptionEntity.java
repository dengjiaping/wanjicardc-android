package com.wjika.client.network.entities;

import java.util.List;

/**
 * Created by jacktian on 15/9/7.
 * 城市选择
 */
public class OptionEntity {

	private String id;
	private String name;
	private boolean isSelected;

	private List<OptionEntity> optionEntities;

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

	public List<OptionEntity> getOptionEntities() {
		return optionEntities;
	}

	public void setOptionEntities(List<OptionEntity> optionEntities) {
		this.optionEntities = optionEntities;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
