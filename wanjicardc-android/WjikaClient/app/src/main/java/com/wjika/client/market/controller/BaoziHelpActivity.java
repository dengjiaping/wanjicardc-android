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
public class BaoziHelpActivity extends ToolBarActivity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baozi_help);
		initView();
	}

	private void initView() {
		setLeftTitle("帮助");
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_Event");
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.toolbar_left_title) {
			this.finish();
		}
	}
}
