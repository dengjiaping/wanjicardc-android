package com.wjika.client.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.common.utils.StringUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.store.controller.StoreListFragment;
import com.wjika.client.utils.LocationUtils;

/**
 * Created by ZHXIA on 2016/6/12
 * 首页类别到商家类别列表
 */
public class CategoryStoreListActivity extends BaseActivity implements BDLocationListener {

	public static final String BROADCAST_ACTION_LOACTION_OK = "com.wjk.loaction.ok";
	public static final int BROADCAST_LOCATION_STATUS_FAILED = 0x1;
	public static final int BROADCAST_LOCATION_STATUS_SUCCESS = 0x2;
	public static final int BROADCAST_LOCATION_STATUS_NO_ACTIVE = 0x3;
	public static final int BROADCAST_LOCATION_NO_GPS = 0x6;
	public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
	public static final String EXTRA_CATEGORY_ID = "extra_category_id";
	public static final int STORE_LIST_ACTIVITY = 1;
	public static final String ALL_MERCHANT = "商家列表";
	private String category_id = "";
	private String category_name = "";
	private String all_merchant_tag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isHasFragment = true;
		setContentView(R.layout.home_morestore);
		initView();
		StoreListFragment store_list_fragment = new StoreListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("categoryID", category_id);
		bundle.putString("categoryName", category_name);
		bundle.putInt("from", STORE_LIST_ACTIVITY);
		bundle.putString("all_merchant_tag", all_merchant_tag);
		store_list_fragment.setArguments(bundle);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tc = fm.beginTransaction();
		tc.add(R.id.ll_content, store_list_fragment);
		tc.commit();
	}

	private void initView() {
		if (getIntent() != null) {
			category_id = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
			category_name = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
			all_merchant_tag = getIntent().getStringExtra(ALL_MERCHANT);
		}
	}

	//发广播，解决商家商圈问题
	@Override
	public void onReceiveLocation(BDLocation bdLocation) {

		String locationCityName = bdLocation.getAddress().city;
		if (StringUtil.isEmpty(locationCityName)) {
			if (LocationUtils.isGPSOpen(this)) {
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_STATUS_FAILED);
				LocalBroadcastManager.getInstance(CategoryStoreListActivity.this).sendBroadcast(intent);
			} else {
				Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
				intent.putExtra("status", BROADCAST_LOCATION_NO_GPS);
				LocalBroadcastManager.getInstance(CategoryStoreListActivity.this).sendBroadcast(intent);
			}
		} else if (!StringUtil.isEmpty(CityDBManager.getAvailableCityId(this, locationCityName))) { //定位成功，并且定位城市也开通了服务
			Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
			intent.putExtra("status", BROADCAST_LOCATION_STATUS_SUCCESS);
			LocalBroadcastManager.getInstance(CategoryStoreListActivity.this).sendBroadcast(intent);
		} else {
			Intent intent = new Intent(BROADCAST_ACTION_LOACTION_OK);
			intent.putExtra("status", BROADCAST_LOCATION_STATUS_NO_ACTIVE);
			LocalBroadcastManager.getInstance(CategoryStoreListActivity.this).sendBroadcast(intent);
		}
	}
}
