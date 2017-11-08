package com.wjika.client.person.entity;

/**
 * 弹窗实体
 * Created by 张家洛 on 2016/8/27.
 */
public class OrderStatusEntity {

	private boolean status;
	private String name;
	private int statusOrder;
	private int typeOrder;

	public OrderStatusEntity(boolean status, String name, int statusOrder, int typeOrder) {
		this.status = status;
		this.name = name;
		this.statusOrder = statusOrder;
		this.typeOrder = typeOrder;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatusOrder() {
		return statusOrder;
	}

	public void setStatusOrder(int statusOrder) {
		this.statusOrder = statusOrder;
	}

	public int getTypeOrder() {
		return typeOrder;
	}

	public void setTypeOrder(int typeOrder) {
		this.typeOrder = typeOrder;
	}
}
