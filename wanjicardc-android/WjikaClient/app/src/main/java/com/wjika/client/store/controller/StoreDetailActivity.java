package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.view.SidesLipGallery;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.PrivilegeEntity;
import com.wjika.client.network.entities.ProductEntity;
import com.wjika.client.network.entities.StoreDetailsEntity;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.network.entities.StoreImgEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.BigStorePhotoAdapter;
import com.wjika.client.store.adapter.StoreShowcaseAdapter;
import com.wjika.client.store.adapter.SupportStoreAdapter;
import com.wjika.client.store.view.ActionSheetDialog;
import com.wjika.client.store.view.AlphaTitleScrollView;
import com.wjika.client.utils.CommonShareUtil;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Leo_Zhang on 2016/5/19.
 * 商家详情
 */
public class StoreDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static String EXTRA_FROM = "extra_from";
	public static final String EXTRA_STORE_ID = "extra_store_id";
	private static final int REQUEST_GET_STORE_DETAILS_CODE = 0x1;
	public static final int FROM_STORE_DETAILS_ACTIVITY = 100;
	public static final int FROM_STORE_LIST_ACTIVITY = 102;
	public static final int FROM_CARD_PACKAGE = 103;
	public static final String EXTRA_LATITUDE = "extra_latitude";
	public static final String EXTRA_LONGITUDE = "extra_longitude";

	@ViewInject(R.id.sotre_left_button)
	private ImageView storeLeftButton;
	@ViewInject(R.id.txt_name)
	private TextView txtName;
	@ViewInject(R.id.txt_businesstime)
	private TextView txtBusinesstime;
	@ViewInject(R.id.txt_address)
	private TextView txtAddress;
	@ViewInject(R.id.layout_privilege)
	private RelativeLayout layoutPrivilage;
	@ViewInject(R.id.store_contain_card_list)
	private ListView storeContainCardList;
	@ViewInject(R.id.store_details_desc)
	private TextView storeDetailsDesc;
	@ViewInject(R.id.txt_address_label)
	private LinearLayout txtAddressLabel;
	@ViewInject(R.id.store_detail_phone)
	private RelativeLayout storeDetailPhone;
	@ViewInject(R.id.store_detail_describe)
	private RelativeLayout storeDetailDescribl;
	@ViewInject(R.id.store_arrow)
	private ImageView storeArrow;
	@ViewInject(R.id.store_card_more)
	private RelativeLayout storeCardMore;
	@ViewInject(R.id.store_shop_more)
	private RelativeLayout storeShopMore;
	@ViewInject(R.id.store_correlation_shop)
	private ListView storeCorrelationShop;
	@ViewInject(R.id.privilege_icon_container)
	private LinearLayout privilegeIconContainer;
	@ViewInject(R.id.scrollview)
	private AlphaTitleScrollView scrollview;
	@ViewInject(R.id.layout_store_contain_card)
	private LinearLayout layoutStoreContainCard;
	@ViewInject(R.id.check_more_product)
	private TextView checkMoreProduct;
	@ViewInject(R.id.check_more_product_img)
	private ImageView checkMoreProductImg;
	@ViewInject(R.id.layout_support_store)
	private LinearLayout layoutSupportStore;
	@ViewInject(R.id.support_store_count)
	private TextView supportStoreCount;
	@ViewInject(R.id.support_store_count_img)
	private ImageView supportStoreCountImg;
	@ViewInject(R.id.store_button_share)
	private ImageView storeButtonShare;
	@ViewInject(R.id.store_activity)
	private LinearLayout storeActivity;
	@ViewInject(R.id.loading_img_refresh)
	private ImageView loadingImgRefresh;
	@ViewInject(R.id.store_toolbar)
	private RelativeLayout base_titlebar;
	@ViewInject(R.id.store_left_title)
	private TextView storeLeftTitle;
	@ViewInject(R.id.layout_store_desc)
	private LinearLayout layoutStoreDesc;
	@ViewInject(R.id.detail_big_image_gallery)
	private SidesLipGallery gallery;

	private int height;
	private boolean isExtend = false;//是否展开。
	private boolean isRunAnim = false;

	private String storeId;
	private StoreDetailsEntity storeDetails;
	private StoreShowcaseAdapter storeShowcaseAdapter;
	private String businessTime;
	private String address;
	private String name;
	private String phone;
	private String introductionUrl;
	private ArrayList<StoreImgEntity> imgPaths;
	private int bigImgPosition = 0;
	private BigStorePhotoAdapter bigStorePhotoAdapter;
	private ArrayList<PrivilegeEntity> privilegeEntities;
	private List<ProductEntity> productEntityList;
	private List<StoreEntity> branchs;
	private SupportStoreAdapter supportStoreAdapter;
	private int from;
	private double latitude;
	private double longitude;
	private String personLatitude;
	private String personLongitude;
	private int branchNum;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_detail_act);
		ViewInjectUtils.inject(this);
		storeId = getIntent().getStringExtra(EXTRA_STORE_ID);
		personLatitude = LocationUtils.getLocationLatitude(this);
		personLongitude = LocationUtils.getLocationLongitude(this);
		from = getIntent().getIntExtra("extra_from", 0);
		initView();
		loadData();
		LocationUtils.startLocation(this);
	}

	private void initView() {
		storeLeftTitle.setText(this.getString(R.string.store_details_title));
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		txtAddressLabel.setOnClickListener(this);
		storeDetailPhone.setOnClickListener(this);
		layoutPrivilage.setOnClickListener(this);
		storeDetailDescribl.setOnClickListener(this);
		storeLeftButton.setOnClickListener(this);
		storeButtonShare.setVisibility(View.VISIBLE);
		storeButtonShare.setOnClickListener(this);
		loadingImgRefresh.setOnClickListener(this);
		base_titlebar.getBackground().mutate().setAlpha(0);
		scrollview.setTitleAndHead(base_titlebar, gallery);
	}

	private void loadData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("merchantId", storeId);
		params.put("merchantLatitude", personLatitude);
		params.put("merchantLongitude", personLongitude);
		requestHttpData(Constants.Urls.URL_GET_STORE_DETAILS,
				REQUEST_GET_STORE_DETAILS_CODE,
				FProtocol.HttpMethod.POST,
				params);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_GET_STORE_DETAILS_CODE:
				if (data != null) {
					storeDetails = Parsers.getStoreDetails(data);
					if (storeDetails != null) {
						setLoadingStatus(LoadingStatus.GONE);
						getData();
						setStorePhoto();
						scrollview.setTitleAndHead(base_titlebar, gallery);
						setStoreName();
						setStoreDesc();
						setPrivilegeView();
						productData();
						storeData();
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
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_MerchantDetails");
	}

	private void setStoreDesc() {
		if (!StringUtil.isEmpty(introductionUrl)) {
			CharSequence charSequence = Html.fromHtml(introductionUrl);
			storeDetailsDesc.setText(charSequence);
		} else {
			layoutStoreDesc.setVisibility(View.GONE);
		}
		storeDetailsDesc.setMovementMethod(LinkMovementMethod.getInstance());
		storeDetailsDesc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				storeDetailsDesc.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				height = storeDetailsDesc.getHeight();
				storeDetailsDesc.getLayoutParams().height = 0;
				storeDetailsDesc.requestLayout();
			}
		});
	}

	private void setActivity() {
		if (storeDetails != null && storeDetails.getActivityNum() > 0) {
			storeActivity.setVisibility(View.VISIBLE);
		} else {
			storeActivity.setVisibility(View.GONE);
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		switch (requestCode) {
			case REQUEST_GET_STORE_DETAILS_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	private void setPrivilegeView() {
		if (privilegeEntities != null && privilegeEntities.size() > 0) {
			layoutPrivilage.setVisibility(View.VISIBLE);
			int defaultPadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_comment_marginright) * 2;
			int width = DeviceUtil.getWidth(this) - 44 - defaultPadding;
			int privilegeSize = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_size);
			int privilegePadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_padding);
			int columsNum = width / (privilegeSize + privilegePadding);
			if (columsNum > storeDetails.getPrivilegeEntityList().size()) {
				columsNum = storeDetails.getPrivilegeEntityList().size();
			}

			for (int i = 0; i < columsNum; i++) {
				SimpleDraweeView view = new SimpleDraweeView(this);
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(privilegeSize, privilegeSize);
				view.setLayoutParams(imgParams);
				GenericDraweeHierarchyBuilder builder =
						new GenericDraweeHierarchyBuilder(getResources());
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
				String url = storeDetails.getPrivilegeEntityList().get(i).getImgPath();
				if (!TextUtils.isEmpty(url) && !url.contains("?")) {
					ImageUtils.setSmallImg(view,url);
				}
				privilegeIconContainer.addView(view);
				privilegeIconContainer.addView(blankView);
			}
			layoutPrivilage.requestFocus();
		} else {
			layoutPrivilage.setVisibility(View.GONE);
		}
	}

	private void getData() {
		name = storeDetails.getName();
		businessTime = storeDetails.getBusinessTime();
		address = storeDetails.getAddress();
		phone = storeDetails.getPhone();
		introductionUrl = storeDetails.getIntroductionUrl();
		imgPaths = storeDetails.getStoreImgEntities();
		privilegeEntities = storeDetails.getPrivilegeEntityList();
		productEntityList = storeDetails.getProductEntityList();
		branchs = storeDetails.getBranchs();
		latitude = storeDetails.getLatitude();
		longitude = storeDetails.getLongitude();
		branchNum = storeDetails.getBranchNum();

	}

	//相关门店
	private void storeData() {
		if (storeDetails != null && branchNum > 0 && from != FROM_STORE_DETAILS_ACTIVITY) {
			layoutSupportStore.setVisibility(View.VISIBLE);
			if (branchNum > 3) {
				List<StoreEntity> storeEntities = new ArrayList<>();
				storeEntities.add(branchs.get(0));
				storeEntities.add(branchs.get(1));
				storeEntities.add(branchs.get(2));
				supportStoreAdapter = new SupportStoreAdapter(this, storeEntities);
				storeShopMore.setOnClickListener(this);
			} else {
				storeShopMore.setOnClickListener(null);
				supportStoreCount.setVisibility(View.INVISIBLE);
				supportStoreCountImg.setVisibility(View.INVISIBLE);
				supportStoreAdapter = new SupportStoreAdapter(this, branchs);
			}
			supportStoreAdapter.setLocation(personLatitude, personLongitude);
			storeCorrelationShop.setAdapter(supportStoreAdapter);
		} else {
			layoutSupportStore.setVisibility(View.GONE);
		}

		storeCorrelationShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(StoreDetailActivity.this, StoreDetailActivity.class);
				intent.putExtra(EXTRA_STORE_ID, supportStoreAdapter.getItem(position).getId());
				intent.putExtra("extra_from", FROM_STORE_DETAILS_ACTIVITY);
				startActivity(intent);
			}
		});
	}

	//商户橱窗
	private void productData() {
		if (productEntityList != null && productEntityList.size() > 0) {
			layoutStoreContainCard.setVisibility(View.VISIBLE);
			if (productEntityList.size() > 3) {
				List<ProductEntity> productEntities = new ArrayList<>();
				productEntities.add(productEntityList.get(0));
				productEntities.add(productEntityList.get(1));
				productEntities.add(productEntityList.get(2));
				storeShowcaseAdapter = new StoreShowcaseAdapter(this, productEntities);
				storeCardMore.setOnClickListener(this);
			} else {
				storeCardMore.setOnClickListener(null);
				checkMoreProduct.setVisibility(View.INVISIBLE);
				checkMoreProductImg.setVisibility(View.INVISIBLE);
				storeShowcaseAdapter = new StoreShowcaseAdapter(this, productEntityList);
			}
			storeContainCardList.setAdapter(storeShowcaseAdapter);
		} else {
			layoutStoreContainCard.setVisibility(View.GONE);
		}

		storeContainCardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(StoreDetailActivity.this, CardDetailActivity.class);
				intent.putExtra(CardDetailActivity.EXTRA_CARD_ID, storeShowcaseAdapter.getItem(position).getId());
				intent.putExtra(CardDetailActivity.EXTRA_CARD_NAME, storeShowcaseAdapter.getItem(position).getName());
				intent.putExtra(CardDetailActivity.EXTRA_FROM, CardDetailActivity.FROM_STORE_LIST);//须展示购买
				intent.putExtra(CardDetailActivity.EXTRA_BRANCH_MER_ID, storeId);
				intent.putExtra(CardDetailActivity.EXTRA_IS_MYCARD, false);
				intent.putExtra(CardDetailActivity.EXTRA_CARD_POSITION, position);
				intent.putExtra(CardDetailActivity.EXTRA_CARD_NAME, storeShowcaseAdapter.getItem(position).getStoreName());
				startActivity(intent);
			}
		});
	}

	private void setStoreName() {
		txtName.setText(name);
		setActivity();
		txtBusinesstime.setText(businessTime);
		txtAddress.setText(address);
	}

	//设置商户图片
	private void setStorePhoto() {
		View bigImage = findViewById(R.id.item_post_detai_big_image);
		if (imgPaths != null && imgPaths.size() > 0) {
			final TextView num = (TextView) findViewById(R.id.detail_big_image_image_count);
			num.setText((bigImgPosition + 1) + "/" + imgPaths.size());
			if (bigStorePhotoAdapter == null) {
				bigStorePhotoAdapter = new BigStorePhotoAdapter(StoreDetailActivity.this, imgPaths);
				gallery.setAdapter(bigStorePhotoAdapter);
			}

			bigImage.setVisibility(View.VISIBLE);
			gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					bigImgPosition = position;
					num.setText(StoreDetailActivity.this.getString(R.string.store_divide, String.valueOf(bigImgPosition + 1), String.valueOf(imgPaths.size())));
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		} else {
			bigImage.setVisibility(View.GONE);
		}
		gallery.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.store_detail_phone:
				AnalysisUtil.onEvent(this, "Android_act_ShopTel");
				if (!StringUtil.isEmpty(phone)) {
					ActionSheetDialog dialog = new ActionSheetDialog(this);
					dialog.builder().setTitle(this.getString(R.string.choose_phone_num)).setCancelable(false).setCanceledOnTouchOutside(true);
					dialog.addSheetItem(phone, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							tell(phone);
						}
					}).show();
				}
				break;
			case R.id.txt_address_label:
				AnalysisUtil.onEvent(this, "Android_act_ShopMap");
				Intent intent = new Intent(this, StoreLocationActivity.class);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				startActivity(intent);
				break;
			case R.id.layout_privilege:
				if (storeDetails != null) {
					Intent helpIntent = new Intent(this, WebViewActivity.class);
					helpIntent.putExtra(WebViewActivity.EXTRA_FROM, WebViewActivity.FROM_PRIVILEGE_EXPLAIN);
					helpIntent.putExtra(WebViewActivity.EXTRA_URL, Constants.Urls.URL_PRIVILEGE_DOMAIN + "?merchantId=" + storeDetails.getMainMerId() + "&token=" + UserCenter.getToken(this) + "&privilegeType=merchant");
					helpIntent.putExtra(WebViewActivity.EXTRA_TITLE, this.getString(R.string.privilege_explain));
					startActivity(helpIntent);
				}
				break;
			case R.id.store_detail_describe:
				executeAnim();
				break;
			case R.id.store_card_more:
				Intent intent1 = new Intent(this, ProductCardActivity.class);
				ArrayList<ProductEntity> list = new ArrayList<>();
				list.addAll(productEntityList);
				intent1.putExtra("productList", list);
				intent1.putExtra("storeId", storeId);
				startActivity(intent1);
				break;
			case R.id.store_shop_more:
				if (storeDetails != null) {
					AnalysisUtil.onEvent(this, "Android_act_relatedmore");
					Intent intent2 = new Intent(this, CorrelationStoreActivity.class);
					intent2.putExtra(CorrelationStoreActivity.EXTRA_MERID, storeDetails.getMainMerId());
					startActivity(intent2);
				}
				break;
			case R.id.sotre_left_button:
				finish();
				break;
			case R.id.store_button_share:
				AnalysisUtil.onEvent(this, "Android_act_Share");
				if (storeDetails == null || storeDetails.getUrl() == null || storeDetails.getTitle() == null || storeDetails.getDesc() == null) {
					return;
				}
				CommonShareUtil.share(this, storeDetails.getDesc(),
						storeDetails.getTitle(), imgPaths.get(0).getImgPath(),
						storeDetails.getUrl());
				break;
			case R.id.loading_img_refresh:
				loadData();
				setLoadingStatus(LoadingStatus.LOADING);
				break;
		}
	}

	private void executeAnim() {
		if (isRunAnim) return;
		ValueAnimator valueAnimator;
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
				storeDetailsDesc.getLayoutParams().height = (int) animation.getAnimatedValue();
				storeDetailsDesc.requestLayout();
			}
		});
		valueAnimator.setDuration(350);
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
		ViewPropertyAnimator.animate(storeArrow).rotationBy(180).setDuration(350).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AnalysisUtil.onEvent(this, "Android_act_ShopBanner");
		if (imgPaths != null) {
			Intent intent = new Intent(this, BigImageActivity.class);
			intent.putExtra(BigImageActivity.EXTRA_POSITION, position);
			intent.putParcelableArrayListExtra(BigImageActivity.EXTRA_IMGPATHS, imgPaths);
			startActivity(intent);
		}
	}
}
