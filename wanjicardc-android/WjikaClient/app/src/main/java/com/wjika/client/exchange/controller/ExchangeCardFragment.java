package com.wjika.client.exchange.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.exchange.adapter.ExchangeListAdapter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeCardPageEntity;
import com.wjika.client.network.parser.Parsers;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/30.
 */

public class ExchangeCardFragment extends BaseFragment implements View.OnClickListener{

	private static final int REQUEEST_NET_CARDLIST = 1;
	private static final int REQUEEST_NET_CARDLIST_MORE = 2;

	private int status;
	private List<ExchangeCardEntity> mDatas;
	private FootLoadingListView listView;
	private ExchangeListAdapter adapter;
	private TextView currentQuota, residuaQuota;

	public void setArgs(int status, TextView currentQuota, TextView residualQuota) {
		this.status = status;
		this.currentQuota = currentQuota;
		this.residuaQuota = residualQuota;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exchange_card_fragment, null);
		initLoadingView(this, view);
		setLoadingStatus(LoadingStatus.LOADING);
		listView = (FootLoadingListView) view.findViewById(R.id.exchange_allcard_list);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			loadData(false);
		}
	}

	public void loadData(boolean isMore) {
		setLoadingStatus(LoadingStatus.LOADING);
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(getActivity()));
			params.put("thirdCardName", "");
			params.put("thirdCardSortId", String.valueOf(status));
			params.put(Constants.PAGENUM, String.valueOf(adapter.getPage() + 1));
			params.put(Constants.PAGESIZE, String.valueOf(Constants.PAGE_SIZE));
			requestHttpData(Constants.Urls.URL_POST_EXCHANGE_CARDLIST, REQUEEST_NET_CARDLIST_MORE, FProtocol.HttpMethod.POST, params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(getActivity()));
			params.put("thirdCardName", "");
			params.put("thirdCardSortId", String.valueOf(status));
			params.put(Constants.PAGENUM, String.valueOf(1));
			params.put(Constants.PAGESIZE, String.valueOf(Constants.PAGE_SIZE));
			requestHttpData(Constants.Urls.URL_POST_EXCHANGE_CARDLIST, REQUEEST_NET_CARDLIST, FProtocol.HttpMethod.POST, params);
		}
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		setLoadingStatus(LoadingStatus.GONE);
		listView.setOnRefreshComplete();
		if (UserCenter.isLogin(getActivity())) {
			ExchangeCardPageEntity exchangeCardPageEntity = Parsers.getExchangeCardPage(data);
			((AllCardActivity) getActivity()).setUrl(exchangeCardPageEntity.getResult().getUrl());
			mDatas = exchangeCardPageEntity.getResult().getEntities();
			currentQuota.setText(getString(R.string.exchange_your_limit, exchangeCardPageEntity.getCurrentQuota()));
			residuaQuota.setText(getString(R.string.exchange_current_limit, exchangeCardPageEntity.getResiduaQuota()));
			switch (requestCode) {
				case REQUEEST_NET_CARDLIST:
					if (null != mDatas && mDatas.size() > 0) {
						adapter = new ExchangeListAdapter(getActivity(), mDatas, (View.OnClickListener) getActivity());
						listView.setAdapter(adapter);
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
					if (exchangeCardPageEntity.getPageNum() < exchangeCardPageEntity.getPages()) {
						listView.setCanAddMore(true);
					} else {
						listView.setCanAddMore(false);
					}
					break;
				case REQUEEST_NET_CARDLIST_MORE:
					if (null != mDatas && mDatas.size() > 0) {
						adapter.addDatas(mDatas);
						listView.setAdapter(adapter);
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
					if (exchangeCardPageEntity.getPageNum() < exchangeCardPageEntity.getPages()) {
						listView.setCanAddMore(true);
					} else {
						listView.setCanAddMore(false);
					}
					break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		listView.setOnRefreshComplete();
		setLoadingStatus(LoadingStatus.RETRY);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.loading_layout:
				loadData(false);
				break;
		}
	}
}
