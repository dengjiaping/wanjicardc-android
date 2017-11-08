package com.wjika.client.exchange.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.exchange.adapter.ExchangePageAdapter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * 卡兑换所有卡界面
 * Created by zhaoweiwei on 2016/11/30.
 */

public class AllCardActivity extends BaseActivity implements View.OnClickListener{
	public static final int CLASSIFY_ALL = 0;//全部
	public static final int CLASSIFY_PHONE = 1;//话费
	public static final int CLASSIFY_GAME = 2;//游戏
	public static final int CLASSIFY_PREPAYMENT = 3;//预付费
	private static final int REQUEST_NET_EXCHANGE_VALUELIST = 4;
	public static final String EXTRA_EXCHANGE_CARD = "exchange_card";

	@ViewInject(R.id.main_iv_message_alert)
	private ImageView titleLeft;
	@ViewInject(R.id.iv_home_bum)
	private ImageView titleRight;
	@ViewInject(R.id.iv_home_cardpkg)
	private ImageView titleRightSecond;
	@ViewInject(R.id.tv_home_title)
	private TextView title;

	@ViewInject(R.id.exchange_allcard_credit)
	public TextView allcardCredit;
	@ViewInject(R.id.exchange_allcard_userable)
	public TextView allcardUsable;
	@ViewInject(R.id.exchange_allcard_group)
	private RadioGroup radioGroup;
	@ViewInject(R.id.exchange_allcard_viewpager)
	private ViewPager exchangeViewpage;

	private ArrayList<ExchangeFacevalueEntity> mDatas;
	private ExchangeCardEntity entity;
	private String url;
	private boolean isEnable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_all_card_act);
		ViewInjectUtils.inject(this);
		initTitle();
		initFragment();
	}

	private void initTitle() {
		titleLeft.setImageResource(R.drawable.ic_back);
		title.setAlpha(1.0f);
		title.setText(getString(R.string.exchange_can_exchange));
		titleRight.setImageResource(R.drawable.exchange_all_search);
		titleRightSecond.setImageResource(R.drawable.exchange_all_what);
		titleLeft.setOnClickListener(this);
		titleRight.setOnClickListener(this);
		titleRightSecond.setOnClickListener(this);
	}

	private void initFragment() {
		ExchangeCardFragment allFragment = new ExchangeCardFragment();
		allFragment.setArgs(CLASSIFY_ALL,allcardCredit,allcardUsable);
		ExchangeCardFragment phoneFragment = new ExchangeCardFragment();
		phoneFragment.setArgs(CLASSIFY_PHONE,allcardCredit,allcardUsable);
		ExchangeCardFragment gameFragment = new ExchangeCardFragment();
		gameFragment.setArgs(CLASSIFY_GAME,allcardCredit,allcardUsable);
		/*ExchangeCardFragment prepayFragment = new ExchangeCardFragment(this);
		prepayFragment.setArgs(this, CLASSIFY_PREPAYMENT);*/
		List<ExchangeCardFragment> fragments = new ArrayList<>();
		fragments.add(allFragment);
		fragments.add(phoneFragment);
		fragments.add(gameFragment);
//		fragments.add(prepayFragment);
		ExchangePageAdapter adapter = new ExchangePageAdapter(getSupportFragmentManager(), fragments);
		exchangeViewpage.setAdapter(adapter);
		exchangeViewpage.setOnPageChangeListener(new MyPageChangeListener());
		radioGroup.setOnCheckedChangeListener(new MyGroupCheckChanged());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.main_iv_message_alert:
				finish();
				break;
			case R.id.iv_home_bum:
				startActivity(new Intent(this,SearchECardActivity.class));
				break;
			case R.id.iv_home_cardpkg:
				if (!StringUtil.isEmpty(url)) {
					startActivity(new Intent(this, WebViewActivity.class).putExtra(WebViewActivity.EXTRA_URL, url).putExtra(WebViewActivity.EXTRA_TITLE, "活动说明"));
				}
				break;
			case R.id.exchange_carditem_exchange:
				if (isEnable) {
					isEnable = false;
					showProgressDialog();
					entity = (ExchangeCardEntity) view.getTag();
					loadData(entity);
					isEnable = true;
				}
				break;
		}
	}
	private void loadData(ExchangeCardEntity entity) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("exchangeCardNameId", String.valueOf(entity.getId()));
		requestHttpData(Constants.Urls.URL_POST_EXCHANGE_VALUELIST, REQUEST_NET_EXCHANGE_VALUELIST, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		closeProgressDialog();
		mDatas = Parsers.getExchangeFace(data);
		Intent intent = new Intent(AllCardActivity.this, ExchangeActivity.class);
		intent.putExtra(ExchangeActivity.EXTRA_CARD_LIST,mDatas);
		intent.putExtra(EXTRA_EXCHANGE_CARD,entity);
		startActivity(intent);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			switch (position) {
				case 0:
					radioGroup.check(R.id.exchange_allcard_all);
					break;
				case 1:
					radioGroup.check(R.id.exchange_allcard_phone);
					break;
				case 2:
					radioGroup.check(R.id.exchange_allcard_game);
					break;
				case 3:
					radioGroup.check(R.id.exchange_allcard_prepayment);
					break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	private class MyGroupCheckChanged implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int i) {
			switch (i) {
				case R.id.exchange_allcard_all:
					exchangeViewpage.setCurrentItem(0);
					break;
				case R.id.exchange_allcard_phone:
					exchangeViewpage.setCurrentItem(1);
					break;
				case R.id.exchange_allcard_game:
					exchangeViewpage.setCurrentItem(2);
					break;
				case R.id.exchange_allcard_prepayment:
					exchangeViewpage.setCurrentItem(3);
					break;
			}
		}
	}
}
