package com.wjika.client.store.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.CategoryStoreListActivity;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.network.entities.SearchOptionEntity;
import com.wjika.client.network.entities.SearchStoreEntity;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.StoreListAdapter;
import com.wjika.client.store.view.CheckDistanceSpinnerView;
import com.wjika.client.store.view.CheckSpinnerView;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * 商家列表
 * Created by jacktian on 16/4/26.
 */
public class StoreListFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

	private static final int REQUEST_GET_SEARCH_STORE_CODE = 0x1;
	private static final int REQUEST_GET_SEARCH_STORE_CODE_MORE = 0x2;
	private static final int REQUEST_GET_SEARCH_OPTION_CODE = 0x3;
	private static final int REQUEST_CODE_OPEN_GPS = 0x100;
	private static final String BTN_TEXT_RETRY = "点击重试";
	private static final String BTN_TEXT_OPEN = "立即开启";

	private View view;

	@ViewInject(R.id.store_list_address)
	private TextView storeListAddress;
	@ViewInject(R.id.store_list_category)
	private TextView storeListCategory;
	@ViewInject(R.id.store_list_hot)
	private TextView storeListHot;
	@ViewInject(R.id.store_list_detail)
	private FootLoadingListView storeListDetail;
	@ViewInject(R.id.right_button)
	private ImageView rightButton;
	@ViewInject(R.id.left_btn_with_arrow)
	private TextView leftBtnWithArrow;
	@ViewInject(R.id.left_button)
	private ImageView leftButton;
	@ViewInject(R.id.location_arrow)
	private ImageView ivCityArrow;
	@ViewInject(R.id.ll_store_list_address)
	private LinearLayout llStoreListAddress;
	@ViewInject(R.id.store_address_arrow)
	private ImageView storeAddressArrow;
	@ViewInject(R.id.ll_store_list_category)
	private LinearLayout llStoreListCategory;
	@ViewInject(R.id.store_category_arrow)
	private ImageView storeCategoryArrow;
	@ViewInject(R.id.ll_store_list_hot)
	private LinearLayout llStoreListHot;
	@ViewInject(R.id.store_hot_arrow)
	private ImageView storeHotArrow;
	@ViewInject(R.id.toolbar_title)
	private TextView toolbarTitle;
	@ViewInject(R.id.txt_location_desc)
	private TextView tvLocationStatus;
	@ViewInject(R.id.layout_location)
	private View vLocation;
	@ViewInject(R.id.store_location_open)
	private TextView tvLocationOpen;
	@ViewInject(R.id.btn_retry_location)
	private ImageView ivArrow;
	@ViewInject(R.id.toolbar)
	private View vToolBar;

	private Context context;
	private CheckDistanceSpinnerView checkDistanceSpinnerView;
	private TranslateAnimation animation;
	private List<CityEntity> currentCityDistrict;

	private String cityName = "0";
	private String cityId = "1";
	private String distractId = "0";
	private int areaPosition1 = 0;
	private int areaPosition2 = 1;
	private String categoryId;
	private int from;
	private int categoryPosition;
	private int orderByPosition;
	private String sortKey;
	private String locationLatitude;
	private String locationLongitude;
	private SearchOptionEntity searchOptions;
	private CheckSpinnerView checkSpinnerView;
	private CheckSpinnerView mCheckSpinnerView;
	private StoreListAdapter storeListAdapter;
	private String oldCityId;
	private String categoryName;
	private String allMerchantTag;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.store_list_frag, null);
			ViewInjectUtils.inject(this, view);
			context = getActivity();
			initView(view);
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		if (isCityChange()) {
			updateUIByCity();
		}
		return view;
	}

	private void initView(View view) {
		initLoadingView(this, view);
		toolbarTitle.setText("商家");
		storeListDetail.setMode(PullToRefreshBase.Mode.BOTH);
		vLocation.setOnClickListener(this);
		mTxtCardEmpty.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		leftBtnWithArrow.setOnClickListener(this);
		mTxtCardEmpty.setClickable(false);
		mImgLoadingRetry.setOnClickListener(this);
		ivCityArrow.setVisibility(View.VISIBLE);
		leftBtnWithArrow.setVisibility(View.VISIBLE);
		toolbarTitle.setVisibility(View.VISIBLE);
		rightButton.setVisibility(View.VISIBLE);
		initParams();
		sortKey = "distance";

		if (getArguments() != null) {
			categoryId = this.getArguments().getString("categoryID");
			categoryName = getArguments().getString("categoryName");
			from = getArguments().getInt("from");
			allMerchantTag = getArguments().getString("all_merchant_tag");
			toolbarTitle.setText("");
			leftButton.setVisibility(View.VISIBLE);
			leftButton.setBackgroundResource(R.drawable.ic_back);
			ivCityArrow.setVisibility(View.INVISIBLE);
			leftBtnWithArrow.setClickable(false);
			if (!StringUtil.isEmpty(allMerchantTag)) {
				leftBtnWithArrow.setText(allMerchantTag);
			} else {
				leftBtnWithArrow.setText(categoryName);
			}
			leftButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
			});
		}

		storeListDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				startLocation();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true, true);
			}
		});
		storeListDetail.setOnItemClickListener(this);
		setLoadingStatus(LoadingStatus.LOADING);
		startLocation();
	}


	private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (getActivity() == null) {
				return;
			}
			int status = intent.getIntExtra("status", -1);
			if (-1 != status) {
				vLocation.setVisibility(storeListDetail.getVisibility());
				ivArrow.setVisibility(View.GONE);
				if (MainActivity.BROADCAST_LOCATION_STATUS_FAILED == status) {
					tvLocationOpen.setVisibility(View.VISIBLE);
					tvLocationStatus.setText(getActivity().getString(R.string.location_fail));
					tvLocationOpen.setText(BTN_TEXT_RETRY);
					updateUIByCity();
				} else if (MainActivity.BROADCAST_LOCATION_SELECT_CITY == status) {//选择城市
					vLocation.setVisibility(View.GONE);
					if (isCityChange()) {
						updateUIByCity();
					}
				} else if (MainActivity.BROADCAST_LOCATION_STATUS_SUCCESS == status) {//定位成功
					vLocation.setVisibility(View.GONE);
					updateUIByCity();
				} else if (MainActivity.BROADCAST_LOCATION_STATUS_NO_ACTIVE == status) {//定位城市没有开通

				} else if (MainActivity.BROADCAST_LOCATION_START == status) {//定位中状态
					setLoactionLoadingUI();
				} else if (MainActivity.BROADCAST_LOCATION_NO_GPS == status) {
					tvLocationOpen.setVisibility(View.VISIBLE);
					tvLocationStatus.setText(getActivity().getString(R.string.location_fail_no_recommend));
					tvLocationOpen.setText(BTN_TEXT_OPEN);
					ivArrow.setVisibility(View.VISIBLE);
					updateUIByCity();
				}
			}
		}
	};

	/**
	 * @return 返回true：表示城市改变； false：表示城市没有改变
	 */
	private boolean isCityChange() {
		String newCityId;
		if (StringUtil.isEmpty(LocationUtils.getSelectedCityId(context))) {
			newCityId = LocationUtils.getLocationCityId(context);
		} else {
			newCityId = LocationUtils.getSelectedCityId(context);
		}
		return !newCityId.equals(oldCityId);
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//是否展示？获取传递过来的值，没获取到默认是true，不展示状态
			boolean dismiss = intent.getBooleanExtra("menu_dismiss", true);
			if (dismiss) {
				ivCityArrow.setBackgroundResource(R.drawable.home_btn_location);
			} else {
				ivCityArrow.setBackgroundResource(R.drawable.icon_arrow_up);
			}
		}
	};

	private void registCityBroadcast() {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this.getActivity());
		lbm.registerReceiver(locationReceiver, new IntentFilter(MainActivity.BROADCAST_ACTION_LOACTION_OK));
		lbm.registerReceiver(broadcastReceiver, new IntentFilter(MainActivity.BROADCAST_ACTION_MENU_SHOW_STATUS));
	}

	private void unRegisterBroadcast() {
		LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(locationReceiver);
		LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(getActivity(), "Android_vie_MerchantView");
		registCityBroadcast();
	}

	@Override
	public void onPause() {
		super.onPause();
		unRegisterBroadcast();
	}


	private void startLocation() {
		LocationUtils.startLocation(context);
		vLocation.setVisibility(storeListDetail.getVisibility());
		setLoactionLoadingUI();
	}

	/**
	 * 定位中UI
	 */
	private void setLoactionLoadingUI() {
		ivArrow.setVisibility(View.GONE);
		tvLocationOpen.setVisibility(View.GONE);
		tvLocationStatus.setText(getActivity().getString(R.string.locationing));
	}

	private void updateUIByCity() {
		initParams();
		initSpinner();
		initSpinnerData();
		loadData(false, false);
	}

	private void initSpinnerData() {
		IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
		identityHashMap.put(Constants.TOKEN, UserCenter.getToken(context));
		identityHashMap.put("areaId", cityId);
		requestHttpData(Constants.Urls.URL_GET_STORE_SEARCH_OPTION,
				REQUEST_GET_SEARCH_OPTION_CODE,
				FProtocol.NetDataProtocol.DataMode.DATA_UPDATE_CACHE,
				FProtocol.HttpMethod.POST,
				identityHashMap);

	}

	private void loadData(boolean isMore, boolean flag) {
		if (!flag) {
			areaPosition1 = 0;
		}
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(context));
			params.put("areaId", distractId);
			params.put("merchantCategory", categoryId);
			params.put("merchantLatitude", locationLatitude);
			params.put("merchantLongitude", locationLongitude);
			params.put("pageNum", storeListAdapter.getPage() + 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", "");
			params.put("merchantAccountId", "");
			params.put("sort", sortKey);

			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_STORE_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(context));
			params.put("areaId", distractId);
			params.put("merchantCategory", categoryId);
			params.put("merchantLatitude", locationLatitude);
			params.put("merchantLongitude", locationLongitude);
			params.put("pageNum", 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", "");
			params.put("merchantAccountId", "");
			params.put("sort", sortKey);

			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_STORE_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}

	}

	private void initSpinner() {
		currentCityDistrict = CityDBManager.getCurrentCityDistrict(context, cityId);
		if (currentCityDistrict == null) {
			currentCityDistrict = new ArrayList<>();
		}
		CityEntity cityEntity = new CityEntity();
		cityEntity.setName(CityUtils.getCityShortName(cityName));
		cityEntity.setId(cityId);
		currentCityDistrict.add(0, cityEntity);
		storeListAddress.setText(getActivity().getString(R.string.store_list_area));
		checkDistanceSpinnerView = new CheckDistanceSpinnerView(getActivity(), new CheckDistanceSpinnerView.OnSpinnerItemClickListener() {

			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
			}

			@Override
			public void onItemClickListener2(List<CityEntity> currentStreet, AdapterView<?> parent, View view, int position1, int position2, long id) {
				areaPosition1 = position1;
				areaPosition2 = position2;
				storeListAddress.setText(currentStreet.get(position2).getName());
				distractId = currentStreet.get(position2).getId();
				checkDistanceSpinnerView.close();
				loadData(false, true);
				setLoadingStatus(LoadingStatus.LOADING);
			}
		});
		checkDistanceSpinnerView.addMenuShowListener(new CheckDistanceSpinnerView.PoupWindowListener() {
			@Override
			public void poupWindowDismiss(boolean isShow) {
				if (isShow) {
					storeListAddress.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
					storeAddressArrow.setBackgroundResource(R.drawable.ic_black_arrow_up);
				} else {
					storeListAddress.setTextColor(getResources().getColor(R.color.wjika_client_dark_grey));
					storeAddressArrow.setBackgroundResource(R.drawable.ic_black_arrow_down);
				}
			}
		});
		llStoreListAddress.setOnClickListener(this);

		checkSpinnerView = new CheckSpinnerView(getActivity(), new CheckSpinnerView.OnSpinnerItemClickListener() {

			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
				categoryPosition = position;
				if (position == 0) {
					storeListCategory.setText(getActivity().getString(R.string.store_list_category));
				} else {
					storeListCategory.setText(searchOptions.getCategorys().get(position).getName());
				}
				categoryId = searchOptions.getCategorys().get(position).getId();
				categoryName = searchOptions.getCategorys().get(position).getName();
				if (from == CategoryStoreListActivity.STORE_LIST_ACTIVITY) {
					leftBtnWithArrow.setText(categoryName);
				} else {
					leftBtnWithArrow.setText(CityUtils.getCityShortName(cityName));
				}
				checkSpinnerView.close();
				loadData(false, true);
				setLoadingStatus(LoadingStatus.LOADING);
			}
		});
		checkSpinnerView.addPoupWindowListener(new CheckSpinnerView.PoupWindowListener() {
			@Override
			public void poupWindowDismiss(boolean isShow) {
				if (isShow) {
					storeListCategory.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
					storeCategoryArrow.setBackgroundResource(R.drawable.ic_black_arrow_up);
				} else {
					storeListCategory.setTextColor(getResources().getColor(R.color.wjika_client_dark_grey));
					storeCategoryArrow.setBackgroundResource(R.drawable.ic_black_arrow_down);
				}
			}
		});

		llStoreListCategory.setOnClickListener(this);

		mCheckSpinnerView = new CheckSpinnerView(getActivity(), new CheckSpinnerView.OnSpinnerItemClickListener() {

			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
				orderByPosition = position;
				storeListHot.setText(searchOptions.getOrderBys().get(position).getName());
				sortKey = searchOptions.getOrderBys().get(position).getId();
				mCheckSpinnerView.close();
				loadData(false, true);
				setLoadingStatus(LoadingStatus.LOADING);
			}
		});
		llStoreListHot.setOnClickListener(this);
		mCheckSpinnerView.addPoupWindowListener(new CheckSpinnerView.PoupWindowListener() {
			@Override
			public void poupWindowDismiss(boolean isShow) {
				if (isShow) {
					storeListHot.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
					storeHotArrow.setBackgroundResource(R.drawable.ic_black_arrow_up);
				} else {
					storeListHot.setTextColor(getResources().getColor(R.color.wjika_client_dark_grey));
					storeHotArrow.setBackgroundResource(R.drawable.ic_black_arrow_down);
				}
			}
		});
		animation = new TranslateAnimation(0, 0, -(DeviceUtil.getHeight(getActivity())), 0);
		animation.setDuration(100);
	}

	private void initParams() {
		locationLatitude = LocationUtils.getLocationLatitude(context);
		locationLongitude = LocationUtils.getLocationLongitude(context);

		if (StringUtil.isEmpty(LocationUtils.getSelectedCity(context))) {
			cityName = LocationUtils.getLocationCity(context);
		} else {
			cityName = LocationUtils.getSelectedCity(context);
		}

		if (StringUtil.isEmpty(LocationUtils.getSelectedCityId(context))) {
			cityId = LocationUtils.getLocationCityId(context);
		} else {
			cityId = LocationUtils.getSelectedCityId(context);
		}
		if (from == CategoryStoreListActivity.STORE_LIST_ACTIVITY) {
			leftBtnWithArrow.setText(categoryName);
		} else {
			leftBtnWithArrow.setText(CityUtils.getCityShortName(cityName));
		}
		if (CategoryStoreListActivity.ALL_MERCHANT.equals(allMerchantTag)) {
			leftBtnWithArrow.setText(allMerchantTag);
		}
		distractId = cityId;
		oldCityId = cityId;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, StoreDetailActivity.class);
		intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, storeListAdapter.getItem(position).getId());
		intent.putExtra(StoreDetailActivity.EXTRA_LATITUDE, locationLatitude);
		intent.putExtra(StoreDetailActivity.EXTRA_LONGITUDE, locationLongitude);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_store_list_address:
				AnalysisUtil.onEvent(getActivity(), "Android_act_AreaFilter");
				checkDistanceSpinnerView.showSpinnerPop(llStoreListAddress, animation, currentCityDistrict, areaPosition1, areaPosition2);
				break;
			case R.id.ll_store_list_category:
				AnalysisUtil.onEvent(getActivity(), "Android_act_ClassFilter");
				if (searchOptions != null && searchOptions.getCategorys() != null) {
					checkSpinnerView.showSpinnerPop(llStoreListCategory, animation, searchOptions.getCategorys(), categoryPosition);
				}
				break;
			case R.id.ll_store_list_hot:
				AnalysisUtil.onEvent(getActivity(), "Android_act_IntelFilter");
				if (searchOptions != null && searchOptions.getOrderBys() != null) {
					mCheckSpinnerView.showSpinnerPop(llStoreListHot, animation, searchOptions.getOrderBys(), orderByPosition);
				}
				break;
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				updateUIByCity();
				loadData(false, false);
				break;
			case R.id.right_button:
				AnalysisUtil.onEvent(getActivity(), "Android_act_SearchButton");
				startActivity(new Intent(context, SearchActivity.class));
				break;
			case R.id.left_btn_with_arrow:
				((MainActivity) getActivity()).showCityMenu(vToolBar);
				break;
			case R.id.layout_location:
				if (BTN_TEXT_OPEN.equals(tvLocationOpen.getText())) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, REQUEST_CODE_OPEN_GPS); // 设置完成后返回到原来的界面
				} else if (BTN_TEXT_RETRY.equals(tvLocationOpen.getText())) {
					startLocation();
				}
				break;
			case R.id.loading_img_refresh:
				loadData(false, false);
				initSpinnerData();
				setLoadingStatus(LoadingStatus.LOADING);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_OPEN_GPS) {
			if (LocationUtils.isGPSOpen(getActivity())) {
				startLocation();
			}
		}
	}

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		switch (requestCode) {
			case REQUEST_GET_SEARCH_STORE_CODE:
				storeListDetail.onRefreshComplete();
				storeListDetail.setVisibility(View.VISIBLE);
				if (data != null) {
					SearchStoreEntity searchStore = Parsers.getSearchStore(data);
					if (searchStore != null && searchStore.getStoreEntityList() != null && searchStore.getStoreEntityList().size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);
						storeListAdapter = new StoreListAdapter(context, searchStore.getStoreEntityList());
						storeListAdapter.setIsLocation(locationLatitude, locationLongitude);
						storeListDetail.setAdapter(storeListAdapter);
						if (searchStore.getTotalPage() > 1) {
							storeListDetail.setCanAddMore(true);
						} else {
							storeListDetail.setCanAddMore(false);
						}
					} else {
						empty();
					}
				} else {
					empty();
				}
				break;
			case REQUEST_GET_SEARCH_STORE_CODE_MORE:
				storeListDetail.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity storeEntity = Parsers.getSearchStore(data);
					storeListAdapter.addDatas(storeEntity.getStoreEntityList());
					if (storeEntity.getPageNumber() < storeEntity.getTotalPage()) {
						storeListDetail.setCanAddMore(true);
					} else {
						storeListDetail.setCanAddMore(false);
					}
				}
				break;
			case REQUEST_GET_SEARCH_OPTION_CODE:
				if (data != null) {
					searchOptions = Parsers.getSearchOptions(data);
					if (searchOptions != null && searchOptions.getCategorys() != null) {
						for (int i = 0; i < searchOptions.getCategorys().size(); i++) {
							if (searchOptions.getCategorys().get(i).getId().equals(categoryId)) {
								categoryPosition = i;
								if (i == 0) {
									storeListCategory.setText(getActivity().getString(R.string.store_list_category));
								} else {
									storeListCategory.setText(searchOptions.getCategorys().get(i).getName());
								}
							}
						}
					}
				}
				break;
		}
	}

	private void empty() {
		setLoadingStatus(LoadingStatus.EMPTY);
		storeListAdapter = new StoreListAdapter(context, new ArrayList<StoreEntity>());
		storeListDetail.setAdapter(storeListAdapter);
		storeListDetail.setCanAddMore(false);
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		storeListDetail.onRefreshComplete();
		switch (requestCode) {
			case REQUEST_GET_SEARCH_STORE_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				vLocation.setVisibility(View.GONE);
				storeListDetail.setVisibility(View.GONE);
				break;
			case REQUEST_GET_SEARCH_OPTION_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				vLocation.setVisibility(View.GONE);
				break;
		}
	}
}
