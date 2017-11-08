package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bob on 16/5/24.
 * 包子商城实体类
 */
public class MarketMainEntity extends Entity {

	@SerializedName("thirdCard")//电子卡
	private MarketECardEntity marketECardEntity;
	@SerializedName("user")//用户包子信息
	private MarketUserEntity marketUserEntity;
	@SerializedName("total")
	private int total;

	public MarketECardEntity getMarketECardEntity() {
		return marketECardEntity;
	}

	public void setMarketECardEntity(MarketECardEntity marketECardEntity) {
		this.marketECardEntity = marketECardEntity;
	}

	public MarketUserEntity getMarketUserEntity() {
		return marketUserEntity;
	}

	public void setMarketUserEntity(MarketUserEntity marketUserEntity) {
		this.marketUserEntity = marketUserEntity;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
