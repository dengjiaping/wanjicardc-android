package com.wjika.client.person.controller;

import android.os.Bundle;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.AuthInfoEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by zhaoweiwei on 2016/6/29.
 * 认证信息界面
 */
public class AuthInfoActivity extends ToolBarActivity {

	public static final String CUS_NAME = "cus_name";
	public static final String ID_CODE = "id_code";
	public static final String ACCT_ID = "acct_id";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String IS_UNIONVERIFY = "isUnionVerify";
	private static final int REQUEST_NET_AUTH_INFO = 100;

	@ViewInject(R.id.person_authentication_name)
	private TextView authInfoName;
	@ViewInject(R.id.person_authentication_identity)
	private TextView authInfoIdno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_auth_info);
		InputMethodUtil.closeInputMethod(this);
		ViewInjectUtils.inject(this);
		initViews();
	}

	private void initViews() {
		setLeftTitle(getString(R.string.person_info_auth));
		loadData();
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> param = new IdentityHashMap<>();
		param.put("token", UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_POST_OBTAIN_AUTH_INFO, REQUEST_NET_AUTH_INFO, FProtocol.HttpMethod.POST, param);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		if (REQUEST_NET_AUTH_INFO == requestCode) {
			AuthInfoEntity authInfo = Parsers.getAuthInfo(data);
			if (authInfo != null) {
				authInfoName.setText(authInfo.getCustName());
				authInfoIdno.setText(authInfo.getIdCode());
			}
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
