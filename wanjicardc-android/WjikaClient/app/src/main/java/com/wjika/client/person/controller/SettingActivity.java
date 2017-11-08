package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.utils.DataCleanManager;
import com.wjika.client.utils.LocationUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/8/31 15:13.
 * 设置
 */
public class SettingActivity extends ToolBarActivity implements View.OnClickListener{
	private static final int REQUEST_LOGOUT = 0x1;
	@ViewInject(R.id.person_setting_clean_cache)
	private TextView personSettingCleanCache;
	@ViewInject(R.id.person_setting_about_us)
	private TextView personSettingAboutUs;
	@ViewInject(R.id.person_setting_login_out)
	private Button personSettingLoginOut;
	@ViewInject(R.id.setting_location_state)
	private TextView locationState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_setting);
		ViewInjectUtils.inject(this);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (LocationUtils.isGPSOpen(this)) {
			locationState.setText(getString(R.string.setting_loaciton_start));
		} else {
			locationState.setText(getString(R.string.setting_loaciton_close));
		}
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.person_setting));
//		Boolean isLogin = getIntent().getBooleanExtra(IS_LOGIN,false);
		if (!UserCenter.isLogin(this)) {
			personSettingLoginOut.setVisibility(View.GONE);
		}
		personSettingCleanCache.setOnClickListener(this);
		personSettingAboutUs.setOnClickListener(this);
		personSettingLoginOut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.person_setting_clean_cache:
				cleanCache();
				break;
			case R.id.person_setting_about_us:
				startActivity(new Intent(this, AboutUsActivity.class));
				break;
			case R.id.person_setting_login_out:
				IdentityHashMap<String, String> params = new IdentityHashMap<>();
				params.put("token", UserCenter.getToken(this));
				requestHttpData(Constants.Urls.URL_LOGOUT, REQUEST_LOGOUT, FProtocol.HttpMethod.POST, params);
				UserCenter.cleanLoginInfo(this);
				setResult(RESULT_OK);
				finish();
//				ExitManager.instance.exit();
//				Intent intent = new Intent(this, LoginActivity.class);
//				startActivity(intent);
				break;
			default:
				break;
		}
	}

	private void cleanCache() {
		String cache = null;
		try {
			cache = DataCleanManager.getTotalCacheSize(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		String message = "清除本地缓存" + cache + "，确定清除？";
		String message = String.format(getString(R.string.person_setting_clear_cache),cache);
		showAlertDialog(getString(R.string.wjika_prompt), message, getString(R.string.wjika_cancel), getString(R.string.button_ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DataCleanManager.clearAllCache(SettingActivity.this);
				closeDialog();
			}
		});
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		personSettingLoginOut.setVisibility(View.GONE);
	}
}
