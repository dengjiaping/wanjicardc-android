package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.cardpackage.controller.CardPkgDetailActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CardEntity;
import com.wjika.client.network.entities.CardPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.CardRechargeActivity;
import com.wjika.client.person.adapter.CardListAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/8/31 15:04.
 * 卡包充值
 */
public class CardPayActivity extends ToolBarActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

	static final int REQUEST_PERSON_CARD_CODE = 100;
	static final int REQUEST_PERSON_CARD_CODE_MORE = 101;

	@ViewInject(R.id.person_card_list)
	private FootLoadingListView pullToRefreshListView;
	@ViewInject(R.id.loading_img_empty)
	private ImageView loadingImgEmpty;
	@ViewInject(R.id.loading_txt_empty)
	private TextView loadingTxtEmpty;

	private CardListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_card_pay);
		initLoadingView(this);
		ViewInjectUtils.inject(this);
		initView();
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.person_card_recharge));

		pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
		pullToRefreshListView.setOnItemClickListener(this);
	}

	private void loadData(boolean ismore) {

        if (ismore){
            IdentityHashMap<String, String> params = new IdentityHashMap<String, String>();
            params.put("pageNum", adapter.getPage() + 1 + "");
            params.put("pageSize", Constants.PAGE_SIZE + "");
            params.put(Constants.TOKEN, UserCenter.getToken(this));
            requestHttpData(Constants.Urls.URL_GET_CARD_LIST,
                    REQUEST_PERSON_CARD_CODE_MORE,
                    FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET,
                    FProtocol.HttpMethod.POST,
                    params);
        }else {
            IdentityHashMap<String, String> params = new IdentityHashMap<String, String>();
            params.put("pageNum",  "1");
            params.put("pageSize", Constants.PAGE_SIZE + "");
            params.put(Constants.TOKEN, UserCenter.getToken(this));
            requestHttpData(Constants.Urls.URL_GET_CARD_LIST,
                    REQUEST_PERSON_CARD_CODE,
                    FProtocol.NetDataProtocol.DataMode.DATA_FROM_NET,
                    FProtocol.HttpMethod.POST,
                    params);
        }

	}

	@Override
	public void success(int requestCode, String data) {
		pullToRefreshListView.onRefreshComplete();
		super.success(requestCode, data);
	}

	@Override
	protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
		switch (requestCode){
			case REQUEST_PERSON_CARD_CODE: {
				CardPageEntity cardPageEntity = Parsers.getCardPage(data);
				if (cardPageEntity != null  && cardPageEntity.getCardEntities() != null && cardPageEntity.getCardEntities().size() > 0){
					adapter = new CardListAdapter(CardPayActivity.this, cardPageEntity.getCardEntities(), this);
					pullToRefreshListView.setAdapter(adapter);
					if (cardPageEntity.getTotalPage() > 1){
						pullToRefreshListView.setCanAddMore(true);
					}else {
						pullToRefreshListView.setCanAddMore(false);
					}
				}else {
					//空数据时不允许重复点击请求
					loadingImgEmpty.setImageResource(R.drawable.ic_empty_card);
					loadingTxtEmpty.setText(getString(R.string.person_card_nocard_topay));
					setLoadingStatus(LoadingStatus.EMPTY);
				}

				break;
			}

			case REQUEST_PERSON_CARD_CODE_MORE: {
				CardPageEntity cardPageEntity = Parsers.getCardPage(data);
				if (cardPageEntity != null  && cardPageEntity.getCardEntities() != null && cardPageEntity.getCardEntities().size() > 0) {
					adapter.addDatas(cardPageEntity.getCardEntities());
					if (cardPageEntity.getPageNum() < cardPageEntity.getTotalPage()){
						pullToRefreshListView.setCanAddMore(true);
					}else {
						pullToRefreshListView.setCanAddMore(false);
					}
				}
				break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		pullToRefreshListView.onRefreshComplete();
		super.mistake(requestCode, status, errorMessage);
		setLoadingStatus(LoadingStatus.RETRY);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.person_card_pay:
				if (UserCenter.isAuthencaiton(this)) {
					CardEntity cardEntity = (CardEntity) v.getTag();
					if (cardEntity != null){
						Intent intent = new Intent(this, CardRechargeActivity.class);
						intent.putExtra(CardRechargeActivity.EXTRA_MER_ID, cardEntity.getMerId());
						intent.putExtra(CardRechargeActivity.EXTRA_TYPE, "2");
						startActivity(intent);
					}
				} else {
					Intent authIntent = new Intent(this,AuthenticationActivity.class);
					authIntent.putExtra("from",AuthenticationActivity.FROM_BUY_CARD);
					startActivity(authIntent);
				}
			break;
			case R.id.loading_layout:
				loadData(false);
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, CardPkgDetailActivity.class);//店铺id
		intent.putExtra("id",adapter.getItem(position).getId());
		startActivity(intent);
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
