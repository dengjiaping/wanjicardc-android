package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CardDetailEntity;
import com.wjika.client.network.entities.CardMessageEntity;
import com.wjika.client.network.entities.PrivilegeEntity;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.CardRechargeActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.store.adapter.CardLogoViewAdapter;
import com.wjika.client.store.adapter.SupportStoreAdapter;
import com.wjika.client.utils.CommonShareUtil;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * 卡详情
 * Created by Leo_Zhang on 2016/6/14.
 */
public class CardDetailActivity extends ToolBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final String EXTRA_CARD_ID = "extra_card_id";
	public static final String EXTRA_CARD_NAME = "extra_card_name";
	public static final String EXTRA_FROM = "extra_from";
	public static final String EXTRA_IS_MYCARD = "extra_is_mycard";
	public static final String EXTRA_BRANCH_MER_ID = "extraEXTRA_BRANCH_MER_ID_branch_mer_id";
	public static final String EXTRA_CARD_POSITION = "extra_card_position";
	public static final int REQUEST_GET_CARD_DETAILS_CODE = 0x1;
	public static final int REQUEST_AUTH_CODE = 0x2;
	public static final int FROM_STORE_LIST = 2;
	public static final int FROM_ACTION_WEBVIEW = 3;
	public static final int REQUST_CARD_DETAIL_CODE = 4;

	@ViewInject(R.id.store_correlation_shop)
	private ListView storeCorrelationShop;
	@ViewInject(R.id.txt_card_details_name)
	private TextView txtCardDetailsName;
	@ViewInject(R.id.txt_card_details_ad)
	private TextView txtCardDetailAd;
	@ViewInject(R.id.txt_card_details_price)
	private TextView txtCardDetailPrice;
	@ViewInject(R.id.txt_card_details_sale_count)
	private TextView txtCardDetailSaleCount;
	@ViewInject(R.id.layout_privilege)
	private RelativeLayout layoutPrivilege;
	@ViewInject(R.id.txt_privilege_title)
	private TextView txtPrivilegeTitle;
	@ViewInject(R.id.privilege_icon_container)
	private LinearLayout privilegeIconContaier;
	@ViewInject(R.id.card_use_explain)
	private TextView cardUseExplain;
	@ViewInject(R.id.layout_support_store)
	private LinearLayout layoutSupportStore;
	@ViewInject(R.id.support_store_count)
	private TextView supportStoreCount;
	@ViewInject(R.id.store_shop_more)
	private RelativeLayout storeShopMore;
	@ViewInject(R.id.bottom_click_pay)
	private TextView bottomClickPay;
	@ViewInject(R.id.card_details_img)
	private ViewPager cardDetailsImg;
	@ViewInject(R.id.image_point_group)
	private LinearLayout imagePointGroup;
	@ViewInject(R.id.right_button)
	private ImageView rightButton;
	@ViewInject(R.id.bottom_card_price)
	private TextView bottomCardPrice;
	@ViewInject(R.id.loading_img_refresh)
	private ImageView loadingImgRefresh;
	@ViewInject(R.id.scrollview)
	private ScrollView scrollview;
	@ViewInject(R.id.ll_card_action)
	private LinearLayout llCardAction;
	@ViewInject(R.id.layout_use_explain)
	private LinearLayout layoutUseExplain;
	@ViewInject(R.id.support_store_count_img)
	private ImageView supportStoreCountImg;
	@ViewInject(R.id.card_use_line)
	private RelativeLayout cardUseLine;
	@ViewInject(R.id.card_arrow)
	private ImageView cardArrow;

	private boolean isExtend = false;//是否展开。
	private boolean isRunAnim = false;

	private String mCardId;
	private CardDetailEntity cardDetails;
	private SupportStoreAdapter supportStoreAdapter;
	private List<CardMessageEntity> cardMessageEntityList;
	private String cardName;
	private String saleValue;
	private String faceValue;
	private String price;
	private ArrayList<PrivilegeEntity> privilegeEntityList;
	private int viewpagerPosition;
	private String activityId;
	private String limitForSale;
	private String branchId;
	private String cardLogo;
	private String latitude;
	private String longitude;
	private String mCardName;

	// 记录当前选中位置
	private int currentIndex;
	private ImageView[] points;
	private List<View> view;

	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_detail_act);
		ViewInjectUtils.inject(this);
		mCardId = getIntent().getStringExtra(EXTRA_CARD_ID);
		branchId = getIntent().getStringExtra(EXTRA_BRANCH_MER_ID);
		mCardName = getIntent().getStringExtra(EXTRA_CARD_NAME);
		latitude = LocationUtils.getLocationLatitude(this);
		longitude = LocationUtils.getLocationLongitude(this);

		initView();
		cardDetailData();
	}

	private void cardDetailData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("merchantCardId", mCardId);
		params.put("merchantLatitude", latitude);
		params.put("merchantLongitude", longitude);
		requestHttpData(Constants.Urls.URL_GET_BUY_CARD_DETAILS, REQUEST_GET_CARD_DETAILS_CODE, FProtocol.HttpMethod.POST, params);
	}

	private void initView() {
		setLeftTitle(mCardName);
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		bottomClickPay.setOnClickListener(this);
		rightButton.setVisibility(View.VISIBLE);
		bottomCardPrice.setVisibility(View.VISIBLE);
		rightButton.setOnClickListener(this);
		if (loadingImgRefresh != null) {
			loadingImgRefresh.setOnClickListener(this);
		}
		viewpagerPosition = getIntent().getIntExtra(EXTRA_CARD_POSITION, 0);
		layoutPrivilege.setOnClickListener(this);
		cardUseLine.setOnClickListener(this);
		cardUseExplain.getLayoutParams().height = 0;
		cardUseExplain.requestLayout();
		view = new ArrayList<>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_Carddetails");
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		bottomClickPay.setText(this.getString(R.string.card_current_buy));
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_GET_CARD_DETAILS_CODE:
				if (data != null) {
					setLoadingStatus(LoadingStatus.GONE);
					cardDetails = Parsers.getCardDetails(data);
					String merchantName = cardDetails.getMerchantName();
					setLeftTitle(merchantName);
					int mark = 0;
					if (cardDetails != null) {
						cardMessageEntityList = cardDetails.getCardMessageEntityList();
						initLogo();
						initCardTitle(mark);
						initPrivilegeView(mark);
						initSupportStore();
						showButton();
						cardDetailsImg.setCurrentItem(viewpagerPosition);
						storeCorrelationShop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
							@Override
							public void onGlobalLayout() {
								if (flag) {
									scrollview.scrollTo(0, 0);
									flag = false;
								}
							}
						});
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		switch (requestCode) {
			case REQUEST_GET_CARD_DETAILS_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	private void initLogo() {
		LayoutInflater lf = LayoutInflater.from(this);
		if (view != null) {
			view.clear();
		}
		if (cardMessageEntityList != null) {
			for (int i = 0; i < cardMessageEntityList.size(); i++) {
				View view1 = lf.inflate(R.layout.card_detail_img_view, null);
				view.add(view1);
			}
		}
		CardLogoViewAdapter cardLogoViewAdapter = new CardLogoViewAdapter(view, cardMessageEntityList, this);
		cardDetailsImg.setOffscreenPageLimit(2);
		cardDetailsImg.setPageMargin(30);
		cardDetailsImg.setAdapter(cardLogoViewAdapter);

		cardDetailsImg.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				initCardTitle(position);
				initPrivilegeView(position);
				showButton();
				setCurrentDot(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		if (view != null && view.size() != 1) {
			points = new ImageView[view.size()];
			imagePointGroup.removeAllViews();
			for (int i = 0; i < view.size(); i++) {
				points[i] = new ImageView(this);
				points[i].setLayoutParams(new ViewGroup.LayoutParams(22, 22));
				points[i].setId(i);
				points[i].setBackgroundResource(R.drawable.guide_dot_icon);
				points[i].setPadding(0, 0, 0, 0);
				points[i].setEnabled(false);
				//添加图间间隔
				View pointView = new View(this);
				pointView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
				imagePointGroup.addView(points[i]);
				imagePointGroup.addView(pointView);
			}

			currentIndex = 0;
			points[currentIndex].setBackgroundResource(R.drawable.guide_dot_icon_selected);
		} else {
			imagePointGroup.setVisibility(View.INVISIBLE);
		}
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > view.size() - 1 || currentIndex == position) {
			return;
		}

		currentIndex = position;
		// 循环取得小点图片
		for (int i = 0; i < view.size(); i++) {
			if (i == currentIndex) {
				points[i].setBackgroundResource(R.drawable.guide_dot_icon_selected);
			} else {
				points[i].setBackgroundResource(R.drawable.guide_dot_icon);
			}
		}
	}

	private void showButton() {
		String isMyCard = "1";//1是购卡 2是充值
		if ((cardDetails.getIsMyCard() != null && cardDetails.getIsMyCard().equals(isMyCard))) {
			bottomClickPay.setText(this.getString(R.string.card_current_buy));
		} else {
			bottomClickPay.setText(this.getString(R.string.card_current_charge));
		}
	}

	private void initSupportStore() {
		if (cardDetails != null && cardDetails.getBranchNum() > 0) {
			layoutSupportStore.setVisibility(View.VISIBLE);
			supportStoreCount.setText(getString(R.string.support_store_count, String.valueOf(cardDetails.getBranchNum())));
			if (cardDetails.getBranchNum() > 3) {
				List<StoreEntity> storeEntities = new ArrayList<>();
				storeEntities.add(cardDetails.getmSupportStoreList().get(0));
				storeEntities.add(cardDetails.getmSupportStoreList().get(1));
				storeEntities.add(cardDetails.getmSupportStoreList().get(2));
				supportStoreAdapter = new SupportStoreAdapter(this, storeEntities);
				storeShopMore.setOnClickListener(this);
			} else {
				supportStoreCount.setVisibility(View.GONE);
				supportStoreCountImg.setVisibility(View.GONE);
				supportStoreAdapter = new SupportStoreAdapter(this, cardDetails.getmSupportStoreList());
				storeShopMore.setOnClickListener(null);
			}
			supportStoreAdapter.setLocation(latitude, longitude);
			storeCorrelationShop.setAdapter(supportStoreAdapter);
		} else {
			layoutSupportStore.setVisibility(View.GONE);
		}
		storeCorrelationShop.setOnItemClickListener(this);
	}

	private void initPrivilegeView(int position) {
		List<PrivilegeEntity> privilegeEntities = new ArrayList<>();
		if (cardMessageEntityList.get(position).getPrivilegeEntityList() != null && cardMessageEntityList.get(position).getPrivilegeEntityList().size() > 0) {
			for (PrivilegeEntity privilegeEntity : cardMessageEntityList.get(position).getPrivilegeEntityList()) {
				privilegeEntities.add(privilegeEntity);
			}
		}
		if (privilegeEntities.size() > 0) {
			privilegeIconContaier.removeAllViews();
			layoutPrivilege.setVisibility(View.VISIBLE);
			txtPrivilegeTitle.setText(this.getString(R.string.card_privilege_count, String.valueOf(privilegeEntities.size())));
			int defaultPadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_comment_marginright) * 2;
			int width = DeviceUtil.getWidth(this) - 44 - defaultPadding;
			int privilegeSize = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_size);
			int privilegePadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_padding);
			int columsNum = width / (privilegeSize + privilegePadding);
			if (columsNum > privilegeEntities.size()) {
				columsNum = privilegeEntities.size();
			}
			for (int i = 0; i < columsNum; i++) {
				SimpleDraweeView view = new SimpleDraweeView(this);
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(privilegeSize, privilegeSize);
				view.setLayoutParams(imgParams);
				GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
				GenericDraweeHierarchy hierarchy = builder
						.setFadeDuration(300)
						.setPlaceholderImage(this.getResources().getDrawable(R.drawable.def_privilege_img))
						.build();
				view.setHierarchy(hierarchy);
				view.setId(i);
				View blankView = new View(this);
				blankView.setId(i + 1000);
				ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(privilegePadding, privilegePadding);
				blankView.setLayoutParams(layoutParams);
				String url = privilegeEntities.get(i).getImgPath();
				if (url != null && url.indexOf("?") < 1) {
					url = url + "?imageView2/0/w/" + width + "/h" + width;
				}
				if (!TextUtils.isEmpty(url) && !url.contains("?")) {
					ImageUtils.setSmallImg(view,url);
				}
				privilegeIconContaier.addView(view);
				privilegeIconContaier.addView(blankView);
			}
			layoutPrivilege.requestFocus();
		} else {
			layoutPrivilege.setVisibility(View.GONE);
		}
	}

	private void initCardTitle(int position) {
		String totalSale = cardMessageEntityList.get(position).getTotalSale();
		cardLogo = cardMessageEntityList.get(position).getCardLogo();
		cardName = cardMessageEntityList.get(position).getCardName();
		saleValue = cardMessageEntityList.get(position).getSaleValue();
		faceValue = cardMessageEntityList.get(position).getFaceValue();
		price = cardMessageEntityList.get(position).getPrice();
		privilegeEntityList = cardMessageEntityList.get(position).getPrivilegeEntityList();
		activityId = cardMessageEntityList.get(position).getActivityId();
		limitForSale = cardMessageEntityList.get(position).getIsLimitForSale();
		txtCardDetailsName.setText(cardName);
		if (StringUtil.isEmpty(cardMessageEntityList.get(position).getAd())) {
			txtCardDetailAd.setVisibility(View.GONE);
		} else {
			txtCardDetailAd.setText(cardMessageEntityList.get(position).getAd());
		}
		txtCardDetailPrice.setText(String.format(this.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(saleValue)));
		txtCardDetailSaleCount.setText(String.format(this.getString(R.string.store_list_out_of), totalSale));

		String isActivity = "1";//1：有活动，有返回。0：没有活动，有返回，是""。
		if (limitForSale.equals(isActivity)) {
			llCardAction.setVisibility(View.VISIBLE);
			bottomCardPrice.setText(String.format(this.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(price)));
			txtCardDetailPrice.setText(String.format(this.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(price)));
		} else {
			llCardAction.setVisibility(View.GONE);
			bottomCardPrice.setText(String.format(this.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(saleValue)));
			txtCardDetailPrice.setText(String.format(this.getString(R.string.person_order_detail_buy_amount), NumberFormatUtil.formatMoney(saleValue)));
		}

		if (cardMessageEntityList.get(position).getUseExplain() != null) {
			layoutUseExplain.setVisibility(View.VISIBLE);
			CharSequence charSequence = Html.fromHtml(cardMessageEntityList.get(position).getUseExplain());
			cardUseExplain.setText(charSequence);
		} else {
			layoutUseExplain.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.store_shop_more:
				if (cardDetails != null) {
					AnalysisUtil.onEvent(this, "Android_act_allStores");
					Intent moreShopIntent = new Intent(this, CorrelationStoreActivity.class);
					moreShopIntent.putExtra(CorrelationStoreActivity.EXTRA_MERID, cardDetails.getMainMerchantId());
					startActivity(moreShopIntent);
				}
				break;
			case R.id.right_button:
				if (cardDetails == null || cardDetails.getShareInfos() == null) {
					return;
				}
				CommonShareUtil.share(this, cardDetails.getShareInfos().getDesc(),
						cardDetails.getShareInfos().getTitle(), cardLogo,
						cardDetails.getShareInfos().getUrl());
				break;
			case R.id.bottom_click_pay:
				AnalysisUtil.onEvent(this, "Android_act_Buynow");
				if (UserCenter.isLogin(this)) {
					if (UserCenter.isAuthencaiton(this)) {
						payOrBuy();
					} else {
						Intent authencaIntent = new Intent(this, AuthenticationActivity.class);
						authencaIntent.putExtra(CardDetailActivity.EXTRA_FROM, AuthenticationActivity.BUY_CARD_DETAIL);
						startActivityForResult(authencaIntent, REQUEST_AUTH_CODE);
					}
				} else {
					Intent intent = new Intent(this, LoginActivity.class);
					intent.putExtra(EXTRA_FROM, LoginActivity.FROM_CARD_DETAIL_CODE);
					startActivityForResult(intent, REQUST_CARD_DETAIL_CODE);
				}
				break;
			case R.id.loading_img_refresh:
				cardDetailData();
				setLoadingStatus(LoadingStatus.LOADING);
				break;
			case R.id.layout_privilege:
				if (cardDetails != null) {
					Intent helpIntent = new Intent(this, WebViewActivity.class);
					helpIntent.putExtra(WebViewActivity.EXTRA_FROM, WebViewActivity.FROM_PRIVILEGE_EXPLAIN);
					helpIntent.putExtra(WebViewActivity.EXTRA_URL, Constants.Urls.URL_PRIVILEGE_DOMAIN + "?merchantId=" + cardDetails.getMainMerchantId() + "&token=" + UserCenter.getToken(this) + "&privilegeType=merchant");
					helpIntent.putExtra(WebViewActivity.EXTRA_TITLE, this.getString(R.string.privilege_explain));
					startActivity(helpIntent);
				}
				break;
			case R.id.card_use_line:
				animation();
				break;
		}
	}

	private void animation() {
		if (isRunAnim) return;
		ValueAnimator valueAnimator;
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		if (isExtend) {
			//执行收缩动画
			valueAnimator = ValueAnimator.ofInt(height, 0);
		} else {
			//执行展开动画
			valueAnimator = ValueAnimator.ofInt(0, height);
		}
		//设置动画执行的监听器
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				cardUseExplain.getLayoutParams().height = (int) animation.getAnimatedValue();
				cardUseExplain.requestLayout();
			}
		});
		valueAnimator.setDuration(100);
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				isRunAnim = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				isRunAnim = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		valueAnimator.start();
		isExtend = !isExtend;
		ViewPropertyAnimator.animate(cardArrow).rotationBy(180).setDuration(350).start();
	}

	private void payOrBuy() {
		Intent intent = new Intent(this, CardRechargeActivity.class);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_NAME, cardName);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_ID, mCardId);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_FACE_VALUE, faceValue);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_SALE_VALUE, saleValue);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_ACTIVITY_VALUE, price);
		String type = cardDetails.getIsMyCard();
		if (limitForSale.equals("1")) {
			type = CardRechargeActivity.TYPE_BUY_CARD;
		}
		intent.putExtra(CardRechargeActivity.EXTRA_TYPE, type);
		if (type.equals(CardRechargeActivity.TYPE_BUY_CARD)) {
			intent.putExtra(CardRechargeActivity.EXTRA_MER_ID, branchId);
		} else {
			intent.putExtra(CardRechargeActivity.EXTRA_MER_ID, cardDetails.getMainMerchantId());
		}
		intent.putParcelableArrayListExtra(CardRechargeActivity.EXTRA_CARD_PRIVILEGE, privilegeEntityList);
		intent.putExtra(CardRechargeActivity.EXTRA_ACTIVITY_ID, activityId);
		intent.putExtra(CardRechargeActivity.EXTRA_ISLIMITFORSALE, limitForSale);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, StoreDetailActivity.class);
		intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, supportStoreAdapter.getItem(position).getId());
		intent.putExtra("extra_from", StoreDetailActivity.FROM_STORE_DETAILS_ACTIVITY);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_AUTH_CODE:
					payOrBuy();
					break;
				case REQUST_CARD_DETAIL_CODE:
					cardDetailData();
					if (!UserCenter.isAuthencaiton(this)) {
						showAlertDialog(null, getString(R.string.person_auth_dialog_info), getString(R.string.wjk_alert_know), getString(R.string.person_auth_auth_right_now), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								closeDialog();
							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								closeDialog();
								Intent authencaIntent = new Intent(CardDetailActivity.this, AuthenticationActivity.class);
								authencaIntent.putExtra(CardDetailActivity.EXTRA_FROM, AuthenticationActivity.BUY_CARD_DETAIL);
								startActivityForResult(authencaIntent, REQUEST_AUTH_CODE);
							}
						});
					}
					break;
			}
		}
	}
}
