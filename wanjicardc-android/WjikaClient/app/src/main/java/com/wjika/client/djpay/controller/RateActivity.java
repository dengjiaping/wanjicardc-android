package com.wjika.client.djpay.controller;

import android.os.Bundle;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.adapter.DjRataAdapter;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DjpayRateChannelEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/22.
 * 斗金-费率
 */
public class RateActivity extends BaseDJActivity {

	private static final int REQUEST_NET_RATE = 0X01;

	@ViewInject(R.id.djpay_rate_list)
	private ListView rateList;

	private DjRataAdapter djRataAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.djpay_rate_act);
		ViewInjectUtils.inject(this);
		setLeftTitle("费率");
		selectBtn(BTN_CODE_RATE);
		loadData();
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", "");
		params.put("dj", "");
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_RATE,  REQUEST_NET_RATE, FProtocol.HttpMethod.POST, params);
	}


	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		closeProgressDialog();
		if (REQUEST_NET_RATE == requestCode) {
			List<DjpayRateChannelEntity> djpayRateChannelEntity = Parsers.getDjpayRateList(data);
			djRataAdapter = new DjRataAdapter(this,djpayRateChannelEntity);
			rateList.setAdapter(djRataAdapter);
		}
	}
}
