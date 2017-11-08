package com.wjika.client.network.parser;

import android.content.Context;

import com.common.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wjika.client.network.entities.ActionListEntity;
import com.wjika.client.network.entities.AuthInfoEntity;
import com.wjika.client.network.entities.AuthenticationEntity;
import com.wjika.client.network.entities.BaoziChargeEntity;
import com.wjika.client.network.entities.BaoziPayEntity;
import com.wjika.client.network.entities.CardDetailEntity;
import com.wjika.client.network.entities.CardPageEntity;
import com.wjika.client.network.entities.CardPkgDetailEntity;
import com.wjika.client.network.entities.ChargeEntity;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.network.entities.ConsumptionPageEntity;
import com.wjika.client.network.entities.CouponPageEntity;
import com.wjika.client.network.entities.DJPayCodeEntity;
import com.wjika.client.network.entities.DJPaymentEntity;
import com.wjika.client.network.entities.DJUserEntity;
import com.wjika.client.network.entities.DisCountCouponEntity;
import com.wjika.client.network.entities.DjBankListEntity;
import com.wjika.client.network.entities.DjBillPageEntity;
import com.wjika.client.network.entities.DjpayRateChannelEntity;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.ECardOrderEntity;
import com.wjika.client.network.entities.ECardPageEntity;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.ExchangeCardPageEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.network.entities.HotKeyEntity;
import com.wjika.client.network.entities.MainTopHalfPageEntity;
import com.wjika.client.network.entities.MarketMainEntity;
import com.wjika.client.network.entities.MessageCenterEntity;
import com.wjika.client.network.entities.MyAssetListEntity;
import com.wjika.client.network.entities.MyBaoziEntity;
import com.wjika.client.network.entities.MyElectCardEntity;
import com.wjika.client.network.entities.MyElectCardPwdEntity;
import com.wjika.client.network.entities.OrderDetailEntity;
import com.wjika.client.network.entities.OrderPageEntity;
import com.wjika.client.network.entities.OrderPayEntity;
import com.wjika.client.network.entities.PayCertificateEntity;
import com.wjika.client.network.entities.PayVerifyPwdEntity;
import com.wjika.client.network.entities.QuestionItemEntity;
import com.wjika.client.network.entities.SearchOptionEntity;
import com.wjika.client.network.entities.SearchStoreEntity;
import com.wjika.client.network.entities.SetPayPwdEntity;
import com.wjika.client.network.entities.ShareEntivity;
import com.wjika.client.network.entities.StoreDetailsEntity;
import com.wjika.client.network.entities.SupportBankEntity;
import com.wjika.client.network.entities.UpdateVersionEntity;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.network.entities.UserQuestionEntity;
import com.wjika.client.network.entities.VerifyResultEntity;
import com.wjika.client.network.json.GsonObjectDeserializer;
import com.wjika.client.utils.CityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/8/30.
 * json解析
 */
public class Parsers {

	public static Gson gson = GsonObjectDeserializer.produceGson();
	public static final String TAG = Parsers.class.getSimpleName();

