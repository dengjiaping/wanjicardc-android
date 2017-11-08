package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/6 15:01.
 * 消费记录
 */
public class ConsumptionPageEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNumber;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("total")
	private int total;
	@SerializedName("pages")
	private int totalPage;
	@SerializedName("result")
	private List<ConsumptionEntity> consumptionEntityList;

	public int getPages() {
		return totalPage;
	}

	public void setPages(int pages) {
		this.totalPage = pages;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<ConsumptionEntity> getConsumptionEntityList() {
		return consumptionEntityList;
	}

	public void setConsumptionEntityList(List<ConsumptionEntity> consumptionEntityList) {
		this.consumptionEntityList = consumptionEntityList;
	}

	/**
	 * status  1 支付成功, 0等待支付,2已退款,3冲正关闭
	 * 消费订单
	 */
	public static class ConsumptionEntity{

		@SerializedName("consumerRecordId")
		private String id;
		@SerializedName("consumerRecoreNo")
		private String paymentNo;
		@SerializedName("merchantId")
		private String merId;
		@SerializedName("userId")
		private String userId;
		@SerializedName("consumerRecoreValue")
		private String amount;
		@SerializedName("cosumerRecordCreateDate")
		private String date;
		@SerializedName("merchantName")
		private String merName;
		@SerializedName("consumerRecordStatus")
		private int status;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPaymentNo() {
			return paymentNo;
		}

		public void setPaymentNo(String paymentNo) {
			this.paymentNo = paymentNo;
		}

		public String getMerId() {
			return merId;
		}

		public void setMerId(String merId) {
			this.merId = merId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getMerName() {
			return merName;
		}

		public void setMerName(String merName) {
			this.merName = merName;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
	}
}
