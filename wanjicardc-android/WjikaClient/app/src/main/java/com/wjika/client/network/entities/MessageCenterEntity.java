package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;
import com.wjika.cardagent.client.R;

/**
 * Created by bob on 16/5/23.
 * 消息中心
 */
public class MessageCenterEntity extends Entity {

	public static final String TYPE_SYSTEM_MESSAGE = "1";
	public static final String TYPE_ACTION_MESSAGE = "2";
	public static final String TYPE_ASSET_MESSAGE = "3";
	public static final String TYPE_CONSUME_MESSAGE = "4";

	@SerializedName("type")
	private String type;
	@SerializedName("reqParam")
	private String reqParam;
	@SerializedName("money")
	private String money;
	@SerializedName("isRead")
	private String isRead;

	public String getReqParam() {
		return reqParam;
	}

	public void setReqParam(String reqParam) {
		this.reqParam = reqParam;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public int getIconResId() {
		if (TYPE_CONSUME_MESSAGE.equals(type)) {
			return R.drawable.consume_msg_icon;
		} else if (TYPE_ASSET_MESSAGE.equals(type)) {
			return R.drawable.my_asset_icon;
		} else if (TYPE_ACTION_MESSAGE.equals(type)) {
			return R.drawable.action_msg_icon;
		} else if (TYPE_SYSTEM_MESSAGE.equals(type)) {
			return R.drawable.system_msg_icon;
		} else {
			return R.drawable.consume_msg_icon;
		}
	}

	public int getTitleResIdByType() {
		if (TYPE_CONSUME_MESSAGE.equals(type)) {
			return R.string.message_center_consume;
		} else if (TYPE_ASSET_MESSAGE.equals(type)) {
			return R.string.message_center_asset;
		} else if (TYPE_ACTION_MESSAGE.equals(type)) {
			return R.string.message_center_action;
		} else if (TYPE_SYSTEM_MESSAGE.equals(type)) {
			return R.string.message_center_system;
		}
		return R.string.message_center_consume;
	}
}
