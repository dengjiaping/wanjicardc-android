package com.wjika.client.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.network.entities.SearchOptionEntity;
import com.wjika.client.network.entities.SearchStoreEntity;
import com.wjika.client.network.entities.StoreEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.StoreListAdapter;
import com.wjika.client.store.controller.StoreDetailActivity;
import com.wjika.client.store.view.CheckDistanceSpinnerView;
import com.wjika.client.store.view.CheckSpinnerView;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.LocationUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by ZHXIA on 2016/6/17
 * 单个品牌列表
 */
public class OneBrandListActivity extends ToolBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final String BRAND_NAME = "brand_name";
	public static final String MERCHANT_ACCOUNT_ID = "merchant_account_id";
	private static final int REQUEST_GET_SEARCH_BRAND_CODE = 0x1;
	private static final int REQUEST_GET_SEARCH_BRAND_CODE_MORE = 0x2;
	private static final int REQUEST_GET_SEARCH_OPTION_CODE = 0x3;
	private TextView brandListAddress;
	private TextView brandListHot;
	private FootLoadingListView brandListDetail;
	private String locationLatitude;
	private String locationLongitude;
	private String cityName = "0";
	private String cityId = "1";
	private String distractId = "0";
	private String sortKey;
	private List<CityEntity> currentCityDistrict;
	private SearchOptionEntity searchOptions;
	private CheckDistanceSpinnerView checkDistanceSpinnerView;
	private CheckSpinnerView mCheckSpinnerView;
	private StoreListAdapter brandListAdapter;
	private int areaPosition1 = 0;
	private int areaPosition2 = 1;
	private TranslateAnimation animation;
	private int orderByPosition;
	private String merchantAccountId = "";
	private String merchantName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_to_hotbrands_list);
		initView();
		initParams();
		initSpinner();
		loadSpinnerData();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		if (getIntent() != null) {
			merchantAccountId = getIntent().getStringExtra(MERCHANT_ACCOUNT_ID);
			merchantName = getIntent().getStringExtra(BRAND_NAME);
		}
		setLeftTitle(merchantName);
		brandListAddress = (TextView) findViewById(R.id.brand_list_address);
		brandListHot = (TextView) findViewById(R.id.brand_list_hot);
		brandListDetail = (FootLoadingListView) findViewById(R.id.brand_list_detail);
		brandListDetail.setOnItemClickListener(this);
		brandListDetail.setMode(PullToRefreshBase.Mode.BOTH);
		brandListDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
	}

	private void loadData(boolean isMore) {
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", distractId);
			params.put("merchantCategory", "0");
			params.put("merchantLatitude", locationLatitude);
			params.put("merchantLongitude", locationLongitude);
			params.put("pageNum", brandListAdapter.getPage() + 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", "");
			params.put("merchantAccountId", merchantAccountId);
			params.put("sort", sortKey);
			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_BRAND_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", distractId);
			params.put("merchantCategory", "0");
			params.put("merchantLatitude", locationLatitude);
			params.put("merchantLongitude", locationLongitude);
			params.put("pageNum", 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", "");
			params.put("merchantAccountId", merchantAccountId);
			params.put("sort", sortKey);
			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_BRAND_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		switch (requestCode) {
			case REQUEST_GET_SEARCH_BRAND_CODE:
				setLoadingStatus(LoadingStatus.GONE);
				brandListDetail.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity searchbrand = Parsers.getSearchStore(data);
					if (searchbrand != null && searchbrand.getStoreEntityList() != null && searchbrand.getStoreEntityList().size() > 0) {
						brandListAdapter = new StoreListAdapter(this, searchbrand.getStoreEntityList());
						brandListAdapter.setIsLocation(locationLatitude, locationLongitude);
						brandListDetail.setAdapter(brandListAdapter);
						if (searchbrand.getTotalPage() > 1) {
							brandListDetail.setCanAddMore(true);
						} else {
							brandListDetail.setCanAddMore(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			case REQUEST_GET_SEARCH_BRAND_CODE_MORE:
				brandListDetail.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity brandEntity = Parsers.getSearchStore(data);
					if (brandEntity != null) {
						brandListAdapter.addDatas(brandEntity.getStoreEntityList());
						if (brandEntity.getPageNumber() < brandEntity.getTotalPage()) {
							brandListDetail.setCanAddMore(true);
						} else {
							brandListDetail.setCanAddMore(false);
						}
					}
				}
				break;
			case REQUEST_GET_SEARCH_OPTION_CODE:
				if (data != null) {
					searchOptions = Parsers.getSearchOptions(data);
				}
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		if (REQUEST_GET_SEARCH_OPTION_CODE == requestCode) {
			closeProgressDialog();
			//ToastUtil.shortShow(this, errorMessage);
			setLoadingStatus(LoadingStatus.RETRY);
		} else {
			brandListDetail.onRefreshComplete();
			if (REQUEST_GET_SEARCH_BRAND_CODE == requestCode) {
				closeProgressDialog();
				ToastUtil.shortShow(this, errorMessage);
				setLoadingStatus(LoadingStatus.RETRY);
			}
		}
	}

	private void initParams() {
		locationLatitude = LocationUtils.getLocationLatitude(this);
		locationLongitude = LocationUtils.getLocationLongitude(this);

		if (StringUtil.isEmpty(LocationUtils.getSelectedCity(this))) {
			cityName = LocationUtils.getLocationCity(this);
		} else {
			cityName = LocationUtils.getSelectedCity(this);
		}
		if (StringUtil.isEmpty(LocationUtils.getSelectedCityId(this))) {
			cityId = LocationUtils.getLocationCityId(this);
		} else {
			cityId = LocationUtils.getSelectedCityId(this);
		}
		distractId = cityId;
	}

	private void initSpinner() {
		currentCityDistrict = CityDBManager.getCurrentCityDistrict(this, cityId);
		if (currentCityDistrict == null) {
			currentCityDistrict = new ArrayList<>();
		}
		CityEntity cityEntity = new CityEntity();
		cityEntity.setName(CityUtils.getCityShortName(cityName));
		cityEntity.setId(cityId);
		currentCityDistrict.add(0, cityEntity);
		brandListAddress.setText("全部商圈");
		checkDistanceSpinnerView = new CheckDistanceSpinnerView(this, new CheckDistanceSpinnerView.OnSpinnerItemClickListener() {
			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
			}

			@Override
			public void onItemClickListener2(List<CityEntity> currentStreet, AdapterView<?> parent, View view, int position1, int position2, long id) {
				areaPosition1 = position1;
				areaPosition2 = position2;
				brandListAddress.setText(currentStreet.get(position2).getName());
				distractId = currentStreet.get(position2).getId();
				checkDistanceSpinnerView.close();
				loadData(false);
			}
		});
		brandListAddress.setOnClickListener(this);

		mCheckSpinnerView = new CheckSpinnerView(this, new CheckSpinnerView.OnSpinnerItemClickListener() {
			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
				orderByPosition = position;
				brandListHot.setText(searchOptions.getOrderBys().get(position).getName());
				sortKey = searchOptions.getOrderBys().get(position).getId();
				mCheckSpinnerView.close();
				loadData(false);
			}
		});
		brandListHot.setOnClickListener(this);

		animation = new TranslateAnimation(0, 0, -(DeviceUtil.getHeight(this)), 0);
		animation.setDuration(100);
	}

	private void loadSpinnerData() {
		IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
		identityHashMap.put(Constants.TOKEN, UserCenter.getToken(this));
		identityHashMap.put("areaId", cityId);
		requestHttpData(Constants.Urls.URL_GET_STORE_SEARCH_OPTION,
				REQUEST_GET_SEARCH_OPTION_CODE,
				FProtocol.NetDataProtocol.DataMode.DATA_UPDATE_CACHE,
				FProtocol.HttpMethod.POST,
				identityHashMap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.brand_list_address:
				checkDistanceSpinnerView.showSpinnerPop(brandListAddress, animation, currentCityDistrict, areaPosition1, areaPosition2);
				break;
			case R.id.brand_list_hot:
				if (searchOptions != null && searchOptions.getOrderBys() != null) {
					mCheckSpinnerView.showSpinnerPop(brandListHot, animation, searchOptions.getOrderBys(), orderByPosition);
				}
				break;
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false);
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		StoreEntity storeEntity = brandListAdapter.getItem(position);
		if (storeEntity != null) {
			Intent intent = new Intent(OneBrandListActivity.this, StoreDetailActivity.class);
			intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, storeEntity.getId());
			startActivity(intent);
		}
	}
}
