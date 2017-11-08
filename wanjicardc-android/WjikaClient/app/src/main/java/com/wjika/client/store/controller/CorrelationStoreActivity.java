package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.SearchStoreEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.SupportStoreAdapter;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Leo_Zhang on 2016/6/14.
 * 店铺
 */
public class CorrelationStoreActivity extends ToolBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final String EXTRA_FROM = "extra_from";
	public static final String EXTRA_MERID = "extra_merid";
	public static final int FROM_STORE_CORRELATION_ACTIVITY = 100;
	private static final int REQUEST_GET_BRANCH_STORE_CODE_MORE = 0x1;
	private static final int REQUEST_GET_BRANCH_STORE_CODE = 0x2;

	@ViewInject(R.id.store_list_correlation)
	private FootLoadingListView storeListCorrelation;

	private String merId;
	private String cityId;
	private SupportStoreAdapter storeListAdapter;
	private String latitude;
	private String longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_correlation_act);
		ViewInjectUtils.inject(this);
		initParams();
		initData(false);
		initView();
	}

	private void initParams() {
		if (getIntent() != null) {
			merId = getIntent().getStringExtra(EXTRA_MERID);
			latitude = LocationUtils.getLocationLatitude(this);
			longitude = LocationUtils.getLocationLongitude(this);
		}
		if (StringUtil.isEmpty(LocationUtils.getSelectedCityId(this))) {
			cityId = LocationUtils.getLocationCityId(this);
		} else {
			cityId = LocationUtils.getSelectedCityId(this);
		}
	}

	private void initView() {
		setLeftTitle(this.getString(R.string.card_detail_store_more));
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		storeListCorrelation.setVisibility(View.GONE);
		storeListCorrelation.setMode(PullToRefreshBase.Mode.BOTH);
		storeListCorrelation.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				initData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				initData(true);
			}
		});
		storeListCorrelation.setOnItemClickListener(this);
	}

	private void initData(boolean ismore) {

		if (ismore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", cityId);
			params.put("merchantId", merId);
			params.put("merchantLatitude", latitude);
			params.put("merchantLongitude", longitude);
			params.put("pageNum", storeListAdapter.getPage() + 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");

			requestHttpData(Constants.Urls.URL_GET_STORE_BRANCH_LIST,
					REQUEST_GET_BRANCH_STORE_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);

		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("areaId", cityId);
			params.put("merchantId", merId);
			params.put("merchantLatitude", latitude);
			params.put("merchantLongitude", longitude);
			params.put("pageNum", 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");

			requestHttpData(Constants.Urls.URL_GET_STORE_BRANCH_LIST,
					REQUEST_GET_BRANCH_STORE_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}

	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_GET_BRANCH_STORE_CODE:
				storeListCorrelation.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity searchStore = Parsers.getSearchStore(data);
					if (searchStore != null && searchStore.getStoreEntityList() != null && searchStore.getStoreEntityList().size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);
						storeListCorrelation.setVisibility(View.VISIBLE);
						storeListAdapter = new SupportStoreAdapter(this, searchStore.getStoreEntityList());
						storeListAdapter.setLocation(latitude, longitude);
						storeListCorrelation.setAdapter(storeListAdapter);
						if (searchStore.getTotalPage() > 1) {
							storeListCorrelation.setCanAddMore(true);
						} else {
							storeListCorrelation.setCanAddMore(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			case REQUEST_GET_BRANCH_STORE_CODE_MORE:
				storeListCorrelation.onRefreshComplete();
				if (data != null) {
					SearchStoreEntity searchStore = Parsers.getSearchStore(data);
					storeListAdapter.addDatas(searchStore.getStoreEntityList());
					if (searchStore.getPageNumber() < searchStore.getTotalPage()) {
						storeListCorrelation.setCanAddMore(true);
					} else {
						storeListCorrelation.setCanAddMore(false);
					}
				}
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		storeListCorrelation.onRefreshComplete();
		switch (requestCode) {
			case REQUEST_GET_BRANCH_STORE_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				storeListCorrelation.setVisibility(View.GONE);
				initData(false);
				break;
			case R.id.left_button:
				finish();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, StoreDetailActivity.class);
		intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, storeListAdapter.getItem(position).getId());
		intent.putExtra("extra_from", FROM_STORE_CORRELATION_ACTIVITY);
		startActivity(intent);
	}
}
