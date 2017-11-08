package com.wjika.client.person.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ConsumptionPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.adapter.ConsumptionListAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/8/31 14:57.
 * 消费记录
 */
public class ConsumptionHistoryActivity extends ToolBarActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnClickListener {

	static final int REQUEST_PERSON_CONSUMPTION_CODE = 100;
	static final int REQUEST_PERSON_CONSUMPTION_CODE_MORE = 101;

	@ViewInject(R.id.person_consumption_list)
	private FootLoadingListView pullToRefreshListView;

	private ConsumptionListAdapter adapter;
	private String merId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_consumption);

		initLoadingView(this);
		ViewInjectUtils.inject(this);
		initView();
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		merId = getIntent().getStringExtra("merId");
		if (TextUtils.isEmpty(merId)) {
			setLeftTitle(res.getString(R.string.person_consumption_history));
		} else {
			setLeftTitle(getString(R.string.person_consumption_record));
		}
		pullToRefreshListView.setOnRefreshListener(this);
	}

	private void loadData(boolean ismore) {
		if (ismore){
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put("merchantId", merId);
			params.put("pageSize",Constants.PAGE_SIZE + "");
			params.put("pageNum",adapter.getPage() + 1 + "");
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_GET_CONSUMPTION_LIST, REQUEST_PERSON_CONSUMPTION_CODE_MORE, FProtocol.HttpMethod.POST, params);

		}else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put("merchantId", merId);
			params.put("pageSize",Constants.PAGE_SIZE + "");
			params.put("pageNum","1");
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_GET_CONSUMPTION_LIST, REQUEST_PERSON_CONSUMPTION_CODE, FProtocol.HttpMethod.POST, params);

		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
		pullToRefreshListView.onRefreshComplete();
		switch (requestCode){
			case REQUEST_PERSON_CONSUMPTION_CODE:
			{
				ConsumptionPageEntity consumption = Parsers.getConsumptionList(data);
				if (consumption != null  && consumption.getConsumptionEntityList() != null
						&& consumption.getConsumptionEntityList().size() > 0){
					adapter = new ConsumptionListAdapter(this, consumption.getConsumptionEntityList());
					pullToRefreshListView.setAdapter(adapter);
					if (consumption.getPageNumber() < consumption.getPages()){
						pullToRefreshListView.setCanAddMore(true);
					}else {
						pullToRefreshListView.setCanAddMore(false);
					}
				}else {
					//空数据时不允许重复点击请求
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			}
			case REQUEST_PERSON_CONSUMPTION_CODE_MORE:
			{
				ConsumptionPageEntity consumption = Parsers.getConsumptionList(data);
				if (consumption != null  && consumption.getConsumptionEntityList() != null
						&& consumption.getConsumptionEntityList().size() > 0){
					adapter.addDatas(consumption.getConsumptionEntityList());
					if (consumption.getPageNumber() < consumption.getPages()){
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
		switch (requestCode){
			case REQUEST_PERSON_CONSUMPTION_CODE:
			{
				setLoadingStatus(LoadingStatus.RETRY);
				break;
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(true);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
            case R.id.loading_layout:
            {
                setLoadingStatus(LoadingStatus.LOADING);
                loadData(false);
            }

          /*  case R.id.right_button:
            {
                final SpannableStringBuilder ssbAmount = new SpannableStringBuilder(res.getString(R.string.person_consumption_info));
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_gray)), 0, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_middle_gray)), 5, 12, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_gray)), 13, 18, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_middle_gray)), 18, 26, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_gray)), 26, 31, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_middle_gray)), 31, 67, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_gray)), 67, 72, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssbAmount.setSpan(new ForegroundColorSpan(res.getColor(R.color.wjika_client_middle_gray)), 72, 81, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                showAlertDialog(null,
                        ssbAmount,
                        res.getString(R.string.wjk_alert_know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeDialog();
                            }
                        });
                break;
            }*/
        }
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
