package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.network.entities.ProductEntity;
import com.wjika.client.store.adapter.StoreShowcaseAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;

/**
 * 商户橱窗
 * Created by Leo_Zhang on 2016/6/14.
 */
public class ProductCardActivity extends ToolBarActivity implements AdapterView.OnItemClickListener {

	@ViewInject(R.id.store_card_list)
	private ListView storeCardList;

	private String storeId;
	private StoreShowcaseAdapter storeShowcaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_product_card_act);
		ViewInjectUtils.inject(this);

		initView();
		initData();
	}

	private void initData() {
		storeId = getIntent().getStringExtra("storeId");
		ArrayList<ProductEntity> productList = (ArrayList<ProductEntity>) getIntent().getSerializableExtra("productList");
		if(productList != null && productList.size() > 0){
			storeShowcaseAdapter = new StoreShowcaseAdapter(this, productList);
			storeCardList.setAdapter(storeShowcaseAdapter);

		}
	}

	private void initView() {
		setLeftTitle(this.getString(R.string.store_showcase));
		storeCardList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, CardDetailActivity.class);
		intent.putExtra(CardDetailActivity.EXTRA_CARD_ID, storeShowcaseAdapter.getItem(position).getId());
		intent.putExtra(CardDetailActivity.EXTRA_CARD_NAME, storeShowcaseAdapter.getItem(position).getName());
		intent.putExtra(CardDetailActivity.EXTRA_FROM, CardDetailActivity.FROM_STORE_LIST);//须展示购买
		intent.putExtra(CardDetailActivity.EXTRA_BRANCH_MER_ID, storeId);
		intent.putExtra(CardDetailActivity.EXTRA_IS_MYCARD, false);
		intent.putExtra(CardDetailActivity.EXTRA_CARD_POSITION,position);
		startActivity(intent);
	}
}
