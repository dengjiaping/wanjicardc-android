package com.wjika.client.message.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.message.adapter.ActionMessageListAdapter;
import com.wjika.client.message.adapter.ConsumeMessageListAdapter;
import com.wjika.client.message.adapter.MyAssetListAdapter;
import com.wjika.client.message.adapter.SystemMessageListAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.MessageCenterEntity;
import com.wjika.client.network.entities.MyAssetEntity;
import com.wjika.client.network.entities.MyAssetListEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by bob on 2016/5/23 0023.
 * 消息列表
 */
public class MessageListActivity extends ToolBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

	public static final String EXTRAS_MESSAGE_TYPE = "extras_message_type";
	private static final int REQUEST_GET_MESSAGE_LIST_CODE = 0x1;
	private static final int REQUEST_GET_MESSAGE_LIST_CODE_MORE = 0x2;

	@ViewInject(R.id.message_center_listView)
	private FootLoadingListView mListView;
	@ViewInject(R.id.toolbar_left_title)
	private TextView tvLeftTitle;
	@ViewInject(R.id.left_button)
	private ImageView ivLeftBtn;

	private String type = "";
	private BaseAdapterNew mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_center_list);
		ViewInjectUtils.inject(this);

		type = getIntent().getStringExtra(EXTRAS_MESSAGE_TYPE);
		initView();

		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		mListView.setVisibility(View.GONE);
		loadData(false);
	}

	private void initView() {
		ivLeftBtn.setOnClickListener(this);
		tvLeftTitle.setText(getTitleByType());

		mListView.setOnItemClickListener(this);
		mListView.setMode(PullToRefreshBase.Mode.BOTH);
		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
		params.put("type", type);
		String pageNum = String.valueOf(1);
		int requestCode = REQUEST_GET_MESSAGE_LIST_CODE;
		if (loadMore) {
			pageNum = String.valueOf(mAdapter.getPage() + 1);
			requestCode = REQUEST_GET_MESSAGE_LIST_CODE_MORE;
		}
		params.put("pageNum", pageNum);
		params.put("pageSize", String.valueOf(Constants.PAGE_SIZE));
		requestHttpData(Constants.Urls.URL_MESSAGE_LIST, requestCode, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		if (REQUEST_GET_MESSAGE_LIST_CODE == requestCode) {
			mListView.setOnRefreshComplete();
			if (data != null) {
				MyAssetListEntity male = Parsers.getMessageList(data);

				if (male != null && male.getList() != null && male.getList().size() > 0) {
					setLoadingStatus(LoadingStatus.GONE);
					mListView.setVisibility(View.VISIBLE);
					updateUI(male.getList(), male.getPages());
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}
		} else if (REQUEST_GET_MESSAGE_LIST_CODE_MORE == requestCode) {
			mListView.setOnRefreshComplete();
			if (data != null) {
				MyAssetListEntity male = Parsers.getMessageList(data);
				mAdapter.addDatas(male.getList());
				if (mAdapter.getPage() < male.getPages()) {
					mListView.setCanAddMore(true);
				} else {
					mListView.setCanAddMore(false);
				}
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		mListView.onRefreshComplete();
		if (REQUEST_GET_MESSAGE_LIST_CODE == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
		}
	}

	/**
	 * 1.系统 2.活动 3.我的资产 4.消费
	 */
	private void updateUI(List<MyAssetEntity> list, int totalPages) {
		if (MessageCenterEntity.TYPE_SYSTEM_MESSAGE.equals(type)) {
			mAdapter = new SystemMessageListAdapter(this, list);
		} else if (MessageCenterEntity.TYPE_ACTION_MESSAGE.equals(type)) {
			mAdapter = new ActionMessageListAdapter(this, list);
		} else if (MessageCenterEntity.TYPE_ASSET_MESSAGE.equals(type)) {
			mAdapter = new MyAssetListAdapter(this, list);
		} else {
			mAdapter = new ConsumeMessageListAdapter(this, list);
		}
		if (mAdapter.getPage() < totalPages) {
			mListView.setCanAddMore(true);
		} else {
			mListView.setCanAddMore(false);
		}
		mListView.setAdapter(mAdapter);
	}

	private String getTitleByType() {
		String title;
		if (MessageCenterEntity.TYPE_SYSTEM_MESSAGE.equals(type)) {
			title = getStringById(R.string.message_center_system);
		} else if (MessageCenterEntity.TYPE_ACTION_MESSAGE.equals(type)) {
			title = getStringById(R.string.message_center_action);
		} else if (MessageCenterEntity.TYPE_ASSET_MESSAGE.equals(type)) {
			title = getStringById(R.string.message_center_asset);
		} else {
			title = getStringById(R.string.message_center_consume);
		}
		return title;
	}

	private String getStringById(int id) {
		return getResources().getString(id);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.left_button) {
			this.finish();
		} else if (id == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (MessageCenterEntity.TYPE_ACTION_MESSAGE.equals(type)) {
			MyAssetEntity mae = ((ActionMessageListAdapter) mAdapter).getItem(position);
			if (mae != null) {
				Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, mae.getActivityUrl());
				intent.putExtra(WebViewActivity.EXTRA_TITLE, mae.getTheme());
				startActivity(intent);
			}
		}
	}
}
