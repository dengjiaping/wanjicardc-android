package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
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
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.StoreAdapter;
import com.wjika.client.store.view.CheckDistanceSpinnerView;
import com.wjika.client.store.view.CheckSpinnerView;
import com.wjika.client.utils.CityUtils;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * 搜索结果页
 * Created by jacktian on 15/9/6.
 */
public class SearchResultActivity extends ToolBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

	private static final int REQUEST_GET_SEARCH_STORE_CODE = 0x1;
	private static final int REQUEST_GET_SEARCH_STORE_CODE_MORE = 0x2;
	private static final int REQUEST_GET_SEARCH_OPTION_CODE = 0x3;
	public static final String EXTRA_SEARCH_RESULT_NAME = "extra_search_name";
	public static final int EXTRA_FROM_SEARCH_CODE = 100;

	@ViewInject(R.id.store_list)
	private FootLoadingListView mStoreListView;
	@ViewInject(R.id.store_list_address)
	private TextView storeListAddress;
	@ViewInject(R.id.store_list_category)
	private TextView storeListCategory;
	@ViewInject(R.id.store_list_hot)
	private TextView storeListHot;
	@ViewInject(R.id.store_address_arrow)
	private ImageView storeAddressArrow;
	@ViewInject(R.id.store_category_arrow)
	private ImageView storeCategoryArrow;
	@ViewInject(R.id.store_hot_arrow)
	private ImageView storeHotArrow;

	private SearchOptionEntity searchOptions;
	private TranslateAnimation animation;
	private CheckDistanceSpinnerView checkDistanceSpinnerView;
	private CheckSpinnerView checkSpinnerView;
	private CheckSpinnerView mCheckSpinnerView;
	private int categoryPosition;
	private int orderByPosition;
	private StoreAdapter mStoreAdapter;
	private List<CityEntity> currentCityDistrict;
	private int areaPosition1 = 0;
	private int areaPosition2 = 1;
	private String cityId = "1";
	private String longitude = "0.0";
	private String latitude = "0.0";
	private String categoryId = "0";
	private String storeName = "";
	private String sortKey = "";
	private String cityName = "0";
	private String distractId = "0";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_single_store_list_act);
		ViewInjectUtils.inject(this);
		initRequestParams();
		initSpinnerData();
		initSpinner();
		initView();
		LocationUtils.startLocation(this);
		searchStoreData(false);

	}

	private void initRequestParams() {
		latitude = LocationUtils.getLocationLatitude(this);
		longitude = LocationUtils.getLocationLongitude(this);
		if (StringUtil.isEmpty(LocationUtils.getSelectedCity(this))) {
			cityId = LocationUtils.getLocationCityId(this);
		} else {
			cityId = LocationUtils.getSelectedCityId(this);
		}

		if (StringUtil.isEmpty(LocationUtils.getSelectedCity(this))) {
			cityName = LocationUtils.getLocationCity(this);
		} else {
			cityName = LocationUtils.getSelectedCity(this);
		}
		distractId = cityId;
	}

	private void initView() {
		storeName = this.getIntent().getStringExtra(EXTRA_SEARCH_RESULT_NAME);
		setLeftTitle("商家");
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		mStoreListView.setVisibility(View.GONE);
		mStoreListView.setMode(PullToRefreshBase.Mode.BOTH);
		mStoreListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				searchStoreData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				searchStoreData(true);
			}
		});
		mStoreListView.setOnItemClickListener(this);
		sortKey = "distance";
	}

	private void searchStoreData(boolean ismore) {
		if (ismore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", distractId);
			params.put("merchantCategory", categoryId);
			params.put("merchantLatitude", latitude);
			params.put("merchantLongitude", longitude);
			params.put("pageNum", mStoreAdapter.getPage() + 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", storeName);
			params.put("merchantAccountId", "");
			params.put("sort", sortKey);

			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_STORE_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);

		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", distractId);
			params.put("merchantCategory", categoryId);
			params.put("merchantLatitude", latitude);
			params.put("merchantLongitude", longitude);
			params.put("pageNum", 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("merchantName", storeName);
			params.put("merchantAccountId", "");
			params.put("sort", sortKey);

			requestHttpData(Constants.Urls.URL_GET_SEARCH_STORE_LIST,
					REQUEST_GET_SEARCH_STORE_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}

	}

	private void initSpinnerData() {
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
	protected void parseData(int requestCode, String data) {
		switch (requestCode) {
			case REQUEST_GET_SEARCH_STORE_CODE: {
				mStoreListView.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity searchStoreEntity = Parsers.getSearchStore(data);
					if (searchStoreEntity != null
							&& searchStoreEntity.getStoreEntityList() != null
							&& searchStoreEntity.getStoreEntityList().size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);
						mStoreListView.setVisibility(View.VISIBLE);
						mStoreAdapter = new StoreAdapter(this, searchStoreEntity.getStoreEntityList());
						mStoreAdapter.setIsLocation(latitude, longitude);
						mStoreListView.setAdapter(mStoreAdapter);
						if (mStoreAdapter.getPage() < searchStoreEntity.getTotalPage()) {
							mStoreListView.setCanAddMore(true);
						} else {
							mStoreListView.setCanAddMore(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
						mStoreListView.setVisibility(View.GONE);
					}

				} else {
					mStoreListView.setVisibility(View.GONE);
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			}
			case REQUEST_GET_SEARCH_STORE_CODE_MORE: {
				mStoreListView.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity searchStoreEntity = Parsers.getSearchStore(data);
					if (searchStoreEntity != null
							&& searchStoreEntity.getStoreEntityList() != null
							&& searchStoreEntity.getStoreEntityList().size() > 0) {

						mStoreAdapter.addDatas(searchStoreEntity.getStoreEntityList());
						if (mStoreAdapter.getPage() < searchStoreEntity.getTotalPage()) {
							mStoreListView.setCanAddMore(true);
						} else {
							mStoreListView.setCanAddMore(false);
						}
					}
				}
				break;
			}
			case REQUEST_GET_SEARCH_OPTION_CODE:
				if (data != null) {
					searchOptions = Parsers.getSearchOptions(data);
					if (searchOptions != null && searchOptions.getCategorys() != null) {
						for (int i = 0; i < searchOptions.getCategorys().size(); i++) {
							if (searchOptions.getCategorys().get(i).getId().equals(categoryId)) {
								categoryPosition = i;
							}
						}
					}
				}
				break;
		}
	}


	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		mStoreListView.onRefreshComplete();
		switch (requestCode) {
			case REQUEST_GET_SEARCH_STORE_CODE: {
				setLoadingStatus(LoadingStatus.RETRY);
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, StoreDetailActivity.class);
		intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, mStoreAdapter.getItem(position).getId());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.loading_layout: {
				setLoadingStatus(LoadingStatus.LOADING);
				mStoreListView.setVisibility(View.GONE);
				searchStoreData(false);
				break;
			}
			case R.id.store_list_address:
				checkDistanceSpinnerView.showSpinnerPop(storeListAddress, animation, currentCityDistrict, areaPosition1, areaPosition2);
				break;
			case R.id.store_list_category:
				if (searchOptions != null && searchOptions.getCategorys() != null) {
					checkSpinnerView.showSpinnerPop(storeListCategory, animation, searchOptions.getCategorys(), categoryPosition);
				}
				break;
			case R.id.store_list_hot:
				if (searchOptions != null && searchOptions.getOrderBys() != null) {

					mCheckSpinnerView.showSpinnerPop(storeListHot, animation, searchOptions.getOrderBys(), orderByPosition);
				}
				break;
		}
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
		storeListAddress.setText(this.getString(R.string.store_list_area));
		checkDistanceSpinnerView = new CheckDistanceSpinnerView(this, new CheckDistanceSpinnerView.OnSpinnerItemClickListener() {

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
				searchStoreData(false);
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
		storeListAddress.setOnClickListener(this);

		checkSpinnerView = new CheckSpinnerView(this, new CheckSpinnerView.OnSpinnerItemClickListener() {

			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
				categoryPosition = position;
				if (position == 0) {
					storeListCategory.setText(SearchResultActivity.this.getString(R.string.store_list_category));
				} else {
					storeListCategory.setText(searchOptions.getCategorys().get(position).getName());
				}
				categoryId = searchOptions.getCategorys().get(position).getId();
				checkSpinnerView.close();
				searchStoreData(false);
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
		storeListCategory.setOnClickListener(this);

		mCheckSpinnerView = new CheckSpinnerView(this, new CheckSpinnerView.OnSpinnerItemClickListener() {

			@Override
			public void onItemClickListener1(AdapterView<?> parent, View view, int position, long id) {
				orderByPosition = position;
				storeListHot.setText(searchOptions.getOrderBys().get(position).getName());
				sortKey = searchOptions.getOrderBys().get(position).getId();
				mCheckSpinnerView.close();
				searchStoreData(false);
				setLoadingStatus(LoadingStatus.LOADING);
			}
		});
		storeListHot.setOnClickListener(this);
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
		animation = new TranslateAnimation(0, 0, -(DeviceUtil.getHeight(this)), 0);
		animation.setDuration(100);
	}
}
