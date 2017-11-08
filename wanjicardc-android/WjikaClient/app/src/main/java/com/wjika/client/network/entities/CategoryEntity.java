package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacktian on 15/9/7.
 * 分类
 */
public class CategoryEntity extends OptionEntity{

	@SerializedName("categoryId")
	private String id;
	@SerializedName("description")
	private String name;
	@SerializedName("parentid")
	private String parentId;

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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}
