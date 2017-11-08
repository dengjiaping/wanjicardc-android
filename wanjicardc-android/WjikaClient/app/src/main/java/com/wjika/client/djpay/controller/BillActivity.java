package com.wjika.client.djpay.controller;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.adapter.DjBillListAdapter;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DjBillEntity;
import com.wjika.client.network.entities.DjBillPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.adapter.OrderStatusAdapter;
import com.wjika.client.person.entity.OrderStatusEntity;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/23.
 * 斗金-账单
 */
public class BillActivity extends BaseDJActivity implements View.OnClickListener{

	public static final int ALL_ORDER_LIST = 10;
	public static final int FINISH_ORDER_LIST = 1;
	public static final int STAY_ORDER_LIST = 0;
	public static final int ORDER_TYPE = 3;
	private static final int REQUEST_NET_BILL = 0X10;
	private static final int REQUEST_NET_BILL_MORE = 0X20;

	@ViewInject(R.id.djpay_billlist)
	private FootLoadingListView billList;

	private List<DjBillEntity> djBillEntity;
	private DjBillListAdapter djBillListAdapter;

	private OrderStatusEntity selectedStatus;//定义选中的状态
	private List<OrderStatusEntity> statusEntities;

	private int status = ALL_ORDER_LIST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.djpay_account_billlist);
		ViewInjectUtils.inject(this);
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false, status);
		initView();
	}

	private void initView() {
		setClickCenterTitle("账单");
		selectBtn(BTN_CODE_ACCOUNT);
		toolbarTitleCenter.setOnClickListener(this);
		initTitleDatas();

		billList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false,status);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true,status);
			}
		});
	}

	private void loadData(boolean isMore, int status) {
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, DJUserCenter.getToken(this));
			params.put("dj", "");
			params.put(Constants.PAGENUM,String.valueOf(djBillListAdapter.getPage()+1));
			params.put(Constants.PAGESIZE, Integer.toString(Constants.PAGE_SIZE));
			params.put("status",String.valueOf(status));
			requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BILL_LIST,  REQUEST_NET_BILL_MORE, FProtocol.HttpMethod.POST, params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, DJUserCenter.getToken(this));
			params.put("dj", "");
			params.put(Constants.PAGENUM,String.valueOf(1));
			params.put(Constants.PAGESIZE, Integer.toString(Constants.PAGE_SIZE));
			params.put("status",String.valueOf(status));
			requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BILL_LIST ,  REQUEST_NET_BILL, FProtocol.HttpMethod.POST, params);
		}
	}


	@Override
	public void success(int requestCode, String data) {
		setLoadingStatus(LoadingStatus.GONE);
		billList.setOnRefreshComplete();
		DjBillPageEntity djBillPageEntity = Parsers.getDjpayBillList(data);
		djBillEntity = djBillPageEntity.getDjBillEntity();
		if (djBillEntity != null && djBillEntity.size() > 0) {
			switch (requestCode) {
				case REQUEST_NET_BILL:
					djBillListAdapter = new DjBillListAdapter(this,djBillEntity);
					billList.setAdapter(djBillListAdapter);
					if (djBillPageEntity.getPageNum() < djBillPageEntity.getPages()) {
						billList.setCanAddMore(true);
					} else {
						billList.setCanAddMore(false);
					}
					break;
				case REQUEST_NET_BILL_MORE:
					djBillListAdapter.addDatas(djBillEntity);
					billList.setAdapter(djBillListAdapter);
					if (djBillPageEntity.getPageNum() < djBillPageEntity.getPages()) {
						billList.setCanAddMore(true);
					} else {
						billList.setCanAddMore(false);
					}
					break;
			}
		} else {
			setLoadingStatus(LoadingStatus.EMPTY);
		}



	}


	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
	}

	private void initTitleDatas() {
		statusEntities = new ArrayList<>();
		OrderStatusEntity statusEntityAll = new OrderStatusEntity(true,"全部订单",ALL_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityAll);
		OrderStatusEntity statusEntityStay = new OrderStatusEntity(false,"待支付订单",STAY_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityStay);
		OrderStatusEntity statusEntityFinish = new OrderStatusEntity(false,"已成功订单",FINISH_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityFinish);
		selectedStatus = statusEntityAll;
	}

	private void initPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.person_all_order_type,null);
		TextView orderType = (TextView) view.findViewById(R.id.order_type_name);
		orderType.setVisibility(View.GONE);
		final PopupWindow pop = new PopupWindow(view,
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		GridView orderStatus = (GridView) view.findViewById(R.id.order_status);
		final OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(this, statusEntities);
		orderStatus.setAdapter(orderStatusAdapter);
		orderStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for(int i=0; i<statusEntities.size(); i++){
					if(i == position){
						statusEntities.get(i).setStatus(true);
						selectedStatus = statusEntities.get(i);
					}else {
						statusEntities.get(i).setStatus(false);
					}
				}
				orderStatusAdapter.notifyDataSetChanged();
				pop.dismiss();
				loadData(false,selectedStatus.getStatusOrder());
			}
		});
		pop.showAsDropDown(mTitlebar);
		toolbarTitleCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_up, 0);

		pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				toolbarTitleCenter.setText(selectedStatus.getName());
				toolbarTitleCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.home_btn_location, 0);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.toolbar_title:
				initPop();
				break;
			case R.id.loading_layout:
				loadData(false,status);
				break;
		}
	}
}
