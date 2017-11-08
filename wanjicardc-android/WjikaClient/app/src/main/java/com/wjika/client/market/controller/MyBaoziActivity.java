package com.wjika.client.market.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.adapter.MyBaoziListAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.BaoziTransRecordsEntity;
import com.wjika.client.network.entities.MyBaoziEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.person.controller.OrderDetailActivity;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by bob on 2016/8/16.
 */
public class MyBaoziActivity extends BaseActivity implements View.OnClickListener {

	private static final int REQUEST_CODE_TRANS_RECORDS_LIST = 0x10;
	private static final int REQUEST_CODE_TRANS_RECORDS_LIST_MORE = 0x11;

	@ViewInject(R.id.listView)
	private FootLoadingListView listView;
	@ViewInject(R.id.ll_user_baozi)
	private View llUserBaozi;
	@ViewInject(R.id.ll_baozi_recharge)
	private View llBaoziRecharge;
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.iv_help)
	private ImageView ivHelp;
	@ViewInject(R.id.tv_baozi_count)
	private TextView tvBaoziCount;
	@ViewInject(R.id.tv_usable_baozi)
	private TextView tvUseableBaozi;

	private MyBaoziListAdapter adapter;
	private MyBaoziEntity myBaozi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_my_baozi);
		ViewInjectUtils.inject(this);
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	@Override
	protected void noLogin() {
		this.finish();
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		ExitManager.instance.toActivity(MainActivity.class);
	}

	private void initView() {
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});

		llUserBaozi.setOnClickListener(this);
		llBaoziRecharge.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		ivHelp.setOnClickListener(this);
	}

	private void loadData(boolean loadMore) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		String pageNum = String.valueOf(1);
		int requestCode = REQUEST_CODE_TRANS_RECORDS_LIST;
		if (loadMore) {
			pageNum = String.valueOf(adapter.getPage() + 1);
			requestCode = REQUEST_CODE_TRANS_RECORDS_LIST_MORE;
		}
		params.put("pageNum", pageNum);
		params.put("pageSize", String.valueOf(Constants.PAGE_SIZE_BAOZI_BILL));
		requestHttpData(Constants.Urls.URL_POST_MY_BAOZI_TRANS_RECORDS, requestCode, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		listView.setVisibility(View.VISIBLE);
		if (REQUEST_CODE_TRANS_RECORDS_LIST == requestCode) {
			listView.onRefreshComplete();
			listView.setCanAddMore(false);
			if (data != null) {
				myBaozi = Parsers.getMyBaoziEntity(data);
				if (myBaozi != null) {
					tvBaoziCount.setText(NumberFormatUtil.formatBun(myBaozi.getWalletCount()));
					tvUseableBaozi.setText(NumberFormatUtil.formatBun(myBaozi.getAvailableWalletCount()));
					List<BaoziTransRecordsEntity> transRecord = myBaozi.getTransRecord();
					if (transRecord != null && transRecord.size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);

						adapter = new MyBaoziListAdapter(this, transRecord);
						listView.setAdapter(adapter);
						listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								AnalysisUtil.onEvent(MyBaoziActivity.this, "Android_act_Records");
								BaoziTransRecordsEntity baoziTransRecordsEntity = adapter.getItem(position);

								Intent intent = new Intent(MyBaoziActivity.this, OrderDetailActivity.class);
								intent.putExtra("cardOrderNo", baoziTransRecordsEntity.getOrderNo());
								int type = 0;
								if (!StringUtil.isEmpty(baoziTransRecordsEntity.getType())) {
									try {
										type = Integer.parseInt(baoziTransRecordsEntity.getType());
									} catch (Exception e) {
										e.printStackTrace();
										ToastUtil.shortShow(MyBaoziActivity.this, "数据异常!");
									}
								} else {
									ToastUtil.shortShow(MyBaoziActivity.this, "数据异常!");
								}
								intent.putExtra("type", type);
								startActivity(intent);
							}
						});
						if (adapter.getPage() < myBaozi.getTotalPage()) {
							listView.setCanAddMore(true);
						} else {
							listView.setCanAddMore(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}

		} else if (REQUEST_CODE_TRANS_RECORDS_LIST_MORE == requestCode) {
			listView.onRefreshComplete();
			if (data != null) {
				MyBaoziEntity myBaozi = Parsers.getMyBaoziEntity(data);
				List<BaoziTransRecordsEntity> transRecord = myBaozi.getTransRecord();
				adapter.addDatas(transRecord);
				if (adapter.getPage() < myBaozi.getTotalPage()) {
					listView.setCanAddMore(true);
				} else {
					listView.setCanAddMore(false);
				}
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		listView.onRefreshComplete();
		if (REQUEST_CODE_TRANS_RECORDS_LIST == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
			listView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_MyBunDetail");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		} else if (id == R.id.iv_back) {
			this.finish();
		} else if (id == R.id.iv_help) {
			Intent intent = new Intent(MyBaoziActivity.this, BaoziHelpActivity.class);
			startActivity(intent);
		} else if (id == R.id.ll_user_baozi) {
			AnalysisUtil.onEvent(this, "Android_act_spendBun");
			//使用包子
			//去包子商城，跳到包子商城tab
			Intent marketIntent = new Intent(this, MainActivity.class);
			marketIntent.putExtra(MainActivity.REQUEST_TO_WHICH_TAB, MainActivity.REQUEST_TO_MARKET);
			startActivity(marketIntent);
		} else if (id == R.id.ll_baozi_recharge) {
			AnalysisUtil.onEvent(this, "Android_act_MyBunRecharge");
			//立即充值
			if (myBaozi != null) {
				if (myBaozi.isIfRecharge()) {
					Intent actionIntent = new Intent(this, BaoziRechargeActivity.class);
					startActivity(actionIntent);
				} else {
					showAlertDialog("", getString(R.string.person_baozipay_toomany), true, getString(R.string.button_ok), new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							closeDialog();
						}
					});
				}
			}
		}
	}
}
