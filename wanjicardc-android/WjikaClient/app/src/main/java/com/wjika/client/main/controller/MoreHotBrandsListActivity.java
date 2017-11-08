package com.wjika.client.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.common.network.FProtocol;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.main.adapter.GridViewBrandsListAdapter;
import com.wjika.client.market.controller.ECardActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.MainBrandsEntity;
import com.wjika.client.network.entities.MainTopHalfPageEntity;
import com.wjika.client.network.parser.Parsers;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by ZHXIA on 2016/5/25
 * 热门品牌墙
 */
public class MoreHotBrandsListActivity extends ToolBarActivity implements OnItemClickListener, View.OnClickListener {

	private static final int REQUEST_MORE_BRAND_ITEM_CODE = 0x1;

	private GridView gvMoreBrands;

	private List<MainBrandsEntity> mainBrandItemlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_brands_more_act);
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData();
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.main_hot_brand));
		gvMoreBrands = (GridView) findViewById(R.id.main_gv_brans_more);
		mBtnTitleLeft.setVisibility(View.VISIBLE);
		ImageView leftButton = (ImageView) findViewById(R.id.left_button);
		leftButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		gvMoreBrands.setOnItemClickListener(this);
	}

	private void loadData() {
		IdentityHashMap<String, String> moreBrandParam = new IdentityHashMap<>();
		moreBrandParam.put("token", "");
		requestHttpData(Constants.Urls.URL_POST_HOME_TO_MORE_BRANDS, REQUEST_MORE_BRAND_ITEM_CODE, FProtocol.HttpMethod.POST, moreBrandParam);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if (data != null) {
			if (REQUEST_MORE_BRAND_ITEM_CODE == requestCode) {
				MainTopHalfPageEntity moreBrandsEntity = Parsers.getMainHalfTopInfo(data);
				if (moreBrandsEntity != null) {
					mainBrandItemlist = moreBrandsEntity.getBrandsList();
					setLoadingStatus(LoadingStatus.GONE);
					GridViewBrandsListAdapter brandsListAdapter = new GridViewBrandsListAdapter(this, mainBrandItemlist, true);
					gvMoreBrands.setAdapter(brandsListAdapter);
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		switch (requestCode) {
			case REQUEST_MORE_BRAND_ITEM_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MainBrandsEntity mainBrandsEntity = mainBrandItemlist.get(position);
		if (MainBrandsEntity.TYPE_OF_STORE == mainBrandsEntity.getType()) {//商家
			Intent intent = new Intent(this, OneBrandListActivity.class);
			intent.putExtra(OneBrandListActivity.MERCHANT_ACCOUNT_ID, mainBrandsEntity.getMerchantAccountId());
			intent.putExtra(OneBrandListActivity.BRAND_NAME, mainBrandsEntity.getBrandName());
			startActivity(intent);
		} else if (MainBrandsEntity.TYPE_OF_ECARD == mainBrandsEntity.getType()) {//电子卡
			Intent intent = new Intent(this, ECardActivity.class);
			intent.putExtra("brandId", mainBrandsEntity.getBrandId());
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
		}
	}
}
