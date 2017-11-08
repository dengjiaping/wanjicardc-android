package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bob on 16/5/24.
 * 活动列表实体类
 */
public class ActionListEntity extends Entity {

	@SerializedName("hasmore")
	private boolean hasmore;
	@SerializedName("result")
	private List<ActionListItemEntity> actionList;

	public boolean isHasmore() {
		return hasmore;
	}

	public void setHasmore(boolean hasmore) {
		this.hasmore = hasmore;
	}

	public List<ActionListItemEntity> getActionList() {
		return actionList;
	}

	public void setActionList(List<ActionListItemEntity> actionList) {
		this.actionList = actionList;
	}
}
