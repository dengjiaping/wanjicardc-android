package com.wjika.client.person.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.adapter.BankSupportAdapter;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/7/19.
 * 实名认证支持银行
 */
public class AuthBankSupportActivity extends ToolBarActivity implements AdapterView.OnItemClickListener{

	private static final int REQUEST_NET_BANK_SUPPORT = 10;
	public static final String INTENT_BANK_NAME = "bankName";

	private ListView bankSupportList;
	private BankSupportAdapter bankSupportAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_bank_support);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_auth_bank_support));
		bankSupportList = (ListView) findViewById(R.id.bank_support_list);
		bankSupportList.setOnItemClickListener(this);
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> param = new IdentityHashMap<>();
		param.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_POST_BANK_SUPPORT, REQUEST_NET_BANK_SUPPORT, FProtocol.HttpMethod.POST, param);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		if (REQUEST_NET_BANK_SUPPORT == requestCode) {
			bankSupportAdapter = new BankSupportAdapter(this, Parsers.getBankSupport(data));
			bankSupportList.setAdapter(bankSupportAdapter);
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		setResult(RESULT_OK,getIntent().putExtra(INTENT_BANK_NAME,bankSupportAdapter.getItem(position).getName()));
		finish();
	}
}
