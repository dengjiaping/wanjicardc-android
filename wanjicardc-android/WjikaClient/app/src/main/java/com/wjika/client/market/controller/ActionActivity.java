package com.wjika.client.market.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.adapter.ActionListAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ActionListEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/8/15.
 */
public class ActionActivity extends ToolBarActivity implements View.OnClickListener{

	private static final int REQUEST_CODE_ACTION_LIST = 0x10;
	private static final int REQUEST_CODE_ACTION_LIST_MORE = 0x11;

	@ViewInject(R.id.action_listView)
	private FootLoadingListView actionListView;

	private ActionListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_list);
		ViewInjectUtils.inject(this);
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		setLeftTitle(getString(R.string.market_edit_recommend));
		actionListView.setMode(PullToRefreshBase.Mode.BOTH);
		actionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

	private void loadData(boolean loadMore) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		String pageNum = String.valueOf(1);
		int requestCode = REQUEST_CODE_ACTION_LIST;
		if (loadMore) {
			pageNum = String.valueOf(adapter.getPage() + 1);
			requestCode = REQUEST_CODE_ACTION_LIST_MORE;
		}
		params.put("pageNum", pageNum);
		params.put("pageSize", String.valueOf(Constants.PAGE_SIZE));
		requestHttpData(Constants.Urls.URL_ACTION_LIST, requestCode, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if (REQUEST_CODE_ACTION_LIST == requestCode) {
			actionListView.onRefreshComplete();
			if (data != null) {
				ActionListEntity actionList = Parsers.getActionListEntity(data);
				if (actionList != null && actionList.getActionList() != null && actionList.getActionList().size() > 0) {
					setLoadingStatus(LoadingStatus.GONE);
					actionListView.setCanAddMore(actionList.isHasmore());
					adapter = new ActionListAdapter(this, actionList.getActionList());
					actionListView.setAdapter(adapter);
					actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Intent intent = new Intent(ActionActivity.this, WebViewActivity.class);
							intent.putExtra(WebViewActivity.EXTRA_URL, adapter.getItem(position).getLinkUrl());
							intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.action_detail_title));
							startActivity(intent);
						}
					});
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}

			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}

		} else if (REQUEST_CODE_ACTION_LIST_MORE == requestCode) {
			actionListView.onRefreshComplete();
			if (data != null) {
				ActionListEntity actionList = Parsers.getActionListEntity(data);
				adapter.addDatas(actionList.getActionList());
				actionListView.setCanAddMore(actionList.isHasmore());
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		actionListView.onRefreshComplete();
		if (REQUEST_CODE_ACTION_LIST == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_Event");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		}
	}
}