	/**
	 * @return 闪屏页图片
	 */
	public static String getLauncherImg(String data) {
		String imgUrl = "";
		try {
			JSONObject jsonObject = new JSONObject(data);
			jsonObject = new JSONObject(jsonObject.optString("val"));
			imgUrl = jsonObject.optString("picUrl");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return imgUrl;
	}

	/**
	 * @return 卡包列表
	 */
	public static CardPageEntity getCardPage(String data) {
		CardPageEntity cardPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			cardPageEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<CardPageEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cardPageEntity;
	}

	/**
	 * @return 卡包卡详情
	 */
	public static CardPkgDetailEntity getCardPkgDetail(String data) {
		CardPkgDetailEntity cardPkgDetailEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			cardPkgDetailEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<CardPkgDetailEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cardPkgDetailEntity;
	}

	/**
	 * 用户信息
	 */
	public static UserEntity getUserInfo(String data) {
		UserEntity userEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			userEntity = gson.fromJson(data, new TypeToken<UserEntity>() {
			}.getType());
			if (userEntity != null) {
				userEntity.setResultCode(jsonObject.getString("rspCode"));
				userEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return userEntity;
	}

	/**
	 * 订单列表
	 */
	public static OrderPageEntity getOrderList(String data) {
		OrderPageEntity orderPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			orderPageEntity = gson.fromJson(data, OrderPageEntity.class);
			if (orderPageEntity != null) {
				orderPageEntity.setResultCode(jsonObject.getString("rspCode"));
				orderPageEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orderPageEntity;
	}

	/**
	 * 消费记录
	 */
	public static ConsumptionPageEntity getConsumptionList(String data) {
		ConsumptionPageEntity consumptionPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			consumptionPageEntity = gson.fromJson(data, ConsumptionPageEntity.class);
			if (consumptionPageEntity != null) {
				consumptionPageEntity.setResultCode(jsonObject.getString("rspCode"));
				consumptionPageEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return consumptionPageEntity;
	}

	/**
	 * 订单详情
	 */
	public static OrderDetailEntity getOrderDetail(String data) {
		OrderDetailEntity orderDetailEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			orderDetailEntity = gson.fromJson(data, OrderDetailEntity.class);
			if (orderDetailEntity != null) {
				orderDetailEntity.setResultCode(jsonObject.getString("rspCode"));
				orderDetailEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orderDetailEntity;
	}

	/**
	 * 优惠券
	 */
	public static CouponPageEntity getCouponList(String data) {
		CouponPageEntity couponPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			couponPageEntity = gson.fromJson(data, CouponPageEntity.class);
			if (couponPageEntity != null) {
				couponPageEntity.setResultCode(jsonObject.getString("rspCode"));
				couponPageEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return couponPageEntity;
	}

	/**
	 * 分享
	 */
	public static List<ShareEntivity> getShareList(String data) {
		List<ShareEntivity> shareEntivityList = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getJSONObject("val").optString("result");
			shareEntivityList = gson.fromJson(data, new TypeToken<List<ShareEntivity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return shareEntivityList;
	}

	/**
	 * 订单支付
	 */
	public static OrderPayEntity getOrderPay(String data) {
		OrderPayEntity orderPayEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			orderPayEntity = gson.fromJson(data, OrderPayEntity.class);
			if (orderPayEntity != null) {
				orderPayEntity.setResultCode(jsonObject.getString("rspCode"));
				orderPayEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orderPayEntity;
	}

	public static SearchOptionEntity getSearchOptions(String data) {
		SearchOptionEntity searchOptionEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			searchOptionEntity = gson.fromJson(data, new TypeToken<SearchOptionEntity>() {
			}.getType());
			if (searchOptionEntity != null) {
				searchOptionEntity.setResultCode(jsonObject.getString("rspCode"));
				searchOptionEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return searchOptionEntity;
	}

	public static SearchStoreEntity getSearchStore(String data) {
		SearchStoreEntity searchStoreEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			searchStoreEntity = gson.fromJson(data, new TypeToken<SearchStoreEntity>() {
			}.getType());
			if (searchStoreEntity != null) {
				searchStoreEntity.setResultCode(jsonObject.getString("rspCode"));
				searchStoreEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return searchStoreEntity;
	}

	public static StoreDetailsEntity getStoreDetails(String data) {
		StoreDetailsEntity storeDetailsEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			storeDetailsEntity = gson.fromJson(data, new TypeToken<StoreDetailsEntity>() {
			}.getType());
			if (storeDetailsEntity != null) {
				storeDetailsEntity.setResultCode(jsonObject.getString("rspCode"));
				storeDetailsEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return storeDetailsEntity;
	}

	public static List<HotKeyEntity> getHotKeysList(String data) {
		List<HotKeyEntity> hotKeyEntityList = null;
		try {
			JSONObject jsonObject = new JSONObject(data).getJSONObject("val");
			data = jsonObject.getString("result");
			hotKeyEntityList = gson.fromJson(data, new TypeToken<List<HotKeyEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hotKeyEntityList;
	}

	public static CardDetailEntity getCardDetails(String data) {
		CardDetailEntity cardDetailEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			cardDetailEntity = gson.fromJson(data, new TypeToken<CardDetailEntity>() {
			}.getType());
			if (cardDetailEntity != null) {
				cardDetailEntity.setResultCode(jsonObject.getString("rspCode"));
				cardDetailEntity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return cardDetailEntity;
	}

	public static List<CityEntity> getUpdateCityList(Context context, String data) {
		List<CityEntity> cityEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONObject result = jsonObject.optJSONObject("val");
			cityEntities = gson.fromJson(result.getString("result"), new TypeToken<List<CityEntity>>() {
			}.getType());
			CityUtils.setCityAreaVersion(context, result.getString("cityVersion"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cityEntities;
	}

	public static Entity getResponseSatus(String data) {
		Entity entity = new Entity();
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity.setResultCode(jsonObject.getString("rspCode"));
			entity.setResultMsg(jsonObject.getString("rspMsg"));
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return entity;
	}

	public static UpdateVersionEntity getUpdateVersionInfo(String data) {
		UpdateVersionEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getString("val");
			entity = gson.fromJson(data, new TypeToken<UpdateVersionEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "json getCompetitionList" + e.toString());
		}
		return entity;
	}

	public static String getPaypwdSalt(String data) {
		if (data != null) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				return jsonObject.getJSONObject("val").getString("salt");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static long getServerTime(String data) {
		if (data != null) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				return jsonObject.getJSONObject("val").getLong("time");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static List<QuestionItemEntity> parseQuestionItem(String data) {
		List<QuestionItemEntity> entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			entity = gson.fromJson(data, new TypeToken<List<QuestionItemEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static List<UserQuestionEntity> getQuestionResult(String data) {
		List<UserQuestionEntity> entities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			entities = gson.fromJson(data, new TypeToken<List<UserQuestionEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entities;
	}

	public static VerifyResultEntity getVerifyResult(String data) {
		VerifyResultEntity verifyResultEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			verifyResultEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<VerifyResultEntity>() {
			}.getType());
			if (verifyResultEntity != null) {
				verifyResultEntity.setResultCode(jsonObject.getString("rspCode"));//opt默认为0
				verifyResultEntity.setResultMsg(jsonObject.optString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return verifyResultEntity;
	}

	public static DisCountCouponEntity getDiscount(String data) {
		DisCountCouponEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<DisCountCouponEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static String getYilianPayResult(String data) {
		String status = "";
		try {
			status = new JSONObject(data).getString("Status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return status;
	}

	public static MainTopHalfPageEntity getMainHalfTopInfo(String data) {
		MainTopHalfPageEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<MainTopHalfPageEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static AuthenticationEntity getAuthCode(String data) {
		AuthenticationEntity authCodeEntity = null;
		try {
			authCodeEntity = gson.fromJson(new JSONObject(data).optString("val"), new TypeToken<AuthenticationEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return authCodeEntity;
	}

	public static AuthInfoEntity getAuthInfo(String data) {
		AuthInfoEntity authInfoEntity = null;
		try {
			authInfoEntity = gson.fromJson(new JSONObject(data).optString("val"), new TypeToken<AuthInfoEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return authInfoEntity;
	}

	public static List<MessageCenterEntity> getMessageEntity(String data) {
		List<MessageCenterEntity> mce = null;
		try {
			String val = new JSONObject(data).optString("val");
			String news = new JSONObject(val).optString("news");
			mce = gson.fromJson(news, new TypeToken<List<MessageCenterEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mce;
	}

	/**
	 * 解析消息列表
	 */
	public static MyAssetListEntity getMessageList(String data) {
		MyAssetListEntity male = null;
		try {
			String val = new JSONObject(data).optString("val");
			male = gson.fromJson(val, new TypeToken<MyAssetListEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return male;
	}

	public static PayVerifyPwdEntity getPayVerifyPwdEntity(String data) {
		PayVerifyPwdEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<PayVerifyPwdEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			} else {
				entity = new PayVerifyPwdEntity();
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static String getUnreadMessage(String data) {
		if (data != null) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				JSONObject jsonObject1 = jsonObject.getJSONObject("val");
				JSONObject jsonObject2 = jsonObject1.getJSONObject("news");
				return jsonObject2.optString("ifRead");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static SetPayPwdEntity getSetPayPwdEntity(String data) {
		SetPayPwdEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<SetPayPwdEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 解析活动列表
	 */
	public static ActionListEntity getActionListEntity(String data) {
		ActionListEntity ale = null;
		try {
			String val = new JSONObject(data).optString("val");
			ale = gson.fromJson(val, new TypeToken<ActionListEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ale;
	}

	public static ChargeEntity getChargeEntity(String data) {
		ChargeEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<ChargeEntity>() {
			}.getType());
			if (entity != null) {
				entity.setResultCode(jsonObject.getString("rspCode"));
				entity.setResultMsg(jsonObject.getString("rspMsg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * @return 实名认证支持银行列表
	 */
	public static List<SupportBankEntity> getBankSupport(String data) {
		List<SupportBankEntity> bankSupportList = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject(data);
			jsonObject = new JSONObject(jsonObject.optString("val"));
			bankSupportList = gson.fromJson(jsonObject.optString("banks"), new TypeToken<List<SupportBankEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bankSupportList;
	}

	/**
	 * @return 首页推荐电子卡
	 */
	public static List<ECardEntity> getEcardList(String data) {
		List<ECardEntity> eCardEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			eCardEntities = gson.fromJson(jsonObject.optString("val"), new TypeToken<List<ECardEntity>>(){
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return eCardEntities;
	}

	/**
	 * @return 电子卡列表分页
	 */
	public static ECardPageEntity getECardPage(String data) {
		ECardPageEntity eCardPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			eCardPageEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<ECardPageEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return eCardPageEntity;
	}

	/**
	 * @return 电子卡详情
	 */
	public static ECardEntity getECard(String data) {
		ECardEntity eCardEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			eCardEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<ECardEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return eCardEntity;
	}

	/**
	 * @return 包子商城数据
	 */
	public static MarketMainEntity getMarketMainEntity(String data) {
		MarketMainEntity marketMainEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			marketMainEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<MarketMainEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return marketMainEntity;
	}

	/**
	 * 我的电子卡列表
	 */
	public static MyElectCardEntity getMyElectCardList(String data) {
		MyElectCardEntity myElectCardEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			myElectCardEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<MyElectCardEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return myElectCardEntity;
	}

	/**
	 * 解析包子充值
	 */
	public static BaoziChargeEntity getBaoziChargeEntity(String data) {
		BaoziChargeEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<BaoziChargeEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 提取电子卡列表
	 */
	public static MyElectCardPwdEntity getMyElectCardExtractList(String data) {
		MyElectCardPwdEntity myElectCardPwdEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			myElectCardPwdEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<MyElectCardPwdEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return myElectCardPwdEntity;
	}

	/**
	 * 解析我的包子
	 */
	public static MyBaoziEntity getMyBaoziEntity(String data) {
		MyBaoziEntity entity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			entity = gson.fromJson(jsonObject.optString("val"), new TypeToken<MyBaoziEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 包子订单支付
	 */
	public static BaoziPayEntity getBaoziPay(String data) {
		BaoziPayEntity baoziPayEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			baoziPayEntity = gson.fromJson(data, new TypeToken<BaoziPayEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return baoziPayEntity;
	}

	/**
	 * @return 电子卡订单号
	 */
	public static ECardOrderEntity getECardOrder(String data) {
		ECardOrderEntity eCardOrderNo = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			eCardOrderNo = gson.fromJson(data, new TypeToken<ECardOrderEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return eCardOrderNo;
	}

	public static PayCertificateEntity getPayCertificate(String data) {
		PayCertificateEntity payCertificateEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			payCertificateEntity = gson.fromJson(data, new TypeToken<PayCertificateEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return payCertificateEntity;
	}
	/*
	卡兑换获取卡列表
	* */
	public static ExchangeCardPageEntity getExchangeCardPage(String data) {
		ExchangeCardPageEntity exchangeCardPageEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			exchangeCardPageEntity = gson.fromJson(data, new TypeToken<ExchangeCardPageEntity>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return exchangeCardPageEntity;
	}

	/*
	卡兑换获取卡详情（可支持兑换面值）
	* */
	public static ArrayList<ExchangeFacevalueEntity> getExchangeFace(String data) {
		ArrayList<ExchangeFacevalueEntity> exchangeFacevalueEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getJSONObject("val").optString("result");
			exchangeFacevalueEntities = gson.fromJson(data, new TypeToken<ArrayList<ExchangeFacevalueEntity>>() {
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return exchangeFacevalueEntities;
	}
	/**
	 * @return 斗金用户信息
	 */
	public static DJUserEntity getDJUser(String data) {
		DJUserEntity user = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			user = gson.fromJson(jsonObject.optString("val"), new TypeToken<DJUserEntity>(){}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * @return 收款配置信息
	 */
	public static DJPaymentEntity getDJPayment(String data) {
		DJPaymentEntity payment = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			payment = gson.fromJson(jsonObject.optString("val"), new TypeToken<DJPaymentEntity>(){}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return payment;
	}

	/**
	 * @return DJ费率列表
	 */
	public static List<DjpayRateChannelEntity> getDjpayRateList(String data) {
		List<DjpayRateChannelEntity> djpayRateChannelEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.getJSONObject("val").optString("result");
			djpayRateChannelEntities = gson.fromJson(data, new TypeToken<List<DjpayRateChannelEntity>>(){
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return djpayRateChannelEntities;
	}

	/**
	 * @return DJ银行卡列表
	 */
	public static List<DjBankListEntity> getDjpayBankList(String data) {
		List<DjBankListEntity> djBankListEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			djBankListEntities = gson.fromJson(data, new TypeToken<List<DjBankListEntity>>(){
			}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return djBankListEntities;
	}

	/**
	 * @return DJ账单列表
	 */
	public static DjBillPageEntity getDjpayBillList(String data) {
		DjBillPageEntity djBillEntities = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			data = jsonObject.optString("val");
			djBillEntities = gson.fromJson(data, new TypeToken<DjBillPageEntity>(){}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return djBillEntities;
	}

	/**
	 * @return DJ付款码
	 */
	public static DJPayCodeEntity getDjPayCode(String data) {
		DJPayCodeEntity djPayCodeEntity = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			djPayCodeEntity = gson.fromJson(jsonObject.optString("val"), new TypeToken<DJPayCodeEntity>(){}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return djPayCodeEntity;
	}
}