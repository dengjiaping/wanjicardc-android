package com.wjika.client.network;

/**
 * Created by jacktian on 15/8/19.
 * 接口地址
 */
public class Constants {

	public static final String TOKEN = "token";
	public static final String PAGENUM = "pageNum";
	public static final String PAGESIZE = "pageSize";
	public static final int PAGE_SIZE = 10;
	public static final int PAGE_SIZE_BAOZI_BILL = 20;
	public static final int BANNER_SWITCH_TIME = 3000;

	public static class Urls {
		// 测试环境域名
//		public static String URL_BASE_DOMAIN = "http://123.56.253.122:8090";
//		public static String URL_BASE_DOMAIN = "https://apptest.wjika.com";
		// 正式环境域名
		public static String URL_BASE_DOMAIN = "https://mobilecardc.wjika.com";
		//正式环境特权域名
		public static String URL_PRIVILEGE_DOMAIN = "http://e.wjika.com/tequan";
		//启动页(闪屏页)
		public static String URL_POST_LAUNCHER_IMG = URL_BASE_DOMAIN + "/home/defaultFlashScreen";
		//获取验证码
		public static final String URL_GET_LOGIN_SIGN_CODE = URL_BASE_DOMAIN + "/common/validatecode/generateCode";
		//登陆
		public static final String URL_LOGIN = URL_BASE_DOMAIN + "/user/registers";
		//登出
		public static final String URL_LOGOUT = URL_BASE_DOMAIN + "/user/logout";
		//验证用户信息
		public static final String URL_POST_VERIFY_MOBILE = URL_BASE_DOMAIN + "/user/validatePhoneAndPassword";
		//更换手机号，校验新手机号
		public static final String URL_POST_CHANGE_PHONE = URL_BASE_DOMAIN + "/user/validUserPhones";
		//个人信息
		public static final String URL_GET_USER_INFO = URL_BASE_DOMAIN + "/userCenter/assets";
		//头像上传
		public static final String URL_POST_UPDATE_AVATAR = URL_BASE_DOMAIN + "/wjapp_mobile/card/user/imageUploads";
		//个人信息上传
		public static final String URL_POST_UPDATE_INFO = URL_BASE_DOMAIN + "/user/updateDetail";
		//获取用户实名信息
		public static final String URL_POST_OBTAIN_AUTH_INFO = URL_BASE_DOMAIN + "/pingan/realNameInformation";
		//设置支付密码
		public static final String URL_POST_FIRST_SET_PWD = URL_BASE_DOMAIN + "/user/setPaypwd";
		//全部订单
		public static String URL_POST_ORDER_LIST = URL_BASE_DOMAIN + "/userCenter/orders";
		//取消订单
		public static String URL_GET_CANCEL_ORDER = URL_BASE_DOMAIN + "/order/cancelOrder";
		//订单详情
		public static String URL_GET_ORDER_DETAIL = URL_BASE_DOMAIN + "/userCardOrder/get";
		public static String URL_POST_ORDER_DETAIL = URL_BASE_DOMAIN + "/userCenter/ordersDetail";//包子和电子卡订单详情
		//我的卡包
		public static String URL_GET_CARD_LIST = URL_BASE_DOMAIN + "/userCard/cardList";
		//卡包卡详情
		public static final String URL_GET_CARD_DETAILS = URL_BASE_DOMAIN + "/userCard/userCardDetail";
		//下订单
		public static final String URL_POST_CARD_CHARGE = URL_BASE_DOMAIN + "/userCardOrder/add";
		//消费记录
		public static String URL_GET_CONSUMPTION_LIST = URL_BASE_DOMAIN + "/userConsumerRecord/list";
		//可用优惠券 type--订单类型 购卡：buy  消费：consume 不传代表全部
		public static String URL_GET_COUPON_LIST = URL_BASE_DOMAIN + "/userCoupon/userOfCoupon";
		//添加优惠券
		public static String URL_ADD_COUPON = URL_BASE_DOMAIN + "/coupon/adds";
		//分享信息
		public static String URL_GET_INVITE_INFO = URL_BASE_DOMAIN + "/coupon/showCouponShares";
		//商品卡详情
		public static String URL_GET_BUY_CARD_DETAILS = URL_BASE_DOMAIN + "/merchantCard/details";
		//获取商铺筛选条件
		public static final String URL_GET_STORE_SEARCH_OPTION = URL_BASE_DOMAIN + "/system/searchDictionary";
		//获取商铺列表
		public static final String URL_GET_SEARCH_STORE_LIST = URL_BASE_DOMAIN + "/merchant/nearby/list";
		//获取商家分店列表
		public static final String URL_GET_STORE_BRANCH_LIST = URL_BASE_DOMAIN + "/merchant/moreBranchs";
		//获取商家详情
		public static final String URL_GET_STORE_DETAILS = URL_BASE_DOMAIN + "/merchant/detailed";
		//获取搜索热词
		public static final String URL_GET_SEARCH_HOT_KEYS = URL_BASE_DOMAIN + "/hotWords/lists";
		//获取城市信息
		public static final String URL_GET_CITY_LIST = URL_BASE_DOMAIN + "/wjkArea/openLists";
		//应用升级提醒接口
		public static final String URL_CHECK_UPDATE_VERSION = URL_BASE_DOMAIN + "/merchant/versionCompare";
		//支付密码验证接口
		public static final String URL_VERIFY_PAY_PWD = URL_BASE_DOMAIN + "/user/verifyPaypwd";
		//获取支付密码sha1加密key
		public static final String URL_PAY_PWD_SHA_KEY = URL_BASE_DOMAIN + "/user/salt?token=%1$s";
		//获取网络时间
		public static final String URL_GET_SERVER_TIME = URL_BASE_DOMAIN + "/system/getTimestamps";
		//获取密保问题
		public static final String URL_GET_SECURITY_QUESTION = URL_BASE_DOMAIN + "/system/WJKSecurityQuestion";
		//获取用户已经设置的所有安全问题
		public static final String URL_GET_USER_SECURITY_QUESTIONS = URL_BASE_DOMAIN + "/userSecurityQuestions/answers";
		//密保问题
		public static final String URL_POST_SECURITY_NEW = URL_BASE_DOMAIN + "/userSecurityQuestions/saveOrUpdates";
		//上传身份证照
		public static final String URL_POST_SECURITY_IDNOIMG = URL_BASE_DOMAIN + "security/idnoimg";
		//添加申诉信息
		public static final String URL_POST_SECURITY_ADDAPPEAL = URL_BASE_DOMAIN + "/wjapp_mobile/card/appeal/individuals";
		//获取用户密保
		public static final String URL_GET_SECURITY_UQUESTIONS = URL_BASE_DOMAIN + "/userSecurityQuestions/list";
		//密保问题验证
		public static final String URL_POST_SECURITY_VERIFY = URL_BASE_DOMAIN + "/userSecurityQuestions/validateUsers";
		//获取折扣
		public static final String URL_GET_CHARGE_AMOUNT = URL_BASE_DOMAIN + "/coupon/user/query";
		//获取首页上半部分
		public static final String URL_POST_HOME_TOP_HALF = URL_BASE_DOMAIN + "/merchant/homePageTop";
		//获取首页下半部分
		public static final String URL_POST_HOME_BOTTOM = URL_BASE_DOMAIN + "/merchant/homePageBottom";
		//首页跳转品牌墙
		public static final String URL_POST_HOME_TO_MORE_BRANDS = URL_BASE_DOMAIN + "/merchant/moreBrandes";
		//提交实名信息获取验证码
		public static final String URL_POST_OBTAIN_CODE = URL_BASE_DOMAIN + "/pingan/createAccount";
		//实名认证验证码校验
		public static final String URL_POST_AUTH_VERIFY_CODE = URL_BASE_DOMAIN + "/pingan/verifyMessage";
		//实名认证小额鉴权
		public static final String URL_POST_AUTH_VERIFY_MONEY = URL_BASE_DOMAIN + "/pingan/verifyAuthAmount";
		//消息中心
		public static final String URL_MESSAGE_CENTER = URL_BASE_DOMAIN + "/news/center";
		//消息列表
		public static final String URL_MESSAGE_LIST = URL_BASE_DOMAIN + "/news/type/list";
		//活动列表
		public static final String URL_ACTION_LIST = URL_BASE_DOMAIN + "/wjkActivity/lists";
		//个人中心未读消息
		public static final String URL_PERSON_UNREAD_MESSAGE = URL_BASE_DOMAIN + "/news/unReadNewNum";
		//充值支付界面，获取充值卡列表
		public static final String URL_CARD_RECHARGE_LISTERS = URL_BASE_DOMAIN + "/merchantCard/cardRechargeListers";
		//实名找回支付密码
		public static final String URL_FINDPASS_BYAUTH = URL_BASE_DOMAIN + "/user/verify/identity";
		//获取实名认证支持银行列表
		public static final String URL_POST_BANK_SUPPORT = URL_BASE_DOMAIN + "/pingan/bank";
		//包子商城接口
		public static final String URL_POST_EMARKET_MAIN = URL_BASE_DOMAIN + "/eMarket/firstPage";
		//电子卡列表
		public static final String URL_POST_ECARD_LIST = URL_BASE_DOMAIN + "/thirdCard/selThirdCardList";
		//电子卡详情
		public static final String URL_POST_ECARD_DETAIL = URL_BASE_DOMAIN + "/thirdCard/cardDetail";
		//我的电子卡列表
		public static final String URL_POST_MY_ELECT_CARD_LIST = URL_BASE_DOMAIN + "/thirdCard/selUserThirdCard";
		//我的包子交易记录
		public static final String URL_POST_MY_BAOZI_TRANS_RECORDS = URL_BASE_DOMAIN + "/userCenter/myBuns";
		//我的电子卡提取列表
		public static final String URL_POST_MY_ELECT_CARD_EXTRACT_LIST = URL_BASE_DOMAIN + "/thirdCard/selUserThirdPWDCard";
		//包子充值卡列表
		public static final String URL_POST_BAOZI_CARD_LIST = URL_BASE_DOMAIN + "/bun/bunList";
		//包子充值订单接口
		public static final String URL_POST_BAOZI_CHARGE_ORDER = URL_BASE_DOMAIN + "/bun/rechargebuner";
		//获取支付凭证
		public static final String URL_POST_PAY_CERTIFICATE = URL_BASE_DOMAIN + "/userCardOrder/createPayCharge";
		//电子卡生成订单
		public static final String URL_POST_CREATE_ECARD_ORDER = URL_BASE_DOMAIN + "/thirdCard/generateThirdCardOrder";
		//电子卡订单支付
		public static final String URL_POST_ECARD_ORDER_PAY = URL_BASE_DOMAIN + "/thirdCard/purchaseThirdCarder";
		//卡兑换获取卡列表
		public static final String URL_POST_EXCHANGE_CARDLIST = URL_BASE_DOMAIN + "/exchange/cardlist";
		//卡兑换获取可兑换金额数据
		public static final String URL_POST_EXCHANGE_VALUELIST = URL_BASE_DOMAIN + "/exchange/detailse";
		//卡兑换提交卡信息
		public static final String URL_POST_EXCHANGE_SUBMIT = URL_BASE_DOMAIN + "/exchange/sellcard";
		//斗金验证码
		public static final String URL_GET_VERIFICATION_CODE = "/userWithdraw/getVerificationCode";
		//斗金登录
		public static final String URL_POST_DJ_LOGIN = "/userWithdraw/user/login";
		//DJ绑定银行卡
		public static final String URL_POST_DJ_BINDCARD = "/bankcard/bindCard";
		//DJ绑定银行卡获取验证码
		public static final String URL_POST_DJ_BANK_GETCODE = "/userWithdraw/getVerificationCode";
		//DJ获取银行卡列表
		public static final String URL_POST_DJ_BANK_LISTS = "/bankcard/cards";
		//DJ获取银行卡列表删除银行卡
		public static final String URL_POST_DJ_BANK_DELETE = "/bankcard/removeBankCard";
		//DJ获取银行卡列表设置默认银行卡
		public static final String URL_POST_DJ_BANK_SETTING = "/bankcard/default/set";
		//DJ获取账单列表
		public static final String URL_POST_DJ_BILL_LIST = "/payorder/payorderlistes";
		//DJ获取费率列表
		public static final String URL_POST_DJ_RATE = "/paychannel/paylister";
		//DJ获取付款码
		public static final String URL_POST_DJ_QR = "/pay/qrCodes";
		//DJ获取收款页配置信息
		public static final String URL_POST_DJ_PAYMENT = "/head/messagers";
	}
}
