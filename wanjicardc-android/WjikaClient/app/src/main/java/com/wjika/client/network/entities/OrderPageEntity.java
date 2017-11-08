package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 我的订单
 * Created by me on 4/21/15.
 */
public class OrderPageEntity extends Entity {

	@SerializedName("pageNum")
	private int pageNumber;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("totalPage")
	private int totalPage;
	@SerializedName("result")
	private List<OrderEntity> orderEntityList;

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

	public List<OrderEntity> getOrderEntityList() {
		return orderEntityList;
	}

	public void setOrderEntityList(List<OrderEntity> orderEntityList) {
		this.orderEntityList = orderEntityList;
	}

	/**
	 * neworder = 10,      // 新订单，未支付
	 * process = 15,  // 处理中
	 * paid ＝ 20,    // 已支付，未发货
	 * success = 30,  // 完成
	 * refunded = 45, // 订单退回
	 * closed = 50,   // 系统关闭
	 * locked = 60    // 锁定
	 */
	public class OrderEntity {

		@SerializedName("orderId")
		private String id;
		@SerializedName("cardOrderNo")
		private String orderNo;
		@SerializedName("cardOrderPic")
		private String cover;
		@SerializedName("merchantCardColor")
		private String cType;
		@SerializedName("merchantCardName")
		private String name;
		@SerializedName("cardOrderAmount")
		private int buyNum;
		@SerializedName("cardFacePrice")
		private String facevalue;//面值
		@SerializedName("cardSalePrice")
		private Double salePrice;//售价
		@SerializedName("cardOrignal")
		private String orderAmount;//总金额
		@SerializedName("merchantId")
		private String merId;//商家id
		@SerializedName("merchantAddress")
		private String merAddress;//商家地址
		@SerializedName("merchantName")
		private String merName;//商家名称
		@SerializedName("payType")
		private int payType;//支付渠道,40表示ping++，5表示易宝
		@SerializedName("orderType")
		private int type;//10购卡，20充值
		@SerializedName("cardOrderStatus")
		private int status;
		@SerializedName("merchantCardId")
		private long pcid;
		@SerializedName("cardOrderCreateDate")
		private String date;
		@SerializedName("charge")
		private String charge;
		@SerializedName("paychannel")
		private String payChannel;
		@SerializedName("cardOrderValue")
		private String orderValue;
		@SerializedName("ifBuy")
		private int ifBuy;
		@SerializedName("cardColorValue")
		private String bgcolor;
		@SerializedName("payWay")
		private String payWay;//支付方式(0:易联；1:ping++；2:包子

		public String getPayWay() {
			return payWay;
		}

		public void setPayWay(String payWay) {
			this.payWay = payWay;
		}

		public int getIfBuy() {
			return ifBuy;
		}

		public void setIfBuy(int ifBuy) {
			this.ifBuy = ifBuy;
		}

		public String getOrderValue() {
			return orderValue;
		}

		public void setOrderValue(String orderValue) {
			this.orderValue = orderValue;
		}

		public String getPayChannel() {
			return payChannel;
		}

		public void setPayChannel(String payChannel) {
			this.payChannel = payChannel;
		}

		public String getCharge() {
			return charge;
		}

		public void setCharge(String charge) {
			this.charge = charge;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public String getCover() {
			return cover;
		}

		public void setCover(String cover) {
			this.cover = cover;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMerAddress() {
			return merAddress;
		}

		public void setMerAddress(String merAddress) {
			this.merAddress = merAddress;
		}

		public int getPayType() {
			return payType;
		}

		public void setPayType(int payType) {
			this.payType = payType;
		}

		public String getcType() {
			return cType;
		}

		public void setcType(String cType) {
			this.cType = cType;
		}

		public String getMerName() {
			return merName;
		}

		public void setMerName(String merName) {
			this.merName = merName;
		}

		public int getBuyNum() {
			return buyNum;
		}

		public void setBuyNum(int buyNum) {
			this.buyNum = buyNum;
		}

		public String getFacevalue() {
			return facevalue;
		}

		public void setFacevalue(String facevalue) {
			this.facevalue = facevalue;
		}

		public Double getSalePrice() {
			return salePrice;
		}

		public void setSalePrice(Double salePrice) {
			this.salePrice = salePrice;
		}

		public String getOrderAmount() {
			return orderAmount;
		}

		public void setOrderAmount(String orderAmount) {
			this.orderAmount = orderAmount;
		}

		public String getMerId() {
			return merId;
		}

		public void setMerId(String merId) {
			this.merId = merId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public OrderStatus getStatus() {
			//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
			switch (status) {
				case 0:
					return OrderStatus.PAYING;
				case 1:
					return OrderStatus.FINISH;
				case 2:
					return OrderStatus.CLOSED;
				case 3:
					return OrderStatus.UNPAY;
				default:
					return OrderStatus.REFUND;
			}
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public long getPcid() {
			return pcid;
		}

		public void setPcid(long pcid) {
			this.pcid = pcid;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getCardOrderValue() {
			return orderValue;
		}

		public void setCardOrderValue(String cardOrderValue) {
			this.orderValue = cardOrderValue;
		}

		public String getBgcolor() {
			return bgcolor;
		}

		public void setBgcolor(String bgcolor) {
			this.bgcolor = bgcolor;
		}
	}

	public enum OrderStatus {
		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		PAYING,
		UNPAY,
		FINISH,
		CLOSED,
		REFUND
	}
}
