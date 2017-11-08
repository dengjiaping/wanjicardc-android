package com.wjika.client.main.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.common.network.FProtocol;
import com.common.network.HttpUtil;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.NetWorkUtil;
import com.common.utils.ToastUtil;
import com.common.view.GridViewForInner;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshHeadView;
import com.common.widget.RefreshRecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.cardpackage.controller.CardPackageActivity;
import com.wjika.client.djpay.controller.PaymentActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.exchange.controller.AllCardActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.adapter.GridViewBrandsListAdapter;
import com.wjika.client.market.adapter.ECardAdapter;
import com.wjika.client.market.controller.ECardActivity;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.market.controller.MyBaoziActivity;
import com.wjika.client.message.controller.MessageCenterActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.MainActionsEntity;
import com.wjika.client.network.entities.MainBrandsEntity;
import com.wjika.client.network.entities.MainCityActivitiesEntity;
import com.wjika.client.network.entities.MainTopHalfPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.PayVerifyPWDActivity;
import com.wjika.client.store.controller.CardDetailActivity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.wjika.client.main.controller.MainActivity.REQUEST_ACT_PAYPWD_CODE;

/**
 * Created by jacktian on 16/4/26.
 * 首页
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

	private static final int REQUEST_HOME_TOPHALF_CODE = 0x1;
	private static final int REQUEST_HOME_BOTTOM_CODE = 0x2;
	private static final int REQUEST_PERSON_NEW_MESSAGE = 600;
	private static final int REQUEST_ACT_LOGIN_ACTIVITY = 0x3;
	private static final int REQUEST_ACT_LOGIN_CITY = 0x4;

	@ViewInject(R.id.main_custom_toolbar)
	private RelativeLayout mainCustomToolbar;//首页toolbar布局
	@ViewInject(R.id.tv_home_title)
	private View tvHomeTitle;
	@ViewInject(R.id.main_iv_message_alert)
	private ImageView mainIvMessageAlert;
	@ViewInject(R.id.home_message_dot)
	private View homeMessageDot;
	@ViewInject(R.id.iv_home_cardpkg)
	private ImageView ivHomeCardpkg;
	@ViewInject(R.id.iv_home_bum)
	private ImageView ivHomeBum;
	@ViewInject(R.id.main_ecard_recommend)
	private RefreshRecyclerView mainEcardRecommend;

	private ConvenientBanner convenientBanner;//轮播
	public View mView;
	//热门品牌
	private View mainActionLayout;
	private GridViewForInner gvMainBrand;
	//活动
	private ImageView mainAction1;
	private ImageView mainAction2;

	private List<MainCityActivitiesEntity> banners;
	private List<MainBrandsEntity> mainBrandsLists;
	private MainCityActivitiesEntity mainCityActivitiesEntity;
	private MainActionsEntity mainActionsEntity;
	private boolean isTopNoData = false;
	private boolean isBottomNoData = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.homepage_frag, null);
			ViewInjectUtils.inject(this, mView);
			initLoadingView(this, mView);
			setLoadingStatus(LoadingStatus.LOADING);
			initView(savedInstanceState);
			initFreshListView();
			loadTopHalfData();
			loadRecommendData();
		}
		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null) {
			parent.removeView(mView);
		}
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(getActivity(), "Android_vie_HomeView");
		loadMessageData();
	}

	private void initView(Bundle savedInstanceState) {
		View mainTopHalfBannerFirst = this.getLayoutInflater(savedInstanceState).inflate(R.layout.home_banner, null);
		View mainTopHalfSecond = this.getLayoutInflater(savedInstanceState).inflate(R.layout.home_top_half_second, null);
		//初始化品牌
		mainActionLayout = mainTopHalfSecond.findViewById(R.id.main_action_layout);
		gvMainBrand = (GridViewForInner) mainTopHalfSecond.findViewById(R.id.gv_main_brand);
		gvMainBrand.setOnItemClickListener(this);
		//初始化两个广告位
		initMainActions(mainTopHalfSecond);

		mainIvMessageAlert.setOnClickListener(this);
		ivHomeCardpkg.setOnClickListener(this);
		ivHomeBum.setOnClickListener(this);
		mainTopHalfSecond.findViewById(R.id.tv_main_brand_more).setOnClickListener(this);

		convenientBanner = (ConvenientBanner) mainTopHalfBannerFirst.findViewById(R.id.convenientBanner);
		//刷新控件 添加头
		mainEcardRecommend.addHeaderView(mainTopHalfBannerFirst);
		mainEcardRecommend.addHeaderView(mainTopHalfSecond);
		mainEcardRecommend.setMode(RefreshRecyclerView.Mode.PULL_FROM_START);
		mainEcardRecommend.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		mainEcardRecommend.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				if (parent.getLayoutManager() instanceof GridLayoutManager) {
					GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
					int spanCount = gridLayoutManager.getSpanCount();//设置的列数
					int headerSize = mainEcardRecommend.getHeaderSize();//头部数量
					int pos = parent.getChildLayoutPosition(view) - headerSize;//减去头部后的下标位置

					if (pos < 0) {//头部
						return;
					}

					//item左右偏移量(左右间距)，仅对2列有效
					if (pos % 2 == 0) {
						outRect.right = CommonTools.dp2px(getActivity(), 6);
						outRect.left = CommonTools.dp2px(getActivity(), 12);
					} else {
						outRect.left = CommonTools.dp2px(getActivity(), 6);
						outRect.right = CommonTools.dp2px(getActivity(), 12);
					}

					//item上下偏移量(上下间距)
					if (pos >= (spanCount + headerSize - 1)) {
						//第二行开始的上偏移量
						outRect.top = CommonTools.dp2px(getActivity(), 16);
					} else {
						//头部和内容第一行上偏移量
						outRect.top = CommonTools.dp2px(getActivity(), 12);
					}
				}
			}
		});
		mainEcardRecommend.setOnRefreshAndLoadMoreListener(new RefreshRecyclerView.OnRefreshAndLoadMoreListener() {
			@Override
			public void onRefresh() {
				//首页上半部分
				loadTopHalfData();
				//首页下半部分
				loadRecommendData();
			}

			@Override
			public void onLoadMore() {
			}
		});
		mainEcardRecommend.setOnRefreshStateChangedListener(new RefreshRecyclerView.OnRefreshStateChangedListener() {
			@Override
			public void onRefreshStateChanged(int state) {
				if (RefreshHeadView.STATE_NORMAL == state) {
					mainCustomToolbar.setVisibility(View.VISIBLE);
				} else {
					mainCustomToolbar.setVisibility(View.INVISIBLE);
				}
			}
		});
		mainEcardRecommend.addOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
					mainCustomToolbar.getBackground().mutate().setAlpha(0);
					tvHomeTitle.setAlpha(0);

					GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
					int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
					int custombarHeight = mainCustomToolbar.getHeight();
					if (firstVisibleItem == 1) {
						View view1 = recyclerView.getChildAt(0);
						if (view1 != null) {
							int top = -view1.getTop();
							int headerHeight = view1.getHeight();
							if (top <= headerHeight - custombarHeight && top >= 0) {
								float f = (float) top / (float) (headerHeight - custombarHeight);
								mainCustomToolbar.getBackground().mutate().setAlpha((int) (f * 255));
								if (f > 0.2f) {
									tvHomeTitle.setAlpha(f);
								} else {
									tvHomeTitle.setAlpha(0);
								}
								mainCustomToolbar.invalidate();
							} else if (top > 0 && headerHeight - custombarHeight > 0) {
								//处理滑动到第一个HeadView高度小于Toolbar时的透明问题
								mainCustomToolbar.getBackground().mutate().setAlpha(255);
								tvHomeTitle.setAlpha(1);
							}
						}
					} else if (firstVisibleItem > 1) {
						mainCustomToolbar.getBackground().mutate().setAlpha(255);
						tvHomeTitle.setAlpha(1);
					}
				}
			}
		});
		mainEcardRecommend.setAdapter(new ECardAdapter(new ArrayList<ECardEntity>(), this));
	}

	private void initFreshListView() {
		mainCustomToolbar.setVisibility(View.VISIBLE);
		mainCustomToolbar.getBackground().mutate().setAlpha(0);
		tvHomeTitle.setAlpha(0);
	}

	private void initMainActions(View v) {
		if (v != null) {
			mainAction1 = (ImageView) v.findViewById(R.id.main_action_1);
			mainAction2 = (ImageView) v.findViewById(R.id.main_action_2);
			mainAction1.setOnClickListener(this);
			mainAction2.setOnClickListener(this);
		}
	}

	private void loadTopHalfData() {
		IdentityHashMap<String, String> homeTopHalfParames = new IdentityHashMap<>();
		homeTopHalfParames.put("areaId", "1");
		homeTopHalfParames.put("token", "");
		if (!NetWorkUtil.isConnect(this.getContext())) {
			requestHttpData(Constants.Urls.URL_POST_HOME_TOP_HALF, REQUEST_HOME_TOPHALF_CODE, FProtocol.NetDataProtocol.DataMode.DATA_FROM_CACHE, FProtocol.HttpMethod.POST, homeTopHalfParames);
		} else {
			requestHttpData(Constants.Urls.URL_POST_HOME_TOP_HALF, REQUEST_HOME_TOPHALF_CODE, FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET, FProtocol.HttpMethod.POST, homeTopHalfParames);
		}
	}

	private void loadRecommendData() {
		IdentityHashMap<String, String> homeBottomParames = new IdentityHashMap<>();
		homeBottomParames.put("token", "");
		if (!NetWorkUtil.isConnect(this.getContext())) {
			requestHttpData(Constants.Urls.URL_POST_HOME_BOTTOM, REQUEST_HOME_BOTTOM_CODE, FProtocol.NetDataProtocol.DataMode.DATA_FROM_CACHE, FProtocol.HttpMethod.POST, homeBottomParames);
		} else {
			requestHttpData(Constants.Urls.URL_POST_HOME_BOTTOM, REQUEST_HOME_BOTTOM_CODE, FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET, FProtocol.HttpMethod.POST, homeBottomParames);
		}
	}

	private void loadMessageData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(getActivity().getApplicationContext()));
		requestHttpData(Constants.Urls.URL_PERSON_UNREAD_MESSAGE, REQUEST_PERSON_NEW_MESSAGE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_main_brand_more:
				AnalysisUtil.onEvent(getActivity(), "Android_act_HotBrand");
				startActivity(new Intent(getActivity(), MoreHotBrandsListActivity.class));
				break;
			case R.id.main_action_1:
			case R.id.main_action_2:
				AnalysisUtil.onEvent(getActivity(), "Android_act_Special");
				try {
					mainActionsEntity = (MainActionsEntity) v.getTag();
					if (mainActionsEntity.isNeedLogin() && !UserCenter.isLogin(getActivity())) {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						intent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_MAIN_ACTIVITY);
						startActivityForResult(intent, REQUEST_ACT_LOGIN_ACTIVITY);
					} else {
						gotoActivity(REQUEST_ACT_LOGIN_ACTIVITY);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.ecard_item_layout:
				ECardEntity eCardEntity = (ECardEntity) v.getTag();
				if (eCardEntity != null) {
					Intent intent = new Intent(getActivity(), ECardDetailActivity.class);
					intent.putExtra(ECardDetailActivity.EXTRA_TITLE, eCardEntity.getName());
					intent.putExtra(ECardDetailActivity.EXTRA_ECARD_ID, eCardEntity.getId());
					startActivity(intent);
				}
				break;
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadTopHalfData();
				loadRecommendData();
				break;
			case R.id.main_iv_message_alert:
				if (UserCenter.isLogin(getActivity())) {
					Intent intent = new Intent(this.getActivity(), MessageCenterActivity.class);
					startActivity(intent);
				} else {
					Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
					loginIntent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_MESSAGE_CENTER);
					startActivity(loginIntent);
				}
				break;
			case R.id.iv_home_cardpkg:
				if (UserCenter.isLogin(getActivity())) {
					if (UserCenter.issetNopwd(getActivity())) {
						startActivity(new Intent(getActivity(), CardPackageActivity.class));
					} else {
						Intent intent = new Intent(getActivity(), PayVerifyPWDActivity.class);
						intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.PAYCONSUMES);
						startActivityForResult(intent, REQUEST_ACT_PAYPWD_CODE);
					}
				} else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
				break;
			case R.id.iv_home_bum:
				if (UserCenter.isLogin(getActivity())) {
					startActivity(new Intent(getActivity(), MyBaoziActivity.class));
				} else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
				break;
		}
	}

	/**
	 * @param requestCode 轮播或活动版块跳转处理
	 */
	private void gotoActivity(int requestCode) {
		if (REQUEST_ACT_LOGIN_ACTIVITY == requestCode) {
			if (mainActionsEntity != null) {
				gotoActivity(mainActionsEntity.getMainActionType(), mainActionsEntity.isNeedLogin(), mainActionsEntity.getMainActionContent(),
						mainActionsEntity.getActivityName(), mainActionsEntity.getMainActionBrandName(), mainActionsEntity.getCardMerchantId(),
						mainActionsEntity.getActionMerchantAccountId(), mainActionsEntity.getMainActionMerchantName(), mainActionsEntity.getBrandId());
			}
		} else if (REQUEST_ACT_LOGIN_CITY == requestCode) {
			if (mainCityActivitiesEntity != null) {
				gotoActivity(mainCityActivitiesEntity.getCityActivitType(), mainCityActivitiesEntity.isNeedLogin(), mainCityActivitiesEntity.getCityActivitiesContent(),
						mainCityActivitiesEntity.getCityActivitiesCategory(), mainCityActivitiesEntity.getCityActionbrandName(), mainCityActivitiesEntity.getCardMerchantId(),
						mainCityActivitiesEntity.getCityActionMerchantAccountId(), mainCityActivitiesEntity.getMainCityActiveMerchantName(), mainCityActivitiesEntity.getBrandId());
			}
		}
	}

	private void gotoActivity(String type, boolean isNeedLogin, String content, String name, String brandName, String merId, String accountId, String merName, String brandId) {
		Intent intent;
		switch (type) {
			case "1":
				intent = new Intent(getActivity(), WebViewActivity.class);
				if (isNeedLogin) {
					content += UserCenter.getToken(getActivity()) + "&sysVersion=" + HttpUtil.Config.INTERFACE_VERSION;
				}
				intent.putExtra(WebViewActivity.EXTRA_URL, content);
				intent.putExtra(WebViewActivity.EXTRA_TITLE, name);
				startActivity(intent);
				break;
			case "2"://品牌列表
				intent = new Intent(getActivity(), OneBrandListActivity.class);
				intent.putExtra(OneBrandListActivity.BRAND_NAME, brandName);
				intent.putExtra(OneBrandListActivity.MERCHANT_ACCOUNT_ID, accountId);
				startActivity(intent);
				break;
			case "3"://卡详情
				intent = new Intent(getActivity(), CardDetailActivity.class);
				intent.putExtra(CardDetailActivity.EXTRA_CARD_ID, content);
				intent.putExtra(CardDetailActivity.EXTRA_BRANCH_MER_ID, merId);
				intent.putExtra(CardDetailActivity.EXTRA_CARD_NAME, merName);
				startActivity(intent);
				break;
			case "4"://电子卡列表
				intent = new Intent(getActivity(), ECardActivity.class);
				intent.putExtra("brandId", brandId);
				startActivity(intent);
				break;
			case "5"://斗金
				DJUserCenter.setDJUrl(getActivity(), content);
//				DJUserCenter.setDJUrl(getActivity(), "http://59.110.26.25:8080");
				startActivity(new Intent(getActivity(), PaymentActivity.class));
				break;
			case "6"://卡兑换
				startActivity(new Intent(getActivity(), AllCardActivity.class));
				break;
		}
	}

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		if (REQUEST_HOME_TOPHALF_CODE == requestCode || REQUEST_HOME_BOTTOM_CODE == requestCode) {
			mainEcardRecommend.setReFreshComplete();
		}
		Entity entity = Parsers.getResponseSatus(data);
		if (entity != null) {
			switch (requestCode) {
				case REQUEST_HOME_TOPHALF_CODE: {
					MainTopHalfPageEntity mainTopHalfPageEntity = Parsers.getMainHalfTopInfo(data);
					if (data != null && mainTopHalfPageEntity != null) {
						isTopNoData = false;
						setLoadingStatus(LoadingStatus.GONE);
						List<MainActionsEntity> mainActionLists = mainTopHalfPageEntity.getActivitiesList();
						mainBrandsLists = mainTopHalfPageEntity.getBrandsList();
						banners = mainTopHalfPageEntity.getCityActivityList();
						if (banners == null) {
							banners = new ArrayList<>();
						}
						//初始化首页banner
						if (banners.size() == 0) {
							MainCityActivitiesEntity me = new MainCityActivitiesEntity();
							banners.add(me);
						}
						initBannerData();
						//初始化首页活动
						initMainActionData(mainActionLists);
						//初始化首页品牌
						if (mainBrandsLists != null && mainBrandsLists.size() > 0) {
							if (mainBrandsLists.size() > 4) {
								List<MainBrandsEntity> brandsEntities = new ArrayList<>();
								for (int i = 0; i < 4; i++) {
									brandsEntities.add(mainBrandsLists.get(i));
								}
								mainBrandsLists = brandsEntities;
							}
							GridViewBrandsListAdapter brandsListAdapter = new GridViewBrandsListAdapter(getActivity(), mainBrandsLists, false);
							gvMainBrand.setAdapter(brandsListAdapter);
						}
					} else {
						isTopNoData = true;
					}
					break;
				}
				case REQUEST_HOME_BOTTOM_CODE: {
					setLoadingStatus(LoadingStatus.GONE);
					if (data != null) {
						List<ECardEntity> eCardEntities = Parsers.getEcardList(data);
						if (eCardEntities != null && !eCardEntities.isEmpty()) {
							isBottomNoData = false;
							setLoadingStatus(LoadingStatus.GONE);
							ECardAdapter eCardAdapter = new ECardAdapter(eCardEntities, this);
							mainEcardRecommend.setAdapter(eCardAdapter);
						} else {
							isBottomNoData = true;
						}
					} else {
						isBottomNoData = true;
					}
				}
				break;
				case REQUEST_PERSON_NEW_MESSAGE:
					String HaveMessage = Parsers.getUnreadMessage(data);
					if ("1".equals(HaveMessage)) {
						homeMessageDot.setVisibility(View.VISIBLE);
					} else {
						homeMessageDot.setVisibility(View.GONE);
					}
					break;
			}
			if (isTopNoData && isBottomNoData) {
				setLoadingStatus(LoadingStatus.EMPTY);
				initFreshListView();
			}
		} else {
			super.success(requestCode, data);
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		if (REQUEST_HOME_TOPHALF_CODE == requestCode || REQUEST_HOME_BOTTOM_CODE == requestCode) {
			mainEcardRecommend.setReFreshComplete();
		}
		switch (requestCode) {
			case REQUEST_HOME_TOPHALF_CODE:
				isTopNoData = true;
				break;
			case REQUEST_HOME_BOTTOM_CODE:
				isBottomNoData = true;
				break;
		}
		if (isBottomNoData && isTopNoData) {
			ToastUtil.shortShow(getContext(), errorMessage);
			setLoadingStatus(LoadingStatus.RETRY);
			initFreshListView();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			if (REQUEST_ACT_PAYPWD_CODE == requestCode) {
				startActivity(new Intent(getActivity(), CardPackageActivity.class));
			} else {
				gotoActivity(requestCode);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AnalysisUtil.onEvent(getActivity(), "Android_act_Brand");
		MainBrandsEntity mainBrandsEntity = mainBrandsLists.get(position);
		if (MainBrandsEntity.TYPE_OF_STORE == mainBrandsEntity.getType()) {//商家
			Intent intent = new Intent(getActivity(), OneBrandListActivity.class);
			intent.putExtra(OneBrandListActivity.MERCHANT_ACCOUNT_ID, mainBrandsEntity.getMerchantAccountId());
			intent.putExtra(OneBrandListActivity.BRAND_NAME, mainBrandsEntity.getBrandName());
			startActivity(intent);
		} else if (MainBrandsEntity.TYPE_OF_ECARD == mainBrandsEntity.getType()) {//电子卡
			Intent intent = new Intent(getActivity(), ECardActivity.class);
			intent.putExtra("brandId", mainBrandsEntity.getBrandId());
			startActivity(intent);
		}
	}

	private void initMainActionData(List<MainActionsEntity> mainactionLists) {
		if (mainactionLists == null || mainactionLists.size() <= 0) {
			mainActionLayout.setVisibility(View.GONE);
			return;
		}
		if (mainactionLists.size() == 1) {
			String url1 = mainactionLists.get(0).getActivityImg();
			if (url1 != null) {
				mainAction1.setTag(mainactionLists.get(0));
				ImageUtils.setSmallImg(mainAction1, url1);
			}
			mainAction2.setVisibility(View.INVISIBLE);
		} else {
			String url1 = mainactionLists.get(0).getActivityImg();
			if (url1 != null) {
				mainAction1.setTag(mainactionLists.get(0));
				ImageUtils.setSmallImg(mainAction1, url1);
			}
			String url2 = mainactionLists.get(1).getActivityImg();
			if (url2 != null) {
				mainAction2.setTag(mainactionLists.get(1));
				ImageUtils.setSmallImg(mainAction2, url2);
			}
		}
	}

	private void initBannerData() {
		convenientBanner.setPages(
				new CBViewHolderCreator<LocalImageHolderView>() {
					@Override
					public LocalImageHolderView createHolder() {
						return new LocalImageHolderView();
					}
				}, banners)
				//设置两个点图片作为翻页指示器
				.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
				.setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer)
				//设置指示器的方向
				.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
				.isViewPagerCanScroll();
		if (banners == null || banners.size() == 1) {
			stopTurning();
		} else {
			startTurning();
		}
	}

	private void startTurning() {
		convenientBanner.startTurning(Constants.BANNER_SWITCH_TIME);
	}

	private void stopTurning() {
		convenientBanner.stopTurning();
	}

	public class LocalImageHolderView implements Holder<MainCityActivitiesEntity> {
		private SimpleDraweeView simpleDraweeView;

		@Override
		public View createView(Context context) {
			simpleDraweeView = new SimpleDraweeView(context);
			return simpleDraweeView;
		}

		@Override
		public void UpdateUI(final Context context, int position, final MainCityActivitiesEntity data) {
			simpleDraweeView.setTag(data);

			if (!TextUtils.isEmpty(data.getCityActivitiesImg())) {
				simpleDraweeView.setLayoutParams(new ViewGroup.LayoutParams(DeviceUtil.getWidth(context), DeviceUtil.dp_to_px(context, 215)));
				ImageUtils.setSmallImg(simpleDraweeView, data.getCityActivitiesImg());
				simpleDraweeView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AnalysisUtil.onEvent(context, "Android_act_HomeBanner");
						mainCityActivitiesEntity = (MainCityActivitiesEntity) simpleDraweeView.getTag();
						if (mainCityActivitiesEntity.isNeedLogin() && !UserCenter.isLogin(getActivity())) {
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							intent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_MAIN_ACTIVITY);
							startActivityForResult(intent, REQUEST_ACT_LOGIN_CITY);
						} else {
							gotoActivity(REQUEST_ACT_LOGIN_CITY);
						}
					}
				});
			} else {
				simpleDraweeView.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.home_banner_default)).build());
			}
		}
	}
}
