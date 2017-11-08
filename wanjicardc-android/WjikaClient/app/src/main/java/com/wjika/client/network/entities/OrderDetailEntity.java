package com.wjika.client.network.entities;

import com.common.utils.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Liu_ZhiChao on 2015/9/7 14:06.
 * 订单详情
 */
public class OrderDetailEntity extends Entity {

	@SerializedName("merchantNum")
	private int supportStoreNum;//分店数量

	@SerializedName("id")
	private String id;
	@SerializedName("cardOrderNo")
	private String orderNo;//订单号
	@SerializedName("cardOrderPic")
	private String cover;//订单logo
	@SerializedName("merchantCardColor")
	private String cType;
	@SerializedName("merchantCardName")
	private String name;
	@SerializedName("cardOrderAmount")
	private int buyNum;//购买数量
	@SerializedName("cardOrignal")
	private double orderAmount;//电子卡包子支付售价
	@SerializedName("cardOrignalRmb")
	private double cardOrignalRmb;//电子卡线上支付售价
	@SerializedName("reduceMoney")
	private String specialAmount;//优惠券总金额
	@SerializedName("cardOrderValue")
	private Double payAmount;//实付款（总金额）
	@SerializedName("payType")
	private int payType;//支付渠道,40表示ping++，5表示易宝
	@SerializedName("cardOrderCreatedate")
	private String date;//下单时间
	@SerializedName("merchantId")
	private String merId;
	@SerializedName("merchantName")
	private String merName;
	@SerializedName("merchantAddress")
	private String merAddress;
	@SerializedName("cardOrderStatus")
	private int status;//订单状态
	@SerializedName("charge")
	private String charge;
	@SerializedName("paychannel")
	private String payChannel;
	@SerializedName("orderType")
	private int orderType;//购卡：1 充值：2
	@SerializedName("countDown")
	private int countTime;//倒计时时间
	@SerializedName("cardFacePrice")
	private String cardFacePrice;//面值
	@SerializedName("privilegeNum")
	private int privilegeNum;
	@SerializedName("privilege")
	private ArrayList<PrivilegeEntity> privilege;
	@SerializedName("arrival")
	private String accoutDetail;
	@SerializedName("walletCount")
	private Double walletCount;
	@SerializedName("rechargeMoney")
	private Double rechargeMoney;//已充值金额

	@SerializedName("successdescribe")
	private String hintInfo;//新老用户赠送提示信息

	@SerializedName("cardColorValue")
	private String bgcolor;

	@SerializedName("payWay")
	private String payWay;//支付方式(0:易联；1:ping++；2:包子
	@SerializedName("cardSalePrice")
	private double cardSalePrice;
	@SerializedName("cardId")
	private String cardId;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String  cardId) {
		this.cardId = cardId;
	}

	public double getCardOrignalRmb() {
		return cardOrignalRmb;
	}

	public void setCardOrignalRmb(double cardOrignalRmb) {
		this.cardOrignalRmb = cardOrignalRmb;
	}

	public double getCardSalePrice() {
		return cardSalePrice;
	}

	public void setCardSalePrice(double cardSalePrice) {
		this.cardSalePrice = cardSalePrice;
	}


	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public Double getRechargeMoney() {
		return rechargeMoney;
	}

	public Double getWalletCount() {
		return walletCount;
	}

	public void setWalletCount(Double walletCount) {
		this.walletCount = walletCount;
	}

	public String getAccoutDetail() {
		return accoutDetail;
	}

	public void setAccoutDetail(String accoutDetail) {
		this.accoutDetail = accoutDetail;
	}

	public String getCardFacePrice() {
		return cardFacePrice;
	}

	public void setCardFacePrice(String cardFacePrice) {
		this.cardFacePrice = cardFacePrice;
	}

	public int getCountTime() {
		return countTime;
	}

	public void setCountTime(int countTime) {
		this.countTime = countTime;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public int getSupportStoreNum() {
		return supportStoreNum;
	}

	public void setSupportStoreNum(int supportStoreNum) {
		this.supportStoreNum = supportStoreNum;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
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

	public CardEntity.CardBGType getcType() {
		switch (cType) {
			case "1":
				return CardEntity.CardBGType.RED;
			case "2":
				return CardEntity.CardBGType.ORANGE;
			case "3":
				return CardEntity.CardBGType.BLUE;
			case "4":
				return CardEntity.CardBGType.GREEN;
			default:
				return CardEntity.CardBGType.BLUE;
		}
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getSpecialAmount() {
		return specialAmount;
	}

	public void setSpecialAmount(String specialAmount) {
		this.specialAmount = specialAmount;
	}

	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getMerAddress() {
		return merAddress;
	}

	public void setMerAddress(String merAddress) {
		this.merAddress = merAddress;
	}

	public int getStatus0() {
		return status;
	}

	public OrderPageEntity.OrderStatus getStatus() {
		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		switch (status) {
			case 0:
				return OrderPageEntity.OrderStatus.PAYING;
			case 1:
				return OrderPageEntity.OrderStatus.FINISH;
			case 2:
				return OrderPageEntity.OrderStatus.CLOSED;
			case 3:
				return OrderPageEntity.OrderStatus.UNPAY;
			default:
				return OrderPageEntity.OrderStatus.REFUND;
		}
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		//（0 支付中 1 支付完成 2 支付取消 3 待支付 5 部分退款 6 全额退款）
		String statusName = "";
		switch (status) {
			case 0:
				statusName = "支付中";
				break;
			case 1:
				statusName = "支付完成";
				break;
			case 2:
				statusName = "支付取消";
				break;
			case 3:
				statusName = "待支付";
				break;
			default:
				statusName = "已退款";
				break;
		}
		return statusName;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public int getPrivilegeNum() {
		return privilegeNum;
	}

	public void setPrivilegeNum(int privilegeNum) {
		this.privilegeNum = privilegeNum;
	}

	public ArrayList<PrivilegeEntity> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(ArrayList<PrivilegeEntity> privilege) {
		this.privilege = privilege;
	}

	public Double isRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(Double rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}

	public String getHintInfo() {
		return hintInfo;
	}

	public void setHintInfo(String hintInfo) {
		this.hintInfo = hintInfo;
	}
}
