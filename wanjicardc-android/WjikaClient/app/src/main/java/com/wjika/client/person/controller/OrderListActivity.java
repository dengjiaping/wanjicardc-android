package com.wjika.client.person.controller;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardOrderEntity;
import com.wjika.client.network.entities.OrderPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.ECardPayResultActivity;
import com.wjika.client.person.adapter.OrderListAdapter;
import com.wjika.client.person.adapter.OrderStatusAdapter;
import com.wjika.client.person.entity.OrderStatusEntity;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/8/31 13:34.
 * 我的订单
 */
public class OrderListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final int FROM_ORDER_LIST = 300;
	private static final int REQUEST_ORDER_LIST_CODE = 100;
	private static final int REQUEST_ORDER_LIST_CODE_MORE = 101;
	private static final int REQUEST_INTENT_CANCEL_ORDER = 102;
	private static final int REQUEST_NET_ECARD_ORDER = 0x1;
	private static final int REQUEST_NET_ECARD_PAY = 0x2;
	private static final int REQUEST_ORDER_PAY_SUCCESS = 0x3;
	public static final int ALL_ORDER_LIST = 25;
	public static final int FINISH_ORDER_LIST = 1;
	public static final int CLOSES_ORDER_LIST = 2;
	public static final int STAY_ORDER_LIST = 3;
	public static final int ORDER_TYPE = 0;
	public static final int CARD_ORDER_LIST = 1;
	public static final int ELECTRON_ORDER_LIST = 4;
	public static final int BAOZI_ORDER_LIST = 3;

	@ViewInject(R.id.person_order_list)
	private FootLoadingListView personOrderList;
	@ViewInject(R.id.order_list_left_button)
	private ImageView orderListLeftButton;
	@ViewInject(R.id.order_list_left_text)
	private TextView orderListLeftText;
	@ViewInject(R.id.order_list_left_img)
	private ImageView orderListLeftImg;
	@ViewInject(R.id.order_list_left)
	private RelativeLayout orderListLeft;

	private OrderListAdapter orderListAdapter;
	private OrderPageEntity orderList;
	private OrderStatusAdapter orderStatusAdapter;
	private List<OrderStatusEntity> statusEntities;
	private List<OrderStatusEntity> typeEntities;
	private PopupWindow pop;

	private OrderStatusEntity selectedStatus;//定义选中的状态
	private OrderStatusAdapter orderTypeAdapter;
	private int buyNum;
	private Double salePrice;
	private String name;
	private String facevalue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_order_list);
		ViewInjectUtils.inject(this);
		initLoadingView(this);
		initView();
		initOrderStatusData();
		loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
		setLoadingStatus(LoadingStatus.LOADING);
	}

	private void initOrderStatusData() {
		/**
		 * 初始化订单状态的数据
		 */
		statusEntities = new ArrayList<>();
		OrderStatusEntity statusEntityAll = new OrderStatusEntity(true,"全部订单",ALL_ORDER_LIST,ORDER_TYPE);
		selectedStatus = statusEntityAll;
		statusEntities.add(statusEntityAll);
		OrderStatusEntity statusEntityStay = new OrderStatusEntity(false,"待支付订单",STAY_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityStay);
		OrderStatusEntity statusEntityClosed = new OrderStatusEntity(false,"已关闭订单",CLOSES_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityClosed);
		OrderStatusEntity statusEntityFinish = new OrderStatusEntity(false,"已完成订单",FINISH_ORDER_LIST,ORDER_TYPE);
		statusEntities.add(statusEntityFinish);

		/**
		 * 初始化订单类型的数据
		 */
		typeEntities = new ArrayList<>();
		OrderStatusEntity orderStatusStore = new OrderStatusEntity(false,"商家卡订单",ALL_ORDER_LIST,CARD_ORDER_LIST);
		typeEntities.add(orderStatusStore);
		OrderStatusEntity orderStatusElectron = new OrderStatusEntity(false,"电子卡订单",ALL_ORDER_LIST,ELECTRON_ORDER_LIST);
		typeEntities.add(orderStatusElectron);
		OrderStatusEntity orderStatusBaozi = new OrderStatusEntity(false,"包子充值订单",ALL_ORDER_LIST,BAOZI_ORDER_LIST);
		typeEntities.add(orderStatusBaozi);
	}

	public void loadData(boolean ismore,int status,int type) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		int code;
		if (ismore) {
			params.put("pageNum", orderListAdapter.getPage()+1 + "");
			code = REQUEST_ORDER_LIST_CODE_MORE;
		} else {
			params.put("pageNum", 1 + "");
			code = REQUEST_ORDER_LIST_CODE;
		}
			params.put("orderStatus", status + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("orderType",type + "");
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_POST_ORDER_LIST, code, FProtocol.HttpMethod.POST, params);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		setLoadingStatus(LoadingStatus.GONE);
		switch (requestCode){
			case REQUEST_ORDER_LIST_CODE:
				personOrderList.onRefreshComplete();
				if(data != null){
					orderList = Parsers.getOrderList(data);
					if(orderList != null && orderList.getOrderEntityList() != null && orderList.getOrderEntityList().size() > 0 ){
						List<OrderPageEntity.OrderEntity> orderEntityList = orderList.getOrderEntityList();
						orderListAdapter = new OrderListAdapter(this,orderEntityList,this);
						personOrderList.setAdapter(orderListAdapter);
						if(orderListAdapter.getPage() < orderList.getTotalPage()){
							personOrderList.setCanAddMore(true);
						}else {
							personOrderList.setCanAddMore(false);
						}
					}else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				}else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			case REQUEST_ORDER_LIST_CODE_MORE:
				personOrderList.onRefreshComplete();
				OrderPageEntity orderList = Parsers.getOrderList(data);
				if(orderList != null && orderList.getOrderEntityList() != null && orderList.getOrderEntityList().size() > 0){
					orderListAdapter.addDatas(orderList.getOrderEntityList());
					if(orderListAdapter.getPage() < orderList.getTotalPage()){
						personOrderList.setCanAddMore(true);
					}else {
						personOrderList.setCanAddMore(false);
					}
				}else {
					personOrderList.setCanAddMore(false);
				}
				break;
			case REQUEST_NET_ECARD_ORDER:
				ECardOrderEntity eCardOrder = Parsers.getECardOrder(data);
				if (eCardOrder != null) {
					String orderNo = eCardOrder.getOrderNo();
					if (salePrice * buyNum > eCardOrder.getBalance()) {
						Intent rechargeIntent = new Intent(this, RechargeDialogActivity.class);
						rechargeIntent.putExtra("from", "orderList");
						rechargeIntent.putExtra("orderName", name);
						rechargeIntent.putExtra("facePrice", facevalue);
						rechargeIntent.putExtra("orderNo", orderNo);
						rechargeIntent.putExtra("payAmount", salePrice * buyNum);
						rechargeIntent.putExtra("buyNum", buyNum);
						rechargeIntent.putExtra("walletCount", eCardOrder.getBalance());
						startActivity(rechargeIntent);
					} else {
						Intent payIntent = new Intent(this, PayDialogActivity.class);
						payIntent.putExtra("from", "orderList");
						payIntent.putExtra("orderName", name);
						payIntent.putExtra("facePrice",  facevalue);
						payIntent.putExtra("orderNo", orderNo);
						payIntent.putExtra("salePrice", salePrice);
						payIntent.putExtra("buyNum", buyNum);
						payIntent.putExtra("walletCount", eCardOrder.getBalance());
						startActivityForResult(payIntent,REQUEST_ORDER_PAY_SUCCESS);
					}
				}
				break;
			case REQUEST_NET_ECARD_PAY:
				Intent intent = new Intent(this, ECardPayResultActivity.class);
				intent.putExtra(ECardPayResultActivity.EXTRA_ECARD_NAME,name);
				intent.putExtra(ECardPayResultActivity.EXTRA_ECARD_FACE_VALUE,facevalue);
				intent.putExtra(ECardPayResultActivity.EXTRA_ECARD_SALE_VALE,salePrice);
				intent.putExtra(ECardPayResultActivity.EXTRA_FROM,FROM_ORDER_LIST);
				intent.putExtra(ECardPayResultActivity.EXTRA_NUM, buyNum);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (REQUEST_NET_ECARD_ORDER == requestCode) {
			ToastUtil.shortShow(this, errorMessage);
		} else {
			setLoadingStatus(LoadingStatus.RETRY);
			personOrderList.onRefreshComplete();
		}
	}

	private void initView() {
		orderListLeftText.setText("全部订单");
		orderListLeftImg.setVisibility(View.VISIBLE);
		personOrderList.setOnItemClickListener(this);
		orderListLeftButton.setOnClickListener(this);
		orderListLeft.setOnClickListener(this);
		personOrderList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case REQUEST_INTENT_CANCEL_ORDER:
					int orderPosition = data.getIntExtra("orderPosition", 0);
					int orderState = data.getIntExtra("orderState", 0);
					if(orderListAdapter.getCount() > orderPosition){
						orderListAdapter.getItem(orderPosition).setStatus(orderState);
						orderListAdapter.notifyDataSetChanged();
					}
					break;
				case REQUEST_ORDER_PAY_SUCCESS:
					loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
					break;
			}
		}
    }

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
				break;
			case R.id.order_list_left:
				AnalysisUtil.onEvent(this, "Android_act_allOrder");
				initPop();
				break;
			case R.id.electron_order_item_pay://再次购买。
				AnalysisUtil.onEvent(this, "Android_act_Buyagain");
				personOrderList.setEnabled(false);
				OrderPageEntity.OrderEntity orderEntity = (OrderPageEntity.OrderEntity)v.getTag();
				buyNum = orderEntity.getBuyNum();
				salePrice = orderEntity.getSalePrice();
				name = orderEntity.getName();
				facevalue = orderEntity.getFacevalue();

				if(UserCenter.isAuthencaiton(this)){
					IdentityHashMap<String, String> params = new IdentityHashMap<>();
					params.put("thirdCardId", String.valueOf(orderEntity.getPcid()));
					params.put("purchaseNum", String.valueOf(buyNum));
					params.put(Constants.TOKEN, UserCenter.getToken(this));
					requestHttpData(Constants.Urls.URL_POST_CREATE_ECARD_ORDER, REQUEST_NET_ECARD_ORDER, FProtocol.HttpMethod.POST, params);
				}else {
					startActivity(new Intent(this, AuthenticationActivity.class));
				}
				break;
			case R.id.order_list_left_button:
				finish();
				break;
		}

	}

	private void initPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.person_all_order_type,null);
		pop = new PopupWindow(view,
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		GridView orderStatus = (GridView) view.findViewById(R.id.order_status);
		GridView orderType = (GridView) view.findViewById(R.id.order_type);
		View orderTypeView = view.findViewById(R.id.order_type_view);

		orderStatusAdapter = new OrderStatusAdapter(this, statusEntities);
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


				for(int i=0; i<typeEntities.size(); i++){
					typeEntities.get(i).setStatus(false);
				}
				orderTypeAdapter.notifyDataSetChanged();
				pop.dismiss();
				loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
			}
		});


		orderTypeAdapter = new OrderStatusAdapter(this, typeEntities);
		orderType.setAdapter(orderTypeAdapter);
		orderType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for(int i=0; i<typeEntities.size(); i++){
					if(i == position){
						typeEntities.get(i).setStatus(true);
						selectedStatus = typeEntities.get(i);
					}else {
						typeEntities.get(i).setStatus(false);
					}
				}
				orderStatusAdapter.notifyDataSetChanged();

				for(int i=0; i<statusEntities.size(); i++){
					statusEntities.get(i).setStatus(false);
				}

				orderTypeAdapter.notifyDataSetChanged();
				pop.dismiss();
				loadData(false,selectedStatus.getStatusOrder(),selectedStatus.getTypeOrder());
			}
		});

		pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				orderListLeftText.setText(selectedStatus.getName());
				orderListLeftImg.setBackgroundResource(R.drawable.home_btn_location);
			}
		});
		pop.showAsDropDown(orderListLeft);
		orderListLeftImg.setBackgroundResource(R.drawable.icon_arrow_up);
		orderTypeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, OrderDetailActivity.class);
		intent.putExtra("type",orderList.getOrderEntityList().get(position).getType());
		intent.putExtra("cardOrderNo", orderListAdapter.getItem(position).getOrderNo());
		intent.putExtra("orderPosition", position);
		startActivityForResult(intent,REQUEST_INTENT_CANCEL_ORDER);
	}
}
