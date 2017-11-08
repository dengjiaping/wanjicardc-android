package com.wjika.client.person.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.db.UserProvider;
import com.wjika.client.db.UserTable;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.controller.MyBaoziActivity;
import com.wjika.client.market.controller.MyElectCardActivity;
import com.wjika.client.message.controller.MessageCenterActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by jacktian on 16/4/26.
 * 个人中心
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {

	private static final int REQUEST_PERSON_INFO_CODE = 100;//个人详细信息
	private static final int REQUEST_PERSON_CODE = 200;
	public static final String USER_INFO = "person_userInfo";
	public static final String ACTION_PERSON_LOGIN = "action_person_login";
	private static final int REQUEST_PERSON_SETTING = 300;//设置中退出登录
	private static final int REQUEST_LOGIN_AUTH = 400;//未登录跳转登录成功后提示实名认证
	private static final int REQUEST_COUPON_USABLENUM = 500;//我的优惠券返回可用优惠券数量
	private static final int REQUEST_PERSON_NEW_MESSAGE = 600;
	private static long loadTime = 0;

	@ViewInject(R.id.person_main_avatar)
	private ImageView personMainAvatar;
	@ViewInject(R.id.person_main_phone)
	private TextView personMainPhone;
	@ViewInject(R.id.person_main_order)
	private TextView personMainOrder;
	@ViewInject(R.id.person_main_consumption)
	private TextView personMainConsumption;
	@ViewInject(R.id.person_main_pay)
	private LinearLayout personMainPay;
	@ViewInject(R.id.person_main_pay_setting)
	private TextView personMainPaySetting;
	@ViewInject(R.id.person_main_coupon_num)
	private TextView personMainCouponNum;
	@ViewInject(R.id.person_main_coupon)
	private LinearLayout personMainCoupon;
	@ViewInject(R.id.person_main_electric)
	private LinearLayout personMainEclctric;
	@ViewInject(R.id.person_main_invite)
	private TextView personMainInvite;
	@ViewInject(R.id.person_main_setting)
	private ImageView personMainSetting;
	@ViewInject(R.id.person_main_message)
	private FrameLayout personMainMessage;
	@ViewInject(R.id.person_message_dot)
	private TextView personMesageDot;
	@ViewInject(R.id.person_main_auth)
	private TextView personAuthInfo;
	@ViewInject(R.id.person_main_pay_setting_dot)
	private TextView personPaySettingDot;
	@ViewInject(R.id.person_main_baozi_num)
	private TextView personMainBaoziNum;
	@ViewInject(R.id.person_main_merchant_num)
	private TextView personMainMerchantNum;
	@ViewInject(R.id.person_main_electric_num)
	private TextView personMainElectricNum;
	@ViewInject(R.id.person_main_baozi_img)
	private ImageView personMainBaoziImg;
	@ViewInject(R.id.person_main_baozi_recharge)
	private TextView personMainBaoziRecharge;


	private UserEntity userInfo;
	private String currentAvatar;
	private Context context;
	private View mView;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.personal_frag, null);
			context = getActivity();
			ViewInjectUtils.inject(this,mView);
			setClickListenerMethord();
			loadTime = 0;
		}
		ViewGroup parent = (ViewGroup)mView.getParent();
		if(parent != null) {
			parent.removeView(mView);
		}
		return mView;
	}

	private void setClickListenerMethord() {
		personMainAvatar.setOnClickListener(this);
		personMainPhone.setOnClickListener(this);
		personMainOrder.setOnClickListener(this);
		personMainConsumption.setOnClickListener(this);
		personMainPay.setOnClickListener(this);
		personMainPaySetting.setOnClickListener(this);
		personMainCoupon.setOnClickListener(this);
		personMainInvite.setOnClickListener(this);
		personMainSetting.setOnClickListener(this);
		personMainMessage.setOnClickListener(this);
		personAuthInfo.setOnClickListener(this);
		personMainEclctric.setOnClickListener(this);
		personMainBaoziImg.setOnClickListener(this);
		personMainBaoziNum.setOnClickListener(this);
		personMainBaoziRecharge.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(getActivity(), "Android_vie_Personal");
		//未读消息
		if (UserCenter.isLogin(getActivity())) {
			String phone = UserCenter.getUserPhone(getActivity());
			if (!StringUtil.isEmpty(phone) && phone.length() == 11) {
				String tempPhone = phone.substring(0, 3) + "****" + phone.substring(7);
				personMainPhone.setText(tempPhone);
			} else {
				ToastUtil.shortShow(getActivity(), getString(R.string.person_phone_illegal));
			}
			if (UserCenter.isAuthencaiton(getActivity())) {
				personAuthInfo.setVisibility(View.GONE);
			} else {
				personAuthInfo.setVisibility(View.VISIBLE);
			}
			if (!UserCenter.isSetSecurity(getActivity())) {
				personPaySettingDot.setVisibility(View.VISIBLE);
			} else {
				personPaySettingDot.setVisibility(View.GONE);
			}
			if (SystemClock.elapsedRealtime() - loadTime > 10000) {
				loadMessageData();
				loadData();
			}
			personMainBaoziRecharge.setEnabled(true);
		} else {
			setNoLoginState();
		}
	}
    /*设置未登录状态*/
	private void setNoLoginState() {
		UserCenter.cleanLoginInfo(this.getActivity());
		personMainPhone.setText(getString(R.string.person_login_now));
		personMainAvatar.setImageResource(R.drawable.default_avatar_bg);
		personAuthInfo.setVisibility(View.GONE);
		personPaySettingDot.setVisibility(View.GONE);
		personMesageDot.setVisibility(View.GONE);
		personMainBaoziRecharge.setEnabled(false);

		String baoziNum = String.format(getString(R.string.person_baozi_num1), "0");
		SpannableStringBuilder amount = new SpannableStringBuilder(baoziNum);
		amount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.person_main_baozi_num)), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		amount.setSpan(new AbsoluteSizeSpan(DeviceUtil.dp_to_px(getActivity(), 24)), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		personMainBaoziNum.setText(amount);

		personMainCouponNum.setText("0");
		personMainMerchantNum.setText("0");
		personMainElectricNum.setText("0");
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		setNoLoginState();
	}

	private void loadMessageData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(getActivity().getApplicationContext()));
		requestHttpData(Constants.Urls.URL_PERSON_UNREAD_MESSAGE, REQUEST_PERSON_NEW_MESSAGE, FProtocol.HttpMethod.POST, params);
	}

	private void loadData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(getActivity().getApplicationContext()));
		requestHttpData(Constants.Urls.URL_GET_USER_INFO, REQUEST_PERSON_INFO_CODE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		loadTime = SystemClock.elapsedRealtime();
		switch (requestCode) {
			case REQUEST_PERSON_INFO_CODE:
				Entity entity = Parsers.getResponseSatus(data);
				switch (entity.getResultCode()) {
					case RESPONSE_NO_LOGIN_CODE:
						setNoLoginState();
						break;
					case RESPONSE_SUCCESS_CODE:
						userInfo = Parsers.getUserInfo(data);
						if (userInfo != null) {
							String phone = userInfo.getPhone();
							if (!StringUtil.isEmpty(phone) && phone.length() == 11) {
								String tempPhone = phone.substring(0, 3) + "****" + phone.substring(7);
								personMainPhone.setText(tempPhone);
							} else {
								ToastUtil.shortShow(getActivity(), getString(R.string.person_phone_illegal));
							}
							String url = userInfo.getHeadImg();
							if (!TextUtils.isEmpty(url) && !url.contains("?")) {
								ImageUtils.setSmallImg(personMainAvatar,url);
							}
							if (2 == userInfo.getAuthentication()) {
								personAuthInfo.setVisibility(View.GONE);
								UserCenter.setAuthenticatiton(getActivity(), true);
							} else {
								personAuthInfo.setVisibility(View.VISIBLE);
								UserCenter.setAuthenticatiton(getActivity(), false);
							}
							personMainCouponNum.setText(String.valueOf(userInfo.getCouponNum()));//优惠券数
							String baoziNum = NumberFormatUtil.formatBun(userInfo.getWalletCount());
							String baozi = String.format(getString(R.string.person_baozi_num1), baoziNum);
							SpannableStringBuilder amount = new SpannableStringBuilder(baozi);
							amount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.person_main_baozi_num)), 0, baoziNum.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
							amount.setSpan(new AbsoluteSizeSpan(DeviceUtil.dp_to_px(getActivity(), 24)), 0, baoziNum.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
							personMainBaoziNum.setText(amount);//包子数
							personMainMerchantNum.setText(String.valueOf(userInfo.getCardCount()));//商家卡数
							personMainElectricNum.setText(String.valueOf(userInfo.getThirdCardCount()));//电子卡数
							UserCenter.setUserPhone(getActivity(), userInfo.getPhone());
							UserCenter.setSecurity(getActivity(), userInfo.isSetSecurity());
							UserCenter.setAppealStatus(getActivity(), userInfo.getAppealStatus());
							UserCenter.setUserRealName(getActivity(), userInfo.getUserRealName());
							saveUserInfo();
						}
						break;
					default:
						break;
				}
				break;
			case REQUEST_PERSON_NEW_MESSAGE:
				//未读消息是否显示
				String isHaveMessage = Parsers.getUnreadMessage(data);
				if ("1".equals(isHaveMessage)) {//0没有1有
					personMesageDot.setVisibility(View.VISIBLE);
				} else {
					personMesageDot.setVisibility(View.GONE);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (REQUEST_PERSON_INFO_CODE == requestCode) {
			closeProgressDialog();
		}
	}

	private void saveUserInfo() {
		//存储用户信息
		ContentResolver contentResolver = getActivity().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(UserTable.COLUMN_USER_NAME, userInfo.getName());
		values.put(UserTable.COLUMN_USER_PHONE, userInfo.getPhone());
		values.put(UserTable.COLUMN_USER_TOKEN, UserCenter.getToken(getActivity()));
		values.put(UserTable.COLUMN_USER_GENDER, userInfo.isGender());
		values.put(UserTable.COLUMN_USER_ADDRESS, userInfo.getAddress());
		values.put(UserTable.COLUMN_USER_IDNO, userInfo.getIdNo());
		values.put(UserTable.COLUMN_USER_BIRTH_DAY, userInfo.getBirthDay());
		values.put(UserTable.COLUMN_USER_HEAD_IMG, userInfo.getHeadImg());
		values.put(UserTable.COLUMN_USER_AUTHENTICATION, userInfo.getAuthentication());
		values.put(UserTable.COLUMN_USER_SET_SECURITY, userInfo.isSetSecurity());

		Cursor cursor = contentResolver.query(Uri.parse(UserProvider.CONTENT_ID_URI + userInfo.getId()),
				new String[]{UserTable.COLUMN_USER_ID, UserTable.COLUMN_USER_PHONE}, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				//更新
				contentResolver.update(Uri.parse(UserProvider.CONTENT_ID_URI + userInfo.getId()), values, null, null);
			} else {
				//新增
				values.put(UserTable.COLUMN_USER_ID, userInfo.getId());
				contentResolver.insert(UserProvider.CONTENT_URI, values);
			}
			cursor.close();
		}
	}

	@Override
	public void onClick(View v) {
		if (UserCenter.isLogin(getActivity())) {
			switch (v.getId()) {
				case R.id.person_main_phone:
				case R.id.person_main_avatar://个人信息
					AnalysisUtil.onEvent(getActivity(), "Android_act_Information");
					if (null != userInfo) {
						Intent infoIntent = new Intent(context, PersonInfoActivity.class);
						infoIntent.putExtra(USER_INFO, userInfo);
						startActivityForResult(infoIntent, REQUEST_PERSON_CODE);
					} else {
						ToastUtil.shortShow(context,getString(R.string.person_info_null));
					}
					break;
				case R.id.person_main_order://我的订单
					AnalysisUtil.onEvent(getActivity(), "Android_act_Order");
					Intent orderIntent = new Intent(context, OrderListActivity.class);
					startActivity(orderIntent);
					break;
				case R.id.person_main_consumption://消费记录
					AnalysisUtil.onEvent(getActivity(), "Android_act_Consumption");
					Intent consumption = new Intent(context, ConsumptionHistoryActivity.class);
					startActivity(consumption);
					break;
				case R.id.person_main_pay://卡包充值
					AnalysisUtil.onEvent(getActivity(), "Android_act_CardRecharge");
					Intent cardIntent = new Intent(context, CardPayActivity.class);
					startActivity(cardIntent);
					break;
				case R.id.person_main_pay_setting://安全设置
					AnalysisUtil.onEvent(getActivity(), "Android_act_Securitysetting");
					if (null != userInfo) {
						Intent payIntent = new Intent(context, PaySettingActivity.class);
						payIntent.putExtra(USER_INFO, userInfo);
						startActivityForResult(payIntent, REQUEST_PERSON_CODE);
					} else {
						ToastUtil.shortShow(context,getString(R.string.person_info_null));
					}
					break;
				case R.id.person_main_coupon://我的优惠券
					AnalysisUtil.onEvent(getActivity(), "Android_act_Coupon");
					startActivityForResult(new Intent(context, CouponActivity.class),REQUEST_COUPON_USABLENUM);
					break;
				case R.id.person_main_invite://邀请好友
					Intent inviteIntent = new Intent(context, InviteFriendActivity.class);
					startActivity(inviteIntent);
					break;
				case R.id.person_main_setting://设置
					AnalysisUtil.onEvent(getActivity(), "Android_act_setting");
					Intent settingIntent = new Intent(context, SettingActivity.class);
					startActivityForResult(settingIntent, REQUEST_PERSON_SETTING);
					break;
				case R.id.person_main_auth://实名认证
					Intent authIntent = new Intent(getActivity(), AuthenticationActivity.class);
					authIntent.putExtra("from", AuthenticationActivity.FROM_PERSON);
					startActivity(authIntent);
					break;
				case R.id.person_main_message://消息中心
					startActivity(new Intent(getActivity(), MessageCenterActivity.class));
					break;
				case R.id.person_main_electric://电子卡
					AnalysisUtil.onEvent(getContext(), "Android_act_MyassetEcard");
					startActivity(new Intent(getActivity(), MyElectCardActivity.class));
					break;
				case R.id.person_main_baozi_num:
				case R.id.person_main_baozi_img://我的包子
					AnalysisUtil.onEvent(getContext(), "Android_act_MyassetBun");
					startActivity(new Intent(getActivity(), MyBaoziActivity.class));
					break;
				case R.id.person_main_baozi_recharge://包子充值
					AnalysisUtil.onEvent(getContext(), "Android_act_MyassetRecharge");
					if (null != userInfo) {
						if (userInfo.isIfRecharge()) {
							startActivity(new Intent(getActivity(), BaoziRechargeActivity.class));
						} else {
							((MainActivity)getActivity()).showLocationDialog(null, getString(R.string.person_baozipay_toomany),
									getString(R.string.button_ok), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									((MainActivity) getActivity()).closeLocationDialog();
								}
							});
						}
					}
					break;
				default:
					break;
			}
		} else {
			switch (v.getId()) {
				case R.id.person_main_setting://设置
					AnalysisUtil.onEvent(getActivity(), "Android_act_setting");
					Intent settingIntent = new Intent(context, SettingActivity.class);
					startActivity(settingIntent);
					break;
				case R.id.person_main_message://消息中心
					Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
					loginIntent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_MESSAGE_CENTER);
					startActivity(loginIntent);
					break;
				default:
					Intent personIntent = new Intent(getActivity(), LoginActivity.class);
					personIntent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_PERSON_CENTER);
					startActivityForResult(personIntent, REQUEST_LOGIN_AUTH);
					break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Activity.RESULT_OK == resultCode) {
			switch (requestCode) {
				case REQUEST_PERSON_CODE:
					userInfo = data.getParcelableExtra(USER_INFO);
					if (userInfo != null) {
						if (!StringUtil.isEmpty(currentAvatar) && !currentAvatar.equals(userInfo.getHeadImg())) {
							String url = currentAvatar = userInfo.getHeadImg();
							if (url != null && url.indexOf("?") < 1) {
								int width = personMainAvatar.getWidth();
								int height = personMainAvatar.getHeight();
								url = url + "?imageView2/0/w/" + width + "/h/" + height;
							}
							if (!TextUtils.isEmpty(url) && !url.contains("?")) {
								ImageUtils.setSmallImg(personMainAvatar,url);
							}
						}
						String tempPhone = userInfo.getPhone().substring(0, 3) + "****" + userInfo.getPhone().substring(7);
						personMainPhone.setText(tempPhone);
						if (UserCenter.isSetSecurity(context)) {
							userInfo.setSetSecurity(true);
						} else {
							userInfo.setSetSecurity(false);
							personMainPaySetting.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.red_circle, 0);
						}
					}
					break;
				case REQUEST_PERSON_SETTING:
					setNoLoginState();
					break;
				case REQUEST_LOGIN_AUTH:
					if (!UserCenter.isAuthencaiton(getActivity())) {
						((MainActivity) getActivity()).showLocationDialog(null, getString(R.string.person_auth_dialog_info), getString(R.string.wjk_alert_know), getString(R.string.person_auth_auth_right_now), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								((MainActivity) getActivity()).closeLocationDialog();
							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								((MainActivity) getActivity()).closeLocationDialog();
								startActivity(new Intent(getActivity(), AuthenticationActivity.class));
							}
						});
					}
					break;
				case REQUEST_COUPON_USABLENUM:
					personMainCouponNum.setText(String.valueOf(data.getIntExtra(CouponActivity.INTETNT_COUPON_USABLENUM,0)));
					break;
				default:
					break;
			}
		}
	}
}
