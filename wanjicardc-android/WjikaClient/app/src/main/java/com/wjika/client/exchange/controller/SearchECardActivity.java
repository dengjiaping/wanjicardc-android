package com.wjika.client.exchange.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.SearchBarActivity;
import com.wjika.client.exchange.adapter.ExchangeListAdapter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeCardPageEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputMethodUtil;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import static com.wjika.client.exchange.controller.AllCardActivity.EXTRA_EXCHANGE_CARD;

/**
 * Created by liuzhichao on 2016/11/29.
 * 卡兑换-搜索卡界面
 */
public class SearchECardActivity extends SearchBarActivity implements View.OnClickListener {

	private static final int REQUEST_NET_CARDLIST = 1;
	private static final int REQUEST_NET_CARDLIST_MORE = 2;
	private static final int REQUEST_NET_EXCHANGE_VALUELIST = 3;

	private FootLoadingListView searchEcardList;
	private String key;
	private ExchangeListAdapter adapter;
	private ExchangeCardEntity exchangeCardEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_search_ecard);
		initLoadingView(null);
		setLoadingStatus(LoadingStatus.GONE);
		initView();
	}

	private void initView() {
		searchEcardList = (FootLoadingListView) findViewById(R.id.search_ecard_list);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodUtil.showInput(SearchECardActivity.this, editSearch);
			}
		}, 100);
		editSearch.setHint("输入卡名称、拼音或者首字母查询");
		mTxtLoadingEmpty.setText("暂无数据,试试其他卡吧~");
		searchEcardList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		searchEcardList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
	}

	@Override
	protected void search() {
		key = editSearch.getText().toString().trim();
		if (StringUtil.isEmpty(key)) {
			return;
		}
		loadData(false);
	}

	private void loadData(boolean isMore) {
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("thirdCardName", key);
			params.put("thirdCardSortId", "0");
			params.put(Constants.PAGENUM, String.valueOf(adapter.getPage() + 1));
			params.put(Constants.PAGESIZE, String.valueOf(Constants.PAGE_SIZE));
			requestHttpData(Constants.Urls.URL_POST_EXCHANGE_CARDLIST, REQUEST_NET_CARDLIST_MORE, FProtocol.HttpMethod.POST, params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("thirdCardName", key);
			params.put("thirdCardSortId", "0");
			params.put(Constants.PAGENUM, String.valueOf(1));
			params.put(Constants.PAGESIZE, String.valueOf(Constants.PAGE_SIZE));
			requestHttpData(Constants.Urls.URL_POST_EXCHANGE_CARDLIST, REQUEST_NET_CARDLIST, FProtocol.HttpMethod.POST, params);
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		ExchangeCardPageEntity exchangeCardPageEntity = Parsers.getExchangeCardPage(data);
		List<ExchangeCardEntity> mDatas = exchangeCardPageEntity.getResult().getEntities();
		switch (requestCode) {
			case REQUEST_NET_CARDLIST:
				if (null != mDatas && mDatas.size() > 0) {
					adapter = new ExchangeListAdapter(this, mDatas, this);
					searchEcardList.setAdapter(adapter);
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				if (exchangeCardPageEntity.getPageNum() < exchangeCardPageEntity.getPages()) {
					searchEcardList.setCanAddMore(true);
				} else {
					searchEcardList.setCanAddMore(false);
				}
				break;
			case REQUEST_NET_CARDLIST_MORE:
				if (null != mDatas && mDatas.size() > 0) {
					adapter.addDatas(mDatas);
					searchEcardList.setAdapter(adapter);
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				if (exchangeCardPageEntity.getPageNum() < exchangeCardPageEntity.getPages()) {
					searchEcardList.setCanAddMore(true);
				} else {
					searchEcardList.setCanAddMore(false);
				}
				break;
			case REQUEST_NET_EXCHANGE_VALUELIST:
				ArrayList<ExchangeFacevalueEntity> facevalueEntities = Parsers.getExchangeFace(data);
				Intent intent = new Intent(this, ExchangeActivity.class);
				intent.putExtra(ExchangeActivity.EXTRA_CARD_LIST, facevalueEntities);
				intent.putExtra(EXTRA_EXCHANGE_CARD, exchangeCardEntity);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.exchange_carditem_exchange:
				exchangeCardEntity = (ExchangeCardEntity) v.getTag();
				loadData(exchangeCardEntity.getId());
				break;
		}
	}

	private void loadData(String id) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("exchangeCardNameId", id);
		requestHttpData(Constants.Urls.URL_POST_EXCHANGE_VALUELIST, REQUEST_NET_EXCHANGE_VALUELIST, FProtocol.HttpMethod.POST, params);
	}
}
