package com.wjika.client.message.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.message.adapter.MessageCenterListAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.MessageCenterEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by bob on 2016/5/23 0023.
 * 消息中心
 */
public class MessageCenterActivity extends ToolBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

	private static final int REQUEST_GET_MESSAGE_LIST_CODE = 0x1;
	public static final int FROM_LOGIN = 10;
	public static final String EXTRA_FROM = "extra_from";

	@ViewInject(R.id.message_center_listView)
	private FootLoadingListView mListView;
	@ViewInject(R.id.toolbar_left_title)
	private TextView tvLeftTitle;
	@ViewInject(R.id.left_button)
	private ImageView ivLeftBtn;

	private MessageCenterListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_center_list);
		ViewInjectUtils.inject(this);
		initView();

		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		mListView.setVisibility(View.GONE);

		int from = getIntent().getIntExtra(EXTRA_FROM, -1);
		if (FROM_LOGIN == from) {
			if (!UserCenter.isAuthencaiton(this)) {
				showAlertDialog(null, "为保护您的账户安全请尽快实名认证！", "取消", "立即实名", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
						startActivity(new Intent(MessageCenterActivity.this, AuthenticationActivity.class));
					}
				});
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	private void initView() {
		ivLeftBtn.setOnClickListener(this);
		tvLeftTitle.setText(getResources().getString(R.string.message_center));

		mListView.setOnItemClickListener(this);
		mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
	}

	private void loadData() {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_MESSAGE_CENTER, REQUEST_GET_MESSAGE_LIST_CODE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void noLogin() {
		this.finish();
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		if (REQUEST_GET_MESSAGE_LIST_CODE == requestCode) {
			mListView.onRefreshComplete();
			if (!StringUtil.isEmpty(data)) {
				List<MessageCenterEntity> list = Parsers.getMessageEntity(data);
				if (list != null && list.size() > 0) {
					setLoadingStatus(LoadingStatus.GONE);
					mListView.setVisibility(View.VISIBLE);
					mAdapter = new MessageCenterListAdapter(this, list);
					mListView.setAdapter(mAdapter);
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MessageCenterEntity messageCenterEntity = mAdapter.getItem(position);
		if (messageCenterEntity != null) {
			Intent intent = new Intent(this, MessageListActivity.class);
			intent.putExtra(MessageListActivity.EXTRAS_MESSAGE_TYPE, messageCenterEntity.getType());
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (R.id.left_button == id) {
			this.finish();
		} else if (id == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
			loadData();
		}
	}
}
