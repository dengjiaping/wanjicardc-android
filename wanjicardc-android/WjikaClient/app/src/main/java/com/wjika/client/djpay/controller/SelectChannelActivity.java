package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.adapter.DjChannleAdapter;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DjpayRateChannelEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by liuzhichao on 2016/11/23.
 * 斗金-选择支付通道
 */
public class SelectChannelActivity extends BaseDJActivity {

	private static final int REQUEST_NET_CHANNEL = 1;
	public static final String EXTRA_AMOUNT = "extra_amount";
	public static final String EXTRA_ID = "extra_id";

	@ViewInject(R.id.select_channel_amount)
	private TextView selectChannelAmount;
	@ViewInject(R.id.select_channel_list)
	private ListView selectChannelList;

	private double amount;
	private DjChannleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_select_channel);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle("选择支付通道");
		showBtn(false);
		amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 0);
		selectChannelAmount.setText(NumberFormatUtil.formatMoney(amount));
		selectChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DjpayRateChannelEntity item = adapter.getItem(position);
				if (item != null && item.getDjpayRateEntity0() != null) {
					Intent intent = new Intent(SelectChannelActivity.this, ScanPayActivity.class);
					intent.putExtra(EXTRA_ID, item.getDjpayRateEntity0().getId());
					intent.putExtra(EXTRA_AMOUNT, amount);
					startActivity(intent);
				}
			}
		});
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("dj", "");
		params.put(Constants.TOKEN, DJUserCenter.getToken(this));
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_RATE, REQUEST_NET_CHANNEL, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		if (REQUEST_NET_CHANNEL == requestCode) {
			List<DjpayRateChannelEntity> channelEntities = Parsers.getDjpayRateList(data);
			if (channelEntities != null && channelEntities.size() > 0) {
				adapter = new DjChannleAdapter(this, channelEntities);
				selectChannelList.setAdapter(adapter);
			}
		}
	}
}
