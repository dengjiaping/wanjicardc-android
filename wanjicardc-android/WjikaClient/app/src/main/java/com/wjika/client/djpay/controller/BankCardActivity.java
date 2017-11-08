package com.wjika.client.djpay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.adapter.DjBankListAdapter;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DjBankListEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/23.
 * 斗金-银行卡列表
 */
public class BankCardActivity extends BaseDJActivity implements View.OnClickListener {

	private static final int REQUEST_NET_BANKLIST = 0x01;
	private static final int REQUEST_NET_BANKSET = 0x02;
	private static final int RESPONSE_FROM_ADDBANK = 0x03;
	private static final int REQUEST_NET_BANKDELETE = 0x04;

	@ViewInject(R.id.djpay_banklist)
	private ListView bankList;

	private DjBankListAdapter djBankListAdapter;
	private List<DjBankListEntity> bankListEntities;
	private DjBankListEntity djBankListSetEntity;
	private DjBankListEntity djBankDeleteEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.djpay_account_banklist);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", DJUserCenter.getToken(this));
		params.put("dj", "");
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BANK_LISTS, REQUEST_NET_BANKLIST, FProtocol.HttpMethod.POST, params);
	}

	private void initView() {
		setLeftTitle("我的银行卡");
		selectBtn(BTN_CODE_ACCOUNT);
		View view = getLayoutInflater().inflate(R.layout.djpay_banklist_footer, null);
		TextView addBank = (TextView) view.findViewById(R.id.djpay_banklist_add);
		bankList.addFooterView(view);
		addBank.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.djpay_banklist_add:
				Intent intent = new Intent(BankCardActivity.this, AddBankActivity.class);
				intent.putExtra("from", 2);
				startActivityForResult(intent, RESPONSE_FROM_ADDBANK);
				break;
			case R.id.djpay_bank_delete:
				djBankDeleteEntity = (DjBankListEntity) view.getTag();
				showAlertDialog("", "是否删除该绑定银行卡？", "确定", "取消", new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								deleteCard();
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								closeDialog();
							}
						});
				break;
			case R.id.djpay_bank_set:
				djBankListSetEntity = (DjBankListEntity) view.getTag();
				if (djBankListSetEntity.isChecked()) {
					ToastUtil.shortShow(this, "该银行卡已被设置为收款卡");
				} else {
					setDefaultCard();
				}
				break;
		}
	}

	private void deleteCard() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", DJUserCenter.getToken(this));
		params.put("dj", "");
		params.put("bankCardId", djBankDeleteEntity.getId());
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BANK_DELETE, REQUEST_NET_BANKDELETE, FProtocol.HttpMethod.POST, params);
	}

	private void setDefaultCard() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("token", DJUserCenter.getToken(this));
		params.put("dj", "");
		params.put("bankCardId", djBankListSetEntity.getId());
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_BANK_SETTING, REQUEST_NET_BANKSET, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		switch (requestCode) {
			case REQUEST_NET_BANKLIST:
				bankListEntities = Parsers.getDjpayBankList(data);
				djBankListAdapter = new DjBankListAdapter(this, bankListEntities, BankCardActivity.this);
				bankList.setAdapter(djBankListAdapter);
				break;
			case REQUEST_NET_BANKSET:
				for (DjBankListEntity djBankListEntity2 : bankListEntities) {
					djBankListEntity2.setChecked(false);
				}
				djBankListSetEntity.setChecked(true);
				djBankListAdapter.notifyDataSetChanged();
				break;
			case REQUEST_NET_BANKDELETE:
				closeDialog();
				djBankListAdapter.remove(djBankDeleteEntity);
				djBankListAdapter.notifyDataSetChanged();
				if (0 == bankListEntities.size()) {
					DJUserCenter.setBankCard(this, false);
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode && RESPONSE_FROM_ADDBANK == requestCode) {
			loadData();
		}
	}
}
